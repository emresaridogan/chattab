package com.yes.chattab;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

public class RegisterFragment extends Fragment {
    EditText email,pass,cpass,username,fn;
    Button register;
    private FirebaseAuth firebaseAuth;
    ProgressDialog pd;
    DatabaseReference reference;

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    public RegisterFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view=inflater.inflate(R.layout.fragment_register, container, false);

        username=view.findViewById(R.id.username);
        email=view.findViewById(R.id.email);
        pass=view.findViewById(R.id.pass);
        cpass=view.findViewById(R.id.cpass);
        register=view.findViewById(R.id.register);
        fn=view.findViewById(R.id.fullname);

        username.setTranslationX(800);
        email.setTranslationX(800);
        fn.setTranslationX(800);
        pass.setTranslationX(800);
        cpass.setTranslationX(800);
        register.setTranslationX(800);
        fn.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(1700).start();
        username.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(1700).start();
        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(1700).start();
        pass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(1700).start();
        cpass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(1700).start();
        register.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(1700).start();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String adress=email.getText().toString();
                String password=pass.getText().toString();
                String uname=username.getText().toString();
                String full=fn.getText().toString();
                String cp=cpass.getText().toString();

                pd=new ProgressDialog(getContext());
                pd.setMessage("Lütfen bekleyin...");
                pd.show();

                if(TextUtils.isEmpty(adress) || TextUtils.isEmpty(password) || TextUtils.isEmpty(uname) ){
                    Toast.makeText(getContext(), "Lütfen boş bırakmayın.", Toast.LENGTH_SHORT).show();

                }else   if (password.length()<6){
                    Toast.makeText(getContext(), "Şifre en az 6 haneli olmalı..", Toast.LENGTH_SHORT).show();

                }
                else{
                register(uname,password,adress,full);
                }

            }
        });

       return view;
    }
    private void register(String username,String pass , String email,String fn){

        firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
                String userid=firebaseUser.getUid();
                reference= FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                    HashMap<String,Object> data=new HashMap<>();
                    data.put("id",userid);
                    data.put("username",username.toLowerCase());
                    data.put("fullname",fn);
                    data.put("bio","");
                    data.put("imageurl","gs://chattab-yes.appspot.com/Default/user.png");
                    data.put("status","offline");
                    data.put("search",username.toLowerCase());

                    reference.setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                pd.dismiss();
                                Toast.makeText(getContext(),"Kayıt Başarılı",Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.addFlags(intent.FLAG_ACTIVITY_SINGLE_TOP);
                                startActivity(intent);
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            pd.dismiss();
                            Toast.makeText(getContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

                        }
                    });


                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(),e.getLocalizedMessage().toString(),Toast.LENGTH_LONG).show();

            }
        });





    }
}