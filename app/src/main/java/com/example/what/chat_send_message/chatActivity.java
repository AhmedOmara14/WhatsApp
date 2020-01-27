package com.example.what.chat_send_message;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.what.R;
import com.example.what.contacts_fragment.contacts;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatActivity extends AppCompatActivity {
    private String messageReceiverID,
            messageReceiverName,
            messageReceiverImage,
            messageSenderID;

    private TextView userName, userLastSeen;
    private CircleImageView userImage;
    FirebaseUser fuser;
    private FirebaseAuth mAuth;
    private DatabaseReference RootRef,userref;
    Toolbar mToolbar;

    private ImageView SendMessageButton, SendFilesButton;
    private EditText MessageInputText;
    private com.example.what.contacts_fragment.contacts contacts;
    private List<Messages> messagesList ;
    private LinearLayoutManager linearLayoutManager;
    private messageAdapter messageAdapter1;
    private RecyclerView userMessagesList;
    StorageReference storageReference;
    String date,time;
    String checker="";
    String messageid;
    private String saveCurrentTime, saveCurrentDate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
          mToolbar = findViewById(R.id.main_toolbar);
          setSupportActionBar(mToolbar);
          getSupportActionBar().setDisplayHomeAsUpEnabled(true);
          getSupportActionBar().setDisplayShowHomeEnabled(true);
          View logo = getLayoutInflater().inflate(R.layout.custom_toolbar, null);
          mToolbar.addView(logo);



            mAuth = FirebaseAuth.getInstance();
            messageSenderID = mAuth.getCurrentUser().getUid();
            RootRef = FirebaseDatabase.getInstance().getReference();


            messageReceiverID = getIntent().getExtras().get("userid").toString();
            messageReceiverName = getIntent().getExtras().get("name_mm").toString();
            messageReceiverImage = getIntent().getExtras().get("image").toString();


            IntializeControllers();

            userName = (TextView) findViewById(R.id.mess_chat);
            userImage = (CircleImageView) findViewById(R.id.image_chat);

            userName.setText(messageReceiverName);

            SendMessageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SendMessage(messageSenderID, messageReceiverID, MessageInputText.getText().toString());
                    MessageInputText.setText("");

                }

            });

            SendFilesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                   CharSequence options[]=new CharSequence[]{
                            "Image",
                           "PDF File",
                           "WORD File"

                   };
                    AlertDialog.Builder builder=new AlertDialog.Builder(chatActivity.this);
                    builder.setTitle("Choose what u want");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which==0)
                            {
                                checker="image";
                                Intent intent=new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("image/*");
                                startActivityForResult(intent.createChooser(intent,"Select u want"),1);
                            }
                            if (which==1)
                            {
                                checker="PDF";

                            }
                            if (which==2)
                            {
                                checker="Docx";

                            }
                        }
                    });
                    builder.show();
                }
            });

                RootRef.child("Users").child(messageReceiverID).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                        contacts contact=dataSnapshot.getValue(contacts.class);
                        Picasso.get().load(messageReceiverImage).placeholder(R.drawable.profile_image).into(userImage);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        readmessage(fuser.getUid(),messageReceiverID,messageReceiverImage);
        displaylastseen();
        }


        private void IntializeControllers()
    {

        SendMessageButton = (ImageView) findViewById(R.id.btn_send_mess_chat);
        SendFilesButton = (ImageView) findViewById(R.id.btn_send_file);
        storageReference= FirebaseStorage.getInstance().getReference().child("images");
        MessageInputText = (EditText) findViewById(R.id.yourmessage_chat);
        fuser=FirebaseAuth.getInstance().getCurrentUser();
        userMessagesList = (RecyclerView) findViewById(R.id.recycler_chat_message);
        userMessagesList.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        userMessagesList.setLayoutManager(linearLayoutManager);
        //userMessagesList.setAdapter(messageAdapter1);
        userLastSeen=(TextView)findViewById(R.id.lastseen_chat);

        RootRef=FirebaseDatabase.getInstance().getReference();



        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat currentDate = new SimpleDateFormat("MMM dd, yyyy");
        saveCurrentDate = currentDate.format(calendar.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("hh:mm a");
        saveCurrentTime = currentTime.format(calendar.getTime());


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1  &&  resultCode==RESULT_OK  &&  data!=null)
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


                StorageReference filePath = storageReference.child(messageSenderID + ".jpg");

                filePath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task)
                    {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(chatActivity.this, "Profile Image uploaded Successfully...", Toast.LENGTH_SHORT).show();

                            final String downloaedUrl =   task.getResult().getDownloadUrl().toString();



                            DatabaseReference usermessageref=FirebaseDatabase.
                                    getInstance().getReference().child("Messages").
                                    child(messageSenderID).child(messageReceiverID).push();

                            messageid=usermessageref.getKey();
                            HashMap<String ,Object> hashMap=new HashMap<>();
                            hashMap.put("Sender",messageSenderID);
                            hashMap.put("Receiver",messageReceiverID);
                            hashMap.put("Message",downloaedUrl);
                            hashMap.put("time",saveCurrentTime);
                            hashMap.put("type","image");
                            hashMap.put("date",saveCurrentDate);
                            hashMap.put("messageid",messageid);

                            RootRef.child("Messages").push().setValue(hashMap);

                        }
                        else
                        {
                            String message = task.getException().toString();
                            Toast.makeText(chatActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        }
    }

    private void SendMessage(String Sender, String receiver, String Message)
    {
        String messageText = MessageInputText.getText().toString();
        if (TextUtils.isEmpty(messageText))
        {
            Toast.makeText(this, "first write your message...", Toast.LENGTH_SHORT).show();
        }
        else
        {

            DatabaseReference usermessageref=FirebaseDatabase.
                    getInstance().getReference().child("Messages").
                    child(Sender).child(receiver).push();

             messageid=usermessageref.getKey();
            HashMap<String ,Object> hashMap=new HashMap<>();
            hashMap.put("Sender",Sender);
            hashMap.put("Receiver",receiver);
            hashMap.put("Message",Message);
            hashMap.put("time",saveCurrentTime);
            hashMap.put("type","text");
            hashMap.put("date",saveCurrentDate);
            hashMap.put("messageid",messageid);

            //   hashMap.put("image",messageReceiverImage);
            RootRef.child("Messages").push().setValue(hashMap);
        }
    }

    private void displaylastseen() {

        RootRef.child("Users").child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("userstate").hasChild("State")) {
                    String state = dataSnapshot.child("userstate").child("State").getValue().toString();
                    String date = dataSnapshot.child("userstate").child("date").getValue().toString();
                    String time = dataSnapshot.child("userstate").child("Time").getValue().toString();

                    if (state.equals("Online")) {
                        userLastSeen.setText("online");

                    } else if (state.equals("offline")) {
                        userLastSeen.setText("last seen : " + date + "   " + time);

                    }

                }
                else {
                    userLastSeen.setText("offline");

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private void readmessage(final String Sender, final String receiver, final String imageurl){
       messagesList=new ArrayList<>();




     DatabaseReference reference =FirebaseDatabase.getInstance().getReference().child("Messages");


      reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
               messagesList.clear();

                    for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                       Messages messages = dataSnapshot1.getValue(Messages.class);

                        messagesList.add(messages);

                     }
                   messageAdapter1 = new messageAdapter(messagesList, chatActivity.this, imageurl);
                    userMessagesList.setAdapter(messageAdapter1);

            }

         @Override
          public void onCancelled(DatabaseError databaseError) {

         }
     });


 }
}
