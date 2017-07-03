package com.uib.web.peptideshaker.presenter.components.peptideshakerview;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.VerticalLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.vaadin.hezamu.canvas.Canvas;

/**
 * This class represents Graph layout component
 *
 * @author Yehia Farag
 */
public class GraphComponent extends VerticalLayout {

    private final Canvas canvas;
    private int width;
    private int height;

    /**
     * The graph.
     */
    private UndirectedSparseGraph<String, String> graph;
    /**
     * The nodes.
     */
    private ArrayList<String> nodes;
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

    public GraphComponent() {

        //init main layout
        GraphComponent.this.addStyleName("lightgraylayout");
        //calculate canavas dimension 
        SizeReporter sizeReporter = new SizeReporter(GraphComponent.this);
        sizeReporter.addResizeListener((ComponentResizeEvent event) -> {
            width = event.getWidth();
            height = event.getHeight();
            System.out.println("Panel size: " + width + " x " + height);
        });
        
        graphContainer = new AbsoluteLayout();
        graphContainer.setSizeFull();
         GraphComponent.this.addComponent(graphContainer);

        //calculate graph
        //draw graph
        graphContainer.addComponent(canvas = new Canvas());
        GraphComponent.this.canvas.setSizeFull();
        GraphComponent.this.setSizeFull();
//        canvas.setSizeFull();
////    drawLines();
//        drawInitialPattern();
//        canvas.addMouseUpListener(new Canvas.CanvasMouseUpListener() {
//            @Override
//            public void onMouseUp() {
//                System.out.println("mouse up");
//            }
//        });
//        canvas.addMouseDownListener(new Canvas.CanvasMouseDownListener() {
//            @Override
//            public void onMouseDown() {
//                System.out.println("mouse down");
//            }
//        });
    }

    public void initGraph(ArrayList<String> nodes, HashMap<String, ArrayList<String>> edges) {
        canvas.clear();
        graphContainer.removeAllComponents();
        //calculate graph
        this.nodes=nodes;
        this.edges=edges;
        visualizationViewer = setUpGraph();
        for (String node : graph.getVertices()) {
            graphContainer.addComponent(initNode(), "left: " +graphLayout.getX(node) + "px; top: " + graphLayout.getY(node) + "px");
//            System.out.println("at fr neame " + node + "   x: " + graphLayout.getX(node) + "  y: " + graphLayout.getX(node));

        }

    }
    private VerticalLayout initNode(){
    VerticalLayout node = new VerticalLayout();
    node.setWidth(20,Unit.PIXELS);
    node.setHeight(20,Unit.PIXELS);
    node.setStyleName("node");
    return node;
    
    }
    private void reDrawGraph(){
    
     visualizationViewer = setUpGraph();
        ScalingControl scaler = new CrossoverScalingControl();
        scaler.scale(visualizationViewer, 0.9f, visualizationViewer.getCenter());
//        jPanel1.add(visualizationViewer);
//        jPanel1.revalidate();
//        jPanel1.repaint();
//        for (String node : graph.getVertices()) {
//            graphLayout.setLocation(node, 100, 100);        }

        for (String node : graph.getVertices()) {
            System.out.println("at fr neame " + node + "   x: " + graphLayout.getX(node) + "  y: " + graphLayout.getX(node));

        }}

