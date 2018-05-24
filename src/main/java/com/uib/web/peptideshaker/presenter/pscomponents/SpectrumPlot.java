package com.uib.web.peptideshaker.presenter.pscomponents;

import com.compomics.util.experiment.biology.AminoAcid;
import com.compomics.util.experiment.biology.AminoAcidPattern;
import com.compomics.util.experiment.biology.Ion;
import com.compomics.util.experiment.biology.IonFactory;
import com.compomics.util.experiment.biology.NeutralLoss;
import com.compomics.util.experiment.biology.PTM;
import com.compomics.util.experiment.biology.PTMFactory;
import com.compomics.util.experiment.biology.Peptide;
import com.compomics.util.experiment.biology.ions.PeptideFragmentIon;
import com.compomics.util.experiment.identification.Identification;
import com.compomics.util.experiment.identification.identification_parameters.PtmSettings;
import com.compomics.util.experiment.identification.identification_parameters.SearchParameters;
import com.compomics.util.experiment.identification.matches.IonMatch;
import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.identification.spectrum_annotation.AnnotationSettings;
import com.compomics.util.experiment.identification.spectrum_annotation.SpecificAnnotationSettings;
import com.compomics.util.experiment.identification.spectrum_annotation.SpectrumAnnotator;
import com.compomics.util.experiment.identification.spectrum_annotation.spectrum_annotators.PeptideSpectrumAnnotator;
import com.compomics.util.experiment.identification.spectrum_assumptions.PeptideAssumption;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Precursor;
import com.compomics.util.gui.spectrum.SequenceFragmentationPanel;
import com.compomics.util.preferences.IdentificationParameters;
import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.itextpdf.text.pdf.codec.Base64;
import com.uib.web.peptideshaker.presenter.pscomponents.eu.isas.peptideshaker.parameters.PSPtmScores;
import com.vaadin.data.Property;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.slider.SliderOrientation;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Image;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Slider;
import com.vaadin.ui.themes.ValoTheme;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import javax.swing.JPanel;
import org.jfree.chart.encoders.ImageEncoder;
import org.jfree.chart.encoders.ImageEncoderFactory;
import org.jfree.chart.encoders.ImageFormat;
import selectioncanvas.SelectioncanvasComponent;

/**
 * This class represents Spectrum Plot extracted from PeptideShaker and
 * converted using DiVA concept
 *
 * @author Yehia Farag
 */
public class SpectrumPlot extends AbsoluteLayout {

    private final Image plot;
    private final Slider levelSlider;
    private final Slider annotationAccuracySlider;
    private WebSpectrumPanel spectrumPanel;
    private final SizeReporter mainSizeReporter;
//    private final String[] ions = {"a", "b", "c", "x", "y", "z"};
    private final MenuBar.Command annotationsItemsCommand;
    private final MenuItem ionsItem;
    private final MenuItem otherItem;
    private final MenuItem lossItem;
    private final MenuItem chargeItem;
    private final LinkedHashMap<String, Integer> ions;
    private final LinkedHashMap<String, Ion.IonType> others;
    private final MenuItem resetAnnoItem;
    private final MenuItem deNovoItem;
    private final MenuItem settingsItem;

