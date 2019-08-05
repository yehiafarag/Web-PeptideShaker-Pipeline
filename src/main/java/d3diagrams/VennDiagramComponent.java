package d3diagrams;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import elemental.json.JsonArray;
import java.io.Serializable;
import java.util.ArrayList;

@JavaScript({"venn.js", "myD3library.js", "myD3component-connector.js", "d3.v5.min.js"}) //,  "https://cdnjs.cloudflare.com/ajax/libs/jquery.touch/1.1.0/jquery.touch.min.js"
public abstract class VennDiagramComponent extends AbstractJavaScriptComponent {

    private final SizeReporter report;

    public VennDiagramComponent() {
        VennDiagramComponent.this.setWidth(100, Unit.PERCENTAGE);
        VennDiagramComponent.this.setHeight(100, Unit.PERCENTAGE);

        VennDiagramComponent.this.addFunction("onClick", (JsonArray arguments) -> {
            getState().setValue(arguments.getString(0));
            listeners.forEach((listener) -> {
                listener.valueChange();
            });
        });

        VennDiagramComponent.this.addValueChangeListener(() -> {
            String value = VennDiagramComponent.this.getValue();
            SelectionPerformed(value);
        });
        report = new SizeReporter(VennDiagramComponent.this);
        report.addResizeListener((ComponentResizeEvent event) -> {
            VennDiagramComponent.this.setSize(event.getWidth(), event.getHeight());
        });
    }

    public abstract void SelectionPerformed(String value);

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
        getState().setValue("serverRequest:data;" + value + ";" + report.getWidth() + "," + report.getHeight());
    }

    public String getValue() {
        return getState().getValue();
    }

    public void setSize(int width, int height) {
        getState().setValue("serverRequest:sizeonly;" + width + ";" + height);

    }

    @Override
    protected VennDiagramComponentState getState() {
        return (VennDiagramComponentState) super.getState();
    }

}
