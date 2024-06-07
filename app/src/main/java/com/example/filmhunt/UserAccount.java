package com.example.filmhunt;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserAccount extends BaseActivity {
    private static final String TAG = "UserAccount";
    TextView nameText, newNameText, usernameText, emailText, uidText;
    EditText newUsernameText, newEmailText, oldPasswordText, newPasswordText, newPassword2Text;
    Button update_nameBtn, emailButton, passwordButton, deleteButton, update_usernameBtn, updatePassBtn;
    DatabaseReference usersReference;
    UserHelperClass helperClass = new UserHelperClass();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_useraccount);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.useraccount), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupNavigationDrawer(R.layout.activity_useraccount, R.id.toolbar, R.id.nav_view, R.id.useraccount, R.id.userDetails);


        // Firebase Auth is already initialized in BaseActivity
//        if (user == null) {
//            Intent intent = new Intent(getApplicationContext(), Login.class);
//            startActivity(intent);
//            finish();
//            return;
//        }
        initializeViews();
        setUpdateClickListener();
        retrieveUserData();
    }

    public void initializeViews() {
        usernameText = findViewById(R.id.username);
        newUsernameText = findViewById(R.id.newUsernameText);
        nameText = findViewById(R.id.name);
        newNameText = findViewById(R.id.newNameText);
        emailText = findViewById(R.id.email);
//        emailText.setText(email);
        uidText = findViewById(R.id.uid);
//        uidText.setText(uid);
        newEmailText = findViewById(R.id.newEmailText);

//        oldPasswordText = findViewById(R.id.oldPassword);
//        newPasswordText = findViewById(R.id.newPassword);
//        newPassword2Text = findViewById(R.id.newPassword2);

        update_nameBtn = findViewById(R.id.update_nameBtn);
        emailButton = findViewById(R.id.emailButton);
        passwordButton = findViewById(R.id.passwordButton);
        deleteButton = findViewById(R.id.deleteButton);
        update_usernameBtn = findViewById(R.id.update_usernameBtn);
//        updatePassBtn = findViewById(R.id.user_updatePasswordBtn);

        uidText.setText(user.getUid());
    }

    //Function to retrieve User Info
    public void retrieveUserData() {
        String uid = user.getUid();
//        String email = user.getEmail();
//        String mailID = email.replace("@", "").replace(".", "");
//        Log.d(TAG, "Formatted mailID: " + mailID);

        //retrieve data ref to specific user
        usersReference = FirebaseDatabase.getInstance().getReference("users").child(uid);
//      Log.d(TAG, "Database Reference Path: " + usersReference.toString());

        usersReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//            Log.d(TAG, "DataSnapshot: " + snapshot.toString());
                helperClass = snapshot.getValue(UserHelperClass.class);
                if (helperClass != null) {
//                    Log.d(TAG, "Name: " + helperClass.getName());
//                    Log.d(TAG, "Username: " + helperClass.getUsername());
//                    Log.d(TAG, "UID: " + user.getUid());
                    nameText.setText(helperClass.getName());
                    usernameText.setText(helperClass.getUsername());
                    emailText.setText(user.getEmail());
                    uidText.setText(user.getUid());

                    //Dynamically matches the hints to the users info
//                    newUsernameText.setHint(helperClass.getUsername());
//                    newNameText.setHint(helperClass.getName());
                }
//                else{
//                    Log.d(TAG, "helperClass is null");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e(TAG, "DatabaseError: " + error.getMessage());
                Toast.makeText(UserAccount.this, "Could not get User Data", Toast.LENGTH_SHORT).show();
            }
        });
    }


    //Update Function
    public void setUpdateClickListener() {
        //Full Name
        update_nameBtn.setOnClickListener(v -> {
            String currentName = nameText.getText().toString();
            String newName = newNameText.getText().toString();

            if (newName.isEmpty()) {
                Toast.makeText(UserAccount.this, "Please enter a new name", Toast.LENGTH_SHORT).show();
            } else {
                new ConfirmationDialog(UserAccount.this, "name", currentName, newName, () -> {
                    // Update to Realtime DB Here
                    usersReference.child("name").setValue(newName)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(UserAccount.this, "Name updated successfully", Toast.LENGTH_SHORT).show();
                                nameText.setText(newName); // Update displayed name
                                // Reset input field
                                newNameText.setText("");
                            })
                            .addOnFailureListener(e -> Toast.makeText(UserAccount.this, "Name update failed", Toast.LENGTH_SHORT).show());
                }).show();
            }
        });

        //Username
        update_usernameBtn.setOnClickListener(v -> {
            String currentUsername = usernameText.getText().toString();
            String newUsername = newUsernameText.getText().toString();

            if (newUsername.isEmpty()) {
                Toast.makeText(UserAccount.this, "Please enter a new username", Toast.LENGTH_SHORT).show();
            } else {
                new ConfirmationDialog(UserAccount.this, "username", currentUsername, newUsername, () -> {
                    // Perform the update directly here
                    usersReference.child("username").setValue(newUsername)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(UserAccount.this, "Username updated successfully", Toast.LENGTH_SHORT).show();
                                usernameText.setText(newUsername); // Update displayed username
                                // Reset input field
                                newUsernameText.setText("");
                            })
                            .addOnFailureListener(e -> Toast.makeText(UserAccount.this, "Username update failed", Toast.LENGTH_SHORT).show());
                }).show();
            }
        });

        // Email
        emailButton.setOnClickListener(v -> {
            String currentEmail = emailText.getText().toString();
            String newEmail = newEmailText.getText().toString();

            if (newEmail.isEmpty()) {
                Toast.makeText(UserAccount.this, "Please enter a new email", Toast.LENGTH_SHORT).show();
            } else {
                Log.d("UserAccount", "Attempting to update email from " + currentEmail + " to " + newEmail);
                new ReauthDialog(UserAccount.this, password -> {
                    Log.d("UserAccount", "Password entered for re-authentication: " + password);
                    reauthenticate(currentEmail, password, () -> {
                        Log.d("UserAccount", "Re-authentication successful");
                    new ConfirmationDialog(UserAccount.this, "email", currentEmail, newEmail, () -> {
                        Log.d("UserAccount", "Confirmed email update");
                        // Perform the email update action in Firebase Authentication
                        user.updateEmail(newEmail)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("UserAccount", "Email update in Firebase Auth successful");
                                    // Update email in the Firebase Realtime Database
                                    usersReference.child("email").setValue(newEmail)
                                            .addOnSuccessListener(aVoid1 -> {
                                                Log.d("UserAccount", "Email update in Firebase Realtime Database successful");
                                                Toast.makeText(UserAccount.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                                                emailText.setText(newEmail); // Update displayed email
                                                // Reset input field
                                                newEmailText.setText("");
                                            })
                                            .addOnFailureListener(e -> {
                                                Log.d("UserAccount", "Email update in database failed: " + e.getMessage());
                                                Toast.makeText(UserAccount.this, "Email update in database failed", Toast.LENGTH_SHORT).show();
                                            });

                                })
                                .addOnFailureListener(e -> {
                                    Log.d("UserAccount", "Email update in Firebase Auth failed: " + e.getMessage());
                                    Toast.makeText(UserAccount.this, "Email update in Firebase Auth failed", Toast.LENGTH_SHORT).show();
                                });
                    }).show();
                    }, () -> {
                        Log.d("UserAccount", "Re-authentication failed");
                        Toast.makeText(UserAccount.this, "Re-authentication failed", Toast.LENGTH_SHORT).show();
                    });
                }).show();
            }
        });

