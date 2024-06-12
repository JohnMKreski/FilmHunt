package com.example.filmhunt;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth auth;
    FirebaseUser user;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TextView textView, nav_head_userDetails;
    UserHelperClass helperClass = new UserHelperClass();
    protected Handler handler;

    protected Runnable checkVerificationRunnable;


    //doing a check to see if user is initialized before using drawer
    protected void checkAuthentication() {
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        checkAuthentication();

//        nav_head_userDetails = findViewById(R.id.nav_head_userDetails);

        retrieveAndSetUsername();
    }

    protected void retrieveAndSetUsername() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users")
                .child(user.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserHelperClass user = dataSnapshot.getValue(UserHelperClass.class);
                String welcome = "Welcome, ";
                if (user != null) {
                    helperClass = user;
                    // UI update should be minimal and efficient
                    runOnUiThread(() -> {
                        if (helperClass.getName() != null && nav_head_userDetails != null) {
                            nav_head_userDetails.setText(welcome + helperClass.getName());
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

    protected void setupNavigationDrawer(int layoutId, int toolbarId, int navViewId, int activityId, int userDetailsId) {
        setContentView(layoutId);

        TitleHelperClass.setToolbarTitle(this, toolbarId, activityId);

        toolbar = findViewById(toolbarId);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(navViewId);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

//        textView = findViewById(R.id.nav_head_userDetails);
//        if (user != null) {
//            textView.setText(user.getEmail());
////            //this handles the visibility of the email
//            textView.setVisibility(View.GONE);
//        } else {
//            textView.setText("");
//        }

        View headerView = navigationView.getHeaderView(0);
        nav_head_userDetails = headerView.findViewById(R.id.nav_head_userDetails);
}

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_settings) {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        }

        if (id == R.id.nav_dashboard) {
            Intent intent = new Intent(this, Dashboard.class);
            startActivity(intent);
        }

        if (id == R.id.nav_userSettings) {
            Intent intent = new Intent(this, UserAccount.class);
            startActivity(intent);
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    protected void sendEmailVerification(FirebaseUser user) {
//        if (user != null) {
//            Log.d("BaseActivity", "Sending email verification to: " + user.getEmail());
//            user.sendEmailVerification().addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    Log.d("BaseActivity", "Verification email sent successfully");
//                    Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show();
//                } else {
//                    Log.d("BaseActivity", "Failed to send verification email", task.getException());
//                    Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }

    protected void reauthenticate(String email, String password, Runnable onReauthSuccess, Runnable onReauthFailure) {
        Log.d("BaseActivity", "Re-authenticating user with email: " + email);

        AuthCredential credential = EmailAuthProvider.getCredential(email, password);
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                onReauthSuccess.run();
                Log.d("BaseActivity", "Re-authentication successful");
            } else {
                onReauthFailure.run();
                Log.d("BaseActivity", "Re-authentication failed", task.getException());
            }
        });
    }

//    protected void sendVerificationEmailToNewEmail(FirebaseUser user, String newEmail) {
//        FirebaseAuth auth = FirebaseAuth.getInstance();
//        auth.fetchSignInMethodsForEmail(newEmail).addOnCompleteListener(task -> {
//            if (task.isSuccessful()) {
//                boolean isNewEmail = task.getResult().getSignInMethods().isEmpty();
//                if (isNewEmail) {
//                    user.updateEmail(newEmail).addOnCompleteListener(updateTask -> {
//                        if (updateTask.isSuccessful()) {
//                            user.sendEmailVerification().addOnCompleteListener(verificationTask -> {
//                                if (verificationTask.isSuccessful()) {
//                                    Log.d("BaseActivity", "Verification email sent to: " + newEmail);
//                                    Toast.makeText(this, "Verification email sent", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Log.d("BaseActivity", "Failed to send verification email: " + verificationTask.getException());
//                                    Toast.makeText(this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
//                                }
//                            });
//                        } else {
//                            Log.d("BaseActivity", "Failed to update email before verification: " + updateTask.getException());
//                            Toast.makeText(this, "Failed to update email before verification", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//                    Log.d("BaseActivity", "Email is already in use by another account");
//                    Toast.makeText(this, "Email is already in use by another account", Toast.LENGTH_SHORT).show();
//                }
//            } else {
//                Log.d("BaseActivity", "Failed to fetch sign-in methods: " + task.getException());
//                Toast.makeText(this, "Failed to verify if email is already in use", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


    protected void checkEmailVerification(FirebaseUser user, Runnable onSuccess, Runnable onFailure) {
        handler = new Handler();
        checkVerificationRunnable = new Runnable() {
            @Override
            public void run() {
                user.reload().addOnSuccessListener(aVoid -> {
                    Log.d("BaseActivity", "Reloading user to check email verification status");
                    if (user.isEmailVerified()) {
                        Log.d("BaseActivity", "Email verified successfully");
                        handler.removeCallbacks(this);// Stop further checks
                        onSuccess.run();
                    } else {
                        Log.d("BaseActivity", "Waiting for user to click verification link...");
                        handler.postDelayed(this, 3000);
                    }
                }).addOnFailureListener(e -> {
                    Log.e("BaseActivity", "Failed to reload user", e);
                    handler.removeCallbacks(this);
                    onFailure.run();
                });
            }
        };
        handler.post(checkVerificationRunnable); // Start the first check immediately
    }


    protected Dialog showSimpleDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notification");
        builder.setMessage(message);
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.show();
        return dialog;
    }


}
