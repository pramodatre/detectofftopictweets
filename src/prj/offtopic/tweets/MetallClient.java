package prj.offtopic.tweets;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * @author pramod anantharam
 */

public class MetallClient {
    private static final String METALL_BASE_URL = "http://peweproxy.fiit.stuba.sk/metall";

    public class MetallClientException extends Exception {
        private static final long serialVersionUID = 5797183739350787524L;

        public MetallClientException(String message) {
            super(message);
        }

        public MetallClientException(Exception e) {
            super(e);
        }
    }

    public String cleartext(String content) throws MetallClientException {
        HttpClient client = new HttpClient();
        PostMethod post = new PostMethod(METALL_BASE_URL + "/readability");
        post.setRequestHeader("Content-Type", PostMethod.FORM_URL_ENCODED_CONTENT_TYPE + "; charset=utf-8");
        post.setRequestHeader("Accept-Charset", "utf-8");
        NameValuePair[] data = {new NameValuePair("url", content)};
        post.setRequestBody(data);

        try {
            int resp = client.executeMethod(post);
            if (resp == 200) {
                return post.getResponseBodyAsString();
            } else {
                return "";
                //throw new MetallClientException(post.getResponseBodyAsString());                
            }
        } catch (IOException e) {
            //throw new MetallClientException(e);
            return "";
        }

    }
}
