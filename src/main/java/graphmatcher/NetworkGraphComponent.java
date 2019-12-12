package graphmatcher;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.itextpdf.text.pdf.codec.Base64;
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
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.jfree.chart.encoders.ImageEncoder;
import org.jfree.chart.encoders.ImageEncoderFactory;
import org.jfree.chart.encoders.ImageFormat;
import selectioncanvas.SelectioncanvasComponent2;

/**
 * This class represents Graph layout component
 *
 * @author Yehia Farag
 */
public abstract class NetworkGraphComponent extends VerticalLayout {

    private final AbsoluteLayout canvasWrapper;
    private final AbsoluteLayout canvas;
    private int liveWidth = 1000;
    private int liveHeight = 500;
    private Image edgesImage;
    private final Stroke internalStroke;
    private final Stroke externalStroke;
    private final Color edgeSelectedColor = new Color(0, 154, 255);
    private final Color edgeUnSelectedColor = Color.lightGray;
    /**
     * The graph.
     */
    private UndirectedSparseGraph<String, String> graph;
    /**
     * The nodes.
     */
    private final Map<String, Map<String, NetworkGraphNode>> graphNodes;
    /**
     * The edges: the keys are the node labels and the elements the list of
     * objects.
     */
    private Set<NetworkGraphEdge> graphEdges;
    /**
     * The nodes.
     */
    private final Map<String, NetworkGraphNode> activeGraphNodes;
    /**
     * The edges: the keys are the node labels and the elements the list of
     * objects.
     */
    private final HashSet<NetworkGraphEdge> activeGraphEdges;
    /**
     * Creates new form GraphForm
     */
    private final Label graphInfo;
    private VisualizationViewer visualizationViewer;
    private FRLayout graphLayout;
    private final AbsoluteLayout mainContainer;
    private final DropHandler dropHandler;
    private final AbsoluteLayout graphWrapper;
    private final SizeReporter sizeReporter;
    private final Set<Object> selectedNodes;
    private String thumbImgeUrl;
    private final OptionGroup nodeControl;
    private final HorizontalLayout bottomRightPanel;
    private final PopupView legendLayout;
    private final Legend informationLegend;
    private final SelectioncanvasComponent2 selectionCanavas;

    public Legend getInformationLegend() {
        return informationLegend;
    }

    public NetworkGraphComponent() {
        NetworkGraphComponent.this.setMargin(new MarginInfo(false, false, false, false));
        NetworkGraphComponent.this.setSizeFull();
        this.externalStroke = new BasicStroke(1f, // Width
                BasicStroke.CAP_BUTT, // End cap
                BasicStroke.JOIN_BEVEL, // Join style
                0.0f, new float[]{3.0f, 3.0f}, 0.0f);
        this.internalStroke = new BasicStroke(1f, // Width
                BasicStroke.CAP_BUTT, // End cap
                BasicStroke.JOIN_BEVEL, // Join style
                0.0f, new float[]{3.0f, 3.0f}, 0.0f);

        this.selectedNodes = new HashSet<>();
        //init main layout
        //calculate canavas dimension 
        mainContainer = new AbsoluteLayout();
        mainContainer.setSizeFull();
        mainContainer.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
            if (event.getClickedComponent() instanceof AbsoluteLayout) {
                selectNodes(new Object[]{});
            }
        });

        selectionCanavas = new SelectioncanvasComponent2() {
            @Override
            public void dragSelectionIsPerformed(double startX, double startY, double endX, double endY) {

                Set<NetworkGraphNode> selectedNodes = new HashSet<>();
                activeGraphEdges.forEach((edge) -> {
                    NetworkGraphNode n1 = edge.getN1();
                    NetworkGraphNode n2 = edge.getN2();
                    if ((n1.getX() <= endX && n1.getX() >= startX) && (n1.getY() <= endY && n1.getY() >= startY)) {
                        selectedNodes.add(n1);
                    }
                    if ((n2.getX() <= endX && n2.getX() >= startX) && (n2.getY() <= endY && n2.getY() >= startY)) {
                        selectedNodes.add(n2);
                    }
                });
                selectionAction(selectedNodes);
                System.out.println("at selected nodes are " + selectedNodes.size());

            }

            @Override
            public void rightSelectionIsPerformed(double startX, double startY) {
            }

            @Override
            public void leftSelectionIsPerformed(double startX, double startY) {
                System.out.println("at left selection invoked " + startX + "  " + startY);
            }

            @Override
            public void mouseWheeAction(int startX, int startY, int zoomLevel) {
            }
        };
        //calculate graph
        edgesImage = new Image();
        NetworkGraphComponent.this.edgesImage.setSizeFull();
        //draw graph    
        canvasWrapper = new AbsoluteLayout();
        canvasWrapper.setSizeFull();

        canvas = new AbsoluteLayout();
        canvas.setSizeFull();
        canvasWrapper.addComponent(canvas);
        sizeReporter = new SizeReporter(NetworkGraphComponent.this.canvas);
        sizeReporter.addResizeListener((ComponentResizeEvent event) -> {
            int tWidth = event.getWidth();
            int tHeight = event.getHeight();
            if (tWidth < 100 || tHeight < 100 || (Math.abs(tWidth - liveWidth) < 5 && Math.abs(tHeight - liveHeight) < 5)) {
                return;
            }
            if (liveWidth == tWidth && liveHeight == tHeight) {
                return;
            }
            liveWidth = tWidth;
            liveHeight = tHeight;
            selectionCanavas.setSize(tWidth, tHeight, true);
            updateGraphLayout();
        });
