package prj.offtopic.tweets;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * This class will process continuous stream of tweets from a directory.
 * It adds the tweets in the inbox to the index as and when the tweets arrive.
 * For every N newly arrived tweet, we compute cosine similarity of newly arrived docs with average minSim from news docs.
 * All tweets with cosine similarity less than average minSim will be tagged off-topic and moved to off-topic folder and move
 * the tweets that are ontopic to ontopic dir.
 * <p>
 * If the system is operating on a stream, just the off-topic tweets will be picked and placed in the off-topic dir.
 *
 * @author pramod anantharam
 */
public class ProcessTwitterStream {
    /**
     * This function processes the tweets placed in the inbox
     */
    public void processTweetsFromInbox() {

        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("./config/config.properties"));
            String inboxDir = properties.getProperty("inbox");
            File inbox = new File(inboxDir);
            FileFilter filter = new FileFilter() {
                public boolean accept(File dir) {
                    return dir.isFile();
                }
            };
            File[] fileList = inbox.listFiles(filter);
            // Iterate through the list of files (tweets) in the inbox
            for (int count = 0; count < fileList.length; count++) {

                ProcessTweet pt = new ProcessTweet(fileList[count].getAbsolutePath());
                System.out.println(pt.getTweetText() + " " + pt.getResolvedURL() + " " + pt.getUrlContent());

                // Save the document to a file depending the source of link - news docs are saved to dir named data/newsdocs

                // tweet with news document is moved to ontopic dir

                // Update the max, min and avg similaraty of news docs (lucene index) -- can be done once is a while

                // If the document is not a news document, then we run the test of cosinesimilarity -- by adding this new doc to index

                // remove the doc from index (as it is not a news document)

                // if cosinesimilarity of new link with existing news links > minSim (from news docs for this event), the tweet is on-topic (moved to ontopic dir)
                // else tweet is off-topic (moved to offtopic dir)
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String args[]) {

        ProcessTwitterStream pts = new ProcessTwitterStream();
        //ThresholdAnalyzer tan = new ThresholdAnalyzer();
        // Just index all docs withot doing any analysis
        //tan.IndexAllDocs();
        // Find the average minSim between all the 20 news dataset documents
        //double avgMinSim = tan.IndexAndFindAverageMinMatch();
//		double avgMinSim = tan.findAverageMinMatchWithoutIndexing();
//		System.out.println("avgMinSim = " + avgMinSim); 
//		double avgMaxSim = tan.findAverageMaxMatchWithoutIndexing();
//		System.out.println("avgMinSim = " + avgMaxSim); 
//		double avgSim = tan.findAverageSimWithoutIndexing();
//		System.out.println("avgMinSim = " + avgSim); 
        //tan.findAverageMinMaxAvgWithoutIndexing();
        //tan.IndexAllDocs();

        //AnalyzeDocuments adcs = new AnalyzeDocuments();
        //adcs.AnalyzeToFindMaxSimilarityAcrossCategories("/Users/pramod/Pramod/Datasets/20_newsgroup/talk.politics.guns", "data/index/talk.politics.misc", 1);
        //System.out.println("avgMaxSim = " + avgMaxSim);
        // Process tweets in inbox by
        pts.processTweetsFromInbox();

    }
}
