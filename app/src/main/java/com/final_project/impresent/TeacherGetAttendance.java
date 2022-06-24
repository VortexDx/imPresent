package com.final_project.impresent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class TeacherGetAttendance extends AppCompatActivity {
    Spinner spinnerSem, spinnerClass, spinnerDate, spinnerTime, spinnerSubject;
    Button getButton,getFullButton;
    String globalSem, globalClass, globalDate, globalTime, globalSub;
    int totalClasses=0;

    ArrayList<String> attendanceList = new ArrayList<>();
    HashMap<String,Integer> attendanceFreq = new HashMap<>();
    ArrayList<String> sems = new ArrayList<>();
    ArrayList<String> classes = new ArrayList<>();
    ArrayList<String> subjects = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    ArrayList<String> times = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_get_attendance);
        spinnerSem = findViewById(R.id.spinner_TeacherGetAttendance_sem);
        spinnerClass = findViewById(R.id.spinner_TeacherGetAttendance_class);
        spinnerSubject = findViewById(R.id.spinner_TeacherGetAttendance_sub);
        spinnerDate = findViewById(R.id.spinner_TeacherGetAttendance_date);
        spinnerTime = findViewById(R.id.spinner_TeacherGetAttendance_time);
        getButton = findViewById(R.id.button_TeacherGetAttendance_getParticular);
        getFullButton = findViewById(R.id.button_TeacherGetAttendance_getFullData);
        getButton.setEnabled(false);
        getFullButton.setEnabled(false);

        setSpinners();

        getButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAttendanceByDate();
            }
        });

        getFullButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAttendanceFull();
            }
        });
    }

    private void getAttendanceFull() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance").child(globalSem).child(globalClass).child((globalSub));
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dspDates : snapshot.getChildren()){
                        for(DataSnapshot dspTimes : dspDates.getChildren()){
                            totalClasses++;
                            for(DataSnapshot dspLocations : dspTimes.getChildren()){
                                for(DataSnapshot dspEntries : dspLocations.getChildren()){
                                    if(attendanceFreq.containsKey(dspEntries.getValue().toString())) {
                                        attendanceFreq.put(dspEntries.getValue().toString(), attendanceFreq.get(dspEntries.getValue().toString()) + 1);
                                    }
                                    else{
                                        attendanceFreq.put(dspEntries.getValue().toString(),1);
                                    }
                                }
                            }
                        }
                    }
                    Intent intent = new Intent(TeacherGetAttendance.this,ShowAttendanceFull.class);
                    intent.putExtra("attendance",attendanceFreq);
                    intent.putExtra("class",globalClass);
                    intent.putExtra("totalClasses",totalClasses);
                    intent.putExtra("sub",globalSub);
                    intent.putExtra("student",false);
                    startActivity(intent);
                }
                else{
                    Toast.makeText(TeacherGetAttendance.this, "No Data in DB", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TeacherGetAttendance.this, "Error", Toast.LENGTH_SHORT).show();
                Log.e("getAttendanceFull","Error "+error);
            }
        });
    }

    private void getAttendanceByDate() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance").child(globalSem).child(globalClass).child(globalSub).child(globalDate).child(globalTime);
        DatabaseReference imgReference = FirebaseDatabase.getInstance().getReference("Images").child(globalSem).child(globalClass).child(globalSub).child(globalDate).child(globalTime);
        
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        for(DataSnapshot dsp2 : dsp.getChildren()){
                            attendanceList.add(dsp2.getValue().toString());
                        }
                    }
                    Log.d("List = ", ""+attendanceList);
                    imgReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.exists()){
                                HashMap<String,String> enrollImgMap = new HashMap<>();
                                //ArrayList<String> imgAttendanceList = new ArrayList<>();
                                for(DataSnapshot imgdsp : snapshot.getChildren()){
                                    for(DataSnapshot imgdsp2 : imgdsp.getChildren()){
                                        //imgAttendanceList.add(imgdsp2.getValue().toString());
                                        enrollImgMap.put(imgdsp2.getKey(), imgdsp2.getValue().toString());
                                    }
                                }
                                Intent intent = new Intent(TeacherGetAttendance.this,ShowTeacherAttendanceDateWise.class);
                                intent.putExtra("attendance",attendanceList);
                                intent.putExtra("imgMap",enrollImgMap);
                                intent.putExtra("class",globalClass);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(TeacherGetAttendance.this, "No Image data: ERROR", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    
                }
                else {
                    Toast.makeText(TeacherGetAttendance.this, "No Attendance in Db", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setSpinners() {
        setSem();
    }

    private void setSem() {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        sems.add(dsp.getKey());
                    }
                    ArrayAdapter<String> adapterSem = new ArrayAdapter<>(TeacherGetAttendance.this, android.R.layout.simple_spinner_dropdown_item,sems);
                    spinnerSem.setAdapter(adapterSem);
                    spinnerSem.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            classes.clear();
                            subjects.clear();
                            dates.clear();
                            times.clear();
                            globalSem = sems.get(i);
                            setClass(globalSem,reference);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                else{
                    Toast.makeText(TeacherGetAttendance.this, "No sem in DB", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setClass(String sem, DatabaseReference reference) {
        DatabaseReference semReference = reference.child(sem);
        semReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        classes.add(dsp.getKey());
                    }
                    ArrayAdapter<String> adapterClass = new ArrayAdapter<>(TeacherGetAttendance.this, android.R.layout.simple_spinner_dropdown_item,classes);
                    spinnerClass.setAdapter(adapterClass);
                    spinnerClass.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            subjects.clear();
                            dates.clear();
                            times.clear();
                            globalClass = classes.get(i);
                            setSubjects(globalClass,semReference);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                else{
                    Toast.makeText(TeacherGetAttendance.this, "No classes in DB", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setSubjects(String sub, DatabaseReference reference) {
        DatabaseReference subReference = reference.child(sub);
        subReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        subjects.add(dsp.getKey());
                    }
                    ArrayAdapter<String> adapterSubjects = new ArrayAdapter<>(TeacherGetAttendance.this, android.R.layout.simple_spinner_dropdown_item,subjects);
                    spinnerSubject.setAdapter(adapterSubjects);
                    spinnerSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            dates.clear();
                            times.clear();
                            globalSub = subjects.get(i);
                            setDate(globalSub,subReference);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    getFullButton.setEnabled(true);
                }
                else{
                    Toast.makeText(TeacherGetAttendance.this, "No Subjects in DB", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setDate(String date, DatabaseReference subReference) {
        DatabaseReference dateReference = subReference.child(date);
        dateReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        dates.add(dsp.getKey());
                    }
                    ArrayAdapter<String> adapterDates = new ArrayAdapter<>(TeacherGetAttendance.this, android.R.layout.simple_spinner_dropdown_item,dates);
                    spinnerDate.setAdapter(adapterDates);
                    spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            times.clear();
                            globalDate = dates.get(i);
                            setTime(globalDate,dateReference);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                else{
                    Toast.makeText(TeacherGetAttendance.this, "No Dates in DB", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setTime(String date, DatabaseReference dateReference) {
        DatabaseReference timeReference = dateReference.child(date);
        timeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        times.add(dsp.getKey());
                    }
                    ArrayAdapter<String> adapterTimes = new ArrayAdapter<>(TeacherGetAttendance.this, android.R.layout.simple_spinner_dropdown_item,times);
                    spinnerTime.setAdapter(adapterTimes);
                    spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            globalTime = times.get(i);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    getButton.setEnabled(true);
                }
                else{
                    Toast.makeText(TeacherGetAttendance.this, "No Times in DB", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}