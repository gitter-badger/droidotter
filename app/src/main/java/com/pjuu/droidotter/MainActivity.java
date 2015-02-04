package com.pjuu.droidotter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends Activity {

    private WebView webView;
    private ProgressBar prgrsBar;

    public Boolean hasConnection() {
        // Get a ConnectivityManager
        ConnectivityManager cm =
                (ConnectivityManager)getApplicationContext()
                        .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public void onBackPressed (){

        if (webView.isFocused() && webView.canGoBack()) {
            webView.goBack();
        }
        else {
            super.onBackPressed();
            finish();
        }
    }
    //catching back key to take back if history exists doesnot finish Activity
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public class PjuuWebChromeClient extends WebChromeClient {

    }

    public class PjuuWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (hasConnection()) {
                if (url.contains("pjuu.com")) {
                    view.loadUrl(url);
                } else {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(i);
                }
            } else {
                Toast.makeText(getApplicationContext(), "No connection", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            prgrsBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            prgrsBar.setVisibility(View.GONE);
        }

        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            // Will display a standard error message if the application can't connect
            view.loadUrl("file:///android_asset/error.html");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*
         * Activity stuff
         */
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        /*
         * WebView stuff
         */
        webView = (WebView)findViewById(R.id.webview);
        WebSettings browserSettings = webView.getSettings();
        // We need Javascript on the site
        browserSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new PjuuWebViewClient());
        webView.setWebChromeClient(new PjuuWebChromeClient());
        // The URL is stored in values/strings.xml
        webView.loadUrl(getString(R.string.app_url));

        // Progress bar
        prgrsBar = (ProgressBar)findViewById(R.id.progress_bar);
    }

}
