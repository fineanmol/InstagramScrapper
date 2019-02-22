package com.tirtakapps.instasave;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gamew on 8.06.2017.
 */

public class CustomListAdapter extends BaseAdapter {

    List<String> usernames = new ArrayList<String>();
    Context context;
    private static LayoutInflater inflater = null;

    public CustomListAdapter(Context context, List<String> usernames) {
        this.context = context;
        this.usernames = usernames;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return usernames.size();
    }

    @Override
    public Object getItem(int position) {
        return usernames.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null){
            view = inflater.inflate(R.layout.list_item, null);
        }

        Button button = (Button) view.findViewById(R.id.btnDelete);
        TextView textView = (TextView) view.findViewById(R.id.username);
        ImageView imageView = (ImageView) view.findViewById(R.id.profilePhoto);

        textView.setText(usernames.get(position));
        getProfilePhoto(usernames.get(position), imageView);

        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MyQTLiteDatabase db = new MyQTLiteDatabase(context);
                        db.deleteRow(usernames.get(position));
                        usernames.remove(position);
                        notifyDataSetChanged();
                    }
                }
        );

        return view;
    }

    private void getProfilePhoto(String username, final ImageView ımageView){
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.instagram.com/" + username + "/media/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject tmp = new JSONObject(response);
                            JSONObject tmpArray = new JSONObject(tmp.getJSONArray("items").get(0).toString());
                            Picasso.with(context).load(tmpArray.getJSONObject("user").getString("profile_picture")).into(ımageView);
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
