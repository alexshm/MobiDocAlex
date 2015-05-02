package example.com.mobidoc.CommunicationLayer;

import android.os.AsyncTask;

import org.json.JSONObject;
import org.json.JSONArray;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;
/**
 * Created by Alex on 04/04/2015.
 */
public class OpenMrsApi {

    private final String baseUrl;
    private static String username = "";
    private static String password = "";

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
        HttpRecTask httpRecTask = new HttpRecTask(username, password, baseUrl );
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
            Thread.sleep(2000);
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

        if (aJsonBoolean){
            this.username = userName;
            this.password = pass;
        }
        return aJsonBoolean;
    }


    /**
     * get person Uuid(the unique Id of person at the MRS) by the name of the person.
     * @param name The person name
     * @return All persons with that name. (Not parse!!)
     */
    public String getPersonUuidByName(String name){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "person?q="+name);
        HttpRecTask httpRecTask = new HttpRecTask(username, password, baseUrl );
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
        HttpRecTask httpRecTask = new HttpRecTask(username, password, baseUrl );
        try {
            answer = httpRecTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getHash).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return jsonArray(answer,"uuid","results");
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
        HttpRecTask httpRecTask = new HttpRecTask(username, password, baseUrl );
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
     * get the last 50 measures(observations).
     * @return Last 50 measures of that patient.
     */
    public String[] getObs(){
        String patientID = getPatientID(username);
        patientID = patientID.substring(0,patientID.lastIndexOf(" - "));
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "obs?q="+patientID);
        HttpRecTask httpRecTask = new HttpRecTask(username, password, baseUrl );
        try {
            answer = httpRecTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getHash).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        String[] UUidArray = jsonArray2(answer,"uuid","results");
        String[] arrayObs = new String[UUidArray.length];
        for (int i= 0;i<UUidArray.length;i++){
            arrayObs[i] = getOneObs(UUidArray[i]);
        }


        return arrayObs;
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
        HttpRecTask httpRecTask = new HttpRecTask(username, password, baseUrl );
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
     * get personUuid by user uuid.
     * @param userUuid the user uuid
     * @return personUuid
     */
    public String getPersonUUIDbyUserUUID(String userUuid){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "user/"+userUuid);
        HttpRecTask httpRecTask = new HttpRecTask(username, password, baseUrl );
        try {
            answer = httpRecTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getHash).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        String personUUID = "";
        try {
            JSONObject jObject = new JSONObject(answer);
            JSONObject  jObjectPerson = jObject.getJSONObject("person");
            personUUID = jObjectPerson.getString("uuid");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return personUUID;
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
        HttpRecTask httpRecTask = new HttpRecTask(username, password, baseUrl );
        try {
            answer = httpRecTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getHash).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return jsonArray(answer,"uuid","results");
    }

    /**
     * get user uuid by user name.
     * @param userName the user name
     * @return the user uuid. (Not parse!!!)
     */
    public String getPatientID(String userName){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "patient?q="+userName);
        HttpRecTask httpRecTask = new HttpRecTask(username, password, baseUrl );
        try {
            answer = httpRecTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getHash).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return jsonArray(answer,"display","results");
    }

    /**
     * Upload one measure(observation) of the person.
     * @param value the value of the measure
     * @param dateTime the time when the measure was taken(Format example: 2010-03-23T00:00:00.000+0200)
     * @param conceptName the concept name.
     * @return succeed or failed to upload(Not parse!!).
     */
    public String enterMeasure(int value, String dateTime, String conceptName){
        String userUUID = getUserUuid(username);
        String personUuid = getPersonUUIDbyUserUUID(userUUID);
        String conceptUuid = getConceptUuid(conceptName);

        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Post");
        getHash.put("URLPath", "obs");
        String body = "{\"person\":\""+personUuid+"\",\"obsDatetime\":\""+dateTime+"\""+
                ",\"concept\":\""+conceptUuid+"\",\"value\":\""+value+"\",\"location\":\"8d6c993e-c2cc-11de-8d13-0010c6dffd0f\"}";
        getHash.put("JSON", body);
        HttpRecTask httpRecTask = new HttpRecTask(username, password, baseUrl );
        try {
            answer = httpRecTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getHash).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return answer;
    }
    public String getPersonUUID() {
        String userUUID = getUserUuid(username);
        return getPersonUUIDbyUserUUID(userUUID);
    }

    private String jsonArray(String JSON, String paramName,String arrayName) {
        try {
            JSONObject jObject = new JSONObject(JSON);
            JSONArray jArray = jObject.getJSONArray(arrayName);

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject oneObject = jArray.getJSONObject(i);
                // Pulling items from the array
                String oneObjectsItem = oneObject.getString(paramName);
                return oneObjectsItem;
            }
        }  catch (Exception e) {
        // Oops
        }
        return "Problem: JSON ARRAY";
    }

    private String[] jsonArray2(String JSON, String paramName,String arrayName) {
        String[] obsUUIDs = null;
        try {
            JSONObject jObject = new JSONObject(JSON);
            JSONArray jArray = jObject.getJSONArray(arrayName);
            obsUUIDs = new String[jArray.length()];
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject oneObject = jArray.getJSONObject(i);
                // Pulling items from the array
                obsUUIDs[i] = oneObject.getString(paramName);
            }
            return obsUUIDs;
        }  catch (Exception e) {
            // Oops
        }
        return obsUUIDs;
    }

    public String getOneObs(String obsUUID){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "obs/"+obsUUID);
        HttpRecTask httpRecTask = new HttpRecTask(username, password, baseUrl );
        try {
            answer = httpRecTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getHash).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        String obsData = "";
        try {
            JSONObject jObject = new JSONObject(answer);
            obsData += jObject.getString("display") + ": ";
            obsData += jObject.getString("obsDatetime");
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obsData;
    }

    public List<String> getPatientList(){
        return null;
    }


}
