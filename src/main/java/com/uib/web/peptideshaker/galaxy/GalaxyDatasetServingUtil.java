package com.uib.web.peptideshaker.galaxy;

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
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.ac.ebi.pride.tools.braf.BufferedRandomAccessFile;

/**
 *
 * @author Yehia Farag
 */
public class GalaxyDatasetServingUtil {

    /**
     * user API key for galaxy.
     */
    private final String userApiKey;

    /**
     * The Galaxy server address (URL).
     */
    private final String galaxyURL;
    private final GalaxyDatasetServingUtil.ParameterNameValue[] params;

    /**
     * Update the file settings to be ready for reading the data.
     *
     * @param galaxyURL In use Galaxy server URL
     * @param userApiKey the required API key to access galaxy API
     */
    public GalaxyDatasetServingUtil(String galaxyURL, String userApiKey) {
        this.galaxyURL = galaxyURL;
        this.userApiKey = userApiKey;
        params = new GalaxyDatasetServingUtil.ParameterNameValue[]{
            new GalaxyDatasetServingUtil.ParameterNameValue("key", userApiKey), //                new PeptidShakerUI.ParameterNameValue("dataset_id", "42038d56a41ee4b9"), //                new PeptidShakerUI.ParameterNameValue("offset", "131085")
        };
//        try {
//            String tool;
//            PeptidShakerUI.ParameterNameValue[] params = new PeptidShakerUI.ParameterNameValue[]{
//                new PeptidShakerUI.ParameterNameValue("key", "ab84003e53c247bd7e2ca7ec949ab2cb"), //                new PeptidShakerUI.ParameterNameValue("dataset_id", "42038d56a41ee4b9"), //                new PeptidShakerUI.ParameterNameValue("offset", "131085")
//            };
//            StringBuilder locationBuilder = new StringBuilder("http://129.177.231.22/galaxy/api/histories/03501d7626bd192f/contents/0aab0e4c25198ad8/display?/data/input_database.fasta?");
//            for (int i = 0; i < params.length; i++) {
//                if (i > 0) {
//                    locationBuilder.append('&');
//                }
//                locationBuilder.append(params[i].name).append('=').append(params[i].value);
//            }
//            String location = locationBuilder.toString();
//            URL website = new URL(location);
//
//            URLConnection conn = website.openConnection();
//            conn.addRequestProperty("Accept", "application/json, text/javascript, */*; q=0.01");
//            conn.addRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
//            conn.addRequestProperty("Accept-Language", "ar,en-US;q=0.8,en;q=0.6,en-GB;q=0.4");
//            conn.addRequestProperty("Cache-Control", "no-cache");
//            conn.addRequestProperty("Connection", "keep-alive");
////            conn.addRequestProperty("Range", "bytes=0-900");
//            conn.addRequestProperty("DNT", "1");
//            conn.addRequestProperty("X-Requested-With", "XMLHttpRequest");
//            conn.addRequestProperty("Pragma", "no-cache");
//            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/64.0.3282.186 Safari/537.36");
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//            System.out.println("--->> " + conn.getHeaderFields() + "\"}");//  
//
////            
//            ZipInputStream Zis = new ZipInputStream(new BufferedInputStream(conn.getInputStream()));
//            byte[] bytesToRead = new byte[200];//            
////            conn.getInputStream().read(bytesToRead);
////            try {
////                decompress(bytesToRead);
////            } catch (DataFormatException ex) {
////                ex.printStackTrace();
////            }
////                System.out.println("at zip file start " + "   end " + + "   "+ Zis.available());
////                System.out.println("--->> " + new String(bytesToRead) + "");         
////                ByteArrayInputStream bis = new ByteArrayInputStream(bytesToRead);
////		GZIPInputStream gis = new GZIPInputStream(bis);
////		BufferedReader br = new BufferedReader(new InputStreamReader(gis, "UTF-8"));
////		StringBuilder sb = new StringBuilder();
////		String line;
////		while((line = br.readLine()) != null) {
////			sb.append(line);
////		}
////		br.close();
////		gis.close();
////		bis.close();
////                 System.out.println("--->> done --> " + Zis.getNextZipEntry().getSize()+ "");
////   
////
//            ZipEntry entry = Zis.getNextEntry();
////
//            int counter = 0;
//            int size = 0;
//
//            while (entry != null && counter < 10) {
//                if (entry.getName().endsWith(".mgf")) {
//                    Zis.mark(300);
//                    Zis.read(bytesToRead, 0,100);
//                    System.out.println("--->> " + entry.getCompressedSize()+ "");
//                    break;
//                }
//                entry = Zis.getNextEntry();
//                counter++;
//            }
//
////            
//        } catch (MalformedURLException ex) {
//            ex.printStackTrace();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//        }
    }

