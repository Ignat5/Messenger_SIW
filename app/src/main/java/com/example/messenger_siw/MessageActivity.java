package com.example.messenger_siw;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.messenger_siw.Adapters.MessageAdapter;
import com.example.messenger_siw.Model.Chat;
import com.example.messenger_siw.Model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MessageActivity extends AppCompatActivity {

    public static final String EXTRA_ID = "userID";
    //
    EditText ET_message;
    ImageButton IB_send_message;
    TextView TV_username;
    ImageView IV_userImage;
    //
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    String userID;
    //
    RecyclerView RV_message;
    MessageAdapter messageAdapter;
    List<Chat> allChats;
    //Listener
    ValueEventListener seenMessageListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        //initialize components
        TV_username = findViewById(R.id.TV_message);
        IV_userImage = findViewById(R.id.IV_message);
        ET_message = findViewById(R.id.ET_message);
        IB_send_message = findViewById(R.id.IB_message);
        Toolbar toolbar = findViewById(R.id.toolbar_message);
        setSupportActionBar(toolbar);
        //Recycler View
        RV_message = findViewById(R.id.RV_message);
        RV_message.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        RV_message.setLayoutManager(layoutManager);
        //get intent info (companion's id)
        userID = getIntent().getExtras().getString(EXTRA_ID);
        //set username and image of your companion
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("MyUsers")
                .child(userID);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                TV_username.setText(user.getUsername());
                if(user.getImageURL().equals("default")) {
                    IV_userImage.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(MessageActivity.this)
                            .load(user.getImageURL())
                            .into(IV_userImage);
                }
                readMessages(firebaseUser.getUid(),userID,user.getImageURL());
                //SeenMessage(userID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //set onSend message listener
        IB_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String message = ET_message.getText().toString();
                if(!message.isEmpty()) {
                    sendMessage(firebaseUser.getUid(),userID,message);
                }else {
                    Toast.makeText(MessageActivity.this,"You can't send blank message",Toast.LENGTH_SHORT).show();
                }
                ET_message.setText("");//clean edit text after sending a message
            }
        });
        //написать метод для получения всех сообщений->передать нужные параметры в
        //конструктор адаптера, установить адаптер для RV
        //SeenMessage(userID);
    }
    private void sendMessage(String senderID,String receiverID,String message) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("senderID",senderID);
        hashMap.put("receiverID",receiverID);
        hashMap.put("message",message);
        hashMap.put("isSeen",false);
        databaseReference.child("Chats").push().setValue(hashMap);
        DatabaseReference chatReference = FirebaseDatabase.getInstance().getReference("ChatList")
                .child(firebaseUser.getUid())
                .child(userID);
        chatReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    chatReference.child("id").setValue(userID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readMessages(String myID,String companionID,String imageURL) {
        allChats = new ArrayList<>();
        databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                allChats.clear();
                for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    //allChats.add(dataSnapshot.getValue(Chat.class));
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getSenderID().equals(myID) && chat.getReceiverID().equals(companionID)||
                            (chat.getSenderID().equals(companionID) && chat.getReceiverID().equals(myID))) {
                        allChats.add(chat);
                    }
                }
                messageAdapter = new MessageAdapter(getApplicationContext(),allChats,imageURL);
                RV_message.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        SeenMessage(userID);
    }

    //updating user's status
    private void UpdateStatus(String status) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("MyUsers")
                .child(firebaseUser.getUid());
        HashMap<String,Object> hashMap = new HashMap();
        hashMap.put("status",status);
        userRef.updateChildren(hashMap);
    }
    //seen message
    private void SeenMessage(String user_id) {
       databaseReference = FirebaseDatabase.getInstance().getReference("Chats");
       databaseReference.addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               for(DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if(chat.getReceiverID().equals(firebaseUser.getUid()) && chat.getSenderID().equals(user_id)) {
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("isSeen",true);
                        dataSnapshot.getRef().updateChildren(hashMap);
                    }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
    }
    //
    @Override
    protected void onResume() {
        super.onResume();
        UpdateStatus("Online");
    }
    @Override
    protected void onPause() {
        super.onPause();
        //databaseReference.removeEventListener(seenMessageListener);
        UpdateStatus("Offline");
    }
}