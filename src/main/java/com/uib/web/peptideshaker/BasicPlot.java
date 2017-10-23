package com.uib.web.peptideshaker;

import com.byteowls.vaadin.chartjs.ChartJs;
import com.byteowls.vaadin.chartjs.config.LineChartConfig;
import com.byteowls.vaadin.chartjs.options.Position;
import com.byteowls.vaadin.chartjs.options.scale.Axis;
import com.byteowls.vaadin.chartjs.options.scale.LinearScale;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import java.util.logging.Level;
import java.util.logging.Logger;


public class BasicPlot {
    
    HorizontalLayout grid = new HorizontalLayout();
    int labelNumber = 0;
    LinearScale scale;
    LineChartConfig lineConfig;
    
    public BasicPlot() {
        
        lineConfig = new LineChartConfig();
        
        scale = new LinearScale();
        
        scale.display(true).scaleLabel().display(true).labelString("Initial label")
                .and().position(Position.RIGHT);
        lineConfig.options().scales().add(Axis.Y, scale);
        lineConfig.options().maintainAspectRatio(false);
        
        lineConfig.options().title().display(true).text("Initial title");
        
        chart = new ChartJs(lineConfig);
        chart.setSizeFull();
        grid.addComponent(chart);
        
        scale.scaleLabel().labelString("New label 1");
        lineConfig.options().title().display(true).text("New label 1");
        
        
        Button button = new Button("Adjust label");
        button.addClickListener(event -> adjustLabel());
        grid.addComponent(button); 
        grid.setSizeFull();
        
        
        scale.scaleLabel().labelString("New label 2");
        lineConfig.options().title().display(true).text("New label 2");
        
        labelNumber = 2;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.currentThread().sleep(20000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(BasicPlot.class.getName()).log(Level.SEVERE, null, ex);
                }
                  adjustLabel();
                }
        });
        t.start();
//        adjustLabel();
//        adjustLabel();
        

    }
    ChartJs chart ;
    
    private void adjustLabel() {
        labelNumber++;
        String newLabel = "Label " + labelNumber;
        System.out.println("Title and axis Y axis label should now read: " + newLabel);
       
        lineConfig.options().title().text(newLabel);        
        scale.scaleLabel().labelString(newLabel);
        chart.setImmediate(true);
        chart.markAsDirty();
        chart.refreshData();
        
        
        
        
        
        
    }
    
    public Component getGrid() {
        return grid;
    }    
    
}
