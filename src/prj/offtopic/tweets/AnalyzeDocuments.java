package prj.offtopic.tweets;//package lucene;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.math.linear.OpenMapRealVector;
import org.apache.commons.math.linear.RealVectorFormat;
import org.apache.commons.math.linear.SparseRealVector;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Assert;

/**
 * This class expects one argument indexDir - The location of the index files
 * for which analysis is to be carried out.
 * <p>
 * It finds the highest and least cosine similarity between documents in the
 * index.
 *
 * @author pramod anantharam
 */

public class AnalyzeDocuments {

    private static double getCosineSimilarity(DocVector d1, DocVector d2) {
        return (d1.vector.dotProduct(d2.vector))
                / (d1.vector.getNorm() * d2.vector.getNorm());
    }

    private static double findMinSimilarity(DocVector[] docs, int numDocs) {

        double minSimilarity = 0;

        for (int i = 0; i < numDocs; i++) {
            for (int j = 0; j < numDocs; j++) {

                if (i == j)
                    continue;
                double cosim01 = getCosineSimilarity(docs[i], docs[j]);
                // System.out.println("cosim(" + i +"," + j +") = " + cosim01);
                if (minSimilarity > cosim01)
                    minSimilarity = cosim01;
                if (i == 0 && j == 1)
                    minSimilarity = cosim01;
            }
        }
        return minSimilarity;
    }

    private static double findMaxSimilarity(DocVector[] docs, int numDocs) {

        double maxSimilarity = 0;

        for (int i = 0; i < numDocs; i++) {
            for (int j = 0; j < numDocs; j++) {

                if (i == j)
                    continue;
                double cosim01 = getCosineSimilarity(docs[i], docs[j]);
                if (maxSimilarity < cosim01)
                    maxSimilarity = cosim01;
                if (i == 0 && j == 1)
                    maxSimilarity = cosim01;
            }
        }
        return maxSimilarity;
    }

    private static double findMaxSimilarity(DocVector[] docs1, DocVector[] docs2) {

        double maxSimilarity = 0;
        int numDocs1 = docs1.length;
        int numDocs2 = docs2.length;
        for (int i = 0; i < numDocs1; i++) {
            for (int j = 0; j < numDocs2; j++) {

                if (i == j)
                    continue;
                double cosim01 = getCosineSimilarity(docs1[i], docs2[j]);
                if (maxSimilarity < cosim01)
                    maxSimilarity = cosim01;
                if (i == 0 && j == 1)
                    maxSimilarity = cosim01;
            }
        }
        return maxSimilarity;
    }

    public double findAvgSimilarity(DocVector[] docs, int numDocs) {
        double similaritySum = 0;
        double cosim01 = 0;
        int count = 0;
        for (int i = 0; i < numDocs; i++) {
            for (int j = 0; j < numDocs; j++) {

                if (i == j)
                    continue;
                cosim01 = getCosineSimilarity(docs[i], docs[j]);
                similaritySum = similaritySum + cosim01;
                count++;
            }
        }
        return similaritySum / count;
    }

    private static double findMinSimilarity(DocVector[] docs1, DocVector[] docs2) {

        double minSimilarity = 0;
        int numDocs1 = docs1.length;
        int numDocs2 = docs2.length;
        for (int i = 0; i < numDocs1; i++) {
            for (int j = 0; j < numDocs2; j++) {

                if (i == j)
                    continue;
                double cosim01 = getCosineSimilarity(docs1[i], docs2[j]);
                if (minSimilarity > cosim01)
                    minSimilarity = cosim01;
                if (i == 0 && j == 1)
                    minSimilarity = cosim01;
            }
        }
        return minSimilarity;
    }

    public double findMaxCosineSimBetweenOneAndOtherDocs(DocVector[] docs,
                                                         int idOfDocToBeCompared) {

        double maxSim = 0;
        for (int i = 0; i < docs.length; i++) {
            if (i == idOfDocToBeCompared)
                continue;
            double cosim01 = getCosineSimilarity(docs[idOfDocToBeCompared],
                    docs[i]);
            if (maxSim < cosim01)
                maxSim = cosim01;
        }
        return maxSim;
    }

