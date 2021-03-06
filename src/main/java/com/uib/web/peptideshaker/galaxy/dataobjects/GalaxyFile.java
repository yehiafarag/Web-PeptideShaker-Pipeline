package com.uib.web.peptideshaker.galaxy.dataobjects;

import com.vaadin.server.VaadinSession;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * This class represents usable files on galaxy the class allow downloading the
 * files once there is a need to use it
 *
 * @author Yehia Farag
 */
public class GalaxyFile extends SystemDataSet {

    private final SystemDataSet dataset;
    private final File userFolder;
    private final boolean zipped;
    private final Set<String> filesList;
    private String originalFileName;

    public GalaxyFile(File userFolder, SystemDataSet dataset, boolean zipped) {
        this.dataset = dataset;
        this.userFolder = userFolder;
        this.zipped = zipped;
        this.filesList = new LinkedHashSet<>();
        super.setName(dataset.getName());
        super.setType(dataset.getType());
        super.setStatus(dataset.getStatus());
        super.setDownloadUrl(dataset.getDownloadUrl());
        super.setGalaxyId(dataset.getGalaxyId());
        super.setHistoryId(dataset.getHistoryId());
    }

    public SystemDataSet getDataset() {
        return dataset;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public Set<String> getFileInformation() {
        if (zipped && filesList.isEmpty()) {
            FileOutputStream fos = null;
            try {

                URL downloadableFile = new URL(dataset.getDownloadUrl());
                URLConnection conn = downloadableFile.openConnection();
                conn.addRequestProperty("Accept", "*/*");
                conn.addRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
                conn.addRequestProperty("Accept-Language", "ar,en-US;q=0.8,en;q=0.6,en-GB;q=0.4");
                conn.addRequestProperty("Cache-Control", "no-cache");
                conn.addRequestProperty("Connection", "keep-alive");
                conn.addRequestProperty("DNT", "1");
                conn.addRequestProperty("Pragma", "no-cache");
                conn.setDoInput(true);
                ZipInputStream Zis = new ZipInputStream(conn.getInputStream());
                ZipEntry entry = Zis.getNextEntry();
                while (entry != null) {
                    filesList.add(entry.getName());
                    entry = Zis.getNextEntry();
                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }

            }

        }
        return filesList;

    }

    public File getFile() throws IOException {
        String fileName = dataset.getGalaxyId().replace("/", "_");
        if (dataset.getType().equalsIgnoreCase("Search Paramerters File (JSON)") && !zipped) {
            fileName += dataset.getName() ;
        }
        File file = new File(userFolder, fileName);
        if (file.exists()) {
            return file;
        }
        if (zipped) {
            FileOutputStream fos = null;
            try {
                URL downloadableFile = new URL(dataset.getDownloadUrl());
                URLConnection conn = downloadableFile.openConnection();
                conn.addRequestProperty("Accept", "*/*");
                conn.addRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
                conn.addRequestProperty("Accept-Language", "ar,en-US;q=0.8,en;q=0.6,en-GB;q=0.4");
                conn.addRequestProperty("Cache-Control", "no-cache");
                conn.addRequestProperty("Connection", "keep-alive");
                conn.addRequestProperty("DNT", "1");
                conn.addRequestProperty("Pragma", "no-cache");
                conn.setDoInput(true);
                ZipInputStream Zis = new ZipInputStream(conn.getInputStream());
                int counter = 0;
                ZipEntry entry = Zis.getNextEntry();

                while (entry != null && counter < 10) {
                    if (!entry.isDirectory() && entry.getName().equalsIgnoreCase("SEARCHGUI_IdentificationParameters.par")) //do something with entry  
                    {
                        try (ReadableByteChannel rbc = Channels.newChannel(Zis)) {
                            fos = new FileOutputStream(file);
                            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                            fos.close();
                            rbc.close();
                            Zis.close();
                            break;
                        }
                    } else if (!entry.isDirectory() && entry.getName().endsWith(dataset.getGalaxyId().split("__")[1].replace("reports/", ""))) //do something with entry  
                    {
                        try (ReadableByteChannel rbc = Channels.newChannel(Zis)) {
                            fos = new FileOutputStream(file);
                            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                            fos.close();
                            rbc.close();
                            Zis.close();
                            break;
                        }
                    }
                    entry = Zis.getNextEntry();
                    counter++;
                }

            } catch (MalformedURLException ex) {
//                ex.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ex) {
//                        ex.printStackTrace();
                    }
                }

            }

        } else {
            FileOutputStream fos = null;
            try {
                URL downloadableFile = new URL(dataset.getDownloadUrl());
                URLConnection conn = downloadableFile.openConnection();
                conn.addRequestProperty("Cookie", VaadinSession.getCurrent().getAttribute("cookies") + "");
                conn.addRequestProperty("Accept", "*/*");
                conn.addRequestProperty("Accept-Encoding", "gzip, deflate, sdch, br");
                conn.addRequestProperty("Accept-Language", "ar,en-US;q=0.8,en;q=0.6,en-GB;q=0.4");
                conn.addRequestProperty("Cache-Control", "no-cache");
                conn.addRequestProperty("Connection", "keep-alive");
                conn.addRequestProperty("DNT", "1");
                conn.addRequestProperty("Pragma", "no-cache");
                conn.setDoInput(true);
                InputStream in = conn.getInputStream();
                try (ReadableByteChannel rbc = Channels.newChannel(in)) {
                    fos = new FileOutputStream(file);
                    fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
                    fos.close();
                    rbc.close();
                    in.close();

                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }

                }
            } catch (MalformedURLException ex) {
                ex.printStackTrace();
            }
        }
        return file;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

}
