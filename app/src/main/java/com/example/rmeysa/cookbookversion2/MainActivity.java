package com.example.rmeysa.cookbookversion2;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

//    public static int splash_time_out = 4000;

    RecyclerView r_View;
    DatabaseHelper db;
    RecipeAdapter adapter;
    List<Recipe> recipeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        recipeList = new ArrayList<>();
        recipeList = db.getAllRecipeList();

        adapter = new RecipeAdapter(this, recipeList);

        r_View = (RecyclerView) findViewById(R.id.list_view);
        r_View.setLayoutManager(new LinearLayoutManager(this));
        r_View.setAdapter(adapter);
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.add:
                Intent intent_add = new Intent(this, AddRecipe.class);
                startActivity(intent_add);
        }
        return super.onOptionsItemSelected(item);
    }
}
