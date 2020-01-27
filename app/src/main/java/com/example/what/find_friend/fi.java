package com.example.what.find_friend;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.what.R;
import com.example.what.contacts_fragment.contacts;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class fi extends AppCompatActivity {


    DatabaseReference reference;
    View view;
    RecyclerView recyclerView1;
    ArrayList<contacts> list;
    myAdapter adapter;
    private Toolbar mToolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fi);



          mToolbar =  findViewById(R.id.main_toolbar);
          setSupportActionBar(mToolbar);
       getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setDisplayShowHomeEnabled(true);
      getSupportActionBar().setTitle("Find Friends");

        reference= FirebaseDatabase.getInstance().getReference().child("Users");
        list=new ArrayList<contacts>();
        recyclerView1 =(RecyclerView)findViewById(R.id.recy);
        adapter=new myAdapter(fi.this,list);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));




       recyclerView1.setLayoutManager(new LinearLayoutManager(fi.this));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                   for (DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                  contacts p=dataSnapshot1.getValue(contacts.class);
                  list.add(p);

                 }
                    adapter=new myAdapter(fi.this,list);

                    recyclerView1.setAdapter(adapter);

                }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(fi.this, "Wrong", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