    public SpectrumPlot() {
        SpectrumPlot.this.setStyleName("splotframe");
        SpectrumPlot.this.setSizeFull();
        ions = new LinkedHashMap<>();
        ions.put("a", PeptideFragmentIon.A_ION);
        ions.put("b", PeptideFragmentIon.B_ION);
        ions.put("c", PeptideFragmentIon.C_ION);
        ions.put("x", PeptideFragmentIon.X_ION);
        ions.put("y", PeptideFragmentIon.Y_ION);
        ions.put("z", PeptideFragmentIon.Z_ION);

        others = new LinkedHashMap<>();
        others.put("Precursor", Ion.IonType.PRECURSOR_ION);
        others.put("Immonium", Ion.IonType.IMMONIUM_ION);
        others.put("Related", Ion.IonType.RELATED_ION);
        others.put("Reporter", Ion.IonType.REPORTER_ION);
//                   
//        SpectrumPlot.this.setWidth(1000, Unit.PIXELS);
//        SpectrumPlot.this.setHeight(500, Unit.PIXELS);
//        SpectrumPlot.this.setStyleName(ValoTheme.LAYOUT_WELL);
        plot = new Image() {
            @Override
            public void setSource(Resource source) {
                if (this.getStyleName().contains("imgI")) {
                    this.removeStyleName("imgI");
                    this.addStyleName("imgII");
                    System.out.println("add imge II ");
                } else {
                    System.out.println("add imge I ");
                    this.removeStyleName("imgII");
                    this.addStyleName("imgI");
                }
                super.setSource(source); //To change body of generated methods, choose Tools | Templates.
            }

        };
        SpectrumPlot.this.addComponent(plot, "left:0px;top:0px");
        SelectioncanvasComponent selectionCanvas = new SelectioncanvasComponent() {
            @Override
            public void dragSelectionIsPerformed(double startX, double startY, double endX, double endY) {
                System.out.println("at selection is " + startX + "," + startY + ")(" + endX + "," + endY + ")");
                if (spectrumPanel != null) {
                    spectrumPanel.zoom((int) startX, (int) startY, (int) endX, (int) endY);
                    plot.setSource(new ExternalResource(drawImage(spectrumPanel)));
                }
            }

            @Override
            public void rightSelectionIsPerformed(double startX, double startY) {
                if (spectrumPanel != null) {
                    spectrumPanel.reset();
                    plot.setSource(new ExternalResource(drawImage(spectrumPanel)));
                }
            }

            @Override
            public void leftSelectionIsPerformed(double startX, double startY) {
                System.out.println("mouse event is ready -- " + startX + " -- " + startY);
                if (spectrumPanel != null) {

//                    MouseEvent e = new MouseEvent(spectrumPanel, 0, 0, -1, (int) startX, (int) startY, 1, false, MouseEvent.BUTTON1);
//                    spectrumPanel.mouseClickedAction(e);
//                    plot.setSource(new ExternalResource(drawImage(spectrumPanel)));
                }
            }

        };
        selectionCanvas.setSize(1000, 500);

        SpectrumPlot.this.addComponent(selectionCanvas, "left:0px;top:0px");

        levelSlider = new Slider();
        levelSlider.setMax(100);
        levelSlider.setMin(0);
        levelSlider.setStyleName("borderslider");
        levelSlider.setHeight(100, Unit.PIXELS);
        levelSlider.setCaptionAsHtml(true);
        levelSlider.setCaption("<center>Level<br/>");
        levelSlider.setOrientation(SliderOrientation.VERTICAL);
        SpectrumPlot.this.addComponent(levelSlider, "right:10px;top:220px");

        annotationAccuracySlider = new Slider();
        annotationAccuracySlider.setMax(100);
        annotationAccuracySlider.setMin(0);
        annotationAccuracySlider.setStyleName("borderslider");
        annotationAccuracySlider.setHeight(100, Unit.PIXELS);
        annotationAccuracySlider.setCaptionAsHtml(true);
        annotationAccuracySlider.setCaption("<center>0.02 Da</center>");
        annotationAccuracySlider.setDescription("Annotation accuracy : 0.02 Da");
//        annotationAccuracySlider.setResolution(3);
        annotationAccuracySlider.setOrientation(SliderOrientation.VERTICAL);
        SpectrumPlot.this.addComponent(annotationAccuracySlider, "right:10px;top:100px");

        HorizontalLayout controlsLayout = new HorizontalLayout();
        controlsLayout.setWidth(100, Unit.PERCENTAGE);
        controlsLayout.setHeight(40, Unit.PIXELS);
        SpectrumPlot.this.addComponent(controlsLayout, "left:0px;bottom:50px");
        MenuBar menue = new MenuBar();
        menue.setStyleName(ValoTheme.MENUBAR_BORDERLESS);
        menue.addStyleName(ValoTheme.MENUBAR_SMALL);
        menue.setSizeFull();
        menue.setAutoOpen(false);
        controlsLayout.addComponent(menue);

        //initialise ions tab
        ionsItem = menue.addItem("Ions", null, null);
        annotationsItemsCommand = new MenuBar.Command() {
            @Override
            public void menuSelected(MenuItem selectedItem) {
                defaultAnnotationInUse = false;
                if (selectedItem.getText().equalsIgnoreCase("Adapt")) {
                    MenuItem H2OItem = lossItem.getChildren().get(0);
                    H2OItem.setEnabled(!selectedItem.isChecked());
                    MenuItem NH3Item = lossItem.getChildren().get(1);
                    NH3Item.setEnabled(!selectedItem.isChecked());
                }
                updateAnnotationPreferences();
                resetAnnoItem.setVisible(true);
            }
        };
        initIonsItem(ionsItem, annotationsItemsCommand);
        otherItem = menue.addItem("Other", null, null);
        initOtherItem(otherItem, annotationsItemsCommand);

        lossItem = menue.addItem("Loss", null, null);
        MenuItem H2OItem = lossItem.addItem("H2O", annotationsItemsCommand);
        H2OItem.setEnabled(false);
        H2OItem.setCheckable(true);
        H2OItem.setChecked(true);
        MenuItem NH3Item = lossItem.addItem("NH3", annotationsItemsCommand);
        NH3Item.setEnabled(false);
        NH3Item.setCheckable(true);
        NH3Item.setChecked(true);
        MenuItem adaptItem = lossItem.addItem("Adapt", annotationsItemsCommand);
        adaptItem.setCheckable(true);
        adaptItem.setChecked(true);
        lossItem.addSeparatorBefore(adaptItem);
        chargeItem = menue.addItem("Charge", null, null);
        deNovoItem = menue.addItem("De Novo", null, null);
        MenuBar.Command deNovoItemItemsCommand = (MenuItem selectedItem) -> {
            if (selectedItem.getText().contains("Single Charge")) {
                deNovoItem.getChildren().get(4).setChecked(false);
            } else if (selectedItem.getText().contains("Double Charge")) {
                deNovoItem.getChildren().get(3).setChecked(false);
            }
            updateAnnotationPreferences();
        };
        MenuItem bions = deNovoItem.addItem("b-ions", deNovoItemItemsCommand);
        bions.setCheckable(true);
        bions.setChecked(false);
        MenuItem yions = deNovoItem.addItem("y-ions", deNovoItemItemsCommand);
        yions.setCheckable(true);
        yions.setChecked(false);
        deNovoItem.addSeparator();

        MenuItem singleChargeItem = deNovoItem.addItem("Single Charge", deNovoItemItemsCommand);
        singleChargeItem.setCheckable(true);
        singleChargeItem.setChecked(true);
        MenuItem doubleChargeItem = deNovoItem.addItem("Double Charge", deNovoItemItemsCommand);
        doubleChargeItem.setCheckable(true);
        doubleChargeItem.setChecked(false);

        settingsItem = menue.addItem("Settings", null, null);
        MenuItem showAllPeaksItem = settingsItem.addItem("Show All Peaks", deNovoItemItemsCommand);
        showAllPeaksItem.setCheckable(true);
        showAllPeaksItem.setChecked(false);
        MenuItem highResolutionItem = settingsItem.addItem("High Resolution", deNovoItemItemsCommand);
        highResolutionItem.setCheckable(true);
        highResolutionItem.setChecked(true);
        settingsItem.addSeparator();
        MenuItem automaticAnnotationItem = settingsItem.addItem("Automatic Annotation", deNovoItemItemsCommand);
        automaticAnnotationItem.setCheckable(true);
        automaticAnnotationItem.setChecked(true);

        MenuItem exportItem = menue.addItem("Export", null, null);
        MenuItem helpItem = menue.addItem("Help", null, null);

        mainSizeReporter = new SizeReporter(SpectrumPlot.this);
        mainSizeReporter.addResizeListener((ComponentResizeEvent event) -> {
            int w = event.getWidth() - 52;
            int h = event.getHeight() - 92;
            if (w <= 0 || h <= 0) {
                return;
            }
            selectionCanvas.setSize(w, h);
            if (spectrumPanel != null) {
                spectrumPanel.setSize(w, h);
                plot.setWidth(w, Unit.PIXELS);
                plot.setHeight(h, Unit.PIXELS);
                plot.setSource(new ExternalResource(drawImage(spectrumPanel)));
            }
        });
        MenuBar.Command showPeakDetailsCommand = (MenuItem selectedItem) -> {
            spectrumPanel.showPeakDetails((selectedItem.getText().equals("Show Peak Details")));
            if (selectedItem.getText().equals("Show Peak Details")) {
                selectedItem.setText("Hide Peak Details");
            } else {
                selectedItem.setText("Show Peak Details");
            }
            plot.setSource(new ExternalResource(drawImage(spectrumPanel)));
        };
        menue.addItem("Show Peack Details", null, showPeakDetailsCommand);
        resetAnnoItem = menue.addItem("Reset Annotations", null, (MenuItem selectedItem) -> {
            selectedItem.setVisible(false);
            resetAnnotations();
            updateAnnotationPreferences();
        });
        resetAnnoItem.setVisible(false);

    }

