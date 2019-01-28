package com.kalil.csa;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.PrintWriter;
import java.io.StringWriter;

public class WebviewActivity extends AppCompatActivity {
    // Set up the Spinner.
    private View mProgressView;
    private View mWebView;
    boolean loadingFinished;
    boolean redirect;

    // Set up the Webview.
    private WebView colegio;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Gather the data from the intent to prepare
        // the POST request in order to login
        Bundle login = getIntent().getExtras();
        String data = "UserName=" + login.getString("username") + "&Password=" + login.getString("password") + "&RememberMe=false";

        // Set up the constant URL needed to login
        String urlcsa = "http://mobile.csa.g12.br/EducaMobile/Account/Login";

        // Set up the layout
        setContentView(R.layout.activity_webview);

        // Set up basic webview settings
        colegio = (WebView)findViewById(R.id.webView);
        WebSettings websettings = colegio.getSettings();
        websettings.setJavaScriptEnabled(true);

        // Set up the spinner to show up when any page is loading
        colegio.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(
                    WebView view, WebResourceRequest request) {
                if (!loadingFinished) {
                    redirect = true;
                }

                setLoading(true);
                colegio.loadUrl(request.getUrl().toString());
                return true;
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                if (!redirect) {
                    setLoading(false);
                }

                if (loadingFinished && !redirect) {
                    setLoading(false);
                } else {
                    redirect = false;
                }
            }
        });

        try {
            // Try to send the POST data to login.
            colegio.postUrl(urlcsa, data.getBytes("UTF-8"));
            Log.i(LoginActivity.TAG, "Connection succeeded!");
        } catch (Exception e) {
            // Oops, there was an error, help me with an strace.
            Log.w(LoginActivity.TAG, "Connection failed");
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            Log.e(LoginActivity.TAG, errors.toString());
        }

        // Get the ID of the spinner and the Webview
        // so that it can hide itself or the webview.
        mWebView = findViewById(R.id.webView);
        mProgressView = findViewById(R.id.progress);
    }


    @Override
    public void onBackPressed() {
        if(colegio.canGoBack()) {
            // If we can go back to a previous webpage, do it.
            colegio.goBack();
        } else {
            // Else, go back to the login activity
            openLogin();
        }
    }


    // Start the login activity
    public void openLogin() {
        Intent login = new Intent(this, LoginActivity.class);
        startActivity(login);
        finish();
    }


    // This function defines the param as the new
    // value for the loadingFinished variable, so
    // that i can do stuff once a change is made.
    private void setLoading(boolean newLoading){
        if(newLoading != loadingFinished) {
            loadingFinished = newLoading;
            showProgress(loadingFinished);
        }
    }


    // Shows the progress UI and hides the webview.
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mWebView.setVisibility(show ? View.GONE : View.VISIBLE);
        mWebView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mWebView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
}
