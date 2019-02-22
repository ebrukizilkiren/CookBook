package com.example.rmeysa.cookbookversion2;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShowRecipe extends AppCompatActivity {
    DatabaseHelper db;
    List<Recipe> recipeList;
    TextView edit_text_input;
    private TextView textview_timer;
    private Button button_start;
    private Button button_pause;
    private Button button_reset;
    private  long LeftTime;
    private  long startTime;
    private long endTime;
    private boolean HasRun;
    CountDownTimer countdownTimer;
    SharedPreferences mypreference;
    SharedPreferences.Editor editor;
    ImageView recipeImageView;
    String selectedImageUriStr;
    Uri selectedImageUri;
    Bitmap bitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_recipe);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DatabaseHelper(this);

        recipeList = new ArrayList<>();
        recipeList = db.getAllRecipeList();
        recipeImageView = (ImageView) findViewById(R.id.recipeImageView);

        final Bundle bundle = getIntent().getExtras();
        final int key_from_bundle = Integer.parseInt(bundle.getString("recipe_id"));


        //Updating textViews
        TextView recipe_name_text = (TextView) findViewById(R.id.recipe_name_text);
        TextView preparation_text = (TextView) findViewById(R.id.preparation_text);
        edit_text_input = (TextView) findViewById(R.id.edit_text_input);
        recipe_name_text.setText(recipeList.get(key_from_bundle-1).getRecipe_name());
        preparation_text.setText(recipeList.get(key_from_bundle-1).getPreparation());
        edit_text_input.setText(String.valueOf(recipeList.get(key_from_bundle-1).getCookingTime()));
        selectedImageUriStr = recipeList.get(key_from_bundle-1).getImageURL();
        preparation_text.setMovementMethod(new ScrollingMovementMethod());
        camera();

        Timer();


    }
    public void camera() {
        InputStream inputStream = null;
        try {
            if(selectedImageUriStr != null ) {
                selectedImageUri = Uri.parse(selectedImageUriStr);
                inputStream = getContentResolver().openInputStream(selectedImageUri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                recipeImageView.setImageBitmap(bitmap);
            }
            else{

            }


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.edit, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item){
        final Bundle bundle2 = getIntent().getExtras();
        final int key_from_bundle = Integer.parseInt(bundle2.getString("recipe_id"));
        switch (item.getItemId()){
            case R.id.edit:
                Intent intent_edit = new Intent(ShowRecipe.this, EditRecipe.class);
                Bundle bundle = new Bundle();
                bundle.putString("recipe_id", String.valueOf(key_from_bundle));
                intent_edit.putExtras(bundle);
                startActivity(intent_edit);
                break;
            case R.id.delete:
                createAlert(recipeList.get(key_from_bundle-1).getRecipe_id());
                break;
        }

        return super.onOptionsItemSelected(item);
    }
    public void Timer() {

        textview_timer = (TextView) findViewById(R.id.textview_timer); // 00.00
        button_start = (Button) findViewById(R.id.button_start);
        button_pause = (Button) findViewById(R.id.button_pause);
        button_reset = (Button) findViewById(R.id.button_reset);

        button_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textview_timer.setText("");
                String time_input = edit_text_input.getText().toString();

                long parse_input_mil = Long.parseLong(time_input) * 60000;
                if (parse_input_mil == 0) {
                    Toast.makeText(ShowRecipe.this, "Cannot be zero", Toast.LENGTH_SHORT).show();
                    return;
                }
                startTime = parse_input_mil;
                resetTimer();


                if (!HasRun) {
                    startTimer();
                    pause_visible();
                    button_reset.setVisibility(View.INVISIBLE);
                }
            }
        });
        button_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (HasRun) {
                    pauseTimer();
                    pause_invisible();
                }
            }
        });

        button_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetTimer();
                pause_invisible();
                start_visible();

            }
        });
    }

    private void startTimer() {
        endTime = System.currentTimeMillis() + LeftTime;
        countdownTimer = new CountDownTimer(LeftTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LeftTime = millisUntilFinished;
                CalculateTime();
            }

            @Override
            public void onFinish() {
                HasRun = false;
                AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                MediaPlayer thePlayer = MediaPlayer.create(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

                try {
                    thePlayer.setVolume((float) (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) / 7.0), (float) (audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION) / 7.0));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                thePlayer.start();

            }
        }.start();

        HasRun = true;
        start_invisible();
    }
    private void pauseTimer() {
        countdownTimer.cancel();
        HasRun = false;
        button_reset.setVisibility(View.VISIBLE);
        start_visible();

    }

    private void resetTimer() {
        LeftTime = startTime;
        CalculateTime();
        start_visible();
        button_reset.setVisibility(View.INVISIBLE);

    }

    private void CalculateTime() {
        int hour = (int) (LeftTime / 1000) / 3600;
        int minute = (int) ((LeftTime / 1000) % 3600) / 60;
        int second = (int) (LeftTime / 1000) % 60;

        String LeftTimeString;
        if (hour > 0) {
            LeftTimeString = String.format(Locale.getDefault(), "%d:%02d:%02d", hour, minute, second);
        } else {
            LeftTimeString = String.format(Locale.getDefault(), "%02d:%02d", minute, second);
        }
        textview_timer.setText(LeftTimeString);
    }
    @Override
    protected void onStop() {
        super.onStop();

        mypreference= getSharedPreferences("mypreference", MODE_PRIVATE);
        editor = mypreference.edit();

        editor.putLong("startTime", startTime);
        editor.putLong("LeftTime", LeftTime);
        editor.putBoolean("hasRunning", HasRun);
        editor.putLong("endTime", endTime);

        editor.apply();

        if (countdownTimer != null) {
            countdownTimer.cancel();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        SharedPreferences mypreference = getSharedPreferences("mypreference", MODE_PRIVATE);
        startTime = mypreference.getLong("startTime", 600000);
        LeftTime = mypreference.getLong("LeftTime", startTime);
        HasRun = mypreference.getBoolean("hasRunning", false);


        CalculateTime();

        if (HasRun) {
            endTime = mypreference.getLong("endTime", 0);
            LeftTime = endTime - System.currentTimeMillis();

            if (LeftTime >= 0) {
                startTimer();
                pause_visible();


            } else {
                LeftTime = 0;
                HasRun = false;
                CalculateTime();
                start_visible();

            }

        }
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
                        //Delete
                        db.deleteRecipe(i);
                        Toast.makeText(ShowRecipe.this, "Deleted", Toast.LENGTH_SHORT).show();
                        Intent intentDelete = new Intent(ShowRecipe.this, MainActivity.class);
                        startActivity(intentDelete);
                        dialog.dismiss();
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Do nothing
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void start_visible(){
        button_start.setVisibility(View.VISIBLE);
    }
    private void start_invisible(){
        button_start.setVisibility(View.INVISIBLE);
    }
    private void pause_visible(){
        button_pause.setVisibility(View.VISIBLE);
    }
    private void pause_invisible(){
        button_pause.setVisibility(View.INVISIBLE);
    }
}

