package prj.offtopic.tweets;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.store.FSDirectory;

/**
 *
 * @author pramod anantharam
 */

public class ComputeCosineSimilarity {

    public static void main(String args[]) {
        IndexReader reader;
        try {
            reader = IndexReader.open(
                    FSDirectory.open(new File("data/index")));

            // first find all terms in the index
            int numDocs = reader.numDocs();

            for (int i = 0; i < numDocs; i++) {
                Document doc = reader.document(i);
                System.out.println(doc.getFieldable("title"));
                //System.out.println(doc.hashCode());
//				TermFreqVector tfv = reader.getTermFreqVector(i,"title");
//				int[] tf = tfv.getTermFrequencies();				
//				System.out.println(tf.length);  
                TermFreqVector[] tfvs = reader.getTermFreqVectors(i);

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

}
