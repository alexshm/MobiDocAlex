package example.com.mobidoc;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import example.com.mobidoc.CommunicationLayer.OpenMrsApi;

/**
 * Created by Alex on 17/04/2015.
 */
public   class LoginTask extends AsyncTask<String, Boolean, Boolean> {

    private final String BaseUrl;



    public LoginTask(String baseUrl ){
        this.BaseUrl = baseUrl;


    }

    @Override

    protected Boolean doInBackground(String... params) {

        boolean ans = false;

            Log.i("Login Screen", "the url to connect is :" + BaseUrl);
            OpenMrsApi mrsApi = new OpenMrsApi(BaseUrl);
            ans = mrsApi.logIn(params[0], params[1]);
            Log.i("Login Screen", "the answer from OPEN MRS IS :" + ans);



        return ans;

    }


    @Override
    protected void onPostExecute(Boolean result) {


    }


}