    public double findMinCosineSimBetweenOneAndOtherDocs(DocVector[] docs,
                                                         int idOfDocToBeCompared) {

        double minSim = 0;
        for (int i = 0; i < docs.length; i++) {
            if (i == idOfDocToBeCompared)
                continue;
            double cosim01 = getCosineSimilarity(docs[idOfDocToBeCompared],
                    docs[i]);
            if (minSim > cosim01)
                minSim = cosim01;
            if (i == 0)
                minSim = cosim01;
        }
        return minSim;
    }

    public double findAvgSimBetweenOneAndOtherDocs(DocVector[] docs,
                                                   int idOfDocToBeCompared) {

        double Sim = 0;
        for (int i = 0; i < docs.length; i++) {
            if (i == idOfDocToBeCompared)
                continue;
            double cosim01 = getCosineSimilarity(docs[idOfDocToBeCompared],
                    docs[i]);
            Sim = Sim + cosim01;
        }
        return Sim / docs.length;
    }

    /**
     * This function takes one document from the first indexDir, compares to all
     * documents in the second index dir and returns the maximum cosine
     * similarity.
     *
     * @param indexDir1
     * @param indexDir2
     * @param numDocs
     * @return
     */
    public double AnalyzeToFindMaxSimilarityAcrossCategories(String SourceDir,
                                                             String indexDir2, int numDocs) {

        double maxSim = 0;
        double minSim = 0;
        File docsdir = new File(SourceDir);
        File[] documents = docsdir.listFiles();
        double sumMaxSim = 0;
        double sumMinSim = 0;
        double avgSim = 0;
        int loopCount = 0;
        for (loopCount = 0; loopCount < documents.length; loopCount++) {
            // Get the file to be compared against a set of files in other
            // category
            System.out
                    .println("Dcoument to be comapred against other set of documents - "
                            + documents[loopCount].getAbsolutePath());
            // Add the file to the index incrementally
            IndexDocuments idx = new IndexDocuments();
            idx.incrementalIndexing(documents[loopCount],
                    indexDir2);
            //System.out.println("Contents: " + fileContent);
            // Get Id of newly added file
            int idOfDocToBeCompared = idx.getIdForContent(
                    documents[loopCount].getName(), indexDir2);
            // int idOfDocToBeCompared = 0;
            System.out.println("idOfDocToBeCompared = " + idOfDocToBeCompared);
            // Now select the newly added document using ID and compare it with
            // all other documents to get the max cosine similarity
            IndexReader reader;
            try {
                reader = IndexReader
                        .open(FSDirectory.open(new File(indexDir2)));
                TermEnum termEnum;
                termEnum = reader.terms(new Term("title"));

                Map<String, Integer> terms = new HashMap<String, Integer>();

                int pos1 = 0;
                while (termEnum.next()) {
                    Term term1 = termEnum.term();
                    if (!"title".equals(term1.field()))
                        break;
                    terms.put(term1.text(), pos1++);
                }
                // int[] docIds = new int[] {31825, 31835, 31706, 30};
                int num = reader.numDocs();
                DocVector[] docs1 = new DocVector[num];
                int i = 0;
                for (int docId = 0; docId < num; docId++) {
                    TermFreqVector[] tfvs = reader.getTermFreqVectors(docId);
                    Assert.assertTrue(tfvs.length == 1);
                    docs1[i] = new DocVector(terms);
                    for (TermFreqVector tfv : tfvs) {
                        String[] termTexts = tfv.getTerms();
                        int[] termFreqs = tfv.getTermFrequencies();
                        Assert.assertEquals(termTexts.length, termFreqs.length);
                        for (int j = 0; j < termTexts.length; j++) {
                            docs1[i].setEntry(termTexts[j], termFreqs[j]);
                        }
                    }
                    docs1[i].normalize();
                    i++;
                }

                // Call function with idOfDocToBeCompared as a separate argument
                maxSim = findMaxCosineSimBetweenOneAndOtherDocs(docs1,
                        idOfDocToBeCompared);
                System.out.println("maxSim = " + maxSim);
                minSim = findMinCosineSimBetweenOneAndOtherDocs(docs1,
                        idOfDocToBeCompared);
                avgSim = findAvgSimBetweenOneAndOtherDocs(docs1,
                        idOfDocToBeCompared);
                System.out.println("minSim = " + minSim);
                sumMinSim = sumMinSim + minSim;
                sumMaxSim = sumMaxSim + maxSim;

                // Remove the document from the index
                idx.removeDocument(documents[loopCount].getName(), indexDir2);
                System.out.println("Removed document"
                        + documents[loopCount].getName() + " from index dir "
                        + indexDir2);

                numDocs--;
                loopCount++;
                //System.gc();
            } catch (CorruptIndexException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        System.out.println("AvgMinSim = " + sumMinSim / documents.length);
        System.out.println("AvgMaxSim = " + sumMaxSim / documents.length);
        System.out.println("AvgSim = " + avgSim);
        return sumMaxSim / documents.length;
    }

    /**
     * @param fileToIndex
     * @param indexDir2
     * @return
     */
    public double maxSimBetweenNewDocAndNewsDocs(String fileToIndex,
                                                 String indexDir2) {

        double maxSim = 0;
        File file = new File(fileToIndex);
        if (file.length() == 0)
            return 0;
        IndexDocuments idx = new IndexDocuments();
        idx.incrementalIndexing(file,
                indexDir2);

        // Get Id of newly added file
        int idOfDocToBeCompared = idx.getIdForContent(
                file.getName(), indexDir2);
        // int idOfDocToBeCompared = 0;
        System.out.println("idOfDocToBeCompared = " + idOfDocToBeCompared);
        // Now select the newly added document using ID and compare it with
        // all other documents to get the max cosine similarity
        IndexReader reader;
        try {
            reader = IndexReader
                    .open(FSDirectory.open(new File(indexDir2)));
            TermEnum termEnum;
            termEnum = reader.terms(new Term("title"));

            Map<String, Integer> terms = new HashMap<String, Integer>();

            int pos1 = 0;
            while (termEnum.next()) {
                Term term1 = termEnum.term();
                if (!"title".equals(term1.field()))
                    break;
                terms.put(term1.text(), pos1++);
            }
            // int[] docIds = new int[] {31825, 31835, 31706, 30};
            int num = reader.numDocs();
            DocVector[] docs1 = new DocVector[num];
            int i = 0;
            for (int docId = 0; docId < num; docId++) {
                TermFreqVector[] tfvs = reader.getTermFreqVectors(docId);
                Assert.assertTrue(tfvs.length == 1);
                docs1[i] = new DocVector(terms);
                for (TermFreqVector tfv : tfvs) {
                    String[] termTexts = tfv.getTerms();
                    int[] termFreqs = tfv.getTermFrequencies();
                    Assert.assertEquals(termTexts.length, termFreqs.length);
                    for (int j = 0; j < termTexts.length; j++) {
                        docs1[i].setEntry(termTexts[j], termFreqs[j]);
                    }
                }
                docs1[i].normalize();
                i++;
            }

            // Call function with idOfDocToBeCompared as a separate argument
            System.out.println("Length of docs1 = " + docs1.length);
            maxSim = findMaxCosineSimBetweenOneAndOtherDocs(docs1,
                    idOfDocToBeCompared);

            // Remove the document from the index
            idx.removeDocument(file.getName(), indexDir2);
        } catch (CorruptIndexException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return maxSim;
    }

    /**
     * @param fileToIndex
     * @param indexDir2
     * @return
     */
    public double avgSimBetweenNewDocAndNewsDocs(String fileToIndex,
                                                 String indexDir2) {

        double maxSim = 0;
        File file = new File(fileToIndex);
        IndexDocuments idx = new IndexDocuments();
        idx.incrementalIndexing(file,
                indexDir2);

        // Get Id of newly added file
        int idOfDocToBeCompared = idx.getIdForContent(
                file.getName(), indexDir2);
        // int idOfDocToBeCompared = 0;
        System.out.println("idOfDocToBeCompared = " + idOfDocToBeCompared);
        // Now select the newly added document using ID and compare it with
        // all other documents to get the max cosine similarity
        IndexReader reader;
        try {
            reader = IndexReader
                    .open(FSDirectory.open(new File(indexDir2)));
            TermEnum termEnum;
            termEnum = reader.terms(new Term("title"));

            Map<String, Integer> terms = new HashMap<String, Integer>();

            int pos1 = 0;
            while (termEnum.next()) {
                Term term1 = termEnum.term();
                if (!"title".equals(term1.field()))
                    break;
                terms.put(term1.text(), pos1++);
            }
            // int[] docIds = new int[] {31825, 31835, 31706, 30};
            int num = reader.numDocs();
            DocVector[] docs1 = new DocVector[num];
            int i = 0;
            for (int docId = 0; docId < num; docId++) {
                TermFreqVector[] tfvs = reader.getTermFreqVectors(docId);
                Assert.assertTrue(tfvs.length == 1);
                docs1[i] = new DocVector(terms);
                for (TermFreqVector tfv : tfvs) {
                    String[] termTexts = tfv.getTerms();
                    int[] termFreqs = tfv.getTermFrequencies();
                    Assert.assertEquals(termTexts.length, termFreqs.length);
                    for (int j = 0; j < termTexts.length; j++) {
                        docs1[i].setEntry(termTexts[j], termFreqs[j]);
                    }
                }
                docs1[i].normalize();
                i++;
            }

            // Call function with idOfDocToBeCompared as a separate argument
            System.out.println("Length of docs1 = " + docs1.length);
            maxSim = findMaxCosineSimBetweenOneAndOtherDocs(docs1,
                    idOfDocToBeCompared);

            // Remove the document from the index
            idx.removeDocument(file.getName(), indexDir2);
        } catch (CorruptIndexException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return maxSim;
    }

    /**
     * This function takes two index dirs as arguments and compares every
     * document in the first index dir to every document in the second index dir
     * and returns the maxSim found.
     * <p>
     * This gives us an idea when compared to the minSim between docs in one
     * category to see if we can distinguish docs on one topic by basically
     * considering the similarity between them.
     *
     * @param indexDir1
     * @param indexDir2
     * @return
     */

    public double AnalyzeToFindMaxSimilarityAcrossCategories(String indexDir1,
                                                             String indexDir2) {

        double maxSimilarity = 0, minSimilarity = 0;
        try {
            IndexReader reader1;
            reader1 = IndexReader.open(FSDirectory.open(new File(indexDir1)));

            IndexReader reader2;
            reader2 = IndexReader.open(FSDirectory.open(new File(indexDir2)));

            // Need indexWriter to write the new documents so that we can unify
            // the dimention for
            // computing cosine similarity
            StopAnalyzer analyzer = new StopAnalyzer(Version.LUCENE_34);
            IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_34,
                    analyzer);
            File indexdir1 = new File(indexDir1);
            Directory index1 = new SimpleFSDirectory(indexdir1);
            File indexdir2 = new File(indexDir2);
            Directory index2 = new SimpleFSDirectory(indexdir2);
            IndexWriter w1 = new IndexWriter(index1, config);
            IndexWriter w2 = new IndexWriter(index2, config);

            // first find all terms in the index
            Map<String, Integer> terms1 = new HashMap<String, Integer>();
            Map<String, Integer> terms2 = new HashMap<String, Integer>();

            TermEnum termEnum1;

            termEnum1 = reader1.terms(new Term("title"));

            int pos1 = 0;
            while (termEnum1.next()) {
                Term term1 = termEnum1.term();
                if (!"title".equals(term1.field()))
                    break;
                terms1.put(term1.text(), pos1++);
            }
            // int[] docIds = new int[] {31825, 31835, 31706, 30};
            int numDocs = reader1.numDocs();
            DocVector[] docs1 = new DocVector[numDocs];
            int i = 0;
            for (int docId = 0; docId < numDocs; docId++) {
                TermFreqVector[] tfvs = reader1.getTermFreqVectors(docId);
                Assert.assertTrue(tfvs.length == 1);
                docs1[i] = new DocVector(terms1);
                for (TermFreqVector tfv : tfvs) {
                    String[] termTexts = tfv.getTerms();
                    int[] termFreqs = tfv.getTermFrequencies();
                    Assert.assertEquals(termTexts.length, termFreqs.length);
                    for (int j = 0; j < termTexts.length; j++) {
                        docs1[i].setEntry(termTexts[j], termFreqs[j]);
                    }
                }
                docs1[i].normalize();
                w2.addDocument(reader1.document(docId));
                i++;
            }

            // This section of code is for reading the second index dir
            int pos2 = 0;

            TermEnum termEnum2;

            termEnum2 = reader2.terms(new Term("title"));

            while (termEnum2.next()) {
                Term term2 = termEnum2.term();
                if (!"title".equals(term2.field()))
                    break;
                terms1.put(term2.text(), pos2++);
            }

            int numDocs2 = reader2.numDocs();
            DocVector[] docs2 = new DocVector[numDocs2];
            int j = 0;
            for (int docId2 = 0; docId2 < numDocs2; docId2++) {
                TermFreqVector[] tfvs = reader2.getTermFreqVectors(docId2);
                Assert.assertTrue(tfvs.length == 1);
                docs2[j] = new DocVector(terms2);
                for (TermFreqVector tfv : tfvs) {
                    String[] termTexts = tfv.getTerms();
                    int[] termFreqs = tfv.getTermFrequencies();
                    Assert.assertEquals(termTexts.length, termFreqs.length);
                    for (int k = 0; k < termTexts.length; k++) {
                        docs2[j].setEntry(termTexts[k], termFreqs[k]);
                    }
                }
                docs2[j].normalize();
                w1.addDocument(reader1.document(docId2));
                j++;
            }

            // Invoke a function that will compute cosine similarity for all
            // pairs of
            // documents and finds the smallest and greatest similarity
            maxSimilarity = findMaxSimilarity(docs1, docs2);
            minSimilarity = findMinSimilarity(docs1, docs2);
            System.out.println("maxSimilarity across two categories "
                    + indexDir1 + " and " + indexDir2 + " is = "
                    + maxSimilarity);
            System.out.println("minSimilarity across two categories "
                    + indexDir1 + " and " + indexDir2 + " is = "
                    + minSimilarity);
            reader1.close();
            reader2.close();

        } catch (CorruptIndexException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return maxSimilarity;
    }

    public double AnalyzeToFindMinSimilarity(String indexDir) {

        double minSim = 0;
        try {
            IndexReader reader;
            reader = IndexReader.open(FSDirectory.open(new File(indexDir)));

            // first find all terms in the index
            Map<String, Integer> terms = new HashMap<String, Integer>();
            TermEnum termEnum;

            termEnum = reader.terms(new Term("title"));

            int pos = 0;
            while (termEnum.next()) {
                Term term = termEnum.term();
                if (!"title".equals(term.field()))
                    break;
                terms.put(term.text(), pos++);
            }
            int numDocs = reader.numDocs();
            DocVector[] docs = new DocVector[numDocs];
            int i = 0;
            for (int docId = 0; docId < numDocs; docId++) {
                TermFreqVector[] tfvs = reader.getTermFreqVectors(docId);
                Assert.assertTrue(tfvs.length == 1);
                docs[i] = new DocVector(terms);
                for (TermFreqVector tfv : tfvs) {
                    String[] termTexts = tfv.getTerms();
                    int[] termFreqs = tfv.getTermFrequencies();
                    Assert.assertEquals(termTexts.length, termFreqs.length);
                    for (int j = 0; j < termTexts.length; j++) {
                        docs[i].setEntry(termTexts[j], termFreqs[j]);
                    }
                }
                docs[i].normalize();
                i++;
            }

            // Invoke a function that will compute cosine similarity for all
            // pairs of
            // documents and finds the smallest and greatest similarity
            //double maxSimilarity = findMaxSimilarity(docs, numDocs);
            double minSimilarity = findMinSimilarity(docs, numDocs);

            //System.out.println("maxSimilarity = " + maxSimilarity);
            System.out.println("minSimilarity = " + minSimilarity);

            reader.close();
            minSim = minSimilarity;

        } catch (CorruptIndexException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return minSim;
    }

    public double AnalyzeToFindMaxSimilarity(String indexDir) {

        double maxSim = 0;
        try {
            IndexReader reader;
            reader = IndexReader.open(FSDirectory.open(new File(indexDir)));

            // first find all terms in the index
            Map<String, Integer> terms = new HashMap<String, Integer>();
            TermEnum termEnum;

            termEnum = reader.terms(new Term("title"));

            int pos = 0;
            while (termEnum.next()) {
                Term term = termEnum.term();
                if (!"title".equals(term.field()))
                    break;
                terms.put(term.text(), pos++);
            }
            int numDocs = reader.numDocs();
            DocVector[] docs = new DocVector[numDocs];
            int i = 0;
            for (int docId = 0; docId < numDocs; docId++) {
                TermFreqVector[] tfvs = reader.getTermFreqVectors(docId);
                Assert.assertTrue(tfvs.length == 1);
                docs[i] = new DocVector(terms);
                for (TermFreqVector tfv : tfvs) {
                    String[] termTexts = tfv.getTerms();
                    int[] termFreqs = tfv.getTermFrequencies();
                    Assert.assertEquals(termTexts.length, termFreqs.length);
                    for (int j = 0; j < termTexts.length; j++) {
                        docs[i].setEntry(termTexts[j], termFreqs[j]);
                    }
                }
                docs[i].normalize();
                i++;
            }

            // Invoke a function that will compute cosine similarity for all
            // pairs of
            // documents and finds the smallest and greatest similarity
            double maxSimilarity = findMaxSimilarity(docs, numDocs);
            //double minSimilarity = findMinSimilarity(docs, numDocs);

            System.out.println("maxSimilarity = " + maxSimilarity);
            //System.out.println("minSimilarity = " + minSimilarity);

            reader.close();
            maxSim = maxSimilarity;

        } catch (CorruptIndexException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return maxSim;
    }

    public double AnalyzeToFindAvgSimilarity(String indexDir) {
        double avgSim = 0;
        int numDocs = 0;
        try {
            IndexReader reader;
            reader = IndexReader.open(FSDirectory.open(new File(indexDir)));

            // first find all terms in the index
            Map<String, Integer> terms = new HashMap<String, Integer>();
            TermEnum termEnum;

            termEnum = reader.terms(new Term("title"));

            int pos = 0;
            while (termEnum.next()) {
                Term term = termEnum.term();
                if (!"title".equals(term.field()))
                    break;
                terms.put(term.text(), pos++);
            }
            numDocs = reader.numDocs();
            DocVector[] docs = new DocVector[numDocs];
            int i = 0;
            for (int docId = 0; docId < numDocs; docId++) {
                TermFreqVector[] tfvs = reader.getTermFreqVectors(docId);
                Assert.assertTrue(tfvs.length == 1);
                docs[i] = new DocVector(terms);
                for (TermFreqVector tfv : tfvs) {
                    String[] termTexts = tfv.getTerms();
                    int[] termFreqs = tfv.getTermFrequencies();
                    Assert.assertEquals(termTexts.length, termFreqs.length);
                    for (int j = 0; j < termTexts.length; j++) {
                        docs[i].setEntry(termTexts[j], termFreqs[j]);
                    }
                }
                docs[i].normalize();
                i++;
            }

            // Invoke a function that will compute cosine similarity for all
            // pairs of
            // documents and finds the smallest and greatest similarity
            avgSim = findAvgSimilarity(docs, numDocs);
            System.out.println("avgSim = " + avgSim);
            reader.close();
        } catch (CorruptIndexException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return avgSim;
    }

    public static void main(String args[]) {

        AnalyzeDocuments adcs = new AnalyzeDocuments();
        double minSim = adcs.AnalyzeToFindMinSimilarity("data/index");
        System.out.println("minSim = " + minSim);
    }

}

class DocVector {
    public Map<String, Integer> terms;
    public SparseRealVector vector;

    public DocVector(Map<String, Integer> terms) {
        this.terms = terms;
        this.vector = new OpenMapRealVector(terms.size());
    }

    public void setEntry(String term, int freq) {
        if (terms.containsKey(term)) {
            int pos = terms.get(term);
            vector.setEntry(pos, (double) freq);
        }
    }

    public void normalize() {
        double sum = vector.getL1Norm();
        vector = (SparseRealVector) vector.mapDivide(sum);
    }

    public String toString() {
        RealVectorFormat formatter = new RealVectorFormat();
        return formatter.format(vector);
    }
}
