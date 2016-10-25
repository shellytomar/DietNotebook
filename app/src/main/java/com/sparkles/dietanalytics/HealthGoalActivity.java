package com.sparkles.dietanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sparkles.dietanalytics.model.User;

public class HealthGoalActivity extends AppCompatActivity {

    private User user;
    final static String LOSE_WEIGHT = "loseWeight";
    final static String MAINTAIN_WEIGHT = "maintainWeight";
    final static String GAIN_WEIGHT = "gainWeight";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            user = (User) savedInstanceState.getSerializable(UserDetailsActivity.USER);
        }else {
            user = (User) getIntent().getSerializableExtra(UserDetailsActivity.USER);
        }
        setContentView(R.layout.activity_health_goal);
    }

    public void onClickActive(View v){

        if (v.getId() == R.id.loseWeight){
            user.setGoalType(HealthGoalActivity.LOSE_WEIGHT);
        }else if (v.getId() == R.id.maintainWeight){
            user.setGoalType(HealthGoalActivity.MAINTAIN_WEIGHT);
        }else if(v.getId() == R.id.gainWeight){
            user.setGoalType(HealthGoalActivity.GAIN_WEIGHT);
        }
        Intent i = new Intent(v.getContext(), ActiveTypeActivity.class);
        i.putExtra(UserDetailsActivity.USER,user);
        startActivity(i);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(UserDetailsActivity.USER, user);
    }
}
