package com.example.prime.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;


public class ChatWindow extends AppCompatActivity {

    // =========================================================
    // USER INFORMATION
    // =========================================================

    String receiverUsername = "";
    String receiverProfileImg = "";

    String senderUID = "";
    String receiverId = "";


    // =========================================================
    // VIEWS
    // =========================================================

    CircleImageView profilePic;

    TextView receiverName;
    TextView chatUserStatus;
    TextView chatAnalytics;

    ImageView goback;
    ImageView chatcall;

    CardView sendmessageButton;

    EditText sendMessage;

    RecyclerView messagesRecyclerView;


    // =========================================================
    // FIREBASE
    // =========================================================

    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;


    // =========================================================
    // CHAT ROOMS
    // =========================================================

    String senderRoom;
    String receiverRoom;


    // =========================================================
    // MESSAGES
    // =========================================================

    ArrayList<msgModelclass> messagesArrayList;

    messagesAdapterr messagesAdapter;


    public static String senderImg;
    public static String receiverIMg;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_window);


        // =====================================================
        // FIREBASE
        // =====================================================

        firebaseAuth =
                FirebaseAuth.getInstance();

        database =
                FirebaseDatabase.getInstance();


        FirebaseUser currentUser =
                firebaseAuth.getCurrentUser();


        if (currentUser == null) {

            finish();

            return;
        }


        senderUID =
                currentUser.getUid();


        // =====================================================
        // GET RECEIVER INFORMATION
        // =====================================================

        receiverUsername =
                getIntent()
                        .getStringExtra("namE");


        receiverProfileImg =
                getIntent()
                        .getStringExtra("profilePic");


        receiverId =
                getIntent()
                        .getStringExtra("receiverRID");


        if (receiverId == null ||
                receiverId.isEmpty()) {

            Toast.makeText(
                    ChatWindow.this,
                    "User information not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // =====================================================
        // FIND VIEWS
        // =====================================================

        chatcall =
                findViewById(
                        R.id.ChatCall
                );


        profilePic =
                findViewById(
                        R.id.chatUserprofile
                );


        receiverName =
                findViewById(
                        R.id.ChatUserName
                );


        chatUserStatus =
                findViewById(
                        R.id.chatUserStatus
                );


        chatAnalytics =
                findViewById(
                        R.id.chatAnalytics
                );


        messagesRecyclerView =
                findViewById(
                        R.id.msgAdaptshowChat
                );


        sendmessageButton =
                findViewById(
                        R.id.sendMessageButton
                );


        sendMessage =
                findViewById(
                        R.id.writemsgtext
                );


        goback =
                findViewById(
                        R.id.returntofirstactivity
                );


        // =====================================================
        // HEADER
        // =====================================================

        receiverName.setText(
                receiverUsername
        );


        profilePic.setImageResource(
                R.drawable.ic_dummy_user
        );


        // =====================================================
        // CHAT ROOMS
        // =====================================================

        senderRoom =
                senderUID + receiverId;


        receiverRoom =
                receiverId + senderUID;


        // =====================================================
        // ONLINE STATUS
        // =====================================================

        listenToUserStatus(
                receiverId
        );


        // =====================================================
        // MARK CHAT AS READ
        // =====================================================

        markChatAsRead();


        // =====================================================
        // RECYCLER VIEW
        // =====================================================

        messagesArrayList =
                new ArrayList<>();


        LinearLayoutManager layoutManager =
                new LinearLayoutManager(
                        this
                );


        layoutManager.setStackFromEnd(
                true
        );


        messagesRecyclerView.setLayoutManager(
                layoutManager
        );


        messagesAdapter =
                new messagesAdapterr(
                        ChatWindow.this,
                        messagesArrayList
                );


        messagesRecyclerView.setAdapter(
                messagesAdapter
        );


        // =====================================================
        // READ MESSAGES
        // =====================================================

        DatabaseReference chatReference =
                database
                        .getReference()
                        .child("chats")
                        .child(senderRoom)
                        .child("message");


        chatReference.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {

                        messagesArrayList.clear();


                        for (DataSnapshot dataSnapshot :
                                snapshot.getChildren()) {

                            msgModelclass message =
                                    dataSnapshot.getValue(
                                            msgModelclass.class
                                    );


                            if (message != null) {

                                messagesArrayList.add(
                                        message
                                );
                            }
                        }


                        messagesAdapter.notifyDataSetChanged();


                        if (messagesArrayList.size() > 0) {

                            messagesRecyclerView
                                    .scrollToPosition(
                                            messagesArrayList.size() - 1
                                    );
                        }
                    }


                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                    }
                }
        );


        // =====================================================
        // CHAT ANALYTICS
        // =====================================================

        chatAnalytics.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Intent intent =
                                new Intent(
                                        ChatWindow.this,
                                        ChatAnalyticsActivity.class
                                );


                        intent.putExtra(
                                "receiverRID",
                                receiverId
                        );


                        intent.putExtra(
                                "namE",
                                receiverUsername
                        );


                        startActivity(intent);
                    }
                }
        );


        // =====================================================
        // CALL BUTTON
        // =====================================================

        chatcall.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        /*
                         * We already know who the receiver is.
                         *
                         * receiverId = Firebase UID of
                         * the person we are chatting with.
                         *
                         * So we pass it to callingAct.
                         */

                        Intent intent =
                                new Intent(
                                        ChatWindow.this,
                                        callingAct.class
                                );


                        intent.putExtra(
                                "receiverId",
                                receiverId
                        );


                        intent.putExtra(
                                "receiverName",
                                receiverUsername
                        );


                        startActivity(intent);
                    }
                }
        );


        // =====================================================
        // SEND MESSAGE
        // =====================================================

        sendmessageButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {


                        String messageText =
                                sendMessage
                                        .getText()
                                        .toString()
                                        .trim();


                        if (messageText.isEmpty()) {

                            Toast.makeText(
                                    ChatWindow.this,
                                    "Enter the message first",
                                    Toast.LENGTH_SHORT
                            ).show();

                            return;
                        }


                        sendMessage.setText("");


                        Date date =
                                new Date();


                        msgModelclass message =
                                new msgModelclass(
                                        messageText,
                                        senderUID,
                                        date.getTime()
                                );


                        // =====================================
                        // SENDER ROOM
                        // =====================================

                        database
                                .getReference()
                                .child("chats")
                                .child(senderRoom)
                                .child("message")
                                .push()
                                .setValue(message)
                                .addOnCompleteListener(
                                        new OnCompleteListener<Void>() {

                                            @Override
                                            public void onComplete(
                                                    @NonNull Task<Void> task) {

                                                if (!task.isSuccessful()) {

                                                    Toast.makeText(
                                                            ChatWindow.this,
                                                            "Message failed",
                                                            Toast.LENGTH_SHORT
                                                    ).show();

                                                    return;
                                                }


                                                // ==============================
                                                // RECEIVER ROOM
                                                // ==============================

                                                database
                                                        .getReference()
                                                        .child("chats")
                                                        .child(receiverRoom)
                                                        .child("message")
                                                        .push()
                                                        .setValue(message)
                                                        .addOnCompleteListener(
                                                                new OnCompleteListener<Void>() {

                                                                    @Override
                                                                    public void onComplete(
                                                                            @NonNull Task<Void> task) {

                                                                        if (!task.isSuccessful()) {

                                                                            Toast.makeText(
                                                                                    ChatWindow.this,
                                                                                    "Message delivery failed",
                                                                                    Toast.LENGTH_SHORT
                                                                            ).show();
                                                                        }
                                                                    }
                                                                }
                                                        );
                                            }
                                        }
                                );
                    }
                }
        );


        // =====================================================
        // BACK BUTTON
        // =====================================================

        goback.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        finish();
                    }
                }
        );
    }


    // =========================================================
    // ONLINE / OFFLINE STATUS
    // =========================================================

    private void listenToUserStatus(
            String receiverId) {


        DatabaseReference statusReference =
                database
                        .getReference("Users")
                        .child(receiverId);


        statusReference.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {


                        Boolean online =
                                snapshot
                                        .child("online")
                                        .getValue(
                                                Boolean.class
                                        );


                        if (Boolean.TRUE.equals(
                                online
                        )) {

                            chatUserStatus.setText(
                                    "● Online"
                            );


                            chatUserStatus.setTextColor(
                                    Color.GREEN
                            );

                        } else {

                            chatUserStatus.setText(
                                    "Offline"
                            );


                            chatUserStatus.setTextColor(
                                    Color.LTGRAY
                            );
                        }
                    }


                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                    }
                }
        );
    }


    // =========================================================
    // MARK CHAT AS READ
    // =========================================================

    private void markChatAsRead() {


        if (receiverId == null ||
                receiverId.isEmpty()) {

            return;
        }


        FirebaseUser currentUser =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser();


        if (currentUser == null) {

            return;
        }


        String currentUid =
                currentUser.getUid();


        String room =
                currentUid + receiverId;


        FirebaseDatabase
                .getInstance()
                .getReference()
                .child("chats")
                .child(room)
                .child("message")
                .get()
                .addOnCompleteListener(
                        task -> {


                            long latestTimestamp = 0;


                            if (task.isSuccessful() &&
                                    task.getResult() != null) {


                                for (DataSnapshot snapshot :
                                        task.getResult()
                                                .getChildren()) {


                                    Long timestamp =
                                            snapshot
                                                    .child("timeStamp")
                                                    .getValue(
                                                            Long.class
                                                    );


                                    if (timestamp != null &&
                                            timestamp >
                                                    latestTimestamp) {

                                        latestTimestamp =
                                                timestamp;
                                    }
                                }
                            }


                            getSharedPreferences(
                                    "chat_read_status",
                                    MODE_PRIVATE
                            )
                                    .edit()
                                    .putLong(
                                            "read_" + receiverId,
                                            latestTimestamp
                                    )
                                    .apply();
                        }
                );
    }
}