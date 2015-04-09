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

    public String logIn(String userName, String pass){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "session");
        HttpRecTask httpRecTask = new HttpRecTask(userName, pass, baseUrl );
        try {
            answer = httpRecTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getHash).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return answer;
    }

    public String getPersonUuid(String name){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "person?q="+name);
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

    public String getConceptUuid(String name){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "concept?q="+name);
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

    public String postObs(String personUuid, int value, String dateTime, String conceptUuid){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Post");
        getHash.put("URLPath", "obs");
        String body = "{\"person\":\""+personUuid+"\",\"obsDatetime\":\""+dateTime+"\""+
                ",\"concept\":\""+conceptUuid+"\",\"value\":\""+value+"\",\"location\":\"8d6c993e-c2cc-11de-8d13-0010c6dffd0f\"}";
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

    public String getObs(String patientID){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "obs?q="+patientID);
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

    public String getPatintUuid(String patientID){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "patient?q="+patientID);
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

    //return : personUuid included!!!
    public String getUserDetails(String userUuid){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "user/"+userUuid);
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

    public String getUserUuid(String userName){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "user?q="+userName);
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
