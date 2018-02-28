package com.uib.web.peptideshaker.presenter.layouts.peptideshakerview.components;

import com.ejt.vaadin.sizereporter.ComponentResizeEvent;
import com.ejt.vaadin.sizereporter.SizeReporter;
import com.itextpdf.text.pdf.codec.Base64;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Image;
import com.vaadin.ui.Notification;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jfree.chart.encoders.ImageEncoder;
import org.jfree.chart.encoders.ImageEncoderFactory;
import org.jfree.chart.encoders.ImageFormat;

/**
 * This class responsible for generate change coverage images and do all
 * calculations related to that task
 *
 * @author Yehia Farag
 */
public class ChainCoverageComponent {

    private final int proteinSequenceLength;
    private final Map<String, Rectangle> chainsBlocks;
    private int compWidth = 260;
    private int comHeight = 30;
    private double correctFactor;
    private final int[] coverageArr;
    private Image chainCoverageWebComponent;

    public ChainCoverageComponent(int proteinSequenceLength) {
        this.proteinSequenceLength = proteinSequenceLength;
        this.correctFactor = (double) (compWidth - 60) / (double) this.proteinSequenceLength;
        this.chainsBlocks = new LinkedHashMap<>();
        this.coverageArr = new int[proteinSequenceLength];
        this.chainCoverageWebComponent = new Image();
        this.chainCoverageWebComponent.setSizeFull();
        SizeReporter imageSizeReporter = new SizeReporter(chainCoverageWebComponent);
        imageSizeReporter.addResizeListener((ComponentResizeEvent event) -> {
            Notification.show("new width " + event.getWidth() + "   new h " + event.getHeight());
            this.compWidth = event.getWidth();
            chainCoverageWebComponent.setSource(new ExternalResource(drawImage(lasteSelectedChain)));
        });

    }

    int counter = 1;
    private String lasteSelectedChain;

    public void addChainRange(String chainId, int start, int end) {
        Rectangle chain = new Rectangle(start, 10, (end - start + 1), 10);

        if (end > proteinSequenceLength) {
            System.out.println("at end is bigger ");
        }

        chainsBlocks.put(chainId + "_" + (counter++), chain);
        for (int i = start; i <= end; i++) {
            coverageArr[i] = 1;
        }
    }
    private double coverage = -1;

    public double getCoverage() {
        if (coverage != -1) {
            return coverage;
        }
        counter = 0;
        for (int i = 0; i < coverageArr.length; i++) {
            counter += coverageArr[i];
        }
        coverage = ((double) counter / (double) this.proteinSequenceLength) * 100.00;
        return coverage;
    }

    public String selectChain(String chainId) {
        return drawImage(chainId);

    }

    private Color chaincolor;
    private Color bordercolor;
    DecimalFormat df = new DecimalFormat("#.#");

    private String drawImage(String chainId) {
        lasteSelectedChain = chainId;
        BufferedImage image = new BufferedImage(compWidth, comHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = image.createGraphics();

        //draw sequence line
        g2.setColor(Color.GRAY);
        g2.fillRect(5, 14, (compWidth - 60), 3);

        //chaine border
        g2.setStroke(new BasicStroke(2));

//        if (chainId.equalsIgnoreCase("All")) {
        chaincolor = new Color(245, 245, 245);
        bordercolor = Color.LIGHT_GRAY;
//        } else {
////            chaincolor = new Color(245, 245, 245);
////            bordercolor = Color.LIGHT_GRAY;
//        }
        for (int i = 0; i < coverageArr.length; i++) {
            if (coverageArr[i] > 0) {
                //start draw
                double start = i;
                double end = i;
                for (int y = i; (y < coverageArr.length) && coverageArr[y] > 0; y++) {
                    end++;
                    i = y;
                }
                g2.setColor(bordercolor);
                g2.drawRect(5 + (int) ((double) start * correctFactor), 10, (int) ((end - start + 1) * correctFactor), 10);
                g2.setColor(chaincolor);
                g2.fillRect(5 + (int) ((double) start * correctFactor), 10, (int) ((end - start + 1) * correctFactor), 10);

            }
        }

        String v;
        if (!chainId.contains("All")) {
            counter = 0;
            chainsBlocks.keySet().forEach((c) -> {
                if (c.contains(chainId)) {
                    g2.setColor(new Color(255, 195, 206));
                    g2.drawRect(5 + (int) ((double) chainsBlocks.get(c).x * correctFactor), chainsBlocks.get(c).y, (int) ((double) chainsBlocks.get(c).width * correctFactor), chainsBlocks.get(c).height);
                    g2.setColor(Color.LIGHT_GRAY);
                    g2.fillRect(5 + (int) ((double) chainsBlocks.get(c).x * correctFactor), chainsBlocks.get(c).y, (int) ((double) chainsBlocks.get(c).width * correctFactor), chainsBlocks.get(c).height);
                    counter += (chainsBlocks.get(c).width - 1);
                }
            });

            v = df.format(((double) counter / (double) proteinSequenceLength) * 100.0);
        } else {
            double d = getCoverage();
            v = df.format(d);
        }
        //total chain coverage
//     
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("\"Open Sans\", sans-serif", Font.PLAIN, 14));
        g2.drawString(v + "%", (compWidth - 40), (comHeight / 2) + 6);
        g2.dispose();
        byte[] imageData = null;
        try {
            ImageEncoder in = ImageEncoderFactory.newInstance(ImageFormat.PNG, 1);
            imageData = in.encode(image);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }

        String base64 = Base64.encodeBytes(imageData);
        base64 = "data:image/png;base64," + base64;
        chainCoverageWebComponent.setSource(new ExternalResource(base64));

        return base64;
    }

    public void setCompWidth(int compWidth) {
        this.compWidth = compWidth;
        this.correctFactor = (double) this.proteinSequenceLength / ((double) compWidth - 60.0);
    }

    public void setComHeight(int comHeight) {
        this.comHeight = comHeight;
    }

    public Image getChainCoverageWebComponent() {
        return chainCoverageWebComponent;
    }
}
