package com.example.what.find_friend;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.what.R;
import com.example.what.contacts_fragment.contacts;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
public class myAdapter extends RecyclerView.Adapter<myAdapter.myviewholder1> {

    private  Context context;
    private ArrayList<contacts> profiles;
    DatabaseReference reference;
    FirebaseAuth auth;

    public myAdapter(Context c, ArrayList<contacts> p) {
        this.context = c;
        this.profiles = p;
    }

    @NonNull
    @Override
    public myviewholder1 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        reference= FirebaseDatabase.getInstance().getReference();
        auth=FirebaseAuth.getInstance();
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.findfriend, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        myviewholder1 rcv = new myviewholder1(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull final myviewholder1 holder, final int position) {



             holder.txt_name.setText(profiles.get(position).getName());
             holder.txt_status.setText(profiles.get(position).getStatus());

        Picasso.get().load(profiles.get(position).getImage()).placeholder(R.drawable.profile_image).into(holder.circleImageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             String visit_user_id= profiles.get(position).getId();
                String visit_user_name= profiles.get(position).getName();
                String visit_user_descrition= profiles.get(position).getStatus();
                String image=profiles.get(position).getImage();
                Intent profileIntent = new Intent(context, ProfileActivity.class);
                profileIntent.putExtra("visit_user_id", visit_user_id);
                profileIntent.putExtra("visit_user_name", visit_user_name);
                profileIntent.putExtra("visit_user_description", visit_user_descrition);
                profileIntent.putExtra("visit_user_image", image);

                context.startActivity(profileIntent);
                   }


        });


    }
    @Override
    public int getItemCount() {
        return profiles.size();
    }

    static class myviewholder1 extends RecyclerView.ViewHolder
    {
       TextView txt_name,txt_status;
       CircleImageView circleImageView;
       ImageView onlineicon;

        public myviewholder1(@NonNull View itemView) {
            super(itemView);
            circleImageView=(CircleImageView)itemView.findViewById(R.id.rect);
            txt_name=(TextView) itemView.findViewById(R.id.profile_name);
            txt_status=(TextView)itemView.findViewById(R.id.profile_statue);
            onlineicon=(ImageView)itemView.findViewById(R.id.onlineornot);
            onlineicon.setVisibility(View.INVISIBLE);
        }
    }


}
