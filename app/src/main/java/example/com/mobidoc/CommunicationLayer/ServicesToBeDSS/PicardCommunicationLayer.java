package example.com.mobidoc.CommunicationLayer.ServicesToBeDSS;


import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import example.com.mobidoc.ConfigReader;

/**
     *  obj to handle communication to the BE-DSS(main server)
     */

public  final class  PicardCommunicationLayer  {

        public static String ans;



    public static void DataNotificationOnCallBack(String patientID,String notificationConcept,String startTime,String MobileRegId,String baseUrl) {

        try {
            Log.i("CommunicationLayer"," execute Data Notification to the server ");

            String encodeTime=URLEncoder.encode(startTime, "utf-8");
            baseUrl+="/DataNotification?patientId="+patientID+ "&conceptId=" + notificationConcept + "&nowTime=" + encodeTime+ "&mobileID="+MobileRegId;
            String ans= new DataNotificationAsyncTask().executeOnExecutor(AsyncTask.SERIAL_EXECUTOR,baseUrl).get();

            Log.i("CommunicationLayer"," Recieving answer from DataNotification : "+ans);

        } catch (InterruptedException e) {
            Log.e("CommunicationLayer","Error StartGuideLine .error msg : "+e.getMessage());

        } catch (ExecutionException e) {
            Log.e("CommunicationLayer", "Error StartGuideLine .error msg : " + e.getMessage());
        }
        catch (UnsupportedEncodingException e) {
            Log.e("CommunicationLayer", "Error encoding time : " + e.getMessage());

        }

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

    private static class  DataNotificationAsyncTask extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... params) {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response;
            String responseString = null;
            try {
                response = httpclient.execute(new HttpGet(params[0]));

                StatusLine statusLine = response.getStatusLine();
                if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    response.getEntity().writeTo(out);

                    responseString = out.toString();
                    out.close();

                    return responseString;
                } else {
                    //Closes the connection.
                    response.getEntity().getContent().close();
                    throw new IOException(statusLine.getReasonPhrase());
                }
            } catch (ClientProtocolException e) {
                Log.e("HttpRequestTask", "error communicate Picard");
                return "ClientProtocolError  "+e.getMessage();
            } catch (IOException e) {
                Log.e("HttpRequestTask", "error communicate Picard");
                return "IOException Error : "+e.getMessage();
            }

        }


    }


}
