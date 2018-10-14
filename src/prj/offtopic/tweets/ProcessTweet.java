package prj.offtopic.tweets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.queryParser.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;

import argo.jdom.JdomParser;

/**
 * This class will process a single tweet by: 1. Extracting the URL from the
 * tweet 2. Resolve the URL 3. Check if it is news URL and if so, grab the
 * contents of the page pointed to by the URL 4. If URL is not a news URL, check
 * if its cosine similarity with the news documents
 *
 * @author pramod anantharam
 */
public class ProcessTweet {

    private static final JdomParser JDOM_PARSER = new JdomParser();
    private List<String> URLs;
    private String resolvedURL;
    private String urlContent;
    private String tweetContent;
    private double currentMaxSim;
    private boolean pageContentEmpty;

    public double getCurrentMaxSim() {
        return currentMaxSim;
    }

    public void setCurrentMaxSim(double currentMaxSim) {
        this.currentMaxSim = currentMaxSim;
    }

    public double getCurrentMinSim() {
        return currentMinSim;
    }

    public void setCurrentMinSim(double currentMinSim) {
        this.currentMinSim = currentMinSim;
    }

    public double getCurrentAvgSim() {
        return currentAvgSim;
    }

    public void setCurrentAvgSim(double currentAvgSim) {
        this.currentAvgSim = currentAvgSim;
    }

    private double currentMinSim;
    private double currentAvgSim;

    public String getTweetContent() {
        return tweetContent;
    }

    public void setTweetContent(String tweetContent) {
        this.tweetContent = tweetContent;
    }

    private boolean newsflag = false;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isNewsflag() {
        return newsflag;
    }

    public void setNewsflag(boolean newsflag) {
        this.newsflag = newsflag;
    }

    public String getUrlContent() {
        return urlContent;
    }

    public void setUrlContent(String urlContent) {
        this.urlContent = urlContent;
    }

    public List<String> getURLs() {
        return URLs;
    }

