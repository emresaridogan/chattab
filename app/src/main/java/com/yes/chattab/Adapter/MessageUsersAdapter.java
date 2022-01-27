package com.yes.chattab.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yes.chattab.MessageActivity;
import com.yes.chattab.MessageUsersActivity;
import com.yes.chattab.R;

import org.w3c.dom.Text;

import java.util.List;

import Model.Chat;
import Model.MessageUsers;

public class MessageUsersAdapter extends RecyclerView.Adapter<MessageUsersAdapter.ViewHolder> {

    private Context mContext;
    private List<MessageUsers> mUsers;
    private boolean ischat;

    String theLastMessage;


    public MessageUsersAdapter(Context mContext, List<MessageUsers> mUsers, boolean ischat) {
        this.mContext = mContext;
        this.mUsers = mUsers;
        this.ischat = ischat;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.contact_item,parent,false);
        return  new MessageUsersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageUsers user=mUsers.get(position);
       holder.username.setText(user.getUsername());
        if(user.getImageurl()!=null){
            Glide.with(mContext).load(user.getImageurl()).into(holder.profile_image);
        }

        if (ischat){
            lastMessage(user.getId(), holder.last_msg);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

        if(mUsers.get(position).getStatus().equals("Çevrimiçi")){
            holder.img_on.setVisibility(View.VISIBLE);
            holder.img_off.setVisibility(View.GONE);
        }else{
            holder.img_on.setVisibility(View.GONE);
            holder.img_off.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(mContext, MessageActivity.class);
                intent.putExtra("userid",user.getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public ImageView profile_image,img_on,img_off;
        private TextView last_msg;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_on=itemView.findViewById(R.id.img_on);
            img_off=itemView.findViewById(R.id.img_off);
            username=itemView.findViewById(R.id.username);
            profile_image=itemView.findViewById(R.id.profile_image);
            last_msg=itemView.findViewById(R.id.last_msg);

        }
    }
    //check for last message
    private void lastMessage(final String userid, final TextView last_msg){
        theLastMessage = "default";
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chats");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Chat chat = snapshot.getValue(Chat.class);
                    if (firebaseUser != null && chat != null) {
                        if (chat.getReceiver().equals(firebaseUser.getUid()) && chat.getSender().equals(userid) ||
                                chat.getReceiver().equals(userid) && chat.getSender().equals(firebaseUser.getUid()) ) {
                            theLastMessage = chat.getMessage();
                        }
                    }
                    if (!chat.isIsseen() && firebaseUser.getUid().equals(chat.getReceiver())){
                        last_msg.setTypeface(null, Typeface.BOLD);
                    }else {
                        last_msg.setTypeface(null, Typeface.NORMAL);
                    }
                }


                switch (theLastMessage){
                    case  "default":
                        last_msg.setText("No Message");
                        break;

                    default:
                        last_msg.setText(theLastMessage);
                        break;
                }

                theLastMessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
