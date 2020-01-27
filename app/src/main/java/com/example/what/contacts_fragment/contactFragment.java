package com.example.what.contacts_fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.what.R;
import com.example.what.chat_send_message.chatActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 */
public class contactFragment extends Fragment {

    DatabaseReference reference, user_ref;
    View ContactsView;
    RecyclerView recyclerView_contact;
    FirebaseAuth auth;
    String current_user_id;
    String user_id;
    ArrayList<contacts> list4;

    public contactFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        ContactsView = inflater.inflate(R.layout.fragment_contact2, container, false);


        recyclerView_contact = (RecyclerView) ContactsView.findViewById(R.id.recycler11);
        recyclerView_contact.setLayoutManager(new LinearLayoutManager(this.getActivity()));


        auth = FirebaseAuth.getInstance();
        current_user_id = auth.getCurrentUser().getUid();

        list4=new ArrayList<contacts>();
        reference = FirebaseDatabase.getInstance().getReference().child("Contacts").child(current_user_id);
        user_ref = FirebaseDatabase.getInstance().getReference().child("Users");


        return ContactsView;
    }

    @Override
    public void onStart()
    {
        super.onStart();

        FirebaseRecyclerOptions options =
                new FirebaseRecyclerOptions.Builder<contacts>()
                        .setQuery(reference, contacts.class)
                        .build();


        final FirebaseRecyclerAdapter<contacts, ContactsViewHolder> adapter
                = new FirebaseRecyclerAdapter<contacts, ContactsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final ContactsViewHolder holder, int i, @NonNull contacts contacts) {

                final String userIDs = getRef(i).getKey();

                user_ref.child(userIDs).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot)
                    {
                        if (dataSnapshot.exists())
                        {
                            if (dataSnapshot.child("userstate").hasChild("State")) {
                                String state = dataSnapshot.child("userstate").child("State").getValue().toString();

                                if (state.equals("Online")) {


                                } else if (state.equals("offline")) {
                                    holder.onlineIcon.setVisibility(View.INVISIBLE);
                                }
                            }
                                else
                                    {
                                    holder.onlineIcon.setVisibility(View.INVISIBLE);
                                }





                                if (dataSnapshot.hasChild("image"))
                            {
                                String userImage = dataSnapshot.child("image").getValue().toString();
                                String profileName = dataSnapshot.child("name").getValue().toString();
                                String profileStatus = dataSnapshot.child("Status").getValue().toString();

                                holder.userName.setText(profileName);
                                holder.userStatus.setText(profileStatus);
                                Picasso.get().load(userImage).placeholder(R.drawable.profile_image).into(holder.profileImage);
                                list4.add(dataSnapshot.getValue(contacts.class));
                            }
                            else
                            {
                                String profileName = dataSnapshot.child("name").getValue().toString();
                                String profileStatus = dataSnapshot.child("Status").getValue().toString();

                                holder.userName.setText(profileName);
                                holder.userStatus.setText(profileStatus);
                            }
                            final String profileName = dataSnapshot.child("name").getValue().toString();
                            String profileStatus = dataSnapshot.child("Status").getValue().toString();
                            holder.itemView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent=new Intent(getContext(), chatActivity.class);
                                    if (dataSnapshot.hasChild("image")) {
                                        String userImage = dataSnapshot.child("image").getValue().toString();
                                        intent.putExtra("image",userImage);
                                      intent.putExtra("userid",userIDs);
                                        intent.putExtra("name_mm",profileName);

                                    }

                                    else {
                                       intent.putExtra("userid", userIDs);
                                        intent.putExtra("name_mm", profileName);
                                        intent.putExtra("image","default");

                                    }
                                    startActivity(intent);
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }



            @NonNull
            @Override
            public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
            {
                View layoutView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.findfriend, null, false);
               // RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
               // layoutView.setLayoutParams(lp);
                ContactsViewHolder rcv = new ContactsViewHolder(layoutView);
                return rcv;
            }


        };

        recyclerView_contact.setAdapter(adapter);
        adapter.startListening();
    }




    public  class ContactsViewHolder extends RecyclerView.ViewHolder
    {
        TextView userName, userStatus;
        CircleImageView profileImage;
        ImageView onlineIcon;


        public ContactsViewHolder(@NonNull View itemView)
        {
            super(itemView);

            userName = itemView.findViewById(R.id.profile_name);
            userStatus = itemView.findViewById(R.id.profile_statue);
            profileImage = itemView.findViewById(R.id.rect);
            onlineIcon = itemView.findViewById(R.id.onlineornot);
        }
    }
}


