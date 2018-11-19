package com.uib.web.peptideshaker.presenter.core.graph;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.itextpdf.text.pdf.codec.Base64;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.PeptideObject;
import com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects.ProteinGroupObject;
import com.uib.web.peptideshaker.presenter.core.filtercharts.components.RangeColorGenerator;
import com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage.Legend;
import com.vaadin.data.Property;
import com.vaadin.event.LayoutEvents;
import com.vaadin.event.dd.DragAndDropEvent;
import com.vaadin.event.dd.DropHandler;
import com.vaadin.event.dd.acceptcriteria.AcceptAll;
import com.vaadin.event.dd.acceptcriteria.AcceptCriterion;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.AbsoluteLayout.ComponentPosition;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.DragAndDropWrapper;
import com.vaadin.ui.DragAndDropWrapper.WrapperTargetDetails;
import com.vaadin.ui.DragAndDropWrapper.WrapperTransferable;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.jfree.chart.encoders.ImageEncoder;
import org.jfree.chart.encoders.ImageEncoderFactory;
import org.jfree.chart.encoders.ImageFormat;

/**
 * This class represents Graph layout component
 *
 * @author Yehia Farag
 */
public abstract class GraphComponent extends VerticalLayout {

    private final AbsoluteLayout canvas;
    private int liveWidth;
    private int liveHeight;
    private Image edgesImage;
    private final Stroke dashLineStroke;

    /**
     * The graph.
     */
    private UndirectedSparseGraph<String, String> graph;
    /**
     * The nodes.
     */
    private Map<String, ProteinGroupObject> proteinNodes;
    private Map<String, PeptideObject> peptidesNodes;
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

    private final SizeReporter sizeReporter;
    private final Map<String, String> styles;
    private boolean ignorResize = true;
    private final Label graphInfo;
    private final HorizontalLayout bottomRightPanel;
    private final HorizontalLayout rightBottomPanel;

    private final VerticalLayout lefTtopPanel;
    private final Map<Object, Node> nodesMap;
    private final Set<Edge> edgesMap;

    private final OptionGroup proteinsControl;
    private final OptionGroup nodeControl;
    private boolean uniqueOnly = false;

    private final Set<Object> selectedProteins;
    private final Set<Object> selectedPeptides;

    private final PopupView legendLayout;

    private String thumbImgeUrl;

    private final Property.ValueChangeListener proteinsControlListener;

    public String getThumbImgeUrl() {
        return thumbImgeUrl;
    }

    public GraphComponent() {
        GraphComponent.this.setMargin(new MarginInfo(false, false, false, false));
        this.dashLineStroke = new BasicStroke(1.0f, // Width
                BasicStroke.CAP_SQUARE, // End cap
                BasicStroke.JOIN_MITER, // Join style
                10.0f, new float[]{10.0f}, 0.0f);

        nodesMap = new HashMap<>();
        edgesMap = new HashSet<>();
        styles = new HashMap<>();
        this.selectedProteins = new HashSet<>();
        this.selectedPeptides = new HashSet<>();
        //new String[]{"greenbackground", "orangebackground", "seabluebackground", "purplebackground", "redbackground", "graybackground"};

        styles.put("Confident", "greenbackground");
        styles.put("Doubtful", "orangebackground");
        styles.put("Not Validated", "redbackground");
        styles.put("Not Available", "graybackground");
        styles.put("Protein", "greenbackground");
        styles.put("Transcript", "orangebackground");
        styles.put("Homology", "seabluebackground");
        styles.put("Predicted", "purplebackground");
        styles.put("Uncertain", "redbackground");
        styles.put("Not Applicable", "lightgraybackground");

        //init main layout
        //calculate canavas dimension 
        mainContainer = new AbsoluteLayout();
        mainContainer.setWidth(100, Unit.PERCENTAGE);
        mainContainer.setHeight(100, Unit.PERCENTAGE);
        mainContainer.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            if (event.getClickedComponent() instanceof AbsoluteLayout) {
                selectNodes(new Object[]{});
            }
        });

        bottomRightPanel = new HorizontalLayout();
        bottomRightPanel.setSpacing(false);
//        bottomRightPanel.setWidth(300, Unit.PIXELS);
        bottomRightPanel.setStyleName("inframe");

        rightBottomPanel = new HorizontalLayout();
        rightBottomPanel.setSpacing(true);
