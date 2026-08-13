package com.example.prime.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.prime.R;

import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.Collections;

public class videocall extends AppCompatActivity {

    private ZegoSendCallInvitationButton voiceCall;
    private ZegoSendCallInvitationButton videoCall;

    private ImageView backButton;

    private String receiverId = "";
    private String receiverName = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(
                R.layout.activity_videocall
        );


        // =====================================================
        // GET RECEIVER
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
        // FIND VIEWS
        // =====================================================

        backButton =
                findViewById(
                        R.id.backFromVideoCall
                );

        voiceCall =
                findViewById(
                        R.id.voice_call
                );

        videoCall =
                findViewById(
                        R.id.video_call
                );


        // =====================================================
        // BACK BUTTON
        // =====================================================

        backButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        finish();
                    }
                }
        );


        // =====================================================
        // SET VOICE CALL
        // =====================================================

        setupVoiceCall();


        // =====================================================
        // SET VIDEO CALL
        // =====================================================

        setupVideoCall();
    }


    // =========================================================
    // VOICE CALL
    // =========================================================

    private void setupVoiceCall() {

        voiceCall.setIsVideoCall(
                false
        );

        voiceCall.setResourceID(
                "zego_uikit_call"
        );

        voiceCall.setInvitees(
                Collections.singletonList(
                        new ZegoUIKitUser(
                                receiverId,
                                receiverName
                        )
                )
        );
    }


    // =========================================================
    // VIDEO CALL
    // =========================================================

    private void setupVideoCall() {

        videoCall.setIsVideoCall(
                true
        );

        videoCall.setResourceID(
                "zego_uikit_call"
        );

        videoCall.setInvitees(
                Collections.singletonList(
                        new ZegoUIKitUser(
                                receiverId,
                                receiverName
                        )
                )
        );
    }
}