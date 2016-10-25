package com.sparkles.dietanalytics;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.sparkles.dietanalytics.common.Constants;
import com.sparkles.dietanalytics.model.Diet;
import com.sparkles.dietanalytics.model.FoodIntake;
import com.sparkles.dietanalytics.model.User;
import com.sparkles.dietanalytics.model.Wrapper;
import com.sparkles.dietanalytics.rest.RestAPIClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddItemActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    static ImageView barcode;
    AutoCompleteTextView searchValue;
    static String enteredItem;
    static String scanContent;
    ImageView okButton;
    static EditText item_name, calorieVal, fatVal, carbVal, proteinVal;
    User user;
    Diet diet;
    FoodIntake intake;
    TextView decisionValue;
    // static Button saveButton;

    String[] autoCompleteItems = {"Apple","Avocado","Banana","Yogurt","Cashews","Almond","Apricot","Bread","Beans","Egg",
            "Blackberry","Buttermilk","Rice","Broccoli","Sandwich","popcorn","Potato","Fish","Croissant","Corn","Icecream","Blueberry","Strawberry"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            user = (User) savedInstanceState.getSerializable(UserDetailsActivity.USER);
            diet = (Diet) savedInstanceState.getSerializable(Constants.DIET);
            intake = (FoodIntake)savedInstanceState.getSerializable(Constants.FOODINTAKE);

        } else {
            user = (User) getIntent().getSerializableExtra(UserDetailsActivity.USER);
            diet = (Diet) getIntent().getSerializableExtra(Constants.DIET);
            intake = (FoodIntake) getIntent().getSerializableExtra(Constants.FOODINTAKE);
        }
        intake.setDiet(diet);

        setContentView(R.layout.activity_add_item);

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

        searchValue = (AutoCompleteTextView) findViewById(R.id.searchBox);
        barcode = (ImageView) findViewById(R.id.barcode);
        okButton = (ImageView) findViewById(R.id.okButton);

        item_name = (EditText) findViewById(R.id.item_name);
        calorieVal = (EditText) findViewById(R.id.calorie_value);
        fatVal = (EditText) findViewById(R.id.fat_value);
        carbVal = (EditText) findViewById(R.id.carb_value);
        proteinVal = (EditText) findViewById(R.id.protein_value);
        decisionValue = (TextView) findViewById(R.id.decisionId);

        ArrayAdapter<?> adapter = new ArrayAdapter<Object>(this, android.R.layout.simple_dropdown_item_1line, autoCompleteItems);
        searchValue.setAdapter(adapter);  // keywordField is a AutoCompleteTextView
        searchValue.setThreshold(1);

        adapter.notifyDataSetChanged();


        // saveButton = (Button) findViewById(R.id.saveButton);

    }

    public void saveFood(View v){
        new AlertDialog.Builder(AddItemActivity.this)
                .setTitle("Save")
                .setMessage("Add Food Item")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        addFood();
                        updateTotalCalories();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    public void searchItem(View v) {
        enteredItem = searchValue.getText().toString();
        new BackgroundActivity().execute();
        System.out.println("coming to searchItem+++++++++++++++++++++++++");
    }

    public void getItem(View v) {
        IntentIntegrator scanIntegrator = new IntentIntegrator(this);
        scanIntegrator.initiateScan();
    }

    /**
     * Activity for the obtained result
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        if (scanningResult != null) {
            //we have a scanning result
            scanContent = scanningResult.getContents();
            new BackgroundActivity().execute();

        } else {
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    private void addFood(){
        Call<FoodIntake> call = RestAPIClient.get().insertFoodItem(intake);
        call.enqueue(new Callback<FoodIntake>() {
            @Override
            public void onResponse(Call<FoodIntake> call, Response<FoodIntake> response) {
                Log.i("Added food item", "Successfully added food item");
                Log.d("Added food item", "Successfully added food item");
                intake = response.body();
                Log.i("intake id is", " " + intake.getFoodIntakeId());
                Log.d("intake id is", " " + intake.getFoodIntakeId() + " diet it " + intake.getDiet().getDietId());

            }

            @Override
            public void onFailure(Call<FoodIntake> call, Throwable t) {
                Log.i("Error ", "Cannot add food");
            }
        });
    }

    private void calculateTotalCalories(){
        float cal= diet.getTotalCaloriesIntake()+intake.getCalories();
        Log.i("calories diet",""+diet.getTotalCaloriesIntake());
        Log.i("calories intake",""+intake.getCalories());
        Log.i("total calories",""+cal);
        diet.setTotalCaloriesIntake(cal);
        Log.i("total calories intake", "" + diet.getTotalCaloriesIntake());
    }

    private void updateTotalCalories(){
        calculateTotalCalories();
        Call<Diet> call = RestAPIClient.get().updateTotalCalories(diet);
        call.enqueue(new Callback<Diet>() {
            @Override
            public void onResponse(Call<Diet> call, Response<Diet> response) {
                Log.i("Updated calories", "successfully");
                Intent intent = new Intent(AddItemActivity.this, FoodDetailsActivity.class);
                intent.putExtra(UserDetailsActivity.USER, user);
                intent.putExtra(Constants.DIET, diet);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<Diet> call, Throwable t) {
                Log.i("cannot updated calories", "successfully");
            }
        });
    }


    class BackgroundActivity extends AsyncTask<Void, Void, Wrapper> {
        Wrapper w;

        protected String getASCIIContentFromEntity(HttpEntity entity) throws IllegalStateException, IOException {
            InputStream in = entity.getContent();

            StringBuffer out = new StringBuffer();
            int n = 1;
            while (n > 0) {
                byte[] b = new byte[4096];
                n = in.read(b);
                if (n > 0) out.append(new String(b, 0, n));
            }
            return out.toString();
        }

        @Override
        protected Wrapper doInBackground(Void... params) {
            String searchItem = AddItemActivity.enteredItem;
            AddItemActivity.enteredItem = null;
            String scanned_barcode = AddItemActivity.scanContent;
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext localContext = new BasicHttpContext();
            HttpGet httpGet = null;
            w = new Wrapper();
            if (searchItem == null) {
                if (scanned_barcode == null) {
                    System.out.println("No value");
                } else {
                    System.out.println("barcode ===========================");
                    w.entryType = "barcode";
                    httpGet = new HttpGet("https://api.nutritionix.com/v1_1/item?upc=" + scanned_barcode + "&appId=97e21d7e&appKey=96cfdaa75b1c399a246206f51103a007");
                }
            } else {
                System.out.println("manual ===========================" + searchItem);
                w.entryType = "manual";
            /*httpGet = new HttpGet("https://api.nutritionix.com/v1_1/search/" + searchItem +
                    "?fields=item_name%2Citem_id%2Cbrand_name%2Cnf_calories%2Cnf_total_fat&appId=97e21d7e&appKey=96cfdaa75b1c399a246206f51103a007");*/
                httpGet = new HttpGet("https://api.nutritionix.com/v1_1/search/"+ searchItem +"?fields=item_name%2Citem_id%2Cbrand_name%2Cnf_calories%2C" +
                        "nf_total_fat%2Cnf_protein%2Cnf_total_carbohydrate&appId=97e21d7e&appKey=96cfdaa75b1c399a246206f51103a007");

            }

            String text = null;
            try {
                HttpResponse response = httpClient.execute(httpGet, localContext);

                HttpEntity entity = response.getEntity();
                text = getASCIIContentFromEntity(entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            w.results = text;
            return w;
        }

        protected void onPostExecute(Wrapper results) {
            if (results != null) {

                String item_name = null;
                String calorie_val = null;
                String fat_val = null;
                String carb_val = null;
                String protein_val = null;
                final int servings=1;

                try {
                    JSONObject jObject = new JSONObject(w.results);
                    if (w.entryType.equals("barcode")) {
                        item_name = jObject.getString("item_name");
                        calorie_val = jObject.getString("nf_calories");
                        fat_val = jObject.getString("nf_total_fat");
                        carb_val = jObject.getString("nf_total_carbohydrate");
                        protein_val = jObject.getString("nf_protein");

                    } else if (w.entryType.equals("manual")) {

                        JSONArray jArray = jObject.getJSONArray("hits");
                        System.out.println("Array length :" + jArray.length());
                        for (int i = 0; i < jArray.length(); i++) {

                            JSONObject oneObject = jArray.getJSONObject(i);
                            JSONObject obj = oneObject.getJSONObject("fields");
                            // Pulling items from the array
                            item_name = obj.getString("item_name");
                            calorie_val = obj.getString("nf_calories");
                            fat_val = obj.getString("nf_total_fat");
                            carb_val = obj.getString("nf_total_carbohydrate");
                            protein_val = obj.getString("nf_protein");
                        }
                    }
                    AddItemActivity.item_name.setText(item_name);
                    AddItemActivity.calorieVal.setText(calorie_val);
                    AddItemActivity.fatVal.setText(fat_val);
                    AddItemActivity.carbVal.setText(carb_val);
                    AddItemActivity.proteinVal.setText(protein_val);

                    boolean decisionVal = checkCalories(user,Float.parseFloat(calorie_val));
                    float totalCal= Float.parseFloat(calorie_val)+ diet.getTotalCaloriesIntake();
                    if(decisionVal) {
                        decisionValue.setText("Your calorie intake is " + totalCal + ". You are good to eat this food");
                    }else{
                        decisionValue.setText("Your calorie intake is " + totalCal + ". We suggest you not to eat this food");
                    }

                    intake.setCalories(Float.parseFloat(calorie_val));
                    intake.setItem(item_name);
                    intake.setServings(servings);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }

        private boolean checkCalories(User user, float calorie){
            if(user.getAge()>=4 && user.getAge()<=8){
                if(calorie<=1488.889)
                    return true;
                else
                    return false;
            }else{
                if(user.getGender() == null || user.getGender().equalsIgnoreCase("female")){
                    if(user.getActiveType().equalsIgnoreCase("sedentary") || user.getActiveType().equalsIgnoreCase("moderate")){
                        if(calorie<=1880.00)
                            return true;
                        else
                            return false;
                    }else if(user.getActiveType().equalsIgnoreCase("active")){
                        if(calorie<=2280.00)
                            return true;
                        else
                            return false;
                    }
                }else if(user.getGender().equalsIgnoreCase("male")){
                    if(user.getActiveType().equalsIgnoreCase("sedentary")){
                        if(calorie<=2120.00)
                            return true;
                        else
                            return false;
                    }else if(user.getActiveType().equalsIgnoreCase("active") || user.getActiveType().equalsIgnoreCase("moderate")){
                        if(calorie<=2740.00)
                            return true;
                        else
                            return false;
                    }
                }
            }
            return false;
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.i("id is:",""+id);
        System.out.print(id);
        System.out.print(R.id.action_steps);
        if (id == R.id.action_steps) {
            Intent i = new Intent(AddItemActivity.this,StepCountActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_addFood) {
            Intent i = new Intent(AddItemActivity.this,FoodDetailsActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_articles) {
            Intent i = new Intent(AddItemActivity.this,ArticlesActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_reports) {
            Intent i = new Intent(AddItemActivity.this, ReportActivity.class);
            i.putExtra(UserDetailsActivity.USER, user);
            i.putExtra(Constants.DIET, diet);
            startActivity(i);
        }else if (id == R.id.action_logout) {
            Intent i = new Intent(AddItemActivity.this, SignUpActivity.class);
            startActivity(i);
        }else if (id == R.id.exit) {
        }else if(id == R.id.home){
            Intent i = new Intent(AddItemActivity.this, Dashboard.class);
            i.putExtra(UserDetailsActivity.USER, user);
            startActivity(i);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}