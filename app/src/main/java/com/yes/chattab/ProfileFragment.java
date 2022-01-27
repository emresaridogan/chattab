package com.yes.chattab;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.yes.chattab.Adapter.MyPhotosAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import Model.Post;
import Model.User;


public class ProfileFragment extends Fragment {

    ImageView image_profile,options;
    TextView posts,followers,following,fullname,bio,username;
    Button edit_profile;

    private List<String> mySaves;
    RecyclerView recyclerView_saves;
    MyPhotosAdapter myPhotosAdapter_saves;
    List<Post> postList_saves;

    RecyclerView recyclerView;
    MyPhotosAdapter myPhotosAdapter;
    List<Post> postList;

    FirebaseUser firebaseUser;
    String profileid;
    ImageButton my_photos,saved_photos;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profile, container, false);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs=getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileid=prefs.getString("profileid","none");
        image_profile=view.findViewById(R.id.image_profile);
        options=view.findViewById(R.id.options);
        posts=view.findViewById(R.id.posts);
        followers=view.findViewById(R.id.followers);
        following=view.findViewById(R.id.following);
        fullname=view.findViewById(R.id.fullname);
        bio=view.findViewById(R.id.bio);
        username=view.findViewById(R.id.username);
        edit_profile=view.findViewById(R.id.edit_profile);
        my_photos=view.findViewById(R.id.my_photos);
        saved_photos=view.findViewById(R.id.saved_photos);

        recyclerView=view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList=new ArrayList<>();
        myPhotosAdapter=new MyPhotosAdapter(getContext(),postList);
        recyclerView.setAdapter(myPhotosAdapter);

        recyclerView_saves=view.findViewById(R.id.recycler_view_save);
        recyclerView_saves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager_saves=new GridLayoutManager(getContext(),3);
        recyclerView_saves.setLayoutManager(linearLayoutManager_saves);
        postList_saves=new ArrayList<>();
        myPhotosAdapter_saves=new MyPhotosAdapter(getContext(),postList_saves);
        recyclerView_saves.setAdapter(myPhotosAdapter_saves);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView_saves.setVisibility(View.GONE);

        userInfo();
        getFollowers();
        getnrPosts();
        myPhotos();
        mysaves();

        if(profileid.equals(firebaseUser.getUid())){
            edit_profile.setText("Profili Düzenle");
            options.setVisibility(View.VISIBLE);
        }else{
            checkFollow();
            saved_photos.setVisibility(View.GONE);
        }

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn=edit_profile.getText().toString();
                if(btn.equals("Profili Düzenle")){
                    Intent intent=new Intent(getContext(),EditProfileActivity.class);
                    startActivity(intent);
                }else if(btn.equals("Takip Et")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(profileid).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("Followers").child(firebaseUser.getUid()).setValue(true);
                    addNotifications();
                }else if(btn.equals("Takip Ediliyor")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid())
                            .child("Following").child(profileid).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileid)
                            .child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });



        options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),OptionsActivity.class);
                startActivity(intent);
            }
        });

        my_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.VISIBLE);
                recyclerView_saves.setVisibility(View.GONE);
            }
        });
        saved_photos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.setVisibility(View.GONE);
                recyclerView_saves.setVisibility(View.VISIBLE);
            }
        });

        followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),FollowersActivity.class);
                intent.putExtra("id",profileid);
                intent.putExtra("title","Takipçiler");
                startActivity(intent);
            }
        });
        following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),FollowersActivity.class);
                intent.putExtra("id",profileid);
                intent.putExtra("title","Takip Edilenler");
                startActivity(intent);
            }
        });

        return view;
    }

    private void addNotifications(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notifications").child(profileid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","Seni takip etmeye başladı.");
        hashMap.put("postid","");
        hashMap.put("ispost",false);
        reference.push().setValue(hashMap);
    }

    private void userInfo(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(profileid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getContext()==null){
                    return;
                }
                User user=dataSnapshot.getValue(User.class);
                Picasso.get().load(user.getImageurl()).into(image_profile);
                username.setText(user.getUsername());
                fullname.setText(user.getFullname());
                bio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollow(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.child(profileid).exists()){
                edit_profile.setText("Takip Ediliyor");
            }else{
                edit_profile.setText("Takip Et");
            }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowers(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("Followers");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followers.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        DatabaseReference reference1=FirebaseDatabase.getInstance().getReference()
                .child("Follow").child(profileid).child("Following");

        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                following.setText(""+dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getnrPosts(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int i=0;
                for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post=snapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)){
                        i++;
                    }
                }
                posts.setText(""+i);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void myPhotos(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post=snapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileid)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myPhotosAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void mysaves(){
        mySaves=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Saves")
                .child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    mySaves.add(snapshot.getKey());
                }
                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readSaves(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList_saves.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Post post=snapshot.getValue(Post.class);

                    for(String id:mySaves){
                        if(post.getPostid().equals(id)){
                            postList_saves.add(post);
                        }
                    }
                }
                myPhotosAdapter_saves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}