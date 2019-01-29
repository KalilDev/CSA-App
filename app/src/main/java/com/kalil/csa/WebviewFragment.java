package com.kalil.csa;

// android.animation
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;

// android.Context
import android.content.Context;

// android.net
import android.net.Uri;

// android.os
import android.os.Bundle;

// android.util
import android.util.Log;

// android.view
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// android.webkit
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

// java.io
import java.io.PrintWriter;
import java.io.StringWriter;


// Actual fragment
public class WebviewFragment extends android.support.v4.app.Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    static final String USERNAME = "username";
    static final String PASSWORD = "password";

    // Set up the Spinner.
    private View mProgressView;
    private View mWebView;
    boolean loadingFinished;
    boolean redirect;

    // Set up the Webview.
    private WebView colegio;

    // Set up the fields
    private String mUsername;
    private String mPassword;

    private String data;
    private String urlcsa;
    // Set up the constant URL needed to login

    private OnFragmentInteractionListener mListener;

    public WebviewFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     */
    public static WebviewFragment newInstance(String username, String password) {
        WebviewFragment fragment = new WebviewFragment();
        Bundle args = new Bundle();
        args.putString(USERNAME, username);
        args.putString(PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mUsername = getArguments().getString(USERNAME);
            mPassword = getArguments().getString(PASSWORD);
        }

        // Gather the data from the intent to prepare
        // the POST request in order to login
        data = "UserName=" + mUsername + "&Password=" + mPassword + "&RememberMe=false";
    }

    /*@Override
    public void onBackPressed() {
        if(colegio.canGoBack()) {
            // If we can go back to a previous webpage, do it.
            colegio.goBack();
        } else {
            // Else, go back to the login activity
            openLogin();
        }
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_webview, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();
        urlcsa = "http://mobile.csa.g12.br/EducaMobile/Account/Login";


        // Set up basic webview settings
        colegio = (WebView)getView().findViewById(R.id.webView);
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
        mWebView = getView().findViewById(R.id.webView);
        mProgressView = getView().findViewById(R.id.progress);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
