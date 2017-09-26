
package com.uib.web.peptideshaker.model.core;
import com.compomics.util.Util;
import com.compomics.util.exceptions.ExceptionHandler;
import com.compomics.util.experiment.biology.Protein;
import com.compomics.util.experiment.identification.protein_inference.proteintree.ProteinTree;
import com.compomics.util.experiment.identification.protein_sequences.FastaIndex;
import com.compomics.util.waiting.WaitingHandler;
import com.compomics.util.io.SerializationUtils;
import com.compomics.util.preferences.UtilitiesUserPreferences;
import com.compomics.util.protein.Header;
import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import javax.swing.JProgressBar;
import uk.ac.ebi.pride.tools.braf.BufferedRandomAccessFile;

/**
 * Factory retrieving the information of the loaded FASTA file.
 *
 * @author Marc Vaudel
 * @author Harald Barsnes
 */
public class SequenceFactory {

    /**
     * Map of the currently loaded Headers.
     */
    private HashMap<String, Header> currentHeaderMap = new HashMap<String, Header>();
    /**
     * Map of the currently loaded proteins.
     */
    private HashMap<String, Protein> currentProteinMap = new HashMap<String, Protein>();
    /**
     * Index of the FASTA file.
     */
    private FastaIndex fastaIndex = null;
    /**
     * Random access file of the current FASTA file.
     */
    private BufferedRandomAccessFile currentRandomAccessFile = null;
    /**
     * The FASTA file currently loaded.
     */
    private File currentFastaFile = null;
    /**
     * Number of proteins to keep in cache, 100000 by default.
     */
    private int nCache = 100000;
    /**
     * List of accessions of the loaded proteins.
     */
    private ArrayList<String> loadedProteins = new ArrayList<String>();
    /**
     * Recognized flags for a decoy protein.
     */
    private  final String[] decoyFlags = {"REVERSED", "RND", "SHUFFLED", "DECOY"};
    /**
     * HashMap of the currently calculated protein molecular weights.
     */
    private HashMap<String, Double> molecularWeights = new HashMap<String, Double>();
    /**
     * The tag added after adding decoy sequences to a FASTA file.
     */
    private  String targetDecoyFileNameTag = "_concatenated_target_decoy.fasta";
    /**
     * The default protein tree attached to the database loaded
     */
    private ProteinTree defaultProteinTree = null;
    /**
     * Boolean indicating that the factory is reading the file.
     */
    private boolean reading = false;
    /**
     * The time out in milliseconds when querying the file.
     */
    public final  long timeOut = 10000;
    /**
     * Indicates whether the decoy hits should be kept in memory.
     */
    private boolean decoyInMemory = true;
    /**
     * The minimal protein count required for reliable target/decoy based
     * statistics.
     */
    public  int minProteinCount = 1000; // @TODO: use a better metric

    /**
     * Constructor.
     */
    public SequenceFactory() {
    }

    public void setFastaIndex(FastaIndex fastaIndex) {
        this.fastaIndex = fastaIndex;
    }

    


    /**
     * Indicates whether the database contained enough protein sequences for
     * reliability of the target/decoy based statistics.
     *
     * @return a boolean indicating whether the database contained enough
     * protein sequences for reliability of the target/decoy based statistics
     */
    public boolean hasEnoughSequences() {
        return getNTargetSequences() > minProteinCount;
    }

    /**
     * Clears the factory getInstance() needs to be called afterwards.
     *
     * @throws IOException if an IOException occurs
     * @throws SQLException if an SQLException occurs
     */
    public void clearFactory() throws IOException, SQLException {      
        defaultProteinTree = null;
        currentHeaderMap.clear();
        currentProteinMap.clear();
        fastaIndex = null;
        currentRandomAccessFile = null;
        currentFastaFile = null;
        loadedProteins.clear();
        molecularWeights.clear();
    }

    /**
     * Empties the cache of the factory.
     */
    public void emptyCache() {
        currentHeaderMap.clear();
        currentProteinMap.clear();
        loadedProteins.clear();
        molecularWeights.clear();
        if (defaultProteinTree != null) {
            defaultProteinTree.emptyCache();
        }
    }

