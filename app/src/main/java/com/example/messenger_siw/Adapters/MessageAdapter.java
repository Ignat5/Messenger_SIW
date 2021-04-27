package com.example.messenger_siw.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.messenger_siw.Model.Chat;
import com.example.messenger_siw.Model.User;
import com.example.messenger_siw.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    public Context context;
    public List<Chat> allChats;
    public String imgURL;
    //
    FirebaseUser firebaseUser;
    //
    public static final int MSG_TYPE_LEFT = 0;
    public static final int MSG_TYPE_RIGHT = 1;

    public MessageAdapter(Context context,List<Chat> allChats,String imgURL) {
        this.context = context;
        this.allChats = allChats;
        this.imgURL = imgURL;
    }


    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        if(viewType == MSG_TYPE_LEFT) {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_left,parent,false);
        }else if(viewType == MSG_TYPE_RIGHT) {
            view = LayoutInflater.from(context).inflate(R.layout.item_chat_right,parent,false);
        }
        return new MessageAdapter.MessageHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        final Chat chat = allChats.get(position);
        holder.TV_message.setText(chat.getMessage());
        //
        if(!firebaseUser.getUid().equals(chat.getSenderID())) {
            if (imgURL.equals("default")) {
                holder.IV_profile.setImageResource(R.mipmap.ic_launcher);
            } else {
                Glide.with(context).load(imgURL).into(holder.IV_profile);
            }
        }
        if(position == allChats.size() - 1) {
            if (chat.getIsSeen()) {
                holder.TV_status.setText("Seen");
            }else {
                holder.TV_status.setText("Delivered");
            }
            //holder.TV_status.setVisibility(View.VISIBLE);
        }else {
            holder.TV_status.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return allChats.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {

        public TextView TV_message;
        public ImageView IV_profile;
        public TextView TV_status;

        public MessageHolder(@NonNull View itemView) { //!!!
            super(itemView);
            TV_message = itemView.findViewById(R.id.TV_chat_message);
            IV_profile = itemView.findViewById(R.id.IV_chat_profileImage);
            TV_status = itemView.findViewById(R.id.TV_message_status);

        }
    }
    @Override
    public int getItemViewType(int position) {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if(allChats.get(position).getSenderID().equals(firebaseUser.getUid())) {
            //my message
            return MSG_TYPE_RIGHT;
        }else {
            //message of my companion
            return MSG_TYPE_LEFT;
        }
    }

}
