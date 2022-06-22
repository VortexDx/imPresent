package com.final_project.impresent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class StudentGetAttendance extends AppCompatActivity {

    private static final String TAG = "StudentGetAttendance";
    ArrayList<String> subjects = new ArrayList<>();
    ArrayList<String> dates = new ArrayList<>();
    ArrayList<String> times = new ArrayList<>();

    String globalSub,globalDate,globalTime, globalClass, globalSem, globalBranch;

    Spinner subSpinner, dateSpinner, timeSpinner;
    ImageButton getAttendance, getAllData;
    Student student;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_get_attendance);

        Intent incomingIntent = getIntent();
        student = (Student) incomingIntent.getSerializableExtra("student");
        globalSem = String.valueOf(student.getSem());
        globalBranch = student.getBranch();
        Log.d("student",""+student.getName());

        subSpinner = findViewById(R.id.spinner_StudentGetAttendance_sub);
        dateSpinner = findViewById(R.id.spinner_StudentGetAttendance_date);
        timeSpinner = findViewById(R.id.spinner_StudentGetAttendance_Time);
        getAttendance = findViewById(R.id.imageButton_StudentGetAttendance_getAttendance);
        getAllData = findViewById(R.id.imageButton_StudentGetAttendance_getFullData);

        getAttendance.setEnabled(false);
        //getAllData.setEnabled(false);

        setSpinners();

        getAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean[] present = {false};
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance").child(String.valueOf(student.getSem())).child(globalClass).child(globalSub).child(globalDate).child(globalTime);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot dsp : snapshot.getChildren()){
                                for(DataSnapshot entries : dsp.getChildren()){
                                    if(entries.getValue().equals(student.getId())){
                                        present[0] =true;
                                        break;
                                    }
                                }
                                if(present[0]){
                                    break;
                                }
                            }
                            if(present[0]){
                                Toast.makeText(StudentGetAttendance.this, "You were Present", Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(StudentGetAttendance.this, "You were Absent", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(StudentGetAttendance.this, "No Attendance Data", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        getAllData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Integer> attendanceFreq = new HashMap<>();
                HashMap<String,Integer> totalSubClasses = new HashMap<>();
                ArrayList<String> totalSubjects = new ArrayList<>();
                DatabaseReference subReference = FirebaseDatabase.getInstance().getReference("Semester").child(globalSem).child(globalBranch);
                subReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot dsp : snapshot.getChildren()){
                                totalSubjects.add(dsp.getKey());
                            }
                        }
                        else{
                            Toast.makeText(StudentGetAttendance.this, "No Data in DB Semester", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance").child(globalSem).child(globalBranch);
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot dsp : snapshot.getChildren()){
                                String localSubject = dsp.getKey();
                                for(DataSnapshot dsp2 : dsp.getChildren()){
                                    String localDate = dsp2.getKey();
                                    for(DataSnapshot dsp3 : dsp2.getChildren()){
                                        String localTime = dsp3.getKey();
                                        if(totalSubClasses.containsKey(localSubject)){
                                            totalSubClasses.put(localSubject, totalSubClasses.get(localSubject)+1);
                                        }
                                        else{
                                            totalSubClasses.put(localSubject,1);
                                        }
                                        for(DataSnapshot dsp4 : dsp3.getChildren()){
                                            String localLocation = dsp4.getKey();
                                            for(DataSnapshot dsp5 : dsp4.getChildren()){
                                                String localEnroll = Objects.requireNonNull(dsp5.getValue()).toString();            ////////// take care of exceptions
                                                if(localEnroll.equals(student.getId())){
                                                    if(attendanceFreq.containsKey(localSubject))
                                                        attendanceFreq.put(localSubject,attendanceFreq.get(localSubject)+1);
                                                    else
                                                        attendanceFreq.put(localSubject,1);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            Log.d(TAG, "onDataChange: "+attendanceFreq);
                            Log.d(TAG,""+totalSubClasses);
                            if(!totalSubClasses.isEmpty() && !attendanceFreq.isEmpty()){
                                Intent intent = new Intent(StudentGetAttendance.this,ShowAttendanceFull.class);
                                intent.putExtra("attendanceFreq",attendanceFreq);
                                intent.putExtra("totalSubClasses",totalSubClasses);
                                intent.putExtra("allSubs",totalSubjects);
                                intent.putExtra("student",true);
                                startActivity(intent);
                            }
                            else{
                                Toast.makeText(StudentGetAttendance.this, "No attendance data", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else{
                            Toast.makeText(StudentGetAttendance.this, "No attendance of subjects in DB", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        });



    }

    private void setSpinners() {
        setSubSpinner();
    }

    private void setSubSpinner() {
        globalClass = student.getId().substring(5,8);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Attendance").child(String.valueOf(student.getSem())).child(globalClass);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        subjects.add(dsp.getKey());
                    }
                    ArrayAdapter<String> adapterSubjects = new ArrayAdapter<>(StudentGetAttendance.this, android.R.layout.simple_spinner_dropdown_item,subjects);
                    subSpinner.setAdapter(adapterSubjects);
                    subSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            dates.clear();
                            times.clear();
                            globalSub = subjects.get(i);
                            setDate(reference);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                else{
                    Toast.makeText(StudentGetAttendance.this, "No Attendance of class", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setDate(DatabaseReference reference) {
        DatabaseReference dateReference = reference.child(globalSub);
        dateReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        dates.add(dsp.getKey());
                    }
                    ArrayAdapter<String> adapterDates = new ArrayAdapter<>(StudentGetAttendance.this, android.R.layout.simple_spinner_dropdown_item,dates);
                    dateSpinner.setAdapter(adapterDates);
                    dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            times.clear();
                            globalDate = dates.get(i);
                            setTimes(dateReference);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }
                else{
                    Toast.makeText(StudentGetAttendance.this, "No Attendance on that date", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setTimes(DatabaseReference dateReference) {
        DatabaseReference timeReference = dateReference.child(globalDate);
        timeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        times.add(dsp.getKey());
                    }
                    ArrayAdapter<String> adapterTimes =  new ArrayAdapter<>(StudentGetAttendance.this, android.R.layout.simple_spinner_dropdown_item,times);
                    timeSpinner.setAdapter(adapterTimes);
                    timeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            globalTime = times.get(i);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                    getAttendance.setEnabled(true);
                }
                else{
                    Toast.makeText(StudentGetAttendance.this, "No attendance at that time", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}