    /**
     * Reduces the node cache size of the protein tree by the given share.
     *
     * @param share the share of the cache to remove. 0.5 means 50%
     */
    public void reduceNodeCacheSize(double share) {
        defaultProteinTree.reduceNodeCacheSize(share);
    }

    /**
     * Returns the number of nodes currently loaded in cache.
     *
     * @return the number of nodes currently loaded in cache
     */
    public int getNodesInCache() {
        return defaultProteinTree.getNodesInCache();
    }




   
    /**
     * Returns the protein indexed by the given index. It can be that the IO is
     * busy (especially when working on distant servers) thus returning an
     * error. The method will then retry after waiting waitingTime milliseconds.
     * The waitingTime is doubled for the next try. The method throws an
     * exception after timeout (see timeOut attribute).
     *
     * @param index the index where to look at
     * @return the header indexed by the given index
     * @throws InterruptedException
     * @throws IOException
     * @throws IllegalArgumentException
     */
    public  Protein getProtein(String accession, long index) throws IOException, IllegalArgumentException, InterruptedException, FileNotFoundException {

        
        try {
            if (reading) {
                throw new IllegalStateException("Attempting to read new line before current read operation is completed.");
            }
            reading = true;
            currentRandomAccessFile.seek(index);
            String line, sequence = "";
            Header currentHeader = currentHeaderMap.get(accession);
            boolean headerFound = false;

            while ((line = currentRandomAccessFile.readLine()) != null) {
                line = line.trim();

                if (line.startsWith(">")) {
                    if (!sequence.equals("") || headerFound) {
                        break;
                    }
                    if (currentHeader == null) {
                        currentHeader = Header.parseFromFASTA(line);
                        if (currentHeader == null) {
                            throw new IllegalArgumentException("Could not parse fasta header \"" + line + "\".");
                        }
                        currentHeaderMap.put(accession, currentHeader);
                    }
                    headerFound = true;
                } else {
                    sequence += line;
                }
            }

            Protein currentProtein = new Protein(accession, currentHeader.getDatabaseType(), sequence, isDecoyAccession(accession));

            addProteinToCache(accession, currentProtein);

            reading = false;

            return currentProtein;

        } catch (IOException e) {
            reading = false;            
        }
        return null;
    }

    /**
     * Adds a protein to the cache and keeps it under the desired size.
     *
     * @param accession the accession of the protein to add
     * @param protein the protein to add
     */
    private synchronized void addProteinToCache(String accession, Protein protein) {
        while (loadedProteins.size() >= nCache) {
            currentProteinMap.remove(loadedProteins.get(0));
            currentHeaderMap.remove(loadedProteins.get(0));
            loadedProteins.remove(0);
        }

        loadedProteins.add(accession);
        currentProteinMap.put(accession, protein);
    }

    /**
     * Returns the desired header for the protein in the FASTA file.
     *
     * @param accession accession of the desired protein
     * @return the corresponding header
     * @throws IOException exception thrown whenever an error occurred while
     * reading the FASTA file
     * @throws IllegalArgumentException exception thrown whenever a protein is
     * not found
     * @throws InterruptedException if an InterruptedException occurs
     * @throws ClassNotFoundException if a ClassNotFoundException occurs
     * @throws FileNotFoundException if a FileNotFoundException occurs
     */
    public Header getHeader(String accession){
        Header result = currentHeaderMap.get(accession);

        if (result == null) {

            Long index = fastaIndex.getIndex(accession);  
            result = getHeader(index, 0);
            currentHeaderMap.put(accession, result);
        }

        return result;
    }

   
    /**
     * Returns the header indexed by the given index. It can be that the IO is
     * busy (especially when working on distant servers) thus returning an
     * error. The method will then try 100 times at 0.01 second intervals.
     *
     * @param index the index where to look at
     * @param nTries the number of tries already made
     * @return the header indexed by the given index
     */
    private Header getHeader(long index, int nTries){

        if (reading) {
            throw new IllegalStateException("Attempting to read new line before current read operation is completed.");
        }
        try {
            reading = true;
            currentRandomAccessFile.seek(index);
            Header result = Header.parseFromFASTA(currentRandomAccessFile.readLine());
            reading = false;
            return result;
        } catch (IOException e) {
            reading = false;
            if (nTries <= 100) {
                return getHeader(index, nTries + 1);
            } else {
                return null;
            }
        }
    }