//        // Password
//        passwordButton.setOnClickListener(v -> {
//            String newPassword = newPasswordText.getText().toString();
//            String confirmPassword = confirmPasswordText.getText().toString();
//
//            if (newPassword.isEmpty() || confirmPassword.isEmpty() || !newPassword.equals(confirmPassword)) {
//                Toast.makeText(UserAccount.this, "Please enter and confirm the new password", Toast.LENGTH_SHORT).show();
//            } else {
//                String currentEmail = user.getEmail();
//                promptForPassword(currentEmail, () -> {
//                    new ConfirmationDialog(UserAccount.this, "Confirm Update", "Are you sure you want to update your password?", () -> {
//                        // Perform the password update action in Firebase Authentication
//                        user.updatePassword(newPassword)
//                                .addOnSuccessListener(aVoid -> {
//                                    Toast.makeText(UserAccount.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
//                                    // Reset input fields
//                                    newPasswordText.setText("");
//                                    confirmPasswordText.setText("");
//                                })
//                                .addOnFailureListener(e -> Toast.makeText(UserAccount.this, "Password update failed", Toast.LENGTH_SHORT).show());
//                    }).show();
//                });
//            }
//        });
//
//        // Delete Account
//        deleteButton.setOnClickListener(v -> {
//            String currentEmail = user.getEmail();
//            promptForPassword(currentEmail, () -> {
//                new ConfirmationDialog(UserAccount.this, "Confirm Delete", "Are you sure you want to delete your account?", () -> {
//                    // Perform the account deletion action in Firebase Authentication
//                    user.delete()
//                            .addOnSuccessListener(aVoid -> {
//                                // Delete user data from the Firebase Realtime Database
//                                usersReference.removeValue()
//                                        .addOnSuccessListener(aVoid1 -> {
//                                            Toast.makeText(UserAccount.this, "Account deleted successfully", Toast.LENGTH_SHORT).show();
//                                            startActivity(new Intent(UserAccount.this, LoginActivity.class));
//                                            finish();
//                                        })
//                                        .addOnFailureListener(e -> Toast.makeText(UserAccount.this, "Failed to delete user data", Toast.LENGTH_SHORT).show());
//                            })
//                            .addOnFailureListener(e -> Toast.makeText(UserAccount.this, "Account deletion failed", Toast.LENGTH_SHORT).show());
//                }).show();
//            });
//        });
//    }


    }
}


