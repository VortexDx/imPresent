package com.final_project.impresent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LiveAttendance extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_attendance);

        String sem,className,subject,timer,subid="";
        Intent intent = getIntent();
        sem = intent.getStringExtra("sem");
        className = intent.getStringExtra("class");
        subject = intent.getStringExtra("subject");
        timer = intent.getStringExtra("timer");
        for(int i=0;subject.charAt(i+1) !=':'; i++){
            subid += subject.charAt(i);
        }
        //DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Attendance").child(sem).child(className).child(subid);
        // get current date
        //reference.child()
        Date today = new Date();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
        String dateToStr = format.format(today);
        String timeToStr = timeFormat.format(today);
        System.out.println(sem+className+subject+timer+subid);
        System.out.println(dateToStr+" "+timeToStr);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Attendance").child(sem).child(className).child(subid).child(dateToStr).child(timeToStr);
        reference.setValue(true);

    }
}