package com.example.smartpantrymanager;

import java.util.ArrayList;
import java.util.List;

public class Recipe {
    private long id;
    private String title;
    private String category;
    private String prepTime;
    private int servings;
    private String instructions;
    private String imageCategory;
    private List<RecipeIngredient> ingredients = new ArrayList<>();

    // Runtime properties for matching logic & display
    private int missingIngredientCount = 0;
    private List<String> missingIngredientNames = new ArrayList<>();

    public Recipe() {
    }

    public Recipe(long id, String title, String category, String prepTime, int servings, String instructions, String imageCategory) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.prepTime = prepTime;
        this.servings = servings;
        this.instructions = instructions;
        this.imageCategory = imageCategory;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPrepTime() {
        return prepTime;
    }

    public void setPrepTime(String prepTime) {
        this.prepTime = prepTime;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public String getImageCategory() {
        return imageCategory != null ? imageCategory : "Breakfast";
    }

    public void setImageCategory(String imageCategory) {
        this.imageCategory = imageCategory;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> ingredients) {
        this.ingredients = ingredients;
    }

    public int getMissingIngredientCount() {
        return missingIngredientCount;
    }

    public void setMissingIngredientCount(int missingIngredientCount) {
        this.missingIngredientCount = missingIngredientCount;
    }

    public List<String> getMissingIngredientNames() {
        return missingIngredientNames;
    }

    public void setMissingIngredientNames(List<String> missingIngredientNames) {
        this.missingIngredientNames = missingIngredientNames;
    }
}
