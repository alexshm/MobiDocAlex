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

    public List<String> getPatientList(){
        return null;
    }


}