    public MSnSpectrum getSpectrum(long start, String historyId, String dsId, String fileName) {

        try {

            StringBuilder locationBuilder = new StringBuilder(galaxyURL + "/api/histories/" + historyId + "/contents/" + dsId + "/display?");
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
            conn.addRequestProperty("Range", "bytes=" + start + "-" + (start + 10000));
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
                        } catch (Exception e) {
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
                        MSnSpectrum msnSpectrum = new MSnSpectrum(2, precursor, spectrumTitle, spectrum, fileName);
                        msnSpectrum.setScanNumber(scanNumber);
                        return msnSpectrum;
                    } else if (insideSpectrum && !line.equals("")) {
                        try {
                            System.out.println("expected error with line "+line);
                            String values[] = line.split("\\s+");
                            Double mz = new Double(values[0]);
                            Double intensity = new Double(values[1]);
                            spectrum.put(mz, new Peak(mz, intensity));
                        } catch (NumberFormatException e1) {
                            // ignore comments and all other lines
                        }
                    }
                }

//                String header = bin.readLine();
//
//                System.out.println("at header " + header);
//                String mgfLine = "";
//                String line;
//                int count = 1;
//                while ((line = bin.readLine()) != null) {
//                    mgfLine += line;
//                    System.out.println("mgf line " + (count++) + " - " + line);
//                    if (line.contains("END IONS")) {
//                        break;
//                    }
//                }
//                bin.close();
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

        for (String chargeAsString : chargesAsString) {

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
        }

        // if empty, add a default charge of 1
        if (result.isEmpty()) {
            result.add(new Charge(Charge.PLUS, 1));
        }

        return result;
    }

