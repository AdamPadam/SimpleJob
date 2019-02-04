package com.codedevstudio.orders.activities;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.codedevstudio.orders.R;
import com.codedevstudio.orders.models.Order;
import com.codedevstudio.orders.models.UserCredentials;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PayActivity extends AppCompatActivity {
    static final String URL_TAG  = "PAY_URL";
    static final String EXIT_URL = "http://exit_activity/";
    WebView webView;

    public static Intent newIntent(Context packageContext, String payUrl){
        Intent intent = new Intent(packageContext, PayActivity.class);
        intent.putExtra(URL_TAG, payUrl);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay);
        webView = (WebView) findViewById(R.id.payView);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            String payUrl = extras.getString(URL_TAG);

            webView.getSettings().setJavaScriptEnabled(true);
            webView.setWebViewClient(new WebViewClient(){
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (Objects.equals(url, EXIT_URL)){
                        finish();
                    }

                    view.loadUrl(url);
                    return false;
                }
            });
//            webView.setWebViewClient(wvc);
            webView.loadUrl(payUrl);
        }
    }
}
