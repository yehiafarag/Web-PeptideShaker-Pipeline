package com.uib.web.peptideshaker.presenter.pscomponents;

import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.matches.IonMatch;
import com.compomics.util.experiment.identification.spectrum_annotation.AnnotationSettings;
import com.compomics.util.experiment.identification.spectrum_annotation.SpecificAnnotationSettings;
import com.compomics.util.experiment.identification.spectrum_annotation.spectrum_annotators.PeptideSpectrumAnnotator;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.gui.spectrum.SequenceFragmentationPanel;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.itextpdf.text.pdf.codec.Base64;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import org.jfree.chart.encoders.ImageEncoder;
import org.jfree.chart.encoders.ImageEncoderFactory;
import org.jfree.chart.encoders.ImageFormat;

/**
 *
 * @author Yehia Farag this class represents spectra chart in relation with
 * peptide sequence
 */
public class SequenceFragmentationChart extends VerticalLayout {

    private final Image plotImage;
//    private final Label sequenceLabel;
    private final Object objectId;
    private int imgW = -1;
    private int imgH = -1;
    private String base64;

    public Object getObjectId() {
        return objectId;
    }

    public SequenceFragmentationChart(String sequence, Object objectId, SpectrumInformation spectrumInformation) {
        SequenceFragmentationChart.this.setHeight(70, Unit.PIXELS);
        SequenceFragmentationChart.this.setWidth(100, Unit.PERCENTAGE);
        this.plotImage = new Image();

        SequenceFragmentationChart.this.setStyleName("sequencefragmentationchart");
        SequenceFragmentationChart.this.addComponent(this.plotImage);
        SequenceFragmentationChart.this.setComponentAlignment(this.plotImage, Alignment.TOP_CENTER);
        SequenceFragmentationChart.this.setDescription(sequence);
//        this.sequenceLabel = new Label(sequence, ContentMode.HTML);
//        this.sequenceLabel.setStyleName(ValoTheme.LABEL_SMALL);
//        this.sequenceLabel.setSizeFull();
//        SequenceFragmentationChart.this.addComponent(sequenceLabel);
//        SequenceFragmentationChart.this.reset();
        this.objectId = objectId;

        // create the sequence fragment ion view
        // create the sequence fragment ion view
        double accuracy = spectrumInformation.getFragmentIonAccuracy();
        PeptideAssumption peptideAssumption = spectrumInformation.getSpectrumMatch().getBestPeptideAssumption();
        Peptide currentPeptide = peptideAssumption.getPeptide();
        AnnotationSettings annotationPreferences = spectrumInformation.getIdentificationParameters().getAnnotationPreferences();
        annotationPreferences.setIntensityLimit(0.75);
        annotationPreferences.setFragmentIonAccuracy(accuracy);
        PeptideSpectrumAnnotator spectrumAnnotator = new PeptideSpectrumAnnotator();

        SpecificAnnotationSettings specificAnnotationPreferences = new SpecificAnnotationSettings(spectrumInformation.getSpectrum().getSpectrumKey(), peptideAssumption);

        spectrumInformation.getIdentificationParameters().setAnnotationSettings(annotationPreferences);
        try {
            specificAnnotationPreferences = annotationPreferences.getSpecificAnnotationPreferences(spectrumInformation.getSpectrum().getSpectrumKey(), specificAnnotationPreferences.getSpectrumIdentificationAssumption(), spectrumInformation.getIdentificationParameters().getSequenceMatchingPreferences(), spectrumInformation.getIdentificationParameters().getPtmScoringPreferences().getSequenceMatchingPreferences());
        } catch (IOException | InterruptedException | ClassNotFoundException | SQLException ex) {
            Logger.getLogger(PeptideShakerVisualizationDataset.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<IonMatch> annotations = spectrumAnnotator.getSpectrumAnnotation(annotationPreferences, specificAnnotationPreferences, spectrumInformation.getSpectrum(), currentPeptide);
        Integer forwardIon = spectrumInformation.getIdentificationParameters().getSearchParameters().getForwardIons().get(0);
        Integer rewindIon = spectrumInformation.getIdentificationParameters().getSearchParameters().getRewindIons().get(0);//
        String taggedPeptideSequence = currentPeptide.getTaggedModifiedSequence(spectrumInformation.getIdentificationParameters().getSearchParameters().getPtmSettings(), false, false, false);
        SequenceFragmentationPanel sequenceFragmentationPanel = new SequenceFragmentationPanel(taggedPeptideSequence, annotations, false, spectrumInformation.getIdentificationParameters().getSearchParameters().getPtmSettings(), forwardIon, rewindIon);

//            SequenceFragmentationPanel sequenceFragmentationPanel = new SequenceFragmentationPanel(currentPeptide.getSequence(),//getTaggedPeptideSequence(spectrumMatch, false, false, false),
//                    annotations, true, getIdentificationParameters().getSearchParameters().getPtmSettings(), forwardIon, rewindIon);
        sequenceFragmentationPanel.setOpaque(true);
        sequenceFragmentationPanel.setBackground(Color.WHITE);

        SizeReporter reporter = new SizeReporter(SequenceFragmentationChart.this);
        reporter.addResizeListener((event) -> {

        });
        sequenceFragmentationPanel.setSize(sequence.length() * 15, 68);
        this.plotImage.setWidth(sequenceFragmentationPanel.getWidth(), Unit.PIXELS);
        this.plotImage.setHeight(sequenceFragmentationPanel.getHeight(), Unit.PIXELS);
        plotImage.setSource(new ExternalResource(drawImage(sequenceFragmentationPanel)));

    }

    public void reset() {
//        this.plotImage.setVisible(false);
//        this.sequenceLabel.setVisible(true);

    }
    int count = 0;

    private String drawImage(JPanel panel) {
        panel.revalidate();
        panel.repaint();
        if (panel.getWidth() <= 0) {
            panel.setSize(100, panel.getHeight());
        }
        if (panel.getHeight() <= 0) {
            panel.setSize(panel.getWidth(), 100);
        }
        if (imgH == panel.getHeight() && imgW == panel.getWidth()) {
            return base64;
        }
        imgH = panel.getHeight();
        imgW = panel.getWidth();
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        //draw sequence line
        g2d.setColor(Color.LIGHT_GRAY);
        panel.paint(g2d);
        byte[] imageData = null;
        try {
            ImageEncoder in = ImageEncoderFactory.newInstance(ImageFormat.PNG, 1);
            imageData = in.encode(image);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        base64 = Base64.encodeBytes(imageData);
        base64 = "data:image/png;base64," + base64;
        System.out.println("----------------------------->>>>>>>>>>>>>>" + count++);
        //total chain coverage     
        return base64;
    }

}
