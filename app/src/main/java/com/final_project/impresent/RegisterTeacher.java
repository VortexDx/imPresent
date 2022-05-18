package com.final_project.impresent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Executor;

public class RegisterTeacher extends AppCompatActivity {

    private static Activity activity=null;
    EditText teacher_name;
    EditText teacher_id;
    //EditText teacher_subjectId;
    EditText enter_password;
    EditText confirmPassword;
    EditText emailid;
    Button register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_teacher);
        activity = this;

        teacher_name = findViewById(R.id.editText_register_teacher_name);
        teacher_id = findViewById(R.id.editText_register_teacher_teacherId);
        //teacher_subjectId = findViewById(R.id.editText_register_teacher_subject_id);
        emailid = findViewById(R.id.editTextTextEmailAddress_register_teacher);
        enter_password = findViewById(R.id.editText_register_teacher_enterPassword);
        confirmPassword = findViewById(R.id.editText_teacher_register_confirm_password);

        register = findViewById(R.id.button_register_teacher_register);






        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("RegisterTeacherActivity","Register Clicked");
                String name,id,email,pass,confirmPass;
                name=teacher_name.getText().toString();
                id=teacher_id.getText().toString();
                email = emailid.getText().toString();
                pass=enter_password.getText().toString();
                confirmPass=confirmPassword.getText().toString();

                if(name.equals("") || id.equals("")  || pass.equals("") || email.equals("") || confirmPass.equals("")  || !pass.equals(confirmPass)){
                    Toast.makeText(RegisterTeacher.this, "Invalid Entries", Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d("RegisterTeacher_Values","Name= "+name+" id= "+id+" email= "+email+" pass= "+pass+" confPass= "+confirmPass);
                    // store in DB or Cloud
                    Teacher teacher = new Teacher(name,id,email,pass);
                    // check Teacherid Already present?
                    RegisterTeacher.checkId(teacher,getApplicationContext());


                }

            }
        });
    }

    private static void checkId(Teacher teacher, Context applicationContext) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference.child("Teacher").orderByChild("id").equalTo(teacher.getId());
        final boolean[] present = {false};
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    Toast.makeText(applicationContext, "Already registered ID", Toast.LENGTH_SHORT).show();
                    Log.d("checkId","present already");
                    present[0]=true;
                }
                else{
                    FirebaseAuth auth;
                    auth = FirebaseAuth.getInstance();
                    auth.createUserWithEmailAndPassword(teacher.getEmail(), teacher.getPass()).addOnCompleteListener(RegisterTeacher.activity, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                Log.d("Create teacher","Success");
                                TeacherDAO dao = new TeacherDAO();
                                dao.add(teacher).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(applicationContext, "Success", Toast.LENGTH_SHORT).show();
                                        Log.d("Add Teacher","Success");
                                        RegisterTeacher.activity.startActivity(new Intent(applicationContext,MainActivity.class));
                                        activity.finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(applicationContext, "Fail", Toast.LENGTH_SHORT).show();
                                        Log.d("Add Teacher","Failed");
                                    }
                                });
                            }
                            else{
                                Log.d("Create Teacher User","Failed "+task.getException());
                            }
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