package com.sparkles.dietanalytics;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sparkles.dietanalytics.common.CommonUtils;
import com.sparkles.dietanalytics.common.Constants;
import com.sparkles.dietanalytics.model.Diet;
import com.sparkles.dietanalytics.model.User;
import com.sparkles.dietanalytics.rest.RestAPIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    User user;
    Diet diet;
    TextView mGoalValue, mFoodValue, mStepValue, mRemainingCalorieVal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.getMenu().findItem(R.id.action_signin).setVisible(false);
        navigationView.setNavigationItemSelectedListener(this);

        if(savedInstanceState!=null){
            user = (User) savedInstanceState.getSerializable(UserDetailsActivity.USER);
        }else {
            user = (User) getIntent().getSerializableExtra(UserDetailsActivity.USER);
        }
        mGoalValue = (TextView) findViewById(R.id.goalValue);
        mFoodValue = (TextView) findViewById(R.id.foodValue);
        mStepValue = (TextView) findViewById(R.id.stepValue);
        mRemainingCalorieVal = (TextView) findViewById(R.id.leftValue);
        getDiet();
        calculateGoalCalories();

    }

    public void onClickSteps(View view){
        Intent intent = new Intent(Dashboard.this, StepCountActivity.class);
        intent.putExtra(UserDetailsActivity.USER, user);
        intent.putExtra(Constants.DIET, diet);
        startActivity(intent);
    }


    public void onClickArticle(View view){
        Intent intent = new Intent(Dashboard.this, ArticlesActivity.class);
        intent.putExtra(UserDetailsActivity.USER, user);
        intent.putExtra(Constants.DIET, diet);
        startActivity(intent);
    }

    public void onClickFood(View view){
        Intent intent = new Intent(Dashboard.this, FoodDetailsActivity.class);
        intent.putExtra(UserDetailsActivity.USER, user);
        intent.putExtra(Constants.DIET, diet);
        startActivity(intent);
    }

    public void onClickProgress(View view){
        Intent intent = new Intent(Dashboard.this, ReportActivity.class);
        intent.putExtra(UserDetailsActivity.USER, user);
        intent.putExtra(Constants.DIET, diet);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i("id is:", "" + id);
        System.out.print(id);
        System.out.print(R.id.action_steps);
        if (id == R.id.action_logout) {
            Intent i = new Intent(Dashboard.this, SignUpActivity.class);
            startActivity(i);
        } else if (id == R.id.action_steps) {
            Intent i = new Intent(Dashboard.this,StepCountActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_addFood) {
            Intent i = new Intent(Dashboard.this,FoodDetailsActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_articles) {
            Intent i = new Intent(Dashboard.this,ArticlesActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_reports) {
            Intent i = new Intent(Dashboard.this, ReportActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.exit) {
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getDiet() {
        Log.i("user id " + user.getUserId(), CommonUtils.getTodaysDate());
        Call<Diet> call = RestAPIClient.get().getUserDiet(user.getUserId(), CommonUtils.getTodaysDate());

        call.enqueue(new Callback<Diet>() {
            @Override
            public void onResponse(Call<Diet> call, Response<Diet> response) {
                Log.i("User", "Successfully fetched user diet");
                diet = response.body();
                Log.i("Diet id", " "+diet.getDietId());
                calculateCalories();
            }

            @Override
            public void onFailure(Call<Diet> call, Throwable t) {
                Log.i("Error ", "Cannot fetch user diet");
            }
        });
    }

        @Override
        public void onSaveInstanceState(Bundle bundle) {
            super.onSaveInstanceState(bundle);
            bundle.putSerializable(UserDetailsActivity.USER, user);
            bundle.putSerializable(Constants.DIET, diet);
        }

    private void calculateCalories(){
        float calorieIntake = diet.getTotalCaloriesIntake();
        float calorieBurned = diet.getTotalCaloriesBurned();

        if(calorieIntake != 0) {
            mFoodValue.setText(String.valueOf(calorieIntake));
        }else{
            mFoodValue.setText("0");
        }
        if(calorieBurned != 0) {
            mStepValue.setText(String.valueOf(calorieBurned));
        }else{
            mStepValue.setText("0");
        }

        float remainingCalorieVal = calculateGoalCalories() - calorieIntake + calorieBurned;

        mRemainingCalorieVal.setText(String.valueOf(remainingCalorieVal));

    }

    private float calculateGoalCalories(){
        float calorie=0;
        if(user.getAge()>=4 && user.getAge()<=8){
            calorie = 1489;
        }else{
            if(user.getGender() == null || user.getGender().equalsIgnoreCase("female")){
                if(user.getActiveType().equalsIgnoreCase("sedentary") || user.getActiveType().equalsIgnoreCase("moderate")){
                    calorie=1880;
                }else if(user.getActiveType().equalsIgnoreCase("active")){
                    calorie=2280;
                }
            }else if(user.getGender().equalsIgnoreCase("male")){
                if(user.getActiveType().equalsIgnoreCase("sedentary")){
                    calorie=2120;
                }else if(user.getActiveType().equalsIgnoreCase("active") || user.getActiveType().equalsIgnoreCase("moderate")){
                    calorie=2740;
                }
            }
        }
        mGoalValue.setText(String.valueOf(calorie));
        return calorie;
    }

}