/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package selectioncanvas;

import com.vaadin.shared.ui.JavaScriptComponentState;

/**
 *
 * @author y-mok
 */
public class SelectioncanvasComponentState extends JavaScriptComponentState {
    private String value;
  

   
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
}