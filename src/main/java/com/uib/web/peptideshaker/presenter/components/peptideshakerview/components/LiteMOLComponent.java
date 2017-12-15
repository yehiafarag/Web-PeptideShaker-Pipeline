package com.uib.web.peptideshaker.presenter.components.peptideshakerview.components;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * This class provides an abstraction layer for LiteMOL 3D protein structure
 *
 * @author Yehia Farag
 */
public class LiteMOLComponent extends VerticalLayout {

    public LiteMOLComponent() {
        LiteMOLComponent.this.setSizeFull();
        Label proteinStructurePanel = new Label("<iframe id=\"litemolframe\" src='litemol' style='width:100%; height:100%;border: none;'></iframe>", ContentMode.HTML);
        proteinStructurePanel.setSizeFull();
        LiteMOLComponent.this.addComponent(proteinStructurePanel);
//         JavaScript.getCurrent().execute("document.getElementById('jsmolframe').contentWindow.document.getElementById('appdiv').style.width = '100%';");

    }

    public void loadProtein(String pdbAccession) {
//         liteMolScope.LiteMolComponent.moleculeId = pdb;
//JavaScript.getCurrent().execute("document.getElementById('litemolframe').contentWindow.angular.element('#litemolid').scope().liteMolScope.LiteMolComponent.moleculeId = '5exw';");

//JavaScript.getCurrent().execute("document.getElementById('litemolframe').contentWindow.angular.element.liteMolScope.LiteMolComponent.hideControls();");
//            JavaScript.getCurrent().execute("document.getElementById('litemolframe').contentWindow.liteMolScope.LiteMolComponent.loadMolecule();");
//            JavaScript.getCurrent().execute("document.getElementById('litemolframe').contentWindow.document.$angular.hideControls();");
//        JavaScript.getCurrent().execute("document.getElementById('litemolframe').contentWindow.loadNewProtein('" + pdbAccession.toLowerCase() + "');");
    
    JavaScript.getCurrent().execute("document.getElementById('litemolframe').contentWindow.document.getElementById('container').style.background = 'red !important';");
    
    
    }

    public void excuteQuery(String query) {
        System.out.println("quesy invoked " + query);
//        JavaScript.getCurrent().execute("document.getElementById('litemolframe').contentWindow.excutequery('" + query + "');");
    }

 
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            this.removeStyleName("hide");
        } else {
            this.addStyleName("hide");
        }
    }

}