// Wrap the layout to allow handling drops
        DragAndDropWrapper layoutWrapper = new DragAndDropWrapper(canvasWrapper);
        layoutWrapper.addStyleName("draganddroplayout");
        NetworkGraphComponent.this.addComponent(mainContainer);
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
                    NetworkGraphNode n = activeGraphNodes.get(node.getData() + "");
                    n.setX(x);
                    n.setY(y);
                    graphLayout.setLocation(node.getData() + "", x, y);
                    drawEdges();
                    setShowLabels(nodeControl.getValue().toString().contains("Labels"));
                }
            }
        };
        layoutWrapper.setDropHandler(dropHandler);
        VerticalLayout wrapper = new VerticalLayout();
        wrapper.setSizeFull();
        wrapper.setMargin(new MarginInfo(true, true, true, true));
        wrapper.addComponent(edgesImage);
        graphWrapper = new AbsoluteLayout() {
            @Override
            public void setVisible(boolean visible) {
                super.setVisible(visible);

            }

        };
        graphWrapper.setSizeFull();
        graphWrapper.addComponent(wrapper, "left: 0px; top: 0px");
        wrapper = new VerticalLayout();
        wrapper.setSizeFull();
        wrapper.setMargin(new MarginInfo(true, true, true, true));
        wrapper.addComponent(layoutWrapper);
        graphWrapper.addComponent(wrapper, "left: 0px; top: 0px");
        mainContainer.addComponent(graphWrapper, "left: 0px; top: 0px");
        activeGraphEdges = new HashSet<>();
        activeGraphNodes = new LinkedHashMap<>();
        this.graphNodes = new LinkedHashMap<>();

        VerticalLayout leftBottomPanel = new VerticalLayout();
        leftBottomPanel.setSpacing(false);
        leftBottomPanel.setWidthUndefined();
        leftBottomPanel.addStyleName("inframe");
        leftBottomPanel.addStyleName("proteoformoptioncontrol");
        nodeControl = new OptionGroup();
        nodeControl.setMultiSelect(true);
        nodeControl.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);
        nodeControl.addStyleName(ValoTheme.OPTIONGROUP_SMALL);
        nodeControl.addStyleName("smallertext");
        nodeControl.addItem("External");
        nodeControl.addItem("Proteoform");
        nodeControl.addItem("Reactom Only");
        nodeControl.addItem("Labels");
        nodeControl.select("Proteoform");
        nodeControl.select("External");
        nodeControl.addValueChangeListener((Property.ValueChangeEvent event) -> {
//            if()
//                setShowLabels(nodeControl.getValue().toString().contains("Labels"));
//            else
            nodeControlAction(!nodeControl.getValue().toString().contains("Proteoform"), !nodeControl.getValue().toString().contains("External"), nodeControl.getValue().toString().contains("Reactom Only"));

        });
        leftBottomPanel.addComponent(nodeControl);
        mainContainer.addComponent(leftBottomPanel, "left: " + 10 + "px; bottom: " + -12 + "px");

        graphInfo = new Label();
        graphInfo.setContentMode(ContentMode.HTML);
        graphInfo.setStyleName(ValoTheme.LABEL_LIGHT);
        graphInfo.addStyleName(ValoTheme.LABEL_SMALL);
        graphInfo.addStyleName(ValoTheme.LABEL_TINY);
        graphInfo.setWidthUndefined();
        graphInfo.addStyleName("inframe");
        mainContainer.addComponent(graphInfo, "right: " + 10 + "px; top: " + 10 + "px");

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

        informationLegend = new Legend(true) {
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
        bottomRightPanel = new HorizontalLayout();
        bottomRightPanel.setSpacing(false);

        bottomRightPanel.setStyleName("inframe");
        bottomRightPanel.addStyleName("righttbottompanelstyle");
        bottomRightPanel.addComponent(selectAllBtn);
        bottomRightPanel.setComponentAlignment(selectAllBtn, Alignment.TOP_CENTER);
        bottomRightPanel.addComponent(updateLayoutBtn);
        bottomRightPanel.setComponentAlignment(updateLayoutBtn, Alignment.TOP_CENTER);
        bottomRightPanel.addComponent(legendLayoutBtn);
        bottomRightPanel.setComponentAlignment(legendLayoutBtn, Alignment.TOP_CENTER);
        mainContainer.addComponent(bottomRightPanel, "right: " + 10 + "px; bottom: " + -12 + "px");
        mainContainer.addComponent(legendLayout, "left: " + 50 + "%; top: " + 50 + "%");
        canvasWrapper.addComponent(selectionCanavas);
    }
    int counter = 0;

    public void updateGraphData(Set<String> selectedIds, Set<NetworkGraphEdge> edges) {//Map<String, Map<String, Node>> graphNodes
        this.canvas.removeAllComponents();
        this.selectedNodes.clear();
        this.graphNodes.clear();
        this.graphEdges = edges;
        this.activeGraphEdges.clear();
        this.activeGraphNodes.clear();
        edges.forEach((edge) -> {
            NetworkGraphNode n1 = edge.getN1();
            NetworkGraphNode n2 = edge.getN2();
            if (!graphNodes.containsKey(n1.getAccession())) {
                this.graphNodes.put(n1.getAccession(), new LinkedHashMap<>());

            }

            if (!graphNodes.containsKey(n2.getAccession())) {
                this.graphNodes.put(n2.getAccession(), new LinkedHashMap<>());
            }

            this.graphNodes.get(n1.getAccession()).put(n1.getNodeId(), n1);
            this.graphNodes.get(n2.getAccession()).put(n2.getNodeId(), n2);
            this.activeGraphNodes.put(n1.getNodeId(), n1);
            this.activeGraphNodes.put(n2.getNodeId(), n2);

        });
        graphNodes.keySet().forEach((key) -> {
            int indexer = 1;
            for (String subKey : graphNodes.get(key).keySet()) {
                if (subKey.contains(";")) {
                    graphNodes.get(key).get(subKey).updateProteformIndex("P" + indexer++);
                }
            }
        });
        selectedIds.forEach((id) -> {
            if (this.graphNodes.containsKey(id)) {
                this.graphNodes.get(id).values().forEach((n) -> {
                    n.setSelected(true);
                });
            } else {
                NetworkGraphNode node = new NetworkGraphNode(id, true, true) {
                    @Override
                    public void selected(String id) {
                    }
                };
                node.setSelected(true);
                this.graphNodes.put(id, new LinkedHashMap<>());
                this.graphNodes.get(id).put(id, node);
                this.activeGraphNodes.put(id, node);

            }
        });
        this.activeGraphEdges.addAll(edges);
        this.initializeGraphLayout();
        if (!nodeControl.getValue().toString().contains("Proteoform") || !nodeControl.getValue().toString().contains("External") || nodeControl.getValue().toString().contains("Reactom Only")) {
            nodeControlAction(!nodeControl.getValue().toString().contains("Proteoform"), !nodeControl.getValue().toString().contains("External"), nodeControl.getValue().toString().contains("Reactom Only"));
        }
        this.setEnabled((boolean) graphInfo.getData());

    }

    private void initializeGraphLayout() {
        layoutGraphData();
        canvas.removeAllComponents();
        graph.getVertices().forEach((node) -> {
            NetworkGraphNode n = activeGraphNodes.get(node);
            n.setX(graphLayout.getX(node));
            n.setY(graphLayout.getY(node));
            if (n.getWrappedComponent() == null) {
                final WrappedComponent wrapper = new WrappedComponent(n, dropHandler);
                wrapper.setSizeUndefined();
                wrapper.setData(node);
                wrapper.setDescription(n.getDescription());
                n.setWrappedComponent(wrapper);
            }
            canvas.addComponent(n.getWrappedComponent(), "left: " + n.getX() + "px; top: " + n.getY() + "px");
            if (n.isParent() && !n.isInternal()) {
                n.getWrappedComponent().setVisible(false);
            }

        });
        activeGraphEdges.forEach((edge) -> {
            canvas.addComponent(edge.getEdgeLabel());
        });
        drawEdges();
        setShowLabels(nodeControl.getValue().toString().contains("Labels"));
    }

    private void setShowLabels(boolean showLables) {
        activeGraphEdges.forEach((edge) -> {
            edge.getEdgeLabel().setVisible(false);
            if (edge.isSelected()) {
                if (showLables) {
                    int startX = ((int) edge.getStartX() + (int) edge.getEndX()) / 2;
                    int startY = ((int) edge.getStartY() + (int) edge.getEndY()) / 2;
                    edge.setLabelPostion(startX, startY);
                    edge.getEdgeLabel().setVisible(true);
                    AbsoluteLayout.ComponentPosition newPosition = canvas.getPosition(edge.getEdgeLabel());
                    newPosition.setCSSString("left: " + startX + "px; top: " + startY + "px");
                }
            }
        });

    }

    private void nodeControlAction(boolean hideProteoform, boolean hideExternal, boolean showReactomDataOnly) {
        if (hideProteoform && hideExternal) {
            activeGraphNodes.values().forEach((node) -> {
                if (node.isInternal() && node.isParent()) {
                    node.getWrappedComponent().setVisible(true);
                } else {
                    node.getWrappedComponent().setVisible(false);
                }
            });
            activeGraphEdges.stream().map((edge) -> {
                edge.setHideChildNodes(hideProteoform);
                return edge;
            }).forEachOrdered((edge) -> {
                if (edge.isInternalEdge() && !edge.isLocalEdge()) {
                    edge.setHide(false);
                } else {
                    edge.setHide(true);
                }
            });
        } else if (hideProteoform && !hideExternal) {

            setHideProtoeform();

        } else if (!hideProteoform && hideExternal) {

            setHideExternal();
//            
        } else {
            activeGraphNodes.values().forEach((node) -> {
                node.getWrappedComponent().setVisible(true);
                if (node.isParent() && !node.isInternal()) {
                    node.getWrappedComponent().setVisible(false);
                }
            });
            activeGraphEdges.stream().map((edge) -> {
                edge.setHideChildNodes(hideProteoform);
                return edge;
            }).forEachOrdered((edge) -> {
                if (edge.isInternalEdge() && edge.isLocalEdge()) {
                    edge.setHide(false);
                } else if (!edge.isInternalEdge() && !edge.isLocalEdge()) {
                    edge.setHide(false);
                } else {
                    edge.setHide(true);
                }
            });

        }
        if (showReactomDataOnly) {
            activeGraphNodes.values().forEach((node) -> {
                if (!node.isParent() && node.isInternal() && node.getWrappedComponent().isVisible()) {
                    node.getWrappedComponent().setVisible(node.getEdgesNumber() > 1);
                    for (NetworkGraphEdge e : node.getEdges()) {
                        if (e.isLocalEdge()) {
                            e.setHide(node.getEdgesNumber() <= 1);
                        }
                    }

                }
            });
        }

        redrawFull();
    }

    private void setHideProtoeform() {
        activeGraphNodes.values().forEach((node) -> {
            if (node.isProteoform()) {
                node.getWrappedComponent().setVisible(false);
            } else if (!node.isInternal() && node.isParent()) {
                node.getWrappedComponent().setVisible(true);
            }
        });
        activeGraphEdges.stream().map((edge) -> {
            edge.setHideChildNodes(true);
            return edge;
        }).forEachOrdered((edge) -> {
            edge.setHide(edge.isLocalEdge());
        });

    }

    private void setHideExternal() {
        activeGraphNodes.values().stream().map((node) -> {
            if (node.isProteoform()) {
                node.getWrappedComponent().setVisible(true);
            }
            return node;
        }).filter((node) -> (!node.isInternal())).forEachOrdered((node) -> {
            node.getWrappedComponent().setVisible(false);
        });
        activeGraphEdges.stream().map((edge) -> {
            edge.setHideChildNodes(false);
            return edge;
        }).map((edge) -> {
            edge.setHide(false);
            return edge;
        }).filter((edge) -> (!edge.isInternalEdge())).forEachOrdered((edge) -> {
            edge.setHide(true);
        });

    }

    private void redrawFull() {
        if (visualizationViewer != null) {
            edgesImage.addStyleName("hide");
            Map<String, NetworkGraphNode> layoutGraphNodes = new HashMap<>();
            activeGraphEdges.forEach((edge) -> {
                NetworkGraphNode n1 = edge.getN1();
                NetworkGraphNode n2 = edge.getN2();
                layoutGraphNodes.put(n1.getNodeId(), n1);
                layoutGraphNodes.put(n2.getNodeId(), n2);

            });
            Iterator<Component> itr = canvas.iterator();
            while (itr.hasNext()) {
                Component component = itr.next();
                if (component instanceof WrappedComponent) {
                    WrappedComponent wComp = (WrappedComponent) component;
                    String nodeName = wComp.getData() + "";
                    if (!layoutGraphNodes.containsKey(nodeName)) {
                        continue;
                    }
                    AbsoluteLayout.ComponentPosition newPosition = canvas.getPosition(wComp);
                    double x = graphLayout.getX(nodeName);
                    double y = graphLayout.getY(nodeName);
                    NetworkGraphNode n = layoutGraphNodes.get(nodeName);
                    n.setX(x);
                    n.setY(y);
                    if (y > liveHeight) {
                        System.out.println("at -------------- here comes error  y " + y + "   " + liveHeight);
                    } else if (x > liveWidth) {
                        System.out.println("at -------------- here comes error x  " + x + "   " + liveWidth);
                    }
                    newPosition.setCSSString("left: " + x + "px; top: " + y + "px");

                }
            }
            drawEdges();
            setShowLabels(nodeControl.getValue().toString().contains("Labels"));
        }
    }

    /**
     * Set up the graph.
     *
     * @param parentPanel the parent panel
     * @return the visualisation viewer
     */
    private void layoutGraphData() {
        graph = new UndirectedSparseGraph<>();
        // add all the nodes
        activeGraphNodes.keySet().forEach((node) -> {
            graph.addVertex(node);
        });

        // add the vertexes
        activeGraphEdges.forEach((edge) -> {
            graph.addEdge(edge.getN1().getNodeId() + "|" + edge.getN2().getNodeId(), edge.getN1().getNodeId(), edge.getN2().getNodeId());
        });
        // create the visualization viewer
        graphLayout = new FRLayout<>(graph);
        visualizationViewer = new VisualizationViewer<>(graphLayout, new Dimension(liveWidth, liveHeight));
    }

    private void drawEdges() {
        Set<NetworkGraphNode> internalNodes = new HashSet<>();
        Set<NetworkGraphNode> externalNodes = new HashSet<>();
        if (liveWidth < 1 || liveHeight < 1) {
            return;
        }
        BufferedImage image = new BufferedImage(liveWidth, liveHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        activeGraphEdges.forEach((edge) -> {
            if (!edge.isHide()) {
                Shape edgeLine = drawEdge((int) edge.getStartX(), (int) edge.getStartY(), (int) edge.getEndX(), (int) edge.getEndY());

                if (edge.isSelected()) {
                    g2.setPaint(edgeSelectedColor);
                } else {
                    g2.setPaint(edgeUnSelectedColor);
                }
                if (edge.isLocalEdge()) {
                    g2.draw((edgeLine));
                } else if (edge.isInternalEdge()) {
                    g2.draw(internalStroke.createStrokedShape(edgeLine));
                } else {
                    g2.draw(externalStroke.createStrokedShape(edgeLine));
                }
                if (edge.getN1().isInternal()) {
                    internalNodes.add(edge.getN1());
                } else {

                    externalNodes.add(edge.getN1());
                }
                if (edge.getN2().isInternal()) {
                    internalNodes.add(edge.getN2());
                } else {
                    externalNodes.add(edge.getN2());
                }

            }

        });//             

        g2.dispose();
        byte[] imageData = null;
        try {
            ImageEncoder in = ImageEncoderFactory.newInstance(ImageFormat.PNG, 1);
            imageData = in.encode(image);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

        String base64 = Base64.encodeBytes(imageData);
        base64 = "data:image/png;base64," + base64;
        edgesImage.setSource(new ExternalResource(base64));
        edgesImage.removeStyleName("hide");
        graphInfo.setValue("#Internal: <font style='float:right ;padding-left: 3px;'>" + (internalNodes.size() - 1) + "</font><br/>#External: <font style='float:right ;padding-left: 3px;'>" + externalNodes.size() + "</font>");
        graphInfo.setData(!internalNodes.isEmpty());

    }

    private Shape drawLine(int x1, int y1, int x2, int y2) {
        return new Line2D.Double(x1, y1, x2, y2);
    }

    private Shape drawEdge(int x1, int y1, int x2, int y2) {
        return new Line2D.Double(x1, y1, x2, y2);
    }

    private void selectNodes(Object[] ids) {

//        redrawSelection(ids, true);
//        selectedItem(selectedNodes, selectedPeptides);
    }

    private void selectAll() {
        selectNodes(graphNodes.keySet().toArray());
    }

    private void updateGraphLayout() {
        if (graph == null) {
            return;
        }
        graphLayout = new FRLayout<>(graph);
        visualizationViewer = new VisualizationViewer<>(graphLayout, new Dimension(liveWidth, liveHeight));
        redrawFull();
    }

    public String getThumbImgeUrl() {
        return thumbImgeUrl;
    }

    private String generateThumbImg() {
        FRLayout tgraphlayout = new FRLayout<>(graph);
//           tgraphlayout.setMaxIterations(itrNumber);
//        tgraphlayout.setAttractionMultiplier(1.0);
//        tgraphlayout.setRepulsionMultiplier(0.04);
//        tgraphlayout.initialize();
        VisualizationViewer vv = new VisualizationViewer<>(tgraphlayout, new Dimension(80, 80));
        visualizationViewer.getRenderContext().setEdgeShapeTransformer(new EdgeShape.QuadCurve<>());
        ScalingControl scaler = new CrossoverScalingControl();
        scaler.scale(vv, 0.9f, vv.getCenter());

        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();
        g2.setPaint(Color.GRAY);
        graph.getEdges().stream().map((edge) -> drawLine((int) tgraphlayout.getX(graph.getEndpoints(edge).getFirst()) + 10, (int) tgraphlayout.getY(graph.getEndpoints(edge).getFirst()) + 10, (int) tgraphlayout.getX(graph.getEndpoints(edge).getSecond()) + 10, (int) tgraphlayout.getY(graph.getEndpoints(edge).getSecond()) + 10)).forEachOrdered((line) -> {
            g2.draw(line);
        });
        graph.getVertices().stream().map((node) -> {
            Shape circle;
            g2.setPaint(Color.LIGHT_GRAY);
            g2.setPaint(new Color(233, 0, 0));

            circle = new Ellipse2D.Double((int) tgraphlayout.getX(node) - 10 + 10, tgraphlayout.getY(node) - 10 + 10, 20, 20);

            return circle;
        }).map((circle) -> {
            g2.fill(circle);
            return circle;
        }).forEachOrdered((circle) -> {
            g2.setPaint(Color.WHITE);
            g2.draw(circle);
        });

        g2.dispose();
        byte[] imageData = null;
        try {
            ImageEncoder in = ImageEncoderFactory.newInstance(ImageFormat.PNG, 1);
            imageData = in.encode(image);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

        String base64 = Base64.encodeBytes(imageData);
        base64 = "data:image/png;base64," + base64;
        return base64;

    }

    public void selectParentItem(Object parentId) {
        System.out.println("at select parent " + parentId);
        redrawSelection(new Object[]{parentId});

    }

    private void redrawSelection(Object[] ids) {
        edgesImage.addStyleName("hide");
        activeGraphNodes.values().forEach((node) -> {
            node.setSelected(false);
        });
        this.selectedNodes.clear();
        for (Object id : ids) {
            NetworkGraphNode n = activeGraphNodes.get(id + "");
            n.setSelected(true);
        }
        drawEdges();
        setShowLabels(nodeControl.getValue().toString().contains("Labels"));
    }

    public Set<Object> getSelectedNodes() {
        return selectedNodes;
    }

    public VisualizationViewer getVisualizationViewer() {
        return visualizationViewer;
    }

//   
    public void updateGraph() {
//        readNodesFile();

    }

    private void selectionAction(Set<NetworkGraphNode> selectedNodes) {
        Set<Object> selectedParentItems = new LinkedHashSet<>();
        Set<Object> selectedChiledItems = new LinkedHashSet<>();
        activeGraphNodes.values().forEach((node) -> {
            node.setSelected(false);
        });
        for (NetworkGraphNode node : selectedNodes) {
            node.setSelected(true);
            if (node.isInternal() && node.isParent()) {
                selectedParentItems.add(node.getAccession());
                node.getEdges().stream().map((edge) -> {
                    edge.getN2().setSelected(true);
                    selectedChiledItems.add(edge.getN2().getNodeId());
                    return edge;
                }).forEachOrdered((edge) -> {
                    edge.getN1().setSelected(true);
                    selectedChiledItems.add(edge.getN1().getNodeId());
                });

            } else if (node.isInternal()) {
                node.setSelected(true);
                NetworkGraphNode parentNode = node.getParentNode();
                parentNode.setSelected(true);
                selectedParentItems.add(parentNode.getAccession());
                selectedChiledItems.add(node.getNodeId());

            } else {
                for (NetworkGraphEdge edge : node.getEdges()) {
                    if (edge.getN1().isInternal()) {
                        node = edge.getN1();
                    } else if (edge.getN2().isInternal()) {
                        node = edge.getN2();
                    } else {
                        continue;
                    }
                    node.setSelected(true);
                    selectedChiledItems.add(node.getNodeId());
                    NetworkGraphNode parentNode = node.getParentNode();
                    selectedParentItems.add(parentNode.getAccession());
                    parentNode.setSelected(true);
//                    parentNode.getEdges().stream().map((edge1) -> {
//                        edge1.getN2().setSelected(true);
//                        return edge1;
//                    }).forEachOrdered((edge1) -> {
//                        edge1.getN1().setSelected(true);
//                    });
                }

            }
//            if (node.isInternal()) {
//                NetworkGraphNode parentNode = node.getParentNode();
//                selectedParentItems.add(parentNode.getAccession());
//                parentNode.getEdges().stream().map((edge) -> {
//                    edge.getN2().setSelected(true);
//                    return edge;
//                }).forEachOrdered((edge) -> {
//                    edge.getN1().setSelected(true);
//                });
//            } else {
//                for (NetworkGraphEdge edge : node.getEdges()) {
//                    if (edge.getN1().isInternal()) {
//                        node = edge.getN1();
//                    } else if (edge.getN2().isInternal()) {
//                        node = edge.getN2();
//                    } else {
//                        continue;
//                    }
//                    node.setSelected(true);
//                    NetworkGraphNode parentNode = node.getParentNode();
//                    selectedParentItems.add(parentNode.getAccession());
//                    parentNode.getEdges().stream().map((edge1) -> {
//                        edge1.getN2().setSelected(true);
//                        return edge1;
//                    }).forEachOrdered((edge1) -> {
//                        edge1.getN1().setSelected(true);
//                    });
//                }
//
//            }

        }
        drawEdges();
        setShowLabels(nodeControl.getValue().toString().contains("Labels"));
        selectedItem(selectedParentItems, selectedChiledItems);

    }

    public abstract void selectedItem(Set<Object> selectedParentItemsm, Set<Object> selectedChildItems);

    /**
     * This class is a wrapper for the dropped component that is used in the
     * Drag-Drop layout.
     *
     * @author Yehia Farag
     */
    protected class WrappedComponent extends DragAndDropWrapper {

        /**
         * The layout drop handler.
         */
        private final DropHandler dropHandler;

        /**
         * Constructor to initialise the main attributes.
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
