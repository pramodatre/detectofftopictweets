package prj.offtopic.tweets;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author pramod anantharam
 */

public class HelloLucene {
  public static void main(String[] args) throws IOException, ParseException {
    // 0. Specify the analyzer for tokenizing text.
    //    The same analyzer should be used for indexing and searching
    StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_34);

    // 1. create the index
    //Directory index = new RAMDirectory();
    File indexFile = new File("data/index");
    Directory index = new SimpleFSDirectory(indexFile);

    IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_34, analyzer);

    IndexWriter w = new IndexWriter(index, config);
    addDoc(w, "RT @umenumen: Subhanallah.. Christian protesters protecting Muslims during their prayers in #Tahrir", "1234343431434");
    addDoc(w, "RT @monaeltahawy: We had respect for #Egypt armed forces for position till today. Day of shame as they watched and did nothing. #Egypt w ...", "1234343431435");
    addDoc(w, "RT @litfreak: 7aram 3aleiko, my people are dying! MY PEOPLE ARE DYING! #Jan25 #Egypt", "1234343431436");
    addDoc(w, "@Maddow, @RichardEngelNBC Tank smokescreen meant to prevent aiming, allowing leaving undercover, clear people #Egypt", "1234343431437");
    w.close();

    // 2. query
    String querystr = args.length > 0 ? args[0] : "protesters";

    // the "title" arg specifies the default field to use
    // when no field is explicitly specified in the query.
    Query q = new QueryParser(Version.LUCENE_34, "title", analyzer).parse(querystr);

    // 3. search
    int hitsPerPage = 10;
    IndexSearcher searcher = new IndexSearcher(index, true);
    TopScoreDocCollector collector = TopScoreDocCollector.create(hitsPerPage, true);
    searcher.search(q, collector);
    ScoreDoc[] hits = collector.topDocs().scoreDocs;
    
    // 4. display results
    System.out.println("Found " + hits.length + " hits.");
    for(int i=0;i<hits.length;++i) {
      int docId = hits[i].doc;
      Document d = searcher.doc(docId);
      System.out.println((i + 1) + ". " + d.get("id"));
    }

    // searcher can only be closed when there
    // is no need to access the documents any more. 
    searcher.close();
  }

  private static void addDoc(IndexWriter w, String value, String fileName) throws IOException {
    Document doc = new Document();
    doc.add(new Field("title", value, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
    doc.add(new Field("id", fileName, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
    w.addDocument(doc);
  }
}
