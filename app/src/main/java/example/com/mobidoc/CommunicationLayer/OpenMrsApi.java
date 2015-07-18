package example.com.mobidoc.CommunicationLayer;

import android.os.AsyncTask;

import org.json.JSONObject;
import org.json.JSONArray;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        getHash.put("URLPath", "rest/v1/session");
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
        getHash.put("URLPath", "rest/v1/session");

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
        getHash.put("URLPath", "rest/v1/person?q="+name);
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
        getHash.put("URLPath", "rest/v1/concept?q="+name);
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


    public String[] getPreferences(){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "module/personalization/manage2?id=2&uuid="+getPatintUuid());
        HttpRecTask httpRecTask = new HttpRecTask(username, password, baseUrl );
        try {
            answer = httpRecTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getHash).get();
        } catch (InterruptedException e) {

        } catch (ExecutionException e) {

        }

        String[] arrayObs = new String[13];

        try {
            //please keep this order.

            JSONObject jObject = new JSONObject(answer);
            arrayObs[0] = jObject.getString("fasting");
            arrayObs[1] = jObject.getString("breakfast");
            arrayObs[2] = jObject.getString("lunch");
            arrayObs[3] = jObject.getString("evening");
            arrayObs[4] = jObject.getString("fastingAlarm");
            arrayObs[5] = jObject.getString("breakfastAlarm");
            arrayObs[6] = jObject.getString("lunchAlarm");
            arrayObs[7] = jObject.getString("eveningAlarm");
            arrayObs[8] = jObject.getString("day");//once a week  day
            arrayObs[9] = jObject.getString("time");//once a week  measure time
            arrayObs[10] =  jObject.getString("dayAlarm"); //once  a week  reminder time
            arrayObs[11] = jObject.getString("day1")+","+jObject.getString("day2");//twice a week  days
            arrayObs[12] ="08:00"; //TODO: jObject.getString("time2"); //twice a week  measure time for now its simulated


        } catch (Exception e) {

        }

        return arrayObs;
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
        getHash.put("URLPath", "rest/v1/obs");
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
    public String[][] getObs(){
        String patientID = getPatientID();
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "rest/v1/obs?q="+patientID);
        HttpRecTask httpRecTask = new HttpRecTask(username, password, baseUrl );
        try {
            answer = httpRecTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getHash).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        String[] UUidArray = jsonArray2(answer,"uuid","results");
        String[][] arrayObs = new String[UUidArray.length][];
        for (int i= 0;i<UUidArray.length;i++){
            arrayObs[i] = getOneObs(UUidArray[i]);
        }


        return arrayObs;
    }

    /**
     * get the last 50 measures(observations) without date.
     * @return Last 50 measures of that patient.
     */
    public String[] getObsWithoutDate(){
        String patientID = getPatientID();
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "rest/v1/obs?q="+patientID);
        HttpRecTask httpRecTask = new HttpRecTask(username, password, baseUrl );
        try {
            answer = httpRecTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getHash).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return jsonArray2(answer,"uuid","results");
    }



    /**
     * get Patient unique id(uuid) by patient id(not uuid!!)
     * @return the the patient uuid.
     */
    public String getPatintUuid(){
        String patientID = getPatientID();
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "rest/v1/patient?q="+patientID);
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
     * get personUuid by user uuid.
     * @param userUuid the user uuid
     * @return personUuid
     */
    public String getPersonUUIDbyUserUUID(String userUuid){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "rest/v1/user/"+userUuid);
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
     * @return the user uuid.
     */
    public String getUserUuid(){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "rest/v1/user?q="+username);
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

     * @return the user uuid. (Not parse!!!)
     */
    public String getPatientID(){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "rest/v1/patient?q="+username);
        HttpRecTask httpRecTask = new HttpRecTask(username, password, baseUrl );
        try {
            answer = httpRecTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getHash).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        String displayId = jsonArray(answer,"display","results");
        if (displayId.split(" - ").length > 0)
            answer = displayId.split(" - ")[0];
        return answer;
    }

    /**
     * Upload one measure(observation) of the person.
     * @param value the value of the measure
     * @param dateTime the time when the measure was taken(Format example: 2010-03-23T00:00:00.000+0200)
     * @param conceptName the concept name.
     * @return succeed or failed to upload(Not parse!!).
     */
    public String enterMeasure(String value, String dateTime, String conceptName){
        String userUUID = getUserUuid();
        String personUuid = getPersonUUIDbyUserUUID(userUUID);
        String conceptUuid = getConceptUuid(conceptName);

        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Post");
        getHash.put("URLPath", "rest/v1/obs");
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
        String userUUID = getUserUuid();
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

    public String[] getOneObs(String obsUUID){
        String answer="problem";
        HashMap<String, String> getHash = new HashMap<String, String>();
        getHash.put("requestType", "Get");
        getHash.put("URLPath", "rest/v1/obs/"+obsUUID);
        HttpRecTask httpRecTask = new HttpRecTask(username, password, baseUrl );
        try {
            answer = httpRecTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, getHash).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        String[] obsData = new  String[3];
        try {
            JSONObject jObject = new JSONObject(answer);
            String[] displayval=jObject.getString("display").split(":");
            if(displayval[0].contains("BLOOD"))
                displayval[0]="Blood Pressure";
            if(displayval[0].contains("Catanu"))
                displayval[0]="Ketanuria";

            obsData[0]=displayval[0];
            Date d=sdf.parse(jObject.getString("obsDatetime"));
            obsData[1]=output.format(d);
            obsData[2]=displayval[1];

        } catch (Exception e) {
            e.printStackTrace();
        }

        return obsData;
    }

//    public List<String> getPatientList(){
//        return null;
//    }


}