    /**
     * Loads a new FASTA file in the factory. Only one FASTA file can be loaded
     * at a time.
     *
     * @param fastaFile the FASTA file to load
     *
     * @throws FileNotFoundException exception thrown if the file was not found
     * @throws IOException exception thrown if an error occurred while reading
     * the FASTA file
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing the file index
     * @throws StringIndexOutOfBoundsException thrown if issues occur during the
     * parsing of the protein headers
     * @throws IllegalArgumentException if non unique accession numbers are
     * found
     */
    public void loadFastaFile(File fastaFile) throws FileNotFoundException, IOException, ClassNotFoundException, StringIndexOutOfBoundsException, IllegalArgumentException {
        loadFastaFile(fastaFile, null);
    }

    /**
     * Loads a new FASTA file in the factory. Only one FASTA file can be loaded
     * at a time.
     *
     * @param fastaFile the FASTA file to load
     * @param waitingHandler a waitingHandler showing the progress
     * @throws FileNotFoundException exception thrown if the file was not found
     * @throws IOException exception thrown if an error occurred while reading
     * the FASTA file
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while deserializing the file index
     * @throws StringIndexOutOfBoundsException thrown if issues occur during the
     * parsing of the protein headers
     * @throws IllegalArgumentException if non unique accession numbers are
     * found
     */
    public void loadFastaFile(File fastaFile, FastaIndex fastaIndex )  {

        if (!fastaFile.exists()) {
           System.out.println("The FASTA file \'" + fastaFile.getAbsolutePath() + "\' could not be found!");
        }

        defaultProteinTree = null;
        currentFastaFile = fastaFile;
//        currentRandomAccessFile = new BufferedRandomAccessFile(fastaFile, "r", 1024 * 100);
        this.fastaIndex = fastaIndex;
    }

    /**
     * Indicates whether the connection to the random access file has been
     * closed.
     *
     * @return a boolean indicating whether the connection to the random access
     * file has been closed.
     */
    public boolean isClosed() {
        return currentFastaFile == null;
    }

    /**
     * Resets the connection to the random access file.
     *
     * @throws IOException if an IOException occurs
     */
    public void resetConnection() throws IOException {
        currentRandomAccessFile.close();
        currentRandomAccessFile = new BufferedRandomAccessFile(currentFastaFile, "r", 1024 * 100);
    }



 


    /**
     * Returns a boolean indicating whether a protein is decoy or not based on
     * the protein accession and a given decoy flag. Note: in most cases the
     * faster isDecoyAccession method should be used instead!
     *
     * @param proteinAccession The accession of the protein
     * @param decoyFlag the decoy flag
     * @return a boolean indicating whether the protein is Decoy.
     */
    public  boolean isDecoy(String proteinAccession, String decoyFlag) {

        // test if the decoy tag is empty, and return false if it is
        if (decoyFlag == null || decoyFlag.isEmpty()) {
            return false;
        }

        String start = decoyFlag + ".*";
        String end = ".*" + decoyFlag;

        return proteinAccession.matches(start) || proteinAccession.matches(end);
    }

    /**
     * Returns the default tag matched in the sequence if any. Null else.
     *
     * @param proteinAccession the protein accession
     *
     * @return the decoy tag matched by this protein
     */
    private  String getDecoyFlag(String proteinAccession) {
        for (String flag : decoyFlags) {
            if (isDecoy(proteinAccession, flag)) {
                return flag;
            }
        }
        return null;
    }

    /**
     * Indicates whether a protein is a decoy in the selected loaded FASTA file.
     *
     * @param proteinAccession the protein accession of interest.
     * @return true if decoy
     */
    public boolean isDecoyAccession(String proteinAccession) {
        return fastaIndex.isDecoy(proteinAccession);
    }

    /**
     * Indicates whether the database loaded contains decoy sequences.
     *
     * @return a boolean indicating whether the database loaded contains decoy
     * sequences
     */
    public boolean concatenatedTargetDecoy() {
        return fastaIndex.isConcatenatedTargetDecoy();
    }

