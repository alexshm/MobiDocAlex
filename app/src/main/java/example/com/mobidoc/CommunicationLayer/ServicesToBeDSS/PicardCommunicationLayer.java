package example.com.mobidoc.CommunicationLayer.ServicesToBeDSS;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import example.com.mobidoc.ConfigReader;

/**
     *  obj to handle communication to the BE-DSS(main server)
     */

public  final class  PicardCommunicationLayer  {

    private static final long GUIDE_LINE_ID = 19857;
    private static final String MAIN_SERVER_LINK="http://medinfo2.ise.bgu.ac.il/Picard/PicardWCFServicesMG/PicardWCFServer.ClientEMRService.svc/jsonp";
        public static String ans;
    private static final String OPEN_MRS_LINK ="http://132.72.23.228:8081/openmrs-standalone";
    public static void EnrollNewPatient(String patientID) {


        String url = MAIN_SERVER_LINK + "/enrollPatient?patientID=" + patientID + "&glID=" + GUIDE_LINE_ID;
      // new HttpGetTask().execute(url);

    }

    public static boolean  StartGuideLine(String patientID,String startTime,String guideLineId,String MobileRegId,String baseUrl) {

        try {

            JSONObject ans= new StartGLTask(baseUrl).executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,patientID,guideLineId,startTime,MobileRegId).get();
            if( ans!=null && ans.getInt("ResultCode")==0)
                return true;
            return false;
        } catch (InterruptedException e) {
            Log.e("CommunicationLayer","Error StartGuideLine .error msg : "+e.getMessage());
            return false;
        } catch (ExecutionException e) {
            Log.e("CommunicationLayer","Error StartGuideLine .error msg : "+e.getMessage());
           return false;
        } catch (JSONException e) {
            Log.e("CommunicationLayer","Error getting the field in received Json .error msg : "+e.getMessage());
            return false;
        }
    }
    public static void sendfirst(View v) {

        String url = "http://132.72.23.228:8081/openmrs-standalone/ws/rest/v1/obs?q=123456";
       // new HttpGetTask().execute(url);

    }




}
