package d3diagrams;

//import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
//import com.ejt.vaadin.sizereporter.SizeReporter;
import com.vaadin.annotations.JavaScript;
import com.vaadin.ui.AbstractJavaScriptComponent;
import elemental.json.JsonArray;
import java.io.Serializable;
import java.util.ArrayList;

@JavaScript({"venn.js", "myD3library.js", "myD3component-connector.js", "d3.v5.min.js"}) //,  "https://cdnjs.cloudflare.com/ajax/libs/jquery.touch/1.1.0/jquery.touch.min.js"
public abstract class VennDiagramComponent extends AbstractJavaScriptComponent {

//    private final SizeReporter report;
    /**
     * The width of the chart.
     */
    private int mainWidth;
    /**
     * The height of the chart.
     */
    private int mainHeight;

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
//        report = new SizeReporter(VennDiagramComponent.this);
//        report.addResizeListener((ComponentResizeEvent event) -> {
////            VennDiagramComponent.this.setSize(event.getWidth(), event.getHeight());
//        });
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
        getState().setValue("serverRequest:data;" + value + ";" + Math.max(300, mainWidth) + "," + Math.max(300, mainHeight));
    }
     public void setValue(String value,int w,int h) {
        getState().setValue("serverRequest:data;" + value + ";" + Math.max(300, w) + "," + Math.max(300, h));
    }

    public String getValue() {
        return getState().getValue();
    }

    public void setSize(int width, int height) {
//        System.out.println("at 1 venn setSize " + mainWidth + "  " + mainHeight+ "  "+width+"  "+height);
//        if (width <= 0 || height <= 0) {
//            return;
//        }
//        if (mainWidth == 0 || mainHeight == 0) {
//            this.mainWidth = width;
//            this.mainHeight = height;
//            return;
//        }
//        this.mainWidth = width;
//        this.mainHeight = height;
//        System.out.println("at 2 venn setSize " + mainWidth + "  " + mainHeight+ "  "+width+"  "+height);
        getState().setValue("serverRequest:sizeonly;" + width + ";" + height);

    }

    @Override
    protected VennDiagramComponentState getState() {
        return (VennDiagramComponentState) super.getState();
    }

}
