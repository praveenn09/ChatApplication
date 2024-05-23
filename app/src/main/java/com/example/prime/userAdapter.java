package com.example.prime;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prime.activities.ChatWindow;
import com.example.prime.activities.firstUsersPage;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class userAdapter extends RecyclerView.Adapter<userAdapter.viewholder> {
    firstUsersPage firstUsersPage;
    ArrayList<Users> usersArrayList;
    public userAdapter(firstUsersPage firstUsersPage, ArrayList<Users> usersArrayList) {
                this.firstUsersPage=firstUsersPage;
                this.usersArrayList=usersArrayList;


    }

    @NonNull
    @Override
    public userAdapter.viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
      View view= LayoutInflater.from(firstUsersPage).inflate(R.layout.user_item,parent,false);
        return new viewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull userAdapter.viewholder holder, int position) {
        Users users=usersArrayList.get(position);
        holder.username.setText(users.username);
      Picasso.get().load(users.profileImage).into(holder.imageprofile);
   //    Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chatting-application-dcdf5.appspot.com/o/profile.png?alt=media&token=f436be3c-d8c0-473e-acd6-04ab23b40281").into(holder.imageprofile);
 holder.itemView.setOnClickListener(new View.OnClickListener() {
     @Override
     public void onClick(View view) {
         Intent intent=new Intent(firstUsersPage, ChatWindow.class);
         intent.putExtra("namE",users.getUsername());
         intent.putExtra("profilePic",users.getProfileImage());
         intent.putExtra("receiverRID",users.getUserId());
         firstUsersPage.startActivity(intent);

     }
 });




    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class viewholder extends RecyclerView.ViewHolder {
        CircleImageView imageprofile;
        TextView username;
        public viewholder(@NonNull View itemView) {
            super(itemView);
            imageprofile=itemView.findViewById(R.id.imageprofilee);
            username=itemView.findViewById(R.id.username);
        }
    }
}