    private void resetAnnotations() {
        ionsItem.getChildren().forEach((mi) -> {
            mi.setChecked(false);
        });
        identificationParameters.getSearchParameters().getForwardIons().forEach((i) -> {
            ionsItem.getChildren().get(i).setChecked(true);
        });
        identificationParameters.getSearchParameters().getRewindIons().forEach((i) -> {
            ionsItem.getChildren().get(i + 1).setChecked(true);
        });
        otherItem.getChildren().forEach((mi) -> {
            mi.setChecked(true);
        });
        MenuItem H2OItem = lossItem.getChildren().get(0);
        H2OItem.setChecked(true);
        H2OItem.setEnabled(false);
        MenuItem NH3Item = lossItem.getChildren().get(1);
        NH3Item.setChecked(true);
        NH3Item.setEnabled(false);

        MenuItem adaptItem = lossItem.getChildren().get(3);
        adaptItem.setChecked(true);
        adaptItem.setEnabled(true);

        chargeItem.getChildren().forEach((mi) -> {
            mi.setChecked(true);
        });

    }

    /**
     * Save the current annotation preferences selected in the annotation menus
     * in the specific annotation preferences.
     */
    public void updateAnnotationPreferences() {
        try {
            levelSlider.setCaption("<center>" + ((int) ((double) levelSlider.getValue())) + " %<center>");
            levelSlider.setDescription("Level : " + ((int) ((double) levelSlider.getValue())) + " %");
            double accuracy = (annotationAccuracySlider.getValue() / 100.0) * fragmentIonAccuracy;
            annotationAccuracySlider.setCaption("<center>" + String.format("%.2f", accuracy) + " Da</center>");
            annotationAccuracySlider.setDescription("Annotation accuracy : " + accuracy + " Da");
            PeptideAssumption peptideAssumption = spectrumMatch.getBestPeptideAssumption();
            currentPeptide = peptideAssumption.getPeptide();
            AnnotationSettings annotationPreferences = getIdentificationParameters().getAnnotationPreferences();
            annotationPreferences.setIntensityLimit(levelSlider.getValue() / 100.0);
            annotationPreferences.setFragmentIonAccuracy(accuracy);

            specificAnnotationPreferences = new SpecificAnnotationSettings(currentSpectrum.getSpectrumKey(), peptideAssumption);
            try {
                identificationParameters.setAnnotationSettings(annotationPreferences);
                specificAnnotationPreferences = annotationPreferences.getSpecificAnnotationPreferences(currentSpectrum.getSpectrumKey(), specificAnnotationPreferences.getSpectrumIdentificationAssumption(), identificationParameters.getSequenceMatchingPreferences(), identificationParameters.getPtmScoringPreferences().getSequenceMatchingPreferences());
                if (!defaultAnnotationInUse) {
                    specificAnnotationPreferences.getIonTypes().get(Ion.IonType.PEPTIDE_FRAGMENT_ION).clear();
                    specificAnnotationPreferences.getIonTypes().get(Ion.IonType.TAG_FRAGMENT_ION).clear();
                    specificAnnotationPreferences.clearIonTypes();
                    ionsItem.getChildren().forEach((mi) -> {
                        if (mi.isChecked()) {
                            specificAnnotationPreferences.addIonType(Ion.IonType.PEPTIDE_FRAGMENT_ION, ions.get(mi.getText()));
                            specificAnnotationPreferences.addIonType(Ion.IonType.TAG_FRAGMENT_ION, ions.get(mi.getText()));
                        }
                    });
                    otherItem.getChildren().forEach((mi) -> {
                        if (mi.isChecked()) {
                            if (mi.getText().equalsIgnoreCase("Reporter")) {
                                ArrayList<Integer> reporterIons = new ArrayList<>(IonFactory.getReporterIons(getIdentificationParameters().getSearchParameters().getPtmSettings()));
                                reporterIons.forEach((subtype) -> {
                                    specificAnnotationPreferences.addIonType(Ion.IonType.REPORTER_ION, subtype);
                                });
                            } else {
                                specificAnnotationPreferences.addIonType(others.get(mi.getText()));
                            }
                        }
                    });

                    MenuItem adaptItem = lossItem.getChildren().get(3);
                    if (!adaptItem.isChecked()) {
                        specificAnnotationPreferences.setNeutralLossesAuto(false);
                        specificAnnotationPreferences.clearNeutralLosses();
                        lossItem.getChildren().forEach((mi) -> {
                            if (mi.isChecked()) {
                                if (mi.getText().equalsIgnoreCase("NH3")) {
                                    specificAnnotationPreferences.addNeutralLoss(NeutralLoss.NH3);
                                } else if (mi.getText().equalsIgnoreCase("H2O")) {
                                    specificAnnotationPreferences.addNeutralLoss(NeutralLoss.H2O);
                                }
                            }
                        });

                    }

                    specificAnnotationPreferences.clearCharges();
                    chargeItem.getChildren().stream().filter((charge) -> (charge.isChecked())).forEachOrdered((charge) -> {
                        specificAnnotationPreferences.addSelectedCharge(Integer.parseInt(charge.getText().replace("+", "").trim()));
                    });
                }

// The following preferences are kept for all spectra
                annotationPreferences.setShowForwardIonDeNovoTags(deNovoItem.getChildren().get(0).isChecked());
                annotationPreferences.setShowRewindIonDeNovoTags(deNovoItem.getChildren().get(1).isChecked());
                if (deNovoItem.getChildren().get(3).isChecked()) {
                    annotationPreferences.setDeNovoCharge(1);
                } else {
                    annotationPreferences.setDeNovoCharge(2);
                }

                SpectrumAnnotator.TiesResolution tiesResolution = settingsItem.getChildren().get(1).isChecked() ? SpectrumAnnotator.TiesResolution.mostAccurateMz : SpectrumAnnotator.TiesResolution.mostIntense;
                annotationPreferences.setTiesResolution(tiesResolution); //@TODO: replace by a drop down menu
                annotationPreferences.setShowAllPeaks(settingsItem.getChildren().get(0).isChecked());//@TODO:implement control btns
                annotationPreferences.setAutomaticAnnotation(settingsItem.getChildren().get(3).isChecked());
                System.out.println("at set show all " + annotationPreferences.showForwardIonDeNovoTags() + ","
                        + annotationPreferences.showRewindIonDeNovoTags() + "   --  " + deNovoItem.getChildren().get(0).isChecked() + "   " + deNovoItem.getChildren().get(1).isChecked() + "  ");
            } catch (IOException | ClassNotFoundException | InterruptedException | SQLException e) {
                e.printStackTrace();
            }

//                    if (searchParameters.getForwardIons().contains(PeptideFragmentIon.A_ION)) {
//            forwardIonsDeNovoCheckBoxMenuItem.setText("a-ions");
//        }
//        if (searchParameters.getForwardIons().contains(PeptideFragmentIon.B_ION)) {
//            forwardIonsDeNovoCheckBoxMenuItem.setText("b-ions");
//        }
//        if (searchParameters.getForwardIons().contains(PeptideFragmentIon.C_ION)) {
//            forwardIonsDeNovoCheckBoxMenuItem.setText("c-ions");
//        }
//
//        forwardIonsDeNovoCheckBoxMenuItem.repaint();
//
//        if (searchParameters.getRewindIons().contains(PeptideFragmentIon.X_ION)) {
//            rewindIonsDeNovoCheckBoxMenuItem.setText("x-ions");
//        }
//        if (searchParameters.getRewindIons().contains(PeptideFragmentIon.Y_ION)) {
//            rewindIonsDeNovoCheckBoxMenuItem.setText("y-ions");
//        }
//        if (searchParameters.getRewindIons().contains(PeptideFragmentIon.Z_ION)) {
//            rewindIonsDeNovoCheckBoxMenuItem.setText("z-ions");
//        }
//
//       
            spectrumPanel.removeAllReferenceAreasXAxis();
            spectrumPanel.removeAllReferenceAreasYAxis();
            spectrumPanel.setDeltaMassWindow(accuracy);
            ArrayList<IonMatch> annotations = spectrumAnnotator.getSpectrumAnnotation(annotationPreferences, specificAnnotationPreferences, currentSpectrum, currentPeptide);
            spectrumPanel.setAnnotations(SpectrumAnnotator.getSpectrumAnnotation(annotations));
            spectrumPanel.showAnnotatedPeaksOnly(!annotationPreferences.showAllPeaks());
            spectrumPanel.setYAxisZoomExcludesBackgroundPeaks(annotationPreferences.yAxisZoomExcludesBackgroundPeaks());//
            Integer forwardIon = identificationParameters.getSearchParameters().getForwardIons().get(0);
            Integer rewindIon = identificationParameters.getSearchParameters().getRewindIons().get(0);//

            spectrumPanel.addAutomaticDeNovoSequencing(currentPeptide, annotations,
                    forwardIon, rewindIon, annotationPreferences.getDeNovoCharge(),
                    annotationPreferences.showForwardIonDeNovoTags(),
                    annotationPreferences.showRewindIonDeNovoTags(), false);
//                    SpectrumPanel.setKnownMassDeltas(getCurrentMassDeltas(identificationParameters));
            spectrumPanel.setAnnotateHighestPeak(annotationPreferences.getTiesResolution() == SpectrumAnnotator.TiesResolution.mostIntense); //@TODO: implement ties resolution in the spectrum panel
            spectrumPanel.setAnnotations(SpectrumAnnotator.getSpectrumAnnotation(annotations), annotationPreferences.getTiesResolution() == SpectrumAnnotator.TiesResolution.mostIntense); //@TODO: the selection of the peak to annotate should be done outside the spectrum panel
            spectrumPanel.updateUI();
            plot.setSource(new ExternalResource(drawImage(spectrumPanel)));

            // create the sequence fragment ion view
           String taggedPeptideSequence =  currentPeptide.getTaggedModifiedSequence(getIdentificationParameters().getSearchParameters().getPtmSettings(),false, false, false);
             SequenceFragmentationPanel sequenceFragmentationPanel = new SequenceFragmentationPanel(taggedPeptideSequence, annotations, false, getIdentificationParameters().getSearchParameters().getPtmSettings(), forwardIon, rewindIon);
            
            
            
            
            
        } catch (Exception e) {
            e.printStackTrace();
//            catchException(e);
        }
    }