    public void setURLs(List<String> uRLs) {
        URLs = uRLs;
    }

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }

    private String tweetText = "";

    public String getResolvedURL() {
        return resolvedURL;
    }

    public void setResolvedURL(String resolvedURL) {
        this.resolvedURL = resolvedURL;
    }

    /**
     * Read the contents of the given file.
     */
    public String readFile(String fFileName) throws IOException {
        StringBuilder text = new StringBuilder();
        String NL = System.getProperty("line.separator");
        Scanner scanner = new Scanner(new FileInputStream(fFileName));
        try {
            while (scanner.hasNextLine()) {
                text.append(scanner.nextLine() + NL);
            }
        } finally {
            scanner.close();
        }
        return text.toString();
    }

    public List<String> getURLFromText(String tweetText) {

        List<String> result = new ArrayList<String>();
        Pattern pattern = Pattern.compile("(http://[a-z]*.[a-z]*/\\w*)");
        Matcher matcher = pattern.matcher(tweetText);
        while (matcher.find()) {
            result.add(matcher.group());
        }
        return result;
    }

    /**
     * This function resolves bit.ly URLs
     *
     * @param inURLs
     * @return
     */
    public boolean resolveURLs(List<String> inURLs) {

        // System.out.println(" In the resolveURLs method ... ");
        // List<String> resolvedURLs = null;
        String resolvedURL = "";
        Iterator<String> iturl = inURLs.iterator();
        while (iturl.hasNext()) {

            String url = iturl.next();
            if (!url.contains("bit") && url.length() > 28) {
                // return url;
                this.setResolvedURL(resolvedURL);
                return true;
            }

            // System.out.println("URL found is: " + url);
            // if (url.contains("bit")) {
            // if the URL is a short URL, resolve them

            // Construct data
            String data;
            try {
                data = URLEncoder.encode("login", "UTF-8") + "="
                        + URLEncoder.encode("pramodatre", "UTF-8");
                data += "&"
                        + URLEncoder.encode("apiKey", "UTF-8")
                        + "="
                        + URLEncoder.encode(
                        "R_4a6c9ac9d3c61205e3fe291847b33019", "UTF-8");
                data += "&" + URLEncoder.encode("shortUrl", "UTF-8") + "="
                        + URLEncoder.encode(url, "UTF-8");

                // Send data
                URL requestUrl = new URL("http://api.bitly.com/v3/expand");
                URLConnection conn = requestUrl.openConnection();
                conn.setDoOutput(true);
                OutputStreamWriter wr = new OutputStreamWriter(conn
                        .getOutputStream());
                // System.out.println(data);
                wr.write(data);
                wr.flush();

                // Get the response
                BufferedReader rd = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                String line;
                StringBuffer sbuff = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    // Process line...
                    System.out.println(line);
                    sbuff.append(line);
                }
                wr.close();
                rd.close();

                JSONObject response = new JSONObject(sbuff.toString());
                JSONArray expand = response.getJSONObject("data").getJSONArray("expand");
                for (int i = 0; i < expand.length(); i++) {
                    JSONObject obj = expand.getJSONObject(i);
                    if (obj.has("error")) {
                        return false;
                    } else if (obj.has("long_url")) {
                        this.setResolvedURL(obj.getString("long_url"));
                        return true;
                    }
                }

//				Set<String> fieldNames = new HashSet<String>();
//				StajParser stajParser = null;
//				Reader jsonReader = new StringReader(sbuff.toString());
//				try {
//					stajParser = new StajParser(jsonReader);
//					try {
//						while (stajParser.hasNext()) {
//							if (stajParser.next() == JsonStreamElementType.STRING
//									|| stajParser.next() == JsonStreamElementType.START_FIELD) {
//								fieldNames.add(stajParser.getText());
//								// System.out.println(stajParser.getText());
//							}
//						}
//					} catch (JsonStreamException e) {
//						// TODO Auto-generated catch block
//						e.printStackTrace();
//					}
//				} finally {
//					if (stajParser != null) {
//						stajParser.close();
//					}
//				}
//				
//				if(fieldNames.contains("NOT_FOUND")) 
//					return false;
//				
//				Iterator<String> it = fieldNames.iterator();
//				while (it.hasNext()) {
//					System.out.println("resolvedURL = " + resolvedURL); 
//					resolvedURL = it.next();
//					if(resolvedURL.contains("NOT_FOUND")){
//						return false;
//					}
//					else if (resolvedURL.contains("http")
//							&& !resolvedURL.contains("bit")) {
//						System.out.println("resolvedURL = " + resolvedURL);
//						// return resolvedURL;
//						this.setResolvedURL(resolvedURL);
//					}
//				}
//
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }

    public boolean isThisNewsURL(String resolvedURL) {

        boolean newsflag = false;
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream("./config/config.properties"));
            String newsSources = properties.getProperty("NewsSources");
            StringTokenizer stok = new StringTokenizer(newsSources, ",");
            while (stok.hasMoreTokens()) {
                String newsDomain = stok.nextToken();
                System.out.println("Cecking for " + newsDomain);
                if (this.getResolvedURL().contains(newsDomain))
                    newsflag = true;
            }
            this.setNewsflag(newsflag);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return newsflag;
    }

    /**
     * This function will move the current tweet to the specified location
     *
     * @param newLoc
     * @throws IOException
     */
    public void moveThisTweet(String newLoc) throws IOException {
        File tweetFile = new File(newLoc);
        System.out.println(newLoc);
        Writer tweet = new BufferedWriter(new FileWriter(tweetFile));
        tweet.write(this.getTweetText());
        tweet.flush();
        // Delete
        File oldFile = new File(this.getId());
        oldFile.delete();
    }

    // Deletes all files and subdirectories under dir.
    // Returns true if all deletions were successful.
    // If a deletion fails, the method stops attempting to delete and returns false.
    public static boolean deleteDir(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return dir.delete();
    }

    /**
     * This function will do: if the newsFlag is true save the document pointed
     * to by the URL to data/newsdocs (file name can be -- tweetID-news)
     * re-index the documents in data/newsdocs to find min, avg, and max
     * similarity (can be done once in a while -- config file setting) move the
     * tweet to ontopic dir else save the document to data/nonnewsdocs
     * (tweetID-nonnews) add the document to index and find its avg similarity
     * with other docs (news docs are present in the index) if avgSim > minSim
     * b/w news docs move the tweet to ontopic dir remove the document from the
     * index (option that can be set in the config file) else move the tweet to
     * offtopic dir remove the document from the index (option that can be set
     * in the config file)
     */
    public void saveAnalyzeAndMoveFiles() {


        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream("./config/config.properties"));

            if (this.isNewsflag() && !this.pageContentEmpty) {
                String newsDocsDir = properties.getProperty("newsDocs");
                File aFile = new File(newsDocsDir
                        + "/"
                        + this.getId().substring(this.getId().lastIndexOf("/"),
                        this.getId().length()) + "-news");
                System.out.println("Contents will be saved to : " + aFile);
                Writer output = new BufferedWriter(new FileWriter(aFile));
                output.write(this.getUrlContent());
                output.flush();
                // Move the tweet to ontopic dir
                String ontopicDir = properties.getProperty("ontopicDir");
                this.moveThisTweet(ontopicDir
                        + this.getId().substring(this.getId().lastIndexOf("/"),
                        this.getId().length()));

                // Build/Update the index
                // Here, the index is built fresh if it does not exist or it is
                // updated with newly arrived documents
                // To keep it simple - the index will be overwritten every time
                // new doc arrives
                File indexdir = new File(properties.getProperty("newsIndex"));
                if (deleteDir(indexdir)) {
                    System.out.println("Deleted " + indexdir.getAbsolutePath());
                } else {
                    System.out.println("Could not delete " + indexdir.getAbsolutePath());
                }
                IndexDocuments idx = new IndexDocuments();
                idx.indexAllDocuments(properties.getProperty("newsDocs"),
                        properties.getProperty("newsIndex"));

                // compute min, max, and avg similarity between news docs
                // TODO Instead of setting a variable, we can store it in the properties file? so that it is persistent
                //      for every new instance of this class created.
                AnalyzeDocuments adcs = new AnalyzeDocuments();
                this.setCurrentMaxSim(adcs.AnalyzeToFindMaxSimilarity(indexdir
                        .getAbsolutePath()));
                this.setCurrentMinSim(adcs.AnalyzeToFindMinSimilarity(indexdir
                        .getAbsolutePath()));
                this.setCurrentAvgSim(adcs.AnalyzeToFindAvgSimilarity(indexdir
                        .getAbsolutePath()));

            } else { // If the document is not a news source
                // collect the document
                if (!this.pageContentEmpty) {

                    File indexdirnews = new File(properties.getProperty("newsIndex"));
                    // compute min, max, and avg similarity between news docs
                    // TODO Remove this computation since this is an overheard
                    AnalyzeDocuments adcsnews = new AnalyzeDocuments();
                    this.setCurrentMaxSim(adcsnews.AnalyzeToFindMaxSimilarity(indexdirnews
                            .getAbsolutePath()));
                    this.setCurrentMinSim(adcsnews.AnalyzeToFindMinSimilarity(indexdirnews
                            .getAbsolutePath()));
                    this.setCurrentAvgSim(adcsnews.AnalyzeToFindAvgSimilarity(indexdirnews
                            .getAbsolutePath()));

                    String nonnewsdocs = properties.getProperty("nonnewsDocs");

                    File tweetFile = new File(nonnewsdocs
                            + this.getId().substring(this.getId().lastIndexOf("/"),
                            this.getId().length()) + "-doc");

                    Writer tweet = new BufferedWriter(new FileWriter(tweetFile));
                    tweet.write(this.getUrlContent());
                    tweet.flush();


                    // Check if there are some news documents indexed
                    File indexdir = new File(properties.getProperty("newsIndex"));
                    if (indexdir.exists()) { // If index exists, index this new
                        // document and compare it with rest
                        // of the docs
                        AnalyzeDocuments adcs = new AnalyzeDocuments();
                        double maxSimNewAndNewsDocs = adcs
                                .maxSimBetweenNewDocAndNewsDocs(tweetFile
                                        .getAbsolutePath(), indexdir
                                        .getAbsolutePath());

                        // TODO see if this hypothesis works --
                        // Finding AvgSim between new document and rest of the news document is better than
                        // finding maxSim between new document and rest if the news documents.

                        System.out.println("maxSimNewAndNewsDocs = "
                                + maxSimNewAndNewsDocs);
                        System.out.println("Comparing  maxSimNewAndNewsDocs = " + maxSimNewAndNewsDocs + " and " + " avgSimAmongNewsDocs = " + this.getCurrentAvgSim());
                        if (maxSimNewAndNewsDocs >= this.getCurrentAvgSim()) { // if
                            // the
                            // new
                            // document
                            // is
                            // greater
                            // than
                            // AvgSim/minSim
                            // among
                            // the
                            // news
                            // docs
                            // We will tag the tweet as ontopic
                            String ontopicDir = properties
                                    .getProperty("ontopicDir");
                            this.moveThisTweet(ontopicDir
                                    + this.getId().substring(
                                    this.getId().lastIndexOf("/"),
                                    this.getId().length()));
                        } else { // tweet is pointing to an offtopic link
                            String offtopicDir = properties.getProperty("offtopicDir");
                            this.moveThisTweet(offtopicDir
                                    + this.getId().substring(
                                    this.getId().lastIndexOf("/"),
                                    this.getId().length()));
                        }
                    }

                } else { // No news docs found till now and we need to wait
                    // before we can test link contents
                    // As it can be noticed that this situation must be avoided
                    // since we are processing a tweet in one shot.
                    // There should be at least one news document so that we can
                    // process the new docs
                    // If not, these docs will be ignored.
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ProcessTweet(String tweetLocation) {

        String fileContents;
        try {
            this.setId(tweetLocation);
            System.out.println("Processing: " + tweetLocation + "\n");
            fileContents = this.readFile(tweetLocation);
            this.setTweetText(fileContents);
            // Extract URL
            List<String> uRLs = this.getURLFromText(fileContents);
            this.setURLs(uRLs);
            // Resolve the URL
            boolean resolveResult = this.resolveURLs(uRLs);
            System.out.println("resolveResult flag = " + resolveResult);
            // System.out.println(resolvedUrl);
            // this.setResolvedURL(resolvedUrl);
            // Get contents of the document pointed to by the URL

            // If the URL cannot be resolved, we will ignore the tweet
            if (resolveResult) {
                MetallClient mc = new MetallClient();
                try {
                    String pageContent = mc.cleartext(this.getResolvedURL());
                    System.out.println("pageContent = " + pageContent);
                    if (pageContent.length() < 20) {
                        this.pageContentEmpty = true;
                    }
                    // Remove all the new lines from the string
                    StringBuilder result = new StringBuilder();
                    StringTokenizer t = new StringTokenizer(pageContent, "\n");
                    while (t.hasMoreTokens()) {
                        result.append(t.nextToken().trim()).append("");
                    }
                    this.setUrlContent(result.toString());
                } catch (MetallClient.MetallClientException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                // If news source, collect it is a directory which is later on
                this.isThisNewsURL(this.getResolvedURL());
                this.saveAnalyzeAndMoveFiles();
                // indexed (after certain number of docs are collected)
            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        ProcessTweet pt = new ProcessTweet(
                "/home/pramod/workspaceNew/offtopicContentDetection/data/inbox/32963805436715008");
        // System.out.println(pt.getTweetText());
        System.out
                .println("=============== tweet and its processed data =============");
        System.out.println(pt.getTweetText() + " " + pt.getResolvedURL() + " "
                + pt.getUrlContent() + " " + pt.isNewsflag());
        // pt.resolveURLs(urls);
        // Iterator<String> iturl = urls.iterator();
        // while(iturl.hasNext()){
        // System.out.println("URL found is: " + iturl.next());
        // }
    }
}
