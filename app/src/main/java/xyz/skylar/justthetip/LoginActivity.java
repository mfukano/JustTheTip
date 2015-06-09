package xyz.skylar.justthetip;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;


public class LoginActivity extends Activity {

    static final public String PREF_URL = "restore_url";
    static final public String WEBPAGE_NOTHING = "about:blank";
    public String authCode;
    ProgressDialog pDialog;
    Boolean showingPD = false;
    WebView myWebView;
    protected FrameLayout webViewPlaceholder;

    //Overrides the back button so that we can go to the last page instead of back to MainActivity
    @Override
    public void onBackPressed(){
        if (myWebView.canGoBack()) {
            myWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initUI();
    }

    protected void initUI(){
        myWebView = (WebView) findViewById(R.id.webView1);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.loadUrl(getIntentMessage());
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig)
    {
        if (myWebView != null)
        {
            // Remove the WebView from the old placeholder
            webViewPlaceholder.removeView(myWebView);
        }
        super.onConfigurationChanged(newConfig);

        // Reinitialize the UI
        initUI();
    }

    private class MyWebViewClient extends WebViewClient {
        boolean authComplete = false;
        Intent resultIntent = new Intent();
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            showProgressDialog(view);
            myWebView.setVisibility(View.INVISIBLE);
        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            hideProgressDialog();
            Log.i("", "~~~URL: " + url);
            if (url.contains("?access_token=") && !authComplete) {
                Uri uri = Uri.parse(url);
                authCode = uri.getQueryParameter("access_token");
                Log.i("", "~~~CODE : " + authCode);
                authComplete = true;
                resultIntent.putExtra("code", authCode);
                LoginActivity.this.setResult(Activity.RESULT_OK, resultIntent);
                resultIntent.putExtra("status", "SUCCESS");
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            } else if (url.contains("error=User+denied")) {
                Log.i("", "ACCESS_DENIED_HERE");
                //resultIntent.putExtra("code", "NULL");
                resultIntent.putExtra("status", "DENIED");
                authComplete = true;
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }else{
                myWebView.setVisibility(View.VISIBLE);
                hideProgressDialog();
            }
        }
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("api.venmo.com") || url.contains("localhost")) {
                // This is an OAuth page, do not override
                view.loadUrl(url);
            }else {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(i);
            }
            return true;
            /* Otherwise, the link is not for a page on my site, so launch the browser
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            startActivity(intent);
            return true;*/
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);

        // Save the state of the WebView
        myWebView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);

        // Restore the state of the WebView
        myWebView.restoreState(savedInstanceState);
    }


/*
    @Override
    public void onPause() {
        super.onPause();
            hideProgressDialog();

        Method pause = null;
        try {
            pause = WebView.class.getMethod("onPause");
        } catch (SecurityException e) {
            // Nothing
        } catch (NoSuchMethodException e) {
            // Nothing
        }
        if (pause != null) {
            try {
                pause.invoke(myWebView);
            } catch (InvocationTargetException e) {
            } catch (IllegalAccessException e) {
            }
        } else {
            // No such method.  Stores the current URL.
            String suspendUrl = myWebView.getUrl();
            //SharedPreferences settings = getSharedPreferences(MainActivity.MYPREFS, 0);
            //SharedPreferences.Editor ed = settings.edit();
            //ed.putString(PREF_URL, suspendUrl);
            //ed.commit();
            // And loads a URL without any processing.
            myWebView.clearView();
            myWebView.loadUrl(WEBPAGE_NOTHING);
        }
    }*/

    //returns the url passed to the activity from the main activity, in this case the URL to go to
    private String getIntentMessage() {
        Intent intent = getIntent();
        return intent.getStringExtra("xyz.skylar.justthetip.URL");
    }
    private void showProgressDialog(View view) {
        if (!showingPD){
            pDialog = ProgressDialog.show(view.getContext(), "","Just a moment...", false);
            showingPD = true;
        }
    }
    private void hideProgressDialog() {
        if(pDialog != null) {
            pDialog.dismiss();
            showingPD = false;
        }
    }
}
