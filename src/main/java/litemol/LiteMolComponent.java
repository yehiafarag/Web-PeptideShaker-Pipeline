
package litemol;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.StyleSheet;
import com.vaadin.ui.AbstractJavaScriptComponent;
import elemental.json.JsonArray;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
@StyleSheet({"mylitemolstyle.css","LiteMol-plugin.css"})
@JavaScript({"LiteMol-plugin.js", "mylitemol-connector.js","mylitemollibrary.js", "https://ajax.googleapis.com/ajax/libs/jquery/2.0.3/jquery.min.js", "https://cdnjs.cloudflare.com/ajax/libs/jquery.touch/1.1.0/jquery.touch.min.js"})
public class LiteMolComponent extends AbstractJavaScriptComponent {



    public LiteMolComponent() {
        
//        LiteMolComponent.this.setWidth(500, Unit.PIXELS);
//        LiteMolComponent.this.setHeight(500, Unit.PIXELS);

        LiteMolComponent.this.addFunction("onClick", (JsonArray arguments) -> {
            getState().setValue(arguments.getString(0));
            listeners.forEach((listener) -> {
                listener.valueChange();
            });
        });

        LiteMolComponent.this.addValueChangeListener(() -> {           
            String value = LiteMolComponent.this.getValue();
        });
    }

   

    public interface ValueChangeListener extends Serializable {

        void valueChange();
    }
    ArrayList<ValueChangeListener> listeners
            = new ArrayList<>();

    public void addValueChangeListener(
            ValueChangeListener listener) {
        listeners.add(listener);
    }

    public void setValue(String value) {
//        try {
//            value = URLEncoder.encode(value, "UTF-8");
//        } catch (UnsupportedEncodingException ex) {
//            ex.printStackTrace();
//        }
        getState().setValue(value);
    }

    public String getValue() {
        return getState().getValue();
    }

    public void setSize(int width, int height) {
//        this.setWidth(width, Unit.PIXELS);
//        this.setHeight(height, Unit.PIXELS);
//        this.setValue(width + "," + height);

    }

    @Override
    protected LiteMolComponentState getState() {
        return (LiteMolComponentState) super.getState();
    }

 
    

}