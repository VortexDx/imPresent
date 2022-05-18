package com.final_project.impresent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button login_button;
    Button register_button;
    EditText email_edittext;
    EditText password_edittext;
    private FirebaseAuth auth;
    boolean onStartFlag = false;

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if(!onStartFlag) {
            onStartFlag = true;
            FirebaseUser currentUser = auth.getCurrentUser();
            if (currentUser != null)
                updateUI(currentUser);
        }
    }

    private void updateUI(FirebaseUser currentUser) {
        String email = currentUser.getEmail();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query1 = reference.child("Student").orderByChild("email").equalTo(email);
        //Query query2 = reference.child("Teacher").orderByChild("email").equalTo(email);
        query1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    // is student
                    Intent intent = new Intent(MainActivity.this,UserStudent.class);
                    intent.putExtra("email",email);
                    startActivity(intent);
                }
                else{
                    // is teacher
                    Intent intent = new Intent(MainActivity.this,UserTeacher.class);
                    intent.putExtra("email",email);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_button=findViewById(R.id.button_login);
        register_button=findViewById(R.id.button_register);
        email_edittext=findViewById(R.id.editTextTextEmailAddress);
        password_edittext=findViewById(R.id.editText_enter_password);

        auth = FirebaseAuth.getInstance();

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("login_button_onclick", "clicked_login_button");
                String email = email_edittext.getText().toString().toLowerCase();
                String password = password_edittext.getText().toString();
                Log.d("id",email);
                Log.d("password",password);
                if(email.equals("") || password.length()<6)
                    Toast.makeText(MainActivity.this, "Invalid Data", Toast.LENGTH_SHORT).show();
                else
                    loginUser(email,password);

            }

            private void loginUser(String email, String password) {
                auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Log.d("Login User","Success");
                            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT).show();
                            FirebaseUser currentUser = auth.getCurrentUser();
                            updateUI(currentUser);
                        }
                        else{
                            Log.d("Login User","Failed "+task.getException());
                            Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("register_button_onclick","clicked");
                Intent register_intent = new Intent(MainActivity.this,RegisterType.class);
                startActivity(register_intent);
            }
        });
    }


}