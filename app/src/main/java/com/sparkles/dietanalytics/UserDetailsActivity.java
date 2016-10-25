package com.sparkles.dietanalytics;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.sparkles.dietanalytics.model.User;

import java.util.Calendar;

public class UserDetailsActivity extends AppCompatActivity {

    TextView txtDate;
    private int mYear, mMonth, mDay;
    private User user;
    public final static String USER = "user";
    private RadioGroup gender;
    final static String MALE = "male";
    final static String FEMALE = "female";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!=null){
            user = (User)savedInstanceState.getSerializable(UserDetailsActivity.USER);
        }else {
            user = new User();
        }
//        gender = (RadioGroup) findViewById(R.id.gender);
//        if(gender!=null) {
//            gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(RadioGroup group, int checkedId) {
//                    if (checkedId == R.id.male)
//                        user.setGender(UserDetailsActivity.MALE);
//                    else if (checkedId == R.id.female)
//                        user.setGender(UserDetailsActivity.FEMALE);
//                }
//            });
//        }

        setContentView(R.layout.activity_user_details);

    }

    private void setUserDetails(){
        EditText email = (EditText)findViewById(R.id.email);
        EditText name = (EditText)findViewById(R.id.userName);
        EditText password = (EditText)findViewById(R.id.password);
        EditText confirmPassword = (EditText)findViewById(R.id.confirmpassword);
        user.setEmail(email.getText().toString());
        user.setName(name.getText().toString());
        user.setPassword(password.getText().toString());
    }

    @Override
    public void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putSerializable(UserDetailsActivity.USER, user);
    }

    public void onClickDOB(View v){

        txtDate = (TextView) findViewById(R.id.txtViewDateofBirth);
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        // Launch Date Picker Dialog
        DatePickerDialog dpd = new DatePickerDialog(UserDetailsActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        // Display Selected date in text box
                        txtDate.setText(dayOfMonth + " - " + (monthOfYear + 1) + " - " + year);
                        int age = mYear - year;
                        user.setAge(age);
                        Log.i("age is ",""+age);

                    }
                }, mYear, mMonth, mDay);
        dpd.show();
    }

    public void onClickContinue(View v){
        if (v.getId() == R.id.btn_continue_details) {
            setUserDetails();
            Intent i = new Intent(v.getContext(), HealthGoalActivity.class);
            i.putExtra(UserDetailsActivity.USER,user);
            startActivity(i);
        }
    }

    public void onRadioButtonClicked(View view){
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.male:
                if (checked)
                    user.setGender(UserDetailsActivity.MALE);
                    break;
            case R.id.female:
                if (checked)
                    user.setGender(UserDetailsActivity.FEMALE);
                    break;
        }
    }
}
