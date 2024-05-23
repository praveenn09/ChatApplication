package com.example.prime.activities;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Application;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prime.R;
import com.example.prime.messagesAdapterr;
import com.example.prime.msgModelclass;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatWindow extends AppCompatActivity {
    String recieverusername="";
    String recieverprofileImG="";
    String senderUID="",receiverId="";
    CircleImageView profilePic;
    TextView recieverName;
    FirebaseAuth firebaseAuth;
    ImageView goback,chatcall;
    CardView sendmessageButton;
    FirebaseDatabase database;
    EditText sendMessage;
    RecyclerView mmessagesAdapter;

    public  static  String senderImg;
    public static String receiverIMg;
    String senderRoom,receiverRoom;
    ArrayList<msgModelclass> messagesArrayList;
    messagesAdapterr messagesAdapterr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_window);
        chatcall=findViewById(R.id.ChatCall);
        recieverusername=getIntent().getStringExtra("namE");
        recieverprofileImG=getIntent().getStringExtra("profilePic");
        receiverId=getIntent().getStringExtra("receiverRID");

        messagesArrayList =new ArrayList<>();

        profilePic=findViewById(R.id.chatUserprofile);
        recieverName=findViewById(R.id.ChatUserName);
        mmessagesAdapter=findViewById(R.id.msgAdaptshowChat);

        Picasso.get().load(recieverprofileImG).into(profilePic);
        recieverName.setText(""+recieverusername);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        mmessagesAdapter.setLayoutManager(linearLayoutManager);
        messagesAdapterr =new messagesAdapterr(ChatWindow.this,messagesArrayList);
        mmessagesAdapter.setAdapter(messagesAdapterr);

        database=FirebaseDatabase.getInstance();

        // retrieve user id
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();

        if (currentUser != null) {
            senderUID= currentUser.getUid();

        } else {

        }

        senderRoom=senderUID+receiverId;
        receiverRoom=receiverId+senderUID;

        DatabaseReference reference=database.getReference().child("Users").child(firebaseAuth.getUid());
        DatabaseReference chatreference=database.getReference("chats").child(senderRoom).child("message");

        Toast.makeText(ChatWindow.this,""+senderUID,Toast.LENGTH_SHORT).show();

        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    msgModelclass messages=dataSnapshot.getValue(msgModelclass.class);
                    messagesArrayList.add(messages);


                }
                messagesAdapterr.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg=snapshot.child("profileImage").getValue().toString();
                receiverIMg=recieverprofileImG;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        sendmessageButton=findViewById(R.id.sendMessageButton);
        sendMessage=findViewById(R.id.writemsgtext);
        goback=findViewById(R.id.returntofirstactivity);





        sendmessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message=sendMessage.getText().toString();
                if (message.isEmpty()){
                    Toast.makeText(ChatWindow.this,"Enter the message first",Toast.LENGTH_SHORT).show();
                }
                sendMessage.setText("");
                Date date=new Date();
                msgModelclass messages=new msgModelclass(message,senderUID,date.getTime());
                database=FirebaseDatabase.getInstance();
                database.getReference().child("chats").child(senderRoom).child("message").push()
                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                database.getReference().child("chats").child(receiverRoom).child("message").push()
                                        .setValue(messages).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                            }
                                        });//second
                            }
                        });//first

            }
        });



        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ChatWindow.this, firstUsersPage.class);
                startActivity(intent);
                finish();
            }
        });
        chatcall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent=new Intent(ChatWindow.this,callingAct.class);
                startActivity(intent);



            }
        });


    }




}
