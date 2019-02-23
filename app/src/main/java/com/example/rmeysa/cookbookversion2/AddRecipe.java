package com.example.rmeysa.cookbookversion2;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddRecipe extends AppCompatActivity {
    DatabaseHelper db;
    List<Recipe> recipeList;
    ImageButton CameraButton;
    String path;
    private Uri uri;
    Bitmap bitmap;
    private Uri imageURI;
    String timeStamp;
    String date;
    String uri_to_str;
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int PICTURE_REQUEST_CODE = 1;

    Button btnAdd;
    EditText ingredientsName;
    LinearLayout linearLayout;
    private ArrayList<String> array_ingredient = new ArrayList<String>();
    private static TableLayout ingredient_table;
    private static TableRow ing_row;
    private static EditText ingredient_edittext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        db = new DatabaseHelper(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        linearLayout = (LinearLayout) findViewById(R.id.layout_ingredient);
        ingredientsName = (EditText) findViewById(R.id.editText_ingredient);
        btnAdd = (Button) findViewById(R.id.btn_ing);
        ingredient_table = (TableLayout) findViewById(R.id.ingredient_table);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ingredientToAdd = ingredientsName.getText().toString();
                if (ingredientToAdd.matches("")) {
                    Message("Enter an ingredient");
                } else {
                    ing_row = new TableRow(AddRecipe.this);
                    ingredient_edittext = new EditText(AddRecipe.this);

                    Button remove = new Button(AddRecipe.this);
                    remove.setText("-");

                    remove.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View row = (View) v.getParent();
                            ingredient_table = ((TableLayout) row.getParent());
                            ingredient_table.removeView(row);
                        }
                    });

                    ingredient_edittext.setText(ingredientToAdd);
                    ingredient_table.addView(ing_row);
                    ing_row.addView(remove);
                    ing_row.addView(ingredient_edittext);
                    Log.e("Bu ingredient eklendi :", ingredientToAdd);
                    ingredientsName.setText("");
                }
            }
        });

        CameraButton = findViewById(R.id.camera_button);


        recipeList = new ArrayList<>();
        recipeList = db.getAllRecipeList();

        long day = System.currentTimeMillis();
        date = new SimpleDateFormat("dd/MM/yyyy").format(new Date(day));


        CameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseSource();
            }
        });


    }
    public void addIngredient(String ingredientToAdd) {

        db = new DatabaseHelper(this);
        db.addIngredients(ingredientToAdd, recipeList.size());
        db.close();

    }


    //----------------Camera-----------------------//
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                ChooseSource();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void ChooseSource() {

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fname = timeStamp + ".jpg";

        final File sdDirectory = new File(storageDir, fname);
        path = sdDirectory.getAbsolutePath();
        imageURI = Uri.fromFile(sdDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(camera_intent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(camera_intent);
            intent.setComponent(new ComponentName(packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
            cameraIntents.add(intent);
        }

        // Filesystem.
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_PICK);


        final Intent select_intent = Intent.createChooser(galleryIntent, "Select:");
        select_intent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[cameraIntents.size()]));
        startActivityForResult(select_intent, PICTURE_REQUEST_CODE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK && requestCode == PICTURE_REQUEST_CODE) {

            final boolean isCamera;
            if (data == null) {
                isCamera = true;
            } else {
                if (data.getAction() == null) {
                    isCamera = false;
                } else {
                    isCamera = data.getAction().equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                }
            }

            if (isCamera) {
                // FROM CAMERA
                uri = imageURI;
            } else {
                // From gallery a pic was chosen
                uri = data == null ? null : data.getData();
            }

            if (uri != null) {

                InputStream inputStream = null;
                try {
                    inputStream = getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    CameraButton.setImageBitmap(bitmap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

        } else {
            uri = Uri.parse("R.mipmap.ic_launcher_broken_image_round.png");
            Message("RESULT CANCEL");

        }
    }
    //---------camera end----//

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.save, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item){
        EditText edit_text_input =(EditText) findViewById(R.id.edit_text_input);
        int int_edit = 0;
        String edit_text_input_string = edit_text_input.getText().toString();

        EditText recipe_name_edit = (EditText) findViewById(R.id.recipe_name_edit);
        EditText preparation_edit = (EditText) findViewById(R.id.preparation_edit);

        if(edit_text_input.getText().length() != 0){
            int_edit  = Integer.parseInt(edit_text_input_string);
        } else {
            Message(" Time is empty");
        }

        if(uri != null){
            uri_to_str = uri.toString();
        }
        switch (item.getItemId()){
            case R.id.save:
                if(recipe_name_edit.getText().length()==0 || preparation_edit.getText().length()==0  ){
                    Message("Recipe Name or Preparation part is empty");
                }
                else {
                            db.addNewRecipe(new Recipe(recipeList.size(), recipe_name_edit.getText().toString().toUpperCase(), preparation_edit.getText().toString(),int_edit,date,uri_to_str));
                            SharedPreferences mypreference_image= getSharedPreferences("mypreference", MODE_PRIVATE);
                            SharedPreferences.Editor editor = mypreference_image.edit();
                                editor.putString("bitmap",uri_to_str);
                                editor.apply();

                    for (int i = 0; i <= ingredient_table.getChildCount(); i++) {
                        View viewChild = ingredient_table.getChildAt(i);
                        if (viewChild instanceof TableRow) {
                            int rowChildParts = ((TableRow) viewChild).getChildCount();
                            for (int j = 0; j < rowChildParts; j++) {
                                View viewChild2 = ((TableRow) viewChild).getChildAt(j);
                                if (viewChild2 instanceof EditText) {
                                    String ingredients = ((EditText) viewChild2).getText().toString();
                                    array_ingredient.add(ingredients);
                                    addIngredient(ingredients);
                                }
                            }
                        }
                    }


                    if (array_ingredient.isEmpty()) {
                        Toast.makeText(this, "Please add ingredients", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("All ingredients : ", String.valueOf(array_ingredient));
                    }
                        Message("Added!");
                        Intent intentNewRecipe = new Intent(AddRecipe.this, MainActivity.class);
                        startActivity(intentNewRecipe);
                        return true;
                    }
            case R.id.homeAsUp:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                }

        return super.onOptionsItemSelected(item);
    }
    private void Message(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