    /**
     * Updates the annotations in the selected tab.
     */
//    public void selectDefaultAnnotations() {
//
////        AnnotationSettings annotationPreferences = getIdentificationParameters().getAnnotationPreferences();
////        annotationPreferences.setIntensityLimit(levelSlider.getValue() / 100.0);
////
////        levelSlider.setCaption("<center>" + ((int) ((double) levelSlider.getValue())) + " %<center>");
////        levelSlider.setDescription("Level : " + ((int) ((double) levelSlider.getValue())) + " %");
////
////        double accuracy = (annotationAccuracySlider.getValue() / 100.0) * fragmentIonAccuracy;
////        annotationPreferences.setFragmentIonAccuracy(accuracy);
////        defaultAnnotationInUse = true;
////        annotationAccuracySlider.setCaption("<center>" + String.format("%.2f", accuracy) + " Da</center>");
////        annotationAccuracySlider.setDescription("Annotation accuracy : " + accuracy + " Da");
////        spectrumPanel.setDeltaMassWindow(accuracy);
////        specificAnnotationPreferences = new SpecificAnnotationSettings(currentSpectrum.getSpectrumKey(), peptideAssumption);
////        try {
////            identificationParameters.setAnnotationSettings(annotationPreferences);
////            specificAnnotationPreferences = annotationPreferences.getSpecificAnnotationPreferences(currentSpectrum.getSpectrumKey(),
////                    specificAnnotationPreferences.getSpectrumIdentificationAssumption(), identificationParameters.getSequenceMatchingPreferences(),
////                    identificationParameters.getPtmScoringPreferences().getSequenceMatchingPreferences());
//
////                            selectDefaultAnnotationMenuItem();
//// The following preferences are kept for all spectra
////            annotationPreferences.setTiesResolution(SpectrumAnnotator.TiesResolution.mostAccurateMz); //@TODO: replace by a drop down menu
////            annotationPreferences.setShowAllPeaks(false);//@TODO:implement control btns
////            annotationPreferences.setShowForwardIonDeNovoTags(false);
////            annotationPreferences.setShowRewindIonDeNovoTags(false);
////            annotationPreferences.setDeNovoCharge(1);
////            annotationPreferences.setAutomaticAnnotation(true);
//        } catch (IOException | ClassNotFoundException | InterruptedException | SQLException e) {
//            e.printStackTrace();
//        }
//
//        ArrayList<IonMatch> annotations = spectrumAnnotator.getSpectrumAnnotation(annotationPreferences, specificAnnotationPreferences, currentSpectrum, currentPeptide);
//        spectrumPanel.setAnnotations(SpectrumAnnotator.getSpectrumAnnotation(annotations));
//        spectrumPanel.showAnnotatedPeaksOnly(true);
//        spectrumPanel.setYAxisZoomExcludesBackgroundPeaks(true);//
//        Integer forwardIon = identificationParameters.getSearchParameters().getForwardIons().get(0);
//        Integer rewindIon = identificationParameters.getSearchParameters().getRewindIons().get(0);//
//        spectrumPanel.addAutomaticDeNovoSequencing(currentPeptide, annotations,
//                forwardIon, rewindIon, annotationPreferences.getDeNovoCharge(),
//                annotationPreferences.showForwardIonDeNovoTags(),
//                annotationPreferences.showRewindIonDeNovoTags(), false);
////                    SpectrumPanel.setKnownMassDeltas(getCurrentMassDeltas(identificationParameters));
//        spectrumPanel.setAnnotateHighestPeak(annotationPreferences.getTiesResolution() == SpectrumAnnotator.TiesResolution.mostIntense); //@TODO: implement ties resolution in the spectrum panel
//        spectrumPanel.setAnnotations(SpectrumAnnotator.getSpectrumAnnotation(annotations), annotationPreferences.getTiesResolution() == SpectrumAnnotator.TiesResolution.mostIntense); //@TODO: the selection of the peak to annotate should be done outside the spectrum panel
//        spectrumPanel.updateUI();
//        plot.setSource(new ExternalResource(drawImage(spectrumPanel)));
//    }
//
//        IdentificationParameters identificationParameters = getIdentificationParameters();
//        AnnotationSettings annotationPreferences = identificationParameters.getAnnotationPreferences();
//        SearchParameters searchParameters = identificationParameters.getSearchParameters();
//
//        int selectedTabIndex = allTabsJTabbedPane.getSelectedIndex();
//        IdentificationParameters identificationParameters = getIdentificationParameters();
//        AnnotationSettings annotationPreferences = identificationParameters.getAnnotationPreferences();
//        SearchParameters searchParameters = identificationParameters.getSearchParameters();
//
//        if (selectedTabIndex == OVER_VIEW_TAB_INDEX) {
//            overviewPanel.setIntensitySliderValue((int) (annotationPreferences.getAnnotationIntensityLimit() * 100));
//            overviewPanel.setAccuracySliderValue((int) ((annotationPreferences.getFragmentIonAccuracy() / searchParameters.getFragmentIonAccuracy()) * 100));
//            overviewPanel.updateSpectrum();
//        } else if (selectedTabIndex == SPECTRUM_ID_TAB_INDEX) {
//            spectrumIdentificationPanel.setIntensitySliderValue((int) (annotationPreferences.getAnnotationIntensityLimit() * 100));
//            spectrumIdentificationPanel.setAccuracySliderValue((int) ((annotationPreferences.getFragmentIonAccuracy() / searchParameters.getFragmentIonAccuracy()) * 100));
//            spectrumIdentificationPanel.updateSpectrum();
//        } else if (selectedTabIndex == MODIFICATIONS_TAB_INDEX) {
//            ptmPanel.setIntensitySliderValue((int) (annotationPreferences.getAnnotationIntensityLimit() * 100));
//            ptmPanel.setAccuracySliderValue((int) ((annotationPreferences.getFragmentIonAccuracy() / searchParameters.getFragmentIonAccuracy()) * 100));
//            ptmPanel.updateGraphics(null);
//        }
    private void initIonsItem(MenuItem parent, MenuBar.Command mainCommand) {
        ions.keySet().stream().map((str) -> parent.addItem(str, null, mainCommand)).forEachOrdered((ion) -> {
            ion.setCheckable(true);
        });
        parent.addSeparatorBefore(parent.getChildren().get(3));
    }

