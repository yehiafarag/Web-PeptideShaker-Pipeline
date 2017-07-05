package com.uib.web.peptideshaker.presenter.components.peptideshakerview;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.uib.web.peptideshaker.galaxy.dataobjects.ProteinObject;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.WrapperTargetDetails;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.vaadin.hezamu.canvas.Canvas;

/**
 * This class represents Graph layout component
 *
 * @author Yehia Farag
 */
public class GraphComponent extends VerticalLayout {

    private final Canvas canvas;
    private int liveWidth;
    private int liveHeight;

    /**
     * The graph.
     */
    private UndirectedSparseGraph<String, String> graph;
    /**
     * The nodes.
     */
    private Set<String> proteinNodes;
    private Set<String> peptidesNodes;
    /**
     * The edges: the keys are the node labels and the elements the list of
     * objects.
     */
    private HashMap<String, ArrayList<String>> edges;
    /**
     * Creates new form GraphForm
     */
    private VisualizationViewer visualizationViewer;
    private FRLayout graphLayout;

    private final AbsoluteLayout graphContainer;
    private final DropHandler dropHandler;

    private boolean ignorResize = true;
    private final Label graphInfo;
    private final HorizontalLayout bottomPanel;

    public GraphComponent() {
        GraphComponent.this.setMargin(new MarginInfo(false, false, true, false));
        //init main layout

        //calculate canavas dimension 
        graphContainer = new AbsoluteLayout();
        SizeReporter sizeReporter = new SizeReporter(GraphComponent.this.graphContainer);
        sizeReporter.addResizeListener((ComponentResizeEvent event) -> {
            int tWidth = event.getWidth();
            int tHeight = event.getHeight();
            if (liveWidth == tWidth && liveHeight == tHeight || ignorResize) {
                ignorResize = false;
                return;
            }
            ignorResize = true;
            liveWidth = tWidth - 100;
            liveHeight = tHeight - 100;
            reDrawGraph();
        });
        graphContainer.setSizeFull();

        bottomPanel = new HorizontalLayout();
        bottomPanel.setSpacing(true);
        graphInfo = new Label();
        graphInfo.setStyleName(ValoTheme.LABEL_LIGHT);
        graphInfo.addStyleName(ValoTheme.LABEL_SMALL);
        graphInfo.addStyleName(ValoTheme.LABEL_TINY);
        bottomPanel.addComponent(graphInfo);

        Button updateLayoutBtn = new Button("Update layout");
        updateLayoutBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        updateLayoutBtn.setStyleName(ValoTheme.BUTTON_TINY);
        updateLayoutBtn.addClickListener((Button.ClickEvent event) -> {
            updateGraphLayout();
        });
        bottomPanel.addComponent(updateLayoutBtn);
        //calculate graph
        //draw graph     
        canvas = new Canvas();
//        graphContainer.addComponent(canvas);
        GraphComponent.this.canvas.setSizeFull();
        GraphComponent.this.setSizeFull();

// Wrap the layout to allow handling drops
        DragAndDropWrapper layoutWrapper
                = new DragAndDropWrapper(graphContainer);
        layoutWrapper.addStyleName("subframe");
        GraphComponent.this.addComponent(layoutWrapper);
        layoutWrapper.setSizeFull();
// Handle moving components within the AbsoluteLayout

        dropHandler = new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {

                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {

                Component component = event.getTransferable().getSourceComponent();
                if (component instanceof WrappedComponent) {
                    WrappedComponent node = (WrappedComponent) component;
                    WrapperTransferable t = (WrapperTransferable) event.getTransferable();
                    WrapperTargetDetails details = (WrapperTargetDetails) event.getTargetDetails();
                    // Calculate the drag coordinate difference
                    int xChange = details.getMouseEvent().getClientX() - t.getMouseDownEvent().getClientX();
                    int yChange = details.getMouseEvent().getClientY() - t.getMouseDownEvent().getClientY();
                    // Move the component in the absolute layout
                    ComponentPosition pos = graphContainer.getPosition(t.getSourceComponent());
                    pos.setLeftValue(pos.getLeftValue() + xChange);
                    pos.setTopValue(pos.getTopValue() + yChange);
                    graphLayout.setLocation(node.getData() + "", pos.getLeftValue(), pos.getTopValue());
                    drawEdges();
                }

            }
        };

        layoutWrapper.setDropHandler(dropHandler);

    }

    private void updateGraphLayout() {
        ScalingControl scaler = new CrossoverScalingControl();
        scaler.scale(visualizationViewer, 0.9f, visualizationViewer.getCenter());
        reDrawGraph();
    }

