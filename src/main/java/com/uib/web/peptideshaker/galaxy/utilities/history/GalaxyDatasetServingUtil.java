package com.uib.web.peptideshaker.galaxy.utilities.history;

import com.compomics.util.experiment.massspectrometry.Charge;
import com.compomics.util.experiment.massspectrometry.MSnSpectrum;
import com.compomics.util.experiment.massspectrometry.Peak;
import com.compomics.util.experiment.massspectrometry.Precursor;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Utility class for galaxy files that helps in managing the integration and
 * data transfer between Galaxy Server and Online Peptide Shaker (managing
 * requests and responses)
 *
 * @author Yehia Farag
 */
public class GalaxyDatasetServingUtil {

    /**
     * The Galaxy server address (URL).
     */
    private final String galaxyLink;
    /**
     * Requests parameters values.
     */
    private final GalaxyDatasetServingUtil.ParameterNameValue[] params;

    /**
     * Update the file settings to be ready for reading the data.
     *
     * @param galaxyLink In use Galaxy server URL
     * @param userApiKey the required API key to access galaxy API
     */
    public GalaxyDatasetServingUtil(String galaxyLink, String userApiKey) {
        this.galaxyLink = galaxyLink;
        params = new GalaxyDatasetServingUtil.ParameterNameValue[]{
            new GalaxyDatasetServingUtil.ParameterNameValue("key", userApiKey),};

    }

