package com.willlawler.mmc;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.graphics.drawable.ColorDrawable;
import android.os.CountDownTimer;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import javax.security.auth.login.LoginException;


public class MainActivity extends AppCompatActivity implements whoWonFragment.NoticeDialogListener, SelectColor.NoticeDialogListener{


    String JSON_FILE_NAME = "gameListSave.json";
    String jsonString;
    String JSON_CURRENT_GAME = "currentGameSave.json";
    String jsonCurrentString;
    int gameID =0;
    int startingHealth = 20;
    TextView tvplayerOneHealth;
    TextView tvplayerTwoHealth;
    EditText playerOneName;
    EditText playerTwoName;
    String[] nameArray;
    Game currentGame = new Game();
    CountDownTimer cTimer = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);



        //This sets the background colour
        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#9E9E9E"));

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        tvplayerTwoHealth = (TextView) findViewById(R.id.currenthealthcountplayerTwo);
        tvplayerOneHealth = (TextView) findViewById(R.id.currenthealthcountplayerOne);
        playerOneName = (EditText)findViewById(R.id.playerOneName);
        playerTwoName = (EditText)findViewById(R.id.playerTwoName);


    }

    public void printJsons(){
        String printout = readFile(JSON_FILE_NAME);
        Log.d("JSON big Content", printout);
        printout = readFile(JSON_CURRENT_GAME);
        Log.d("JSON current game", printout);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.new_game:
                resetGame();
                return true;
            case R.id.scorespage:
                JSONsaveGameCurrent();
                printJsons();
                goToListScreenTwo();
                return true;
            case R.id.commanderhealth:
                fortyHealthGame();
                return true;
            case R.id.normalhealth:
                twentyHealthGame();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void playerOneHealthIncrease(View view){
        counterTimer();
        currentGame.playerOneHealth++;
        tvplayerOneHealth.setText(Integer.toString(currentGame.playerOneHealth));
    }

    public void playerOneHealthDecrease(View view){
        counterTimer();
        currentGame.playerOneHealth--;
        tvplayerOneHealth.setText(Integer.toString(currentGame.playerOneHealth));
    }

    public void playerTwoHealthIncrease(View view){
        counterTimer();
        currentGame.playerTwoHealth++;
        tvplayerTwoHealth.setText(Integer.toString(currentGame.playerTwoHealth));
    }

    public void playerTwoHealthDecrease(View view){
        counterTimer();
        currentGame.playerTwoHealth--;
        tvplayerTwoHealth.setText(Integer.toString(currentGame.playerTwoHealth));
    }



    public void resetGameNumbers(){
        currentGame.playerOneHealth = startingHealth;
        currentGame.playerTwoHealth = startingHealth;
        tvplayerOneHealth.setText(Integer.toString(currentGame.playerOneHealth));
        tvplayerTwoHealth.setText(Integer.toString(currentGame.playerTwoHealth));

    }

    public void twentyHealthGame(){
        startingHealth = 20;
        resetGame();
    }

    public void fortyHealthGame(){
        startingHealth = 40;
        resetGame();
    }

    public void goToListScreenTwo(){
        Intent intent = new Intent(this, GameList.class);
        startActivity(intent);
    }

   // this is turning into a more "save score change" method
    public void saveScore(){
        Log.d("SAVING SCORE", "Saved");

        currentGame.scoreArray.add(Integer.toString(currentGame.playerOneHealth)+","+Integer.toString(currentGame.playerTwoHealth));
        currentGame.playerOneName = playerOneName.getText().toString();
        currentGame.playerTwoName = playerTwoName.getText().toString();


        /*
        try{
            outputStream = openFileOutput(saveFile, Context.MODE_APPEND);
            //String line =  playerOneName.getText().toString() + ": " + Integer.toString(playerOne.health)+ "  -  " + playerTwoName.getText().toString() + ": " + Integer.toString(playerTwo.health) + "\n";
            String line = Integer.toString(currentGame.playerOneHealth)+","+Integer.toString(currentGame.playerTwoHealth)+"\n";
            outputStream.write(line.getBytes());
            outputStream.close();
        } catch (Exception e){
            e.printStackTrace();
        }
        */
    }

    public void JSONsaveGame(){
        Log.d("json", "atempted to JSONsave");

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String date = df.format(Calendar.getInstance().getTime());

        jsonString = readFile(JSON_FILE_NAME);
        try{
            JSONObject jsonObj =  new JSONObject(jsonString);
            //JSONObject jsonObj =  new JSONObject();

            int jsonLength = jsonObj.length();
            currentGame.gameID = jsonLength++;
            Log.d("jsonlength = ", Integer.toString(jsonLength));

            JSONObject jsonGame = new JSONObject(); // we need another object to store the address
            jsonGame.put("dateTime", date);
            jsonGame.put("playerOneName", currentGame.playerOneName);
            jsonGame.put("playerTwoName", currentGame.playerTwoName);
            jsonGame.put("winner", Integer.toString(currentGame.winningPlayer));
            //jsonGame.put("score", currentGame.score);
            JSONArray scoreArray = new JSONArray();

            for (int i=0; i<currentGame.scoreArray.size(); i++){
                scoreArray.put(currentGame.scoreArray.get(i));
            }
            jsonGame.put("score", scoreArray);

            // We add the object to the main object

            jsonObj.put(Integer.toString(currentGame.gameID), jsonGame);
            //jsonObj.put(Integer.toString(gameID), jsonGame);


            writeToFile(jsonObj.toString(), JSON_FILE_NAME);
        }
        catch (final JSONException e) {
            Log.e("JSON", "Json parsing error: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
        gameID++;
    }

    //This Methods is to save the current game is a separate JSON file to the permanent one so That I can view the current game score.
    public void JSONsaveGameCurrent(){


        //Delete old current game JSON and make a new one
        this.deleteFile(JSON_CURRENT_GAME);
        try {
            JSONObject jsonObj = new JSONObject();
            writeToFile(jsonObj.toString(), JSON_CURRENT_GAME);
        } catch (Exception e){
            e.printStackTrace();
        }

        jsonString = readFile(JSON_CURRENT_GAME);
        try{

            JSONObject jsonGame = new JSONObject(); // we need another object to store the address
            jsonGame.put("playerOneName", currentGame.playerOneName);
            jsonGame.put("playerTwoName", currentGame.playerTwoName);
            JSONArray scoreArray = new JSONArray();

            for (int i=0; i<currentGame.scoreArray.size(); i++){
                scoreArray.put(currentGame.scoreArray.get(i));
            }
            jsonGame.put("score", scoreArray);


            writeToFile(jsonGame.toString(),JSON_CURRENT_GAME);
        }
        catch (final JSONException e) {
            Log.e("JSON", "Json parsing error: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    // this timer waits for 1 second after user input and then saves the game score.
    //timer restarts everytime someone changes the score
    public void counterTimer() {
        Log.d("Timer", "timer called");
        if(cTimer!=null){
            cTimer.cancel();
        }
        cTimer = null;
        cTimer = new CountDownTimer(1000, 1000) {

            @Override
            public void onTick(long l) {
                Log.d("Timer", "tick");
            }

            @Override
            public void onFinish() {
                Log.d("Timer", "game should be saved");
                saveScore();
            }
        }.start();
    }

    public void newGame(){
        saveScore();
        JSONsaveGame();
        currentGame = new Game();
        resetGameNumbers();
        currentGame.scoreArray.add(Integer.toString(startingHealth)+","+Integer.toString(startingHealth));
    }

     public void changeColor (View view){
        final View playerButton = view;

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_select_color);
        ImageButton whiteButton = (ImageButton)dialog.findViewById(R.id.WhiteButton);
        whiteButton.setOnClickListener(new View.OnClickListener(){

            @Override
            final public void onClick(View view) {
                if(playerButton.getId() == R.id.ColourButtonP1){
                    ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.PlayerOneBackground);
                    layout.setBackgroundColor(Color.parseColor("#fff9c4"));
                    Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
                    toolbar.setBackgroundColor(Color.parseColor("#fff9c4"));
                }
                if(playerButton.getId() == R.id.ColourButtonP2){
                    ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.PlayerTwoBackground);
                    layout.setBackgroundColor(Color.parseColor("#fff9c4"));
                }
                dialog.dismiss();
            }
        });
        ImageButton blueButton = (ImageButton)dialog.findViewById(R.id.BlueButton);
        blueButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(playerButton.getId() == R.id.ColourButtonP1){
                    ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.PlayerOneBackground);
                    layout.setBackgroundColor(Color.parseColor("#64b5f6"));
                    Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
                    toolbar.setBackgroundColor(Color.parseColor("#64b5f6"));
                }
                if(playerButton.getId() == R.id.ColourButtonP2){
                    ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.PlayerTwoBackground);
                    layout.setBackgroundColor(Color.parseColor("#64b5f6"));
                }
                dialog.dismiss();
            }
        });
        ImageButton greenButton = (ImageButton)dialog.findViewById(R.id.GreenButton);
        greenButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(playerButton.getId() == R.id.ColourButtonP1){
                    ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.PlayerOneBackground);
                    layout.setBackgroundColor(Color.parseColor("#a5d6a7"));
                    Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
                    toolbar.setBackgroundColor(Color.parseColor("#a5d6a7"));
                }
                if(playerButton.getId() == R.id.ColourButtonP2){
                    ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.PlayerTwoBackground);
                    layout.setBackgroundColor(Color.parseColor("#a5d6a7"));
                }
                dialog.dismiss();
            }
        });
        ImageButton redButton = (ImageButton)dialog.findViewById(R.id.RedButton);
        redButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(playerButton.getId() == R.id.ColourButtonP1){
                    ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.PlayerOneBackground);
                    layout.setBackgroundColor(Color.parseColor("#ef9a9a"));
                    Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
                    toolbar.setBackgroundColor(Color.parseColor("#ef9a9a"));
                }
                if(playerButton.getId() == R.id.ColourButtonP2){
                    ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.PlayerTwoBackground);
                    layout.setBackgroundColor(Color.parseColor("#ef9a9a"));
                }
                dialog.dismiss();
            }
        });
        ImageButton blackButton = (ImageButton)dialog.findViewById(R.id.BlackButton);
        blackButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(playerButton.getId() == R.id.ColourButtonP1){
                    ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.PlayerOneBackground);
                    layout.setBackgroundColor(Color.parseColor("#9e9e9e"));
                    Toolbar toolbar = (Toolbar)findViewById(R.id.my_toolbar);
                    toolbar.setBackgroundColor(Color.parseColor("#9e9e9e"));
                }
                if(playerButton.getId() == R.id.ColourButtonP2){
                    ConstraintLayout layout = (ConstraintLayout)findViewById(R.id.PlayerTwoBackground);
                    layout.setBackgroundColor(Color.parseColor("#9e9e9e"));
                }
                dialog.dismiss();
            }
        });
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void resetGameButton(View view) {
        resetGame();
    }

    public void resetGame(){
        nameArray = new String[]{playerOneName.getText().toString(), playerTwoName.getText().toString(), "Discard Game"};
        //position 0 is player 1, position 1 is player 2
        DialogFragment newFragment = whoWonFragment.newInstance(nameArray);
        newFragment.show(getSupportFragmentManager(), "Score Confirm");
    }
    // this is the listener for the game reset dialog box
    // it takes the winner of teh last game and saves the scores
    public void onClick(DialogFragment dialog, int position){
        switch(position){
            case 0:
                // player 1 won
                currentGame.winningPlayer = 1;
                currentGame.playerTwoHealth = 0;
                newGame();
                break;
            case 1:
                // player 2 won
                currentGame.winningPlayer = 2;
                currentGame.playerOneHealth = 0;
                newGame();
                break;
            case 2:
                // discard game
                resetGameNumbers();
        }
    }

    public String readFile(String fileName){
        String data ="";

        try {
            InputStream inputStream = openFileInput(fileName);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString;
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }

                inputStream.close();
                data = stringBuilder.toString();
            }

        }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }
        return data;
    }

    public void writeToFile(String data, String Name){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(Name, Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }


    }




    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general_menu, menu);
        return true;
    }

    */

}