    /**
     * Indicates whether the decoy sequences are reversed versions of the target
     * and the decoy accessions built based on the sequence factory methods. See
     * getDefaultDecoyAccession(String targetAccession).
     *
     * @return true if the the decoy sequences are reversed versions of the
     * target and the decoy accessions built based on the sequence factory
     * method
     */
    public boolean isDefaultReversed() {
        return fastaIndex.isDefaultReversed();
    }

    /**
     * Returns the number of target sequences in the database.
     *
     * @return the number of target sequences in the database
     */
    public int getNTargetSequences() {
        return fastaIndex.getNTarget();
    }

    /**
     * Returns the number of sequences in the FASTA file.
     *
     * @return the number of sequences in the FASTA file
     */
    public int getNSequences() {
        return fastaIndex.getNSequences();
    }
    
    /**
     * Reverses a protein sequence.
     *
     * @param sequence the protein sequence
     * @return the reversed protein sequence
     */
    public  String reverseSequence(String sequence) {
        return new StringBuilder(sequence).reverse().toString();
    }

    /**
     * Returns the sequences present in the database. An empty list if no file
     * is loaded.
     *
     * @return the sequences present in the database
     */
    public Set<String> getAccessions() {
        Set<String> setToFill = new HashSet<String>();
        if (fastaIndex != null) {
            setToFill = fastaIndex.getIndexes().keySet();
        }
        return setToFill;
    }
 
    /**
     * Returns the occurrence of every amino acid in the database.
     *
     * @return a map containing all amino acid occurrence in the database
     * @throws IOException exception thrown whenever an error occurred while
     * reading the database
     * @throws IllegalArgumentException if an IllegalArgumentException occurs
     * @throws InterruptedException if an InterruptedException occurs
     * @throws FileNotFoundException if a FileNotFoundException occurs
     * @throws ClassNotFoundException if an ClassNotFoundException occurs
     */
    public HashMap<String, Integer> getAAOccurrences() throws IOException, IllegalArgumentException, InterruptedException, FileNotFoundException, ClassNotFoundException {

        HashMap<String, Integer> aaMap = new HashMap<>();
//        Set<String> accessions = getAccessions();      
//        for (String accession : accessions) {
//
//            if (!isDecoyAccession(accession)) {
//                Protein protein = getProtein(accession);
//                for (String aa : protein.getSequence().split("")) {
//                    Integer n = aaMap.get(aa);
//                    if (n == null) {
//                        n = 0;
//                    }
//                    aaMap.put(aa, n + 1);
//                }
//            }
//          
//        }      

        return aaMap;
    }

    /**
     * Returns the protein's molecular weight in kDa.
     *
     * @param accession the protein's accession number
     *
     * @return the protein's molecular weight
     *
     * @throws IOException exception thrown whenever an error occurred while
     * reading the protein sequence
     * @throws InterruptedException exception thrown whenever an error occurred
     * while reading the protein sequence
     * @throws FileNotFoundException exception thrown whenever an error occurred
     * while reading the protein sequence
     * @throws ClassNotFoundException exception thrown whenever an error
     * occurred while reading the protein sequence
     */
    public double computeMolecularWeight(String accession) throws IOException, InterruptedException, FileNotFoundException, ClassNotFoundException {

        if (isDefaultReversed() && isDecoyAccession(accession)) {
            // Don't really see where we would need that...
            try {
                return computeMolecularWeight(getDefaultTargetAccession(accession));
            } catch (Exception e) {
                // back to standard mode
            }
        }

        // see if we've already calculated the weight of this protein
        Double molecularWeight = molecularWeights.get(accession);
//        if (molecularWeight == null) {
//            Protein protein = getProtein(accession);
//            molecularWeight = protein.computeMolecularWeight() / 1000;
//            molecularWeights.put(accession, molecularWeight);
//        }
        return molecularWeight;
    }

    /**
     * Returns the target-decoy file name tag.
     *
     * @return the targetDecoyFileNameTag
     */
    public  String getTargetDecoyFileNameTag() {
        return targetDecoyFileNameTag;
    }


    /**
     * Returns the name of the loaded FASTA file. Null if none loaded.
     *
     * @return the name of the loaded FASTA file
     */
    public String getFileName() {
        if (fastaIndex == null) {
            return null;
        }
        return fastaIndex.getFileName();
    }

