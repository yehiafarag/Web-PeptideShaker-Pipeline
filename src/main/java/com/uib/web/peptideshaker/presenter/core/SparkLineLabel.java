package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import java.awt.Color;
import java.util.Map;

/**
 * This class represent Spark-line label to be used inside tables the spark line
 * support staked barchart with 1,2 or 3 colours
 *
 * @author Yehia Farag
 */
public abstract class SparkLineLabel extends AbsoluteLayout implements Comparable<SparkLineLabel>{

    private final Label textLabel;
 

    public SparkLineLabel(String labelValue, Map<String, Number> values, Object itemId) {
        SparkLineLabel.this.setSizeFull();
        SparkLineLabel.this.setStyleName("sparkline");
        textLabel = new Label(labelValue, ContentMode.HTML);
        textLabel.setStyleName("sparklinelabel");
        SparkLineLabel.this.addComponent(textLabel,"left:10px;top:0px");
        HorizontalLayout sparkLineContainer = initSparkLine(values);
        SparkLineLabel.this.addComponent(sparkLineContainer,"left:50px;top:0px");
        SparkLineLabel.this.addLayoutClickListener((event) -> {
            selected(itemId);
        });

    }

    private HorizontalLayout initSparkLine(Map<String, Number> values) {
        HorizontalLayout container = new HorizontalLayout();
        container.setSizeFull();
        container.setStyleName("sparklinecontainer");
        float left = 1;
        for (String style : values.keySet()) {
            VerticalLayout dataLayout = new VerticalLayout();
            dataLayout.setStyleName(style);
            dataLayout.setSizeFull();
            container.addComponent(dataLayout);
            container.setExpandRatio(dataLayout, (float) values.get(style));
            left = left - (float) values.get(style);

        }
        if (left > 0) {
            VerticalLayout leftLayout = new VerticalLayout();
            leftLayout.setSizeFull();
            container.addComponent(leftLayout);
            container.setExpandRatio(leftLayout, left);
        }

        return container;
    }

    public abstract void selected(Object itemId);

    @Override
    public int compareTo(SparkLineLabel o) {
        return textLabel.getValue().compareTo(o.textLabel.getValue());
    }
    

}