    private void initOtherItem(MenuItem parent, MenuBar.Command mainCommand) {
        others.keySet().stream().map((str) -> parent.addItem(str, null, mainCommand)).forEachOrdered((subItem) -> {
            subItem.setCheckable(true);
        });
    }
    private IdentificationParameters identificationParameters;
    private SpecificAnnotationSettings specificAnnotationPreferences;
    private boolean defaultAnnotationInUse;
    private MSnSpectrum currentSpectrum;
    private Peptide currentPeptide;
    private double fragmentIonAccuracy;
    private SpectrumMatch spectrumMatch;

    public void selectedSpectrum(MSnSpectrum currentSpectrum, String charge, double fragmentIonAccuracy, IdentificationParameters identificationParameters, SpectrumMatch spectrumMatch) {
        this.identificationParameters = identificationParameters;
        this.currentSpectrum = currentSpectrum;
        this.fragmentIonAccuracy = fragmentIonAccuracy;
        this.spectrumMatch = spectrumMatch;
        Thread t;
        t = new Thread(() -> {
            Precursor precursor = currentSpectrum.getPrecursor();
            try {
                spectrumPanel = new WebSpectrumPanel(currentSpectrum.getMzValuesAsArray(), currentSpectrum.getIntensityValuesAsArray(), precursor.getMz(), charge, "", 40, false, false, false, 2, false);
                spectrumPanel.setBorder(null);
                int w = mainSizeReporter.getWidth() - 52;
                int h = mainSizeReporter.getHeight() - 92;
                if (w <= 0 || h <= 0) {
                    return;
                }

                spectrumPanel.setSize(w, h);
                plot.setWidth(w, Unit.PIXELS);
                plot.setHeight(h, Unit.PIXELS);
                spectrumPanel.setDataPointAndLineColor(Color.RED, 0);
                spectrumPanel.setSpectrumPeakColor(Color.RED);
                spectrumPanel.setSpectrumProfileModeLineColor(Color.RED);
                spectrumPanel.rescale(0.0, spectrumPanel.getMaxXAxisValue());
                this.defaultAnnotationInUse = true;
                chargeItem.removeChildren();
                int precursorCharge = 1;
                int currentCharge = spectrumMatch.getBestPeptideAssumption().getIdentificationCharge().value;

                if (currentCharge > precursorCharge) {
                    precursorCharge = currentCharge;
                }
                if (precursorCharge == 1) {
                    precursorCharge = 2;
                }
                for (Integer tcharge = 1; tcharge < precursorCharge; tcharge++) {
                    MenuItem item = chargeItem.addItem(tcharge + "+", annotationsItemsCommand);
                    item.setCheckable(true);
                    item.setChecked(true);
                }
                resetAnnotations();

                //loss @todo
//                HashMap<String, NeutralLoss> neutralLosses = new HashMap<String, NeutralLoss>();
                // add the general neutral losses
//        for (NeutralLoss neutralLoss : IonFactory.getInstance().getDefaultNeutralLosses()) {
//            neutralLosses.put(neutralLoss.name, neutralLoss);
//        }
                // add the sequence specific neutral losses
//                for (ModificationMatch modMatch : modificationMatches) {
//                    PTM ptm = ptmFactory.getPTM(modMatch.getTheoreticPtm());
//                    for (NeutralLoss neutralLoss : ptm.getNeutralLosses()) {
//                        if (!neutralLosses.containsKey(neutralLoss.name)) {
//                            neutralLosses.put(neutralLoss.name, neutralLoss);
//                        }
//                    }
//                }
//
//                ArrayList<String> names = new ArrayList<String>(neutralLosses.keySet());
//                Collections.sort(names);
//
//                if (neutralLosses.isEmpty()) {
//                    lossMenu.setVisible(false);
//                    lossSplitter.setVisible(false);
//                } else {
//
//                    for (int i = 0; i < names.size(); i++) {
//
//                        String neutralLossName = names.get(i);
//                        NeutralLoss neutralLoss = neutralLosses.get(neutralLossName);
//
//                        boolean selected = false;
//                        for (String specificNeutralLossName : specificAnnotationPreferences.getNeutralLossesMap().getAccountedNeutralLosses()) {
//                            NeutralLoss specificNeutralLoss = NeutralLoss.getNeutralLoss(specificNeutralLossName);
//                            if (neutralLoss.isSameAs(specificNeutralLoss)) {
//                                selected = true;
//                                break;
//                            }
//                        }
                // get the spectrum annotations
                Property.ValueChangeListener listener = (Property.ValueChangeEvent event) -> {
                    updateAnnotationPreferences();
                };
                annotationAccuracySlider.setValue(100.0);
                levelSlider.addValueChangeListener(listener);
                annotationAccuracySlider.addValueChangeListener(listener);
                levelSlider.setValue(50.0);

            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        });
        t.start();
        try {
            t.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
    }

    public IdentificationParameters getIdentificationParameters() {
        return identificationParameters;
    }

    private String drawImage(JPanel panel) {
        panel.revalidate();
        panel.repaint();
        if (panel.getWidth() <= 0) {
            panel.setSize(100, panel.getHeight());
        }
        if (panel.getHeight() <= 0) {
            panel.setSize(panel.getWidth(), 100);
        }
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
        String base64 = Base64.encodeBytes(imageData);
        base64 = "data:image/png;base64," + base64;
        //total chain coverage     
        return base64;
    }

    /**
     * Returns the reference identifying the identification under process.
     *
     * @return a String identifying the identification under process
     */
    public String getIdentificationReference() {
        String expRef = "experiment.getReference()";//experment id    'Galaxy_Experiment_2018032217121521735147' -sample 'Sample_2018032217121521735147'
        String sampleRef = "sample.getReference()";//sample id
        int replicateNumber = 1; //replicats number (1 default until )
        return Identification.getDefaultReference(expRef, sampleRef, replicateNumber);
    }

    /**
     * Get the current delta masses for use when annotating the spectra.
     *
     * @param identificationParameters
     * @return the current delta masses
     */
    public HashMap<Double, String> getCurrentMassDeltas(IdentificationParameters identificationParameters) {

        HashMap<Double, String> knownMassDeltas = new HashMap<>();

        // add the monoisotopic amino acids masses
        knownMassDeltas.put(AminoAcid.A.getMonoisotopicMass(), "A");
        knownMassDeltas.put(AminoAcid.R.getMonoisotopicMass(), "R");
        knownMassDeltas.put(AminoAcid.N.getMonoisotopicMass(), "N");
        knownMassDeltas.put(AminoAcid.D.getMonoisotopicMass(), "D");
        knownMassDeltas.put(AminoAcid.C.getMonoisotopicMass(), "C");
        knownMassDeltas.put(AminoAcid.Q.getMonoisotopicMass(), "Q");
        knownMassDeltas.put(AminoAcid.E.getMonoisotopicMass(), "E");
        knownMassDeltas.put(AminoAcid.G.getMonoisotopicMass(), "G");
        knownMassDeltas.put(AminoAcid.H.getMonoisotopicMass(), "H");
        knownMassDeltas.put(AminoAcid.I.getMonoisotopicMass(), "I/L");
        knownMassDeltas.put(AminoAcid.K.getMonoisotopicMass(), "K");
        knownMassDeltas.put(AminoAcid.M.getMonoisotopicMass(), "M");
        knownMassDeltas.put(AminoAcid.F.getMonoisotopicMass(), "F");
        knownMassDeltas.put(AminoAcid.P.getMonoisotopicMass(), "P");
        knownMassDeltas.put(AminoAcid.S.getMonoisotopicMass(), "S");
        knownMassDeltas.put(AminoAcid.T.getMonoisotopicMass(), "T");
        knownMassDeltas.put(AminoAcid.W.getMonoisotopicMass(), "W");
        knownMassDeltas.put(AminoAcid.Y.getMonoisotopicMass(), "Y");
        knownMassDeltas.put(AminoAcid.V.getMonoisotopicMass(), "V");
        knownMassDeltas.put(AminoAcid.U.getMonoisotopicMass(), "U");
        knownMassDeltas.put(AminoAcid.O.getMonoisotopicMass(), "O");

        // add default neutral losses
//        knownMassDeltas.put(NeutralLoss.H2O.mass, "H2O");
//        knownMassDeltas.put(NeutralLoss.NH3.mass, "NH3");
//        knownMassDeltas.put(NeutralLoss.CH4OS.mass, "CH4OS");
//        knownMassDeltas.put(NeutralLoss.H3PO4.mass, "H3PO4");
//        knownMassDeltas.put(NeutralLoss.HPO3.mass, "HPO3");
//        knownMassDeltas.put(4d, "18O"); // @TODO: should this be added to neutral losses??
//        knownMassDeltas.put(44d, "PEG"); // @TODO: should this be added to neutral losses??
        // add the modifications
        SearchParameters searchParameters = identificationParameters.getSearchParameters();
        PtmSettings modificationProfile = searchParameters.getPtmSettings();
        ArrayList<String> modificationList = modificationProfile.getAllModifications();
        Collections.sort(modificationList);

        // iterate the modifications list and add the non-terminal modifications
        for (String modification : modificationList) {
            PTM ptm = PTMFactory.getInstance().getPTM(modification);

            if (ptm != null) {

                String shortName = ptm.getShortName();
                double mass = ptm.getMass();

                if (ptm.getType() == PTM.MODAA) {
                    AminoAcidPattern ptmPattern = ptm.getPattern();
                    for (Character aa : ptmPattern.getAminoAcidsAtTarget()) {
                        if (!knownMassDeltas.containsValue(aa + "<" + shortName + ">")) {
                            AminoAcid aminoAcid = AminoAcid.getAminoAcid(aa);
                            knownMassDeltas.put(mass + aminoAcid.getMonoisotopicMass(),
                                    aa + "<" + shortName + ">");
                        }
                    }
                }
            } else {
                System.out.println("Error: PTM not found: " + modification);
            }
        }

        return knownMassDeltas;
    }

    /**
     * The spectrum annotator.
     */
    private final PeptideSpectrumAnnotator spectrumAnnotator = new PeptideSpectrumAnnotator();
//
//    /**
//     * Returns the peptide with modification sites tagged (color coded or with
//     * PTM tags, e.g, &lt;mox&gt;) in the sequence based on PeptideShaker site
//     * inference results for the best assumption of the given spectrum match.
//     *
//     * @param spectrumMatch the spectrum match of interest
//     * @param useHtmlColorCoding if true, color coded HTML is used, otherwise
//     * PTM tags, e.g, &lt;mox&gt;, are used
//     * @param includeHtmlStartEndTags if true, HTML start and end tags are added
//     * @param useShortName if true the short names are used in the tags
//     * @return the tagged peptide sequence
//     */
//    public String getTaggedPeptideSequence(SpectrumMatch spectrumMatch, boolean useHtmlColorCoding, boolean includeHtmlStartEndTags, boolean useShortName) {
//        try {
//            Peptide peptide = spectrumMatch.getBestPeptideAssumption().getPeptide();
//            PSPtmScores ptmScores = new PSPtmScores();
//            ptmScores = (PSPtmScores) spectrumMatch.getUrParam(ptmScores);
//            return getTaggedPeptideSequence(peptide, ptmScores, useHtmlColorCoding, includeHtmlStartEndTags, useShortName);
//        } catch (Exception e) {
//           e.printStackTrace();
//            return "Error";
//        }
//    }
//
//    /**
//     * Returns the peptide with modification sites tagged (color coded or with
//     * PTM tags, e.g, &lt;mox&gt;) in the sequence based on the provided PTM
//     * localization scores.
//     *
//     * @param peptide the spectrum match of interest
//     * @param ptmScores the PTM localization scores
//     * @param useHtmlColorCoding if true, color coded HTML is used, otherwise
//     * PTM tags, e.g, &lt;mox&gt;, are used
//     * @param includeHtmlStartEndTags if true, HTML start and end tags are added
//     * @param useShortName if true the short names are used in the tags
//     *
//     * @return the tagged peptide sequence
//     */
//    public String getTaggedPeptideSequence(Peptide peptide, PSPtmScores ptmScores, boolean useHtmlColorCoding, boolean includeHtmlStartEndTags, boolean useShortName) {
//        HashMap<Integer, ArrayList<String>> fixedModifications = getFilteredModifications(peptide.getIndexedFixedModifications(), displayedPTMs);
//        HashMap<Integer, ArrayList<String>> confidentLocations = new HashMap<Integer, ArrayList<String>>();
//        HashMap<Integer, ArrayList<String>> representativeAmbiguousLocations = new HashMap<Integer, ArrayList<String>>();
//        HashMap<Integer, ArrayList<String>> secondaryAmbiguousLocations = new HashMap<Integer, ArrayList<String>>();
//        if (ptmScores != null) {
//            confidentLocations = getFilteredConfidentModificationsSites(ptmScores, displayedPTMs);
//            representativeAmbiguousLocations = getFilteredAmbiguousModificationsRepresentativeSites(ptmScores, displayedPTMs);
//            secondaryAmbiguousLocations = getFilteredAmbiguousModificationsSecondarySites(ptmScores, displayedPTMs);
//        }
//        return Peptide.getTaggedModifiedSequence(modificationProfile,
//                peptide, confidentLocations, representativeAmbiguousLocations, secondaryAmbiguousLocations, fixedModifications, useHtmlColorCoding, includeHtmlStartEndTags, useShortName);
//    }
//      /**
//     * The displayed PTMs.
//     */
//    private HashMap<String, Boolean> displayedPTMs = new HashMap<String, Boolean>();
//      /**
//     * The display preferences.
//     */
//    private DisplayPreferences displayPreferences = new DisplayPreferences();
//     /**
//     * Returns a list containing the names of the PTMs to display.
//     * 
//     * @return a list containing the names of the PTMs to display
//     */
//    public ArrayList<String> getDisplayedPtms() {
//        ArrayList<String> result = new ArrayList<String>();
//        for (String ptmName : displayedPTMs.keySet()) {
//            if (displayedPTMs.get(ptmName)) {
//                result.add(ptmName);
//            }
//        }
//        return result;
//    }

}
