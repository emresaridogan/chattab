package com.yes.chattab.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yes.chattab.MainActivity;
import com.yes.chattab.ProfileFragment;
import com.yes.chattab.R;

import java.util.HashMap;
import java.util.List;

import Model.User;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> mUser;
    private boolean isFragment;

    FirebaseUser firebaseUser;

    public UserAdapter(Context context, List<User> mUser,boolean isFragment) {
        this.context = context;
        this.mUser = mUser;
        this.isFragment = isFragment;


    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
       View view= LayoutInflater.from(context).inflate(R.layout.user_item,parent,false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
    final User user=mUser.get(position);
    holder.btn_follow.setVisibility(View.VISIBLE);
    holder.username.setText(user.getUsername());
    holder.fullname.setText(user.getFullname());
    Glide.with(context).load(user.getImageurl()).into(holder.image_profile);
    isFollowing(user.getId(),holder.btn_follow);


         if(user.getId().equals((firebaseUser.getUid()))){
             holder.btn_follow.setVisibility(View.GONE);
            }

         holder.itemView.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(isFragment){

                 SharedPreferences.Editor editor=context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                 editor.putString("profileid",user.getId());
                 editor.apply();
                 ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new ProfileFragment()).commit();
                 }else{
                     Intent intent=new Intent(context, MainActivity.class);
                     intent.putExtra("publisherid",user.getId());
                     context.startActivity(intent);
                 }
             }

         });

         holder.btn_follow.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if(holder.btn_follow.getText().equals("Takip Et")){
                     FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                             .child("Following").child(user.getId()).setValue(true);
                     FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                             .child("Followers").child(firebaseUser.getUid()).setValue(true);
                     addNotifications(user.getId());
                 }
                 else
                 {
                     FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                             .child("Following").child(user.getId()).removeValue();
                     FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                             .child("Followers").child(firebaseUser.getUid()).removeValue();
                 }
             }
         });
    }
    private void addNotifications(String userid){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notifications").child(userid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","Seni takip etmeye başladı.");
        hashMap.put("postid","");
        hashMap.put("ispost",false);
        reference.push().setValue(hashMap);
    }

    @Override
    public int getItemCount() {
        return mUser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView username;
        public TextView fullname;
        public CircleImageView image_profile;
        public Button btn_follow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username=itemView.findViewById(R.id.username);
            fullname=itemView.findViewById(R.id.fullname);
            image_profile=itemView.findViewById(R.id.image_profile);
            btn_follow=itemView.findViewById(R.id.btn_follow);

        }
    }
    private  void isFollowing(String userid,Button button){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(userid).exists()){
                    button.setText("Takip Ediliyor");
                }else{
                    button.setText("Takip Et");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
