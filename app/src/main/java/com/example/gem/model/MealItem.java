package com.example.gem.model;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.UUID;

public class MealItem {
    private String id;
    private String mealName; // Завтрак, Обед, Перекус, Ужин
    private String foods;
    private int calories;

    public MealItem(String mealName, String foods, int calories) {
        this.id = UUID.randomUUID().toString();
        this.mealName = mealName;
        this.foods = foods != null ? foods : "";
        this.calories = calories;
    }

    public MealItem(String id, String mealName, String foods, int calories) {
        this.id = id != null ? id : UUID.randomUUID().toString();
        this.mealName = mealName;
        this.foods = foods != null ? foods : "";
        this.calories = calories;
    }

    public JSONObject toJsonObject() throws JSONException {
        JSONObject obj = new JSONObject();
        obj.put("id", id);
        obj.put("mealName", mealName);
        obj.put("foods", foods);
        obj.put("calories", calories);
        return obj;
    }

    public static MealItem fromJsonObject(JSONObject obj) {
        String id = obj.optString("id", UUID.randomUUID().toString());
        String mealName = obj.optString("mealName", "");
        String foods = obj.optString("foods", "");
        int calories = obj.optInt("calories", 0);
        return new MealItem(id, mealName, foods, calories);
    }

    public String getId() { return id; }
    public String getMealName() { return mealName; }
    public void setMealName(String mealName) { this.mealName = mealName; }
    public String getFoods() { return foods; }
    public void setFoods(String foods) { this.foods = foods; }
    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }
}
