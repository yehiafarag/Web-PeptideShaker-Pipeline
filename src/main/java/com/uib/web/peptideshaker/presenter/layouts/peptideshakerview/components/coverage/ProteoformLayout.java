package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components.coverage;

import com.vaadin.event.LayoutEvents;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.ThemeResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Component;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;
import graphmatcher.ReactomWindow;
import java.awt.Color;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This class represents the proteins protoform layout that link the protoform
 * to the protein coverage and to the modified peptides
 *
 * @author Yehia Farag
 */
public abstract class ProteoformLayout extends AbsoluteLayout implements LayoutEvents.LayoutClickListener {
    
    private final String proteoformKey;
    private final Set<ProtoformModificationLayout>includedModifications;
    
    public ProteoformLayout(int index, int topLocation, Color finalColor, String proteoformKey, boolean availableOnReactome) {
        ProteoformLayout.this.setWidth(100, Unit.PERCENTAGE);
        ProteoformLayout.this.setHeight(15, Unit.PIXELS);
        this.includedModifications = new LinkedHashSet<>();
        this.proteoformKey = proteoformKey;
        ProteoformLayout.this.setStyleName("protoformlayout");
        Label label = new Label("<font style='background:rgb(" + finalColor.getRed() + "," + finalColor.getGreen() + "," + finalColor.getBlue() + ");color:white'>P " + index + "</font>", ContentMode.HTML);
        label.setWidth(25, Unit.PIXELS);
        ProteoformLayout.this.addComponent(label, "left:0px;top:0px;");
        
        VerticalLayout centeredLine = new VerticalLayout();
        centeredLine.setWidth(100, Unit.PERCENTAGE);
        centeredLine.setHeight(2, Unit.PIXELS);
        centeredLine.setStyleName("graylayout");
        ProteoformLayout.this.addComponent(centeredLine, "left:0px;top:6.5px;right:0px");
        
        ProteoformLayout.this.addLayoutClickListener(ProteoformLayout.this);
//        if (availableOnReactome) {
            Image reactomIcon = new Image();
            reactomIcon.setSource(new ThemeResource("img/reactom_gray.png"));
            reactomIcon.setStyleName("reactomicon");
            ProteoformLayout.this.addComponent(reactomIcon, "top:0px;right:0px");
            reactomIcon.setDescription("Pathway");
            reactomIcon.setEnabled(availableOnReactome);
            
            Link reactomLink = new Link("", new ExternalResource("https://reactome.org/content/query?q=" + proteoformKey.substring(0, proteoformKey.length()-1) + "&types=Protein&cluster=true"));
            reactomLink.setCaptionAsHtml(true);
            reactomLink.setIcon(new ThemeResource("img/reactom.png"));
            reactomLink.setStyleName("reactomicon");
            reactomLink.setSizeFull();
            reactomLink.setTargetName("_blank");
            ProteoformLayout.this.addComponent(reactomLink, "top:0px;right:0px");
            reactomLink.setDescription("View in Reactom");
            reactomLink.setEnabled(availableOnReactome);
//        }
    }
    
    public void updateHighlightedComponents(AbsoluteLayout containerToCopy) {
        Iterator<Component> itr = containerToCopy.iterator();
        while (itr.hasNext()) {
            HighlightPeptide c = (HighlightPeptide) itr.next();
            ProteoformLayout.this.addComponent(c.cloneComponent(), containerToCopy.getPosition(c).getCSSString());
        }
        
    }
    ReactomWindow w = new ReactomWindow();
    
    @Override
    public void layoutClick(LayoutEvents.LayoutClickEvent event) {
        Component c = event.getClickedComponent();
        if (c != null && (c instanceof Image)) {
            if(c.isEnabled())
            w.visulaizeProtein(proteoformKey);
        } else {
            selectProtoform(this);
        }
    }
    
    
    public void addModificationLayout(ProtoformModificationLayout mod){
        this.includedModifications.add(mod);
    }

    public Set<ProtoformModificationLayout> getIncludedModifications() {
        return includedModifications;
    }
    public abstract void selectProtoform(ProteoformLayout protoform);
    
}
