package com.final_project.impresent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserTeacher extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_teacher);

        ImageView startAttendance = findViewById(R.id.imageView_user_teacher_startAttendance);
        ImageView getAttendance = findViewById(R.id.imageView_user_teacher_getAttendance);
        Button logout = findViewById(R.id.button_user_teacher_logout);

        String email;
        Intent intent = getIntent();
        email = intent.getStringExtra("email");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Teacher");
        reference.orderByChild("email").equalTo(email).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Log.d("userTeacher snapshot",""+snapshot.getValue());
                    for(DataSnapshot child : snapshot.getChildren()){
                        Log.d("snapshot",child.getValue().toString());
                        Teacher teacher = child.getValue(Teacher.class);
                        TextView tv = findViewById(R.id.textView_user_teacher_name);
                        tv.setText(teacher.getName());
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
                    startActivity(new Intent(UserTeacher.this,MainActivity.class));
                    finish();
                }
                catch (Exception e){
                    Log.e("UserTeacher logout",e.toString());
                }

            }
        });
        startAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startAttendanceIntent = new Intent(UserTeacher.this,StartAttendance.class);
                startActivity(startAttendanceIntent);
            }
        });
        getAttendance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent getAttendanceIntent = new Intent(UserTeacher.this, TeacherGetAttendance.class);
                startActivity(getAttendanceIntent);
            }
        });
    }
}