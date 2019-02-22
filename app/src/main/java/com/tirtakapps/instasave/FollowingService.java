package com.tirtakapps.instasave;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class FollowingService extends Service {

    public FollowingService() {
        Log.d("following service: ", "started");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("following service: ", "started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final MyQTLiteDatabase db = new MyQTLiteDatabase(getApplicationContext());
        ArrayList<HashMap<String, String>> datas = db.rows();
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        for(int i = 0; i < datas.size(); i++){
            final String username = datas.get(i).get("AccountUserName");
            final String lastPhotoUrl = datas.get(i).get("LastPhotoUrl");
            StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.instagram.com/" + username + "/media/",
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject data = new JSONObject(response);
                                JSONObject array = new JSONObject(data.getJSONArray("items").get(0).toString());
                                String lastPhoto = array.getString("link");
                                if(!lastPhotoUrl.equals(lastPhoto)){
                                    db.newPhoto(username, lastPhoto);
                                    FollowingService.notify(username, lastPhoto, getApplicationContext());
                                }
                                Log.d("following service: ", username + " checked, last photo" + lastPhoto + ", photo on database " + lastPhotoUrl);
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
        return Service.START_STICKY;
    }

    public static void notify(String username, String photoUrl, Context context){
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.putExtra("url", photoUrl);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        Intent share = new Intent(context, MainActivity.class);
        share.putExtra("mode", "share");
        share.putExtra("url", photoUrl);
        TaskStackBuilder stackBuilder2 = TaskStackBuilder.create(context);
        stackBuilder2.addParentStack(MainActivity.class);
        stackBuilder2.addNextIntent(share);
        PendingIntent pendingShareIntent = stackBuilder2.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_share_black_24dp)
                .addAction(R.drawable.ic_file_download_black_24dp, "Save", resultPendingIntent)
                .addAction(R.drawable.ic_share_black_24dp, "Share",pendingShareIntent)
                .setContentTitle(username + " shared new photo in instagram")
                .setContentText(username + " shared new photo in instagram. You can view it from " + R.string.app_name + " and you can download it");
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }

    private void getProfilePhoto(String username, final ImageView ımageView){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, "https://www.instagram.com/" + username + "/media/",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject tmp = new JSONObject(response);
                            JSONObject tmpArray = new JSONObject(tmp.getJSONArray("items").get(0).toString());
                            Picasso.with(getApplicationContext()).load(tmpArray.getJSONObject("user").getString("profile_picture")).into(ımageView);
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

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
