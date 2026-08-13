package com.example.prime.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.prime.ChatListModel;
import com.example.prime.ChatRequest;
import com.example.prime.R;
import com.example.prime.Users;
import com.example.prime.userAdapter;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.makeramen.roundedimageview.RoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class useradd extends AppCompatActivity {


    // =========================================================
    // TAG
    // =========================================================

    private static final String TAG = "CHAT_REQUEST";


    // =========================================================
    // FIREBASE
    // =========================================================

    private FirebaseAuth firebaseAuth;

    private FirebaseDatabase database;


    // =========================================================
    // HEADER
    // =========================================================

    private CircleImageView circleImageView;

    private RoundedImageView roundedImageView;

    private ImageView logout;

    private TextView setName;


    // =========================================================
    // CHAT LIST
    // =========================================================

    private RecyclerView chatRecyclerView;

    private userAdapter adapter;

    private ArrayList<ChatListModel> chatList;


    // =========================================================
    // REQUEST LIST
    // =========================================================

    private RecyclerView requestRecyclerView;

    private ChatRequestAdapter requestAdapter;

    private ArrayList<ChatRequest> requestList;

    private TextView requestTitle;


    // =========================================================
    // REQUEST LISTENER
    // =========================================================

    private DatabaseReference requestReference;

    private ValueEventListener requestListener;


    // =========================================================
    // CONNECTION LISTENER
    // =========================================================

    private DatabaseReference connectionReference;

    private ValueEventListener connectionListener;


    // =========================================================
    // CHAT LISTENERS
    // =========================================================

    private ArrayList<DatabaseReference> chatReferences;

    private ArrayList<ValueEventListener> chatListeners;


    // =========================================================
    // CURRENT USER
    // =========================================================

    private String currentUserId = "";


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_useradd);


        // =====================================================
        // FIREBASE
        // =====================================================

        firebaseAuth =
                FirebaseAuth.getInstance();

        database =
                FirebaseDatabase.getInstance();


        // =====================================================
        // GET CURRENT USER
        // =====================================================

        FirebaseUser currentUser =
                firebaseAuth.getCurrentUser();


        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "User is not logged in",
                    Toast.LENGTH_LONG
            ).show();

            Intent intent =
                    new Intent(
                            useradd.this,
                            SignIn.class
                    );

            startActivity(intent);

            finish();

            return;
        }


        // =====================================================
        // CURRENT UID
        // =====================================================

        currentUserId =
                currentUser.getUid();


        Log.d(
                TAG,
                "===================================="
        );

        Log.d(
                TAG,
                "ON CREATE CURRENT UID = "
                        + currentUserId
        );

        Log.d(
                TAG,
                "===================================="
        );


        // =====================================================
        // FIND VIEWS
        // =====================================================

        circleImageView =
                findViewById(
                        R.id.userprofile
                );


        roundedImageView =
                findViewById(
                        R.id.addbutton
                );


        logout =
                findViewById(
                        R.id.logoutbutton
                );


        setName =
                findViewById(
                        R.id.setUserName
                );


        chatRecyclerView =
                findViewById(
                        R.id.chatRecyclerView
                );


        requestRecyclerView =
                findViewById(
                        R.id.requestRecyclerView
                );


        requestTitle =
                findViewById(
                        R.id.requestTitle
                );


        // =====================================================
        // INITIALIZE LISTS
        // =====================================================

        chatList =
                new ArrayList<>();


        requestList =
                new ArrayList<>();


        chatReferences =
                new ArrayList<>();


        chatListeners =
                new ArrayList<>();


        // =====================================================
        // CHAT RECYCLER
        // =====================================================

        chatRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        useradd.this
                )
        );


        adapter =
                new userAdapter(
                        useradd.this,
                        chatList
                );


        chatRecyclerView.setAdapter(
                adapter
        );


        // =====================================================
        // REQUEST ADAPTER
        // =====================================================

        requestAdapter =
                new ChatRequestAdapter(
                        requestList,

                        new ChatRequestAdapter
                                .OnRequestActionListener() {

                            @Override
                            public void onAccept(
                                    ChatRequest request) {

                                acceptRequest(request);
                            }


                            @Override
                            public void onDecline(
                                    ChatRequest request) {

                                declineRequest(request);
                            }
                        }
                );


        // =====================================================
        // REQUEST RECYCLER
        // =====================================================

        requestRecyclerView.setLayoutManager(
                new LinearLayoutManager(
                        useradd.this
                )
        );


        requestRecyclerView.setAdapter(
                requestAdapter
        );


        // =====================================================
        // INIT REQUEST UI
        // =====================================================

        setRequestSectionVisible(false);


        // =====================================================
        // LOAD CURRENT USER
        // =====================================================

        loadCurrentUser();


        // =====================================================
        // ADD USER BUTTON
        // =====================================================

        roundedImageView.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Intent intent =
                                new Intent(
                                        useradd.this,
                                        firstUsersPage.class
                                );

                        startActivity(intent);
                    }
                }
        );


        // =====================================================
        // LOGOUT
        // =====================================================

        logout.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        showLogoutDialog();
                    }
                }
        );
    }


    // =========================================================
    // ON START
    // =========================================================

    @Override
    protected void onStart() {

        super.onStart();


        FirebaseUser user =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser();


        if (user == null) {

            Log.e(
                    TAG,
                    "NO USER LOGGED IN"
            );

            return;
        }


        // =====================================================
        // GET CURRENT UID AGAIN
        // =====================================================

        currentUserId =
                user.getUid();


        Log.d(
                TAG,
                "===================================="
        );

        Log.d(
                TAG,
                "ON START CURRENT UID = "
                        + currentUserId
        );

        Log.d(
                TAG,
                "===================================="
        );


        // =====================================================
        // LOAD REQUESTS
        // =====================================================

        loadChatRequests();


        // =====================================================
        // LOAD CHATS
        // =====================================================

        loadPreviousChats();
    }


    // =========================================================
    // ON STOP
    // =========================================================

    @Override
    protected void onStop() {

        super.onStop();


        removeRequestListener();

        removeConnectionListener();

        removeChatListeners();
    }


    // =========================================================
    // LOAD CURRENT USER
    // =========================================================

    private void loadCurrentUser() {


        if (currentUserId == null ||
                currentUserId.isEmpty()) {

            return;
        }


        DatabaseReference reference =
                database
                        .getReference()
                        .child("Users")
                        .child(currentUserId);


        reference.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {


                        if (!snapshot.exists()) {

                            Log.e(
                                    TAG,
                                    "CURRENT USER DOES NOT EXIST"
                            );

                            return;
                        }


                        String name =
                                snapshot
                                        .child("username")
                                        .getValue(
                                                String.class
                                        );


                        String image =
                                snapshot
                                        .child("profileImage")
                                        .getValue(
                                                String.class
                                        );


                        if (name != null &&
                                !name.isEmpty()) {

                            setName.setText(
                                    name
                            );
                        }


                        if (image != null &&
                                !image.isEmpty() &&
                                !image.equals("null")) {

                            Picasso.get()
                                    .load(image)
                                    .placeholder(
                                            R.drawable.ic_dummy_user
                                    )
                                    .error(
                                            R.drawable.ic_dummy_user
                                    )
                                    .into(
                                            circleImageView
                                    );

                        } else {

                            circleImageView
                                    .setImageResource(
                                            R.drawable.ic_dummy_user
                                    );
                        }
                    }


                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        Log.e(
                                TAG,
                                "USER LOAD ERROR = "
                                        + error.getMessage()
                        );
                    }
                }
        );
    }


    // =========================================================
    // LOAD CHAT REQUESTS
    // =========================================================

    private void loadChatRequests() {


        if (currentUserId == null ||
                currentUserId.isEmpty()) {

            Log.e(
                    TAG,
                    "CURRENT USER UID IS EMPTY"
            );

            return;
        }


        // =====================================================
        // REMOVE OLD LISTENER
        // =====================================================

        removeRequestListener();


        // =====================================================
        // VERY IMPORTANT
        //
        // Firebase:
        //
        // chatRequests
        //     └── CURRENT_USER_UID
        //             └── REQUEST_ID
        //
        // =====================================================

        requestReference =
                database
                        .getReference()
                        .child("chatRequests")
                        .child(currentUserId);


        Log.d(
                TAG,
                "===================================="
        );

        Log.d(
                TAG,
                "LOADING CHAT REQUESTS"
        );

        Log.d(
                TAG,
                "AUTH UID = "
                        + currentUserId
        );

        Log.d(
                TAG,
                "READ PATH = "
                        + requestReference.toString()
        );

        Log.d(
                TAG,
                "===================================="
        );


        requestListener =
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {


                        // =====================================
                        // BASIC INFORMATION
                        // =====================================

                        Log.d(
                                TAG,
                                "REQUEST SNAPSHOT RECEIVED"
                        );


                        Log.d(
                                TAG,
                                "EXISTS = "
                                        + snapshot.exists()
                        );


                        Log.d(
                                TAG,
                                "CHILD COUNT = "
                                        + snapshot.getChildrenCount()
                        );


                        // =====================================
                        // CLEAR OLD REQUESTS
                        // =====================================

                        requestList.clear();


                        // =====================================
                        // READ EVERY REQUEST
                        // =====================================

                        for (DataSnapshot child :
                                snapshot.getChildren()) {


                            Log.d(
                                    TAG,
                                    "--------------------------------"
                            );


                            Log.d(
                                    TAG,
                                    "REQUEST KEY = "
                                            + child.getKey()
                            );


                            Log.d(
                                    TAG,
                                    "RAW DATA = "
                                            + child.getValue()
                            );


                            // =================================
                            // CONVERT TO CHAT REQUEST
                            // =================================

                            ChatRequest request =
                                    child.getValue(
                                            ChatRequest.class
                                    );


                            if (request == null) {

                                Log.e(
                                        TAG,
                                        "REQUEST CONVERSION FAILED"
                                );

                                continue;
                            }


                            // =================================
                            // PRINT REQUEST DATA
                            // =================================

                            Log.d(
                                    TAG,
                                    "senderId = "
                                            + request.getSenderId()
                            );


                            Log.d(
                                    TAG,
                                    "senderName = "
                                            + request.getSenderName()
                            );


                            Log.d(
                                    TAG,
                                    "receiverId = "
                                            + request.getReceiverId()
                            );


                            Log.d(
                                    TAG,
                                    "receiverName = "
                                            + request.getReceiverName()
                            );


                            Log.d(
                                    TAG,
                                    "status = "
                                            + request.getStatus()
                            );


                            // =================================
                            // CHECK RECEIVER
                            // =================================

                            boolean correctReceiver =
                                    currentUserId.equals(
                                            request.getReceiverId()
                                    );


                            Log.d(
                                    TAG,
                                    "CORRECT RECEIVER = "
                                            + correctReceiver
                            );


                            // =================================
                            // CHECK STATUS
                            // =================================

                            boolean pending =
                                    request.getStatus() != null
                                            &&
                                            request
                                                    .getStatus()
                                                    .trim()
                                                    .equalsIgnoreCase(
                                                            "pending"
                                                    );


                            Log.d(
                                    TAG,
                                    "PENDING = "
                                            + pending
                            );


                            // =================================
                            // ADD REQUEST
                            // =================================

                            if (correctReceiver &&
                                    pending) {


                                requestList.add(
                                        request
                                );


                                Log.d(
                                        TAG,
                                        "******** REQUEST ADDED ********"
                                );
                            }
                        }


                        // =====================================
                        // SORT
                        // =====================================

                        Collections.sort(
                                requestList,
                                new Comparator<ChatRequest>() {

                                    @Override
                                    public int compare(
                                            ChatRequest first,
                                            ChatRequest second) {

                                        return Long.compare(
                                                second.getTimestamp(),
                                                first.getTimestamp()
                                        );
                                    }
                                }
                        );


                        // =====================================
                        // UPDATE RECYCLER
                        // =====================================

                        requestAdapter
                                .notifyDataSetChanged();


                        // =====================================
                        // FINAL COUNT
                        // =====================================

                        Log.d(
                                TAG,
                                "===================================="
                        );

                        Log.d(
                                TAG,
                                "FINAL REQUEST COUNT = "
                                        + requestList.size()
                        );

                        Log.d(
                                TAG,
                                "===================================="
                        );


                        // =====================================
                        // SHOW / HIDE REQUEST UI
                        // =====================================

                        if (requestList.size() > 0) {

                            requestTitle.setText(
                                    "🔔 Chat Requests ("
                                            + requestList.size()
                                            + ")"
                            );

                            setRequestSectionVisible(true);

                        } else {

                            setRequestSectionVisible(false);
                        }
                    }


                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {


                        Log.e(
                                TAG,
                                "===================================="
                        );


                        Log.e(
                                TAG,
                                "FIREBASE REQUEST ERROR"
                        );


                        Log.e(
                                TAG,
                                error.getMessage()
                        );


                        Log.e(
                                TAG,
                                "===================================="
                        );


                        Toast.makeText(
                                useradd.this,
                                "Request error: "
                                        + error.getMessage(),
                                Toast.LENGTH_LONG
                        ).show();
                    }
                };


        // =====================================================
        // START LISTENER
        // =====================================================

        requestReference.addValueEventListener(
                requestListener
        );
    }


    // =========================================================
    // SHOW / HIDE THE ENTIRE REQUEST SECTION
    //
    // Fix: toggling requestTitle + requestRecyclerView alone
    // was not enough, because both sit inside a parent
    // container (e.g. "requestSection") that XML may have set
    // to GONE by default. Toggling the parent here as well
    // guarantees the section becomes visible regardless of
    // how the XML nested these views.
    // =========================================================

    private void setRequestSectionVisible(boolean visible) {

        int visibility =
                visible ? View.VISIBLE : View.GONE;

        requestTitle.setVisibility(visibility);
        requestRecyclerView.setVisibility(visibility);

        // Walk up and also unhide/hide the immediate parent view,
        // in case it's a wrapping container (e.g. requestSection)
        // that was left GONE in the XML layout.
        if (requestTitle.getParent() instanceof View) {

            View parent =
                    (View) requestTitle.getParent();

            parent.setVisibility(visibility);
        }
    }


    // =========================================================
    // ACCEPT REQUEST
    // =========================================================

    private void acceptRequest(
            ChatRequest request) {


        if (request == null) {
            return;
        }


        String senderId =
                request.getSenderId();


        String receiverId =
                request.getReceiverId();


        if (senderId == null ||
                senderId.isEmpty() ||
                receiverId == null ||
                receiverId.isEmpty()) {


            Toast.makeText(
                    this,
                    "Invalid request",
                    Toast.LENGTH_SHORT
            ).show();


            return;
        }


        // =====================================================
        // SECURITY CHECK
        // =====================================================

        if (!currentUserId.equals(
                receiverId
        )) {


            Toast.makeText(
                    this,
                    "This request is not for you",
                    Toast.LENGTH_SHORT
            ).show();


            return;
        }


        // =====================================================
        // ROOT
        // =====================================================

        DatabaseReference root =
                database.getReference();


        Map<String, Object> updates =
                new HashMap<>();


        // =====================================================
        // ACCEPT CURRENT USER'S REQUEST
        // =====================================================

        updates.put(
                "chatRequests/"
                        + receiverId
                        + "/"
                        + senderId
                        + "/status",
                "accepted"
        );


        // =====================================================
        // CREATE REVERSE REQUEST
        // =====================================================

        updates.put(
                "chatRequests/"
                        + senderId
                        + "/"
                        + receiverId
                        + "/senderId",
                senderId
        );


        updates.put(
                "chatRequests/"
                        + senderId
                        + "/"
                        + receiverId
                        + "/receiverId",
                receiverId
        );


        updates.put(
                "chatRequests/"
                        + senderId
                        + "/"
                        + receiverId
                        + "/senderName",
                request.getSenderName()
        );


        updates.put(
                "chatRequests/"
                        + senderId
                        + "/"
                        + receiverId
                        + "/receiverName",
                request.getReceiverName()
        );


        updates.put(
                "chatRequests/"
                        + senderId
                        + "/"
                        + receiverId
                        + "/status",
                "accepted"
        );


        updates.put(
                "chatRequests/"
                        + senderId
                        + "/"
                        + receiverId
                        + "/timestamp",
                request.getTimestamp()
        );


        // =====================================================
        // CONNECTION CURRENT USER
        // =====================================================

        updates.put(
                "connections/"
                        + receiverId
                        + "/"
                        + senderId,
                true
        );


        // =====================================================
        // CONNECTION SENDER
        // =====================================================

        updates.put(
                "connections/"
                        + senderId
                        + "/"
                        + receiverId,
                true
        );


        // =====================================================
        // WRITE EVERYTHING
        // =====================================================

        root.updateChildren(
                updates
        ).addOnCompleteListener(
                task -> {


                    if (task.isSuccessful()) {


                        Toast.makeText(
                                useradd.this,
                                "Chat request accepted",
                                Toast.LENGTH_SHORT
                        ).show();


                        // =================================
                        // REMOVE FROM LOCAL LIST
                        // =================================

                        requestList.remove(
                                request
                        );


                        requestAdapter
                                .notifyDataSetChanged();


                        // =================================
                        // HIDE IF EMPTY
                        // =================================

                        if (requestList.isEmpty()) {

                            setRequestSectionVisible(false);
                        }


                        // =================================
                        // ADD TO CHAT
                        // =================================

                        addConnectedUserToChatList(
                                senderId
                        );


                    } else {


                        String error =
                                "Unknown error";


                        if (task.getException() != null) {

                            error =
                                    task.getException()
                                            .getMessage();
                        }


                        Toast.makeText(
                                useradd.this,
                                "Failed: " + error,
                                Toast.LENGTH_LONG
                        ).show();
                    }
                }
        );
    }


    // =========================================================
    // DECLINE REQUEST
    // =========================================================

    private void declineRequest(
            ChatRequest request) {


        if (request == null) {
            return;
        }


        String senderId =
                request.getSenderId();


        if (senderId == null ||
                senderId.isEmpty()) {

            return;
        }


        DatabaseReference reference =
                database
                        .getReference()
                        .child("chatRequests")
                        .child(currentUserId)
                        .child(senderId);


        reference
                .child("status")
                .setValue("declined")
                .addOnCompleteListener(
                        task -> {


                            if (task.isSuccessful()) {


                                Toast.makeText(
                                        useradd.this,
                                        "Request declined",
                                        Toast.LENGTH_SHORT
                                ).show();


                                requestList.remove(
                                        request
                                );


                                requestAdapter
                                        .notifyDataSetChanged();


                                if (requestList.isEmpty()) {

                                    setRequestSectionVisible(false);
                                }


                            } else {


                                Toast.makeText(
                                        useradd.this,
                                        "Failed to decline request",
                                        Toast.LENGTH_SHORT
                                ).show();
                            }
                        }
                );
    }


    // =========================================================
    // LOAD PREVIOUS CHATS
    // =========================================================

    private void loadPreviousChats() {


        removeConnectionListener();

        removeChatListeners();


        chatList.clear();

        adapter.notifyDataSetChanged();


        connectionReference =
                database
                        .getReference()
                        .child("connections")
                        .child(currentUserId);


        connectionListener =
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {


                        Log.d(
                                TAG,
                                "CONNECTION COUNT = "
                                        + snapshot.getChildrenCount()
                        );


                        removeOnlyChatListeners();


                        if (!snapshot.exists()) {

                            adapter.notifyDataSetChanged();

                            return;
                        }


                        for (DataSnapshot child :
                                snapshot.getChildren()) {


                            String otherUserId =
                                    child.getKey();


                            if (otherUserId == null ||
                                    otherUserId.isEmpty()) {

                                continue;
                            }


                            if (otherUserId.equals(
                                    currentUserId
                            )) {

                                continue;
                            }


                            Boolean connected =
                                    child.getValue(
                                            Boolean.class
                                    );


                            if (!Boolean.TRUE.equals(
                                    connected
                            )) {

                                continue;
                            }


                            addConnectedUserToChatList(
                                    otherUserId
                            );
                        }
                    }


                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {


                        Log.e(
                                TAG,
                                "CONNECTION ERROR = "
                                        + error.getMessage()
                        );
                    }
                };


        connectionReference.addValueEventListener(
                connectionListener
        );
    }


    // =========================================================
    // ADD CONNECTED USER
    // =========================================================

    private void addConnectedUserToChatList(
            String otherUserId) {


        if (otherUserId == null ||
                otherUserId.isEmpty()) {

            return;
        }


        if (otherUserId.equals(
                currentUserId
        )) {

            return;
        }


        DatabaseReference userReference =
                database
                        .getReference()
                        .child("Users")
                        .child(otherUserId);


        userReference.addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {


                        if (!snapshot.exists()) {

                            Log.e(
                                    TAG,
                                    "USER NOT FOUND = "
                                            + otherUserId
                            );

                            return;
                        }


                        Users user =
                                snapshot.getValue(
                                        Users.class
                                );


                        if (user == null) {

                            return;
                        }


                        user.setUserId(
                                otherUserId
                        );


                        listenToChat(
                                user,
                                otherUserId
                        );
                    }


                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {


                        Log.e(
                                TAG,
                                "USER READ ERROR = "
                                        + error.getMessage()
                        );
                    }
                }
        );
    }


    // =========================================================
    // LISTEN TO CHAT
    // =========================================================

    private void listenToChat(
            Users user,
            String otherUserId) {


        // =====================================================
        // PREVENT DUPLICATE
        // =====================================================

        for (DatabaseReference reference :
                chatReferences) {


            if (reference.toString().endsWith(
                    "/" + otherUserId + "/message"
            )) {

                return;
            }
        }


        // =====================================================
        // CHAT ROOM
        // =====================================================

        String room =
                currentUserId
                        + otherUserId;


        DatabaseReference chatReference =
                database
                        .getReference()
                        .child("chats")
                        .child(room)
                        .child("message");


        ValueEventListener listener =
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {


                        updateChat(
                                user,
                                otherUserId,
                                snapshot
                        );
                    }


                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {


                        Log.e(
                                TAG,
                                "CHAT ERROR = "
                                        + error.getMessage()
                        );
                    }
                };


        chatReferences.add(
                chatReference
        );


        chatListeners.add(
                listener
        );


        chatReference.addValueEventListener(
                listener
        );
    }


    // =========================================================
    // UPDATE CHAT
    // =========================================================

    private void updateChat(
            Users user,
            String otherUserId,
            DataSnapshot snapshot) {


        String lastMessage =
                "Start chatting";


        long latestTimestamp =
                0;


        int unreadCount =
                0;


        if (snapshot.exists()) {


            long lastReadTime =
                    getLastReadTime(
                            otherUserId
                    );


            for (DataSnapshot messageSnapshot :
                    snapshot.getChildren()) {


                Long timestamp =
                        messageSnapshot
                                .child("timeStamp")
                                .getValue(
                                        Long.class
                                );


                String message =
                        messageSnapshot
                                .child("message")
                                .getValue(
                                        String.class
                                );


                String senderId =
                        messageSnapshot
                                .child("senderId")
                                .getValue(
                                        String.class
                                );


                if (timestamp == null) {
                    continue;
                }


                // =========================================
                // LATEST MESSAGE
                // =========================================

                if (timestamp >
                        latestTimestamp) {


                    latestTimestamp =
                            timestamp;


                    if (message != null &&
                            !message.isEmpty()) {

                        lastMessage =
                                message;
                    }
                }


                // =========================================
                // UNREAD
                // =========================================

                if (timestamp >
                        lastReadTime &&
                        senderId != null &&
                        !senderId.equals(
                                currentUserId
                        )) {

                    unreadCount++;
                }
            }
        }


        // =====================================================
        // MODEL
        // =====================================================

        ChatListModel newChat =
                new ChatListModel(
                        user,
                        otherUserId,
                        lastMessage,
                        latestTimestamp,
                        unreadCount
                );


        // =====================================================
        // CHECK EXISTING
        // =====================================================

        boolean found =
                false;


        for (int i = 0;
             i < chatList.size();
             i++) {


            ChatListModel existing =
                    chatList.get(i);


            if (existing.getUserId() != null &&
                    existing.getUserId().equals(
                            otherUserId
                    )) {


                chatList.set(
                        i,
                        newChat
                );


                found =
                        true;


                break;
            }
        }


        // =====================================================
        // ADD
        // =====================================================

        if (!found) {

            chatList.add(
                    newChat
            );
        }


        // =====================================================
        // SORT
        // =====================================================

        Collections.sort(
                chatList,
                new Comparator<ChatListModel>() {

                    @Override
                    public int compare(
                            ChatListModel first,
                            ChatListModel second) {

                        return Long.compare(
                                second.getTimestamp(),
                                first.getTimestamp()
                        );
                    }
                }
        );


        adapter.notifyDataSetChanged();
    }


    // =========================================================
    // LAST READ TIME
    // =========================================================

    private long getLastReadTime(
            String userId) {


        return getSharedPreferences(
                "chat_read_status",
                MODE_PRIVATE
        )
                .getLong(
                        "read_" + userId,
                        0
                );
    }


    // =========================================================
    // REMOVE REQUEST LISTENER
    // =========================================================

    private void removeRequestListener() {


        if (requestReference != null &&
                requestListener != null) {


            requestReference.removeEventListener(
                    requestListener
            );
        }


        requestReference =
                null;


        requestListener =
                null;
    }


    // =========================================================
    // REMOVE CONNECTION LISTENER
    // =========================================================

    private void removeConnectionListener() {


        if (connectionReference != null &&
                connectionListener != null) {


            connectionReference.removeEventListener(
                    connectionListener
            );
        }


        connectionReference =
                null;


        connectionListener =
                null;
    }


    // =========================================================
    // REMOVE CHAT LISTENERS
    // =========================================================

    private void removeChatListeners() {

        removeOnlyChatListeners();
    }


    private void removeOnlyChatListeners() {


        for (int i = 0;
             i < chatReferences.size();
             i++) {


            if (i < chatListeners.size()) {


                chatReferences
                        .get(i)
                        .removeEventListener(
                                chatListeners.get(i)
                        );
            }
        }


        chatReferences.clear();

        chatListeners.clear();
    }


    // =========================================================
    // LOGOUT
    // =========================================================

    private void showLogoutDialog() {


        Dialog dialog =
                new Dialog(
                        useradd.this,
                        R.style.dialog
                );


        dialog.setContentView(
                R.layout.dialog_layout
        );


        Button yes =
                dialog.findViewById(
                        R.id.YesButton
                );


        Button no =
                dialog.findViewById(
                        R.id.NoButton
                );


        yes.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {


                        FirebaseAuth
                                .getInstance()
                                .signOut();


                        nameStore.myname =
                                "";


                        Intent intent =
                                new Intent(
                                        useradd.this,
                                        SignIn.class
                                );


                        intent.setFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                                        |
                                        Intent.FLAG_ACTIVITY_CLEAR_TASK
                        );


                        startActivity(
                                intent
                        );


                        dialog.dismiss();

                        finish();
                    }
                }
        );


        no.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        dialog.dismiss();
                    }
                }
        );


        dialog.show();
    }
}