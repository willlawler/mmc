package com.willlawler.mmc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.support.v4.app.Fragment;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class GameList extends FragmentActivity implements resetScoreConfirm.NoticeDialogListener{


    String JSON_FILE_NAME = "gameListSave.json";
    ListView mListView;
    ArrayList<String> gameListArray;
    String jsonString;
    //TextView textView3;
    String winner;
    ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);

        populateTable();
    }

    public void populateTable(){
        gameListArray = new ArrayList<>();
        //textView3 = (TextView) findViewById(R.id.textView3);
        getWindow().getDecorView().setBackgroundColor(Color.parseColor("#9E9E9E"));

        //use this one to read from assets file
        //

        //use this one to read from internal storage file.
        jsonString = readFile();


        //this line should display the whole json file. This is super helpful for debugging. Also sometimes I forget what the Json looks like.
        //textView3.setText(jsonString);
        try{
            JSONObject jsonObj =  new JSONObject(jsonString);

            //loop through all games

            for (int i=jsonObj.length()-1;i>=0;i--){
                StringBuilder stringBuilder = new StringBuilder();


                JSONObject g = jsonObj.getJSONObject(Integer.toString(i));
                winner = g.getString("winner");
                if(winner.equals("1")){
                    stringBuilder.append(g.getString("dateTime"));
                    stringBuilder.append(" - ");
                    stringBuilder.append("**");
                    stringBuilder.append(g.getString("playerOneName"));
                    stringBuilder.append("**");
                    stringBuilder.append(" vs. ");
                    stringBuilder.append(g.getString("playerTwoName"));
                    gameListArray.add(stringBuilder.toString());
                }
                else if(winner.equals("2")){
                    stringBuilder.append(g.getString("dateTime"));
                    stringBuilder.append(" - ");
                    stringBuilder.append(g.getString("playerOneName"));
                    stringBuilder.append(" vs. ");
                    stringBuilder.append("**");
                    stringBuilder.append(g.getString("playerTwoName"));
                    stringBuilder.append("**");
                    gameListArray.add(stringBuilder.toString());
                }
            }
        }
        catch (final JSONException e) {
            Log.e("JSON", "Json parsing error: " + e.getMessage());
        }

        mListView = (ListView) findViewById(R.id.gamelist);
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, gameListArray);
        mListView.setAdapter(adapter);

        final Context context = this;

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                Intent intent = new Intent(context, GameDetail.class);

                //this line is the key for sending extra information to the new activity
                intent.putExtra("position", Integer.toString(position));
                try{
                    JSONObject jsonObj =  new JSONObject(jsonString);
                    intent.putExtra("Jsonlength", Integer.toString(jsonObj.length()-1));
                }
                catch (final JSONException e) {
                    Log.e("JSON", "Json parsing error: " + e.getMessage());
                }

                startActivity(intent);
            }

        });
    }

/*
    public String loadJSONFromAsset() {
        String json;
        try {
            InputStream is = getAssets().open(FILE_NAME);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");

        } catch (IOException ex) {
            Log.d("JSONLOADER","failed for some reason");
            ex.printStackTrace();
            return null;
        }
        return json;
    }
    */

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

        return data;
    }


    public void fileDelete(View view){

        DialogFragment newFragment = new resetScoreConfirm();
        newFragment.show(getSupportFragmentManager(), "dialog");

    }

    public void CurrentGame(View view){
        final Context context = this;
        Intent intent = new Intent(context, GameDetailCurrent.class);
        startActivity(intent);

    }

    public void showNoticeDialog() {
        // Create an instance of the dialog fragment and show it
        DialogFragment dialog = new resetScoreConfirm();
        dialog.show(getSupportFragmentManager(), "dialog");

    }
    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        this.deleteFile(JSON_FILE_NAME);
        createBlankJson();
        populateTable();

    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        // User touched the dialog's negative button
    }

    public void createBlankJson(){
        try {
            JSONObject jsonObj = new JSONObject();
            writeToFile(jsonObj.toString());
        } catch (Exception e){
            e.printStackTrace();
        }
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
}
