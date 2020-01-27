package com.example.what.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.what.fragment_main.MainActivity;
import com.example.what.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class loginActivity extends AppCompatActivity {

    private FirebaseUser firebaseUser;
    private EditText txt_email,txt_pass;
    private Button btn_login;
    private TextView txt_register;
    private Button login_with_phone;
    private FirebaseAuth auth;
    private DatabaseReference reference_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        txt_email=(EditText)findViewById(R.id.email_login);
        txt_pass=(EditText)findViewById(R.id.pass_login);
        btn_login=(Button)findViewById(R.id.login_btn);
        txt_register=(TextView)findViewById(R.id.cratenewaccount);
        login_with_phone=(Button)findViewById(R.id.login_btn_with_phone);
        reference_user= FirebaseDatabase.getInstance().getReference().child("Users");

        auth=FirebaseAuth.getInstance();
        firebaseUser=auth.getCurrentUser();

        txt_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendusertoRegister();
            }
        });
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotochat();
            }
        });

        login_with_phone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendusertologinwithphone();
            }
        });
    }



    private void sendusertoMain(){
        Intent intent=new Intent(loginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
    private void sendusertoRegister() {
        Intent intent=new Intent(loginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }
    private void sendusertologinwithphone() {
        Intent intent=new Intent(loginActivity.this, LoginActivitywithphone.class);
        startActivity(intent);
    }


    private void gotochat() {
        String email=txt_email.getText().toString();
        String pass=txt_pass.getText().toString();


        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "enter your email", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(pass)){
            Toast.makeText(this, "enter your password", Toast.LENGTH_SHORT).show();
        }
        else{

//            String current_user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();

            auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        sendusertoMain();
                        Toast.makeText(loginActivity.this, "Welcome", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

}
