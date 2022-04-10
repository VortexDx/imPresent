package com.final_project.impresent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class RegisterTeacher extends AppCompatActivity {

    EditText teacher_name;
    EditText teacher_id;
    EditText teacher_subjectId;
    EditText enter_password;
    EditText confirmPassword;
    Spinner select_semester;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_teacher);

        teacher_name = findViewById(R.id.editText_register_teacher_name);
        teacher_id = findViewById(R.id.editText_register_teacher_teacherId);
        teacher_subjectId = findViewById(R.id.editText_register_teacher_subject_id);
        enter_password = findViewById(R.id.editText_register_teacher_enterPassword);
        confirmPassword = findViewById(R.id.editText_teacher_register_confirm_password);
        select_semester = findViewById(R.id.spinner_semester_dropdown);
        register = findViewById(R.id.button_register_teacher_register);

        String[] semesters = new String[]{"I","II","III","IV","V","VI","VII","VIII"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,semesters);
        select_semester.setAdapter(adapter);

        final String[] sem = {"I"};

        select_semester.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sem[0] =semesters[i];
                Log.d("spinner_registerTeacher",sem[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("RegisterTeacherActivity","Register Clicked");
                String name,id,subId,pass,confirmPass;
                name=teacher_name.getText().toString();
                id=teacher_id.getText().toString();
                subId=teacher_subjectId.getText().toString();
                pass=enter_password.getText().toString();
                confirmPass=confirmPassword.getText().toString();

                if(name.equals("") || id.equals("") || subId.equals("") || pass.equals("") || confirmPass.equals("") || subId.contains(" ") || !pass.equals(confirmPass)){
                    Toast.makeText(RegisterTeacher.this, "Invalid Entries", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d("RegisterTeacher_Values","Name= "+name+" id= "+id+" sem= "+sem[0]+" subId= "+subId+" pass= "+pass+" confPass= "+confirmPass);
                    // store in DB or Cloud
                }

            }
        });
    }
}