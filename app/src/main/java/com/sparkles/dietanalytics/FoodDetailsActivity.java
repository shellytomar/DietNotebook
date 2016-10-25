package com.sparkles.dietanalytics;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.sparkles.dietanalytics.common.Constants;
import com.sparkles.dietanalytics.model.Diet;
import com.sparkles.dietanalytics.model.FoodIntake;
import com.sparkles.dietanalytics.model.User;
import com.sparkles.dietanalytics.rest.RestAPIClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodDetailsActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    FoodIntake foodIntake;
    List<FoodIntake> foodItems;
    Diet diet;
    User user;

    TextView breakfastValue, lunchValue, snackValue, dinnerValue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_details);
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

        breakfastValue = (TextView)findViewById(R.id.bfValue);
        lunchValue = (TextView)findViewById(R.id.lunchValue);
        snackValue = (TextView)findViewById(R.id.snackValue);
        dinnerValue = (TextView)findViewById(R.id.dinnerValue);

        if (savedInstanceState != null) {
            user = (User) savedInstanceState.getSerializable(UserDetailsActivity.USER);
            diet = (Diet) savedInstanceState.getSerializable(Constants.DIET);

        } else {
            user = (User) getIntent().getSerializableExtra(UserDetailsActivity.USER);
            diet = (Diet) getIntent().getSerializableExtra(Constants.DIET);
        }

        getFoodItems();

    }

    public void displayFood(List<FoodIntake> foodItems){
        StringBuffer breakfastBuffer = new StringBuffer();
        StringBuffer lunchBuffer = new StringBuffer();
        StringBuffer snackBuffer = new StringBuffer();
        StringBuffer dinnerBuffer = new StringBuffer();

        for(FoodIntake foodIntake: foodItems) {

            if (foodIntake.getMealType().equalsIgnoreCase("breakfast")) {
                breakfastBuffer.append(foodIntake.getItem() + " - ");
                breakfastBuffer.append(foodIntake.getCalories() + "\n");
            } else if (foodIntake.getMealType().equalsIgnoreCase("lunch")) {
                lunchBuffer.append(foodIntake.getItem() + " - ");
                lunchBuffer.append(foodIntake.getCalories() + "\n");
            } else if (foodIntake.getMealType().equalsIgnoreCase("snack")) {
                snackBuffer.append(foodIntake.getItem() + " - ");
                snackBuffer.append(foodIntake.getCalories() + "\n");
            } else if (foodIntake.getMealType().equalsIgnoreCase("dinner")) {
                dinnerBuffer.append(foodIntake.getItem() + " - ");
                dinnerBuffer.append(foodIntake.getCalories() + "\n");
            }
        }
        breakfastValue.setText(breakfastBuffer);
        lunchValue.setText(lunchBuffer);
        snackValue.setText(snackBuffer);
        dinnerValue.setText(dinnerBuffer);
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable(UserDetailsActivity.USER, user);
        bundle.putSerializable(Constants.DIET, diet);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i("id is:",""+id);
        System.out.print(id);
        System.out.print(R.id.action_steps);
        if (id == R.id.action_steps) {
            Intent i = new Intent(FoodDetailsActivity.this,StepCountActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_addFood) {
            Intent i = new Intent(FoodDetailsActivity.this,FoodDetailsActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_articles) {
            Intent i = new Intent(FoodDetailsActivity.this,ArticlesActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_reports) {
            Intent i = new Intent(FoodDetailsActivity.this, ReportActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_logout) {
            Intent i = new Intent(FoodDetailsActivity.this, SignUpActivity.class);
            startActivity(i);
        }else if (id == R.id.exit) {
        }else if(id == R.id.home){
            Intent i = new Intent(FoodDetailsActivity.this, Dashboard.class);
            i.putExtra(UserDetailsActivity.USER, user);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void addFood(View view) {
        final CharSequence[] items = {"Breakfast", "Lunch", "Snack", "Dinner"};
        AlertDialog.Builder builder = new AlertDialog.Builder(FoodDetailsActivity.this);
        builder.setTitle("Select a Meal Type!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                foodIntake = new FoodIntake();
                foodIntake.setMealType(items[item].toString());
                Intent intent = new Intent(FoodDetailsActivity.this, AddItemActivity.class);
                intent.putExtra("FoodIntake", String.valueOf(foodIntake));
                intent.putExtra(Constants.DIET, diet);
                intent.putExtra(UserDetailsActivity.USER, user);
                intent.putExtra(Constants.FOODINTAKE,foodIntake);
                startActivity(intent);
            }
        });
        builder.show();
    }

    private void getFoodItems(){
        Call<List<FoodIntake>> call = RestAPIClient.get().getDietItems(diet.getDietId());
        call.enqueue(new Callback<List<FoodIntake>>() {
            @Override
            public void onResponse(Call<List<FoodIntake>> call, Response<List<FoodIntake>> response) {
                Log.i("Food Items","Fetched successfully");
                List<FoodIntake> list = response.body();
                foodItems = list;
                Log.i("food item size"," "+foodItems.size());
                Log.i("food item size"," "+list.size());
                displayFood(foodItems);
            }

            @Override
            public void onFailure(Call<List<FoodIntake>> call, Throwable t) {
                Log.i("Food Items","couldn't fetch");
            }
        });
    }

}