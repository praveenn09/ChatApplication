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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignIn extends AppCompatActivity {
   private FirebaseAuth mAuth;
    EditText email,password;
    /*  FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("hello");
        myRef.setValue("Hello, World!");*/
    // Initialize Firebase Auth
    EditText userN;
    MaterialButton signInButton;
    TextView createNew,ForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mAuth = FirebaseAuth.getInstance();
        email=findViewById(R.id.inputEmail);
        userN=findViewById(R.id.enterUser);
        password=findViewById(R.id.inputPassword);
        signInButton=findViewById(R.id.button);
        createNew=findViewById(R.id.createNewAccount);
        ForgotPassword=findViewById(R.id.ForgotPassword);
        createNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SignIn.this,SignUp.class);
                startActivity(i);
            }
        });
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=userN.getText().toString();
                nameStore.myname=name;
                Toast.makeText(SignIn.this," "+nameStore.myname,Toast.LENGTH_SHORT).show();
                String em =email.getText().toString();
                String ps =password.getText().toString();
                if(em.isEmpty() )
                {
                    email.setError("ENTER VALID EMAIL");
                } else if ( ps.isEmpty()) {
                    password.setError("ENTER VALID PASSWORD");
                }
                else {
                    performLogin(em,ps);
                }
            }
        });

        ForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(SignIn.this, forgotPass.class);
                startActivity(intent);
            }
        });

    }

    private void performLogin(String em, String ps) {
        mAuth.signInWithEmailAndPassword(em, ps)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            Toast.makeText(SignIn.this, "Authentication Success.",
                                    Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(SignIn.this,useradd.class);
                            startActivity(i);
                            finish();
                        } else {
                            Toast.makeText(SignIn.this, "Authentication error.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}