package com.codedevstudio.orders.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.codedevstudio.orders.R;

import java.util.HashMap;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AttachCardActivity extends AppCompatActivity {
    static final String EXIT_URL = "http://exit_activity/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attach_card);
        final WebView wv = (WebView) findViewById(R.id.attach_card_webview);
        wv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (Objects.equals(url, EXIT_URL)){
                    finish();
                }

                view.loadUrl(url);
                return false;
            }
        });

        wv.getSettings().setJavaScriptEnabled(true);

        Call<HashMap<String,String>> call  =  App.getApi().attachCard();
        call.enqueue(new Callback<HashMap<String, String>>() {
            @Override
            public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                HashMap<String,String> body = response.body();

                if (body.containsKey("redirect_url")){
                    wv.loadUrl(body.get("redirect_url"));
                }
            }

            @Override
            public void onFailure(Call<HashMap<String, String>> call, Throwable t) {

            }
        });
    }
}
