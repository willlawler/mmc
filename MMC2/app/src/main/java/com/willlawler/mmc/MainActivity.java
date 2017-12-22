package com.willlawler.mmc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.os.CountDownTimer;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
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

public class MainActivity extends AppCompatActivity implements whoWonFragment.NoticeDialogListener{

    String JSON_FILE_NAME = "gameListSave.json";
    String jsonString;
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

    public void twentyHealthGame(View view){
        startingHealth = 20;
        resetGame();
    }

    public void fortyHealthGame(View view){
        startingHealth = 40;
        resetGame();
    }


    public void goToListScreen(View view){
        Intent intent = new Intent(this, GameList.class);
        startActivity(intent);
    }

   // this is turning into a more "save score change" method
    public void saveScore(){
        Log.d("IO", "Game saved");
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

        jsonString = readFile();
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

            Log.d("Outfile", "test" + jsonObj.toString());
            writeToFile(jsonObj.toString());
        }
        catch (final JSONException e) {
            Log.e("JSON", "Json parsing error: " + e.getMessage());
        } catch (Exception e){
            e.printStackTrace();
        }
        gameID++;
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

    public String readFile(){
        String data ="";

        try {
            InputStream inputStream = openFileInput(JSON_FILE_NAME);

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
        Log.d("Outfile", "test" + data);
        return data;
    }

    public void writeToFile(String data){
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(openFileOutput(JSON_FILE_NAME, Context.MODE_PRIVATE));
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
