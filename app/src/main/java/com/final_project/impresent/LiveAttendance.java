package com.final_project.impresent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LiveAttendance extends AppCompatActivity {
    private LocationRequest locationRequest;
    TextView tv;
    double latitude=0.0,longitude=0.0;
    String sem, className, subject, timer, subid, dateToStr, timeToStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_attendance);


        Intent intent = getIntent();
        sem = intent.getStringExtra("sem");
        className = intent.getStringExtra("class");
        subject = intent.getStringExtra("subject");
        timer = intent.getStringExtra("timer");
        subid="";
        for (int i = 0; subject.charAt(i + 1) != ':'; i++) {
            subid += subject.charAt(i);
        }
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Attendance").child(sem).child(className).child(subid);
        // get current date
        //reference.child()
        Date today = new Date();

        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.MINUTE,Integer.parseInt(timer));


        dateToStr = format.format(today);
        timeToStr = timeFormat.format(calendar.getTime());
        System.out.println(sem + className + subject + timer + subid);
        System.out.println(dateToStr + " " + timeToStr);


        // testing starts

        // get location




        //Button getLocation = findViewById(R.id.button_liveAttendance_button);
        tv = findViewById(R.id.textView_liveAttendance);



        getLocation();



    }

    private void getLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(LiveAttendance.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if(isGPSEnabled()){
                    LocationServices.getFusedLocationProviderClient(LiveAttendance.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(LiveAttendance.this).removeLocationUpdates(this);
                            if(locationResult != null && locationResult.getLocations().size()>0){
                                int index = locationResult.getLocations().size()-1;
                                latitude = locationResult.getLocations().get(index).getLatitude();
                                longitude = locationResult.getLocations().get(index).getLongitude();
                                tv.setText(""+latitude+" "+longitude);
                                TextView tv2 = findViewById(R.id.textView_liveAttendance_2);
                                tv2.setText("Attendance Started");
                                commitToDB();
                            }

                        }
                    }, Looper.getMainLooper());

                }
                else{
                    turnOnGPS();
                }
            }
            else{
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    private void commitToDB() {
        String locationToStr = ""+latitude+","+longitude;
        // firebase key cannot contain '.' changed to ':'
        String locStr="";
        for(int i=0;i<locationToStr.length();i++){
            if(locationToStr.charAt(i)=='.') locStr+=':';
            else    locStr+=locationToStr.charAt(i);
        }
        System.out.println(locationToStr);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Attendance").child(sem).child(className).child(subid).child(dateToStr).child(timeToStr).child(locStr);
        reference.setValue(true);
    }

    private void turnOnGPS() {


        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                    Toast.makeText(LiveAttendance.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(LiveAttendance.this, 2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });
    }

    private boolean isGPSEnabled() {
        LocationManager locationManager = null;
        boolean isEnabled = false;
        if (locationManager == null) {
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }
}