package com.willlawler.mmc;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class GameDetail extends Activity {

    //String FILE_NAME = getResources().getString(R.string.json_file_name);

    String JSON_FILE_NAME = "gameListSave.json";
    String jsonString;
    ArrayList<TableRow> rowArray = new ArrayList<>();
    ArrayList<TextView> leftTVArray = new ArrayList<>();
    ArrayList<TextView> rightTVArray = new ArrayList<>();

    String objPositionClick;
    String objPosition;
    String objLength;
    ArrayList<String> scoreArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

            getWindow().getDecorView().setBackgroundColor(Color.parseColor("#9E9E9E"));

            jsonString = readFile();

            Log.d("JSON Content", jsonString);


            //This line gets the extra information I generated when I made the intent and started this activity
            objPositionClick = this.getIntent().getExtras().getString("position");
            objLength = this.getIntent().getExtras().getString("Jsonlength");

            objPosition = Integer.toString(Integer.parseInt(objLength) - Integer.parseInt(objPositionClick));

            try{
                JSONObject jsonObj =  new JSONObject(jsonString);
                JSONObject g = jsonObj.getJSONObject(objPosition);
                JSONArray jsonScoreArray = g.getJSONArray("score");

                TableLayout tL = (TableLayout) findViewById(R.id.gameDetailTable);
                TableRow tbRow0 = new TableRow(this);
                TextView tv0 = new TextView(this);

                tv0.setText(g.getString("playerOneName"));
                tv0.setGravity(Gravity.CENTER);
                tbRow0.addView(tv0);

                TextView tv1 = new TextView(this);
                tv1.setText( g.getString("playerTwoName"));
                tv1.setGravity(Gravity.CENTER);
                tbRow0.addView(tv1);


            //tL.setGravity(Gravity.CENTER);

            tL.addView(tbRow0);
            for (int i=0;i<jsonScoreArray.length(); i++){
                scoreArray.add(jsonScoreArray.getString(i));
                rowArray.add(new TableRow(this));
                String[] splitString = scoreArray.get(i).split(",");

                leftTVArray.add(new TextView(this));
                leftTVArray.get(i).setText(splitString[0]);
                leftTVArray.get(i).setGravity(Gravity.CENTER);

                rightTVArray.add(new TextView(this));
                rightTVArray.get(i).setText(splitString[1]);
                rightTVArray.get(i).setGravity(Gravity.CENTER);

                rowArray.get(i).addView(leftTVArray.get(i));
                rowArray.get(i).addView(rightTVArray.get(i));

                tL.addView(rowArray.get(i));
            }

        }
        catch (final JSONException e) {
            Log.e("JSON", "Json parsing error: " + e.getMessage());
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

        return data;
    }

}
