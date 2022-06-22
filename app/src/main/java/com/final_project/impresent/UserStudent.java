package com.final_project.impresent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class UserStudent extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_student);

        ImageView markPresent = findViewById(R.id.imageView_user_student_markPresent);
        ImageView checkAttendance = findViewById(R.id.imageView_user_student_checkAttendance);
        Button logout = findViewById(R.id.button_user_student_logout);

        String name,email,pass,id;
        int sem;
        final Student[] student = new Student[1];

        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Student");
        //Query query = reference.orderByChild("email").equalTo(email);
        reference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Log.d("userStudent Snapshot",""+snapshot.getValue());
                    //Iterable<DataSnapshot> studentList = snapshot.getChildren();
                    for(DataSnapshot child: snapshot.getChildren()){
                        Log.d("snapshot",child.getValue().toString());
                        student[0] = child.getValue(Student.class);
                        TextView tv = findViewById(R.id.textView_userStudent_name);
                        tv.setText(student[0].getName());

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(UserStudent.this,MainActivity.class));
                    finish();
                }
                catch (Exception e){
                    Log.e("user student logout", "onClick: "+e.toString());
                }
            }
        });
        markPresent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent markPresentIntent = new Intent(UserStudent.this, MarkPresent.class);
                markPresentIntent.putExtra("name", student[0].getName());
                markPresentIntent.putExtra("enroll",student[0].getId());
                markPresentIntent.putExtra("email",student[0].getEmail());
                markPresentIntent.putExtra("sem",""+student[0].getSem());
                System.out.println(student[0].getSem());
                startActivity(markPresentIntent);
            }
        });
        checkAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentGetAttendance = new Intent(UserStudent.this,StudentGetAttendance.class );
                intentGetAttendance.putExtra("student",student[0]);
                startActivity(intentGetAttendance);
            }
        });
    }
}