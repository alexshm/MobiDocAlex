package example.com.mobidoc.CommunicationLayer;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

/**
 * Created by Alex on 27/03/2015.
 */
public class ApiAuthRest {
    private String username = null;
    private String password = null;
    private String URLBase = null;

    public ApiAuthRest(String username, String password, String URLBase) {
        this.username = username;
        this.password = password;
        this.URLBase = URLBase;
    }

    /**
     * HTTP POST
     *
     * @param URLPath
     * @param input
     * @return
     * @throws Exception
     */
    public String post(String URLPath, StringEntity input) throws Exception {
        String URL = URLBase + URLPath;
        Boolean response = false;
        DefaultHttpClient httpclient = new DefaultHttpClient();
        String responseString;
        try {
            HttpPost httpPost = new HttpPost(URL);
            System.out.println(URL);
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
            BasicScheme scheme = new BasicScheme();
            Header authorizationHeader = scheme.authenticate(credentials, httpPost);
            httpPost.setHeader(authorizationHeader);
            httpPost.setEntity(input);
            HttpResponse responseRequest = httpclient.execute(httpPost);
            HttpEntity entity = responseRequest.getEntity();
            responseString = EntityUtils.toString(entity, "UTF-8");
            System.out.println(responseString);
            if (responseRequest.getStatusLine().getStatusCode() != 204 && responseRequest.getStatusLine().getStatusCode() != 201
                    && responseRequest.getStatusLine().getStatusCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + responseRequest.getStatusLine().getStatusCode());
            }

            httpclient.getConnectionManager().shutdown();
//            response = true;
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return responseString;
    }

    /**
     * HTTP GET
     *
     * @param URLPath
     * @return
     * @throws Exception
     */
    public String get(String URLPath) throws Exception {
        String URL = URLBase + URLPath;
        String response = "";
        DefaultHttpClient httpclient = new DefaultHttpClient();
        try {
            HttpGet httpGet = new HttpGet(URL);

            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(username, password);
            BasicScheme scheme = new BasicScheme();
            Header authorizationHeader = scheme.authenticate(credentials, httpGet);
            httpGet.setHeader(authorizationHeader);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            response = httpclient.execute(httpGet, responseHandler);
        } finally {
            httpclient.getConnectionManager().shutdown();
        }
        return response;
    }


}
