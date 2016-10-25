package com.sparkles.dietanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class SignUpActivity extends AppCompatActivity{

    Button btn_sign_up;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);


//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        drawer.setDrawerListener(toggle);
//        toggle.syncState();
//
//        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        //navigationView.getMenu().findItem(R.id.action_signin).setVisible(false);
//        navigationView.setNavigationItemSelectedListener(this);

        btn_sign_up = (Button) findViewById(R.id.signUp);
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.signUp) {
                    Intent i = new Intent(SignUpActivity.this, UserDetailsActivity.class);
                    startActivity(i);
                }
            }
        });

    }

    public void onSignIn(View view) {
        if (view.getId() == R.id.signIn) {
            Intent i = new Intent(SignUpActivity.this, SignInActivity.class);
            startActivity(i);
        }
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onStop() {
        super.onStop();


    }
}
