package com.sparkles.dietanalytics;

import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataSourcesRequest;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.fitness.result.DataSourcesResult;
import com.sparkles.dietanalytics.common.Constants;
import com.sparkles.dietanalytics.model.Diet;
import com.sparkles.dietanalytics.model.User;
import com.sparkles.dietanalytics.rest.RestAPIClient;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StepCountActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnDataPointListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    private static final int REQUEST_OAUTH = 1;
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private GoogleApiClient mApiClient;

    TextView mStepCountTxt;
    TextView mCalBurnTxt;
    Diet diet;
    User user;

    int res1;

    double caloriesBurnedPerMile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_count);

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
            diet = (Diet) savedInstanceState.getSerializable(Constants.DIET);
        }else {
            user = (User) getIntent().getSerializableExtra(UserDetailsActivity.USER);
            diet = (Diet) getIntent().getSerializableExtra(Constants.DIET);
        }
        mStepCountTxt = (TextView) findViewById(R.id.step_ount_txt);
        mCalBurnTxt = (TextView) findViewById(R.id.cal_burn_txt);

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }

        mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        calculateCaloriedBurned();
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if( !authInProgress ) {
            try {
                authInProgress = true;
                connectionResult.startResolutionForResult( StepCountActivity.this, REQUEST_OAUTH );
            } catch(IntentSender.SendIntentException e ) {

            }
        } else {
            Log.e("GoogleFit", "authInProgress");
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        mApiClient.connect();
    }

    @Override
    protected  void onPause(){
        super.onPause();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if( requestCode == REQUEST_OAUTH ) {
            authInProgress = false;
            if( resultCode == RESULT_OK ) {
                System.out.println("result ok=========================================");
                if( !mApiClient.isConnecting() && !mApiClient.isConnected() ) {
                    System.out.println("Coming to inside if isConnecting");
                    mApiClient.connect();
                    System.out.println("mApiClient: "+mApiClient);
                }
            } else if( resultCode == RESULT_CANCELED ) {
                Log.e( "GoogleFit", "RESULT_CANCELED" );
            }
        } else {
            Log.e("GoogleFit", "requestCode NOT request_oauth");
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        System.out.println("OnConnected======================");
        DataSourcesRequest dataSourceRequest = new DataSourcesRequest.Builder()
                .setDataTypes( DataType.TYPE_STEP_COUNT_CUMULATIVE )
                .build();

        ResultCallback<DataSourcesResult> dataSourcesResultCallback = new ResultCallback<DataSourcesResult>() {
            @Override
            public void onResult(DataSourcesResult dataSourcesResult) {
                System.out.println("ResultCallback======================");
                for( DataSource dataSource : dataSourcesResult.getDataSources() ) {
                    System.out.println("Inside for loop ==================="+dataSource);
                    if( DataType.TYPE_STEP_COUNT_CUMULATIVE.equals( dataSource.getDataType()) ) {
                        registerFitnessDataListener(dataSource, DataType.TYPE_STEP_COUNT_CUMULATIVE);
                    }
                }
            }
        };

        Fitness.SensorsApi.findDataSources(mApiClient, dataSourceRequest)
                .setResultCallback(dataSourcesResultCallback);
    }

    private void registerFitnessDataListener(DataSource dataSource, DataType dataType) {
        System.out.println("registerFitnessDataListener======================");
        SensorRequest request = new SensorRequest.Builder()
                .setDataSource( dataSource )
                .setDataType( dataType )
                        // .setSamplingRate( 1, TimeUnit.SECONDS )
                .build();

        Fitness.SensorsApi.add(mApiClient, request, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        System.out.println("status+++++++++++++++++++++++++++++++++++++ : "+status);
                        if (status.isSuccess()) {
                            Log.e( "GoogleFit", "SensorApi successfully added" );
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onDataPoint(DataPoint dataPoint) {
        System.out.println("Coming to onDataPoint=====================");
        for( final Field field : dataPoint.getDataType().getFields() ) {
            final Value value = dataPoint.getValue( field );
            System.out.println("field: "+field+"value:"+value+"================================");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mStepCountTxt.setText("No of Steps \n"+value.toString());
                    callExecutor(value.toString());
                    //Toast.makeText(getApplicationContext(), "Field: " + field.getName() + " Value: " + value, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Fitness.SensorsApi.remove( mApiClient, this )
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            mApiClient.disconnect();
                        }
                    }
                });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
        outState.putSerializable(UserDetailsActivity.USER, user);
        outState.putSerializable(Constants.DIET, diet);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        Log.i("id is:",""+id);
        System.out.print(id);
        System.out.print(R.id.action_steps);
        if (id == R.id.action_steps) {
            Intent i = new Intent(StepCountActivity.this,StepCountActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_addFood) {
            Intent i = new Intent(StepCountActivity.this,FoodDetailsActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_articles) {
            Intent i = new Intent(StepCountActivity.this,ArticlesActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_reports) {
            Intent i = new Intent(StepCountActivity.this, ReportActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_logout) {
            Intent i = new Intent(StepCountActivity.this, SignUpActivity.class);
            startActivity(i);
        }else if (id == R.id.exit) {
        }else if(id == R.id.home){
            Intent i = new Intent(StepCountActivity.this, Dashboard.class);
            i.putExtra(UserDetailsActivity.USER, user);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void callExecutor(String value){
        System.out.println("Value : in call executor=================="+value);
        ScheduledExecutorService scheduleTaskExecutor;
        scheduleTaskExecutor= Executors.newScheduledThreadPool(5);
        System.out.println("Diet Id===================="+diet.getDietId());
        diet.setStepCount(Integer.parseInt(value));

        // This schedule a task to run every 1 hours:
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            public void run() {

                Call<Diet> call = RestAPIClient.get().updateStepCount(diet);
                call.enqueue(new Callback<Diet>() {
                    @Override
                    public void onResponse(Call<Diet> call, Response<Diet> response) {
                        Log.i("Updated Step Count", "Successfully");
                        diet = response.body();
                    }

                    @Override
                    public void onFailure(Call<Diet> call, Throwable t) {
                        Log.i("Error ", "Cannot add ");
                    }
                });


                // for updating the UI
                runOnUiThread(new Runnable() {
                    public void run() {

                    }
                });
            }
        }, 0, 1, TimeUnit.HOURS);
    }

    public void calculateCaloriedBurned() {
        ProgressBar stepCountProgress = (ProgressBar) findViewById(R.id.circle_progress_bar);
        System.out.println("************diet************:" + diet.getStepCount());
        int userStepCount = diet.getStepCount();
        res1 = (int) Math.round(userStepCount / 100.0);
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
        diet.setTotalCaloriesBurned(totalCaloriesBurnt);
        mCalBurnTxt.setText("Calories Burned \n"+totalCaloriesBurnt);

        int total = (int) Math.round(totalCaloriesBurnt / 100.0);

        caloriesburntprogress.setProgress(total);
    }


}