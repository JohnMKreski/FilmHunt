package com.example.filmhunt;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserAccount extends BaseActivity {
//    private static final String TAG = "UserAccount";
    TextView nameText, userText, emailText, uidText;
    EditText newEmailText, oldPasswordText, newPasswordText, newPassword2Text;
    Button emailButton, passwordButton, deleteButton;
    DatabaseReference usersReference;
    UserHelperClass helperClass = new UserHelperClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupNavigationDrawer(R.layout.activity_useraccount, R.id.toolbar, R.id.nav_view, R.id.dashboard, R.id.userDetails);

        // Firebase Auth is already initialized in BaseActivity
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
            return;
        }

        String email = user.getEmail();
        String mailID = email.replace("@", "").replace(".", "");
//        Log.d(TAG, "Formatted mailID: " + mailID);

        //Firebase RealtimeDB
        String uid = user.getUid();
        //retrieve data ref to specific user
        usersReference = FirebaseDatabase.getInstance().getReference("users").child(uid);
//        Log.d(TAG, "Database Reference Path: " + usersReference.toString());

        userText = findViewById(R.id.username);
        nameText = findViewById(R.id.name);
        emailText = findViewById(R.id.email);
        emailText.setText(email);
        uidText = findViewById(R.id.uid);
        uidText.setText(uid);
        newEmailText = findViewById(R.id.newEmailText);
        oldPasswordText = findViewById(R.id.oldPassword);
        newPasswordText = findViewById(R.id.newPassword);
        newPassword2Text = findViewById(R.id.newPassword2);
        emailButton = findViewById(R.id.emailButton);
        passwordButton = findViewById(R.id.passwordButton);
        deleteButton = findViewById(R.id.deleteButton);

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                Log.d(TAG, "DataSnapshot: " + snapshot.toString());
                helperClass = snapshot.getValue(UserHelperClass.class);
                if(helperClass != null) {
//                    Log.d(TAG, "Name: " + helperClass.getName());
//                    Log.d(TAG, "Username: " + helperClass.getUsername());
//                    Log.d(TAG, "UID: " + user.getUid());
                    nameText.setText(helperClass.getName());
                    userText.setText(helperClass.getUsername());
                    emailText.setText(user.getEmail());
                    uidText.setText(user.getUid());
                }
//                else{
//                    Log.d(TAG, "helperClass is null");
//                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e(TAG, "DatabaseError: " + error.getMessage());
                Toast.makeText(UserAccount.this, "Could not get User Data",
                        Toast.LENGTH_SHORT).show();
            }
        });

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String newEmail = String.valueOf(newEmailText.getText());

                if (newEmail.isEmpty()) {
                    Toast.makeText(UserAccount.this, "Please enter a new " +
                            "e-mail", Toast.LENGTH_SHORT).show();
                } else {

                    user.verifyBeforeUpdateEmail(newEmail);

                    Toast.makeText(UserAccount.this, "A verification e-mail has been sent",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String oldPassword = String.valueOf(oldPasswordText.getText());
                String newPassword = String.valueOf(newPasswordText.getText());
                String newPassword2 = String.valueOf(newPassword2Text.getText());

                if (oldPassword.isEmpty() || newPassword.isEmpty() || newPassword2.isEmpty()) {
                    Toast.makeText(UserAccount.this, "Please enter all password fields.", Toast.LENGTH_SHORT).show();
                } else if (!newPassword.equals(newPassword2)) {
                    Toast.makeText(UserAccount.this, "New password does not match", Toast.LENGTH_SHORT).show();
                } else if (oldPassword.equals(newPassword)) {
                    Toast.makeText(UserAccount.this, "New password cannot match current password", Toast.LENGTH_SHORT).show();
                } else if (!UserHelperClass.validatePassword(newPassword, UserAccount.this)) {
                    //error message handled in UserHelperClass.java
                } else {
                    AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
                    user.reauthenticate(credential).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Toast.makeText(UserAccount.this, "Password updated", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(UserAccount.this, "Password update failed", Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(UserAccount.this, "Current password is incorrect.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });


        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(UserAccount.this);
                alertBuilder.setTitle("Confirm Delete");
                alertBuilder.setMessage("Are you sure you want to delete your account?");
                alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        user.delete();

                        Toast.makeText(UserAccount.this, "You have deleted your " +
                                "account", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(getApplicationContext(), Login.class);
                        startActivity(intent);
                        finish();
                    }
                });

                alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertBuilder.show();
            }
        });
    }
}
