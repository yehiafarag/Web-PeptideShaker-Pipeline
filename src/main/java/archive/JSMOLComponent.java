/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package archive;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/**
 * This class provides an abstraction layer for JSMOL 3D protein structure
 *
 * @author Yehia Farag
 */
public class JSMOLComponent extends VerticalLayout {

    public JSMOLComponent() {
        JSMOLComponent.this.setSizeFull();
        Label proteinStructurePanel = new Label("<iframe id=\"jsmolframe\" src='jsmol' style='width:100%; height:100%'></iframe>", ContentMode.HTML);
        proteinStructurePanel.setSizeFull();
        JSMOLComponent.this.addComponent(proteinStructurePanel);
//         JavaScript.getCurrent().execute("document.getElementById('jsmolframe').contentWindow.document.getElementById('appdiv').style.width = '100%';");

    }

    public void loadProtein(String pdbAccession) {
        JavaScript.getCurrent().execute("document.getElementById('jsmolframe').contentWindow.loadNewProtein('" + pdbAccession + "');");
    }

    public void excuteQuery(String query) {
        System.out.println("quesy invoked "+query);
        JavaScript.getCurrent().execute("document.getElementById('jsmolframe').contentWindow.excutequery('" + query + "');");
    }

    public void resizePanel(int w, int h) {
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
