package com.example.messenger_siw.Fragments;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.messenger_siw.Model.User;
import com.example.messenger_siw.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends Fragment {

    private TextView TV_profile_username;
    private ImageView IV_profile;

    FirebaseUser firebaseUser;
    DatabaseReference userDBReference;

    StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask storageTask;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        TV_profile_username = view.findViewById(R.id.TV_profile_username);
        IV_profile = view.findViewById(R.id.IV_profile);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        userDBReference = FirebaseDatabase.getInstance().getReference("MyUsers").child(firebaseUser.getUid());

        //StorageReference
        storageReference = FirebaseStorage.getInstance().getReference("uploads");

        userDBReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                TV_profile_username.setText(user.getUsername());
                if(user.getImageURL().equals("default")) {
                    IV_profile.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(getContext()).load(user.getImageURL()).into(IV_profile);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //3:14:03
            }
        });

        IV_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });


        return view;
    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContext().getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void UploadImage () {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Uploading your file");
        progressDialog.show();

        if(imageUri!=null) {
            final StorageReference fileReference = storageReference
                    .child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            storageTask = fileReference.putFile(imageUri);
            storageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String userUri = downloadUri.toString();
                        userDBReference = FirebaseDatabase.getInstance().
                                getReference("MyUsers").child(firebaseUser.getUid());
                        HashMap<String,Object> map = new HashMap<>();
                        map.put("imageURL",userUri);
                        userDBReference.updateChildren(map);
                        progressDialog.dismiss();
                    }else {
                        Toast.makeText(getContext(),"Failed to download file",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(),e.getMessage().toString(),Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                }
            });
        }else {
            Toast.makeText(getContext(),"No Image was selected",Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
    @Override
    public void onActivityResult (int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode,resultCode,data);
            if(requestCode == IMAGE_REQUEST && resultCode == RESULT_OK
            && data!=null && data.getData()!=null) {
                imageUri = data.getData();
            }
            if(storageTask!=null && storageTask.isInProgress()) {
                Toast.makeText(getContext(),"Uploading image...",Toast.LENGTH_LONG).show();
            }else {
                UploadImage();
            }
}

}