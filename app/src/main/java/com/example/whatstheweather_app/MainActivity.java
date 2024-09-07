package com.example.whatstheweather_app;

import androidx.appcompat.app.AppCompatActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import org.json.JSONObject;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView weatherResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cityName = findViewById(R.id.editText);
        weatherResult = findViewById(R.id.resultTextView);
        Button getWeatherButton = findViewById(R.id.button);

        getWeatherButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = cityName.getText().toString();
                if (!city.isEmpty()) {
                    getWeatherData(city);
                } else {
                    weatherResult.setText("Please enter a city name");
                }
            }
        });
    }

    public void getWeatherData(String city) {
        try {
            // Encode the city name to handle spaces and special characters
            String encodedCityName = URLEncoder.encode(city, "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=YOUR_API_KEY&units=metric");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            if (s == null || s.isEmpty()) {
                weatherResult.setText("Error retrieving weather data");
                return;
            }

            try {
                JSONObject jsonObject = new JSONObject(s);

                // Parsing temperature and weather description
                JSONObject main = jsonObject.getJSONObject("main");
                double temperature = main.getDouble("temp");

                JSONObject weatherPart = jsonObject.getJSONArray("weather").getJSONObject(0);
                String description = weatherPart.getString("description");

                String weatherInfo = "Temperature: " + temperature + "°C\n" + "Description: " + description;
                weatherResult.setText(weatherInfo);

            } catch (Exception e) {
                e.printStackTrace();
                weatherResult.setText("Error parsing weather data");
            }
        }
    }
}
