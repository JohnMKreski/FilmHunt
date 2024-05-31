package com.example.filmhunt;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserAccount extends AppCompatActivity {

    TextView nameText;
    TextView userText;
    TextView emailText;
    TextView uidText;

    EditText newEmailText;
    EditText oldPasswordText;
    EditText newPasswordText;
    EditText newPassword2Text;

    Button emailButton;
    Button passwordButton;
    Button deleteButton;

    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    FirebaseDatabase rootNode = FirebaseDatabase.getInstance();
    DatabaseReference usersReference = rootNode.getReference("users");

    UserHelperClass helperClass = new UserHelperClass();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_useraccount);

        FirebaseUser user = mAuth.getCurrentUser();


        String email = user.getEmail();
        String uid = user.getUid();


        nameText = findViewById(R.id.name);


        userText = findViewById(R.id.username);



        String mailID = email.replace("@", "").replace(".", "");

        usersReference.child(mailID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                helperClass = snapshot.getValue(UserHelperClass.class);

                nameText.setText(helperClass.getName());
                userText.setText(helperClass.getUsername());


                //Toast.makeText(UserAccount.this, name, Toast.LENGTH_SHORT).show();
                //Toast.makeText(UserAccount.this, username, Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserAccount.this, "Could not get User Data",
                        Toast.LENGTH_SHORT).show();
            }
        });


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

                if (oldPassword.isEmpty()) {
                    Toast.makeText(UserAccount.this, "Please enter your current " +
                            "password", Toast.LENGTH_SHORT).show();
                }

                if (newPassword.isEmpty()) {
                    Toast.makeText(UserAccount.this, "Please enter a new password",
                            Toast.LENGTH_SHORT).show();
                }

                if (newPassword2.isEmpty()) {
                    Toast.makeText(UserAccount.this, "Please confirm the new password",
                            Toast.LENGTH_SHORT).show();
                }

                if (oldPassword.equals(newPassword)) {
                    Toast.makeText(UserAccount.this, "New password cannot match " +
                            "current password", Toast.LENGTH_SHORT).show();
                }

                if (!newPassword.equals(newPassword2)) {
                    Toast.makeText(UserAccount.this, "New password does not match",
                            Toast.LENGTH_SHORT).show();
                }

                if (newPassword.equals(newPassword2)) {
                    user.updatePassword(newPassword);

                    user.reload();

                    Toast.makeText(UserAccount.this, "Password has been changed",
                            Toast.LENGTH_SHORT).show();

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
