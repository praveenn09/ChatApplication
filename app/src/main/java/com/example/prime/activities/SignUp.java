package com.example.prime.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.prime.R;
import com.example.prime.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.makeramen.roundedimageview.RoundedImageView;

import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SignUp extends AppCompatActivity {
    private FirebaseAuth mAuth;
    //RoundedImageView profileImage;
    RoundedImageView chooseImageButton;
    EditText name,email,password,confirmPassword;
    MaterialButton signUp;
    TextView loginback,imagetext;
    Uri imageURI;
    String imageuri;
    FirebaseDatabase database;
    DatabaseReference reference;
    FirebaseStorage storage=FirebaseStorage.getInstance();
    //StorageReference storageReference;

    // Initialize the Firebase Database reference



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        name=findViewById(R.id.inputName);
      //  profileImage=findViewById(R.id.imageProfile);
        chooseImageButton=findViewById(R.id.imageProfile);
        email=findViewById(R.id.inputEmail);
        imagetext=findViewById(R.id.image);
        loginback=findViewById(R.id.backLogin);
        loginback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i=new Intent(SignUp.this,SignIn.class);
                startActivity(i);
                finish();
            }
        });
        password=findViewById(R.id.inputPassword);
        confirmPassword=findViewById(R.id.inputRePassword);
        signUp=findViewById(R.id.button);
        
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
                String nm=name.getText().toString();
                String em=email.getText().toString();
                String pw=password.getText().toString();
                String cp=confirmPassword.getText().toString();
                Pattern pattern = Pattern.compile(emailRegex);
                Matcher matcher = pattern.matcher(em);
                if(nm.isEmpty()){
                    name.setError("Enter Name");
                }
                if(em.isEmpty()){
                    email.setError("Enter Email");
                }
                else {

                    if (!matcher.matches()) {
                        email.setError("Enter Valid Email");

                    }
                }
                if (pw.length()<6) {
                    password.setError("Enter Strong Password");

                }
                if (!pw.equals(cp)){

                    confirmPassword.setError("Please Enter Correct Password");

                }

               if(!nm.isEmpty() && !em.isEmpty() && matcher.matches() && pw.length()>=6 && pw.equals(cp))
                {
                    nameStore.myname=nm;
                    performSignUp(nm,em,pw);
                }
                else {
                    Toast.makeText(SignUp.this,"Please enter correct details",Toast.LENGTH_SHORT).show();
                }
            }
        });//button Listener
        chooseImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagetext.setText("");
                Intent intent=new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
     startActivityForResult(Intent.createChooser(intent,"Select Picture"),10);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 10) {
            if (data != null) {
            imageURI=data.getData();
            chooseImageButton.setImageIcon(Icon.createWithContentUri(imageURI));


            }
        }
    }
    private void performSignUp(String name ,String em, String pw) {
        mAuth.createUserWithEmailAndPassword(em, pw)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                           String id=task.getResult().getUser().getUid();
                            Toast.makeText(SignUp.this, "id ."+id,
                                    Toast.LENGTH_SHORT).show();
                          StorageReference storageReference=storage.getReference().child("Upload").child(id);
                            if(imageURI!=null){
                         storageReference.putFile(imageURI).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                             @Override
                             public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                 if(task.isSuccessful()){
                                     storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                         @Override
                                         public void onSuccess(Uri uri) {
                                            imageuri=uri.toString();
                                             Users users=new Users(name,em,pw,imageuri,id);
                                             database=FirebaseDatabase.getInstance();
                                             reference=database.getReference("Users");
                                             reference.child(id).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                 @Override
                                                 public void onComplete(@NonNull Task<Void> task) {
                                                     Toast.makeText(SignUp.this, "Success.",
                                                             Toast.LENGTH_SHORT).show();
                                                 }
                                             });
                                         }
                                     });
                                 }
                             }
                         });

                            }//imageURI
                            else{
                                imageuri = "https://firebasestorage.googleapis.com/v0/b/chatting-application-dcdf5.appspot.com/o/profile.png?alt=media&token=f436be3c-d8c0-473e-acd6-04ab23b40281";
                                Users users=new Users(name,em,pw,imageuri,id);
                                database=FirebaseDatabase.getInstance();
                                reference=database.getReference("Users");
                                reference.child(id).setValue(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(SignUp.this, "Success.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });


                            }
                            Toast.makeText(SignUp.this, "Authentication Success.",
                                    Toast.LENGTH_SHORT).show();
                            Intent i=new Intent(SignUp.this,useradd.class);
                            startActivity(i);
                        }//first if
                        else {

                            Toast.makeText(SignUp.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }

                });
    }//performSignUp


}