    /**
     * Returns the currently loaded fasta file.
     *
     * @return the currently loaded fasta file
     */
    public File getCurrentFastaFile() {
        return currentFastaFile;
    }

    /**
     * Returns the default suffix for a decoy accession.
     *
     * @return the default suffix for a decoy accession
     */
    public  String getDefaultDecoyAccessionSuffix() {
        return "_" + decoyFlags[0];
    }

    /**
     * Returns the default decoy accession for a target accession.
     *
     * @param targetAccession the target accession
     * @return the default decoy accession
     */
    public  String getDefaultDecoyAccession(String targetAccession) {
        return targetAccession + getDefaultDecoyAccessionSuffix();
    }

    /**
     * Returns the default description for a decoy protein.
     *
     * @param targetDescription the description of a target protein
     * @return the default description of the decoy protein
     */
    public  String getDefaultDecoyDescription(String targetDescription) {
        return targetDescription + "-" + decoyFlags[0];
    }

    /**
     * Returns the default target accession of a given decoy protein. Note:
     * works only for the accessions constructed according to
     * getDefaultDecoyAccession(String targetAccession).
     *
     * @param decoyAccession the decoy accession
     * @return the target accession
     */
    public  String getDefaultTargetAccession(String decoyAccession) {
        return decoyAccession.substring(0, decoyAccession.length() - getDefaultDecoyAccessionSuffix().length());
    }

    /**
     * Returns the FASTA index of the currently loaded file.
     *
     * @return the FASTA index of the currently loaded file
     */
    public FastaIndex getCurrentFastaIndex() {
        return fastaIndex;
    }

    /**
     * Returns the default protein tree. Null if none created.
     *
     * @return the default protein tree
     */
    public ProteinTree getDefaultProteinTree() {
        return defaultProteinTree;
    }

    /**
     * Returns the default protein tree corresponding to the database loaded in
     * factory, creates a new one if none found.
     *
     * @param waitingHandler waiting handler displaying progress to the user
     * during the initiation of the tree
     * @param exceptionHandler handler for the exceptions encountered while
     * creating the tree
     *
     * @return the default protein tree
     *
     * @throws IOException exception thrown whenever an error occurs while
     * reading or writing a file.
     * @throws ClassNotFoundException exception thrown whenever an error occurs
     * while deserializing an object.
     * @throws InterruptedException exception thrown whenever a threading issue
     * occurred while interacting with the tree.
     * @throws SQLException if an SQLException exception thrown whenever a
     * problem occurred while interacting with the tree database.
     */
    public ProteinTree getDefaultProteinTree(WaitingHandler waitingHandler, ExceptionHandler exceptionHandler) throws IOException, InterruptedException, ClassNotFoundException, IllegalArgumentException, SQLException {
        int nThreads = Math.max(Runtime.getRuntime().availableProcessors(), 1);
        return getDefaultProteinTree(nThreads, waitingHandler, exceptionHandler, true);
    }

    /**
     * Returns the default protein tree corresponding to the database loaded in
     * factory, creates a new one if none found.
     *
     * @param nThreads the number of threads to use
     * @param waitingHandler waiting handler displaying progress to the user
     * during the initiation of the tree
     * @param exceptionHandler handler for the exceptions encountered while
     * creating the tree
     *
     * @return the default protein tree
     *
     * @throws IOException exception thrown whenever an error occurs while
     * reading or writing a file.
     * @throws ClassNotFoundException exception thrown whenever an error occurs
     * while deserializing an object.
     * @throws InterruptedException exception thrown whenever a threading issue
     * occurred while interacting with the tree.
     * @throws SQLException if an SQLException exception thrown whenever a
     * problem occurred while interacting with the tree database.
     */
    public ProteinTree getDefaultProteinTree(int nThreads, WaitingHandler waitingHandler, ExceptionHandler exceptionHandler) throws IOException, InterruptedException, ClassNotFoundException, IllegalArgumentException, SQLException {
        return getDefaultProteinTree(nThreads, waitingHandler, exceptionHandler, true);
    }

