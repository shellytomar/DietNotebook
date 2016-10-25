package com.sparkles.dietanalytics.rest;

import com.sparkles.dietanalytics.model.Diet;
import com.sparkles.dietanalytics.model.FoodIntake;
import com.sparkles.dietanalytics.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by pavanibaradi on 10/12/16.
 */
public interface RestAPIInterface {

    @POST("users")
    Call<User> registerUser(@Body User user);

    @GET("users/{userId}")
    Call<User> getUser(@Path("userId") int userId);

    @GET("users/{email}/{password}")
    Call<User> login(@Path("email") String email,@Path("password") String password );

    @POST("items")
    Call<FoodIntake> insertFoodItem(@Body FoodIntake foodIntake);

    @GET("items/{dietId}")
    Call<List<FoodIntake>> getDietItems(@Path("dietId") int userId);

    @POST("diets")
    Call<Diet> insertUserDiet(@Body Diet diet);

    @PUT("diets")
    Call<Diet> updateStepCount(@Body Diet diet);

    @PUT("diets/calories")
    Call<Diet> updateTotalCalories(@Body Diet diet);

    @GET("diets/{userId}")
    Call<List<Diet>> getUserDiet(@Path("userId") int userId);

    @GET("diets/{userId}/{date}")
    Call<Diet> getUserDiet(@Path("userId") int userId, @Path("date") String date);
}