package prj.offtopic.tweets;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.lucene.queryParser.ParseException;

/**
 * This class will analyze a set of news documents to find the minmum similarity between any two documents
 * that belong to the same class. Ideal input for this algorithm will be a gold standard data set like
 * 20 news dataset. If the destination directory has many directories, all the directories will be analyzed.
 *
 * @author pramod anantharam
 */

public class ThresholdAnalyzer {

    /**
     * This function will not create fresh index, but will analyze an existin index and returns the result.
     * <p>
     * TODO: make the index dir a config file parameter
     *
     * @return AverageMinMatch - the average of minimum match across all categories = (minMatchOfAllCategories / number of categories).
     */
    public double findAverageMinMatchWithoutIndexing() {

        // Get all sub-directories in the directory to be indexed /home/pramod/Desktop/20_newsgroup
        File dir = new File("/Users/pramod/Pramod/Datasets/20_newsgroup");
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        File[] dirs = dir.listFiles(fileFilter);

        double totalSimValue = 0;
        // Now, analyze indexed documents to find minSimilarity
        for (int i = 0; i < dirs.length; i++) {

            AnalyzeDocuments adcs = new AnalyzeDocuments();
            String subDir = dirs[i].getName();
            String indexDir = "data/index/" + subDir;
            double minSim = adcs.AnalyzeToFindMinSimilarity(indexDir);
            totalSimValue = totalSimValue + minSim;
            System.out.println("minSim for " + indexDir + " is = " + minSim);
        }
        System.out.println("Average minimum similarity for all the categories = " + totalSimValue / dirs.length);

        return totalSimValue / dirs.length;
    }

    public double findAverageMinMaxAvgWithoutIndexing() {
        // Get all sub-directories in the directory to be indexed /home/pramod/Desktop/20_newsgroup
        File dir = new File("/Users/pramod/Pramod/Datasets/20_newsgroup");
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        File[] dirs = dir.listFiles(fileFilter);

        double totalMinSimValue = 0;
        double totalMaxSimValue = 0;
        double totalAvgSimValue = 0;
        // Now, analyze indexed documents to find minSimilarity
        for (int i = 0; i < dirs.length; i++) {

            AnalyzeDocuments adcs = new AnalyzeDocuments();
            String subDir = dirs[i].getName();
            String indexDir = "data/index/" + subDir;
            double minSim = adcs.AnalyzeToFindMinSimilarity(indexDir);
            double maxSim = adcs.AnalyzeToFindMaxSimilarity(indexDir);
            double avgSim = adcs.AnalyzeToFindAvgSimilarity(indexDir);

            totalMinSimValue = totalMinSimValue + minSim;
            totalMaxSimValue = totalMaxSimValue + maxSim;
            totalAvgSimValue = totalAvgSimValue + avgSim;
            System.out.println("minSim for " + indexDir + " is = " + minSim);
            System.out.println("maxSim for " + indexDir + " is = " + maxSim);
            System.out.println("avgSim for " + indexDir + " is = " + avgSim);
        }
        System.out.println("Average minimum similarity for all the categories = " + totalMinSimValue / dirs.length);
        System.out.println("Average max similarity for all the categories = " + totalMaxSimValue / dirs.length);
        System.out.println("Average similarity for all the categories = " + totalAvgSimValue / dirs.length);

        return totalMinSimValue / (dirs.length - 1);
    }

    public double findAverageMaxMatchWithoutIndexing() {

        // Gel all sub-directories in the directory to be indexed /home/pramod/Desktop/20_newsgroup
        File dir = new File("/Users/pramod/Pramod/Datasets/20_newsgroup");
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        File[] dirs = dir.listFiles(fileFilter);

        double totalSimValue = 0;
        // Now, analyze indexed documents to find minSimilarity
        for (int i = 0; i < dirs.length; i++) {

            AnalyzeDocuments adcs = new AnalyzeDocuments();
            String subDir = dirs[i].getName();
            String indexDir = "data/index/" + subDir;
            double minSim = adcs.AnalyzeToFindMaxSimilarity(indexDir);
            totalSimValue = totalSimValue + minSim;
            System.out.println("minSim for " + indexDir + " is = " + minSim);
        }
        System.out.println("Average minimum similarity count for all the categories = " + totalSimValue / dirs.length);

        return totalSimValue / dirs.length;
    }

    public double findAverageSimWithoutIndexing() {
        // Gel all sub-directories in the directory to be indexed /home/pramod/Desktop/20_newsgroup
        File dir = new File("/Users/pramod/Pramod/Datasets/20_newsgroup");
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        File[] dirs = dir.listFiles(fileFilter);

        double totalSimValue = 0;
        // Now, analyze indexed documents to find minSimilarity
        for (int i = 0; i < dirs.length; i++) {

            AnalyzeDocuments adcs = new AnalyzeDocuments();
            String subDir = dirs[i].getName();
            String indexDir = "data/index/" + subDir;
            double avgSim = adcs.AnalyzeToFindAvgSimilarity(indexDir);
            totalSimValue = totalSimValue + avgSim;
            System.out.println("minSim for " + indexDir + " is = " + avgSim);
        }
        System.out.println("Average minimum similarity for all the categories = " + totalSimValue / dirs.length);

        return totalSimValue / dirs.length;
    }

    public void IndexAllDocs() {
        // Gel all sub-directories in the directory to be indexed /home/pramod/Desktop/20_newsgroup
        File dir = new File("/Users/pramod/Pramod/Datasets/20_newsgroup");
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        File[] dirs = dir.listFiles(fileFilter);
        // loop through all subdirs indexing them
        for (int i = 0; i < dirs.length; i++) {
            IndexDocuments idx = new IndexDocuments();
            String docsDir = dirs[i].getAbsolutePath();
            String subDir = dirs[i].getName();
            String indexDir = "data/index/" + subDir;
            try {
                idx.indexAllDocuments(docsDir, indexDir);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    /**
     * This function will index all documents
     *
     * @return
     */
    public double IndexAndFindAverageMinMatch() {

        // Gel all sub-directories in the directory to be indexed /home/pramod/Desktop/20_newsgroup
        File dir = new File("/Users/pramod/Pramod/Datasets/20_newsgroup");
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File file) {
                return file.isDirectory();
            }
        };
        File[] dirs = dir.listFiles(fileFilter);
        // loop through all subdirs indexing them
        for (int i = 0; i < dirs.length; i++) {
            IndexDocuments idx = new IndexDocuments();
            String docsDir = dirs[i].getAbsolutePath();
            String subDir = dirs[i].getName();
            String indexDir = "data/index/" + subDir;
            try {
                idx.indexAllDocuments(docsDir, indexDir);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        double totalSimValue = 0;
        // Now, analyze indexed documents to find minSimilarity
        for (int i = 0; i < dirs.length; i++) {

            AnalyzeDocuments adcs = new AnalyzeDocuments();
            String docsDir = dirs[i].getAbsolutePath();
            String subDir = dirs[i].getName();
            String indexDir = "data/index/" + subDir;
            double minSim = adcs.AnalyzeToFindMinSimilarity(indexDir);
            totalSimValue = totalSimValue + minSim;
            System.out.println("minSim for " + indexDir + " is = " + minSim);
        }
        System.out.println("Average minimum similarity count for all the categories = " + totalSimValue / dirs.length);

        return totalSimValue / dirs.length;
    }

    public static void main(String args[]) {

        ThresholdAnalyzer tan = new ThresholdAnalyzer();
        tan.findAverageMinMatchWithoutIndexing();
    }

}
