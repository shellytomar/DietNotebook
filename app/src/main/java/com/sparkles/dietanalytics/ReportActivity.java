package com.sparkles.dietanalytics;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import com.sparkles.dietanalytics.common.Constants;
import com.sparkles.dietanalytics.model.Diet;
import com.sparkles.dietanalytics.model.User;
import com.sparkles.dietanalytics.rest.RestAPIClient;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportActivity extends AppCompatActivity implements  NavigationView.OnNavigationItemSelectedListener,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener,OnChartGestureListener,com.github.mikephil.charting.listener.OnChartValueSelectedListener {

    private Button mButtonViewWeek;
    private Button mButtonViewToday;

    static TextView mReportList;

    static GoogleApiClient mGoogleApiClient;

    private LineChart mChart;

    private User user;
    private Diet diet;
    private List<Diet> diets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        if(savedInstanceState!=null){
            user = (User) savedInstanceState.getSerializable(UserDetailsActivity.USER);
            diet = (Diet) savedInstanceState.getSerializable(Constants.DIET);
        }else {
            user = (User) getIntent().getSerializableExtra(UserDetailsActivity.USER);
            diet = (Diet) getIntent().getSerializableExtra(Constants.DIET);
        }

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


        mButtonViewWeek = (Button) findViewById(R.id.btn_view_week);
        mButtonViewToday = (Button) findViewById(R.id.btn_view_today);
        mReportList = (TextView) findViewById(R.id.reportList);

        mButtonViewWeek.setOnClickListener(this);
        mButtonViewToday.setOnClickListener(this);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addConnectionCallbacks(this)
                .enableAutoManage(this, 0, this)
                .build();

        getUserDiets();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        Log.e("HistoryAPI", "onConnected");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e("HistoryAPI", "onConnectionSuspended");

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_view_week) {
            new ViewWeekStepCountTask().execute("weekly");
        } else if (v.getId() == R.id.btn_view_today) {
            new ViewWeekStepCountTask().execute("daily");
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        Log.i("id is:",""+id);
        System.out.print(id);
        System.out.print(R.id.action_steps);
        if (id == R.id.action_steps) {
            Intent i = new Intent(ReportActivity.this,StepCountActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_addFood) {
            Intent i = new Intent(ReportActivity.this,FoodDetailsActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_articles) {
            Intent i = new Intent(ReportActivity.this,ArticlesActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_reports) {
            Intent i = new Intent(ReportActivity.this, ReportActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_logout) {
            Intent i = new Intent(ReportActivity.this, SignUpActivity.class);
            startActivity(i);
        }else if (id == R.id.exit) {
        }else if(id == R.id.home){
            Intent i = new Intent(ReportActivity.this, Dashboard.class);
            i.putExtra(UserDetailsActivity.USER, user);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("HistoryAPI", "onConnectionFailed");

    }

    class ViewWeekStepCountTask extends AsyncTask<String, StringBuffer, StringBuffer> {
        protected StringBuffer doInBackground(String... params) {
            StringBuffer buff = null;
            if (params[0].equals("weekly")) {
                buff = displayLastWeeksData();

            } else if (params[0].equals("daily")) {
                buff = displayStepDataForToday();
            }
            return buff;
        }

        @Override
        protected void onProgressUpdate(StringBuffer... values) {
            super.onProgressUpdate(values);

        }

        @Override
        protected void onPostExecute(StringBuffer stringBuffer) {
            super.onPostExecute(stringBuffer);
            ReportActivity.mReportList.setText(stringBuffer);
        }

        private StringBuffer displayLastWeeksData() {
            Calendar cal = Calendar.getInstance();
            Date now = new Date();
            cal.setTime(now);
            long endTime = cal.getTimeInMillis();
            cal.add(Calendar.WEEK_OF_YEAR, -1);
            long startTime = cal.getTimeInMillis();
            StringBuffer buff = null;

            java.text.DateFormat dateFormat = DateFormat.getDateInstance();
            Log.e("History", "Range Start: " + dateFormat.format(startTime));
            Log.e("History", "Range End: " + dateFormat.format(endTime));

//Check how many steps were walked and recorded in the last 7 days
            DataReadRequest readRequest = new DataReadRequest.Builder()
                    .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                    .bucketByTime(1, TimeUnit.DAYS)
                    .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                    .build();

            DataReadResult dataReadResult = Fitness.HistoryApi.readData(ReportActivity.mGoogleApiClient, readRequest).await(1, TimeUnit.MINUTES);

            //Used for aggregated data
            if (dataReadResult.getBuckets().size() > 0) {
                Log.e("History", "Number of buckets: " + dataReadResult.getBuckets().size());
                for (Bucket bucket : dataReadResult.getBuckets()) {
                    List<DataSet> dataSets = bucket.getDataSets();
                    for (DataSet dataSet : dataSets) {
                        buff = showDataSet(dataSet);
                    }
                }
            }
//Used for non-aggregated data
            else if (dataReadResult.getDataSets().size() > 0) {
                Log.e("History", "Number of returned DataSets: " + dataReadResult.getDataSets().size());
                for (DataSet dataSet : dataReadResult.getDataSets()) {
                    buff = showDataSet(dataSet);
                }
            }
            buff.append("Range Start : " + dateFormat.format(startTime) + "\n");
            buff.append("Range End : " + dateFormat.format(endTime) + "\n");
            return buff;

        }

        private StringBuffer displayStepDataForToday() {
            DailyTotalResult result = Fitness.HistoryApi.readDailyTotal(ReportActivity.mGoogleApiClient, DataType.TYPE_STEP_COUNT_DELTA).await(1, TimeUnit.MINUTES);
            StringBuffer buff = showDataSet(result.getTotal());
            return buff;

        }

        private StringBuffer showDataSet(DataSet dataSet) {

            final StringBuffer buffer = new StringBuffer();
//         Handler mHandler = new Handler();

            Log.e("History", "Data returned for Data type: " + dataSet.getDataType().getName());
            DateFormat dateFormat = DateFormat.getDateInstance();
            DateFormat timeFormat = DateFormat.getTimeInstance();

            for (DataPoint dp : dataSet.getDataPoints()) {
                Log.e("History", "Data point:");
                Log.e("History", "\tType: " + dp.getDataType().getName());
                Log.e("History", "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
                Log.e("History", "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));
                // buffer.append("Type : "+dp.getDataType().getName());
                buffer.append("Start : " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)) + "\n");
                buffer.append("End : " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + " " + timeFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)) + "\n");

                for (Field field : dp.getDataType().getFields()) {
                    final Value value = dp.getValue(field);
                    Log.e("History", "\tField: " + field.getName() +
                            " Value: " + value);
                    buffer.append("Value : " + value + "\n");


                }
            }
            return buffer;
        }
    }

    private void setupGraph(){
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mChart = (LineChart) findViewById(R.id.linechart);
        mChart.setOnChartGestureListener(this);
        mChart.setOnChartValueSelectedListener(this);
        mChart.setDrawGridBackground(false);

        // add data
        setData();

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        // l.setPosition(LegendPosition.LEFT_OF_CHART);
        l.setForm(Legend.LegendForm.LINE);

        // no description text
        // mChart.setDescription("Demo Line Chart");
        mChart.setNoDataTextDescription("You need to provide data for the chart.");

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        // mChart.setScaleXEnabled(true);
        // mChart.setScaleYEnabled(true);

        LimitLine upper_limit = new LimitLine(5000, "Upper Limit");
        upper_limit.setLineWidth(4f);
        upper_limit.enableDashedLine(10f, 10f, 0f);
        upper_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        upper_limit.setTextSize(10f);

        LimitLine lower_limit = new LimitLine(0, "Lower Limit");
        lower_limit.setLineWidth(4f);
        lower_limit.enableDashedLine(10f, 10f, 0f);
        lower_limit.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        lower_limit.setTextSize(10f);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.removeAllLimitLines(); // reset all limit lines to avoid overlapping lines
        leftAxis.addLimitLine(upper_limit);
        leftAxis.addLimitLine(lower_limit);
        leftAxis.setAxisMaxValue(10000);
        leftAxis.setAxisMinValue(0);
        //leftAxis.setYOffset(20f);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawZeroLine(false);

        // limit lines are drawn behind data (and not on top)
        leftAxis.setDrawLimitLinesBehindData(true);

        mChart.getAxisRight().setEnabled(false);

        //mChart.getViewPortHandler().setMaximumScaleY(2f);
        //mChart.getViewPortHandler().setMaximumScaleX(2f);

        mChart.animateX(3500, Easing.EasingOption.EaseInOutQuart);

        //  dont forget to refresh the drawing
        mChart.invalidate();
    }

    private ArrayList<String> setXAxisValues(){
        ArrayList<String> xVals = new ArrayList<String>();
        SimpleDateFormat dt1 = new SimpleDateFormat("mm/dd");
        for (Diet d: diets){
            xVals.add(diet.getDate());
        }
//        xVals.add("10/10");
//        xVals.add("10/11");
//        xVals.add("10/12");
//        xVals.add("10/13");
//        xVals.add("10/14");
//        xVals.add("10/15");
//        xVals.add("10/16");

        return xVals;
    }

    private ArrayList<Entry> setYAxisValues(){
        ArrayList<Entry> yVals = new ArrayList<Entry>();
        int i=0;
        for (Diet d:diets){
            yVals.add(new Entry(d.getTotalCaloriesBurned(),i));
            i++;
        }
//        yVals.add(new Entry(3000, 0));
//        yVals.add(new Entry(1000, 1));
//        yVals.add(new Entry(6587, 2));
//        yVals.add(new Entry(5987, 3));
//        yVals.add(new Entry(9005, 4));
//        yVals.add(new Entry(4826, 5));
//        yVals.add(new Entry(2689, 6));

        return yVals;
    }

    private void setData() {
        ArrayList<String> xVals = setXAxisValues();

        ArrayList<Entry> yVals = setYAxisValues();

        LineDataSet set1;

        // create a dataset and give it a type
        set1 = new LineDataSet(yVals, "DataSet 1");

        set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        // set the line to be drawn like this "- - - - - -"
        //   set1.enableDashedLine(10f, 5f, 0f);
        // set1.enableDashedHighlightLine(10f, 5f, 0f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLACK);
        set1.setLineWidth(1f);
        set1.setCircleRadius(3f);
        set1.setDrawCircleHole(false);
        set1.setValueTextSize(9f);
        set1.setDrawFilled(true);

        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);

        // set data
        mChart.setData(data);

    }


    @Override
    public void onChartGestureStart(MotionEvent me,
                                    ChartTouchListener.ChartGesture
                                            lastPerformedGesture) {

        Log.i("Gesture", "START, x: " + me.getX() + ", y: " + me.getY());
    }

    @Override
    public void onChartGestureEnd(MotionEvent me,
                                  ChartTouchListener.ChartGesture
                                          lastPerformedGesture) {

        Log.i("Gesture", "END, lastGesture: " + lastPerformedGesture);

        // un-highlight values after the gesture is finished and no single-tap
        if(lastPerformedGesture != ChartTouchListener.ChartGesture.SINGLE_TAP)
            // or highlightTouch(null) for callback to onNothingSelected(...)
            mChart.highlightValues(null);
    }

    @Override
    public void onChartLongPressed(MotionEvent me) {
        Log.i("LongPress", "Chart longpressed.");
    }

    @Override
    public void onChartDoubleTapped(MotionEvent me) {
        Log.i("DoubleTap", "Chart double-tapped.");
    }

    @Override
    public void onChartSingleTapped(MotionEvent me) {
        Log.i("SingleTap", "Chart single-tapped.");
    }

    @Override
    public void onChartFling(MotionEvent me1, MotionEvent me2,
                             float velocityX, float velocityY) {
        Log.i("Fling", "Chart flinged. VeloX: "
                + velocityX + ", VeloY: " + velocityY);
    }

    @Override
    public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
        Log.i("Scale / Zoom", "ScaleX: " + scaleX + ", ScaleY: " + scaleY);
    }

    @Override
    public void onChartTranslate(MotionEvent me, float dX, float dY) {
        Log.i("Translate / Move", "dX: " + dX + ", dY: " + dY);
    }

    @Override
    public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
        Log.i("Entry selected", e.toString());
        Log.i("LOWHIGH", "low: " + mChart.getLowestVisibleXIndex()
                + ", high: " + mChart.getHighestVisibleXIndex());

        Log.i("MIN MAX", "xmin: " + mChart.getXChartMin()
                + ", xmax: " + mChart.getXChartMax()
                + ", ymin: " + mChart.getYChartMin()
                + ", ymax: " + mChart.getYChartMax());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable(UserDetailsActivity.USER, user);
        bundle.putSerializable(Constants.DIET, diet);
    }

    private void getUserDiets(){
        Call<List<Diet>> call = RestAPIClient.get().getUserDiet(user.getUserId());
        call.enqueue(new Callback<List<Diet>>() {
            @Override
            public void onResponse(Call<List<Diet>> call, Response<List<Diet>> response) {
                Log.i("User diet","fetched successfully");
                diets = response.body();
                setupGraph();
            }

            @Override
            public void onFailure(Call<List<Diet>> call, Throwable t) {
                Log.i("cannot fetch ","user diet");
            }
        });
    }

}


