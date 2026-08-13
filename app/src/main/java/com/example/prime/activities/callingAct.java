package com.example.prime.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prime.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;


public class callingAct extends AppCompatActivity {

    private String receiverId = "";
    private String receiverName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_calling);


        // =====================================================
        // GET RECEIVER FROM CHAT WINDOW
        // =====================================================

        receiverId =
                getIntent()
                        .getStringExtra("receiverId");


        receiverName =
                getIntent()
                        .getStringExtra("receiverName");


        if (receiverId == null ||
                receiverId.isEmpty()) {

            Toast.makeText(
                    this,
                    "Receiver not found",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // =====================================================
        // START ZEGO SERVICE
        // =====================================================

        startServices();


        // =====================================================
        // OPEN VIDEO CALL SCREEN
        // =====================================================

        Intent intent =
                new Intent(
                        callingAct.this,
                        videocall.class
                );


        intent.putExtra(
                "receiverId",
                receiverId
        );


        intent.putExtra(
                "receiverName",
                receiverName
        );


        startActivity(intent);


        finish();
    }


    // =========================================================
    // ZEGO INITIALIZATION
    // =========================================================

    private void startServices() {


        FirebaseUser currentUser =
                FirebaseAuth
                        .getInstance()
                        .getCurrentUser();


        if (currentUser == null) {

            Toast.makeText(
                    this,
                    "User is not logged in",
                    Toast.LENGTH_SHORT
            ).show();

            finish();

            return;
        }


        // =====================================================
        // CURRENT USER = ZEGO USER
        // =====================================================

        String userID =
                currentUser.getUid();


        String userName =
                userID;


        // =====================================================
        // YOUR ZEGO APP ID
        // =====================================================

        long appID =
                1361685803L;


        // =====================================================
        // YOUR ZEGO APP SIGN
        // =====================================================

        String appSign =
                "b3d1c330ab3a7c94a9142b36156858baba530a4e033cd16b20e6695cba017147";


        // =====================================================
        // ZEGO CONFIG
        // =====================================================

        ZegoUIKitPrebuiltCallInvitationConfig
                callInvitationConfig =
                new ZegoUIKitPrebuiltCallInvitationConfig();


        // =====================================================
        // NOTIFICATION CONFIG
        // =====================================================

        ZegoNotificationConfig notificationConfig =
                new ZegoNotificationConfig();


        notificationConfig.sound =
                "zego_uikit_sound_call";


        notificationConfig.channelID =
                "CallInvitation";


        notificationConfig.channelName =
                "CallInvitation";


        // =====================================================
        // INITIALIZE ZEGO
        // =====================================================

        ZegoUIKitPrebuiltCallInvitationService.init(
                getApplication(),
                appID,
                appSign,
                userID,
                userName,
                callInvitationConfig
        );
    }


    @Override
    protected void onDestroy() {

        super.onDestroy();

        /*
         * Don't unInit ZEGO here.
         *
         * The service should remain available
         * while the user is logged into the app.
         */
    }
}