package com.final_project.impresent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executor;

public class MarkPresent extends AppCompatActivity {
    public static final int CAMERA_REQ_CODE = 102;
    Spinner spinnerClass, spinnerSubject;
    ImageButton verifyBiometric;
    ImageView biometricStatus;
    ImageView presentDone;
    Button markPresent;
    TextView textViewCoordinates;

    LocationRequest locationRequest;

    String name="",sem="",enroll="",email="",globalDate="",globalTime="",globalLocation="";
    String className="",subjectName="",subjectId="";
    double latitude=0.0,longitude=0.0;
    double range = 30.0;
    String globalDeviceID;
    DatabaseReference globalLocationReference;

    final boolean[] pushed = {false};

    ArrayList<String> classes = new ArrayList<>();
    ArrayList<String> subjects = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mark_present);

        spinnerClass = findViewById(R.id.spinner_markPresent_class);
        spinnerSubject = findViewById(R.id.spinner_markPresent_subject);
        verifyBiometric = findViewById(R.id.imageButton_markPresent_biometric);
        biometricStatus = findViewById(R.id.imageView_markPresent_biometricStatus);
        markPresent = findViewById(R.id.button_markPresent_markPresent);
        textViewCoordinates = findViewById(R.id.textView_markPresent_coordinates);
        presentDone = findViewById(R.id.imageView_markPresent_presentDone);


        getLocation();

        markPresent.setEnabled(false);

        Intent incomingIntent = getIntent();

        sem = incomingIntent.getStringExtra("sem");
        enroll = incomingIntent.getStringExtra("enroll");
        System.out.println(sem+" "+enroll);

        setClassesSubjectsSpinners(sem);


        verifyBiometric.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkBiometric();
            }
        });

        markPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(longitude==0.0 && latitude==0.0){
                    Toast.makeText(MarkPresent.this, "Location couldn't be found", Toast.LENGTH_SHORT).show();
                }
                else{
                    // extract subject id from subjectName
                    subjectId="";
                    for (int i = 0; subjectName.charAt(i + 1) != ':'; i++) {
                        subjectId += subjectName.charAt(i);
                    }
                    System.out.println(subjectId);
                    pushEnroll();
                }
            }
        });
    }

    private void pushEnroll() {
        // get Date and time
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a");
        String dateToStr = format.format(today);
        globalDate = dateToStr;
        String timeToStr = timeFormat.format(today);
        System.out.println(dateToStr+" "+timeToStr);
        
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Attendance").child(sem).child(className).child(subjectId).child(dateToStr);
        final DatabaseReference[] timeReference = new DatabaseReference[1];
        System.out.println("key "+reference.getKey());
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    boolean timeOver=true;
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        // getting times here
                        // continue from here       //////////////////////////////////////////////////////////////////////////////////
                        System.out.println(dsp.getKey());
                        String attendanceEndTime = dsp.getKey();
                        Date DBTime = null;
                        Date DeviceTime = null;
                        try {
                            DBTime = timeFormat.parse(attendanceEndTime);
                            String DBTimeToStr = timeFormat.format(DBTime);
                            DeviceTime = timeFormat.parse(timeToStr);
                            // compare with deviceTime(current device time)
                            if(DeviceTime.compareTo(DBTime)<0){
                                System.out.println("Valid times "+DeviceTime+" < "+DBTime);
                                timeOver = false;
                                globalTime = dsp.getKey();
                                timeReference[0] = reference.child(dsp.getKey());
                                if(matchLocation(timeReference[0])){
                                    System.out.println("if matchLocation true");
                                    break;
                                }
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Time from DB "+DBTime+" : "+DeviceTime);
                    }
                    if(timeOver){
                        Toast.makeText(MarkPresent.this, "Time is over now", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(MarkPresent.this, "No valid data in DB", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean matchLocation(DatabaseReference timeReference) {
        System.out.println("In matchLocation");

        final double[] distance = {0};
        final String[] DBLatitude = {""};
        final String[] DBLongitude = {""};
        final String[] DBLocationStr = {""};
        final String[] DBLocation = new String[1];
        timeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        DBLocation[0] = dsp.getKey();
                        for(int i=0;i<DBLocation[0].length();i++){
                            if(DBLocation[0].charAt(i)==':'){
                                DBLocationStr[0] +='.';
                            }
                            else{
                                DBLocationStr[0] += DBLocation[0].charAt(i);
                            }
                        }
                        System.out.println(DBLocationStr[0]);
                        int indexOfComma = DBLocationStr[0].indexOf(',');
                        for(int i=0;i<indexOfComma-1;i++){
                            DBLatitude[0] +=DBLocationStr[0].charAt(i);
                        }
                        for(int i=indexOfComma+1;i<DBLocationStr[0].length();i++){
                            DBLongitude[0] += DBLocationStr[0].charAt(i);
                        }
                        System.out.println("DB Latitude "+ DBLatitude[0]);
                        System.out.println("DB Longitude "+ DBLongitude[0]);


                        distance[0] = calculateDistance(Double.parseDouble(DBLatitude[0]),Double.parseDouble(DBLongitude[0]),latitude,longitude);
                        System.out.println("Latitude" + latitude);
                        System.out.println("Longitude" + longitude);
                        System.out.println("Distance btw "+distance[0]);
                        ////////////////////////////////////// continue from here
                        if(distance[0]<=range){                          /////////////////////////////////////////////////////////    range updates
                            DatabaseReference locationReference = timeReference.child(DBLocation[0]);
                            Log.d("test","12300");
                            globalLocation = DBLocation[0];
                            String deviceID = Settings.Secure.getString(markPresent.getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
                            Log.d("Device ID",deviceID);
                            checkDeviceIDMatch(deviceID, locationReference);



                        }
                        else{
                            Toast.makeText(MarkPresent.this, "Out of Range", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else{
                    Toast.makeText(MarkPresent.this, "No Locations in DB", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return pushed[0];
    }

    private void checkDeviceIDMatch(String deviceID,DatabaseReference locationReference) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Student");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        Student student = dsp.getValue(Student.class);
                        if(student.getId().equals(enroll)){
                            if(student.getAndroidId().equals(deviceID)){
                                globalDeviceID = deviceID;
                                globalLocationReference = locationReference;
                                captureUploadImage();

                            }
                            else{
                                Toast.makeText(MarkPresent.this, "Android id different contact admin to update", Toast.LENGTH_SHORT).show();
                            }

                            break;
                        }
                    }
                }
                else{
                    Toast.makeText(MarkPresent.this, "No Student data in DB", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private boolean checkDeviceIDPresent(String id, DatabaseReference locationReference, String encodedImg) {
        final boolean[] present = {false};
        locationReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        if(dsp.getKey().equals(id)){
                            present[0] = true;
                            Toast.makeText(MarkPresent.this, "Already marked using this device", Toast.LENGTH_SHORT).show();
                            Log.d("Device id","Present "+id);
                            break;
                        }
                    }
                    if(!present[0]){
                        Log.d("Device id","Absent "+id);
                        uploadImage(encodedImg);
                        locationReference.child(id).setValue(enroll);
                        pushed[0] = true;
                        Toast.makeText(MarkPresent.this, "You are Present", Toast.LENGTH_SHORT).show();
                    }


                }
                else{
                    Log.d("Device id","Absent "+id);
                    uploadImage(encodedImg);
                    locationReference.child(id).setValue(enroll);
                    pushed[0] = true;
                    Toast.makeText(MarkPresent.this, "You are Present", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return present[0];
    }

    private void uploadImage(String encodedImg) {
        Log.d("img to string",""+encodedImg);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Images").child(sem).child(className).child(subjectId).child(globalDate).child(globalTime).child(globalLocation);
        reference.child(enroll).setValue(encodedImg);
        presentDone.setImageResource(R.drawable.ic_baseline_check_24);
        markPresent.setEnabled(false);
    }

    private void captureUploadImage() {
        askCameraPermission();
    }

    private void askCameraPermission() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.CAMERA)!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},101);
        }
        else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent,CAMERA_REQ_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CAMERA_REQ_CODE){
            Bitmap image = null;
            Log.d("OpenCamera","Camera data is here");
            if(resultCode == RESULT_OK) {
                image = (Bitmap) data.getExtras().get("data");
                if (image != null) {
                    // encode image to base64 string
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
                    byte[] byteArray = byteArrayOutputStream.toByteArray();
                    String encodedImage = Base64.encodeToString(byteArray, Base64.DEFAULT);
                    //
                    checkDeviceIDPresent(globalDeviceID, globalLocationReference, encodedImage);
                } else {
                    Toast.makeText(this, "No image captured", Toast.LENGTH_SHORT).show();
                }
            }
            else{
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            }


        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (101 == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private double calculateDistance(double lat1, double long1, double lat2, double long2) {
        int radiusEarth = 6371000;
        double dLat = deg2rad(lat2-lat1);
        double dLon = deg2rad(long2-long1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = radiusEarth * c; // Distance in km
        return d;
    }

    private double deg2rad(double v) {
        return v *(Math.PI/180);
    }

    private void getLocation() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ActivityCompat.checkSelfPermission(MarkPresent.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                if(isGPSEnabled()){
                    LocationServices.getFusedLocationProviderClient(MarkPresent.this).requestLocationUpdates(locationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(@NonNull LocationResult locationResult) {
                            super.onLocationResult(locationResult);
                            LocationServices.getFusedLocationProviderClient(MarkPresent.this).removeLocationUpdates(this);
                            if(locationResult != null && locationResult.getLocations().size()>0){
                                int index = locationResult.getLocations().size()-1;
                                latitude = locationResult.getLocations().get(index).getLatitude();
                                longitude = locationResult.getLocations().get(index).getLongitude();
                                textViewCoordinates.setText(""+latitude+" , "+longitude);

                            }

                        }
                    }, Looper.getMainLooper());
                }
                else{
                    turnOnGPS();
                }
            }
            else{
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }
        }
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
                    Toast.makeText(MarkPresent.this, "GPS is already turned on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) e;
                                resolvableApiException.startResolutionForResult(MarkPresent.this, 2);
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

    private void checkBiometric() {
        androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(MarkPresent.this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Toast.makeText(MarkPresent.this, "You can use Biometric", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(MarkPresent.this, "No Biometric", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(MarkPresent.this, "Biometric not currently available", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(MarkPresent.this, "Biometric didn't matched", Toast.LENGTH_SHORT).show();
                break;
        }
        Executor executor = ContextCompat.getMainExecutor(MarkPresent.this);
        // this will give us result of AUTHENTICATION
        final BiometricPrompt biometricPrompt = new BiometricPrompt(MarkPresent.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            // THIS METHOD IS CALLED WHEN AUTHENTICATION IS SUCCESS
            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                // take print and change image to "Tick"
                biometricStatus.setImageResource(R.drawable.ic_baseline_check_24);
                verifyBiometric.setEnabled(false);
                markPresent.setEnabled(true);
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });
        // creating a variable for our promptInfo
        // BIOMETRIC DIALOG
        final androidx.biometric.BiometricPrompt.PromptInfo promptInfo = new androidx.biometric.BiometricPrompt.PromptInfo.Builder().setTitle("Biometric")
                .setDescription("Use Biometric to Authenticate").setNegativeButtonText("Cancel").build();


        // trigger
        biometricPrompt.authenticate(promptInfo);
    }

    private void setSubjects(String sem, String className) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Semester").child(sem).child(className);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    subjects.clear();
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        subjects.add(dsp.getKey()+" : "+dsp.getValue());
                    }
                    ArrayAdapter<String> adapterSubjects = new ArrayAdapter<String>(MarkPresent.this, android.R.layout.simple_spinner_dropdown_item, subjects);
                    spinnerSubject.setAdapter(adapterSubjects);
                    spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            subjectName = subjects.get(i);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }
                else{
                    Toast.makeText(MarkPresent.this, "No Subjects in DB/ ERROR", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setClassesSubjectsSpinners(String sem) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Semester").child(sem);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        classes.add(dsp.getKey());
                    }
                    // set classes spinner
                    ArrayAdapter<String> adapterClasses = new ArrayAdapter<String>(MarkPresent.this, android.R.layout.simple_spinner_dropdown_item, classes);
                    spinnerClass.setAdapter(adapterClasses);
                    spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            className = classes.get(i);
                            setSubjects(sem,className);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });

                }
                else{
                    Toast.makeText(MarkPresent.this, "No Class in DB/ Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}