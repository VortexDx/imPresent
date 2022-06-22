package com.final_project.impresent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ShowAttendanceFull extends AppCompatActivity {
    ArrayList<Student> allStudents = new ArrayList<>();
    HashMap<String,Integer> attendanceFreq = new HashMap<>();
    TreeMap<String,Integer> sortedAttendance = new TreeMap<>();
    HashMap<String,Integer> totalSubClasses = new HashMap<>();
    ArrayList<String> allSubjects = new ArrayList<>();

    int totalClasses=0;

    TextView tv, totalClassesTV;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_attendance_full);

        allStudents.clear();attendanceFreq.clear();sortedAttendance.clear();totalSubClasses.clear();

        lv = findViewById(R.id.listview_showAttendanceFull);
        tv = findViewById(R.id.textView_showAttendanceFull_StudentData);
        totalClassesTV = findViewById(R.id.textView_showAttendanceFull_totalClasses);

        Intent incomingIntent = getIntent();
        if(!incomingIntent.getBooleanExtra("student",false)){
            String className = incomingIntent.getStringExtra("class");
            String subject = incomingIntent.getStringExtra("sub");
            attendanceFreq = (HashMap<String, Integer>) incomingIntent.getSerializableExtra("attendance");
            totalClasses = incomingIntent.getIntExtra("totalClasses",-1);
            Log.d("Values",""+className+" "+subject+" "+totalClasses+" "+attendanceFreq);

            tv.setText("Student Attendance for "+className+" & subject "+subject);
            totalClassesTV.setText("Total Classes = "+totalClasses);

            if(totalClasses>0)
                getAllStudents(className);
            else{
                Toast.makeText(this, "Total Classes <=0", Toast.LENGTH_SHORT).show();
            }
        }
        else{
            attendanceFreq = (HashMap<String, Integer>) incomingIntent.getSerializableExtra("attendanceFreq");
            totalSubClasses = (HashMap<String, Integer>) incomingIntent.getSerializableExtra("totalSubClasses");
            allSubjects = incomingIntent.getStringArrayListExtra("allSubs");
            Integer localTotalClasses = 0;
            ArrayList<String> finalList = new ArrayList<>();
            for(int i=0;i<allSubjects.size();i++){
                String temp;
                if(totalSubClasses.containsKey(allSubjects.get(i))){
                    temp = "Subject: " + allSubjects.get(i) + ", Classes done: " + attendanceFreq.get(allSubjects.get(i)) + "/" + totalSubClasses.get(allSubjects.get(i)) + ", Percentage: " + (attendanceFreq.get(allSubjects.get(i)) / (double) totalSubClasses.get(allSubjects.get(i)))*100 + "%";
                    localTotalClasses+=totalSubClasses.get(allSubjects.get(i));
                }
                else{
                    temp = "Subjects: " + allSubjects.get(i) + ", Classes done: 0/0, Percentage: 100%";
                }
                finalList.add(temp);
            }
            totalClassesTV.setText("Total Classes: "+localTotalClasses);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(ShowAttendanceFull.this, android.R.layout.simple_list_item_1,finalList);
            lv.setAdapter(adapter);

        }



    }

    private void getAllStudents(String className) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Student");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        Student stud = dsp.getValue(Student.class);
                        if(stud.getId().matches(".*B"+className+".*")){
                            allStudents.add(stud);
                            if(!attendanceFreq.containsKey(stud.getId())){
                                attendanceFreq.put(stud.getId(), 0);
                            }
                        }
                    }
                    sortedAttendance.putAll(attendanceFreq);
                    showAttendance();
                }
                else{
                    Toast.makeText(ShowAttendanceFull.this, "No Registered Students", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ShowAttendanceFull.this, "Error", Toast.LENGTH_SHORT).show();
                Log.e("getAllStudents","Error "+error);
            }
        });
    }

    private void showAttendance() {
        ArrayList<String> finalList = new ArrayList<>();
        for(Map.Entry<String,Integer> entry : sortedAttendance.entrySet()){
            String temp = entry.getKey()+" "+entry.getValue()+"/"+totalClasses+" = "+(entry.getValue()/(double)totalClasses)*100 +"%";
            finalList.add(temp);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShowAttendanceFull.this, android.R.layout.simple_list_item_1,finalList);
        lv.setAdapter(adapter);


    }
}