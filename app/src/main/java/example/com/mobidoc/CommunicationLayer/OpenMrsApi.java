package example.com.mobidoc.CommunicationLayer;

import android.os.AsyncTask;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alex on 04/04/2015.
 */
public class OpenMrsApi {

    private final String baseUrl;

    public OpenMrsApi(String baseUrl){
        this.baseUrl = baseUrl;

    }

    public String getSession(){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "session");
        HttpRecTask httpRecTask = new HttpRecTask("admin", "Admin123", baseUrl );
        try {
            answer = httpRecTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getHash).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public String postObs(String personID, int value, String dateTime, String concept){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Post");
        getHash.put("URLPath", "obs");
        String body = "{\"person\":\""+personID+"\",\"obsDatetime\":\""+dateTime+"\""+
                ",\"concept\":\""+concept+"\",\"value\":\""+value+"\",\"location\":\"8d6c993e-c2cc-11de-8d13-0010c6dffd0f\"}";
        getHash.put("JSON", body);
        HttpRecTask httpRecTask = new HttpRecTask("admin", "Admin123", baseUrl );
        try {
            answer = httpRecTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getHash).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public List<String> getPatientList(){
        return null;
    }


}
