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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.HashMap;
import java.util.Map;

public class SignIn extends AppCompatActivity {

    private FirebaseAuth mAuth;

    private EditText email;
    private EditText password;
    private EditText userN;

    private MaterialButton signInButton;

    private TextView createNew;
    private TextView ForgotPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);


        // ==========================================
        // FIREBASE AUTH
        // ==========================================

        mAuth = FirebaseAuth.getInstance();


        // ==========================================
        // FIND VIEWS
        // ==========================================

        email = findViewById(R.id.inputEmail);

        userN = findViewById(R.id.enterUser);

        password = findViewById(R.id.inputPassword);

        signInButton = findViewById(R.id.button);

        createNew = findViewById(R.id.createNewAccount);

        ForgotPassword = findViewById(R.id.ForgotPassword);


        // ==========================================
        // CREATE ACCOUNT
        // ==========================================

        createNew.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Intent intent =
                                new Intent(
                                        SignIn.this,
                                        SignUp.class
                                );

                        startActivity(intent);
                    }
                }
        );


        // ==========================================
        // LOGIN
        // ==========================================

        signInButton.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        String name =
                                userN.getText()
                                        .toString()
                                        .trim();

                        String em =
                                email.getText()
                                        .toString()
                                        .trim();

                        String ps =
                                password.getText()
                                        .toString()
                                        .trim();


                        // ==================================
                        // VALIDATION
                        // ==================================

                        if (em.isEmpty()) {

                            email.setError(
                                    "ENTER VALID EMAIL"
                            );

                            return;
                        }


                        if (ps.isEmpty()) {

                            password.setError(
                                    "ENTER VALID PASSWORD"
                            );

                            return;
                        }


                        // ==================================
                        // STORE NAME
                        // ==================================

                        nameStore.myname = name;


                        // ==================================
                        // LOGIN
                        // ==================================

                        performLogin(
                                em,
                                ps
                        );
                    }
                }
        );


        // ==========================================
        // FORGOT PASSWORD
        // ==========================================

        ForgotPassword.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Intent intent =
                                new Intent(
                                        SignIn.this,
                                        forgotPass.class
                                );

                        startActivity(intent);
                    }
                }
        );
    }


    // =========================================================
    // LOGIN
    // =========================================================

    private void performLogin(
            String em,
            String ps) {


        mAuth.signInWithEmailAndPassword(
                        em,
                        ps
                )
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task) {


                                if (task.isSuccessful()) {


                                    FirebaseUser currentUser =
                                            FirebaseAuth
                                                    .getInstance()
                                                    .getCurrentUser();


                                    if (currentUser == null) {

                                        Toast.makeText(
                                                SignIn.this,
                                                "User not found",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                        return;
                                    }


                                    // ==================================
                                    // GET FIREBASE UID
                                    // ==================================

                                    String userId =
                                            currentUser.getUid();


                                    // ==================================
                                    // SET ONLINE
                                    // ==================================

                                    setupPresence(userId);


                                    Toast.makeText(
                                            SignIn.this,
                                            "Authentication Success.",
                                            Toast.LENGTH_SHORT
                                    ).show();


                                    // ==================================
                                    // OPEN HOME
                                    // ==================================

                                    Intent intent =
                                            new Intent(
                                                    SignIn.this,
                                                    useradd.class
                                            );

                                    startActivity(intent);

                                    finish();


                                } else {


                                    String errorMessage =
                                            "Unknown error";


                                    if (task.getException() != null) {

                                        errorMessage =
                                                task.getException()
                                                        .getMessage();
                                    }


                                    Toast.makeText(
                                            SignIn.this,
                                            "Login failed: "
                                                    + errorMessage,
                                            Toast.LENGTH_LONG
                                    ).show();
                                }
                            }
                        }
                );
    }


    // =========================================================
    // FIREBASE ONLINE / OFFLINE PRESENCE
    // =========================================================

    private void setupPresence(
            String userId) {


        DatabaseReference
                databaseReference =
                FirebaseDatabase
                        .getInstance()
                        .getReference();


        DatabaseReference
                userPresence =
                databaseReference
                        .child("Users")
                        .child(userId);


        DatabaseReference
                connectedReference =
                databaseReference
                        .child(".info")
                        .child("connected");


        connectedReference.addValueEventListener(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(
                            @NonNull DataSnapshot snapshot) {


                        Boolean connected =
                                snapshot.getValue(
                                        Boolean.class
                                );


                        if (Boolean.TRUE.equals(
                                connected)) {


                            // ======================================
                            // WHEN CONNECTION IS LOST
                            // ======================================

                            userPresence
                                    .child("online")
                                    .onDisconnect()
                                    .setValue(false);


                            userPresence
                                    .child("lastSeen")
                                    .onDisconnect()
                                    .setValue(
                                            ServerValue.TIMESTAMP
                                    );


                            // ======================================
                            // USER IS ONLINE
                            // ======================================

                            Map<String, Object>
                                    onlineData =
                                    new HashMap<>();


                            onlineData.put(
                                    "online",
                                    true
                            );


                            onlineData.put(
                                    "lastSeen",
                                    0
                            );


                            userPresence.updateChildren(
                                    onlineData
                            );
                        }
                    }


                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        // Nothing required here
                    }
                }
        );
    }
}