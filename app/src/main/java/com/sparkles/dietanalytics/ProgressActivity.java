package com.sparkles.dietanalytics;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;

import com.sparkles.dietanalytics.common.CommonUtils;
import com.sparkles.dietanalytics.common.Constants;
import com.sparkles.dietanalytics.model.Diet;
import com.sparkles.dietanalytics.model.User;
import com.sparkles.dietanalytics.rest.RestAPIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProgressActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    int res1;
    User user;
    Diet diet;

    double caloriesBurnedPerMile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress);

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


        if (savedInstanceState != null) {
            user = (User) savedInstanceState.getSerializable(UserDetailsActivity.USER);
        } else {
            user = (User) getIntent().getSerializableExtra(UserDetailsActivity.USER);
            diet = (Diet) getIntent().getSerializableExtra(Constants.DIET);
        }

        //getDiet();


    }

    public void calculateCaloriedBurned() {
        ProgressBar stepCountProgress = (ProgressBar) findViewById(R.id.circle_progress_bar);
        int stepcount = diet.getStepCount();
        res1 = (int) Math.round(stepcount / 100.0);
        System.out.println("************RES1************:" + res1);

        stepCountProgress.setProgress(res1);

        ProgressBar caloriesburntprogress = (ProgressBar) findViewById(R.id.calories_burnt_circle_progress_bar);

        double weigthcalculation = 9.99 * user.getWeight();
        System.out.println("***************weigthcalculation********************" + weigthcalculation);


        double heightcalculation = 6.25 * user.getHeight();
        System.out.println("***************heightcalculation********************" + heightcalculation);

        double agecalculation = 4.92 * user.getAge();
        System.out.println("**************agecalculation********************" + agecalculation);
        int caloriesburnt = 0;
        if (user.getGender().equalsIgnoreCase(Constants.FEMALE))
            caloriesburnt = (int) Math.round(weigthcalculation + heightcalculation - agecalculation - 161);
        else
            caloriesburnt = (int) Math.round(weigthcalculation + heightcalculation - agecalculation + 5);

        System.out.println("***************CALORIES BURNT********************" + caloriesburnt);

        /*steps converted into calories*/
        caloriesBurnedPerMile = ((user.getWeight() * 0.45) * 0.57);
        System.out.println("*****caloriesBurnedPerMile*****:" + caloriesBurnedPerMile);



        double averageStrideLength = (user.getHeight() * 0.39) * 0.413;
        System.out.println("*****averageStrideLength*****:" + averageStrideLength);

        double feetPerStride = averageStrideLength / 12;
        System.out.println("*****feetPerStride*****:" + feetPerStride);

        double noOfStepsPerMile = 5280 / feetPerStride;
        System.out.println("*****noOfStepsPerMile*****:" + noOfStepsPerMile);


        // Calories burned per mile / no of Steps per Mile = Calories per step
        double coversionFactor = caloriesBurnedPerMile / noOfStepsPerMile;
        System.out.println("*****coversionFactor*****:" + coversionFactor);

        int noOfSteps = diet.getStepCount();
        //Calories Burned = Steps * Calories per Step
        int caloriesBurned = (int) Math.round(noOfSteps * coversionFactor);
        System.out.println("*****caloriesBurned using Step *****:" + caloriesBurned);
        //System.out.println("*****RES1*****:" + res1);

        int totalCaloriesBurnt = caloriesburnt + caloriesBurned;
        System.out.println("*****Total Calories *****:" + totalCaloriesBurnt);

        int total = (int) Math.round(totalCaloriesBurnt / 100.0);

        caloriesburntprogress.setProgress(total);
    }

    private void getDiet() {
        Log.i("user id "+user.getUserId(),CommonUtils.getTodaysDate());
        Call<Diet> call = RestAPIClient.get().getUserDiet(user.getUserId(), CommonUtils.getTodaysDate());

        call.enqueue(new Callback<Diet>() {
            @Override
            public void onResponse(Call<Diet> call, Response<Diet> response) {
                Log.i("User", "Successfully fetched user diet");
                diet = response.body();
                calculateCaloriedBurned();
            }

            @Override
            public void onFailure(Call<Diet> call, Throwable t) {
                Log.i("Error ", "Cannot register user");
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(Constants.DIET, diet);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        return false;
    }
}
