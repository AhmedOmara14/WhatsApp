package com.example.what.Setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.what.R;
import com.example.what.fragment_main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Setting extends AppCompatActivity {
     EditText user_name,descriotion;
    Button btn_update;
    private CircleImageView circleImageView1;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private String currentuserid;
    private Toolbar mToolbar;

    private StorageReference Storage2;
    private static final int bic=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);



        mToolbar =  findViewById(R.id.main_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        user_name=(EditText)findViewById(R.id.username);
        descriotion=(EditText)findViewById(R.id.status);
        btn_update=(Button) findViewById(R.id.update_btn);
        circleImageView1=(CircleImageView)findViewById(R.id.profile_image1);
        Storage2=FirebaseStorage.getInstance().getReference().child("image");
        auth=FirebaseAuth.getInstance();
        currentuserid=auth.getCurrentUser().getUid();
        reference= FirebaseDatabase.getInstance().getReference();

        circleImageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, bic);
            }
        });
        RetrieveUserInfo();
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                updateyourinfo();

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==bic  &&  resultCode==RESULT_OK  &&  data!=null)
        {
            Uri ImageUri = data.getData();

            CropImage.activity()
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1, 1)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == RESULT_OK)
            {


                Uri resultUri = result.getUri();


                StorageReference filePath = Storage2.child(currentuserid + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(Setting.this, "Profile Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                            final String downloaedUrl =   task.getResult().getDownloadUrl().toString();




                            reference.child("Users").child(currentuserid).child("image")
                                    .setValue(downloaedUrl)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task)
                                        {
                                            if (task.isSuccessful())
                                            {
                                                Toast.makeText(Setting.this, "Image save in Database, Successfully...", Toast.LENGTH_SHORT).show();

                                            }
                                            else
                                            {
                                                String message = task.getException().toString();
                                                Toast.makeText(Setting.this, "Error: " + message, Toast.LENGTH_SHORT).show();

                                            }
                                        }
                                    });
                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(Setting.this, "Error: " + message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        }
    }



    private void updateyourinfo(){
        String name=user_name.getText().toString();
        String status=descriotion.getText().toString();



        if(TextUtils.isEmpty(name)){
            Toast.makeText(this, "enter your name", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(status)){
            Toast.makeText(this, "enter your Status", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("id",currentuserid);
            hashMap.put("name",name);
            hashMap.put("Status",status);
            reference.child("Users").child(currentuserid).updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                     if(task.isSuccessful()){
                         Toast.makeText(Setting.this, "Successfull", Toast.LENGTH_SHORT).show();
                         sendusertoMain();
                     }
                     else{
                         String message=task.getException().toString();
                         Toast.makeText(Setting.this, "Error..."+message, Toast.LENGTH_SHORT).show();
                     }
                }
            });
        }

    }

    private void sendusertoMain() {
        Intent mainIntent = new Intent(Setting.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }

    private void RetrieveUserInfo()
    {
        reference.child("Users").child(currentuserid)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot)
                    {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image"))))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrievesStatus = dataSnapshot.child("Status").getValue().toString();
                            String retrieveProfileImage = dataSnapshot.child("image").getValue().toString();

                            user_name.setText(retrieveUserName);
                            descriotion.setText(retrievesStatus);
                            Picasso.get().load(retrieveProfileImage).into(circleImageView1);
                        }
                        else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name")))
                        {
                            String retrieveUserName = dataSnapshot.child("name").getValue().toString();
                            String retrievesStatus = dataSnapshot.child("Status").getValue().toString();

                            user_name.setText(retrieveUserName);
                            descriotion.setText(retrievesStatus);
                        }
                        else
                        {
                            user_name.setVisibility(View.VISIBLE);
                            Toast.makeText(Setting.this, "Please set & update your profile information...", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

}