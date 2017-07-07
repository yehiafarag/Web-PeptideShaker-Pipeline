package com.uib.web.peptideshaker.presenter.core.graph;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.uib.web.peptideshaker.galaxy.dataobjects.ProteinObject;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
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
import com.vaadin.ui.OptionGroup;
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
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

    private final AbsoluteLayout mainContainer;
    private final DropHandler dropHandler;

    private boolean ignorResize = true;
    private final Label graphInfo;
    private final HorizontalLayout bottomRightPanel;
    private final HorizontalLayout rightBottomPanel;

    private final VerticalLayout leftBottomPanel;
    private final Map<String, Node> nodesMap;
    private final Set<Edge> edgesMap;

    public GraphComponent() {
        GraphComponent.this.setMargin(new MarginInfo(false, false, true, false));
        nodesMap = new HashMap<>();
        edgesMap = new HashSet<>();
        //init main layout

        //calculate canavas dimension 
        mainContainer = new AbsoluteLayout();
        SizeReporter sizeReporter = new SizeReporter(GraphComponent.this.mainContainer);
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
        mainContainer.setSizeFull();
        mainContainer.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            if (event.getClickedComponent() instanceof Canvas) {
                selectNode(null);
            }
        });

        bottomRightPanel = new HorizontalLayout();
        bottomRightPanel.setSpacing(true);
        rightBottomPanel = new HorizontalLayout();
        rightBottomPanel.setSpacing(true);

        graphInfo = new Label();
        graphInfo.setStyleName(ValoTheme.LABEL_LIGHT);
        graphInfo.addStyleName(ValoTheme.LABEL_SMALL);
        graphInfo.addStyleName(ValoTheme.LABEL_TINY);
        rightBottomPanel.addComponent(graphInfo);

        Button updateLayoutBtn = new Button("Update layout");
        updateLayoutBtn.addStyleName(ValoTheme.BUTTON_TINY);
//        updateLayoutBtn.addStyleName(ValoTheme.BUTTON_LINK);
        updateLayoutBtn.addClickListener((Button.ClickEvent event) -> {
            updateGraphLayout();
        });

        Button selectAllBtn = new Button("Select all");
//        selectAllBtn.setStyleName(ValoTheme.BUTTON_SMALL);
        selectAllBtn.addStyleName(ValoTheme.BUTTON_TINY);
//        selectAllBtn.addStyleName(ValoTheme.BUTTON_LINK);
        selectAllBtn.addClickListener((Button.ClickEvent event) -> {
            selectAll();
        });

        bottomRightPanel.addComponent(selectAllBtn);
        bottomRightPanel.addComponent(updateLayoutBtn);

        leftBottomPanel = new VerticalLayout();
        leftBottomPanel.setSpacing(false);
        leftBottomPanel.setWidth(400, Unit.PIXELS);
        //init controls

        OptionGroup proteinsControl = new OptionGroup();
        proteinsControl.setMultiSelect(false);
        proteinsControl.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        proteinsControl.addStyleName(ValoTheme.OPTIONGROUP_SMALL);
        proteinsControl.addStyleName("smallertext");
        proteinsControl.addItem("Validation Status");
        proteinsControl.addItem("Protein Evidence");
        proteinsControl.addItem("Molecule Type");
        proteinsControl.setValue("Molecule Type");
        leftBottomPanel.addComponent(proteinsControl);
        proteinsControl.addValueChangeListener((Property.ValueChangeEvent event) -> {
            updateNodeColourType(proteinsControl.getValue() + "");
        });

        OptionGroup nodeControl = new OptionGroup();
        nodeControl.setMultiSelect(false);
        nodeControl.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        nodeControl.addStyleName(ValoTheme.OPTIONGROUP_SMALL);
        nodeControl.addStyleName("smallertext");
        nodeControl.addItem("All Neighbors");
        nodeControl.addItem("Unique Only");
        nodeControl.setValue("All Neighbors");
        nodeControl.addValueChangeListener((Property.ValueChangeEvent event) -> {
        });
        leftBottomPanel.addComponent(nodeControl);

        //calculate graph
        //draw graph     
        canvas = new Canvas();
//        graphContainer.addComponent(canvas);
        GraphComponent.this.canvas.setSizeFull();
        GraphComponent.this.setSizeFull();

