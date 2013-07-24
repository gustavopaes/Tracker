package net.gustavopaes.tracker;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


/**
 * Created by Gustavo on 20/06/13.
 */
public class TrackerWebservice {

    MainActivity mainInstance;
    ConnectivityManager connectivityManager;

    // Tempo Timeout
    private static int TIMEOUT_MILLISECONDS = 10 * 1000;

    public TrackerWebservice(MainActivity mainActivity) {
        mainInstance = mainActivity;
        connectivityManager = (ConnectivityManager) mainInstance.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    public boolean isNetworkAvailable() {
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    public void sendData(JSONObject data) throws IOException {
        new RequestTask().execute(data);
    }
}

class RequestTask extends AsyncTask<JSONObject, Integer, String> {
    // URL para request do webservice
    private static String URL_WEBSERVICE = "http://fizpramim.com/tracker/webservice/";

    @Override
    protected String doInBackground(JSONObject... jsonObjects) {
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        String responseString = null;
        try {
            // cria um HTTPPost
            HttpPost post = new HttpPost(URL_WEBSERVICE);
            post.setHeader("User-Agent", "TrackerAPP Android version 1.0");
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            //post.setHeader("Content-Length", String.valueOf(Integer.toString(jsonObjects[0].toString().getBytes().length)));

            response = httpclient.execute(post);
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                out.close();
                responseString = out.toString();
            } else{
                //Closes the connection.
                response.getEntity().getContent().close();
                throw new IOException(statusLine.getReasonPhrase());
            }
        } catch (ClientProtocolException e) {
            //TODO Handle problems..
            Log.i("Erro", "ClientProtocolException");
        } catch (IOException e) {
            //TODO Handle problems..
            Log.i("Erro", "IOException");
        }
        return responseString;
    }

    protected void onPostExecute(Long result) {
        Log.i("Parabens", "acabou!");
    }
}