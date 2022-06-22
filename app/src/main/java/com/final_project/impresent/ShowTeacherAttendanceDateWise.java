package com.final_project.impresent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShowTeacherAttendanceDateWise extends AppCompatActivity {
    ArrayList<String> presentList = new ArrayList<>();
    ArrayList<Student> studentList = new ArrayList<>();
    ArrayList<String> finalList = new ArrayList<>();
    String className;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_teacher_attendance_date_wise);
        Intent incomingIntent = getIntent();
        presentList = incomingIntent.getStringArrayListExtra("attendance");
        className = incomingIntent.getStringExtra("class");

        listView = findViewById(R.id.listview_showTeacherAttendanceDateWise);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Student");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dsp : snapshot.getChildren()){
                        Student student = dsp.getValue(Student.class);
                        String enroll = student.getId();
                        if(enroll.matches(".*B"+className+".*")){
                            studentList.add(student);
                        }
                    }
                    for(int i=0;i< studentList.size();i++){
                        if(presentList.contains(studentList.get(i).getId())){
                            finalList.add(studentList.get(i).getName()+" ("+studentList.get(i).getId()+") "+"Present");
                        }
                        else{
                            finalList.add(studentList.get(i).getName()+" ("+studentList.get(i).getId()+") "+"Absent");
                        }
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(ShowTeacherAttendanceDateWise.this, android.R.layout.simple_list_item_1,finalList);
                    listView.setAdapter(adapter);
                }
                else{
                    Toast.makeText(ShowTeacherAttendanceDateWise.this, "No student in DB", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
}