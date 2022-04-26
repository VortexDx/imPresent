package com.final_project.impresent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;



public class RegisterStudent extends AppCompatActivity {

    public static Activity activity = null;

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
        activity=this;

        editText_name = findViewById(R.id.editText_register_student_name);
        editText_enroll = findViewById(R.id.editText_register_student_enroll);
        editText_pass = findViewById(R.id.editText_register_student_enter_password);
        editText_confirm_pass = findViewById(R.id.editText_register_student_confirm_password);
        semester_dropdown = findViewById(R.id.spinner_register_student_semester);
        finprint_enter = findViewById(R.id.imageButton_register_student_finprint);
        finprint_status = findViewById(R.id.imageView_register_student_finprint_status);
        register = findViewById(R.id.button_register_student_register);

        register.setEnabled(false);

        Integer[] semesters = new Integer[]{1, 2, 3, 4, 5, 6, 7, 8};
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item, semesters);
        semester_dropdown.setAdapter(adapter);

        final Integer[] sem = {1};

        semester_dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                sem[0] = semesters[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        finprint_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("FinPrint", "enter finprint clicked");
                androidx.biometric.BiometricManager biometricManager = androidx.biometric.BiometricManager.from(RegisterStudent.this);
                switch (biometricManager.canAuthenticate()) {
                    case BiometricManager.BIOMETRIC_SUCCESS:
                        Toast.makeText(RegisterStudent.this, "You can use Biometric", Toast.LENGTH_SHORT).show();
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                        Toast.makeText(RegisterStudent.this, "No Biometric", Toast.LENGTH_SHORT).show();
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                        Toast.makeText(RegisterStudent.this, "Biometric not currently available", Toast.LENGTH_SHORT).show();
                        break;
                    case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                        Toast.makeText(RegisterStudent.this, "Biometric didnt matched", Toast.LENGTH_SHORT).show();
                        break;
                }
                Executor executor = ContextCompat.getMainExecutor(RegisterStudent.this);
                // this will give us result of AUTHENTICATION
                final BiometricPrompt biometricPrompt = new BiometricPrompt(RegisterStudent.this, executor, new BiometricPrompt.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                        super.onAuthenticationError(errorCode, errString);
                    }

                    // THIS METHOD IS CALLED WHEN AUTHENTICATION IS SUCCESS
                    @Override
                    public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        Toast.makeText(getApplicationContext(), "Login Success", Toast.LENGTH_SHORT).show();
                        // take print and change image to "Tick"
                        finprint_status.setImageResource(R.drawable.ic_baseline_check_24);
                        finprint_enter.setEnabled(false);
                        register.setEnabled(true);
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                    }
                });
                // creating a variable for our promptInfo
                // BIOMETRIC DIALOG
                final androidx.biometric.BiometricPrompt.PromptInfo promptInfo = new androidx.biometric.BiometricPrompt.PromptInfo.Builder().setTitle("Biometric")
                        .setDescription("Use Biometric to Authenticate").setNegativeButtonText("Cancel").build();


                // trigger
                biometricPrompt.authenticate(promptInfo);

            }
        });




        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name, enroll, pass, confpass;
                name = editText_name.getText().toString();
                enroll = editText_enroll.getText().toString();
                pass = editText_pass.getText().toString();
                confpass = editText_confirm_pass.getText().toString();

                if (name.equals("") || enroll.equals("") || pass.equals("") || confpass.equals("") || !pass.equals(confpass) || enroll.contains(" ")) {
                    Toast.makeText(RegisterStudent.this, "Invalid Entries", Toast.LENGTH_SHORT).show();
                    Log.d("Student_Register", "Name= " + name + " Enroll= " + enroll + " Sem= " + sem[0] + " Pass= " + pass + " ConfPass= " + confpass);
                } else {
                    Log.d("Student_Register", "Name= " + name + " Enroll= " + enroll + " Sem= " + sem[0] + " Pass= " + pass + " ConfPass= " + confpass);
                    Student student = new Student(name, enroll, pass, sem[0]);
                    // check id if already present and add data
                    RegisterStudent.checkEnroll(student,enroll,getApplicationContext());
                }
            }
        });
    }

    public static void checkEnroll(Student student, String enroll, Context context) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Student").orderByChild("id").equalTo(enroll);
        final boolean[] present = {false};
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(context, "Already registered Enroll", Toast.LENGTH_SHORT).show();
                    Log.d("checkEnroll","present already");
                    present[0] = true;
                    Log.d("enrollPresent2", "" + present[0]);
                }
                else{
                    Log.d("addStudent","inside");
                    StudentDAO dao = new StudentDAO();
                    dao.add(student).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                            context.startActivity(new Intent(context, MainActivity.class));
                            activity.finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, "Fail", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}