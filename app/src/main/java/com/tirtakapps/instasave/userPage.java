package com.tirtakapps.instasave;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TabHost;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class userPage extends AppCompatActivity {

    final List<String> urls = new ArrayList<String>();
    List<Integer> selections = new ArrayList<Integer>();
    List<JSONObject> images = new ArrayList<JSONObject>();
    GridView gridView;
    ActionBar actionBar;
    Menu menu;
    FloatingActionButton fap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        actionBar = getActionBar();

        gridView = (GridView) findViewById(R.id.gridView);
        fap = (FloatingActionButton) findViewById(R.id.floatingActionButton3);
        fap.setEnabled(false);

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        final StringRequest stringRequest = new StringRequest(Request.Method.GET, getIntent().getStringExtra("url") + "media/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject datas = new JSONObject(response);
                            JSONArray items = datas.getJSONArray("items");
                            for(int i = 0; i < items.length(); i++){
                                JSONObject tmp = items.getJSONObject(i).getJSONObject("images").getJSONObject("standard_resolution");
                                urls.add(tmp.getString("url"));
                                images.add(items.getJSONObject(i));
                                Log.e("url: ", tmp.getString("url"));
                            }
                            urls.add(items.getJSONObject(0).getJSONObject("user").getString("profile_picture"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "This account may be private. You can not download photos from this account.", Toast.LENGTH_LONG).show();
                        }
                        gridView.setAdapter(new ImageAdapter(getApplicationContext(), urls));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);


        gridView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        try {
                            if(position < images.size()){
                                i.putExtra("url", images.get(position).getString("link").toString());
                            }else {
                                i.putExtra("urlPP", urls.get(position).toString());
                                i.putExtra("name", images.get(0).getJSONObject("user").getString("username").toString());
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(i);
                    }
                }
        );

        gridView.setChoiceMode(GridView.CHOICE_MODE_MULTIPLE);

        gridView.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        fap.setEnabled(true);
                        View viewPrev = gridView.getChildAt(position);
                        if(selections.contains(position)){
                            selections.remove(selections.indexOf(position));
                            viewPrev.setBackgroundColor(Color.WHITE);
                        }else {
                            selections.add(position);
                            viewPrev.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                        }
                        if(selections.size() < 1){
                            fap.setEnabled(false);
                        }
                        Log.e("clicked: ", position + "");
                        return true;
                    }
                }
        );

        fap.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        downloadGroup();
                    }
                }
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_save, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_save:
                downloadGroup();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void downloadGroup(){
        ArrayList<String> datas = new ArrayList<String>();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        for(int i = 0; i < selections.size() - 1; i++){
            try {
                datas.add(images.get(selections.get(i)).getString("link").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        intent.putStringArrayListExtra("urlDatas", datas);
        startActivity(intent);
    }
}
