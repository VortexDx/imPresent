package com.final_project.impresent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class RegisterType extends AppCompatActivity {
    ImageButton teacher_IB;
    ImageButton student_IB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_type);

        teacher_IB = findViewById(R.id.teacher_image_button);
        student_IB = findViewById(R.id.student_image_button);

        teacher_IB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("teacher_ImageButton","Clicked");
                Intent register_teacher = new Intent(RegisterType.this,RegisterTeacher.class);
                startActivity(register_teacher);
            }
        });

        student_IB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("student_ImageButton","Clicked");
                Intent register_student = new Intent(RegisterType.this,RegisterStudent.class);
                startActivity(register_student);
            }
        });
    }
}