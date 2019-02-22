package com.tirtakapps.instasave;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class browser extends AppCompatActivity {

    WebView webView;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_browser);

        webView = (WebView) findViewById(R.id.webView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callMain(v);
            }
        });
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        fab2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        followAccount(webView.getUrl());
                    }
                }
        );

        webView.setWebChromeClient(new WebChromeClient());
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        if(URLUtil.isValidUrl(getIntent().getStringExtra("url"))){
            webView.loadUrl(getIntent().getStringExtra("url"));
        }else if (URLUtil.isValidUrl("https://www.instagram.com/" + getIntent().getStringExtra("url"))){
            webView.loadUrl("https://www.instagram.com/" + getIntent().getStringExtra("url"));
        }else {
            Toast.makeText(getApplicationContext(), "Cant reach to the given url", Toast.LENGTH_SHORT).show();
            webView.loadUrl("https://www.instagram.com/");
        }

    }

    public void callMain(View v){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, webView.getUrl() + "media/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject tmp = new JSONObject(response);
                            if(tmp.has("items")){
                                Intent i = new Intent(getApplicationContext(), userPage.class);
                                i.putExtra("url", webView.getUrl());
                                startActivity(i);
                            }else {
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                i.putExtra("url", webView.getUrl());
                                startActivity(i);
                            }
                        } catch (JSONException e) {
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            i.putExtra("url", webView.getUrl());
                            startActivity(i);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

    public void followAccount(String url){
        final MyQTLiteDatabase db = new MyQTLiteDatabase(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "media/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject tmp = new JSONObject(response);
                            if(tmp.has("items")){
                                JSONObject userInfo = new JSONObject(tmp.getJSONArray("items").get(0).toString());
                                db.newRow(userInfo.getJSONObject("user").getString("username"), userInfo.getString("link"));
                                Intent i = new Intent(getApplicationContext(), FollowAccount.class);
                                startActivity(i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }

}
