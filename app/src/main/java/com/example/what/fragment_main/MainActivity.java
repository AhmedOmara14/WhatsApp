package com.example.what.fragment_main;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.example.what.R;
import com.example.what.Setting.Setting;
import com.example.what.find_friend.fi;
import com.example.what.login.loginActivity;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
   private Toolbar toolbar;
   private ViewPager viewPager;
   private TabLayout tabLayout;
   private com.example.what.fragment_main.tabaccess tabaccess;
   private FirebaseAuth auth;
   private DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar=(Toolbar)findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("WhatsApp");
        toolbar.setTitleTextColor(Color.WHITE);


        viewPager=(ViewPager)findViewById(R.id.main_pager);
        tabLayout=(TabLayout)findViewById(R.id.main_tab);

        auth=FirebaseAuth.getInstance();
        reference= FirebaseDatabase.getInstance().getReference();

        tabaccess=new tabaccess(getSupportFragmentManager());
        viewPager.setAdapter(tabaccess);

        tabLayout.setupWithViewPager(viewPager);

        toolbar.setTitleTextColor(Color.WHITE);


    }

    @Override
    protected void onStart() {
        super.onStart();
         FirebaseUser firebaseUser=auth.getCurrentUser();


        if(firebaseUser ==null)
        {
            sendusertologin();
        }

        else {
             checkonlineornot("Online");
            String currentid=auth.getCurrentUser().getUid();
            reference.child("Users").child(currentid).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if(dataSnapshot.child("name").exists()){
                        Toast.makeText(MainActivity.this, "welcome", Toast.LENGTH_SHORT).show();

                    }
                    else {
                        sendusertosetting();
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseUser firebaseUser=auth.getCurrentUser();

        if(firebaseUser !=null) {
            checkonlineornot("offline");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FirebaseUser firebaseUser=auth.getCurrentUser();

        if(firebaseUser !=null) {
            checkonlineornot("offline");
        }

    }

    private void checkonlineornot(String State)
    {

        String Savecurrenttime,SaveDate;
        Calendar calendar=Calendar.getInstance();

        SimpleDateFormat dateFormat=new SimpleDateFormat("MMM,dd - yyyy");
        SaveDate=dateFormat.format(calendar.getTime());

        SimpleDateFormat dateFormat1=new SimpleDateFormat("hh:mm a");
        Savecurrenttime=dateFormat1.format(calendar.getTime());

        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("date",SaveDate);
        hashMap.put("Time",Savecurrenttime);
        hashMap.put("State",State);

        String Current_userid=auth.getCurrentUser().getUid();
        reference.child("Users").child(Current_userid).child("userstate").updateChildren(hashMap);

    }

    private void sendusertologin() {
        Intent intent=new Intent(MainActivity.this, loginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void sendusertosetting() {
        Intent intent=new Intent(MainActivity.this, Setting.class);
        startActivity(intent);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.option_menu,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
         super.onOptionsItemSelected(item);

         if(item.getItemId()==R.id.main_logout)
         {

             checkonlineornot("offline");

             auth.signOut();
             sendusertologin();
         }
        if(item.getItemId()==R.id.main_setting)
        {
            sendusertosetting();
        }
        if(item.getItemId()==R.id.finh)
        {
            Intent intent=new Intent(MainActivity.this, fi.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }


        return true;
    }

    private void Requestnewgroup() {
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this,R.style.AlertDialog);
        builder.setTitle("Enter Group Name");
        final EditText editText=new EditText(MainActivity.this);
        editText.setHint("e.g FCIH GROUP A");
        builder.setView(editText);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                 String txt_group_name=editText.getText().toString();
                 String currentid=auth.getCurrentUser().getUid();

                 if(TextUtils.isEmpty(txt_group_name)){
                     Toast.makeText(MainActivity.this, "Enter Group name", Toast.LENGTH_SHORT).show();
                 }
                 else {
                     reference.child("GroupName").child(txt_group_name).setValue("");
                     Toast.makeText(MainActivity.this, txt_group_name+" is created", Toast.LENGTH_SHORT).show();
                 }

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String txt_group_name=editText.getText().toString();
                dialogInterface.cancel();
            }
        });
        builder.show();
    }
}
