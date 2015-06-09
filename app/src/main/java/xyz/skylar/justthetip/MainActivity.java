/*
* Copyright 2013 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


package xyz.skylar.justthetip;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends ActivityBase {
    private static final String AUTH_URL = "https://api.venmo.com/v1/oauth/authorize?client_id=2668&scope=make_payments%20access_profile%20access_email%20access_phone";

    WebView myWebView;
    ProgressDialog pDialog;
    public static final String TAG = "MainActivity";
    private Context context;
    Button newTip;

    // progress dialog to display after logging in while pictures, etc. are being set
    ProgressDialog progressDialog;
    Boolean progressShowing = false;

    // Whether the Log Fragment is currently shown
    private boolean mLogShown;
    FragmentManager fm;

    // for making the api calls
    public static String authCode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;
        fm = getSupportFragmentManager();

        if (savedInstanceState == null) {
            FragmentTransaction transaction = fm.beginTransaction();
            SlidingTabsFragment fragment = new SlidingTabsFragment();
            transaction.replace(R.id.sample_content_fragment, fragment);
            transaction.commit();
        }
        if(authCode == null){
            loginVenmo(findViewById(R.id.login));
        }
    }

    // after logging into venmo, this is called
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("code");
                Log.i("", "~~~Token:" + result);
               /* TextView token = (TextView) findViewById(R.id.textView4);
                token.setText(result);*/
                authCode = result;
                // set progress dialog
                // showDialog();
                // calls AsyncTask class to make an API call
                new GetMyInfo(context).execute(authCode);
            }
            if (resultCode == RESULT_CANCELED) {
                Log.i("", "~~~No token");
            }
        }
        if (requestCode == 6){
            if(resultCode == RESULT_OK){

            }
            if(resultCode == RESULT_CANCELED){

            }
        }
    }

    private String getIntentMessage(String s) {
        Intent intent = getIntent();
        return intent.getStringExtra(s);
    }
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals(Uri.parse("venmo.com"))) {
                // This is my web site, so do not override; let my WebView load the page
                return false;
            }
            // Otherwise, the link is not for a page on my site, so launch the browser
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;
        }
    }
    //Launch webview for OAth login
    public void loginVenmo(View v){
        String destination = "xyz.skylar.justthetip.URL";
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra(destination, AUTH_URL);
        startActivityForResult(intent, 1);
    }

    public void showDialog() {
        if (!progressShowing) {
            progressDialog = ProgressDialog.show(context, "", "Just a moment...", false);
            progressShowing = true;
        }
    }

    public void hideDialog() {
        progressDialog.dismiss();
        progressShowing = false;
    }

}
