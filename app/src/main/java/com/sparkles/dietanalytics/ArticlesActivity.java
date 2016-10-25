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
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sparkles.dietanalytics.common.Constants;
import com.sparkles.dietanalytics.model.Diet;
import com.sparkles.dietanalytics.model.User;

public class ArticlesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener
        {

    ListView list;
    String[] web = {
            "The Workout You Need To Do If You’re Trying To Lose Weight",
            "8 Signs You’re Eating Too Much Sugar",
            "5 Steps to Take Control of Food Addiction",
            "The #1 Habit You Should Have to Lose Weight (It’s Not What You Think!)"
    };
    Integer[] imageId = {
            R.drawable.first,
            R.drawable.second,
            R.drawable.third,
            R.drawable.fourth
    };

    User user;
    Diet diet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);

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
        CustomListActivity adapter = new
                CustomListActivity(ArticlesActivity.this, web, imageId);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                WebView webView = new WebView(view.getContext());
                String[] urls = getResources().getStringArray(R.array.bookmark_urls);
                webView.loadUrl(urls[position]);
                // Toast.makeText(MainActivity.this, "You Clicked at " + web[+position], Toast.LENGTH_SHORT).show();

            }
        });

        if(savedInstanceState!=null){
            user = (User) savedInstanceState.getSerializable(UserDetailsActivity.USER);
            diet = (Diet) savedInstanceState.getSerializable(Constants.DIET);
        }else {
            user = (User) getIntent().getSerializableExtra(UserDetailsActivity.USER);
            diet = (Diet) getIntent().getSerializableExtra(Constants.DIET);
        }
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
                Log.i("id is:", "" + id);
                System.out.print(id);
                System.out.print(R.id.action_steps);
                if (id == R.id.action_steps) {
                    Intent i = new Intent(ArticlesActivity.this,StepCountActivity.class);
                    i.putExtra(UserDetailsActivity.USER, user);
                    i.putExtra(Constants.DIET, diet);
                    startActivity(i);
                }else if (id == R.id.action_addFood) {
                    Intent i = new Intent(ArticlesActivity.this,FoodDetailsActivity.class);
                    i.putExtra(UserDetailsActivity.USER, user);
                    i.putExtra(Constants.DIET, diet);
                    startActivity(i);
                }else if (id == R.id.action_articles) {
                    Intent i = new Intent(ArticlesActivity.this,ArticlesActivity.class);
                    i.putExtra(UserDetailsActivity.USER, user);
                    i.putExtra(Constants.DIET, diet);
                    startActivity(i);
                }else if (id == R.id.action_reports) {
                    Intent i = new Intent(ArticlesActivity.this, ReportActivity.class);
                    i.putExtra(UserDetailsActivity.USER, user);
                    i.putExtra(Constants.DIET, diet);
                    startActivity(i);
                }else if (id == R.id.action_logout) {
                    Intent i = new Intent(ArticlesActivity.this, SignUpActivity.class);
                    startActivity(i);
                }else if (id == R.id.exit) {
                }else if(id == R.id.home){
                    Intent i = new Intent(ArticlesActivity.this, Dashboard.class);
                    i.putExtra(UserDetailsActivity.USER, user);
                    startActivity(i);
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        }
