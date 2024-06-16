package com.example.filmhunt;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.preference.PreferenceFragmentCompat;

import com.example.filmhunt.Models.ImdbResponse;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Properties;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class Settings extends BaseActivity {

    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_THEME = "is_night_mode";


    Button watch;
    Button hist;
    Button cache;
    Button contact;

    SwitchCompat themeSwitch;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setupNavigationDrawer(R.layout.activity_settings, R.id.toolbar, R.id.nav_view, R.id.settings, R.id.nav_head_userDetails);

        themeSwitch = findViewById(R.id.themeSwitch);

        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        boolean isNightMode = preferences.getBoolean(KEY_THEME, false);

        themeSwitch = findViewById(R.id.themeSwitch);
        themeSwitch.setChecked(isNightMode);

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }

                // Save the preference
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(KEY_THEME, isChecked);
                editor.apply();
            }
        });

        watch = findViewById(R.id.clearWatchButton);
        hist = findViewById(R.id.clearHistButton);
        cache = findViewById(R.id.clearCacheButton);
        contact = findViewById(R.id.contactButton);

        EditText messageInput = findViewById(R.id.message_input);

        watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WatchlistHelperClass helper = new WatchlistHelperClass(user.getUid());

                helper.getWatchlist(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            ImdbResponse.Movie movie = snapshot.getValue(ImdbResponse.Movie.class);

                            helper.removeMovie(movie.id);
                            helper.removeMovie(movie.getId());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Log.e("WatchlistActivity", "Error fetching watchlist", databaseError.toException());
                    }
                });


            }
        });


        hist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO waiting until other stuff is ready
            }
        });

        cache.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO waiting until other stuff is ready
            }
        });


        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new ContactDialog(Settings.this, message -> {

                    if (messageInput.getText().toString().isEmpty()) {

                        Toast.makeText(Settings.this, "Please enter a message",
                                Toast.LENGTH_SHORT).show();

                    } else{

                        Properties properties = new Properties();
                        properties.put("mail.smtp.auth", "true");
                        properties.put("mail.smtp.starttls.enable", "true");
                        properties.put("mail.smtp.host", "live.smtp.mailtrap.io");
                        properties.put("mail.smtp.port", "587");

                        Authenticator authenticator = new Authenticator() {
                            protected PasswordAuthentication getPasswordAuthentication() {
                                return new PasswordAuthentication("api", "4286e60303b64d4c39a662e2c21fa686");
                            }
                        };
                        Session session = Session.getInstance(properties, authenticator);

                        try {
                            //create a MimeMessage object
                            Message mess = new MimeMessage(session);
                            //set From email field
                            mess.setFrom(new InternetAddress("demomailtrap.com"));
                            //set To email field
                            mess.setRecipients(Message.RecipientType.TO,
                                    InternetAddress.parse("camnc2003@gmail.com"));
                            //set email subject field
                            mess.setSubject("New FilmHunt USer Message");
                            //set the content of the email message
                            mess.setText(message);
                            //send the email message
                            Transport.send(mess);

                        } catch (MessagingException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }
        });
    }

}