//
//        passwordButton.
//
//setOnClickListener(new View.OnClickListener() {
//    @Override
//    public void onClick (View v){
//
//        String oldPassword = String.valueOf(oldPasswordText.getText());
//        String newPassword = String.valueOf(newPasswordText.getText());
//        String newPassword2 = String.valueOf(newPassword2Text.getText());
//
//        if (oldPassword.isEmpty() || newPassword.isEmpty() || newPassword2.isEmpty()) {
//            Toast.makeText(UserAccount.this, "Please enter all password fields.", Toast.LENGTH_SHORT).show();
//        } else if (!newPassword.equals(newPassword2)) {
//            Toast.makeText(UserAccount.this, "New password does not match", Toast.LENGTH_SHORT).show();
//        } else if (oldPassword.equals(newPassword)) {
//            Toast.makeText(UserAccount.this, "New password cannot match current password", Toast.LENGTH_SHORT).show();
//        } else if (!UserHelperClass.validatePassword(newPassword, UserAccount.this)) {
//            //error message handled in UserHelperClass.java
//        } else {
//            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), oldPassword);
//            user.reauthenticate(credential).addOnCompleteListener(task -> {
//                if (task.isSuccessful()) {
//                    user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
//                        if (task1.isSuccessful()) {
//                            Toast.makeText(UserAccount.this, "Password updated", Toast.LENGTH_SHORT).show();
//                        } else {
//                            Toast.makeText(UserAccount.this, "Password update failed", Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                } else {
//                    Toast.makeText(UserAccount.this, "Current password is incorrect.", Toast.LENGTH_SHORT).show();
//                }
//            });
//        }
//    }
//});
//
//
//        deleteButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(UserAccount.this);
//                alertBuilder.setTitle("Confirm Delete");
//                alertBuilder.setMessage("Are you sure you want to delete your account?");
//                alertBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//
//                        user.delete();
//
//                        Toast.makeText(UserAccount.this, "You have deleted your " +
//                                "account", Toast.LENGTH_SHORT).show();
//
//                        Intent intent = new Intent(getApplicationContext(), Login.class);
//                        startActivity(intent);
//                        finish();
//                    }
//                });
//
//                alertBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//
//                alertBuilder.show();
//            }
//        });