    public void updateGraphData(ProteinObject selectedProtein, Set<String> proteinNodes, Set<String> peptidesNodes, HashMap<String, ArrayList<String>> edges) {

        graphContainer.removeAllComponents();
        graphContainer.addComponent(canvas);
        canvas.clear();
        //calculate graph
        if (selectedProtein == null) {
            graphInfo.setValue("#Proteins: 0  || #Peptides: 0");
            return;
        }
        this.proteinNodes = proteinNodes;
        this.peptidesNodes = peptidesNodes;
        graphInfo.setValue("#Proteins: " + selectedProtein.getProteinGroupSet().size() + "  || #Peptides: " + peptidesNodes.size() + "");
        this.edges = edges;
        setUpGraph();
        for (String node : graph.getVertices()) {
            VerticalLayout n = initNode();
            if (peptidesNodes.contains(node)) {
                n.addStyleName("peptidenode");

            } else {
                n.addStyleName("proteinnode");
            }
            final WrappedComponent wrapper = new WrappedComponent(n,
                    dropHandler);
            wrapper.setSizeUndefined();
            wrapper.setData(node);
            wrapper.setDescription(node);
            graphContainer.addComponent(wrapper, "left: " + graphLayout.getX(node) + "px; top: " + graphLayout.getY(node) + "px");
        }
        for (String key : edges.keySet()) {
            ArrayList<String> edg = edges.get(key);
            double startX = graphLayout.getX(key) + 8;
            double startY = graphLayout.getY(key) + 8;
            double endX;
            double endY;
            for (String node : edg) {
                endX = graphLayout.getX(node) + 15;
                endY = graphLayout.getY(node) + 15;
                drawLines(startX, startY, endX, endY);

            }

        }
        graphContainer.addComponent(bottomPanel, "right: " + 5 + "px; bottom: " + 5 + "px");

    }

    private VerticalLayout initNode() {
        VerticalLayout node = new VerticalLayout();
        node.setWidth(20, Unit.PIXELS);
        node.setHeight(20, Unit.PIXELS);
        node.setStyleName("node");
        return node;

    }

    private void reDrawGraph() {

        if (visualizationViewer != null) {
            setUpGraph();
            Iterator<Component> itr = graphContainer.iterator();
            while (itr.hasNext()) {
                Component component = itr.next();
                if (component instanceof WrappedComponent) {
                    WrappedComponent wComp = (WrappedComponent) component;
                    String nodeName = wComp.getData() + "";
                    AbsoluteLayout.ComponentPosition newPosition = graphContainer.getPosition(wComp);
                    newPosition.setCSSString("left: " + graphLayout.getX(nodeName) + "px; top: " + graphLayout.getY(nodeName) + "px");
                }

            }
            drawEdges();

        }
    }

    private void drawEdges() {
        canvas.clear();
        for (String key : edges.keySet()) {
            ArrayList<String> edg = edges.get(key);
            double startX = graphLayout.getX(key) + 10;
            double startY = graphLayout.getY(key) + 10;
            double endX;
            double endY;
            for (String node : edg) {
                endX = graphLayout.getX(node) + 10;
                endY = graphLayout.getY(node) + 10;
                drawLines(startX, startY, endX, endY);

            }

        }

    }

    /**
     * Set up the graph.
     *
     * @param parentPanel the parent panel
     * @return the visualization viewer
     */
    private void setUpGraph() {
        graph = new UndirectedSparseGraph<>();
        // add all the nodes
        for (String node : proteinNodes) {
            graph.addVertex(node);
        }
        for (String node : peptidesNodes) {
            graph.addVertex(node);
        }

        // add the vertexes
        Iterator<String> startNodeKeys = edges.keySet().iterator();
        while (startNodeKeys.hasNext()) {
            String startNode = startNodeKeys.next();
            for (String endNode : edges.get(startNode)) {
                graph.addEdge(startNode + "|" + endNode, startNode, endNode);
            }
        }
        // create the visualization viewer
        graphLayout = new FRLayout<>(graph);
        visualizationViewer = new VisualizationViewer<>(graphLayout, new Dimension(liveWidth, liveHeight));
    }

    private void drawLines(double startX, double startY, double endX, double endY) {
        canvas.saveContext();
        canvas.beginPath();
        canvas.setLineWidth(1);
        canvas.setLineCap("round");
        canvas.setFillStyle(5, 59, 114);
        canvas.setMiterLimit(1);
        canvas.setStrokeStyle(5, 59, 114);
        canvas.moveTo(startX, startY);
        canvas.lineTo(endX, endY);
//        canvas.quadraticCurveTo(startX, startY + 10, endX, endY);

        canvas.stroke();
        canvas.closePath();
        canvas.restoreContext();
    }

    /**
     * This class is a wrapper for the dropped component that is used in the
     * Drag-Drop layout.
     *
     * @author Yehia Farag
     */
    class WrappedComponent extends DragAndDropWrapper {

        /**
         * The layout drop handler.
         */
        private final DropHandler dropHandler;

        /**
         * Constructor to initialize the main attributes.
         *
         * @param content the dropped component (the label layout)
         * @param dropHandler The layout drop handler.
         */
        public WrappedComponent(final Component content, final DropHandler dropHandler) {
            super(content);
            this.dropHandler = dropHandler;
            setDragStartMode(DragAndDropWrapper.DragStartMode.WRAPPER);
        }

        @Override
        public DropHandler getDropHandler() {
            return dropHandler;
        }

    }

}
