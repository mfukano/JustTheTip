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

import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

/**
 * A simple launcher activity containing a summary sample description, sample log and a custom
 * {@link android.support.v4.app.Fragment} which can display a view.
 * <p>
 * For devices with displays with a width of 720dp or greater, the sample log is always visible,
 * on other devices it's visibility is controlled by an item on the Action Bar.
 */
public class MainActivity extends ActivityBase {
    WebView myWebView;
    ProgressDialog pDialog;
    public static final String TAG = "MainActivity";
    private Context context;
    Button newTip;

    // Whether the Log Fragment is currently shown
    private boolean mLogShown;
    FragmentManager fm;

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //MenuItem logToggle = menu.findItem(R.id.menu_toggle_log);
        //logToggle.setVisible(findViewById(R.id.sample_output) instanceof ViewAnimator);
        //logToggle.setTitle(mLogShown ? R.string.sample_hide_log : R.string.sample_show_log);

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /*switch(item.getItemId()) {
            case R.id.menu_toggle_log:
                mLogShown = !mLogShown;
                ViewAnimator output = (ViewAnimator) findViewById(R.id.sample_output);
                if (mLogShown) {
                    output.setDisplayedChild(1);
                } else {
                    output.setDisplayedChild(0);
                }
                supportInvalidateOptionsMenu();
                return true;
        }*/
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == 1) {
            if(resultCode == RESULT_OK){
                String result=data.getStringExtra("code");
                Log.i("","~~~Token:" + result);
                TextView token = (TextView) findViewById(R.id.textView4);
                token.setText(result);
            }
            if (resultCode == RESULT_CANCELED) {
                Log.i("","~~~No token");
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
    public void loginVenmo(View v){
        /*String url = "https://api.venmo.com/v1/oauth/authorize?client_id=2654&scope=make_payments%20access_profile";
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);*/
        /*myWebView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.setWebViewClient(new WebViewClient() {

            boolean authComplete = false;
            Intent resultIntent = new Intent();

            @Override public void onPageStarted(WebView view, String url, Bitmap favicon){
                super.onPageStarted(view, url, favicon);
                pDialog = ProgressDialog.show(view.getContext(), "",
                        "Connecting to Venmo server", false);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                pDialog.dismiss();

                if (url.contains("?access_token=") && authComplete != true) {
                    Uri uri = Uri.parse(url);
                    String authCode = uri.getQueryParameter("access_token");
                    Log.i("", "~~~~~~~~~~TOKEN : " + authCode);
                    authComplete = true;
                    resultIntent.putExtra("code", authCode);
                    //WebActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                    //resultIntent.putExtra("status", YouActivity.Status.SUCCESS.toString());
                    //setResult(Activity.RESULT_CANCELED, resultIntent);
                    finish();
                }else if(url.contains("error=access_denied")){
                    Log.i("", "ACCESS_DENIED_HERE");
                    //resultIntent.putExtra("code", authCode);
                    //resultIntent.putExtra("status", WebActivity.Status.ACCESS_DENIED.toString());
                    authComplete = true;
                    //setResult(Activity.RESULT_CANCELED, resultIntent);
                    finish();
                }
            }
        });
        myWebView.getSettings().setUserAgentString("Mozilla/5.0 (Linux; U; Android 2.0; en-us; Droid Build/ESD20) AppleWebKit/530.17 (KHTML, like Gecko) Version/4.0 Mobile Safari/530.17");
        myWebView.loadUrl("https://api.venmo.com/v1/oauth/authorize?client_id=2654&scope=make_payments%20access_profile");
        */
        String destination = "xyz.skylar.justthetip.URL";
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.putExtra(destination, "https://api.venmo.com/v1/oauth/authorize?client_id=2654&scope=make_payments%20access_profile");
        startActivityForResult(intent, 1);
    }

}
