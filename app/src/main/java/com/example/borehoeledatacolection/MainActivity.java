package com.example.borehoeledatacolection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    EditText b_name, b_description, p_name, o_name;
    TextView longitude, latitude;
    Button btn_get_location, btnSubmit, btnEdit, btnDelete, btnViewAll,btnExport;
    Spinner b_type, b_status;
    DBHepler db;
    FusedLocationProviderClient fusedLocationProviderClient;
    double final_latitude;
    double final_longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b_name = findViewById(R.id.b_name);
        b_description = findViewById(R.id.b_description);
        btn_get_location = findViewById(R.id.btn_get_location);
        btnSubmit = findViewById(R.id.btnSubmit);
//        btnEdit = findViewById(R.id.btnEdit);
        btnExport = findViewById(R.id.btnExport);
//        btnDelete = findViewById(R.id.btnDelete);
//        btnViewAll = findViewById(R.id.btnViewAll);
        b_type = findViewById(R.id.b_type);
        b_status = findViewById(R.id.b_status);
        longitude = findViewById(R.id.longitude);
        latitude = findViewById(R.id.latitude);
        p_name = findViewById(R.id.person_name);
        o_name = findViewById(R.id.org_name);
        db = new DBHepler(this);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // get location
        btn_get_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
                }
            }
        });

        //save data
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String borehole_name =  b_name.getText().toString();
                String borehole_description = b_description.getText().toString();
                String borehole_type = b_type.getSelectedItem().toString();
                String borehole_status = b_status.getSelectedItem().toString();
                String borehole_latitude = String.valueOf(final_latitude);
                String borehole_longitude = String.valueOf(final_longitude);
                String person_name =  p_name.getText().toString();
                String org_name = o_name.getText().toString();
                String date_collected = java.text.DateFormat.getDateTimeInstance().format(new Date());

                Boolean checkInsertData = db.insert_data(null,borehole_name,borehole_description,borehole_status,borehole_type,borehole_latitude,borehole_longitude,person_name,org_name,date_collected);
                if (checkInsertData == true){
                    Toast.makeText(MainActivity.this, "Borehole data has been captured successfully", Toast.LENGTH_SHORT).show();
                    latitude.setText(Html.fromHtml("Borehole Latitude"));
                    longitude.setText(Html.fromHtml("Borehole Longitude"));
                    b_name.getText().clear();
                    b_description.getText().clear();
                    b_type.setSelection(0);
                    b_status.setSelection(0);
                    btnSubmit.setEnabled(false);


                }else{
                    Toast.makeText(MainActivity.this, "An error has occurred data could not be saved. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        });


        btnExport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(MainActivity.this, Main2Activity.class);
                MainActivity.this.startActivity(myIntent);
            }
        });
    }

    private void getLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    Activity#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for Activity#requestPermissions for more details.
                return;
            }
        }
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if (location != null) {
                    try {
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());

                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(), location.getLongitude(), 1
                        );
                        latitude.setText(Html.fromHtml("<b> Latitude </b> :" + addresses.get(0).getLatitude()));
                        final_latitude = addresses.get(0).getLatitude();

                        longitude.setText(Html.fromHtml("<b> Longitude </b> :" + addresses.get(0).getLongitude()));
                        final_longitude = addresses.get(0).getLongitude();

                        btnSubmit.setEnabled(true);


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

}
