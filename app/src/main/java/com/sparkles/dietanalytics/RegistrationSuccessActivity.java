package com.sparkles.dietanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.sparkles.dietanalytics.common.Constants;
import com.sparkles.dietanalytics.model.Diet;
import com.sparkles.dietanalytics.model.User;

public class RegistrationSuccessActivity extends AppCompatActivity {

    User user;
    Diet diet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_success);
        if (savedInstanceState != null) {
            user = (User) savedInstanceState.getSerializable(UserDetailsActivity.USER);
            diet = (Diet) savedInstanceState.getSerializable(Constants.DIET);
        } else {
            user = (User) getIntent().getSerializableExtra(UserDetailsActivity.USER);
            diet = (Diet) getIntent().getSerializableExtra(Constants.DIET);
        }
        Log.i("user id in registration",""+user.getUserId());

    }

    public void onCLickContinue(View view){
        if (view.getId() == R.id.congrats_continue) {
            Intent i = new Intent(RegistrationSuccessActivity.this, Dashboard.class);
            i.putExtra(UserDetailsActivity.USER, user);
            startActivity(i);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable(UserDetailsActivity.USER, user);
        bundle.putSerializable(Constants.DIET, diet);
    }

}
