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
import com.example.prime.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUp extends AppCompatActivity {

    // =====================================================
    // FIREBASE
    // =====================================================

    private FirebaseAuth mAuth;

    private FirebaseDatabase database;

    private DatabaseReference reference;


    // =====================================================
    // VIEWS
    // =====================================================

    private EditText name;
    private EditText email;
    private EditText password;
    private EditText confirmPassword;

    private MaterialButton signUp;

    private TextView loginback;


    // =====================================================
    // DEFAULT PROFILE IMAGE
    // =====================================================

    private static final String DEFAULT_PROFILE_IMAGE =
            "https://firebasestorage.googleapis.com/v0/b/chatting-application-dcdf5.appspot.com/o/profile.png?alt=media&token=f436be3c-d8c0-473e-acd6-04ab23b40281";


    // =====================================================
    // ON CREATE
    // =====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_up);


        // =================================================
        // FIREBASE INITIALIZATION
        // =================================================

        mAuth = FirebaseAuth.getInstance();

        database = FirebaseDatabase.getInstance();

        reference =
                database.getReference("Users");


        // =================================================
        // FIND VIEWS
        // =================================================

        name =
                findViewById(R.id.inputName);

        email =
                findViewById(R.id.inputEmail);

        password =
                findViewById(R.id.inputPassword);

        confirmPassword =
                findViewById(R.id.inputRePassword);

        signUp =
                findViewById(R.id.button);

        loginback =
                findViewById(R.id.backLogin);


        // =================================================
        // BACK TO SIGN IN
        // =================================================

        loginback.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        Intent intent =
                                new Intent(
                                        SignUp.this,
                                        SignIn.class
                                );

                        startActivity(intent);

                        finish();
                    }
                }
        );


        // =================================================
        // SIGN UP BUTTON
        // =================================================

        signUp.setOnClickListener(
                new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        validateAndSignup();
                    }
                }
        );
    }


    // =====================================================
    // VALIDATE INPUT
    // =====================================================

    private void validateAndSignup() {

        String nm =
                name.getText()
                        .toString()
                        .trim();


        String em =
                email.getText()
                        .toString()
                        .trim();


        String pw =
                password.getText()
                        .toString();


        String cp =
                confirmPassword.getText()
                        .toString();


        // =================================================
        // NAME
        // =================================================

        if (nm.isEmpty()) {

            name.setError(
                    "Enter Name"
            );

            name.requestFocus();

            return;
        }


        // =================================================
        // EMAIL
        // =================================================

        if (em.isEmpty()) {

            email.setError(
                    "Enter Email"
            );

            email.requestFocus();

            return;
        }


        String emailRegex =
                "^[A-Za-z0-9+_.-]+@(.+)$";


        Pattern pattern =
                Pattern.compile(emailRegex);


        Matcher matcher =
                pattern.matcher(em);


        if (!matcher.matches()) {

            email.setError(
                    "Enter Valid Email"
            );

            email.requestFocus();

            return;
        }


        // =================================================
        // PASSWORD
        // =================================================

        if (pw.length() < 6) {

            password.setError(
                    "Password must contain at least 6 characters"
            );

            password.requestFocus();

            return;
        }


        // =================================================
        // CONFIRM PASSWORD
        // =================================================

        if (!pw.equals(cp)) {

            confirmPassword.setError(
                    "Passwords do not match"
            );

            confirmPassword.requestFocus();

            return;
        }


        // =================================================
        // STORE NAME
        // =================================================

        nameStore.myname = nm;


        // =================================================
        // CREATE ACCOUNT
        // =================================================

        performSignUp(
                nm,
                em,
                pw
        );
    }


    // =====================================================
    // FIREBASE SIGN UP
    // =====================================================

    private void performSignUp(
            String userName,
            String userEmail,
            String userPassword) {


        signUp.setEnabled(false);


        mAuth
                .createUserWithEmailAndPassword(
                        userEmail,
                        userPassword
                )
                .addOnCompleteListener(
                        this,
                        new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(
                                    @NonNull Task<AuthResult> task) {


                                // =================================
                                // SIGN UP FAILED
                                // =================================

                                if (!task.isSuccessful()) {

                                    signUp.setEnabled(true);


                                    String error =
                                            "Authentication failed";


                                    if (task.getException() != null) {

                                        error =
                                                task.getException()
                                                        .getMessage();
                                    }


                                    Toast.makeText(
                                            SignUp.this,
                                            error,
                                            Toast.LENGTH_LONG
                                    ).show();


                                    return;
                                }


                                // =================================
                                // GET CURRENT USER
                                // =================================

                                FirebaseUser firebaseUser =
                                        mAuth.getCurrentUser();


                                if (firebaseUser == null) {

                                    signUp.setEnabled(true);


                                    Toast.makeText(
                                            SignUp.this,
                                            "User creation failed",
                                            Toast.LENGTH_SHORT
                                    ).show();


                                    return;
                                }


                                // =================================
                                // FIREBASE UID
                                // =================================

                                String userId =
                                        firebaseUser.getUid();


                                // =================================
                                // SAVE USER
                                // =================================

                                saveUserToDatabase(
                                        userName,
                                        userEmail,
                                        userId
                                );
                            }
                        }
                );
    }


    // =====================================================
    // SAVE USER TO REALTIME DATABASE
    // =====================================================

    private void saveUserToDatabase(
            String userName,
            String userEmail,
            String userId) {


        /*
         * IMPORTANT:
         *
         * Password is NOT stored here.
         *
         * Firebase Authentication manages
         * the password.
         */


        Users users =
                new Users(
                        userName,
                        userEmail,
                        DEFAULT_PROFILE_IMAGE,
                        userId
                );


        reference
                .child(userId)
                .setValue(users)
                .addOnCompleteListener(
                        new OnCompleteListener<Void>() {

                            @Override
                            public void onComplete(
                                    @NonNull Task<Void> task) {


                                if (!task.isSuccessful()) {

                                    signUp.setEnabled(true);


                                    Toast.makeText(
                                            SignUp.this,
                                            "Failed to save user information",
                                            Toast.LENGTH_LONG
                                    ).show();


                                    return;
                                }


                                // =================================
                                // SET ONLINE
                                // =================================

                                setupPresence(userId);


                                Toast.makeText(
                                        SignUp.this,
                                        "Account created successfully",
                                        Toast.LENGTH_SHORT
                                ).show();


                                // =================================
                                // OPEN USER HOME
                                // =================================

                                Intent intent =
                                        new Intent(
                                                SignUp.this,
                                                useradd.class
                                        );


                                startActivity(intent);


                                finish();
                            }
                        }
                );
    }


    // =====================================================
    // ONLINE / OFFLINE PRESENCE
    // =====================================================

    private void setupPresence(
            String userId) {


        DatabaseReference rootReference =
                FirebaseDatabase
                        .getInstance()
                        .getReference();


        DatabaseReference userReference =
                rootReference
                        .child("Users")
                        .child(userId);


        DatabaseReference connectedReference =
                rootReference
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


                        if (!Boolean.TRUE.equals(
                                connected
                        )) {

                            return;
                        }


                        // =====================================
                        // WHEN CONNECTION IS LOST
                        // =====================================

                        userReference
                                .child("online")
                                .onDisconnect()
                                .setValue(false);


                        userReference
                                .child("lastSeen")
                                .onDisconnect()
                                .setValue(
                                        ServerValue.TIMESTAMP
                                );


                        // =====================================
                        // CURRENT USER ONLINE
                        // =====================================

                        Map<String, Object> updates =
                                new HashMap<>();


                        updates.put(
                                "online",
                                true
                        );


                        updates.put(
                                "lastSeen",
                                0
                        );


                        userReference.updateChildren(
                                updates
                        );
                    }


                    @Override
                    public void onCancelled(
                            @NonNull DatabaseError error) {

                        // Nothing required
                    }
                }
        );
    }
}