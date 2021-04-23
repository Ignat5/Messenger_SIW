package com.example.messenger_siw.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.messenger_siw.Model.User;
import com.example.messenger_siw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileFragment extends Fragment {

    private TextView TV_profile_username;
    private ImageView IV_profile;

    FirebaseUser firebaseUser;
    DatabaseReference userDBReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        TV_profile_username = view.findViewById(R.id.TV_profile_username);
        IV_profile = view.findViewById(R.id.IV_profile);

        setWidgets();

        return view;
    }
    private void setWidgets() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userDBReference = FirebaseDatabase.getInstance().getReference("MyUsers").child(firebaseUser.getUid());
        userDBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                TV_profile_username.setText(user.getUsername());
                if(user.getImageURL().equals("default")) {
                    IV_profile.setImageResource(R.mipmap.ic_launcher);
                }
                Glide.with(getContext()).load(user.getImageURL()).into(IV_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}