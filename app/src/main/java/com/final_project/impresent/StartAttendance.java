package com.final_project.impresent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.api.SystemParameterOrBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class StartAttendance extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_attendance);

        Spinner semester = findViewById(R.id.spinner_startAttendance_sem);
        Spinner classname = findViewById(R.id.spinner_startAttendance_class);
        Spinner subject = findViewById(R.id.spinner_startAttendance_subject);
        EditText timerEditText = findViewById(R.id.editTextTime_startAttendance_timer);
        Button startAttendance = findViewById(R.id.button_startAttendance_startAttendance);

        classname.setEnabled(false);
        subject.setEnabled(false);

        Integer[] semesters = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8};

        ArrayList<String> classes = new ArrayList<>(), subjects = new ArrayList<>();
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item, semesters);
        semester.setAdapter(adapter);

        final Integer[] sem = {1};
        final String[] localClass = {""};
        final String[] localSubject = {""};

        semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sem[0] = semesters[i];
                classes.clear();
                subjects.clear();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Semester").child(""+sem[0]);
                Log.d("TAG1",reference.getKey());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            System.out.println("exists");
                            for(DataSnapshot dsp : snapshot.getChildren()){
                                System.out.println(dsp.getKey());
                                classes.add(dsp.getKey());
                            }
                            ArrayAdapter<String> adapterClasses = new ArrayAdapter<String>(StartAttendance.this, android.R.layout.simple_spinner_dropdown_item, classes);
                            classname.setAdapter(adapterClasses);
                            classname.setEnabled(true);
                        }
                        else{
                            Log.d("Semester Spinner","No classes in database");
                            Toast.makeText(StartAttendance.this, "No classes in database", Toast.LENGTH_SHORT).show();
                        }


                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });



            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        classname.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                localClass[0] = classes.get(i);
                subjects.clear();
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Semester").child(""+sem[0]).child(localClass[0]);
                Log.d("TAG2",reference.getKey());
                reference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()){
                            for(DataSnapshot dsp : snapshot.getChildren()){
                                System.out.println(dsp.getKey());
                                System.out.println(dsp.getValue().toString());
                                subjects.add(dsp.getKey()+" : "+dsp.getValue().toString());
                            }
                            ArrayAdapter<String> adapterSubjects = new ArrayAdapter<String>(StartAttendance.this, android.R.layout.simple_spinner_dropdown_item, subjects);
                            subject.setAdapter(adapterSubjects);
                            subject.setEnabled(true);
                        }
                        else{
                            Log.d("Subject spinner","no subjects in db");
                            Toast.makeText(StartAttendance.this, "No Subjects in database", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                localSubject[0]=subjects.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        startAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get timer
                String time = timerEditText.getText().toString();
                if(time.contains(":") || time.contains(" ")){
                    Toast.makeText(StartAttendance.this, "Invalid time, Enter digits only", Toast.LENGTH_SHORT).show();
                }
                else{
                    Intent intent = new Intent(StartAttendance.this,LiveAttendance.class);
                    intent.putExtra("sem",""+sem[0]);
                    intent.putExtra("class",localClass[0]);
                    intent.putExtra("subject",localSubject[0]);
                    intent.putExtra("timer",time);
                    startActivity(intent);
                }
            }
        });


    }
}