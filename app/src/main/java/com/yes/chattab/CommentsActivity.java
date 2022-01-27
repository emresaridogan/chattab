package com.yes.chattab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yes.chattab.Adapter.CommentAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Model.Comment;
import Model.User;

public class CommentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;

    EditText addComment;
    ImageView image_profile;
    TextView post;

    String postid,publisherid;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar=findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Yorumlar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Intent intent=getIntent();
        postid=intent.getStringExtra("postid");
        publisherid=intent.getStringExtra("publisherid");

        recyclerView=findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        commentList=new ArrayList<>();
        commentAdapter=new CommentAdapter(this,commentList,postid);
        recyclerView.setAdapter(commentAdapter);

        addComment=findViewById(R.id.add_comment);
        image_profile=findViewById(R.id.image_profile);
        post=findViewById(R.id.post);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addComment.getText().toString().equals("")){
                    Toast.makeText(CommentsActivity.this, "Boş yorum atamazsınız", Toast.LENGTH_SHORT).show();
                }else{
                    addComment();
                }

            }
        });
        getImage();;
        readComments();

    }
    private void addComment(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Comments").child(postid);

        String commentid=reference.push().getKey();

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("comment",addComment.getText().toString());
        hashMap.put("publisher",firebaseUser.getUid());
        hashMap.put("commentid",commentid);


        reference.child(commentid).setValue(hashMap);
        addNotifications();
        addComment.setText("");
    }

    private void addNotifications(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Notifications").child(publisherid);
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("userid",firebaseUser.getUid());
        hashMap.put("text","Yorum yaptı: "+addComment.getText().toString());
        hashMap.put("postid",postid);
        hashMap.put("ispost",true);
        reference.push().setValue(hashMap);
    }

    private void getImage(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getImageurl()).into(image_profile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void readComments(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Comments").child(postid);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Comment comment=snapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}