// Wrap the layout to allow handling drops
        DragAndDropWrapper layoutWrapper
                = new DragAndDropWrapper(mainContainer);
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
                    ComponentPosition pos = mainContainer.getPosition(t.getSourceComponent());
                    pos.setLeftValue(pos.getLeftValue() + xChange);
                    pos.setTopValue(pos.getTopValue() + yChange);

                    double x = pos.getLeftValue();
                    double y = pos.getTopValue();
                    nodesMap.get(node.getData() + "").setX(x);
                    nodesMap.get(node.getData() + "").setY(y);
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

    private void updateNodeColourType(String updatingType) {
        switch (updatingType) {
            case "Molecule Type":
                for (String key : nodesMap.keySet()) {
                    nodesMap.get(key).resetStyle();
                }   break;
            case "Protein Evidence":
                for (String key : nodesMap.keySet()) {
                    nodesMap.get(key).resetStyle();
                     nodesMap.get(key).addStyleName("redbackground");
                }   break;
            default:
                for (String key : nodesMap.keySet()) {
                    nodesMap.get(key).resetStyle();
                    nodesMap.get(key).addStyleName("graybackground");
                }   break;
        }

    }

    public void updateGraphData(ProteinObject selectedProtein, Set<String> proteinNodes, Set<String> peptidesNodes, HashMap<String, ArrayList<String>> edges) {

        mainContainer.removeAllComponents();
        nodesMap.clear();
        edgesMap.clear();
        mainContainer.addComponent(canvas);
        mainContainer.addComponent(bottomRightPanel, "right: " + 5 + "px; bottom: " + 5 + "px");
        mainContainer.addComponent(leftBottomPanel, "left: " + 5 + "px; bottom: " + 5 + "px");
        mainContainer.addComponent(rightBottomPanel, "right: " + 5 + "px; top: " + 5 + "px");

        canvas.clear();
        //calculate graph
        if (selectedProtein == null) {
            graphInfo.setValue("#Proteins: 0  || #Peptides: 0");
            return;
        }
        this.proteinNodes = proteinNodes;
        this.peptidesNodes = peptidesNodes;

        this.edges = edges;
        setUpGraph();
        for (String node : graph.getVertices()) {
            Node n = new Node(node) {
                @Override
                public void selected(String id) {
                    selectNode(id);
                }
            };
            n.setX(graphLayout.getX(node));
            n.setY(graphLayout.getY(node));
            nodesMap.put(node, n);
            if (peptidesNodes.contains(node)) {
                n.setDefaultStyleName("peptidenode");
                if (selectedProtein.isRelatedPeptide(node)) {
                    n.setSelected(true);
                }
                n.setType(1);

            } else {
                n.setDefaultStyleName("proteinnode");
                n.setType(0);
                if (selectedProtein.getProteinGroupSet().contains(node)) {
                    n.setSelected(true);
                }
            }

            final WrappedComponent wrapper = new WrappedComponent(n,
                    dropHandler);
            wrapper.setSizeUndefined();
            wrapper.setData(node);
            wrapper.setDescription(node);
            mainContainer.addComponent(wrapper, "left: " + n.getX() + "px; top: " + n.getY() + "px");
        }
        for (String key : edges.keySet()) {
            ArrayList<String> edg = edges.get(key);
            for (String node : edg) {
                Edge edge = new Edge(nodesMap.get(key), nodesMap.get(node));
                edgesMap.add(edge);
            }

        }
        graphInfo.setValue("#Proteins: " + selectedProtein.getProteinGroupSet().size() + "  || #Peptides: " + (drawEdges()) + "");

    }

    private void reDrawGraph() {
        if (visualizationViewer != null) {
            setUpGraph();
            Iterator<Component> itr = mainContainer.iterator();
            while (itr.hasNext()) {
                Component component = itr.next();
                if (component instanceof WrappedComponent) {
                    WrappedComponent wComp = (WrappedComponent) component;
                    String nodeName = wComp.getData() + "";
                    AbsoluteLayout.ComponentPosition newPosition = mainContainer.getPosition(wComp);
                    double x = graphLayout.getX(nodeName);
                    double y = graphLayout.getY(nodeName);
                    nodesMap.get(nodeName).setX(x);
                    nodesMap.get(nodeName).setY(y);
                    newPosition.setCSSString("left: " + x + "px; top: " + y + "px");

                }
            }
            drawEdges();
        }
    }

    private void selectNode(String id) {
        for (Node node : nodesMap.values()) {
            node.setSelected(false);
        }
        if (id != null) {
            Node n = nodesMap.get(id);
            for (Edge edge : edgesMap) {
                edge.select(n);
            }
        }
        int number = drawEdges();
//
//        if (n.getType() == 0) {
//            graphInfo.setValue("#Proteins: " + 1 + "  || #Peptides: " + number + "");
//        } else {
//            graphInfo.setValue("#Proteins: " + number + "  || #Peptides: " + 1 + "");
//        }

    }

    private void selectAll() {
        for (Node node : nodesMap.values()) {
            node.setSelected(true);
        }
        drawEdges();
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
        canvas.stroke();
        canvas.closePath();
        canvas.restoreContext();
    }

    private int drawEdges() {
        canvas.clear();
        canvas.setLineWidth(1);
        canvas.setLineCap("round");//      
        canvas.setStrokeStyle(5, 59, 114);
        canvas.beginPath();
        int edgeNumber = 0;
        for (Edge edge : edgesMap) {
            if (edge.isSelected()) {
                edgeNumber++;
                canvas.moveTo(edge.getStartX(), edge.getStartY());
                canvas.lineTo(edge.getEndX(), edge.getEndY());
            }
        }
        canvas.stroke();
        canvas.setStrokeStyle("lightgray");
        canvas.beginPath();
        for (Edge edge : edgesMap) {
            if (!edge.isSelected()) {
                canvas.moveTo(edge.getStartX(), edge.getStartY());
                canvas.lineTo(edge.getEndX(), edge.getEndY());
            }
        }

        canvas.stroke();
        return edgeNumber;
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
            WrappedComponent.this.setDragStartMode(DragAndDropWrapper.DragStartMode.WRAPPER);
        }

        @Override
        public DropHandler getDropHandler() {
            return dropHandler;
        }

    }

}
