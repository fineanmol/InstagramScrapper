package com.tirtakapps.instasave;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FollowAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_account);

        ArrayList<HashMap<String, String>> save = new ArrayList<HashMap<String,String>>();
        List<String> usernames = new ArrayList<String>();
        MyQTLiteDatabase db = new MyQTLiteDatabase(getApplicationContext());
        save = db.rows();
        if(save.size() == 0){
            Toast.makeText(getApplicationContext(), "You have not following any account. Let's start following.", Toast.LENGTH_LONG).show();
        }else {
            for(int i = 0; i < save.size(); i++){
                usernames.add(save.get(i).get("AccountUserName"));
            }
        }

        final ListAdapter listAdapter = new CustomListAdapter(getApplicationContext(), usernames);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent i = new Intent(getApplicationContext(), userPage.class);
                        i.putExtra("url", "https://www.instagram.com/" + listAdapter.getItem(position).toString() + "/");
                        startActivity(i);
                    }
                }
        );
    }
}
