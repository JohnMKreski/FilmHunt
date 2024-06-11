package com.example.filmhunt;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    private static final String TAG = "Register";

    TextInputEditText
            regName,
            regUsername,
            editEmail,
            editPassword,
            editConfirmPassword;
    Button regButton;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView, logo_text, subheader;
    ImageView logo;

    FirebaseDatabase rootNode;
    DatabaseReference usersReference;


    @Override
    public void onStart() {
        super.onStart();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Check if user is signed in. If logged in, open Dashboard
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), Dashboard.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Hooks to all xml elements in activity_register.xml
        editEmail = findViewById(R.id.email);
        editPassword = findViewById(R.id.password);
        editConfirmPassword = findViewById(R.id.confirmPass);
        regButton = findViewById(R.id.registerBtn);
        mAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);

        logo = findViewById(R.id.logo);
        logo_text = findViewById(R.id.logo_text);
        subheader = findViewById(R.id.subheader);

        regName = findViewById(R.id.fullname);
        regUsername = findViewById(R.id.username);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this, Login.class);

                Pair[] pairs = new Pair[3];
                pairs[0] = new Pair<View, String>(logo, "logo_image");
                pairs[1] = new Pair<View, String>(logo_text, "logo_text");
                pairs[2] = new Pair<View, String>(subheader, "subheader");


                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(Register.this,pairs);
                startActivity(intent, options.toBundle());
            }
        });


        regButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String name = String.valueOf((regName.getText()));
                String username = String.valueOf(regUsername.getText());
                String email = String.valueOf(editEmail.getText());
                String password = String.valueOf(editPassword.getText());
                String confirmPassword = String.valueOf(editConfirmPassword.getText());

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(email) ||
                        TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
                    Toast.makeText(Register.this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(Register.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if (!UserHelperClass.validateEmail(email, Register.this) || !UserHelperClass.validatePassword(password, Register.this)) {
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(Register.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                                    String uid = firebaseUser.getUid();

                                    rootNode = FirebaseDatabase.getInstance();
                                    usersReference = rootNode.getReference("users");

                                    UserHelperClass helperClass = new UserHelperClass(name, username, email, uid, password);
                                    usersReference.child(uid).setValue(helperClass)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {

                                                        // Send email verification
                                                        sendEmailVerification(firebaseUser);
                                                        Log.d(TAG, "User Reference: " + usersReference.toString());

                                                        Toast.makeText(Register.this, "User registered successfully. Please check your email for verification.", Toast.LENGTH_LONG).show();
                                                        Intent intent = new Intent(Register.this, VerifyEmailActivity.class);
                                                        startActivity(intent);
                                                        finish();
                                                    } else {
                                                        Toast.makeText(Register.this, "Failed to register user.", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(Register.this, "This email is already registered.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Register.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }

            private void sendEmailVerification(FirebaseUser user) {
                if (user != null) {
                    user.sendEmailVerification().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Register.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }


        });
    }
}