package com.example.filmhunt;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Pair;
import android.util.Patterns;
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

public class Register extends AppCompatActivity {

    TextInputEditText editEmail, editPassword, editConfirmPassword;
    Button regButton;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    TextView textView, logo_text, subheader;
    ImageView logo;



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
                String email, password, confirmPassword;
                email = String.valueOf(editEmail.getText());
                password = String.valueOf(editPassword.getText());
                confirmPassword = String.valueOf(editConfirmPassword.getText());

                if(TextUtils.isEmpty(email)) {
                    Toast.makeText(Register.this, "Please enter email.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(Register.this, "Please enter a valid email.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    Toast.makeText(Register.this, "Please enter password.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if(TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(Register.this, "Please confirm your password.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                if(!password.equals(confirmPassword)){
                    Toast.makeText(Register.this, "Passwords do not match.", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Toast.makeText(Register.this, "Registration successful.",
                                            Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        Toast.makeText(Register.this, "This email is already registered.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(Register.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
            }
        });

    }
}