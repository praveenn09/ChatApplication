package com.example.prime.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prime.ChatRequest;
import com.example.prime.R;
import com.example.prime.UserSelectAdapter;
import com.example.prime.Users;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class firstUsersPage extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;

    private RecyclerView recyclerView;

    private UserSelectAdapter adapter;

    private ArrayList<Users> usersArrayList;

    private RoundedImageView roundedImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_first_users_page
        );


        // =====================================================
        // FIREBASE
        // =====================================================

        auth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();


        FirebaseUser currentUser =
                auth.getCurrentUser();


        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // =====================================================
        // ARRAY
        // =====================================================

        usersArrayList =
                new ArrayList<>();


        // =====================================================
        // RECYCLER VIEW
        // =====================================================

        recyclerView =
                findViewById(
                        R.id.usersRecyclerview
                );


        if (recyclerView == null) {

            Toast.makeText(
                    this,
                    "Users RecyclerView not found",
                    Toast.LENGTH_LONG
            ).show();

            finish();

            return;
        }


        recyclerView.setLayoutManager(
                new LinearLayoutManager(this)
        );


        adapter =
                new UserSelectAdapter(
                        this,
                        usersArrayList
                );


        recyclerView.setAdapter(adapter);


        // =====================================================
        // BACK
        // =====================================================

        roundedImageView =
                findViewById(
                        R.id.goback
                );


        if (roundedImageView != null) {

            roundedImageView.setOnClickListener(
                    new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }
            );
        }


        // =====================================================
        // LOAD USERS
        // =====================================================

        loadUsers();
    }


    // =========================================================
    // LOAD USERS
    // =========================================================

    private void loadUsers() {

        DatabaseReference usersReference =
                database
                        .getReference()
                        .child("Users");


        usersReference.addValueEventListener(
                new com.google.firebase.database.ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {


                        usersArrayList.clear();


                        FirebaseUser currentUser =
                                auth.getCurrentUser();


                        if (currentUser == null) {
                            return;
                        }


                        String currentUserId =
                                currentUser.getUid();


                        for (DataSnapshot userSnapshot :
                                snapshot.getChildren()) {


                            Users user =
                                    userSnapshot.getValue(
                                            Users.class
                                    );


                            if (user == null) {
                                continue;
                            }


                            // =================================================
                            // GET USER ID
                            // =================================================

                            String userId =
                                    user.getUserId();


                            /*
                             * If userId isn't inside
                             * the object, Firebase key is UID.
                             */

                            if (userId == null ||
                                    userId.trim().isEmpty() ||
                                    userId.equals("null")) {

                                userId =
                                        userSnapshot.getKey();
                            }


                            if (userId == null ||
                                    userId.trim().isEmpty()) {

                                continue;
                            }


                            user.setUserId(userId);


                            // =================================================
                            // DON'T SHOW CURRENT USER
                            // =================================================

                            if (currentUserId.equals(userId)) {
                                continue;
                            }


                            usersArrayList.add(user);
                        }


                        adapter.notifyDataSetChanged();
                    }


                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Toast.makeText(
                                firstUsersPage.this,
                                "Unable to load users: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


    // =========================================================
    // CHECK CHAT STATUS
    // =========================================================

    public void checkChatStatus(
            Users user) {


        if (user == null) {

            Toast.makeText(
                    this,
                    "Invalid user",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        FirebaseUser currentUser =
                auth.getCurrentUser();


        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "Please login first",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String currentUserId =
                currentUser.getUid();


        String receiverId =
                user.getUserId();


        if (receiverId == null ||
                receiverId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "User ID not found",
                    Toast.LENGTH_LONG
            ).show();

            return;
        }


        if (currentUserId.equals(receiverId)) {

            Toast.makeText(
                    this,
                    "You cannot chat with yourself",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        // =====================================================
        // CHECK CONTACT
        // =====================================================

        DatabaseReference contactReference =
                database
                        .getReference()
                        .child("contacts")
                        .child(currentUserId)
                        .child(receiverId);


        contactReference.get()
                .addOnCompleteListener(
                        task -> {

                            if (!task.isSuccessful()) {

                                Toast.makeText(
                                        firstUsersPage.this,
                                        "Unable to check connection",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }


                            if (task.getResult() != null &&
                                    task.getResult().exists()) {

                                openChat(user);

                                return;
                            }


                            checkRequestStatus(
                                    user,
                                    currentUserId,
                                    receiverId
                            );
                        }
                );
    }


    // =========================================================
    // CHECK BOTH DIRECTIONS
    // =========================================================

    private void checkRequestStatus(
            Users user,
            String currentUserId,
            String receiverId) {


        /*
         *
         * Direction 1:
         *
         * chatRequests
         *      receiverId
         *          currentUserId
         *
         * Means CURRENT USER sent request.
         *
         */


        DatabaseReference outgoing =
                database
                        .getReference()
                        .child("chatRequests")
                        .child(receiverId)
                        .child(currentUserId);


        outgoing.get()
                .addOnCompleteListener(
                        outgoingTask -> {


                            if (!outgoingTask.isSuccessful()) {

                                Toast.makeText(
                                        firstUsersPage.this,
                                        "Unable to check request",
                                        Toast.LENGTH_SHORT
                                ).show();

                                return;
                            }


                            DataSnapshot outgoingSnapshot =
                                    outgoingTask.getResult();


                            if (outgoingSnapshot != null &&
                                    outgoingSnapshot.exists()) {


                                String status =
                                        outgoingSnapshot
                                                .child("status")
                                                .getValue(
                                                        String.class
                                                );


                                if ("accepted".equals(status)) {

                                    createContacts(
                                            currentUserId,
                                            receiverId,
                                            user
                                    );

                                    return;
                                }


                                if ("pending".equals(status)) {

                                    Toast.makeText(
                                            firstUsersPage.this,
                                            "Request already sent",
                                            Toast.LENGTH_SHORT
                                    ).show();

                                    return;
                                }


                                // declined / unknown

                                showSendRequestDialog(user);

                                return;
                            }


                            // =================================================
                            // CHECK INCOMING
                            // =================================================

                            DatabaseReference incoming =
                                    database
                                            .getReference()
                                            .child("chatRequests")
                                            .child(currentUserId)
                                            .child(receiverId);


                            incoming.get()
                                    .addOnCompleteListener(
                                            incomingTask -> {


                                                if (!incomingTask.isSuccessful()) {

                                                    Toast.makeText(
                                                            firstUsersPage.this,
                                                            "Unable to check incoming request",
                                                            Toast.LENGTH_SHORT
                                                    ).show();

                                                    return;
                                                }


                                                DataSnapshot incomingSnapshot =
                                                        incomingTask.getResult();


                                                if (incomingSnapshot == null ||
                                                        !incomingSnapshot.exists()) {

                                                    showSendRequestDialog(user);

                                                    return;
                                                }


                                                String status =
                                                        incomingSnapshot
                                                                .child("status")
                                                                .getValue(
                                                                        String.class
                                                                );


                                                if ("accepted".equals(status)) {

                                                    createContacts(
                                                            currentUserId,
                                                            receiverId,
                                                            user
                                                    );

                                                    return;
                                                }


                                                if ("pending".equals(status)) {

                                                    Toast.makeText(
                                                            firstUsersPage.this,
                                                            "This user has already sent you a request. Accept it from Chat Requests.",
                                                            Toast.LENGTH_LONG
                                                    ).show();

                                                    return;
                                                }


                                                showSendRequestDialog(user);
                                            }
                                    );
                        }
                );
    }


    // =========================================================
    // SEND REQUEST DIALOG
    // =========================================================

    private void showSendRequestDialog(
            Users user) {


        String username =
                user.getUsername();


        if (username == null ||
                username.trim().isEmpty()) {

            username = "this user";
        }


        new AlertDialog.Builder(this)

                .setTitle("Send Chat Request")

                .setMessage(
                        "Send a chat request to "
                                + username
                                + "?"
                )

                .setPositiveButton(
                        "Send Request",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {

                                sendChatRequest(user);
                            }
                        }
                )

                .setNegativeButton(
                        "Cancel",
                        null
                )

                .show();
    }


    // =========================================================
    // SEND REQUEST
    // =========================================================

    private void sendChatRequest(
            Users user) {


        FirebaseUser currentUser =
                auth.getCurrentUser();


        if (currentUser == null) {
            return;
        }


        String senderId =
                currentUser.getUid();


        String receiverId =
                user.getUserId();


        if (receiverId == null ||
                receiverId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Receiver ID not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        if (senderId.equals(receiverId)) {

            Toast.makeText(
                    this,
                    "You cannot send a request to yourself",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        String senderName =
                nameStore.myname;


        if (senderName == null ||
                senderName.trim().isEmpty()) {

            senderName = senderId;
        }


        String receiverName =
                user.getUsername();


        if (receiverName == null ||
                receiverName.trim().isEmpty()) {

            receiverName = receiverId;
        }


        ChatRequest request =
                new ChatRequest(
                        senderId,
                        receiverId,
                        senderName,
                        receiverName,
                        "pending",
                        System.currentTimeMillis()
                );


        database
                .getReference()
                .child("chatRequests")
                .child(receiverId)
                .child(senderId)
                .setValue(request)
                .addOnCompleteListener(
                        task -> {

                            if (task.isSuccessful()) {

                                Toast.makeText(
                                        firstUsersPage.this,
                                        "Chat request sent",
                                        Toast.LENGTH_SHORT
                                ).show();

                            } else {

                                Toast.makeText(
                                        firstUsersPage.this,
                                        "Failed to send request",
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                );
    }


    // =========================================================
    // CREATE CONTACTS
    // =========================================================

    private void createContacts(
            String currentUserId,
            String otherUserId,
            Users user) {


        Map<String, Object> updates =
                new HashMap<>();


        updates.put(
                "contacts/"
                        + currentUserId
                        + "/"
                        + otherUserId,
                true
        );


        updates.put(
                "contacts/"
                        + otherUserId
                        + "/"
                        + currentUserId,
                true
        );


        database
                .getReference()
                .updateChildren(updates)
                .addOnCompleteListener(
                        task -> {

                            if (task.isSuccessful()) {

                                openChat(user);

                            } else {

                                Toast.makeText(
                                        firstUsersPage.this,
                                        "Unable to create chat connection",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }


    // =========================================================
    // OPEN CHAT
    // =========================================================

    private void openChat(
            Users user) {


        String receiverId =
                user.getUserId();


        if (receiverId == null ||
                receiverId.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "User information not found",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }


        Intent intent =
                new Intent(
                        firstUsersPage.this,
                        ChatWindow.class
                );


        intent.putExtra(
                "namE",
                user.getUsername()
        );


        intent.putExtra(
                "profilePic",
                user.getProfileImage()
        );


        intent.putExtra(
                "receiverRID",
                receiverId
        );


        startActivity(intent);
    }
}