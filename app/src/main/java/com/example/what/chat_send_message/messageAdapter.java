package com.example.what.chat_send_message;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.what.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class messageAdapter extends RecyclerView.Adapter<messageAdapter.message_sender_recerive> {

    private List<Messages> list;
    DatabaseReference Reference;
    FirebaseAuth auth;
    private Context context;
    private String imageurl = "default" ;
    String Usersid;
    private int message_chat_sender=1;
    private int message_chat_receiver=0;

    FirebaseUser fuser;



    public messageAdapter(List<Messages> list, Context context, String imageurl) {
        this.list = list;
        this.context = context;
        this.imageurl = imageurl;
    }



    @NonNull
    @Override
    public message_sender_recerive onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == message_chat_receiver){
           // View layoutView1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.cusston_recive_message, null, false);
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cusston_recive_message, null, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
            messageAdapter.message_sender_recerive rcv = new messageAdapter.message_sender_recerive(layoutView);
            return rcv;
        }
        else{
            View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.sender, null, false);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutView.setLayoutParams(lp);
            messageAdapter.message_sender_recerive rcv = new messageAdapter.message_sender_recerive(layoutView);
            return rcv;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull final message_sender_recerive holder, int position) {
        final Messages messages = list.get(position);

        auth = FirebaseAuth.getInstance();
        String current = auth.getCurrentUser().getUid();
        String type = messages.getType();

         if(type.equals("text")) {
             holder.textView_receriver.setText(messages.getMessage() + "\n \n" + messages.getTime() + " - " + messages.getDate());
         }
         else if (type.equals("image"))
         {
             holder.textView_receriver.setVisibility(View.INVISIBLE);

             Picasso.get().load(messages.getMessage()).into(holder.image_receiver);

         }

        if (imageurl.equals("default")) {
            holder.circleImageView.setImageResource(R.drawable.profile_image);
        } else {
            Picasso.get().load(imageurl).placeholder(R.drawable.profile_image).into(holder.circleImageView);
        }
    }





    @Override
    public int getItemCount() {
        return list.size();
    }


    public static class message_sender_recerive extends RecyclerView.ViewHolder{
        TextView textView_sender,textView_receriver;
        CircleImageView circleImageView;
        ImageView image_sender,image_receiver;
        ImageView btn_send_file;

        public message_sender_recerive(@NonNull View itemView) {
            super(itemView);

            textView_receriver=(TextView)itemView.findViewById(R.id.receive_chat);
            circleImageView=(CircleImageView) itemView.findViewById(R.id.image_chat);
            image_receiver=(ImageView) itemView.findViewById(R.id.message_receiver_image_view);
            btn_send_file=(ImageView)itemView.findViewById(R.id.btn_send_file);

        }
    }

    @Override
    public int getItemViewType(int position) {

        String hh = FirebaseAuth.getInstance().getCurrentUser().getUid();
        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser().getUid().equals(list.get(position).getSender())) {
            return message_chat_sender;
        } else
        {

            return message_chat_receiver;
        }

    }
}
