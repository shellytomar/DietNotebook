package com.sparkles.dietanalytics;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.sparkles.dietanalytics.model.User;
import com.sparkles.dietanalytics.rest.RestAPIClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("Sign In");
    }

    public void onClickSignIn(View view){
        EditText email = (EditText)findViewById(R.id.email);
        EditText password = (EditText)findViewById(R.id.password);
        if(email!=null && password!=null) {
            user = new User();
            user.setEmail(email.getText().toString());
            user.setPassword(password.getText().toString());
            fetchUser();

        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(SignInActivity.this);
            builder.setMessage("Please enter email and password")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(SignInActivity.this, SignInActivity.class);
                            startActivity(intent);
                        }
                    });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void fetchUser(){
        Call<User> call = RestAPIClient.get().login(user.getEmail(), user.getPassword());
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.i("Register User", "Successfully Logged in user");
                user  = response.body();
                Log.i("user",user.getName());
                if(user.getUserId()>0) {
                    Intent intent = new Intent(SignInActivity.this, Dashboard.class);
                    intent.putExtra(UserDetailsActivity.USER, user);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Log.i("Error ","Cannot login user");
            }
        });
    }

}
