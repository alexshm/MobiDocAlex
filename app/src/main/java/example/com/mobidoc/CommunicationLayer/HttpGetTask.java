package example.com.mobidoc.CommunicationLayer;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public abstract class HttpGetTask extends AsyncTask<String, String, String>{

    private String answer;
    public HttpGetTask()
    {
        answer="";
    }
    public String getAnswer()
    {
        return answer;
    }
    @Override
    protected String doInBackground(String... uri) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            response = httpclient.execute(new HttpGet(uri[0]));

            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);

                responseString = out.toString();
                out.close();
              //  publishProgress(responseString);
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


    @Override
    protected void onPostExecute(String result) {
        Log.i("on recieve  - httpget-","recieve the result "+result);
      // onResponseReceived(result);
    }


   // public abstract void onResponseReceived(String result);
}