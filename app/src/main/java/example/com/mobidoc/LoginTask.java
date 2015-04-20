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
    private boolean withOpenMRS;
    private ProgressDialog mProgressDialog;

    public LoginTask(String baseUrl, boolean withOpenMRS , ProgressDialog mProgressDialog) {
        this.BaseUrl = baseUrl;
        this.withOpenMRS = withOpenMRS;
        this.mProgressDialog = mProgressDialog;
    }



    private void initProgressBar() {
        // Set Dialog message
        mProgressDialog.setMessage("Please Wait..");
        mProgressDialog.setTitle("Verifying login");
        // Dialog will be displayed for an unknown amount of time
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();


    }

    @Override

    protected Boolean doInBackground(String... params) {

        boolean ans = false;
        if (withOpenMRS) {
            Log.i("Login Screen", "the url to connect is :" + BaseUrl);
            OpenMrsApi mrsApi = new OpenMrsApi(BaseUrl);
            ans = mrsApi.logIn(params[0], params[1]);
            Log.i("Login Screen", "the answer from OPEN MRS IS :" + ans);
        } else {

            if ("admin".equals(params[0]) && "12345".equals(params[1]))
                ans = true;
        }

        System.out.println("finish doInBackground!!!!!!!!!!!!!!");
        return ans;

    }


    @Override
    protected void onPostExecute(Boolean result) {
        System.out.println("finish!!!!!!!!!!!!!!!!!!!!!!!!!!!");



        System.out.println("after sleep");
      // onResponseReceived(result);

    }

    //public abstract void onResponseReceived(Boolean result);
}