//        rightBottomPanel.setStyleName("inframe");

        graphInfo = new Label();
        graphInfo.setContentMode(ContentMode.HTML);
        graphInfo.setStyleName(ValoTheme.LABEL_LIGHT);
        graphInfo.addStyleName(ValoTheme.LABEL_SMALL);
        graphInfo.addStyleName(ValoTheme.LABEL_TINY);

        graphInfo.addStyleName("inframe");
        rightBottomPanel.addComponent(graphInfo);

        Button updateLayoutBtn = new Button("Update layout");
        updateLayoutBtn.addStyleName(ValoTheme.BUTTON_TINY);
        updateLayoutBtn.addStyleName(ValoTheme.BUTTON_LINK);
        updateLayoutBtn.addClickListener((Button.ClickEvent event) -> {
            updateGraphLayout();
        });

        Button selectAllBtn = new Button("Select all");
        selectAllBtn.addStyleName(ValoTheme.BUTTON_TINY);
        selectAllBtn.addStyleName(ValoTheme.BUTTON_LINK);
        selectAllBtn.addClickListener((Button.ClickEvent event) -> {
            selectAll();
        });

        Legend informationLegend = new Legend() {
            @Override
            public void close() {
                legendLayout.setPopupVisible(false);
            }

        };
        legendLayout = new PopupView(null, informationLegend) {
            @Override
            public void setPopupVisible(boolean visible) {
                this.setVisible(visible);
                super.setPopupVisible(visible); //To change body of generated methods, choose Tools | Templates.
            }

        };
        legendLayout.setHideOnMouseOut(false);
        legendLayout.addStyleName("protlegend");
        legendLayout.addStyleName(ValoTheme.BUTTON_TINY);
        legendLayout.addStyleName(ValoTheme.BUTTON_LINK);
        legendLayout.setVisible(false);

        Button legendLayoutBtn = new Button("Legend");
        legendLayoutBtn.addStyleName(ValoTheme.BUTTON_TINY);
        legendLayoutBtn.addStyleName(ValoTheme.BUTTON_LINK);
        legendLayoutBtn.addClickListener((Button.ClickEvent event) -> {
            legendLayout.setPopupVisible(true);
        });

        bottomRightPanel.addComponent(selectAllBtn);
        bottomRightPanel.setComponentAlignment(selectAllBtn, Alignment.TOP_CENTER);
        bottomRightPanel.addComponent(updateLayoutBtn);
        bottomRightPanel.setComponentAlignment(updateLayoutBtn, Alignment.TOP_CENTER);
        bottomRightPanel.addComponent(legendLayoutBtn);
        bottomRightPanel.setComponentAlignment(legendLayoutBtn, Alignment.TOP_CENTER);
        mainContainer.addComponent(legendLayout, "left: " + 50 + "%; top: " + 50 + "%");
        lefTtopPanel = new VerticalLayout();
        lefTtopPanel.setSpacing(false);
        lefTtopPanel.setWidthUndefined();
        lefTtopPanel.addStyleName("inframe");
        //init controls

        proteinsControl = new OptionGroup();
        proteinsControl.setMultiSelect(false);
        proteinsControl.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        proteinsControl.addStyleName(ValoTheme.OPTIONGROUP_SMALL);
        proteinsControl.addStyleName("smallertext");
        proteinsControl.addItem("Validation Status");
        proteinsControl.addItem("Modification  Status");
        proteinsControl.addItem("Protein Evidence");
        proteinsControl.addItem("PSMNumber");
        proteinsControl.setItemCaption("PSMNumber", "#PSM");
        proteinsControl.addItem("Molecule Type");

        proteinsControl.setValue("Molecule Type");
        lefTtopPanel.addComponent(proteinsControl);

        proteinsControlListener = (Property.ValueChangeEvent event) -> {
            updateNodeColourType(proteinsControl.getValue() + "");
        };
        proteinsControl.addValueChangeListener(proteinsControlListener);

        VerticalLayout leftBottomPanel = new VerticalLayout();
        leftBottomPanel.setSpacing(false);
        leftBottomPanel.setWidthUndefined();
        leftBottomPanel.addStyleName("inframe");
        nodeControl = new OptionGroup();
        nodeControl.setMultiSelect(false);
        nodeControl.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        nodeControl.addStyleName(ValoTheme.OPTIONGROUP_SMALL);
        nodeControl.addStyleName("smallertext");
        nodeControl.addItem("All Neighbors");
        nodeControl.addItem("Unique Only");
        nodeControl.setValue("All Neighbors");
        nodeControl.addValueChangeListener((Property.ValueChangeEvent event) -> {
            uniqueOnly = nodeControl.getValue().equals("Unique Only");
            selectNodes(selectedProteins.toArray());
        });
        leftBottomPanel.addComponent(nodeControl);

        //calculate graph
        edgesImage = new Image();
        GraphComponent.this.edgesImage.setSizeFull();

        //draw graph     
        canvas = new AbsoluteLayout();
        GraphComponent.this.canvas.setSizeFull();
        GraphComponent.this.setSizeFull();
        sizeReporter = new SizeReporter(GraphComponent.this.canvas);
        sizeReporter.addResizeListener((ComponentResizeEvent event) -> {
            int tWidth = event.getWidth();
            int tHeight = event.getHeight();
//            if (liveWidth == tWidth && liveHeight == tHeight || ignorResize) {
//                ignorResize = false;
//                return;
//            }
//            ignorResize = true;
            liveWidth = tWidth;
            liveHeight = tHeight;
            updateGraphLayout();
        });

        mainContainer.addStyleName("graphframeframe");
