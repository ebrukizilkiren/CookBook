package com.example.rmeysa.cookbookversion2;

import android.Manifest;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
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

    EditText editTextIngredient;
    private LinearLayout parenLayout;
    Button add_ing;
    Button remove;
    CheckBox check_box;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_recipe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        add_ing = (Button) findViewById(R.id.btnAdd);
        remove = new Button(AddRecipe.this);
        editTextIngredient = (EditText) findViewById(R.id.txtItem);
        parenLayout = (LinearLayout) findViewById(R.id.parent_layout);


       add_ing.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               String editTextIngredient_Str = editTextIngredient.getText().toString();
               if (editTextIngredient_Str != null) {
                   parenLayout.setOrientation(LinearLayout.VERTICAL);
                   LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                   final View rowView = inflater.inflate(R.layout.row, null);
                   check_box = (CheckBox) rowView.findViewById(R.id.checkBox);
                   check_box.setText(editTextIngredient.getText().toString());
                   remove = (Button) rowView.findViewById(R.id.delete);
                   parenLayout.addView(rowView, parenLayout.getChildCount() - 1);
                   editTextIngredient.setText("");
               } else {
                   Message("Add an ingredient");
               }

               remove.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       parenLayout.removeView((View) v.getParent());

                   }
               });

           }
       });

        CameraButton = findViewById(R.id.camera_button);

        db = new DatabaseHelper(this);
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
                        if(!recipeList.isEmpty()){
                            db.addNewRecipe(new Recipe(recipeList.size(), recipe_name_edit.getText().toString(), preparation_edit.getText().toString(),int_edit,date,uri_to_str));
                            SharedPreferences mypreference_image= getSharedPreferences("mypreference", MODE_PRIVATE);
                            SharedPreferences.Editor editor = mypreference_image.edit();
                                editor.putString("bitmap",uri_to_str);
                                editor.apply();
                        }
                        else{
                            db.addNewRecipe(new Recipe(recipeList.size(), recipe_name_edit.getText().toString(), preparation_edit.getText().toString(),int_edit,date,uri_to_str));
                            SharedPreferences mypreference_image= getSharedPreferences("mypreference", MODE_PRIVATE);
                            SharedPreferences.Editor editor = mypreference_image.edit();
                            editor.putString("bitmap",uri_to_str);
                            editor.apply();
                        }
                        Message("Added!");
                        Intent intentNewRecipe = new Intent(AddRecipe.this, MainActivity.class);
                        startActivity(intentNewRecipe);
                        return true;
                    }
            case R.id.home:
                onBackPressed();
                }

        return super.onOptionsItemSelected(item);
    }
    private void Message(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}
