package com.final_project.impresent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    Button login_button;
    Button register_button;
    EditText id_edittext;
    EditText password_edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_button=findViewById(R.id.button_login);
        register_button=findViewById(R.id.button_register);
        id_edittext=findViewById(R.id.editText_enter_id);
        password_edittext=findViewById(R.id.editText_enter_password);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Under Development", Toast.LENGTH_SHORT).show();
                Log.d("login_button_onclick", "clicked_login_button");
                String id = id_edittext.getText().toString();
                String password = password_edittext.getText().toString();
                Log.d("id",id);
                Log.d("password",password);
            }
        });

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "register button pressed", Toast.LENGTH_SHORT).show();
                Log.d("register_button_onclick","clicked");
                Intent register_intent = new Intent(MainActivity.this,RegisterType.class);
                startActivity(register_intent);
            }
        });
    }
}