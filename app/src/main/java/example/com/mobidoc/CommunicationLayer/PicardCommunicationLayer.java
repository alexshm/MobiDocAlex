package example.com.mobidoc.CommunicationLayer;


import android.view.View;

import java.util.Date;

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

    public static void StartGuideLine(String patientID,Date startTime) {

        String url = MAIN_SERVER_LINK +"/TestCallback?patientId=" +patientID+"&glId="+GUIDE_LINE_ID+"&nowTime="+startTime.toString();
     //   new HttpGetTask().execute(url);

    }
    public static void sendfirst(View v) {

        String url = "http://132.72.23.228:8081/openmrs-standalone/ws/rest/v1/obs?q=123456";
       // new HttpGetTask().execute(url);

    }




}
