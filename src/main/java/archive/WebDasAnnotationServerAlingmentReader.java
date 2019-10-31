
package archive;

//import com.compomics.util.pdbfinder.das.readers.DasAlignment;
import java.util.ArrayList;

/**
 * DasAnnotationServerAlingmentReader.
 *
 * @author Niklaas Colaert
 */
public class WebDasAnnotationServerAlingmentReader {

//    /**
//     * The XML string to parse.
//     */
//    private final String iXml;
//    /**
//     * The last feature end position.
//     */
//    private int lastFeatureEndPosition = 0;
//
//    /**
//     * Creates a new reader for a XML string.
//     *
//     * @param aXml the XML string
//     */
//    public WebDasAnnotationServerAlingmentReader(String aXml) {
//        this.iXml = aXml;
//    }
//
//    /**
//     * Get all alignment in the XML string.
//     *
//     * @return all alignment in the XML string
//     */
//    public DasAlignment[] getAllAlignments() {
//
//        ArrayList<DasAlignment> alings = new ArrayList();
//        while (iXml.indexOf("<alignment alignType=\"PDB_SP\">", lastFeatureEndPosition) != -1) {
//            String alignment = iXml.substring(iXml.indexOf("<alignment alignType=\"PDB_SP\">", lastFeatureEndPosition), iXml.indexOf("</alignment>", lastFeatureEndPosition) + 12);
//            lastFeatureEndPosition = iXml.indexOf("</alignment>", lastFeatureEndPosition) + 5;
//            DasAlignment f = new DasAlignment(alignment);
//            alings.add(f);
//        }
//        DasAlignment[] alignments = new DasAlignment[alings.size()];
//        alings.toArray(alignments);
//        return alignments;
//    }
}
