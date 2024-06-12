package com.example.filmhunt;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class VerifyEmailActivity extends AppCompatActivity {
    FirebaseAuth mAuth;
    FirebaseUser user;
    Handler handler;
    Runnable checkVerificationRunnable;
    Button resendEmail;
    TextView verifyDesc, VerifyDesc2, verifyDescEmail ;
    UserHelperClass helperClass = new UserHelperClass();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verifyemail);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        handler = new Handler();

        verifyDesc = findViewById(R.id.verifyDesc);
//        verifyDesc2 = findViewById(R.id.verifyDesc2);
        verifyDescEmail = findViewById(R.id.verifyDescEmail);
        resendEmail = findViewById(R.id.resendEmailBtn);

        resendEmail.setOnClickListener(v -> resendVerificationEmail());

        userEmailDesc();

        checkVerificationRunnable = new Runnable() {
            @Override
            public void run() {
                user.reload().addOnSuccessListener(aVoid -> {
                    if (user.isEmailVerified()) {
                        // Email verified, redirect to dashboard
                        Intent intent = new Intent(VerifyEmailActivity.this, Dashboard.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // Email not verified, keep checking
                        handler.postDelayed(this, 3000);
                    }
                });
            }
        };

        handler.post(checkVerificationRunnable);
    }

    private void resendVerificationEmail() {
        if (user != null) {
            user.sendEmailVerification().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(VerifyEmailActivity.this, "Verification email resent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VerifyEmailActivity.this, "Failed to resend verification email", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(checkVerificationRunnable);
    }


    protected void userEmailDesc() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(user.getUid());

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserHelperClass user = dataSnapshot.getValue(UserHelperClass.class);
                if (user != null) {
                    helperClass = user;
                    // UI update should be minimal and efficient
                    runOnUiThread(() -> {
                        if (helperClass.getEmail() != null && verifyDescEmail != null) {
                            verifyDescEmail.setText(helperClass.getEmail());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Firebase", "Error loading user data", databaseError.toException());
            }
        });

    }
}


