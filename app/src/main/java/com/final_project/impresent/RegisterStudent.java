package com.final_project.impresent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

public class RegisterStudent extends AppCompatActivity {
    EditText editText_name;
    EditText editText_enroll;
    EditText editText_pass;
    EditText editText_confirm_pass;
    Spinner semester_dropdown;
    ImageButton finprint_enter;
    ImageView finprint_status;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_student);

        editText_name = findViewById(R.id.editText_register_student_name);
        editText_enroll = findViewById(R.id.editText_register_student_enroll);
        editText_pass = findViewById(R.id.editText_register_student_enter_password);
        editText_confirm_pass = findViewById(R.id.editText_register_student_confirm_password);
        semester_dropdown = findViewById(R.id.spinner_register_student_semester);
        finprint_enter = findViewById(R.id.imageButton_register_student_finprint);
        finprint_status = findViewById(R.id.imageView_register_student_finprint_status);
        register = findViewById(R.id.button_register_student_register);

        String[] semesters = new String[]{"I","II","III","IV","V","VI","VII","VIII"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item,semesters);
        semester_dropdown.setAdapter(adapter);

        final String[] sem = {"I"};

        semester_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sem[0] =semesters[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        finprint_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FinPrint","enter finprint clicked");
                // take print and change image to "Tick"
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name, enroll, pass, confpass;
                name=editText_name.getText().toString();
                enroll=editText_enroll.getText().toString();
                // semester=sem[0]
                pass=editText_pass.getText().toString();
                confpass=editText_confirm_pass.getText().toString();
                // store finprint

                if(name.equals("") || enroll.equals("") || pass.equals("") || confpass.equals("") || !pass.equals(confpass) || enroll.contains(" ")){
                    Toast.makeText(RegisterStudent.this, "Invalid Entries", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d("Student_Register","Name= "+name+" Enroll= "+enroll+" Sem= "+sem[0]+" Pass= "+pass+" ConfPass= "+confpass);
                }
            }
        });
    }
}