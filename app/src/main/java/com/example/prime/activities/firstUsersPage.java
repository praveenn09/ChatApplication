package com.example.prime.activities;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.prime.R;
import com.example.prime.Users;
import com.example.prime.userAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

public class firstUsersPage extends AppCompatActivity {
   FirebaseAuth auth;
   RecyclerView recyclerView;
  userAdapter adapter;
  RoundedImageView roundedImageView;
   FirebaseDatabase database;
  ArrayList<Users> usersArrayList;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_users_page);
        database=FirebaseDatabase.getInstance();
        auth=FirebaseAuth.getInstance();
        usersArrayList=new ArrayList<>();

       DatabaseReference reference=database.getReference().child("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               for (DataSnapshot dataSnapshot:snapshot.getChildren())
                {
                    Users users=dataSnapshot.getValue(Users.class);
                    usersArrayList.add(users);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        recyclerView=findViewById(R.id.usersRecyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new userAdapter(firstUsersPage.this,usersArrayList);
        recyclerView.setAdapter(adapter);
        // Read from the database
        roundedImageView=findViewById(R.id.goback);
        roundedImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(firstUsersPage.this,useradd.class);
                startActivity(intent);
                finish();
            }
        });
    }
}