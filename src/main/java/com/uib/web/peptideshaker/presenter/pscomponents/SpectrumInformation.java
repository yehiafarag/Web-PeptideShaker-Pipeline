/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.uib.web.peptideshaker.presenter.pscomponents;

import com.compomics.util.experiment.identification.matches.SpectrumMatch;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.preferences.IdentificationParameters;

/**
 *this class contain all the required information to load spectrum data
 * @author Yehia Farag
 */
public class SpectrumInformation {
    private   MSnSpectrum spectrum;
    private String charge;
    private double fragmentIonAccuracy;
    private IdentificationParameters identificationParameters;
    private SpectrumMatch spectrumMatch;
    private Object spectrumId;
    private int maxCharge;
    private double mzError;

    public int getMaxCharge() {
        return maxCharge;
    }

    public void setMaxCharge(int maxCharge) {
        this.maxCharge = maxCharge;
    }

    public Object getSpectrumId() {
        return spectrumId;
    }

    public void setSpectrumId(Object spectrumId) {
        this.spectrumId = spectrumId;
    }

    public MSnSpectrum getSpectrum() {
        return spectrum;
    }

    public void setSpectrum(MSnSpectrum spectrum) {
        this.spectrum = spectrum;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }

    public double getFragmentIonAccuracy() {
        return fragmentIonAccuracy;
    }

    public void setFragmentIonAccuracy(double fragmentIonAccuracy) {
        this.fragmentIonAccuracy = fragmentIonAccuracy;
    }

    public IdentificationParameters getIdentificationParameters() {
        return identificationParameters;
    }

    public void setIdentificationParameters(IdentificationParameters identificationParameters) {
        this.identificationParameters = identificationParameters;
    }

    public SpectrumMatch getSpectrumMatch() {
        return spectrumMatch;
    }

    public void setSpectrumMatch(SpectrumMatch spectrumMatch) {
        this.spectrumMatch = spectrumMatch;
    }

    public double getMzError() {
        return mzError;
    }

    public void setMzError(double mzError) {
        this.mzError = mzError;
    }
    
}
