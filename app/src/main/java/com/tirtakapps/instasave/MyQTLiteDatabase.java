package com.tirtakapps.instasave;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.Telephony;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by gamew on 8.06.2017.
 */

public class MyQTLiteDatabase extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "dbAcc.dp";
    private static  final String TABLE_NAME = "FollowingAccounts";
    private static  final String COLUMN_NAME_ID = "Id";
    private static  final String COLUMN_NAME_ACC = "AccountUserName";
    private static  final String COLUMN_NAME_LPURL = "LastPhotoUrl";

    public MyQTLiteDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_DATABASE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_NAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                COLUMN_NAME_ACC + " TEXT," +
                COLUMN_NAME_LPURL + " TEXT)";
        db.execSQL(SQL_CREATE_DATABASE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

     public void newRow(String username, String lastPhotoUrl){
         SQLiteDatabase db = getWritableDatabase();
         ContentValues contentValues = new ContentValues();
         contentValues.put(COLUMN_NAME_ACC, username);
         contentValues.put(COLUMN_NAME_LPURL, lastPhotoUrl);

         db.insert(TABLE_NAME, null, contentValues);
         db.close();
     }

     public String getLastPhotoUrl(String username){
         String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME_ACC + " = " + username;
         String lastPhotoUrl = null;

         SQLiteDatabase db = getReadableDatabase();
         Cursor cursor = db.rawQuery(query, null);
         cursor.moveToFirst();
         if(cursor.getCount() > 0){
             lastPhotoUrl = cursor.getString(2);
         }
         cursor.close();
         db.close();
         
         return lastPhotoUrl;
     }

     public void newPhoto(String username, String lastPhotoUrl){
         SQLiteDatabase db = getWritableDatabase();
         ContentValues values = new ContentValues();
         values.put(COLUMN_NAME_ACC, username);
         values.put(COLUMN_NAME_LPURL, lastPhotoUrl);

         db.update(TABLE_NAME, values, COLUMN_NAME_ACC + " = ?", new String[]{username});
     }

     public void deleteRow(String username){
         SQLiteDatabase db = getWritableDatabase();
         db.delete(TABLE_NAME, COLUMN_NAME_ACC + " = ?", new String[]{username});
         db.close();
     }

     public ArrayList<HashMap<String, String>> rows(){
         SQLiteDatabase db = getReadableDatabase();
         String query = "SELECT * FROM " + TABLE_NAME;
         Cursor cursor = db.rawQuery(query, null);
         ArrayList<HashMap<String,String>> rowList = new ArrayList<HashMap<String, String>>();

         if(cursor.moveToFirst()){
             do {
                 HashMap<String, String> map = new HashMap<String, String>();
                 for(int i = 0; i < cursor.getColumnCount(); i++){
                     map.put(cursor.getColumnName(i), cursor.getString(i));
                 }
                 rowList.add(map);
             }while (cursor.moveToNext());
         }
         db.close();

         return rowList;
     }
}
