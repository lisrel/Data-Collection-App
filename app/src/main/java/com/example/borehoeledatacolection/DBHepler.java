package com.example.borehoeledatacolection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHepler extends SQLiteOpenHelper {
    public DBHepler(Context context) {
        super(context, "Borehole_data.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table Borehole_data (b_id INTEGER primary key AUTOINCREMENT, b_name text,b_description text, b_status text,b_type text, b_latitude text, b_longitude text, " +
                "person_name text, org_name text,date_collected text)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists Borehole_data");
    }

    public Boolean insert_data(String b_id,String b_name, String b_description, String b_status, String b_type, String b_latitude,String b_longitude, String person_name, String org_name, String date_collected){
        SQLiteDatabase db =  this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("b_id",b_id);
        contentValues.put("b_name",b_name);
        contentValues.put("b_description",b_description);
        contentValues.put("b_status",b_status);
        contentValues.put("b_type",b_type);
        contentValues.put("b_latitude",b_latitude);
        contentValues.put("b_longitude",b_longitude);
        contentValues.put("person_name",person_name);
        contentValues.put("org_name",org_name);
        contentValues.put("date_collected",date_collected);

        long result =  db.insert("Borehole_data", null, contentValues);

        if (result == -1){
            return false;
        }else{
            return true;
        }
    }

    public Boolean update_data(String b_id,String b_name, String b_description, String b_status, String b_type, String b_x_coord,String b_y_coord ){
        SQLiteDatabase db =  this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("b_name",b_name);
        contentValues.put("b_description",b_description);
        contentValues.put("b_status",b_status);
        contentValues.put("b_type",b_type);
        contentValues.put("b_x_coord",b_x_coord);
        contentValues.put("b_y_coord",b_y_coord);

        Cursor cursor = db.rawQuery("SELECT * FROM Borehole_data where b_id =?",new String[]{b_id});

        if (cursor.getCount()>0) {

            long result = db.update("Borehole_data", contentValues, "id=?", new String[]{b_id});

            if (result == -1) {
                return false;
            } else {
                return true;
            }
        }else{
            return false;
        }
    }

    public Boolean delete_data(String b_id){
        SQLiteDatabase db =  this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Borehole_data where b_id =?",new String[]{b_id});

        if (cursor.getCount()>0) {

            long result = db.delete("Borehole_data", "id=?", new String[]{b_id});

            if (result == -1) {
                return false;
            } else {
                return true;
            }
        }else{
            return false;
        }
    }

    public Cursor get_all_data(){
        SQLiteDatabase db =  this.getWritableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM Borehole_data",null);
        return cursor;
    }
}
