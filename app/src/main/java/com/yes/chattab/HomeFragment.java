package com.yes.chattab;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import com.yes.chattab.Adapter.PostAdapter;
import com.yes.chattab.Adapter.StoryAdapter;

import Model.Post;
import Model.Story;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private List<Post> postLists;
    ImageView messageBox;
    private RecyclerView recyclerView_story;
    private StoryAdapter storyAdapter;
    private List<Story> storyList;

    private List<String> followingList;

    ProgressBar progressBar;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getActivity().getWindow().setStatusBarColor(0xFF000000);
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView=view.findViewById(R.id.recycler_view);
        messageBox=view.findViewById(R.id.messageBox);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postLists=new ArrayList<>();
        postAdapter=new PostAdapter(getContext(),postLists);
        recyclerView.setAdapter(postAdapter);

        recyclerView_story=view.findViewById(R.id.recycler_view_story);
        recyclerView_story.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager1=new LinearLayoutManager(getContext(),
                LinearLayoutManager.HORIZONTAL,false);
        recyclerView_story.setLayoutManager(linearLayoutManager1);
        storyList=new ArrayList<>();
        storyAdapter=new StoryAdapter(getContext(),storyList);
        recyclerView_story.setAdapter(storyAdapter);

        postAdapter.notifyDataSetChanged();


        progressBar=view.findViewById(R.id.progress_circular);

        checkFollowing();

        messageBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(),ChatActivity.class);
                startActivity(intent);
            }
        });

        return view;


    }

    private  void checkFollowing(){
        followingList=new ArrayList<>();
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                followingList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    followingList.add(snapshot.getKey());
                }
                readPosts();
                readStory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readPosts(){
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postLists.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Post post=snapshot.getValue(Post.class);

                    for (String id:followingList){
                        if(post.getPublisher().equals(id)){
                            postLists.add(post);
                        }
                    }
                    for (int i=0;i<1;i++){
                        if(post.getPublisher().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
                            postLists.add(post);
                        }
                    }
                }
                postAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readStory(){
        DatabaseReference reference=FirebaseDatabase.getInstance().getReference("Story");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long timeCurrent=System.currentTimeMillis();
                storyList.clear();
                storyList.add(new Story("",0,0,"",
                        FirebaseAuth.getInstance().getCurrentUser().getUid()));

                for (String id:followingList){
                    int countStory=0;
                    Story story=null;
                    for (DataSnapshot snapshot:dataSnapshot.child(id).getChildren()){
                        story=snapshot.getValue(Story.class);
                        if(timeCurrent>story.getTimestart() && timeCurrent<story.getTimeend()){
                            countStory++;
                        }
                    }
                    if(countStory>0){
                        storyList.add(story);
                    }
                }
                storyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}