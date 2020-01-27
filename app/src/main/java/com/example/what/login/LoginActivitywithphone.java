package com.example.what.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.what.fragment_main.MainActivity;
import com.example.what.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class LoginActivitywithphone extends AppCompatActivity {
    EditText txt_phone,pass;
    Button button_login,verity;
    private FirebaseUser firebaseUser;
    private FirebaseAuth auth;
    private DatabaseReference reference;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private  String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activitywithphone);
         intialization();

         button_login.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 String phoneNumber=txt_phone.getText().toString();
                 if(TextUtils.isEmpty(txt_phone.getText())){
                     Toast.makeText(LoginActivitywithphone.this, "Enter your phone num", Toast.LENGTH_SHORT).show();
                 }
                 else {

                     progressDialog.setTitle("Create new Account");
                     progressDialog.setMessage("Wait....While we are Sending  Message");
                     progressDialog.setCanceledOnTouchOutside(false);
                     progressDialog.show();

                     txt_phone.setVisibility(View.INVISIBLE);
                     button_login.setVisibility(View.INVISIBLE);

                     pass.setVisibility(View.VISIBLE);
                     verity.setVisibility(View.VISIBLE);

                     PhoneAuthProvider.getInstance().verifyPhoneNumber(
                             phoneNumber,        // Phone number to verify
                             60,                 // Timeout duration
                             TimeUnit.SECONDS,   // Unit of timeout
                             LoginActivitywithphone.this,               // Activity (for callback binding)
                             mCallbacks);        // OnVerificationStateChangedCallbacks

                 }

             }
         });

         verity.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 txt_phone.setVisibility(View.INVISIBLE);
                 button_login.setVisibility(View.INVISIBLE);

                 String code=pass.getText().toString();
                 if(TextUtils.isEmpty(code)){
                     Toast.makeText(LoginActivitywithphone.this, "Enter code", Toast.LENGTH_SHORT).show();
                 }
                 else{

                     progressDialog.setTitle("Create new Account");
                     progressDialog.setMessage("Wait....While we are creating account");
                     progressDialog.setCanceledOnTouchOutside(false);
                     progressDialog.show();

                     PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, code);
                     signInWithPhoneAuthCredential(credential);
                 }
             }
         });

         mCallbacks=new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
             @Override
             public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

                 signInWithPhoneAuthCredential(phoneAuthCredential);
             }

             @Override
             public void onVerificationFailed(@NonNull FirebaseException e) {
                 progressDialog.dismiss();
                 Toast.makeText(LoginActivitywithphone.this, "Invalid num", Toast.LENGTH_SHORT).show();

                 txt_phone.setVisibility(View.VISIBLE);
                 button_login.setVisibility(View.VISIBLE);

                 pass.setVisibility(View.INVISIBLE);
                 verity.setVisibility(View.INVISIBLE);
             }
             public void onCodeSent( String verificationId,
                                     PhoneAuthProvider.ForceResendingToken token) {

                 mVerificationId = verificationId;
                 mResendToken = token;


             }
         };


    }
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                               progressDialog.dismiss();
                            sendusertoMain();
                        } else {

                            }
                        }

                });
    }


    private void intialization() {
        txt_phone=(EditText)findViewById(R.id.email_login1) ;
        pass=(EditText)findViewById(R.id.pass_login1) ;
        button_login=(Button) findViewById(R.id.login_btn1) ;
        verity=(Button) findViewById(R.id.verity_btn1) ;
        progressDialog=new ProgressDialog(this);
        auth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference();

    }

    private void sendusertoMain(){
        Intent intent=new Intent(LoginActivitywithphone.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();

    }
}
