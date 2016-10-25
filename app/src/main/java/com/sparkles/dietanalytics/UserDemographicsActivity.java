package com.sparkles.dietanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.sparkles.dietanalytics.common.CommonUtils;
import com.sparkles.dietanalytics.common.Constants;
import com.sparkles.dietanalytics.model.Diet;
import com.sparkles.dietanalytics.model.User;
import com.sparkles.dietanalytics.rest.RestAPIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserDemographicsActivity extends AppCompatActivity {

    Button btnHeightWeightContinue;
    User user;
    Diet diet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_demographics);
        if(savedInstanceState!=null){
            user = (User) savedInstanceState.getSerializable(UserDetailsActivity.USER);
        }else {
            user = (User) getIntent().getSerializableExtra(UserDetailsActivity.USER);
        }
        btnHeightWeightContinue = (Button)findViewById(R.id.height_weight_continue);

        btnHeightWeightContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.height_weight_continue) {

                    EditText height = (EditText)findViewById(R.id.height);
                    EditText weight = (EditText)findViewById(R.id.weight);
                    EditText goalWeight = (EditText)findViewById(R.id.goalweight);
                    if(height.getText().length()>0)
                        user.setHeight(Float.parseFloat(height.getText().toString()));
                    if(weight.getText().length()>0)
                        user.setWeight(Float.parseFloat(weight.getText().toString()));
                    if(goalWeight.getText().length()>0)
                        user.setWeightGoal(Integer.parseInt(goalWeight.getText().toString()));

                    registerUser();


                }
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();

    }

    public void addDiet(){
        diet = new Diet();
        diet.setTotalCaloriesBurned(0);
        diet.setTotalCaloriesIntake(0);
        diet.setStepCount(0);
        diet.setUser(user);
        diet.setDate(CommonUtils.getTodaysDate());
        Call<Diet> call = RestAPIClient.get().insertUserDiet(diet);
        call.enqueue(new Callback<Diet>() {
            @Override
            public void onResponse(Call<Diet> call, Response<Diet> response) {
                Log.i("Inserted Diet","successfully");
                diet = response.body();
                Log.i("diet id",""+diet.getDietId());
                Intent i = new Intent(UserDemographicsActivity.this, RegistrationSuccessActivity.class);
                i.putExtra(UserDetailsActivity.USER, user);
                startActivity(i);
            }

            @Override
            public void onFailure(Call<Diet> call, Throwable t) {
                Log.i("cannot insert DIet","successfully");
            }
        });
    }

    public void registerUser(){
        Call<User> call = RestAPIClient.get().registerUser(user);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.i("Register User", "Successfully Registered user");
                user = response.body();
                Log.i("user id is ", "" + user.getUserId());
                addDiet();
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.i("Error ", "Cannot register user");
            }
        });
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable(UserDetailsActivity.USER, user);
        bundle.putSerializable(Constants.DIET, diet);
    }

}
