package com.example.messenger_siw;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

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
                    hashMapUsers.put("username",ET_username.toString());
                    hashMapUsers.put("imageURL","default");

                    //Open Main Activity after successful registration



                }
            }
        });

    }

}