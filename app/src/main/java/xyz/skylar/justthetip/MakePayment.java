package xyz.skylar.justthetip;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by Ryan on 6/7/2015.
 */
public class MakePayment extends AsyncTask<String, Void, String> {
    public Context mp_context;
    public Activity mp_activity;

    public final String VENMO_PREFIX = "https://api.venmo.com/v1/";
    public final String VENMO_PAYMENT = "payments?access_token=";
    public final String VENMO_EMAIL = "&email=";
    public final String VENMO_AMOUNT = "&amount=";
    public final String VENMO_NOTE = "&note=";

    // setting context to use toast & activity to finish activity
    public MakePayment (Context context, Activity activity) {
        this.mp_context = context;
        this.mp_activity = activity;
    }

    protected String doInBackground (String... params) {
        final String authCode = params[0];
        final String email = params[1];
        final String amount = params[2];

        String jsonResultString = "null";
        // build up query url
        String queryString = VENMO_PREFIX + VENMO_PAYMENT + authCode
                           + VENMO_EMAIL + email
                           + VENMO_AMOUNT + amount
                           + VENMO_NOTE + "Delivery";


        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(queryString);

        try {
            // execute http post
            HttpEntity httpEntity = httpClient.execute(httpPost).getEntity();
            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                Reader in = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(in);
                StringBuilder stringBuilder = new StringBuilder();

                String stringReadLine = null;

                while ((stringReadLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(stringReadLine + "\n");
                }

                jsonResultString = stringBuilder.toString();
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return jsonResultString;
    }

    public void onPostExecute (String result) {
        // JSON RESULT
        Log.i("JSON result", result);
        // finish payment activity and show toast
        this.mp_activity.finish();
        Toast.makeText(mp_context, "Payment complete", Toast.LENGTH_LONG).show();
    }
}
