package com.example.rmeysa.cookbookversion2;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper{

    private final String TABLE_NAME = "recipes2";
    private final static String KEY_ID = "recipe_id"; //id
    private final static String NAME = "recipe_name";  //title
    private final static String PREPARATION = "preparation";
    private final static String TIME = "cooking_time";
    private final static String DATE = "date";
    private final static String COLUMN_IMAGE_URL = "image_url";

    private final String TABLE_NAME_ING = "ingredients";
    private final static String ING_ID = "ing_id";
    private final static String ING_NAME = "ing_name";
    private final static String REC_ID = "fk_recipe_id";


    public DatabaseHelper(Context context) {
        super(context, "cookbook_database", null, 1); /**/
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_RECIPE_TABLE = "CREATE TABLE " + TABLE_NAME +"("
                + KEY_ID + " INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT,"
                + NAME + " TEXT,"
                + PREPARATION + " TEXT," + TIME + " INTEGER," + DATE + " TEXT," + COLUMN_IMAGE_URL + " TEXT);";

        String CREATE_INGREDIENTS_TABLE = "CREATE TABLE " + TABLE_NAME_ING +"("
                + ING_ID + " INTEGER UNIQUE PRIMARY KEY AUTOINCREMENT,"
                + ING_NAME + " TEXT,"
                + REC_ID + " INTEGER);";

        db.execSQL(CREATE_RECIPE_TABLE);
        db.execSQL(CREATE_INGREDIENTS_TABLE);

        //db.delete(TABLE_NAME, null, null);
        //db.delete(TABLE_NAME_ING, null, null);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_ING);
        onCreate(db);
    }
    void addNewRecipe(Recipe recipe) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ID, recipe.getRecipe_id());
        values.put(NAME, recipe.getRecipe_name());
        values.put(PREPARATION, recipe.getPreparation());
        values.put(TIME, recipe.getCookingTime());
        values.put(DATE,recipe.getDate());
        values.put(COLUMN_IMAGE_URL,recipe.getImageURL());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    void addNewIngredient(Ingredient ingredient){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ING_ID, ingredient.getIngredientId());
        values.put(ING_NAME, ingredient.getIngredientName());
        values.put(REC_ID, ingredient.getRecipeId());
        db.insert(TABLE_NAME_ING,null,values);
        db.close();

    }
    public void addIngredients(String ingredientsName, int recipeId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ING_NAME, ingredientsName);
        values.put(REC_ID, recipeId);

        db.insert(TABLE_NAME_ING, null, values);
        Log.e("DATABASE OPERATIONS", "1 ingredient added for " + recipeId);
    }

    public boolean updateRecipe(int recipe_id, String recipe_name, String preparation,int time, String date, String imageURL) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(KEY_ID, recipe_id);
        args.put(NAME, recipe_name);
        args.put(PREPARATION, preparation);
        args.put(TIME, time);
        args.put(DATE,date);
        args.put(COLUMN_IMAGE_URL,imageURL);

        return db.update(TABLE_NAME, args, KEY_ID + "=" + recipe_id, null) > 0;
    }

    public boolean updateIngredient(int ingId, String name, int recId){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put(ING_ID, ingId);
        args.put(ING_NAME, name);
        args.put(REC_ID, recId);

        return db.update(TABLE_NAME_ING, args, ING_ID + " = " + ingId, null) > 0;
    }

    public boolean deleteRecipe(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, KEY_ID + " = " + id, null) > 0;
    }

    public boolean deleteIngredient(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME_ING, ING_ID + " = " + id, null) > 0;
    }

    public List<Recipe> getAllRecipeList() {
        List<Recipe> recipeList = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_NAME + " ORDER BY " + NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Recipe recipe = new Recipe();
                recipe.setRecipe_id(Integer.parseInt(cursor.getString(0)));
                recipe.setRecipe_name(cursor.getString(1));
                recipe.setPreparation(cursor.getString(2));
                recipe.setCooking_time(cursor.getInt(3));
                recipe.setDate(cursor.getString(4));
                recipe.setImageURL(cursor.getString(5));

                recipeList.add(recipe);
            } while (cursor.moveToNext());
        }
        return recipeList;
    }


    public List<Ingredient> getAllIngredientsByRecipeId(int rcId) {
        List<Ingredient> ingredientList = new ArrayList<>();
        String selectQuery = "Select * FROM " + TABLE_NAME + "," + TABLE_NAME_ING + " WHERE " + REC_ID + " = " +
                KEY_ID + " AND " + REC_ID + " = " + rcId ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Ingredient ingredient = new Ingredient();
                ingredient.setIngredientId(cursor.getInt(0));
                ingredient.setIngredientName(cursor.getString(1));
                ingredient.setRecipeId(cursor.getInt(2));
                ingredientList.add(ingredient);
            } while (cursor.moveToNext());
        }
        return ingredientList;
    }

    public List<Ingredient> getAllIngredients() {
        List<Ingredient> ingredientList = new ArrayList<>();
        String selectQuery = "Select  * FROM " + TABLE_NAME + "," + TABLE_NAME_ING + " WHERE " + REC_ID + " = " +
                KEY_ID  ;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Ingredient ingredient = new Ingredient();
                ingredient.setIngredientId(cursor.getInt(0));
                ingredient.setIngredientName(cursor.getString(1));
                ingredient.setRecipeId(cursor.getInt(2));
                ingredientList.add(ingredient);
            } while (cursor.moveToNext());
        }
        return ingredientList;
    }
    public Cursor getRecipeDetailIngredients(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select  " + ING_NAME +
                " FROM " + TABLE_NAME_ING + " , " + TABLE_NAME + " where " +
                REC_ID + " = " + KEY_ID + " AND " +  REC_ID + " = " + id , null);
        return cursor;
    }


    public List<Ingredient> getIngredientsByRcId(int recId) {
        List<Ingredient> ingredientList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select  * FROM " + TABLE_NAME_ING + " , " + TABLE_NAME + " where " +
                REC_ID + " = " + KEY_ID + " AND " +  REC_ID + " = " + recId , null);
        if (cursor.moveToFirst()) {
            do {
                Ingredient ingredient = new Ingredient();
                ingredient.setIngredientId(cursor.getInt(0));
                ingredient.setIngredientName(cursor.getString(1));
                ingredient.setRecipeId(cursor.getInt(2));
                ingredientList.add(ingredient);
            } while (cursor.moveToNext());
        }
        return ingredientList;
    }

}




















