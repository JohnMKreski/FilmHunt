package com.example.filmhunt;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class Dashboard extends BaseActivity {
    Button fireBtn;

        @Override
        protected void onCreate (Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setupNavigationDrawer(R.layout.activity_dashboard, R.id.toolbar, R.id.nav_view, R.id.dashboard, R.id.userDetails);

            //firebase logout
            fireBtn = findViewById(R.id.logout);
            fireBtn.setOnClickListener(v -> {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            });

//            username();
        }

//        public void username() {
//            String fbUsername = getInstance(user).toString();
//            String Username = helperClass.getUsername();
//            Log.d(Username, "Username:"+helperClass.getUsername()+".");
//        }
}
