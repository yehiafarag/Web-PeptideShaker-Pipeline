package archive;

import com.vaadin.ui.Button;
import com.vaadin.ui.JavaScript;
import com.vaadin.ui.VerticalLayout;
import de.akquinet.engineering.vaadin.vaangular.demo.weather.LiteMol;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * This class represents wrapper for LiteMOL 3D visualisation for proteins
 *
 * @author Yehia Farag
 */
public class LiteMOLComponent extends VerticalLayout {

    private LiteMol liteMol3D;

    public LiteMOLComponent() {

        LiteMOLComponent.this.setSizeFull();
        LiteMOLComponent.this.setSpacing(true);
        Button javaSend;
        try {
            liteMol3D = new LiteMol();

            javaSend = new Button();
            javaSend.setCaption("E-Mail (from Java)");
            javaSend.addClickListener(new Button.ClickListener() {

                private static final long serialVersionUID = 1L;

                @Override
                public void buttonClick(Button.ClickEvent event) {        
                    liteMol3D.updateProteins();
                  
                }
            });
            this.addComponent(liteMol3D);
            LiteMOLComponent.this.addComponent(javaSend);
        } catch (IOException | URISyntaxException exp) {
            exp.printStackTrace();
        }
    }
    public void clickBtn(){
        liteMol3D.updateProteins();
    }

}
