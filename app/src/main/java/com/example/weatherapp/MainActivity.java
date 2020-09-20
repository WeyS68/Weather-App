package com.example.weatherapp;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCity;
    private TextView textViewShowResult;

    private String Weather_url = "http://api.weatherapi.com/v1/current.json?key=35b40cc2536b43c88e2165638201809&q=%s";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextCity = findViewById(R.id.editTextCity);
        textViewShowResult = findViewById(R.id.textViewShowResult);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
    }

    public void FindOutTheWeather(View view) {
        String city = editTextCity.getText().toString().trim();
        if (!city.isEmpty()){
            DownloadWeatherTask task = new DownloadWeatherTask();
            String url = String.format(Weather_url, city);
            JSONObject jsonObject = null;
            try {
                String rds = task.execute(url).get();
                jsonObject = new JSONObject(rds);
                JSONObject obName = jsonObject.getJSONObject("location");
                String name = obName.getString("name");

                JSONObject obTemp = jsonObject.getJSONObject("current");
                String temperature = obTemp.getString("temp_c");

                JSONObject obCurrent = jsonObject.getJSONObject("current");
                JSONObject obCond = obCurrent.getJSONObject("condition");
                String condition = obCond.getString("text");

                String weather = String.format(" %s\n Temperature : %s \n On Street : %s" , name , temperature , condition);
                textViewShowResult.setText(weather);

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        }
    }



    private static class DownloadWeatherTask extends AsyncTask<String , Void , String>{
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            HttpURLConnection urlConnection = null;
            StringBuilder result = new StringBuilder();
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line = bufferedReader.readLine();
                while (line != null){
                    result.append(line);
                    line = bufferedReader.readLine();
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (urlConnection != null){
                    urlConnection.disconnect();
                }
            }
            return result.toString();

        }

    }
}