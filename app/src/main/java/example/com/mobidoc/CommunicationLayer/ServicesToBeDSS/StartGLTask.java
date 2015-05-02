package example.com.mobidoc.CommunicationLayer.ServicesToBeDSS;

import android.os.AsyncTask;
import android.util.Log;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.zip.GZIPInputStream;

import example.com.mobidoc.CommunicationLayer.HttpRecTask;
import example.com.mobidoc.CommunicationLayer.OpenMrsApi;
import example.com.mobidoc.CommunicationLayer.PushNotification;

/**
 * Async task to start Glide line in the BE-DSS
 *
 */
public class StartGLTask  extends AsyncTask<String, Void, JSONObject> {

    private final String BaseUrl;

    public StartGLTask(String baseUrl) {
        this.BaseUrl = baseUrl;
    }

    @Override

    protected JSONObject doInBackground(String... params) {

        boolean ans = false;

        Log.i("Login Screen", "starting GL ...");

        SimpleDateFormat sdf = new SimpleDateFormat("dd\\-MM\\-yyyy HH\\:mm\\:ss");
        String patientId = params[0];
        String glId = params[1];
        String startTime = "2014\\-02\\-20T19\\:42\\:18+02:00";// TODO:params[2];
        String regId = params[3];
        JSONObject jsonRec = null;

        JSONObject jsonToSend = createJsonTosend(patientId, regId, glId, startTime);
        Log.i("StartGLTask", "sendind the json  : " + jsonToSend.toString());

        if (jsonToSend != null)
            jsonRec = SendHttpPost(BaseUrl + "/MessageNotification", jsonToSend);

        return jsonRec;
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        if (result != null)
            Log.i("StartGL", "the return answer from BE-DSS : <JSONObject>\n" + result.toString() + "\n</JSONObject>");


    }




    private JSONObject createJsonTosend(String patientId,String MobileRegId,String glId,String startTime)
    {

        String jsonbody = "{\"SenderId\":\""+MobileRegId+"\",\"SenderMobiguideId\":\""+patientId+"\""+
                ",\"MessageDataHex\":\"{\\\"result\\\":\\\"restart\\\",\\\"GlId\\\":\\\""+glId+"\\\",\\\"NowTime\\\":\\\"" + startTime+"\\\"}\"" +
            ",\"MessageType\":\"projectDeclarativeKnowledgeResult\"," +
                "\"MessageChannelId\":\"beDss\",\"Retention\":0}";


        try {
            JSONObject  jsonObj = new JSONObject(jsonbody);
            return jsonObj;
        } catch (JSONException e) {
            Log.e("StartGLTask","error creating json- error: "+e.getMessage());
            return null;
        }

    }


    private JSONObject SendHttpPost(String URL, JSONObject jsonObjSend) {

        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPostRequest = new HttpPost(URL);

            StringEntity se;
            se = new StringEntity(jsonObjSend.toString());

            // Set HTTP parameters
            httpPostRequest.setEntity(se);
            httpPostRequest.setHeader("Accept", "application/json");
            httpPostRequest.setHeader("Content-type", "application/json");
            httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

            long t = System.currentTimeMillis();
            HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
            //   Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis()-t) + "ms]");

            // Get hold of the response entity (-> the data):
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Read the content stream
                InputStream instream = entity.getContent();
                Header contentEncoding = response.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    instream = new GZIPInputStream(instream);
                }

                // convert content stream to a String
                String resultString = convertStreamToString(instream);
                instream.close();

                // Transform the String into a JSONObject
             JSONObject jsonObjRecv = new JSONObject(resultString);
                // Raw DEBUG output of our received JSON object:
                Log.i("StartGL", "the return answer from BE-DSS : <JSONObject>\n" + jsonObjRecv.toString() + "\n</JSONObject>");

                return jsonObjRecv;
            }

        } catch (Exception e) {
            // More about HTTP exception handling in another tutorial.
            // For now we just print the stack trace.
            e.printStackTrace();
            return null;
        }
        return null;
    }



    private String convertStreamToString(InputStream is) {
        String line = "";
        StringBuilder total = new StringBuilder();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        try {
            while ((line = rd.readLine()) != null) {
                total.append(line);
            }
        } catch (Exception e) {
            //Toast.makeText(this, "Stream Exception", Toast.LENGTH_SHORT).show();
        }
        return total.toString();
    }
}



