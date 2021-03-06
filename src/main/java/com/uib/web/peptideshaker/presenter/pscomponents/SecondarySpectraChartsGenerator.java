package com.uib.web.peptideshaker.presenter.pscomponents;

import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.compomics.util.experiment.identification.matches.IonMatch;
import com.compomics.util.experiment.identification.spectrum_annotation.AnnotationSettings;
import com.compomics.util.experiment.identification.spectrum_annotation.SpecificAnnotationSettings;
import com.compomics.util.experiment.identification.spectrum_annotation.spectrum_annotators.PeptideSpectrumAnnotator;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.gui.spectrum.MassErrorPlot;
import com.compomics.util.gui.spectrum.SequenceFragmentationPanel;
import com.itextpdf.text.pdf.codec.Base64;
import com.uib.web.peptideshaker.galaxy.dataobjects.PeptideShakerVisualizationDataset;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Sizeable.Unit;
import com.vaadin.ui.Image;
import com.vaadin.ui.VerticalLayout;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.apache.commons.math.MathException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.encoders.ImageEncoder;
import org.jfree.chart.encoders.ImageEncoderFactory;
import org.jfree.chart.encoders.ImageFormat;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.DefaultXYItemRenderer;

/**
 *
 * @author Yehia Farag this class represents spectra chart in relation with
 * peptide sequence
 */
public class SecondarySpectraChartsGenerator {

    private final Image sequenceFragmentationChartComponent;
    private final Image massErrorPlotComponent;
//    private final Label sequenceLabel;
    private final Object objectId;
    private int imgW = -1;
    private int imgH = -1;
    private String base64;
    private MassErrorPlot errorPlot;
    private final VerticalLayout sequenceFragmentationChart;
    private final VerticalLayout massErrorPlot;

    public Object getObjectId() {
        return objectId;
    }

    public VerticalLayout getSequenceFragmentationChart() {
        return sequenceFragmentationChart;
    }

    public VerticalLayout getMassErrorPlot() {
        return massErrorPlot;
    }

    public SecondarySpectraChartsGenerator(String sequence,String tooltip, Object objectId, SpectrumInformation spectrumInformation) {
        this.sequenceFragmentationChart = new VerticalLayout();
        this.massErrorPlot = new VerticalLayout();
        this.sequenceFragmentationChart.setHeight(70, Unit.PIXELS);
        this.sequenceFragmentationChart.setWidth(100, Unit.PERCENTAGE);
        this.massErrorPlot.setHeight(70, Unit.PIXELS);
        this.massErrorPlot.setWidth(100, Unit.PERCENTAGE);
        SecondarySpectraChartsGenerator.this.sequenceFragmentationChart.setStyleName("sequencefragmentationchart");
        SecondarySpectraChartsGenerator.this.sequenceFragmentationChart.setData(objectId);
        SecondarySpectraChartsGenerator.this.massErrorPlot.setStyleName("sequencefragmentationchart");
        SecondarySpectraChartsGenerator.this.massErrorPlot.setData(objectId);
        this.sequenceFragmentationChartComponent = new Image();
        this.massErrorPlotComponent=new Image();

        this.sequenceFragmentationChart.addComponent(this.sequenceFragmentationChartComponent);
         this.massErrorPlot.addComponent(this.massErrorPlotComponent);
         
         
         
//        SecondarySpectraChartsGenerator.this.setComponentAlignment(this.plotImage, Alignment.TOP_CENTER);
        sequenceFragmentationChart.setDescription(tooltip);
//        this.sequenceLabel = new Label(sequence, ContentMode.HTML);
//        this.sequenceLabel.setStyleName(ValoTheme.LABEL_SMALL);
//        this.sequenceLabel.setSizeFull();
//        SecondarySpectraChartsGenerator.this.addComponent(sequenceLabel);
//        SecondarySpectraChartsGenerator.this.reset();
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

        ArrayList<IonMatch> annotations;
        try {
            annotations = spectrumAnnotator.getSpectrumAnnotation(annotationPreferences, specificAnnotationPreferences, spectrumInformation.getSpectrum(), currentPeptide);
        } catch (InterruptedException | MathException ex) {
            Logger.getLogger(SecondarySpectraChartsGenerator.class.getName()).log(Level.SEVERE, null, ex);
            return;
        }
        Integer forwardIon = spectrumInformation.getIdentificationParameters().getSearchParameters().getForwardIons().get(0);
        Integer rewindIon = spectrumInformation.getIdentificationParameters().getSearchParameters().getRewindIons().get(0);//
        String taggedPeptideSequence = currentPeptide.getTaggedModifiedSequence(spectrumInformation.getIdentificationParameters().getSearchParameters().getPtmSettings(), false, false, false);
        SequenceFragmentationPanel sequenceFragmentationPanel = new SequenceFragmentationPanel(taggedPeptideSequence, annotations, currentPeptide.isModified(), spectrumInformation.getIdentificationParameters().getSearchParameters().getPtmSettings(), forwardIon, rewindIon);
        sequenceFragmentationPanel.setOpaque(true);
        sequenceFragmentationPanel.setBackground(Color.WHITE);
        sequenceFragmentationPanel.setSize(1000, 68);
        sequenceFragmentationChartComponent.setSource(new ExternalResource(drawImage(sequenceFragmentationPanel)));
        try {
            errorPlot = new MassErrorPlot(annotations, spectrumInformation.getSpectrum(), accuracy, spectrumInformation.getIdentificationParameters().getSearchParameters().getFragmentAccuracyType() == SearchParameters.MassAccuracyType.PPM);
            errorPlot.setSize(270, 68);
            errorPlot.getChartPanel().setSize(270, 68);
            errorPlot.updateUI();
            XYPlot plot = (XYPlot) errorPlot.getChartPanel().getChart().getPlot();
            plot.getDomainAxis().setVisible(false);
            plot.getRangeAxis().setVisible(false);
            DefaultXYItemRenderer renderer = (DefaultXYItemRenderer) plot.getRenderer();
            for (int i = 0; i < plot.getSeriesCount(); i++) {
                renderer.setSeriesShape(i, new Ellipse2D.Double(-2.0, -2.0, 4.0, 4.0));
            }
            plot.setRenderer(renderer);
            massErrorPlotComponent.setSource(new ExternalResource(drawImage(errorPlot.getChartPanel().getChart(), 270, 68)));
        
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
       

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
        panel.setBorder(BorderFactory.createLineBorder(Color.WHITE,2));

        imgH = panel.getHeight();
        imgW = panel.getWidth();
        BufferedImage image = new BufferedImage(panel.getWidth(), panel.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        //draw sequence line
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
        //total chain coverage     
        return base64;
    }

    private String drawImage(JFreeChart chart, int imgW, int imgH) {

        BufferedImage image = chart.createBufferedImage(imgW, imgH);

        byte[] imageData = null;
        try {
            ImageEncoder in = ImageEncoderFactory.newInstance(ImageFormat.PNG, 1);
            imageData = in.encode(image);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
        base64 = Base64.encodeBytes(imageData);
        base64 = "data:image/png;base64," + base64;
        //total chain coverage     
        return base64;
    }

}
