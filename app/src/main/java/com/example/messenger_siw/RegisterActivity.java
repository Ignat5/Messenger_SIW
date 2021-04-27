package com.example.messenger_siw;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    //Widgets
    EditText ET_username,ET_password,ET_email;
    Button BTN_register;

    //Firebase
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initializing widgets
        ET_username = findViewById(R.id.ET_username);
        ET_password = findViewById(R.id.ET_password);
        ET_email = findViewById(R.id.ET_email);
        BTN_register = findViewById(R.id.BTN_register);

        //Firebase auth
        firebaseAuth = FirebaseAuth.getInstance();

        //Adding event listener to BTN_register
        BTN_register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String username = ET_username.getText().toString();
                String password = ET_password.getText().toString();
                String email = ET_email.getText().toString();
                //check input data
                if(TextUtils.isEmpty(username) || TextUtils.isEmpty(password) ||TextUtils.isEmpty(email)) {
                    Toast.makeText(RegisterActivity.this,"Fill your fields please",Toast.LENGTH_LONG).show();
                } else {
                    RegisterUser(username,password,email);
                }

            }
        });

    }

    private void RegisterUser(final String username,String password,String email) {
        firebaseAuth.createUserWithEmailAndPassword(email,password)
        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    String userID = firebaseUser.getUid();
                    databaseReference = FirebaseDatabase.getInstance().getReference("MyUsers")
                    .child(userID);

                    //hash maps
                    HashMap<String,String> hashMapUsers = new HashMap<>();
                    hashMapUsers.put("id",userID);
                    hashMapUsers.put("username",username.toString());
                    hashMapUsers.put("imageURL","default");
                    //hashMapUsers.put("status","Offline");

                    //Open Main Activity after successful registration
                    databaseReference.setValue(hashMapUsers)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(Task<Void> task) {
                                    if(task.isSuccessful()) {
                                        Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                    });



                }else {
                    Toast.makeText(RegisterActivity.this,"Invalid email or password",Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    

}