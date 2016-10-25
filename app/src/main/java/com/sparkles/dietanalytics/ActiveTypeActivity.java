package com.sparkles.dietanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sparkles.dietanalytics.model.User;

public class ActiveTypeActivity extends AppCompatActivity {

    User user;
    final static String ACTIVE_TYPE_SEDENTARY = "sedentary";
    final static String ACTIVE_TYPE_MODERATE = "moderate";
    final static String ACTIVE_TYPE_ACTIVE = "active";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            user = (User) savedInstanceState.getSerializable(UserDetailsActivity.USER);
        }else {
            user = (User) getIntent().getSerializableExtra(UserDetailsActivity.USER);
        }
        setContentView(R.layout.activity_active_type);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(UserDetailsActivity.USER, user);
    }

    public void onClickActiveButton(View v){
        if(v.getId() == R.id.sedentary)
            user.setActiveType(ActiveTypeActivity.ACTIVE_TYPE_SEDENTARY);
        else if(v.getId() == R.id.moderate)
            user.setActiveType(ActiveTypeActivity.ACTIVE_TYPE_MODERATE);
        else if(v.getId() == R.id.active)
            user.setActiveType(ActiveTypeActivity.ACTIVE_TYPE_ACTIVE);

        Intent i = new Intent(v.getContext(), UserDemographicsActivity.class);
        i.putExtra(UserDetailsActivity.USER,user);
        startActivity(i);
    }


}
