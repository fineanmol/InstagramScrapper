package com.tirtakapps.instasave;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    String queryResult = "", author, title, id;
    WebView webView;
    JSONObject reader;
    EditText editText;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        imageView = (ImageView) findViewById(R.id.imageView);

        //Picasso.with(getApplicationContext()).load("https://media1.popsugar-assets.com/files/thumbor/YMFNX_9jQ6rt8tPMBjvcPDbi2oc/fit-in/2048xorig/filters:format_auto-!!-:strip_icc-!!-/2016/07/27/009/n/1922283/a1841557c1548e96_GettyImages-510051932/i/Mackenzie-Foy.jpg").into(imageView);

        if(getIntent().getStringExtra("url") != null && getIntent().getStringExtra("url") != ""){
            editText.setText(getIntent().getStringExtra("url"));
            new JsonTask().execute("https://api.instagram.com/oembed/?url=" + editText.getText().toString());
        }else if(getIntent().getStringArrayListExtra("urlDatas") != null){
            ArrayList<String> tmpAL = getIntent().getStringArrayListExtra("urlDatas");
            for(int i = 0; i < tmpAL.size(); i++){
                editText.setText(tmpAL.get(i));
                new JsonTask().execute("https://api.instagram.com/oembed/?url=" + tmpAL.get(i));
            }
        }
        if(getIntent().getStringExtra("urlPP") != null && getIntent().getStringExtra("urlPP") != ""){
            Picasso.with(getApplicationContext()).load(getIntent().getStringExtra("urlPP")).into(imageView);
            author = getIntent().getStringExtra("name");
            title = "Profile Picture";
            id = "" + new Random();
            savePhoto(((BitmapDrawable)imageView.getDrawable()).getBitmap());
        }

        Intent serviceIntent = new Intent(this, FollowingService.class);
        startService(serviceIntent);
        PendingIntent alarmIntent = PendingIntent.getService(getApplicationContext(), 0 , serviceIntent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_HOUR, AlarmManager.INTERVAL_HOUR, alarmIntent);
    }

    public void btnDownload(View v) throws JSONException {
        new JsonTask().execute("https://api.instagram.com/oembed/?url=" + editText.getText().toString());
    }

    public void btnPaste(View v) {
        editText.setText("");
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);

        String pasteData = "";

// If it does contain data, decide if you can handle the data.
        if (!(clipboard.hasPrimaryClip())) {


        } else if (!(clipboard.getPrimaryClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN))) {

            // since the clipboard has data but it is not plain text

        } else {

            //since the clipboard contains plain text.
            ClipData.Item item = clipboard.getPrimaryClip().getItemAt(0);

            // Gets the clipboard as text.
            pasteData = item.getText().toString();
        }
        editText.setText(pasteData);
    }

    public void btnGo(View v) {
        //webView.loadUrl(editText.getText().toString());
        Intent i = new Intent(this, browser.class);
        i.putExtra("url", editText.getText().toString());
        startActivity(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(getIntent().getStringExtra("mode") == null || getIntent().getStringExtra("mode") == "download"){
                        savePhoto(((BitmapDrawable)imageView.getDrawable()).getBitmap());
                    }else if(getIntent().getStringExtra("mode") != null && getIntent().getStringExtra("mode") == "share"){
                        photoShare();
                    }

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();


        }

        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            queryResult = result;

            try {
                reader = new JSONObject(queryResult);
                String url = reader.getString("thumbnail_url");
                author = reader.getString("author_name");
                title = reader.getString("title");
                id = reader.getString("media_id");
                Picasso.with(getApplicationContext()).load(url).into(imageView);
                if(getIntent().getStringExtra("mode") == null || getIntent().getStringExtra("mode").contains("download")) {
                    Log.d("mode: ", "download");
                    if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);
                    } else {
                        savePhoto(((BitmapDrawable) imageView.getDrawable()).getBitmap());
                    }
                }else if(getIntent().getStringExtra("mode") != null && getIntent().getStringExtra("mode").contains("share")){
                    Log.d("mode: ", "share");
                    photoShare();
                }
            } catch (Throwable t) {
            }
        }
    }


    String fullPath = String.valueOf(Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES));

    public void savePhoto(Bitmap bm){
        try {
            File dir = new File(fullPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            OutputStream fOut = null;
            File file = new File(fullPath, author + "_" + title + "_" + id + ".png");
            file.createNewFile();
            fOut = new FileOutputStream(file);

// 100 means no compression, the lower you go, the stronger the compression
            bm.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();

            MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());
            Log.e("saveToExternalStorage()", "success" + fullPath);
            Toast.makeText(getApplicationContext(), "Image Saved", Toast.LENGTH_SHORT).show();
            return ;

        } catch (Exception e) {
            Log.e("saveToExternalStorage()", e.getMessage());
            return ;
        }

    }

    public void btnShare(View v){
        photoShare();
    }

    public void photoShare(){
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("image/png");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        ((BitmapDrawable)imageView.getDrawable()).getBitmap().compress(Bitmap.CompressFormat.PNG, 100, bytes);
        File f = new File(Environment.getExternalStorageDirectory() + File.separator + "temporary_file_" + author + "_" + title + ".png");
        try{
            f.createNewFile();
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(bytes.toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        share.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/" + "temporary_file_" + author + "_" + title + ".png"));
        share.putExtra(Intent.EXTRA_TEXT, " Photo from "  + author + " called as " + title + "      Shared by " + getResources().getString(R.string.app_name) + " by Tirtak Apps");
        startActivity(Intent.createChooser(share, "Share Image"));
    }
}