//    private URLConnection initializeConnection(String url) {
//        try {
//            URL website = new URL(url);
//            URLConnection conn = website.openConnection();
//            conn.addRequestProperty("Accept", "*/*");
//            conn.addRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
//            conn.addRequestProperty("Accept-Language", "ar,en-US;q=0.8,en;q=0.6,en-GB;q=0.4");
//            conn.addRequestProperty("Cache-Control", "no-cache");
//            conn.addRequestProperty("Connection", "keep-alive");
//            conn.addRequestProperty("DNT", "1");
//            conn.addRequestProperty("X-Requested-With", "XMLHttpRequest");
//            conn.addRequestProperty("Pragma", "no-cache");
//            conn.setDoInput(true);
//            return conn;
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(GalaxyDatasetServingUtil.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(GalaxyDatasetServingUtil.class.getName()).log(Level.SEVERE, null, ex);
//        }
//
//        return null;
//    }
//
//    /**
//     * Get Data from start index until the end index
//     *
//     * @param points set of selected points
//     * @return Set<String> that can be used for initialising different tables
//     */
//    public Set<String> getDataAsLines(Set<Integer> points) {
//        Set<String> dataLines = new LinkedHashSet<>();
////        System.out.println("at get data from peptides");
//        //if offset
////        if (chunkedData.equalsIgnoreCase("&offset=")) {
////            try {
////                InputStream in = null;
////                long startPonter = 0;
////                String jsonStr;
////                for (Integer p : points) {
////                    byte[] bytesToRead;
//////                    URLConnection conn = (URLConnection) initializeConnection(fileURL + p.getStartPoint());
//////                    in = conn.getInputStream();
//////                    bytesToRead = new byte[(p.getLength() + 30)];
//////                    startPonter = p.getStartPoint() + in.read(bytesToRead);
//////                    jsonStr = new String(bytesToRead) + "\"}";//                      
//////                    JSONObject jsonObject = new JSONObject(jsonStr);
//////                    dataLines.add(jsonObject.getString("ck_data"));
////
////                }
////
////            } catch (IOException ex) {
////                ex.printStackTrace();
////            } catch (JSONException ex) {
////                ex.printStackTrace();
////            }
//
////        } else {
////            System.out.println("is a chunked data");
////        }
//        //if chunked
//        return dataLines;
//
//    }
//
//    /**
//     * Get Data from start index until the end index
//     *
//     * @param startRange start index
//     * @param endRange last index
//     * @return Set<String> that can be used for initialising different tables
//     */
//    public Set<String> getDataAsLines(long startRange, String endRangeKeyword) {
//        Set<String> dataLines = new LinkedHashSet<>();
////        System.out.println("at get data from peptides");
//        //if offset
////        if (chunkedData.equalsIgnoreCase("&offset=")) {
////            try {
////                String jsonStr;
////                final long startTime = System.nanoTime();
////                URLConnection conn = (URLConnection) initializeConnection(fileURL + startRange);
////                InputStream in = conn.getInputStream();
////                BufferedReader bin = new BufferedReader(new InputStreamReader(in));
////                String line = bin.readLine().split(endRangeKeyword)[0];
////
////                jsonStr = line + "\"}";
////                JSONObject jsonObject = new JSONObject(jsonStr);
//////                System.out.println("at -- "+ jsonObject.getString("ck_data"));
////                final long endTime = System.nanoTime();
////                dataLines.addAll(Arrays.asList(jsonObject.getString("ck_data").split("\\+")[1].replaceFirst("\\n", "").split("\n")));
////                System.out.println("method 1 : " + (endTime - startTime));
////
////            } catch (IOException | JSONException ex) {
////                ex.printStackTrace();
////            }
////
////        } else {
////            System.out.println("is a chunked data");
////        }
////        //if chunked
//
//        return dataLines;
//
//    }
//
//    private int checkIndexCover(InputStream in, Iterator<Integer> itr) {
//
//        return -1;
//    }
//    /**
//     * Skip bytes in file is accepted.
//     */
//    private boolean supportByteSkipping;
//
//    /**
//     * The server support byte serving.
//     */
//    private boolean supportByteServing;
//
//    /**
//     * Skip bytes in file is accepted.
//     *
//     * @return boolean skip bytes supported for such file
//     */
//    public boolean isSupportByteSkipping() {
//        return supportByteSkipping;
//    }
//    /**
//     * The file size in bytes.
//     */
//    private long fileSize = Long.MAX_VALUE;
//
//    /**
//     * The file size in bytes.
//     *
//     * @param fileSize
//     */
//    public void setFileSize(long fileSize) {
//        this.fileSize = fileSize;
//    }
//
//    /**
//     * Key word for splitting data chunk or offset..
//     *
//     * @param chunkedData String keyword (chunk or offset)
//     */
//    public void setChunkedData(String chunkedData) {
//        this.chunkedData = chunkedData;
//    }
//
////    /**
////     * Re-index the requested file location based on the server type.
////     *
////     * @param reIndexFactor 1 in case of offset and size of chunk in case of using chunk keyword
////     */
////    public void setReIndexFactor(int reIndexFactor) {
////        this.reIndexFactor = reIndexFactor;
////    }
//    /**
//     * The file unique id.
//     */
//    private String fileId;
//    /**
//     * The path to user folder in the system.
//     */
//    private String pathToFolder;
//    /**
//     * The path to the local file in the system.
//     */
//    private String localFilePath;
//
////    /**
////     * The main cookies to attach to user request.
////     *
////     * @param cookiesRequestProperty cookies as string to be added to requests
////     */
////    public void setCookiesRequestProperty(String cookiesRequestProperty) {
////        this.cookiesRequestProperty = cookiesRequestProperty;
////    }
//    /**
//     ** The Galaxy server support byte Serving (Range header)
//     *
//     * @param supportByteServing boolean server support byte serving
//     */
//    public void setSupportByteServing(boolean supportByteServing) {
//        this.supportByteServing = supportByteServing;
//    }
//
//    /**
//     ** The Galaxy server support byte Serving (Range header)
//     *
//     * @return supportByteServing boolean server support byte serving
//     */
//    public boolean isSupportByteServing() {
//        return supportByteServing;
//    }
//
//    /**
//     * The total size of the file on Galaxy server
//     *
//     * @return
//     * @retun fileSize the file size in bytes
//     */
//    public long getFileSize() {
//        return fileSize;
//    }
//
//    /**
//     * The web address of the file in Galaxy server
//     *
//     * @return fileURL String web address of the file location
//     */
//    public String getFileURL() {
//        return fileURL;
//    }
//
//    /**
//     * The web address of the file in Galaxy server
//     *
//     * @return fileURL String web address of the file location
//     */
//    public String getFileId() {
//        return fileId;
//    }
//
//    /**
//     * The the path to the container folder on the server
//     *
//     * @return pathToFolder String web address of the file location
//     */
//    public String getPathToFolder() {
//        return pathToFolder;
//    }
//
//    /**
//     * The web address of the file in Galaxy server
//     *
//     * @param fileURL String web address of the file location
//     */
//    public void setFileURL(String fileURL) {
//        this.fileURL = fileURL;
//    }
//
//    /**
//     * The the path to the container folder on the server
//     *
//     * @param pathToFolder String web address of the file location
//     */
//    public void setPathToFolder(String pathToFolder) {
//        this.pathToFolder = pathToFolder;
//    }
//
//    /**
//     * The web address of the file in Galaxy server
//     *
//     * @param fileId String unique file id
//     */
//    public void setFileId(String fileId) {
//        this.fileId = fileId;
//    }
//
//
//    
//
////
////    ;
//    /**
//     * Read bytes from server (case of byte serving support)
//     *
//     * @param start start pointer index
//     * @param end last index for the requested data
//     * @return the data as string
//     */
//    protected Set<String> readRemoteOffsetBytes(Set<IndexPoint> points) {
//        BufferedReader br = null;
//        Set<String> dataLines = new LinkedHashSet<>();
//
//        try {
//
//            long pointer = 0;
//            for (IndexPoint point : points) {
//                URL proteinsFileUrl = new URL(fileURL + point.getStartPoint());
//                System.err.println("at peptide url " + fileURL + point.getStartPoint());
//                URLConnection conn = proteinsFileUrl.openConnection();
//                conn.addRequestProperty("Cookie", cookiesRequestProperty);
//                conn.addRequestProperty("Accept", "*/*");
//                conn.addRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
//                conn.addRequestProperty("Accept-Language", "ar,en-US;q=0.8,en;q=0.6,en-GB;q=0.4");
//                conn.addRequestProperty("Cache-Control", "no-cache");
//                conn.addRequestProperty("Connection", "keep-alive");
//                conn.addRequestProperty("DNT", "1");
//                conn.addRequestProperty("X-Requested-With", "XMLHttpRequest");
//                conn.addRequestProperty("Pragma", "no-cache");
//                conn.setDoInput(true);
//                InputStream inputStream = conn.getInputStream();
//                byte[] data = new byte[point.getLength()];
//                inputStream.read(data);
////                char c;
////                String s = "";
////                do {
////                    c = (char) inputStream.read();
////                    if (c == '\n') {
////                        break;
////                    }
////                    s += c + "";
////                } while (c != -1 || s.length()==100);
//
////                dataLines.add(s.trim());
//                inputStream.close();
//                System.out.println("at peptide line " + new String(data));
//
//            }
//
////            System.out.println("at read line " + new String(data));
//        } catch (MalformedURLException ex) {
//            ex.printStackTrace();
//            System.out.println("com.uib.onlinepeptideshaker.model.LogicLayer.loadPeptideShakerResults()" + ex.getLocalizedMessage());
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//            System.out.println("com.uib.onlinepeptideshaker.model.LogicLayer.loadPeptideShakerResults()" + ex.getLocalizedMessage());
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            System.out.println("com.uib.onlinepeptideshaker.model.LogicLayer.loadPeptideShakerResults()" + ex.getLocalizedMessage());
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    System.out.println("com.uib.onlinepeptideshaker.model.LogicLayer.loadPeptideShakerResults()" + e.getLocalizedMessage());
//                }
//            }
//        }
//        return dataLines;
//    }
//
//    /**
//     * Read bytes from server (case of byte serving support)
//     *
//     * @param start start pointer index
//     * @param end last index for the requested data
//     * @return the data as string
//     */
//    protected Set<String> readRemoteServerBytes(Set<IndexPoint> points) {
//        BufferedReader br = null;
//        Set<String> dataLines = new LinkedHashSet<>();
//
//        try {
//            URL proteinsFileUrl = new URL(fileURL);
//            Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("https://test-fe.cbu.uib.no/", 80));
//            try {
//                List<Proxy> l = ProxySelector.getDefault().select(new URI("https://test-fe.cbu.uib.no"));
//                for (Proxy p : l) {
//                    System.out.println("at proxis " + p.address());
//                }
//            } catch (URISyntaxException ex) {
//                Logger.getLogger(ReadableFile.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            URLConnection conn = proteinsFileUrl.openConnection();
//            conn.addRequestProperty("Cookie", cookiesRequestProperty);
//            conn.addRequestProperty("Content-Type", "multipart/byteranges;");
//
//            long pointer = 0;
//            String rangeValue = "bytes=";
//
//            for (IndexPoint point : points) {
////                len += point.getLength();
//                rangeValue += point.getStartPoint() + "-" + (point.getStartPoint() + point.getLength()) + " ";
//            }
//            rangeValue = rangeValue.trim().replace(" ", ",");
//
//            conn.addRequestProperty("Range", rangeValue);
//            conn.setDoInput(true);
//            InputStream inputStream = conn.getInputStream();
//            br = new BufferedReader(new InputStreamReader(inputStream));
//            int len = Integer.parseInt(conn.getHeaderField("Content-Length"));
////            System.out.println("at Range is " + len+"   length "+ conn.getHeaderField("Content-Length"));
//
//            char[] data = new char[len];
//            br.read(data);
//            String line = new String(data);
//            String regex;
//            if (line.contains("Content-Range:")) {
//                regex = line.split("Content-Range:")[1].split("\n")[0].split("/")[1];
//                String[] dataArr = line.split(regex);
//                for (String str : dataArr) {
//                    String finalLine = str.split("--")[0].trim();
//                    if (finalLine.length() == 0) {
//                        continue;
//                    }
//                    dataLines.add(finalLine);
//                }
//            } else {
//                dataLines.add(line.trim());
//            }
//            br.close();
//            inputStream.close();
//
////            System.out.println("at read line " + new String(data));
//        } catch (MalformedURLException ex) {
//            ex.printStackTrace();
//            System.out.println("com.uib.onlinepeptideshaker.model.LogicLayer.loadPeptideShakerResults()" + ex.getLocalizedMessage());
//        } catch (FileNotFoundException ex) {
//            ex.printStackTrace();
//            System.out.println("com.uib.onlinepeptideshaker.model.LogicLayer.loadPeptideShakerResults()" + ex.getLocalizedMessage());
//        } catch (IOException ex) {
//            ex.printStackTrace();
//            System.out.println("com.uib.onlinepeptideshaker.model.LogicLayer.loadPeptideShakerResults()" + ex.getLocalizedMessage());
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    System.out.println("com.uib.onlinepeptideshaker.model.LogicLayer.loadPeptideShakerResults()" + e.getLocalizedMessage());
//                }
//            }
//        }
//        return dataLines;
//    }
//
//    /**
//     * Read bytes from server (case of byte serving support)
//     *
//     * @param start start pointer index
//     * @param end last index for the requested data
//     * @return the data as string
//     */
//    protected Set<String> readLocalFileBytes(Set<IndexPoint> points) {
//        Set<String> linesSet = new LinkedHashSet<>();
//        BufferedRandomAccessFile bufferedRandomAccessFile;
//        try {//           
//            bufferedRandomAccessFile = new BufferedRandomAccessFile(getLocalFilePath(), "r", 1024 * 100);
//            String line;
//            for (IndexPoint point : points) {
//                bufferedRandomAccessFile.seek(point.getStartPoint());
//                byte[] data = new byte[point.getLength()];
//                bufferedRandomAccessFile.read(data);
//                line = new String(data);
//                linesSet.add(line);
//
//            }
//
//            bufferedRandomAccessFile.close();
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return linesSet;
//
//    }
//
//    /**
//     * Read bytes from server (case of byte serving support)
//     *
//     * @param start start pointer index
//     * @param end last index for the requested data
//     * @return the data as string
//     *
//     */
//    private Set<String> skipReadServerBytes(Set<IndexPoint> points) {
//        BufferedReader br = null;
//        Set<String> dataLines = new LinkedHashSet<>();
//
//        try {
//            URL proteinsFileUrl = new URL(fileURL);
//            URLConnection conn = proteinsFileUrl.openConnection();
//            conn.addRequestProperty("Cookie", cookiesRequestProperty);
//            conn.setDoInput(true);
//            InputStream inputStream = conn.getInputStream();
//            long pointer = 0;
//            br = new BufferedReader(new InputStreamReader(inputStream));
//            for (IndexPoint point : points) {
//                br.skip(point.getStartPoint() - pointer);
//                char[] data = new char[point.getLength()];
//                br.read(data);
//                dataLines.add(new String(data));
//                pointer = point.getStartPoint() + point.getLength();
//
//            }
//
//        } catch (MalformedURLException ex) {
//            System.out.println("com.uib.onlinepeptideshaker.model.LogicLayer.loadPeptideShakerResults()" + ex.getLocalizedMessage());
//        } catch (FileNotFoundException ex) {
//            System.out.println("com.uib.onlinepeptideshaker.model.LogicLayer.loadPeptideShakerResults()" + ex.getLocalizedMessage());
//        } catch (IOException ex) {
//            System.out.println("com.uib.onlinepeptideshaker.model.LogicLayer.loadPeptideShakerResults()" + ex.getLocalizedMessage());
//        } finally {
//            if (br != null) {
//                try {
//                    br.close();
//                } catch (IOException e) {
//                    System.out.println("com.uib.onlinepeptideshaker.model.LogicLayer.loadPeptideShakerResults()" + e.getLocalizedMessage());
//                }
//            }
//        }
//        return dataLines;
//
//    }
//
//    /**
//     * Path to the local file in system.
//     *
//     * @return
//     */
//    protected String getLocalFilePath() {
//        return localFilePath;
//    }
//
//    private void initFile(File file, String urlResource) {
//        FileOutputStream fos = null;
//        try {
////            new Proxy(Proxy.Type.HTTP, new InetSocketAddress("test-fe.cbu.uib.no", 3128))
//            System.out.println("at updated headers method");
//            URL proteinsFileUrl = new URL(urlResource);
//            URLConnection conn = proteinsFileUrl.openConnection();
//            conn.addRequestProperty("Cookie", cookiesRequestProperty);
//
//            conn.addRequestProperty("Accept", "*/*");
//            conn.addRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
//            conn.addRequestProperty("Accept-Language", "ar,en-US;q=0.8,en;q=0.6,en-GB;q=0.4");
//            conn.addRequestProperty("Cache-Control", "no-cache");
//            conn.addRequestProperty("Range", "bytes=1000-2000");
//            conn.addRequestProperty("Connection", "keep-alive");
//            conn.addRequestProperty("DNT", "1");
////            conn.addRequestProperty("Host", "test-fe.cbu.uib.no");
//            conn.addRequestProperty("Pragma", "no-cache");
////            conn.addRequestProperty("Referer", "https://test-fe.cbu.uib.no/galaxy/");
////            conn.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");
//
//            conn.setDoInput(true);
//            InputStream in = conn.getInputStream();
////            System.out.println("at update heders " + conn.getHeaderFields());
//            try (ReadableByteChannel rbc = Channels.newChannel(in)) {
//
//                fos = new FileOutputStream(file);
//                fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
//                fos.close();
//                rbc.close();
//                in.close();
//
//            } catch (MalformedURLException ex) {
//                ex.printStackTrace();
//            } catch (IOException ex) {
//                ex.printStackTrace();
//            } finally {
//                if (fos != null) {
//                    try {
//                        fos.close();
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//
//            }
//        } catch (MalformedURLException ex) {
//            Logger.getLogger(GalaxyDataUtil.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(GalaxyDataUtil.class.getName()).log(Level.SEVERE, null, ex);
//        }
//    }
    private class ParameterNameValue {

        private String name;
        private String value;

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
