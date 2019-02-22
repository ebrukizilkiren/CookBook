package com.example.rmeysa.cookbookversion2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.FoodViewHolder>{

    private Context cntex;
    private List<Recipe> recipeList;
    DatabaseHelper db;

    public RecipeAdapter(Context context, List<Recipe> recipeList) {
        cntex = context;
        this.recipeList = recipeList;
    }

    @Override
    public FoodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(cntex);
        View view = inflater.inflate(R.layout.layout_list, null);
        //Database
        db = new DatabaseHelper(cntex);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(FoodViewHolder holder, final int position) {
        final Recipe recipe = recipeList.get(position);
        holder.recipe_name_text.setText(recipe.getRecipe_name());
        holder.preparation_text.setText(recipe.getPreparation());
        holder.date.setText(recipe.getDate());
        //Main activity list -> ShowRecipe
        holder.relative_layout_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent_show = new Intent(cntex, ShowRecipe.class);
                //Bundle for passing key
                Bundle bundle = new Bundle();
                bundle.putString("recipe_id", String.valueOf(position+1));
                intent_show.putExtras(bundle);
                intent_show.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                cntex.getApplicationContext().startActivity(intent_show);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }

    //View holder for the recipe
    class FoodViewHolder extends RecyclerView.ViewHolder{
        TextView recipe_name_text, preparation_text,date;
        RelativeLayout relative_layout_list;
        public FoodViewHolder(View itemView) {
            super(itemView);
            recipe_name_text = itemView.findViewById(R.id.recipe_name_text);
            preparation_text = itemView.findViewById(R.id.preparation_text);
            date = itemView.findViewById(R.id.date);
            relative_layout_list = itemView.findViewById(R.id.relative_layout_list);
        }
    }
}

