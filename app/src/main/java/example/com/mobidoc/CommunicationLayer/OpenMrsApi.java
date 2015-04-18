package example.com.mobidoc.CommunicationLayer;

import android.os.AsyncTask;

import org.json.JSONObject;

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

    /**
     * get session details
     * @return true if the user is authenticated
     */
    public boolean getSession(){
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

        boolean aJsonBoolean = false;
        try {
            JSONObject jObject = new JSONObject(answer);
            aJsonBoolean = jObject.getBoolean("authenticated");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return aJsonBoolean;
    }

    /**
     * Log in to the openMRS system with user name and password
     * @param userName
     * @param pass
     * @return True if the user is authenticated, false otherwise.
     */
    public boolean logIn(String userName, String pass){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "session");
        HttpRecTask httpRecTask = new HttpRecTask(userName, pass, baseUrl );
        try {
            answer = httpRecTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getHash).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        boolean aJsonBoolean = false;
        try {
            JSONObject jObject = new JSONObject(answer);
            aJsonBoolean = jObject.getBoolean("authenticated");
        } catch (Exception e) {
            e.printStackTrace();
        }

//        Object objSessionJson = JSONValue.parse( ApiAuthRest.getRequestGet("session"));
//        JSONObject jsonObjectSessionJson= (JSONObject) objSessionJson;
//        String sessionId = (String) jsonObjectSessionJson.get("sessionId");
//        Boolean authenticated = (Boolean) jsonObjectSessionJson.get("authenticated");
//        System.out.println("Session:"+sessionId+" Authenticated:"+authenticated);
        return aJsonBoolean;
    }


    /**
     * get person Uuid(the unique Id of person at the MRS) by the name of the person.
     * @param name The person name
     * @return All persons with that name. (Not parse!!)
     */
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

    /**
     * get concept Uuid(the unique Id(uuid) of concept at the MRS) by the name of the concept.
     * @param name The concept name
     * @return All concepts with that name. (Not parse!!)
     */
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


    /**
     * Upload one measure(observation) of the person.
     * @param personUuid the person unique id(uuid).
     * @param value the value of the measure
     * @param dateTime the time when the measure was taken(Format example: 2010-03-23T00:00:00.000+0200)
     * @param conceptUuid the concept uuid.
     * @return succeed or failed to upload(Not parse!!).
     */
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

    /**
     * get the last 50 measures(observations) by patient id(not uuid!!).
     * @param patientID the patient id.
     * @return Last 50 measures of that patient. (Not parse!!)
     */
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


    /**
     * get Patient unique id(uuid) by patient id(not uuid!!)
     * @param patientID the patient id.
     * @return the the patient uuid. (not parse!!)
     */
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

    /**
     * get user details(personUuid and more) by user uuid.
     * @param userUuid the user uuid
     * @return user details(personUuid and more). (Not parse!!)
     */
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


    /**
     * get user uuid by user name.
     * @param userName the user name
     * @return the user uuid. (Not parse!!!)
     */
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
