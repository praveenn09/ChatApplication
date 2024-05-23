package com.example.prime.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.prime.R;
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton;
import com.zegocloud.uikit.service.defines.ZegoUIKitUser;

import java.util.Collections;

public class videocall extends AppCompatActivity {
     EditText secondUserId;

      ZegoSendCallInvitationButton simplecall,vd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videocall);
        secondUserId=findViewById(R.id.newUserIDSecond);
      simplecall =findViewById(R.id.voice_call);
      vd=findViewById(R.id.video_call);

      secondUserId.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

          }

          @Override
          public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
              String targetUserId=secondUserId.getText().toString();
              setVoiceCall(targetUserId);
              setVideoCall(targetUserId);
          }

          @Override
          public void afterTextChanged(Editable editable) {

          }
      });

    }
    private void setVoiceCall(String targetUserId) {
        simplecall.setIsVideoCall(false);
        simplecall.setResourceID("zego_uikit_call");
        simplecall.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserId)));
    }
    private void setVideoCall(String targetUserId) {
        vd.setIsVideoCall(true);
        vd.setResourceID("zego_uikit_call");
        vd.setInvitees(Collections.singletonList(new ZegoUIKitUser(targetUserId)));
    }




}