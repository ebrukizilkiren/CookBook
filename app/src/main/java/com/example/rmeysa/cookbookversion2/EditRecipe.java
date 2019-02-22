package com.example.rmeysa.cookbookversion2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
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
import android.util.Base64;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EditRecipe extends AppCompatActivity {
    DatabaseHelper db;
    List<Recipe> recipeList;
    EditText recipe_name_edit;
    EditText preparation_edit;
    ImageButton CameraButton;
    String image_uri_db;
    String uri_to_str;
    String date;
    Uri uri;
    Bitmap bitmap;
    String path;
    private Uri imageURI;
    private static final int REQUEST_STORAGE_PERMISSION = 1;
    private static final int PICTURE_REQUEST_CODE = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_recipe);

        db = new DatabaseHelper(this);

        Bundle bundle = getIntent().getExtras();
        final int keyFromBundle = Integer.parseInt(bundle.getString("recipe_id"));


        recipeList = new ArrayList<>();
        recipeList = db.getAllRecipeList();
        long day = System.currentTimeMillis();
        date = new SimpleDateFormat("dd/MM/yyyy").format(new Date(day));

        EditText recipe_name_edit = (EditText) findViewById(R.id.recipe_name_edit);
        EditText preparation_edit = (EditText) findViewById(R.id.preparation_edit);
        EditText edit_text_input =(EditText) findViewById(R.id.edit_text_input);
        CameraButton = findViewById(R.id.camera_button);

        recipe_name_edit.setText(recipeList.get(keyFromBundle-1).getRecipe_name());
        preparation_edit.setText(recipeList.get(keyFromBundle-1).getPreparation());
        edit_text_input.setText(String.valueOf(recipeList.get(keyFromBundle-1).getCookingTime()));
        image_uri_db = recipeList.get(keyFromBundle-1).getImageURL();
        if( uri != null) {
            InputStream inputStream = null;
            uri = Uri.parse(image_uri_db);

        try {
            inputStream = getContentResolver().openInputStream(uri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        bitmap = BitmapFactory.decodeStream(inputStream);
        CameraButton.setImageBitmap(bitmap);
        } else{
            Message("Uri is null");
        }

        CameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChooseSource();
                }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
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

        String time = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String timejpg = time + ".jpg";

        final File sdDirectory = new File(storageDir, timejpg);
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

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.delete, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        final Bundle bundle2 = getIntent().getExtras();
        final int keyFromBundle = Integer.parseInt(bundle2.getString("recipe_id"));
        recipe_name_edit = (EditText) findViewById(R.id.recipe_name_edit);
        preparation_edit = (EditText) findViewById(R.id.preparation_edit);
        EditText edit_text_input =(EditText) findViewById(R.id.edit_text_input);
        String str_edit = edit_text_input.getText().toString();       //this will get a string
        int int_edit = Integer.parseInt(str_edit);
        uri_to_str = uri.toString();
        switch (item.getItemId()){
            case R.id.delete:
                createAlert(recipeList.get(keyFromBundle-1).getRecipe_id());
                break;
            case R.id.save:
                db.updateRecipe(recipeList.get(keyFromBundle-1).getRecipe_id(), recipe_name_edit.getText().toString(),
                        preparation_edit.getText().toString(),int_edit, uri_to_str,date);
                Intent intentNewRecipe = new Intent(this, MainActivity.class);
                startActivity(intentNewRecipe);
        }

        return super.onOptionsItemSelected(item);
    }
    public void createAlert(final int i){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure?");
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteRecipe(i);
                        Toast.makeText(EditRecipe.this, "Deleted", Toast.LENGTH_SHORT).show();
                        Intent intentDelete = new Intent(EditRecipe.this, MainActivity.class);
                        startActivity(intentDelete);
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void Message(String message){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show();
    }
}


