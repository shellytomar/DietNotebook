package com.sparkles.dietanalytics.model;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by pavanibaradi on 9/30/16.
 */

public class Diet implements Serializable{

    private int dietId;
    private User user;
    private String date;
    private float totalCaloriesBurned;
    private float totalCaloriesIntake;
    private int stepCount;
    private Set<FoodIntake> foodIntakes;

    public int getDietId() {
        return dietId;
    }

    public void setDietId(int dietId) {
        this.dietId = dietId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getTotalCaloriesBurned() {
        return totalCaloriesBurned;
    }

    public void setTotalCaloriesBurned(float totalCaloriesBurned) {
        this.totalCaloriesBurned = totalCaloriesBurned;
    }

    public float getTotalCaloriesIntake() {
        return totalCaloriesIntake;
    }

    public void setTotalCaloriesIntake(float totalCaloriesIntake) {
        this.totalCaloriesIntake = totalCaloriesIntake;
    }

    public int getStepCount() {
        return stepCount;
    }

    public void setStepCount(int stepCount) {
        this.stepCount = stepCount;
    }

    public Set<FoodIntake> getFoodIntakes() {
        return foodIntakes;
    }

    public void setFoodIntakes(Set<FoodIntake> foodIntakes) {
        this.foodIntakes = foodIntakes;
    }
}
