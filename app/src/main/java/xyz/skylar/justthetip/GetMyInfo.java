package xyz.skylar.justthetip;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Ryan on 6/1/2015.
 */
public class GetMyInfo extends AsyncTask<String, Void, String> {

    // complete raw json for QR code (?) along with some json params
    public static String userJSON, userInfo, USERNAME, DISPLAY_NAME,
                         EMAIL, PHONE = null;
    public static Bitmap PICTURE = null;

    private static final String VENMO_PREFIX = "https://api.venmo.com/v1/";
    private static final String VENMO_ME = "me?access_token=";

    public void ParseJSON (String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject jsonData = jsonObject.getJSONObject("data");
            JSONObject jsonUser = jsonData.getJSONObject("user");

            USERNAME = jsonUser.getString("username");
            DISPLAY_NAME = jsonUser.getString("display_name");
            EMAIL = jsonUser.getString("email");
            PHONE = jsonUser.getString("phone");

            // create a bitmap with the profile pic url
            String picture_url = jsonUser.getString("profile_picture_url");
            URL PhotoURL = null;
            try {
                PhotoURL = new URL(picture_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) PhotoURL.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                PICTURE = BitmapFactory.decodeStream(inputStream);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            // create a smaller json object w/ necessary user info
            JSONObject userInfoJSON = new JSONObject();
            try {
                userInfoJSON.put("username", USERNAME);
                userInfoJSON.put("display_name", DISPLAY_NAME);
                userInfoJSON.put("phone", PHONE);
                userInfoJSON.put("email", EMAIL);
                userInfoJSON.put("pic_url", picture_url);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            userInfo = userInfoJSON.toString();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

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

        ParseJSON(jsonResultString);

        return jsonResultString;
    }

    // just prints out the raw json right now
    public void onPostExecute (String result) {
        //Log.i (" ***JSON RESULT***", result);
        userJSON = result;
    }
}