    /**
     * Returns the default protein tree corresponding to the database loaded in
     * factory, creates a new one if none found.
     *
     * @param nThreads the number of threads to use
     * @param waitingHandler waiting handler displaying progress to the user
     * during the initiation of the tree
     * @param exceptionHandler handler for the exceptions encountered while
     * creating the tree
     * @param displayProgress display progress
     * @return the default protein tree
     *
     * @throws IOException exception thrown whenever an error occurs while
     * reading or writing a file.
     * @throws ClassNotFoundException exception thrown whenever an error occurs
     * while deserializing an object.
     * @throws InterruptedException exception thrown whenever a threading issue
     * occurred while interacting with the tree.
     * @throws SQLException if an SQLException exception thrown whenever a
     * problem occurred while interacting with the tree database.
     */
    public ProteinTree getDefaultProteinTree(int nThreads, WaitingHandler waitingHandler, ExceptionHandler exceptionHandler, boolean displayProgress) throws IOException, InterruptedException, ClassNotFoundException, IllegalArgumentException, SQLException {
        if (defaultProteinTree == null) {

            UtilitiesUserPreferences userPreferences = UtilitiesUserPreferences.loadUserPreferences();
            int memoryPreference = userPreferences.getMemoryPreference();
            int memoryAllocated = 3 * memoryPreference / 4;
            int cacheSize = 250000;
            if (memoryPreference < 2500) {
                cacheSize = 5000;
            } else if (memoryPreference < 10000) {
                cacheSize = 25000;
            }

            defaultProteinTree = new ProteinTree(memoryAllocated, cacheSize);

            int tagLength = 3;
            defaultProteinTree.initiateTree(tagLength, 50, 50, waitingHandler, exceptionHandler, true, displayProgress, nThreads);
            emptyCache();

            int treeSize = memoryPreference / 4;
            defaultProteinTree.setMemoryAllocation(treeSize);

            // close and delete the database if the process was canceled
            if (waitingHandler != null && waitingHandler.isRunCanceled()) {
                defaultProteinTree.deleteDb();
            }
        }

        return defaultProteinTree;
    }

    /**
     * Try to delete the default protein tree.
     *
     * @param exceptionHandler handler for the exceptions encountered while
     * creating the tree
     *
     * @return true of the deletion was a success
     */
    public synchronized boolean deleteProteinTree(ExceptionHandler exceptionHandler) {
        if (defaultProteinTree != null) {
            try {
                defaultProteinTree.close();
            } catch (Exception e) {
                if (exceptionHandler != null) {
                    exceptionHandler.catchException(e);
                } else {
                    e.printStackTrace();
                }
                return false;
            }
            if (defaultProteinTree != null) {
                return defaultProteinTree.deleteDb();
            }
        }
        return true;
    }

    /**
     * Returns an iterator of all the headers in the FASTA file. Note: when
     * reaching the end of the file the connection will be closed. Do it using
     * the close() method if the end is never reached.
     *
     * @param targetOnly boolean indicating whether only target accessions shall
     * be iterated
     * @return a header iterator
     * @throws FileNotFoundException if a FileNotFoundException occurs
     */
    public HeaderIterator getHeaderIterator(boolean targetOnly) throws FileNotFoundException {
        return new HeaderIterator(currentFastaFile, targetOnly);
    }

    /**
     * Returns an iterator of all the proteins in the FASTA file. Note: when
     * reaching the end of the file the connection will be closed. Do it using
     * the close() method if the end is never reached.
     *
     * @param targetOnly boolean indicating whether only target accessions shall
     * be iterated
     * @return a protein iterator
     * @throws FileNotFoundException if a FileNotFoundException occurs
     */
    public ProteinIterator getProteinIterator(boolean targetOnly) throws FileNotFoundException {
        return new ProteinIterator(currentFastaFile, targetOnly);
    }

    /**
     * Returns whether decoys should be kept in memory.
     *
     * @return true if decoys should be kept in memory
     */
    public boolean isDecoyInMemory() {
        return decoyInMemory;
    }

    /**
     * Sets whether decoys should be kept in memory.
     *
     * @param decoyInMemory true if decoys should be kept in memory
     */
    public void setDecoyInMemory(boolean decoyInMemory) {
        this.decoyInMemory = decoyInMemory;
    }

    /**
     * Convenience iterator iterating the headers of a FASTA file without using
     * the cache. The order is the one in the FASTA file.
     */
    public class HeaderIterator {

