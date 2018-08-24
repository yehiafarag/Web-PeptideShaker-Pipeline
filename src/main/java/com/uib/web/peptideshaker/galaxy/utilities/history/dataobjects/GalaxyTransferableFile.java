package com.uib.web.peptideshaker.galaxy.utilities.history.dataobjects;

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
public class GalaxyTransferableFile extends GalaxyFileObject {

    /**
     * Galaxy file object.
     */
    private final GalaxyFileObject galaxyFileObject;
    /**
     * User data files folder.
     */
    private final File userFolder;
    /**
     * The file on Galaxy server is compressed.
     */
    private final boolean zipped;
    /**
     * The list of files contained in folder (case of compressed folder).
     */
    private final Set<String> subFilesList;

    /**
     * Constructor to initialise the main variables
     *
     * @param userFolder User data files folder
     * @param galaxyFileObject Galaxy file object
     * @param zipped The file on Galaxy server is compressed
     */
    public GalaxyTransferableFile(File userFolder, GalaxyFileObject galaxyFileObject, boolean zipped) {
        this.galaxyFileObject = galaxyFileObject;
        this.userFolder = userFolder;
        this.zipped = zipped;
        this.subFilesList = new LinkedHashSet<>();
        super.setName(galaxyFileObject.getName());
        super.setType(galaxyFileObject.getType());
        super.setStatus(galaxyFileObject.getStatus());
        super.setDownloadUrl(galaxyFileObject.getDownloadUrl());
        super.setGalaxyId(galaxyFileObject.getGalaxyId());
        super.setHistoryId(galaxyFileObject.getHistoryId());
    }

    /**
     * Get the main Galaxy object that contains galaxy file information
     *
     * @return Galaxy File Object
     */
    public GalaxyFileObject getGalaxyFileObject() {
        return galaxyFileObject;
    }

    /**
     * Get the name list of the files exist in the compressed folder
     *
     * @return the sub files list of files contained in folder (case of
     * compressed folder).
     *
     */
    public Set<String> getSubFilesList() {
        if (zipped && subFilesList.isEmpty()) {
            FileOutputStream fos = null;
            try {

                URL downloadableFile = new URL(galaxyFileObject.getDownloadUrl());
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
                    subFilesList.add(entry.getName());
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
        return subFilesList;

    }

    /**
     * Get the Java File Object that represents the downloaded file on Online
     * Peptide Shaker
     *
     * @return File object that already downloaded on the user files folder
     * @throws java.io.IOException*
     */
    public File getFile() throws IOException {
        String fileName = galaxyFileObject.getGalaxyId().replace("/", "_");
        if (galaxyFileObject.getType().equalsIgnoreCase("Search Paramerters File (JSON)") && !zipped) {
            fileName += galaxyFileObject.getName();
        }
        File file = new File(userFolder, fileName);
        if (file.exists()) {
            return file;
        }
        if (zipped) {
            FileOutputStream fos = null;
            try {
                URL downloadableFile = new URL(galaxyFileObject.getDownloadUrl());
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
                    } else if (!entry.isDirectory() && entry.getName().endsWith(galaxyFileObject.getGalaxyId().split("__")[1].replace("reports/", ""))) //do something with entry  
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
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException ex) {
                    }
                }

            }

        } else {
            FileOutputStream fos = null;
            try {
                URL downloadableFile = new URL(galaxyFileObject.getDownloadUrl());
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

}