package com.uib.web.peptideshaker.presenter.core;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.ProgressBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.io.File;
import pl.exsio.plupload.Plupload;
import pl.exsio.plupload.PluploadError;
import pl.exsio.plupload.PluploadFile;
import pl.exsio.plupload.helper.filter.PluploadFilter;

/**
 * This class represents Uploading class with progress bar
 *
 * @author Yehia Farag
 */
public abstract class Uploader extends AbsoluteLayout {

    private File userUploadFolder;
    private Plupload uploaderComponent;
    private final Label info;
    private final Button uploaderBtn;
    private final ProgressBar bar;

    public Uploader() {
        Uploader.this.setHeight(40, Unit.PIXELS);
        Uploader.this.setWidth(100, Unit.PERCENTAGE);
        Uploader.this.setStyleName("uploaderlayout");

        VerticalLayout uploaderLayout = new VerticalLayout();
        uploaderLayout.setWidth(300, Unit.PIXELS);
        uploaderLayout.setHeight(100, Unit.PERCENTAGE);
        uploaderLayout.setSpacing(false);
        Uploader.this.addComponent(uploaderLayout, "right:140px;top:2px");
        uploaderLayout.addStyleName("smooth");
        uploaderLayout.addStyleName("hidebywidth");

        bar = new ProgressBar(0.0f);
        uploaderLayout.addComponent(bar);
        uploaderLayout.setComponentAlignment(bar, Alignment.TOP_LEFT);
        bar.setWidth(300, Unit.PIXELS);

        info = new Label();
        info.setContentMode(ContentMode.HTML);
        info.setStyleName(ValoTheme.LABEL_TINY);
        info.addStyleName(ValoTheme.LABEL_NO_MARGIN);
        uploaderLayout.addComponent(info);
        uploaderLayout.setComponentAlignment(info, Alignment.TOP_LEFT);
        info.setWidth(300, Unit.PIXELS);

        initUploaderComponent();
        uploaderComponent.addStyleName("hidebywidth");

        uploaderBtn = new Button("Upload", FontAwesome.UPLOAD);
        uploaderBtn.setWidth(88, Unit.PIXELS);
        uploaderBtn.addStyleName(ValoTheme.BUTTON_TINY);
        Uploader.this.addComponent(uploaderBtn, "right:2px;top:2px");
        uploaderBtn.addClickListener((Button.ClickEvent event) -> {
            if (userUploadFolder == null) {
                String userDataFolderUrl = VaadinSession.getCurrent().getAttribute("userDataFolderUrl") + "";
                String APIKey = VaadinSession.getCurrent().getAttribute("ApiKey").toString();
                File user_folder = new File(userDataFolderUrl, APIKey);
                if (!user_folder.exists()) {
                    user_folder.mkdir();
                }
                userUploadFolder = new File(user_folder, "uploadedFiles");
                userUploadFolder.mkdir();
                uploaderComponent.setUploadPath(userUploadFolder.getAbsolutePath());
            }
            if (uploaderBtn.getCaption().equals("Upload")) {
                uploaderLayout.removeStyleName("hidebywidth");
                uploaderComponent.removeStyleName("hidebywidth");
                uploaderBtn.setCaption("");
                uploaderBtn.setIcon(FontAwesome.CLOSE);
                uploaderBtn.setWidth(28, Unit.PIXELS);
                uploaderBtn.addStyleName(ValoTheme.BUTTON_ICON_ONLY);

            } else {
                uploaderLayout.addStyleName("hidebywidth");
                uploaderComponent.addStyleName("hidebywidth");
                uploaderBtn.setCaption("Upload");
                uploaderBtn.setWidth(88, Unit.PIXELS);
                uploaderBtn.setIcon(FontAwesome.UPLOAD);
                uploaderBtn.removeStyleName(ValoTheme.BUTTON_ICON_ONLY);
            }
        });
    }

    private void initUploaderComponent() {
        if (uploaderComponent != null) {
            Uploader.this.removeComponent(uploaderComponent);
        }
        uploaderComponent = new Plupload("Browse", FontAwesome.FILES_O);
        uploaderComponent.setMaxFileSize("1gb");
        uploaderComponent.addStyleName(ValoTheme.BUTTON_TINY);
        uploaderComponent.addStyleName("smooth");
        Uploader.this.addComponent(uploaderComponent, "right:35px;top:2px");
        //show notification after file is uploaded
        uploaderComponent.addFileUploadedListener((PluploadFile file) -> {
            Notification.show("I've just uploaded file: " + file.getName());
        });

//update upload progress
        uploaderComponent.addUploadProgressListener((PluploadFile file) -> {
            info.setValue("File: " + file.getName() + " - " + file.getPercent() + " %");
            float current = bar.getValue();
            if (current < 1.0f) {
                bar.setValue((float) file.getPercent() / 100.0f);
            } else {
                bar.setValue(0.0f);
            }

        });
        uploaderComponent.setPreventDuplicates(true);

//autostart the uploader after addind files
        uploaderComponent.addFilesAddedListener((PluploadFile[] files) -> {
            uploaderComponent.start();
        });

//notify, when the upload process is completed
        uploaderComponent.addUploadCompleteListener(() -> {
            bar.setValue(0.0f);
            info.setValue("upload is done " + FontAwesome.SMILE_O.getHtml());
            filesUploaded(uploaderComponent.getUploadedFiles());
            initUploaderComponent();
            uploaderComponent.removeStyleName("hidebywidth");

        });

//handle errors
        uploaderComponent.addErrorListener((PluploadError error) -> {
            Notification.show("Error in uploading file, only MGF and Fasta file format allowed", Notification.Type.ERROR_MESSAGE);
            info.setValue("Only MGF and Fasta file format allowed " + FontAwesome.FROWN_O.getHtml());
        });
        uploaderComponent.addFilter(new PluploadFilter("mgf", "mgf"));
        uploaderComponent.addFilter(new PluploadFilter("fasta", "fasta"));

    }

    public abstract void filesUploaded(PluploadFile[] uploadedFiles);

}
