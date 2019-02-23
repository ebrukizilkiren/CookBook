package com.example.rmeysa.cookbookversion2;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bryan on 7/12/2016.
 */
public class ListIngredientsAdapter extends ArrayAdapter {

    List list = new ArrayList();

    public ListIngredientsAdapter(Context context, int resource) {
        super(context, resource);
    }


    static class LayoutHandler {

    }

    @Override
    public void add(Object object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        LayoutHandler layoutHandler;
        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.activity_show_recipe, parent, false);
            layoutHandler = new LayoutHandler();
            row.setTag(layoutHandler);
        } else {
            layoutHandler = (LayoutHandler) row.getTag();
        }
        Ingredient ingredients = (Ingredient) this.getItem(position);
        return row;
    }
}
