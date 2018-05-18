package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.event.LayoutEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Label;
import java.awt.Point;
import org.vaadin.hezamu.canvas.Canvas;

/**
 * HTML5 Canvas to support mouse move listeners and will be used as an extra
 * layer over charts to support mouse move actions
 *
 * @author Yehia Farag
 */
public abstract class UnusedSelectionCanvas extends AbsoluteLayout {

    private final Canvas canvas;
    private boolean keydown = false;
    private final Point start;
    private final Point end;
    private boolean rightBtn;
    
     Label selectionCanvasPanel;

    public UnusedSelectionCanvas() {
        start = new Point(-1, -1);
        end = new Point(-1, -1);

        UnusedSelectionCanvas.this.setStyleName("iframecontainer");
         selectionCanvasPanel = new Label("<iframe id=\"selectionpanelcanvs\" src='VAADIN\\SelectionCanvas\\selectionCanvasFrame.html' style='width:100%; height:100%;border: none;'></iframe>", ContentMode.HTML);
        selectionCanvasPanel.setSizeFull();
   

        UnusedSelectionCanvas.this.addLayoutClickListener(new LayoutEvents.LayoutClickListener() {
            @Override
            public void layoutClick(LayoutEvents.LayoutClickEvent event) {
                System.out.println("iframe start and end selection");
            }
        });

        BrowserFrame browser = new BrowserFrame(null,
                new ExternalResource("VAADIN\\SelectionCanvas\\selectionCanvasFrame.html"));
        browser.setWidth("100%");
        browser.setHeight("100%");
        UnusedSelectionCanvas.this.addComponent(browser);// 
        browser.setEnabled(false);
      
        
        browser.addListener(new Listener() {
            @Override
            public void componentEvent(Event event) {
                System.out.println(".componentEvent()"+ event);
            }
        });
        
         selectionCanvasPanel.addStyleName("topzindex");

        canvas = new Canvas();
        
       
        
        
        UnusedSelectionCanvas.this.setStyleName("selectioncanvas");
        UnusedSelectionCanvas.this.addComponent(canvas);
     UnusedSelectionCanvas.this.addComponent(selectionCanvasPanel);
        canvas.addMouseDownListener(() -> {
            System.out.println("at mouse down ");
           
//            keydown = true;
        });
        canvas.addMouseUpListener(() -> {
//             selectionCanvasPanel.removeStyleName("topzindex");
              System.out.println("at mouse up ");
//            keydown = false;
//            canvas.clear();
//            if ((start.getX() == end.getX() && start.getY() == end.getY()) || rightBtn) {
//                return;
//            }
//            dragSelectionIsPerformed(start.getX(), start.getY(), end.getX(), end.getY());
//            start.setLocation(-1, -1);
//            rightBtn = false;
        });
//
//        canvas.addMouseMoveListener((MouseEventDetails mouseDetails) -> {
//            if (keydown) {
//                if (start.getX() == -1) {
//                    start.setLocation(mouseDetails.getRelativeX(), mouseDetails.getRelativeY());
//                }
//                end.setLocation(mouseDetails.getRelativeX(), start.getY());
//                drawLines(mouseDetails.getRelativeX());
//            }
//        });
//        UnusedSelectionCanvas.this.addLayoutClickListener((LayoutEvents.LayoutClickEvent event) -> {
//            rightBtn = event.getButton().getName().equalsIgnoreCase("right");//                dragSelectionIsPerformed(start.getX(), start.getY(), end.getX(), end.getY(), true);
////                 dragSelectionIsPerformed(start.getX(), start.getY(), end.getX(), end.getY(), rightBtn);
//            if (rightBtn) {
//                System.out.println("at ----  right clicked action");
//                rightSelectionIsPerformed(start.getX(), start.getY());
//            } else if (start.getX() == end.getX() && start.getY() == end.getY()) {
//                 leftSelectionIsPerformed(event.getRelativeX(), event.getRelativeY());
//                System.out.println("at ----   left clicked action");
//            }
//            rightBtn = false;
//        });

    }

    public void setSize(int width, int height) {
        this.setWidth(width, Unit.PIXELS);
        this.setHeight(height, Unit.PIXELS);
        canvas.setWidth("" + width);
        canvas.setHeight("" + height);
    }

    private void drawLines(int s) {
        canvas.clear();
        canvas.beginPath();
        canvas.moveTo(start.getX(), start.getY());
        canvas.lineTo(s, start.getY());
        canvas.setStrokeStyle("#000000");
        canvas.stroke();
        canvas.closePath();

    }

//    public abstract void dragSelectionIsPerformed(double startX, double startY, double endX, double endY);
//
//    public abstract void rightSelectionIsPerformed(double startX, double startY);
//
//    public abstract void leftSelectionIsPerformed(double startX, double startY);

}
