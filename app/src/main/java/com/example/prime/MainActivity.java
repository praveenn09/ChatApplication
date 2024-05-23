package com.example.prime;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.prime.activities.ChatWindow;
import com.example.prime.activities.SignIn;
import com.example.prime.activities.callingAct;
import com.example.prime.activities.firstUsersPage;
import com.example.prime.activities.useradd;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(MainActivity.this, SignIn.class);
                startActivity(intent);
                finish();
            }
        },2000);

    }

}