// Wrap the layout to allow handling drops
        DragAndDropWrapper layoutWrapper
                = new DragAndDropWrapper(canvas);

        GraphComponent.this.addComponent(mainContainer);
        layoutWrapper.setSizeFull();
// Handle moving components within the AbsoluteLayout

        dropHandler = new DropHandler() {
            @Override
            public AcceptCriterion getAcceptCriterion() {

                return AcceptAll.get();
            }

            @Override
            public void drop(DragAndDropEvent event) {
                edgesImage.addStyleName("hide");
                Component component = event.getTransferable().getSourceComponent();
                if (component instanceof WrappedComponent) {
                    WrappedComponent node = (WrappedComponent) component;
                    WrapperTransferable t = (WrapperTransferable) event.getTransferable();
                    WrapperTargetDetails details = (WrapperTargetDetails) event.getTargetDetails();
                    // Calculate the drag coordinate difference
                    int xChange = details.getMouseEvent().getClientX() - t.getMouseDownEvent().getClientX();
                    int yChange = details.getMouseEvent().getClientY() - t.getMouseDownEvent().getClientY();
                    // Move the component in the absolute layout
                    ComponentPosition pos = canvas.getPosition(t.getSourceComponent());
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

        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setSizeFull();
        wrapper.setMargin(new MarginInfo(true, true, true, true));
        wrapper.addComponent(edgesImage);
        mainContainer.addComponent(wrapper, "left: 0px; top: 0px");
        mainContainer.addComponent(bottomRightPanel, "right: " + 10 + "px; bottom: " + -12 + "px");
        mainContainer.addComponent(lefTtopPanel, "left: " + 10 + "px; top: " + -12 + "px");
        mainContainer.addComponent(rightBottomPanel, "right: " + 10 + "px; top: " + 10 + "px");
        mainContainer.addComponent(leftBottomPanel, "left: " + 10 + "px; bottom: " + -12 + "px");
        wrapper = new VerticalLayout();
        wrapper.setSizeFull();
        wrapper.setMargin(new MarginInfo(true, true, true, true));
        wrapper.addComponent(layoutWrapper);
        mainContainer.addComponent(wrapper, "left: 0px; top: 0px");

    }

    private void updateGraphLayout() {
        if (graph == null) {
            return;
        }
//        if(liveWidth<20|| liveHeight<20){
//            liveWidth =  sizeReporter.getWidth()+21;
//            liveHeight= sizeReporter.getHeight()+21;
//            
//        }
        ScalingControl scaler = new CrossoverScalingControl();
        graphLayout = new FRLayout<>(graph);
        visualizationViewer = new VisualizationViewer<>(graphLayout, new Dimension(liveWidth, liveHeight));
        scaler.scale(visualizationViewer, 1f, visualizationViewer.getCenter());
        reDrawGraph();
    }

    private void updateNodeColourType(String modeType) {

        nodesMap.keySet().forEach((key) -> {
            nodesMap.get(key).setNodeStatues(modeType);
        });
        updateProteinsMode(modeType);

    }

    public void updateGraphData(ProteinGroupObject selectedProtein, Map<String, ProteinGroupObject> proteinNodes, Map<String, PeptideObject> peptidesNodes, HashMap<String, ArrayList<String>> edges,RangeColorGenerator colorScale) {
        uniqueOnly = nodeControl.getValue().equals("Unique Only");
        canvas.removeAllComponents();
        nodesMap.clear();
        edgesMap.clear();
        selectedProteins.clear();
        selectedPeptides.clear();

//        canvas.clear();
        //calculate graph
        if (selectedProtein == null) {
            graphInfo.setValue("#Proteins: <font style='float:right'>0</font><br/>#Peptides: <font style='float:right'>0</font>");
            return;
        }
        this.proteinNodes = proteinNodes;
        this.peptidesNodes = peptidesNodes;
        
        this.edges = edges;
        setUpGraph();
        
        
        
        
        for (String node : graph.getVertices()) {
            String modifications = "";
            String sequence = "";
            int psmNumber = 0;

            if (peptidesNodes.containsKey(node)) {
                modifications = peptidesNodes.get(node).getVariableModifications();
                sequence = peptidesNodes.get(node).getSequence();
                 psmNumber = peptidesNodes.get(node).getPSMsNumber();
            }
            else{
               psmNumber =-1;
            }
            Node n = new Node(node, modifications, sequence,psmNumber,colorScale.getColor(psmNumber)) {
                @Override
                public void selected(String id) {
                    selectNodes(new Object[]{id});

                }
            };
            n.setX(graphLayout.getX(node));
            n.setY(graphLayout.getY(node));
            nodesMap.put(node, n);
            if (peptidesNodes.containsKey(node)) {
                n.setDefaultStyleName("peptidenode");
                if (selectedProtein.isRelatedPeptide(node)) {
                    n.setSelected(true);
                }
                n.setType(1);
                n.setValidationStatuesStyle(styles.get(peptidesNodes.get(node).getValidation()));
                n.setProteinEvidenceStyle(styles.get("Not Applicable"));
            } else {
                if (proteinNodes.containsKey(node) && proteinNodes.get(node) != null) {
                    n.setValidationStatuesStyle(styles.get(proteinNodes.get(node).getValidation()));
                    n.setProteinEvidenceStyle(styles.get(proteinNodes.get(node).getProteinEvidence()));
                } else {
                    n.setValidationStatuesStyle(styles.get("Not Available"));
                    n.setProteinEvidenceStyle(styles.get("Not Available"));
                }

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
            wrapper.setDescription(n.getDescription());
            canvas.addComponent(wrapper, "left: " + n.getX() + "px; top: " + n.getY() + "px");
        }
        edges.keySet().forEach((key) -> {
            ArrayList<String> edg = edges.get(key);
            edg.stream().map((node) -> {
                Node n1 = nodesMap.get(key);
                Node n2 = nodesMap.get(node);
                n1.addEdge();
                n2.addEdge();
                Edge edge = new Edge(n1, n2, !proteinNodes.get(node).isEnymaticPeptide(key));
                return edge;
            }).forEachOrdered((edge) -> {
                edgesMap.add(edge);
            });
        });
        selectNodes(selectedProtein.getProteinGroupSet().toArray());
        proteinsControl.removeValueChangeListener(proteinsControlListener);
        proteinsControl.setValue("Molecule Type");
        proteinsControl.addValueChangeListener(proteinsControlListener);
        thumbImgeUrl = generateThumbImg();
//        updateGraphLayout();

    }

    private void reDrawGraph() {

        if (visualizationViewer != null) {
//            visualizationViewer.setSize(new Dimension(liveWidth, liveHeight));
            edgesImage.addStyleName("hide");
            setUpGraph();
            Iterator<Component> itr = canvas.iterator();
            while (itr.hasNext()) {
                Component component = itr.next();
                if (component instanceof WrappedComponent) {
                    WrappedComponent wComp = (WrappedComponent) component;
                    String nodeName = wComp.getData() + "";
                    AbsoluteLayout.ComponentPosition newPosition = canvas.getPosition(wComp);
                    double x = graphLayout.getX(nodeName);
                    double y = graphLayout.getY(nodeName);
                    nodesMap.get(nodeName).setX(x);
                    nodesMap.get(nodeName).setY(y);
                    if (y > liveHeight) {
                        System.out.println("at -------------- here comes error  y " + y + "   " + liveHeight);
                    } else if (x > liveWidth) {
                        System.out.println("at -------------- here comes error x  " + x + "   " + liveWidth);
                    }
                    newPosition.setCSSString("left: " + x + "px; top: " + y + "px");

                }
            }
            drawEdges();
        }
    }

    private void selectNodes(Object[] ids) {
        redrawSelection(ids, true);
        selectedItem(selectedProteins, selectedPeptides);

    }

    private void selectAll() {
        selectNodes(nodesMap.keySet().toArray());

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
        for (String node : proteinNodes.keySet()) {
            graph.addVertex(node);
        }
        for (String node : peptidesNodes.keySet()) {
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

    private void drawEdges() {
        if (liveWidth < 1 || liveHeight < 1) {
            return;
        }
        BufferedImage image = new BufferedImage(liveWidth, liveHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        for (Edge edge : edgesMap) {
            Shape edgeLine = drawEdge((int) edge.getStartX(), (int) edge.getStartY(), (int) edge.getEndX(), (int) edge.getEndY());
            if (edge.isSelected()) {
                g2.setPaint(Color.GRAY);
            } else {
                g2.setPaint(Color.LIGHT_GRAY);
            }
            if (edge.isDotted()) {
                g2.draw(dashLineStroke.createStrokedShape(edgeLine));
            } else {
                g2.draw(edgeLine);
            }
        }
//        for (String node : graph.getVertices()) {
//
//            Shape circle;
//            if (peptidesNodes.containsKey(node)) {
//                g2.setPaint(Color.GRAY);
//                g2.setPaint(new Color(25, 125, 225));
//
//                circle = new Ellipse2D.Double((int) tgraphlayout.getX(node) - 5 + 10, tgraphlayout.getY(node) - 5 + 10, 10, 10);
//            } else {
//                g2.setPaint(Color.LIGHT_GRAY);
//                g2.setPaint(new Color(233, 0, 0));
//
//                circle = new Ellipse2D.Double((int) tgraphlayout.getX(node) - 10 + 10, tgraphlayout.getY(node) - 10 + 10, 20, 20);
//            }
//            g2.fill(circle);
//            g2.setPaint(Color.WHITE);
//            g2.draw(circle);
//        }

        g2.dispose();
        byte[] imageData = null;
        try {
            ImageEncoder in = ImageEncoderFactory.newInstance(ImageFormat.PNG, 1);
            imageData = in.encode(image);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        String base64 = Base64.encodeBytes(imageData);
        base64 = "data:image/png;base64," + base64;
        edgesImage.setSource(new ExternalResource(base64));
        edgesImage.removeStyleName("hide");

//        canvas.clear();
//        canvas.setLineWidth(1);
//        canvas.setLineCap("round");
//        canvas.setStrokeStyle("lightgray");
//        canvas.beginPath();
//        for (Edge edge : edgesMap) {
//            if (edge.isSelected() && !edge.isDotted()) {
//                canvas.moveTo(edge.getStartX(), edge.getStartY());
//                canvas.lineTo(edge.getEndX(), edge.getEndY());
//
//            }
//        }
//        canvas.stroke();
//        canvas.setStrokeStyle("#c10a1e");
//        canvas.beginPath();
//        for (Edge edge : edgesMap) {
//            if (edge.isSelected() && edge.isDotted()) {
//                canvas.moveTo(edge.getStartX(), edge.getStartY());
//                canvas.lineTo(edge.getEndX(), edge.getEndY());
//
//            }
//        }
//        canvas.stroke();
//        canvas.setStrokeStyle("whitesmoke");
//        canvas.beginPath();
//        for (Edge edge : edgesMap) {
//            if (!edge.isSelected() && !edge.isDotted()) {
//                canvas.moveTo(edge.getStartX(), edge.getStartY());
//                canvas.lineTo(edge.getEndX(), edge.getEndY());
//            }
//        }
//        canvas.stroke();
//        canvas.setStrokeStyle("#ea9ea6");
//        canvas.beginPath();
//        for (Edge edge : edgesMap) {
//            if (!edge.isSelected() && edge.isDotted()) {
//                canvas.moveTo(edge.getStartX(), edge.getStartY());
//                canvas.lineTo(edge.getEndX(), edge.getEndY());
//
//            }
//        }
//        canvas.stroke();
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

    private String generateThumbImg() {
        FRLayout tgraphlayout = new FRLayout<>(graph);
        VisualizationViewer vv = new VisualizationViewer<>(tgraphlayout, new Dimension(80, 80));
        ScalingControl scaler = new CrossoverScalingControl();
        scaler.scale(vv, 0.9f, vv.getCenter());

        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setPaint(Color.GRAY);
        for (String edge : graph.getEdges()) {
            Shape line = drawLine((int) tgraphlayout.getX(graph.getEndpoints(edge).getFirst()) + 10, (int) tgraphlayout.getY(graph.getEndpoints(edge).getFirst()) + 10, (int) tgraphlayout.getX(graph.getEndpoints(edge).getSecond()) + 10, (int) tgraphlayout.getY(graph.getEndpoints(edge).getSecond()) + 10);
            g2.draw(line);
        }
        for (String node : graph.getVertices()) {

            Shape circle;
            if (peptidesNodes.containsKey(node)) {
                g2.setPaint(Color.GRAY);
                g2.setPaint(new Color(25, 125, 225));

                circle = new Ellipse2D.Double((int) tgraphlayout.getX(node) - 5 + 10, tgraphlayout.getY(node) - 5 + 10, 10, 10);
            } else {
                g2.setPaint(Color.LIGHT_GRAY);
                g2.setPaint(new Color(233, 0, 0));

                circle = new Ellipse2D.Double((int) tgraphlayout.getX(node) - 10 + 10, tgraphlayout.getY(node) - 10 + 10, 20, 20);
            }
            g2.fill(circle);
            g2.setPaint(Color.WHITE);
            g2.draw(circle);
        }

        g2.dispose();
        byte[] imageData = null;
        try {
            ImageEncoder in = ImageEncoderFactory.newInstance(ImageFormat.PNG, 1);
            imageData = in.encode(image);
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        String base64 = Base64.encodeBytes(imageData);
        base64 = "data:image/png;base64," + base64;
        return base64;

    }

    private Shape drawLine(int x1, int y1, int x2, int y2) {
        return new Line2D.Double(x1, y1, x2, y2);
    }

    private Shape drawEdge(int x1, int y1, int x2, int y2) {
        return new Line2D.Double(x1, y1, x2, y2);
    }

    public abstract void selectedItem(Set<Object> selectedParentItems, Set<Object> selectedChildsItems);

    public abstract void updateProteinsMode(String modeType);

    public void selectChildItem(Object parentId, Object childId) {
        redrawSelection(new Object[]{parentId, childId}, false);

    }

    public void selectParentItem(Object parentId) {

        redrawSelection(new Object[]{parentId}, true);

    }

    private void redrawSelection(Object[] ids, boolean selectRelatedNodes) {
        edgesImage.addStyleName("hide");
        nodesMap.values().forEach((node) -> {
            node.setSelected(false);
        });
        this.selectedPeptides.clear();
        this.selectedProteins.clear();
        for (Object id : ids) {
            Node n = nodesMap.get(id);
            if (uniqueOnly && n.getType() == 1) {
                n.setSelected(true);
                selectedPeptides.add(id);
            } else if (n.getType() == 0) {
                n.setSelected(true);
                selectedProteins.add(id);
            } else if (n.getType() == 1) {
                n.setSelected(true);
                selectedPeptides.add(id);
            }
            if (selectRelatedNodes) {
                edgesMap.stream().map((edge) -> {
                    edge.select(n, uniqueOnly);
                    return edge;
                }).map((edge) -> {
                    if (edge.getN2().isSelected()) {
                        selectedProteins.add(edge.getN2().getNodeId());
                    }
                    return edge;
                }).filter((edge) -> (edge.getN1().isSelected())).forEachOrdered((edge) -> {
                    selectedPeptides.add(edge.getN1().getNodeId());
                });
            }
        }
        drawEdges();
        graphInfo.setValue("#Proteins: <font style='float:right'>" + selectedProteins.size() + "</font><br/>#Peptides: <font style='float:right'>" + selectedPeptides.size() + "</font>");
    }

    public Set<Object> getSelectedProteins() {
        return selectedProteins;
    }

    public Set<Object> getSelectedPeptides() {
        return selectedPeptides;
    }

}
