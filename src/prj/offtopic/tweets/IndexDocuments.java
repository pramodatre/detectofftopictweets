package prj.offtopic.tweets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Scanner;

import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.apache.lucene.document.Field;

/**
 * This class expects two arguments: indexDir - place to store the created
 * index. docsDir - place where all the documents to be indexed are placed.
 *
 * @author pramod anantharam
 */

public class IndexDocuments {

    private static void addDoc(IndexWriter w, String value, String id)
            throws IOException {
        Document doc = new Document();
        doc.add(new Field("title", value, Field.Store.YES,
                Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
        doc.add(new Field("id", id, Field.Store.YES, Field.Index.NOT_ANALYZED,
                Field.TermVector.NO));
        w.addDocument(doc);
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

    public void indexAllDocuments(String docsDir, String indexDir)
            throws IOException, ParseException {

        StopAnalyzer analyzer = new StopAnalyzer(Version.LUCENE_34);
        // 1. create the index
        // Directory index = new RAMDirectory();
        File docsdir = new File(docsDir);
        File indexdir = new File(indexDir);
        Directory index = new SimpleFSDirectory(indexdir);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_34,
                analyzer);
        IndexWriter w = new IndexWriter(index, config);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".txt");
            }
        };
        // File[] textFiles = docsdir.listFiles(filter);
        File[] textFiles = docsdir.listFiles();
        // Add documents to the index
        for (int i = 0; i < textFiles.length; i++) {
            // Reader textReader = new FileReader(textFiles[i]);
            String fileContents = readFile(textFiles[i].getAbsolutePath());
            // Document document = new Document();
            System.out.println("Indexing file: "
                    + textFiles[i].getAbsolutePath());
            addDoc(w, fileContents, textFiles[i].getName());
        }
        w.close();
    }

    /**
     * Remove a document from index given its ID
     *
     * @param idOfDocToRemove
     * @param indexDir2
     */
    public void removeDocument(String idOfDocToRemove, String indexDir) {

        StopAnalyzer analyzer = new StopAnalyzer(Version.LUCENE_34);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_34,
                analyzer);
        File indexdir = new File(indexDir);
        Directory index;
        try {
            index = new SimpleFSDirectory(indexdir);
            IndexWriter w = new IndexWriter(index, config);
            Term idTerm = new Term("id", idOfDocToRemove);
            w.deleteDocuments(idTerm);
            // w.commit();
            w.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * This function will take a list of file pointers pointing to the files to
     * be indexed and the location of the index directory.
     *
     * @param files
     * @param indexDir
     */
    public String incrementalIndexing(File file, String indexDir) {

        String fileContents = "";
        StopAnalyzer analyzer = new StopAnalyzer(Version.LUCENE_34);
        IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_34,
                analyzer);
        File indexdir = new File(indexDir);
        Directory index;
        try {
            index = new SimpleFSDirectory(indexdir);
            IndexWriter w = new IndexWriter(index, config);
            fileContents = readFile(file.getAbsolutePath());
            addDoc(w, fileContents, file.getName());
            w.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return fileContents;
    }

    public int getIdForContent(String fileName, String indexDir) {

        int internalId = 0;
        File indexdir = new File(indexDir);
        try {
            Directory index = new SimpleFSDirectory(indexdir);
            // Query q = new QueryParser(Version.LUCENE_34, "title",
            // analyzer).parse(fileContent);
            // TopScoreDocCollector collector = TopScoreDocCollector.create(1,
            // true);
            IndexSearcher searcher = new IndexSearcher(index, true);
            // searcher.search(q, collector);
            // ScoreDoc[] hits = collector.topDocs().scoreDocs;
            // id = hits[0].doc;
            System.out.println("Looking for " + fileName);
            TopDocs results = searcher.search(new TermQuery(new Term("id",
                    fileName)), 1);
            System.out.println(results.scoreDocs.length);
            internalId = results.scoreDocs[0].doc;
            searcher.close();
        } catch (CorruptIndexException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return internalId;
    }

    public static void main(String[] args) {
        IndexDocuments indexdocs = new IndexDocuments();
        try {
            indexdocs.indexAllDocuments("data/inbox", "data/index");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
