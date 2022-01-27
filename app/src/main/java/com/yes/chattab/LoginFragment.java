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
import android.widget.TextView;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginFragment extends Fragment {
    EditText email,pass;
    TextView forgetPass;
    Button login;
    private FirebaseAuth firebaseAuth;

    public LoginFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
        if (firebaseUser !=null){
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.fragment_login, container, false);

        email=view.findViewById(R.id.email);
        pass=view.findViewById(R.id.pass);
        forgetPass=view.findViewById(R.id.forget_pass);
        login=view.findViewById(R.id.login);

        email.setTranslationX(800);
        pass.setTranslationX(800);
        forgetPass.setTranslationX(800);
        login.setTranslationX(800);

        email.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(300).start();
        pass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        forgetPass.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(500).start();
        login.animate().translationX(0).alpha(1).setDuration(800).setStartDelay(700).start();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog pd=new ProgressDialog(getContext());
                pd.setMessage("Lütfen bekleyin.");
                pd.show();

                String adress=email.getText().toString();
                String password=pass.getText().toString();

                if(TextUtils.isEmpty(adress) || TextUtils.isEmpty(password) ){
                    Toast.makeText(getContext(), "Lütfen boş bırakmayın.", Toast.LENGTH_SHORT).show();

                }else{
                    firebaseAuth.signInWithEmailAndPassword(adress,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getCurrentUser().getUid());

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        pd.dismiss();
                                        //Toast.makeText(getContext(),"Giriş Başarılı",Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getContext(), MainActivity.class);
                                        intent.addFlags(intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError e) {
                                    pd.dismiss();
                                    }
                                });
                            }else{
                                pd.dismiss();
                                Toast.makeText(getContext(),"Giriş başarısız.",Toast.LENGTH_LONG).show();

                            }
                        }
                    });




                }


            }
        });
        return view;
    }


}