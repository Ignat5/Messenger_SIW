package com.example.messenger_siw.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messenger_siw.MessageActivity;
import com.example.messenger_siw.Model.User;
import com.example.messenger_siw.R;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyViewHolder> {
    private Context context;
    private List<User> allUsers;

    public UserAdapter (Context context,List<User> allUsers) {
        this.context = context;
        this.allUsers = allUsers;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View myView = LayoutInflater.from(context).inflate(R.layout.item_user, parent,false);
        return new UserAdapter.MyViewHolder(myView);
    }

    @NonNull


    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        User user = allUsers.get(position);
        holder.TV_username.setText(user.getUsername());
        if(user.getImageURL().equals("default")) {
            holder.IV_userImage.setImageResource(R.mipmap.ic_launcher);
        }else {
            Glide.with(context).load(user.getImageURL()).into(holder.IV_userImage);
        }
        //
        if(user.getStatus().equals("Online")) {
            holder.TV_username.setTextColor(context.getResources().getColor(R.color.MyGreen));
        }
        if(user.getStatus().equals("Offline")) {
            holder.TV_username.setTextColor(context.getResources().getColor(R.color.MyRed));
        }
        //
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra(MessageActivity.EXTRA_ID,user.getId());
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return allUsers.size();
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView TV_username;
        public ImageView IV_userImage;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            TV_username = itemView.findViewById(R.id.TV_item_username);
            IV_userImage = itemView.findViewById(R.id.IV_item_image);
        }
    }

}
