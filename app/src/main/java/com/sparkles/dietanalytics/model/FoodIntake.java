package com.sparkles.dietanalytics.model;

import java.io.Serializable;

/**
 * Created by pavanibaradi on 9/30/16.
 */

public class FoodIntake  implements Serializable {

    private int foodIntakeId;
    private String mealType;
    private String item;
    private float calories;
    private float servings;
    private Diet diet;

    public int getFoodIntakeId() {
        return foodIntakeId;
    }

    public void setFoodIntakeId(int foodIntakeId) {
        this.foodIntakeId = foodIntakeId;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public float getCalories() {
        return calories;
    }

    public void setCalories(float calories) {
        this.calories = calories;
    }

    public float getServings() {
        return servings;
    }

    public void setServings(float servings) {
        this.servings = servings;
    }

    public Diet getDiet() {
        return diet;
    }

    public void setDiet(Diet diet) {
        this.diet = diet;
    }
}
