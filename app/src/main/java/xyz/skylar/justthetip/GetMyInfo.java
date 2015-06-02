package xyz.skylar.justthetip;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Created by Ryan on 6/1/2015.
 */
public class GetMyInfo extends AsyncTask<String, Void, String> {

    private static final String VENMO_PREFIX = "https://api.venmo.com/v1/";
    private static final String VENMO_ME = "me?access_token=";

    protected String doInBackground (String... code) {
        String jsonResultString = "null";
        StringBuilder builder = new StringBuilder();
        String queryString = VENMO_PREFIX + VENMO_ME + code[0];

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(queryString);
        try {
            HttpEntity httpEntity = httpClient.execute(httpGet).getEntity();
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

    // just prints out the raw json right now
    public void onPostExecute (String result) {
        Log.i (" ***JSON RESULT***", result);
    }
}
