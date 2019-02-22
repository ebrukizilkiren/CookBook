package com.example.rmeysa.cookbookversion2;

public class Ingredient {
    private int recipeId,ingredientId;
    private String ingredientName;
    private boolean checkValue;

    public Ingredient(){

    }

    public Ingredient(int recipeId, int ingredientId, String ingredientName){
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.recipeId = recipeId;
        this.checkValue = false;
    }


    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }

    public int getIngredientId() {
        return ingredientId;
    }

    public void setIngredientId(int ingredientId) {
        this.ingredientId = ingredientId;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public boolean isChecked() {
        return checkValue;
    }

    public void setCheckValue(boolean checkValue) {
        this.checkValue = checkValue;
    }
}
