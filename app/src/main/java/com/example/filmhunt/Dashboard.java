package com.example.filmhunt;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth auth;
    Button fireBtn;
    FirebaseUser user;
    TextView textView;
//    Toolbar toolbar;
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;



        @Override
        protected void onCreate (Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            EdgeToEdge.enable(this);
            setContentView(R.layout.activity_dashboard);

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.dashboard), (v, insets) -> {
                Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                return insets;
            });

            //firebase
            auth = FirebaseAuth.getInstance();
            fireBtn = findViewById(R.id.logout);
            textView = findViewById(R.id.userDetails);
            user = auth.getCurrentUser();

            if (user == null) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            } else {
                textView.setText(user.getEmail());
            }

            //firebase logout
            fireBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    finish();
                }
            });

            //Toolbar
            TitleHelperClass.setToolbarTitle(this, R.id.toolbar, R.id.dashboard);

            toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            //Nav Drawer
            drawerLayout = findViewById(R.id.drawer_layout);
            navigationView = findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawerLayout, toolbar,
                    R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }

        //Page Settings
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
        }
