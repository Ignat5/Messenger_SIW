package com.example.messenger_siw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    EditText ET_userEmail,ET_password;
    Button BTN_login;
    TextView TV_gotoRegistration;
    //Firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onStart() {
        super.onStart();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //Check if a user is signed in already
        if(firebaseUser!=null) {
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ET_userEmail = findViewById(R.id.ET_email_login);
        ET_password = findViewById(R.id.ET_password_login);
        BTN_login = findViewById(R.id.BTN_login);
        TV_gotoRegistration = findViewById(R.id.TV_gotoRegistration);

        firebaseAuth = FirebaseAuth.getInstance();

        BTN_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail = ET_userEmail.getText().toString();
                String password = ET_password.getText().toString();

                //Check is there a user:
                if(TextUtils.isEmpty(userEmail) || TextUtils.isEmpty(password)) {
                    Toast.makeText(LoginActivity.this,"Fill your fields please",Toast.LENGTH_LONG).show();
                }else {
                    firebaseAuth.signInWithEmailAndPassword(userEmail,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }else {
                                Toast.makeText(LoginActivity.this,"Login failed!",Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
        TV_gotoRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}