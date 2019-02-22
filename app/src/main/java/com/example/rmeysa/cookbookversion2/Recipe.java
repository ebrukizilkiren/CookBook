package com.example.rmeysa.cookbookversion2;

public class Recipe {
    private int recipe_id,cooking_time;
    private String recipe_name, preparation;
    private String imageURL,date;

    public Recipe(){
    }

    public Recipe(int recipe_id, String recipe_name, String preparation,int cooking_time,String date,String imageURL) {
        this.recipe_id = recipe_id;
        this.recipe_name = recipe_name;
        this.preparation = preparation;
        this.cooking_time = cooking_time;
        this.date = date;
        this.imageURL = imageURL;
    }


    public int getRecipe_id() { return recipe_id; }
    public String getRecipe_name() {
        return recipe_name;
    }
    public String getPreparation() {
        return preparation;
    }
    public int getCookingTime() { return cooking_time; }
    public String getDate() {
        return date;
    }
    public String getImageURL() {
        return imageURL;
    }
    public void setRecipe_id(int id){
        this.recipe_id = id;
    }
    public void setRecipe_name(String title){
        this.recipe_name = title;
    }
    public void setPreparation(String description){
        this.preparation = description;
    }
    public void setDate(String date){
        this.date = date;
    }
    public void setCooking_time(int cooking_time){
        this.cooking_time = cooking_time;
    }
    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}



