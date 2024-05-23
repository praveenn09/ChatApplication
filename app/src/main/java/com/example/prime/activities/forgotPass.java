package com.example.prime.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.prime.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

public class forgotPass extends AppCompatActivity {
  MaterialButton reset;
  TextView backToLogin;
  EditText email;
  private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth=FirebaseAuth.getInstance();
        setContentView(R.layout.activity_forgot_pass);
        reset=findViewById(R.id.resetButton);
        backToLogin=findViewById(R.id.gotologin);
        email=findViewById(R.id.resetEmail);
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(forgotPass.this, SignIn.class);
                startActivity(intent);
            }
        });
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String resetEmail=email.getText().toString();
                if(resetEmail.isEmpty()){
                    email.setError("Please Enter Email");
                }
                else{
                    resetEmailMeth(resetEmail);
                }
            }
        });

    }

    private void resetEmailMeth(String resetEmail) {
        mAuth.sendPasswordResetEmail(resetEmail)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful())
                        {
                            Toast.makeText(forgotPass.this,"Please Check Your Email",Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(forgotPass.this, SignIn.class);
                            startActivity(i);
                        }
                        else {
                            Toast.makeText(forgotPass.this,"Error",Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}