        /**
         * The header of the next protein.
         */
        private Header nextHeader = null;
        /**
         * The buffered reader.
         */
        private BufferedReader br;
        /**
         * Boolean indicating whether target protein only should be iterated.
         */
        private final boolean targetOnly;

        /**
         * Constructor.
         *
         * @param targetOnly if true only target proteins will be iterated
         * @param file the FASTA file to iterate
         * @throws FileNotFoundException if a FileNotFoundException occurs
         */
        public HeaderIterator(File file, boolean targetOnly) throws FileNotFoundException {
            this.targetOnly = targetOnly;
            br = new BufferedReader(new FileReader(file));
        }

        /**
         * Returns true if there is a next header.
         *
         * @return true if there is a next header
         * @throws IOException if a IOException occurs
         */
        public boolean hasNext() throws IOException {
            nextHeader = null;
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.equals("")) {
                    if (line.startsWith(">")) {
                        nextHeader = Header.parseFromFASTA(line);
                        if (!targetOnly || !isDecoyAccession(nextHeader.getAccession())) {
                            break;
                        } else {
                            nextHeader = null;
                        }
                    }
                }
            }
            if (nextHeader != null) {
                return true;
            } else {
                close();
                return false;
            }
        }

        /**
         * Returns the next header in the FASTA file.
         *
         * @return the next header in the FASTA file
         */
        public Header getNext() {
            return nextHeader;
        }

        /**
         * Closes the connection to the file.
         *
         * @throws IOException if a IOException occurs
         */
        public void close() throws IOException {
            br.close();
        }
    }

    /**
     * Convenience iterator iterating all proteins in a FASTA file without using
     * index or cache.
     */
    public class ProteinIterator {

        /**
         * The header of the next protein.
         */
        private Header nextHeader = null;

        /**
         * The next protein.
         */
        private Protein nextProtein = null;

        /**
         * The buffered reader.
         */
        private BufferedReader br;
        /**
         * Boolean indicating whether target protein only should be iterated.
         */
        private final boolean targetOnly;

        /**
         * Constructor.
         *
         * @param targetOnly if true only target proteins will be iterated
         * @param file the FASTA file.
         *
         * @throws FileNotFoundException if a FileNotFoundException occurs
         */
        public ProteinIterator(File file, boolean targetOnly) throws FileNotFoundException {
            this.targetOnly = targetOnly;
            br = new BufferedReader(new FileReader(file));
        }

        /**
         * Returns true if there is another protein.
         *
         * @return true if there is another protein
         *
         * @throws IOException if an IOException occurs
         */
        public boolean hasNext() throws IOException {

            nextProtein = null;
            String sequence = "";
            Header header = nextHeader;
            boolean newHeaderFound = false;

            String line = br.readLine();

            // reached end of file
            if (line == null) {
                return false;
            }

            while (line != null) {
                if (line.startsWith(">")) {
                    Header tempHeader = Header.parseFromFASTA(line);
                    String accession = tempHeader.getAccessionOrRest();
                    if (targetOnly && isDecoyAccession(accession)) {
                        while ((line = br.readLine()) != null) {
                            if (line.startsWith(">")) {
                                tempHeader = Header.parseFromFASTA(line);
                                if (!isDecoyAccession(tempHeader.getAccessionOrRest())) {
                                    break;
                                }
                            }
                        }
                        if (line == null) {
                            break;
                        }
                    }
                    if (header == null) {
                        header = tempHeader;
                    } else {
                        nextHeader = tempHeader;
                        newHeaderFound = true;
                        break;
                    }
                } else {
                    sequence += line.trim();
                }

                line = br.readLine();
            }
            if (newHeaderFound || line == null) { // line == null means that we read the last protein
                String accession = header.getAccessionOrRest();
                nextProtein = new Protein(accession, header.getDatabaseType(), sequence, isDecoyAccession(accession));
                currentHeaderMap.put(accession, header);
                return true;
            } else {
                close();
                return false;
            }
        }

        /**
         * Returns the next protein.
         *
         * @return the next protein
         */
        public Protein getNextProtein() {
            return nextProtein;
        }

        /**
         * Closes the connection to the file.
         *
         * @throws IOException if an IOException occurs
         */
        public void close() throws IOException {
            br.close();
        }
    }
}
