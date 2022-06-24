package com.final_project.impresent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class ShowTeacherAttendanceDateWise extends AppCompatActivity {
    ArrayList<String> presentList = new ArrayList<>();
    ArrayList<Student> studentList = new ArrayList<>();
    ArrayList<String> finalList = new ArrayList<>();
    HashMap<String,String> enrollToImg = new HashMap<>();
    String className;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_teacher_attendance_date_wise);
        Intent incomingIntent = getIntent();
        presentList = incomingIntent.getStringArrayListExtra("attendance");
        className = incomingIntent.getStringExtra("class");
        enrollToImg = (HashMap<String, String>) incomingIntent.getSerializableExtra("imgMap");

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
                            finalList.add(studentList.get(i).getName()+", ("+studentList.get(i).getId()+"), "+"Present");
                        }
                        else{
                            finalList.add(studentList.get(i).getName()+", ("+studentList.get(i).getId()+"), "+"Absent");
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

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("image list",""+enrollToImg.size());
                Log.d("Image List",""+enrollToImg);

                String item = finalList.get(i);
                int indexOfComma = item.indexOf(',');
                int indexOfComma2 = item.indexOf(',',indexOfComma+1);
                String enroll = item.substring(indexOfComma+3,indexOfComma2-1);

                Log.d("enrollll", enroll);

                if(enrollToImg.containsKey(enroll)){
                    Intent intent = new Intent(ShowTeacherAttendanceDateWise.this,ShowImage.class);
                    intent.putExtra("imageString", enrollToImg.get(enroll));
                    startActivity(intent);
                }
                else{
                    Toast.makeText(ShowTeacherAttendanceDateWise.this, "No image data", Toast.LENGTH_SHORT).show();
                }

            }
        });


    }
}