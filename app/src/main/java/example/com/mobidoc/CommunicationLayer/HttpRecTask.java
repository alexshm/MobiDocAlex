package example.com.mobidoc.CommunicationLayer;

import android.os.AsyncTask;

import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Created by Alex on 27/03/2015.
 */
public class HttpRecTask extends AsyncTask<HashMap<String, String>, String, String> {


    private final ApiAuthRest apiAuthRest;

    public HttpRecTask(String username, String password, String baseUrl) {
        this.apiAuthRest = new ApiAuthRest(username, password, baseUrl);
    }



    @Override
    protected String doInBackground(HashMap<String, String>... hashMaps) {
        HashMap<String, String> hashMap = hashMaps[0];
        String requestType = hashMap.get("requestType");
        if (requestType.equals("Get")) {
            return sendGet(hashMap);
        } else if (requestType.equals("Post")) {
            return sendPost(hashMap);
        }
        return "got wrong params in the hashMap";

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        System.out.println("pre pre");
    }


    private String sendPost(HashMap<String, String> hashMap) {
        String URLPath = hashMap.get("URLPath");
        String JSON = hashMap.get("JSON ");
        try {
            StringEntity stringEntity = new StringEntity(JSON);
            return apiAuthRest.post(URLPath, stringEntity).toString();
        } catch (UnsupportedEncodingException e) {
            return e.getMessage();
        } catch (Exception e) {
            return e.getMessage();
        }

    }

    private String sendGet(HashMap<String, String> hashMap) {
        String URLPath = hashMap.get("URLPath");
        try {
            String s =apiAuthRest.get(URLPath);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "problemmm";
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        System.out.println("the ans is :" + result);
        //Do anything with response..
    }



}
