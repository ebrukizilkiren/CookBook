package com.example.rmeysa.cookbookversion2;

public class Ingredient {
    private static int recipeId;
    private int ingredientId;
    private String ingredientName;
    public Ingredient(){

    }
    public Ingredient(String ingredientName){
        this.ingredientName = ingredientName;
    }

    public Ingredient(int ingredientId, String ingredientName,int recipeId){
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.recipeId = recipeId;
    }


    public static int getRecipeId() {
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

}
