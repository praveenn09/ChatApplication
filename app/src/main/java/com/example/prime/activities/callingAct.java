package com.example.prime.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Application;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.prime.R;
import com.zegocloud.uikit.prebuilt.call.config.ZegoNotificationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig;
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationService;

public class callingAct extends AppCompatActivity {
   Button call;
   EditText id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calling);
        Toast.makeText(getApplicationContext(), "Please Enter a unique id for to talk to each other ", Toast.LENGTH_SHORT).show();
      id=findViewById(R.id.getUserId);
      call=findViewById(R.id.button2);
      call.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              String userId=id.getText().toString();
              if(userId.isEmpty()){
                  id.setError("Please Enter Id");
              }
              else {

                 startServices(userId);
                  Intent i=new Intent(callingAct.this, videocall.class);
                  startActivity(i);
              }
          }
      });
    }

   private void startServices(String userId) {
        Application application = getApplication(); // Android's application context
        long appID = 1887249431;   // yourAppID 1887249431
       // 303b44eb6a6a3c049e2d518fcc4834ce69f2de7b9f981b3e934375dab28fde06
        String appSign ="303b44eb6a6a3c049e2d518fcc4834ce69f2de7b9f981b3e934375dab28fde06";  // yourAppSign
        String userID =userId; // yourUserID, userID should only contain numbers, English characters, and '_'.
        String userName =userId;   // yourUserName

        ZegoUIKitPrebuiltCallInvitationConfig callInvitationConfig = new ZegoUIKitPrebuiltCallInvitationConfig();
      //  callInvitationConfig.notifyWhenAppRunningInBackgroundOrQuit = true;
        ZegoNotificationConfig notificationConfig = new ZegoNotificationConfig();
        notificationConfig.sound = "zego_uikit_sound_call";
        notificationConfig.channelID = "CallInvitation";
        notificationConfig.channelName = "CallInvitation";
        ZegoUIKitPrebuiltCallInvitationService.init(getApplication(), appID, appSign, userID, userName,callInvitationConfig);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ZegoUIKitPrebuiltCallInvitationService.unInit();
    }
}