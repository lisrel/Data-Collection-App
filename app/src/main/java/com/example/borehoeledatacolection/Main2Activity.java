package com.example.borehoeledatacolection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.Calendar;
import java.util.Date;

public class Main2Activity extends AppCompatActivity {
    DBHepler db;
    TextView show_results;
    Button btnExport;
    private Context mContext=Main2Activity.this;

    private static final int REQUEST = 112;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        show_results = findViewById(R.id.textView2);
        btnExport = findViewById(R.id.btnExport);
        db = new DBHepler(this);

        Cursor res = db.get_all_data();
        if (res.getCount() == 0){
            show_results.setText("No data has been added");
        }else{
            StringBuffer buffer = new StringBuffer();
            while (res.moveToNext()){
                buffer.append("Data Collected On : " + res.getString(9)+ "\n");
                buffer.append("Data collector Name : " + res.getString(7)+ "\n");
                buffer.append("Organisation Name : " + res.getString(8)+ "\n");
                buffer.append("Borehole ID : " + res.getString(0)+ "\n");
                buffer.append("Borehole Name : " + res.getString(1)+ "\n");
                buffer.append("Borehole Description : " + res.getString(2)+ "\n");
                buffer.append("Borehole Status : " + res.getString(3)+ "\n");
                buffer.append("Borehole Type : " + res.getString(4)+ "\n");
                buffer.append("Borehole Latitude : " + res.getString(5)+ "\n");
                buffer.append("Borehole Longitude : " + res.getString(6)+ "\n\n\n");
            }
            show_results.setText(buffer);
        }

        btnExport.setOnClickListener((View v) -> {

            if (Build.VERSION.SDK_INT >= 23) {
                String[] PERMISSIONS = {android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (!hasPermissions(mContext, PERMISSIONS)) {
                    ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, REQUEST );
                } else {
                    export();

                }
            } else {
                export();

            }

        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    export();
                } else {
                    Toast.makeText(mContext, "The app was not allowed to read your store.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private boolean hasPermissions(Context mContext, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && mContext != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(mContext, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void export(){


        File dir=new File( Environment.getExternalStorageDirectory(), "/BoreholeData");

        long current_time = System.currentTimeMillis();
        if(!dir.exists()){
            dir.mkdir();
        }
        try
        {

            CSVWriter csvWrite = new CSVWriter(new FileWriter(dir.getAbsolutePath()+"/borehole_data"+current_time+".csv"));
            Cursor curCSV = db.get_all_data();
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext())
            {
                //Which column you want to exprort
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2),curCSV.getString(3),curCSV.getString(4),
                        curCSV.getString(5),curCSV.getString(6),curCSV.getString(7),curCSV.getString(8),curCSV.getString(9)};
                csvWrite.writeNext(arrStr);


            }

            Toast.makeText(Main2Activity.this, "Data has been exported to" +dir.getAbsolutePath() , Toast.LENGTH_LONG).show();

            csvWrite.close();
            curCSV.close();
        }
        catch(Exception sqlEx)
        {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }
}