    /**
     * Set up the graph.
     *
     * @param parentPanel the parent panel
     * @return the visualization viewer
     */
    private VisualizationViewer setUpGraph() {

        graph = new UndirectedSparseGraph<>();

        // add all the nodes
        for (String node : nodes) {
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
        VisualizationViewer<String, String> vv = new VisualizationViewer<>(graphLayout,
                new Dimension(width - 20, height - 100));
        vv.setBackground(Color.WHITE);

        // set the vertex label transformer
//        vv.getRenderContext().setVertexLabelTransformer(new Transformer<String, String>() {
//            @Override
//            public String transform(String arg0) {
//                return arg0;
//            }
//        });
        // set the edge label transformer
//        vv.getRenderContext().setEdgeLabelTransformer(new Transformer<String, String>() {
//            @Override
//            public String transform(String arg0) {
//                return arg0;
//            }
//        });
        // set the vertex renderer
//        vv.getRenderer().setVertexRenderer(new ProteinInferenceVertexRenderer());
//        // set the edge label renderer
//        vv.getRenderer().setEdgeLabelRenderer(new BasicEdgeLabelRenderer<String, String>() {
//
//            @Override
//            public void labelEdge(RenderContext<String, String> rc, Layout<String, String> layout, String e, String label) {
//                // do nothing
//            }
//        });
//
//        // set the vertex label renderer
//        vv.getRenderer().setVertexLabelRenderer(new BasicVertexLabelRenderer<String, String>() {
//
//            @Override
//            public void labelVertex(RenderContext<String, String> rc, Layout<String, String> layout, String v, String label) {
//                if (label.startsWith("Peptide") && showPeptideLabels) {
//                    String fullTooltip = nodeToolTips.get(label);
//                    super.labelVertex(rc, layout, v, fullTooltip.substring(0, fullTooltip.indexOf("<br>")));
//                }
//                if (label.startsWith("Protein") && showProteinLabels) {
//                    super.labelVertex(rc, layout, v, label.substring(label.indexOf(" ") + 1));
//                }
//            }
//        });
        // set the edge format
//        vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
//        vv.getRenderContext().setEdgeStrokeTransformer(edgeStroke);
//        // set the mouse interaction mode
//        final DefaultModalGraphMouse<String, Number> graphMouse = new DefaultModalGraphMouse<String, Number>();
//        graphMouse.setMode(ModalGraphMouse.Mode.PICKING);
//        vv.setGraphMouse(graphMouse);
        // add a key listener
//        vv.addKeyListener(new KeyAdapter() {
//
//            @Override
//            public void keyReleased(KeyEvent e) {
//                if (e.isControlDown() && e.getKeyCode() == KeyEvent.VK_A) {
//                    for (String tempNode : nodes) {
//                        visualizationViewer.getPickedVertexState().pick(tempNode, true);
//                    }
//                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
//                    for (String tempNode : nodes) {
//                        visualizationViewer.getPickedVertexState().pick(tempNode, false);
//                    }
//                }
//                super.keyReleased(e);
//            }
//
//        });
        // set the vertex tooltips
//        vv.setVertexToolTipTransformer(
//                new ToStringLabeller<String>() {
//
//                    @Override
//                    public String transform(String v) {
//                        if (nodeToolTips != null && nodeToolTips.get(v) != null) {
//                            return super.transform(nodeToolTips.get(v));
//                        } else {
//                            return super.transform(v.substring(v.indexOf(" ") + 1));
//                        }
//                    }
//                }
//        );
        // attach the listener that will print when the vertices selection changes
//        final PickedState<String> pickedState = vv.getPickedVertexState();
//        pickedState.addItemListener(
//                new ItemListener() {
//
//                    @Override
//                    public void itemStateChanged(ItemEvent e) {
//                        Object subject = e.getItem();
//                        if (subject instanceof String) {
//                            String vertex = (String) subject;
//                            if (pickedState.isPicked(vertex)) {
//                                if (!selectedNodes.contains(vertex)) {
//                                    selectedNodes.add(vertex);
//                                }
//                            } else {
//                                selectedNodes.remove(vertex);
//                            }
//                        }
//                        updateNodeSelection();
//                    }
//                }
//        );
        return vv;
    }

    private void drawInitialPattern() {
        canvas.saveContext();
        canvas.translate(175d, 175d);
        canvas.scale(1.6d, 1.6d);

        for (int i = 1; i < 6; ++i) {
            canvas.saveContext();
            canvas.setFillStyle("rgb(" + (51 * i) + "," + (255 - 51 * i)
                    + ",255)");

            for (int j = 0; j < i * 6; ++j) {
                canvas.rotate((Math.PI * 2d / (i * 6)));
                canvas.beginPath();
                canvas.arc(0d, i * 12.5d, 5d, 0d, Math.PI * 2d, true);
                canvas.closePath();
                canvas.fill();
            }

            canvas.restoreContext();
        }

        canvas.closePath();

        canvas.restoreContext();
    }

    private void drawLines() {
        canvas.saveContext();
        canvas.clear();

        canvas.beginPath();
        canvas.setLineWidth(1);
        canvas.setLineCap("round");
        canvas.setMiterLimit(1);
        canvas.moveTo(10, 50);
        canvas.lineTo(30, 150);
        canvas.lineTo(50, 50);
        canvas.stroke();
        canvas.closePath();

//		canvas.beginPath();
//		canvas.setLineWidth(5);
//		canvas.setLineCap("butt");
//		canvas.setLineJoin("round");
//		canvas.setMiterLimit(1);
//		canvas.moveTo(70, 50);
//		canvas.lineTo(90, 150);
//		canvas.lineTo(110, 50);
//		canvas.stroke();
//		canvas.closePath();
//
//		canvas.beginPath();
//		canvas.moveTo(20, 200);
//		canvas.quadraticCurveTo(20, 275, 200, 200);
//		canvas.stroke();
        canvas.restoreContext();
    }

}
