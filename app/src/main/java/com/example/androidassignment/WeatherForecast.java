package com.example.androidassignment;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherForecast extends AppCompatActivity {

    private static final String TAG = "WeatherForecast";

    private ProgressBar progressBar;
    private ImageView weatherImage;
    private TextView currentTemp, minTemp, maxTemp;
    private Spinner citySpinner;

    private final String[] cities = {
            "Ottawa", "Toronto", "Montreal", "Vancouver",
            "Calgary", "Halifax", "Winnipeg", "Edmonton",
            "Quebec", "Regina"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_weather_forecast);

        progressBar = findViewById(R.id.progressBar);
        weatherImage = findViewById(R.id.weatherImage);
        currentTemp = findViewById(R.id.currentTemp);
        minTemp = findViewById(R.id.minTemp);
        maxTemp = findViewById(R.id.maxTemp);
        citySpinner = findViewById(R.id.citySpinner);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(adapter);

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = cities[position];
                Log.i(TAG, "User selected city: " + selectedCity);
                new ForecastQuery().execute(selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private class ForecastQuery extends AsyncTask<String, Integer, Void> {
        private String minTempValue, maxTempValue, currentTempValue;
        private Bitmap weatherIconBitmap;

        @Override
        protected Void doInBackground(String... params) {
            String city = params[0];
            String urlString = "http://api.openweathermap.org/data/2.5/weather?q=" +
                    city + ",ca&APPID=79cecf493cb6e52d25bb7b7050ff723c&mode=xml&units=metric";

            Log.i(TAG, "Fetching weather for: " + city);
            Log.i(TAG, "Weather API URL: " + urlString);

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                InputStream stream = conn.getInputStream();

                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(true);
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(stream, null);

                int eventType = parser.getEventType();
                String iconName = null;

                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        String tagName = parser.getName();
                        if (tagName.equals("temperature")) {
                            currentTempValue = parser.getAttributeValue(null, "value") + "°C";
                            minTempValue = parser.getAttributeValue(null, "min") + "°C";
                            maxTempValue = parser.getAttributeValue(null, "max") + "°C";
                            Log.i(TAG, "Parsed temperature - Current: " + currentTempValue + ", Min: " + minTempValue + ", Max: " + maxTempValue);
                            publishProgress(25);
                        } else if (tagName.equals("weather")) {
                            iconName = parser.getAttributeValue(null, "icon");
                            Log.i(TAG, "Parsed icon name: " + iconName);
                            publishProgress(50);
                        }
                    }
                    eventType = parser.next();
                }

                if (iconName != null) {
                    String iconFile = iconName + ".png";
                    Log.i(TAG, "Icon filename: " + iconFile);

                    if (fileExists(iconFile)) {
                        Log.i(TAG, "Found icon in local storage: " + iconFile);
                        FileInputStream fis = openFileInput(iconFile);
                        weatherIconBitmap = BitmapFactory.decodeStream(fis);
                        fis.close();
                    } else {
                        Log.i(TAG, "Icon not found locally. Downloading: " + iconFile);
                        String imageUrl = "http://openweathermap.org/img/w/" + iconFile;
                        URL imageURL = new URL(imageUrl);
                        HttpURLConnection imageConn = (HttpURLConnection) imageURL.openConnection();
                        InputStream imageStream = imageConn.getInputStream();
                        weatherIconBitmap = BitmapFactory.decodeStream(imageStream);

                        FileOutputStream outputStream = openFileOutput(iconFile, Context.MODE_PRIVATE);
                        weatherIconBitmap.compress(Bitmap.CompressFormat.PNG, 80, outputStream);
                        outputStream.flush();
                        outputStream.close();
                        Log.i(TAG, "Image downloaded and saved locally: " + iconFile);
                    }
                    publishProgress(100);
                }

            } catch (Exception e) {
                Log.e(TAG, "Error fetching weather data", e);
                currentTempValue = "N/A";
                minTempValue = "N/A";
                maxTempValue = "N/A";
            }

            return null;
        }

        private boolean fileExists(String fname) {
            File file = getBaseContext().getFileStreamPath(fname);
            return file.exists();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
        }

        @Override
        protected void onPostExecute(Void result) {
            currentTemp.setText("Current Temp: " + currentTempValue);
            minTemp.setText("Min Temp: " + minTempValue);
            maxTemp.setText("Max Temp: " + maxTempValue);
            if (weatherIconBitmap != null) {
                weatherImage.setImageBitmap(weatherIconBitmap);
            }
            progressBar.setVisibility(View.INVISIBLE);
        }
    }
}
