package com.yes.chattab.Adapter;

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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.yes.chattab.ChatActivity;
import com.yes.chattab.MessageActivity;
import com.yes.chattab.R;

import java.util.List;

import Model.Chat;
import Model.MessageUsers;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSJ_TYPE_LEFT=0;
    public static final int MSJ_TYPE_RIGHT=1;

    private Context mContext;
    private List<Chat> mChat;
    private String imageurl;
    FirebaseUser firebaseUser;

    public MessageAdapter(Context mContext, List<Chat> mChat,String imageurl) {
        this.mContext = mContext;
        this.mChat = mChat;
        this.imageurl = imageurl;

    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==MSJ_TYPE_RIGHT){
            View view= LayoutInflater.from(mContext).inflate(R.layout.chat_item_right,parent,false);
            return  new MessageAdapter.ViewHolder(view);
        }else{
            View view= LayoutInflater.from(mContext).inflate(R.layout.chat_item_left,parent,false);
            return  new MessageAdapter.ViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        Chat chat=mChat.get(position);
        holder.show_message.setText(chat.getMessage());
        holder.show_date.setText(chat.getTimeStamp());

        if (position==mChat.size()-1){
            if(chat.isIsseen()){
                holder.txt_seen.setImageResource(R.drawable.seen);
            }else{
                holder.txt_seen.setImageResource(R.drawable.sended);
            }
        }else{
            holder.txt_seen.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mChat.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView show_message,show_date;
        ImageView txt_seen;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            show_date=itemView.findViewById(R.id.show_date);
            show_message=itemView.findViewById(R.id.show_message);
            txt_seen=itemView.findViewById(R.id.txt_seen);

        }
    }

    @Override
    public int getItemViewType(int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        if(mChat.get(position).getSender().equals(firebaseUser.getUid())){
            return MSJ_TYPE_RIGHT;
        }else{
            return MSJ_TYPE_LEFT;
        }
    }
}