    /**
     * Get MSn spectrum object using HTML request to Galaxy server (byte serving
     * support).
     *
     * @param startIndex the spectra index on the MGF file
     * @param historyId the Galaxy Server History ID that contain the MGF file
     * @param MGFGalaxyID The ID of the MGF file on Galaxy Server
     * @param MGFFileName The MGF file name
     * @return MSnSpectrum spectrum object
     */
    public MSnSpectrum getSpectrum(long startIndex, String historyId, String MGFGalaxyID, String MGFFileName) {

        try {

            StringBuilder locationBuilder = new StringBuilder(galaxyLink + "/api/histories/" + historyId + "/contents/" + MGFGalaxyID + "/display?");
            for (int i = 0; i < params.length; i++) {
                if (i > 0) {
                    locationBuilder.append('&');
                }
                locationBuilder.append(params[i].name).append('=').append(params[i].value);
            }
            String location = locationBuilder.toString();
            URL website = new URL(location);

            URLConnection conn = website.openConnection();
            conn.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
            conn.addRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
            conn.addRequestProperty("Accept-Language", "ar,en-US;q=0.8,en;q=0.6,en-GB;q=0.4");
            conn.addRequestProperty("Cache-Control", "no-cache");
            conn.addRequestProperty("Connection", "keep-alive");
            conn.addRequestProperty("Range", "bytes=" + startIndex + "-" + (startIndex + 10000));
            conn.addRequestProperty("DNT", "1");
            conn.addRequestProperty("X-Requested-With", "XMLHttpRequest");
            conn.addRequestProperty("Pragma", "no-cache");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
            conn.setDoInput(true);

            double precursorMz = 0, precursorIntensity = 0, rt = -1.0, rt1 = -1, rt2 = -1;
            ArrayList<Charge> precursorCharges = new ArrayList<>();
            String scanNumber = "", spectrumTitle = "";
            HashMap<Double, Peak> spectrum = new HashMap<>();
            String line;
            boolean insideSpectrum = false;

            try (BufferedReader bin = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                while ((line = bin.readLine()) != null) {
                    // fix for lines ending with \r
                    if (line.endsWith("\r")) {
                        line = line.replace("\r", "");
                    }

                    if (line.startsWith("BEGIN IONS")) {
                        insideSpectrum = true;
                        spectrum = new HashMap<>();
                    } else if (line.startsWith("TITLE")) {
                        insideSpectrum = true;
                        spectrumTitle = line.substring(line.indexOf('=') + 1);
                        try {
                            spectrumTitle = URLDecoder.decode(spectrumTitle, "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            System.out.println("An exception was thrown when trying to decode an mgf title: " + spectrumTitle);
                            e.printStackTrace();
                        }
                    } else if (line.startsWith("CHARGE")) {
                        precursorCharges = parseCharges(line);
                    } else if (line.startsWith("PEPMASS")) {
                        String temp = line.substring(line.indexOf("=") + 1);
                        String[] values = temp.split("\\s");
                        precursorMz = Double.parseDouble(values[0]);
                        if (values.length > 1) {
                            precursorIntensity = Double.parseDouble(values[1]);
                        } else {
                            precursorIntensity = 0.0;
                        }
                    } else if (line.startsWith("RTINSECONDS")) {
                        try {
                            String rtInput = line.substring(line.indexOf('=') + 1);
                            String[] rtWindow = rtInput.split("-");
                            if (rtWindow.length == 1) {
                                String tempRt = rtWindow[0];
                                // possible fix for values like RTINSECONDS=PT121.250000S
                                if (tempRt.startsWith("PT") && tempRt.endsWith("S")) {
                                    tempRt = tempRt.substring(2, tempRt.length() - 1);
                                }
                                rt = new Double(tempRt);
                            } else if (rtWindow.length == 2) {
                                rt1 = new Double(rtWindow[0]);
                                rt2 = new Double(rtWindow[1]);
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("An exception was thrown when trying to decode the retention time: " + spectrumTitle);
                            e.printStackTrace();
                            // ignore exception, RT will not be parsed
                        }
                    } else if (line.startsWith("TOLU")) {
                        // peptide tolerance unit not implemented
                    } else if (line.startsWith("TOL")) {
                        // peptide tolerance not implemented
                    } else if (line.startsWith("SEQ")) {
                        // sequence qualifier not implemented
                    } else if (line.startsWith("COMP")) {
                        // composition qualifier not implemented
                    } else if (line.startsWith("ETAG")) {
                        // error tolerant search sequence tag not implemented
                    } else if (line.startsWith("TAG")) {
                        // sequence tag not implemented
                    } else if (line.startsWith("SCANS")) {
                        try {
                            scanNumber = line.substring(line.indexOf('=') + 1);
                        } catch (Exception e) {
                            throw new IllegalArgumentException("Cannot parse scan number.");
                        }
                    } else if (line.startsWith("INSTRUMENT")) {
                        // ion series not implemented
                    } else if (line.startsWith("END IONS")) {

                        Precursor precursor;
                        if (rt1 != -1 && rt2 != -1) {
                            precursor = new Precursor(precursorMz, precursorIntensity, precursorCharges, rt1, rt2);
                        } else {
                            precursor = new Precursor(rt, precursorMz, precursorIntensity, precursorCharges);
                        }
                        MSnSpectrum msnSpectrum = new MSnSpectrum(2, precursor, spectrumTitle, spectrum, MGFFileName);
                        msnSpectrum.setScanNumber(scanNumber);
                        return msnSpectrum;
                    } else if (insideSpectrum && !line.equals("")) {
                        try {
                            String values[] = line.split("\\s+");
                            Double mz = new Double(values[0]);
                            Double intensity = new Double(values[1]);
                            spectrum.put(mz, new Peak(mz, intensity));
                        } catch (NumberFormatException e1) {
                            // ignore comments and all other lines
                        }
                    }
                }

            }

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Parses the charge line of an MGF files.
     *
     * @param chargeLine the charge line
     * @return the possible charges found
     * @throws IllegalArgumentException
     */
    private ArrayList<Charge> parseCharges(String chargeLine) throws IllegalArgumentException {
        ArrayList<Charge> result = new ArrayList<>(1);
        String tempLine = chargeLine.substring(chargeLine.indexOf("=") + 1);
        String[] chargesAnd = tempLine.split(" and ");
        ArrayList<String> chargesAsString = new ArrayList<>();
        for (String charge : chargesAnd) {
            for (String charge2 : charge.split(",")) {
                chargesAsString.add(charge2.trim());
            }
        }

        chargesAsString.forEach((chargeAsString) -> {
            Integer value;
            chargeAsString = chargeAsString.trim();
            if (!chargeAsString.isEmpty()) {
                try {
                    if (chargeAsString.endsWith("+")) {
                        value = new Integer(chargeAsString.substring(0, chargeAsString.length() - 1));
                        result.add(new Charge(Charge.PLUS, value));
                    } else if (chargeAsString.endsWith("-")) {
                        value = new Integer(chargeAsString.substring(0, chargeAsString.length() - 1));
                        result.add(new Charge(Charge.MINUS, value));
                    } else if (!chargeAsString.equalsIgnoreCase("Mr")) {
                        result.add(new Charge(Charge.PLUS, new Integer(chargeAsString)));
                    }
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    throw new IllegalArgumentException("\'" + chargeAsString + "\' could not be processed as a valid precursor charge!");
                }
            }
        });
        if (result.isEmpty()) {
            result.add(new Charge(Charge.PLUS, 1));
        }

        return result;
    }

    /**
     * Private inner class represents encoded name and value of parameters that
     * is used in HTML requests
     *
     * @author Yehia Farag
     *
     */
    private class ParameterNameValue {

        /**
         * Parameter name
         */
        private String name;
        /**
         * Parameter value
         */
        private String value;

        /**
         * Initialise the parameters objects
         *
         * @param name parameter name
         * @param value parameter value
         */
        public ParameterNameValue(String name, String value) {
            try {
                this.name = URLEncoder.encode(name, "UTF-8");
                this.value = URLEncoder.encode(value, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
            }
        }
    }
}
