package com.example.prime.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prime.R;
import com.example.prime.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class useradd extends AppCompatActivity {
   CircleImageView circleImageView;
   RoundedImageView roundedImageView;
   FirebaseDatabase database;
   DatabaseReference reference;
   String imageuri="",name="";
   String id="";
   ImageView logout;
    FirebaseAuth firebaseAuth;
   TextView setName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useradd);
        circleImageView=findViewById(R.id.userprofile);

        roundedImageView=findViewById(R.id.addbutton);
        logout=findViewById(R.id.logoutbutton);
        database=FirebaseDatabase.getInstance();
        setName=findViewById(R.id.setUserName);
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            id= currentUser.getUid();
            //Toast.makeText(useradd.this,"user id "+id,Toast.LENGTH_SHORT).show();
        } else {

        }
          reference=database.getReference("Users");
          reference.child(id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (task.isSuccessful()){
                    DataSnapshot dataSnapshot=task.getResult();
                    imageuri=String.valueOf(dataSnapshot.child("profileImage").getValue());
                    name=String.valueOf(dataSnapshot.child("username").getValue());

                 setName.setText(name);
                    Picasso.get().load(imageuri).into(circleImageView);

                }
                else {

                }
            }
        });

       roundedImageView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent i=new Intent(useradd.this,firstUsersPage.class);
               startActivity(i);
               finish();
           }
       });
       logout.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Dialog dialog=new Dialog(useradd.this,R.style.dialog);
               dialog.setContentView(R.layout.dialog_layout);
               Button no,yes;
               yes=dialog.findViewById(R.id.YesButton);
               no=dialog.findViewById(R.id.NoButton);
               yes.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       FirebaseAuth.getInstance().signOut();
                       nameStore.myname="";
                       Intent intent=new Intent(useradd.this, SignIn.class);
                       startActivity(intent);
                       finish();
                   }
               });
               no.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View view) {
                       dialog.dismiss();
                   }
               });
               dialog.show();
           }
       });


    }
}