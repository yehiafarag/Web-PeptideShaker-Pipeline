package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.ui.VerticalLayout;

/**
 * This class represent coloured based Inference label
 *
 * @author Yehia Farag
 */
public class ColorLabel extends VerticalLayout implements Comparable<ColorLabel>{

    private final String[] colorStyles = new String[]{"whitecolor", "greenincolor", "yellowincolor", "orangeincolor", "redincolor"};
private final Integer colorIndex;
    public ColorLabel(int colorIndex, String description) {
        this.colorIndex=colorIndex;
        ColorLabel.this.setSizeFull();
        ColorLabel.this.setStyleName("colorlabelfortablecell");
        ColorLabel.this.addStyleName(colorStyles[colorIndex]);
        ColorLabel.this.setDescription(description);
    }

    @Override
    public int compareTo(ColorLabel t) {
        return this.colorIndex.compareTo(t.colorIndex);
    }
    

}
