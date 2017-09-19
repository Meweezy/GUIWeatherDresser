package com.teckmiles.guiweatherdresser;

import android.animation.ValueAnimator;
import android.os.AsyncTask;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.support.design.widget.Snackbar;
import com.teckmiles.guiweatherdresser.anim_utils.Animation_Utils;
import com.teckmiles.guiweatherdresser.owm_utils.Connection_Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;

public class MainActivity extends AppCompatActivity {

    String appid;
    String result ="";
    ProgressBar mProgressBar;
    TextView mDescriptionTextView;
    TextView mTemperatureTextView;
    TextView mLocationTextView;
    TextView mCurrentDate;
    TextView mLowTemperature;
    ImageView mWeatherIcon;
    CoordinatorLayout mCoordinatorLayout;

    public class DownloadCurrentWeather extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
           mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            try {

                URL url = new URL(params[0]);

                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while( data != -1){

                    char current = (char) data;

                    result += current;

                    data= reader.read();
                }
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            mProgressBar.setVisibility(View.INVISIBLE);
            Log.i("result", result);
            //get JSON Object from result
            try {
                JSONObject forecastJson = new JSONObject(result);
//                Log.i("weather", String.valueOf(forecastJson.getJSONArray("weather").getJSONObject(0).getString("description")));
                Log.i("forecast", String.valueOf(forecastJson.getJSONArray("list").getJSONObject(0)));

//
                //get current weather description
                String description = forecastJson.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("description");
                Log.i("Weather description", description);
                mDescriptionTextView.setText(description);

                //get current temperature
                int currentTemp = (int)(Math.floor(forecastJson.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp")));
                //mTemperatureTextView.setText(String.valueOf(currentTemp)+"\u00b0C");

                Animation_Utils.animateTemp(currentTemp, mTemperatureTextView);

                //get low and max temperature
                int lowTemperature = (int)Math.floor(forecastJson.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp_min"));
                int maxTemperature = (int)Math.floor(forecastJson.getJSONArray("list").getJSONObject(0).getJSONObject("main").getDouble("temp_max"));
                //mLowTemperature.setText(String.valueOf(lowTemperature)+"\u00b0C");

                Animation_Utils.animateTemp(lowTemperature, mLowTemperature);
//
//                //get current location
//                String currentLocation = forecastJson.getString("name");
                String currentLocation = forecastJson.getJSONObject("city").getString("name");
                mLocationTextView.setText(currentLocation);
//
//                //get Current Date
                int unixTime = Integer.parseInt(forecastJson.getJSONArray("list").getJSONObject(0).getString("dt"));
                String todayDate = new SimpleDateFormat("EEE, dd MMM yyyy").format(new Date(unixTime * 1000L));
                mCurrentDate.setText(todayDate);

                //get weather code
                String iconCode = forecastJson.getJSONArray("list").getJSONObject(0).getJSONArray("weather").getJSONObject(0).getString("icon");
                Log.i("Icon Code", iconCode);

                //set icon conditions
                int imageResource = 0;
                if(iconCode.equals("02n")){

                    imageResource = R.drawable.ic_cloudy_night_3;

                }else if(iconCode.equals("02d")) {

                    imageResource = R.drawable.ic_cloudy_day_3;

                }else if(iconCode.equals("01d")){

                    imageResource = R.drawable.ic_day;

                }else if(iconCode.equals("01n")) {

                    imageResource = R.drawable.ic_night;

                }else if(iconCode.equals("03d") ||iconCode.equals("03n")|| iconCode.equals("04d") || iconCode.equals("04n") ) {

                    imageResource = R.drawable.ic_cloudy;

                }else if(iconCode.equals("09d")  ) {

                    imageResource = R.drawable.ic_rainy_6;

                }else if(iconCode.equals("10d")  ) {

                    imageResource = R.drawable.ic_rainy_1;

                }else if(iconCode.equals("10n")  ) {

                    imageResource = R.drawable.ic_rainy_night_2;

                }else if(iconCode.equals("11d") || iconCode.equals("11n")) {

                    imageResource = R.drawable.ic_thunder;

                }else if(iconCode.equals("13d") || iconCode.equals("13n")) {

                    imageResource = R.drawable.ic_thunder;

                }else if(iconCode.equals("50d") || iconCode.equals("50n")) {

                 imageResource = R.drawable.ic_thunder;

                }
                mWeatherIcon.setImageResource(imageResource);

                //get dressing suggestions
                String suggestion = "";
                double windSpeed = forecastJson.getJSONArray("list").getJSONObject(0).getJSONObject("wind").getDouble("speed");
                int cloudiness = forecastJson.getJSONArray("list").getJSONObject(0).getJSONObject("clouds").getInt("all");
                Log.i("clouds", String.valueOf(cloudiness));

                if (windSpeed > 10 && windSpeed <=30){

                    suggestion = "There going to be light breeze. You should consider wearing a Long sleeve shirt";

                } else if (windSpeed > 30){

                    suggestion = "It's going to be a windy day. You should consider a Jacket.";
                } else if(maxTemperature >= 25) {

                    suggestion = "Today will be quite warm. It is perhaps a good weather for T-shirt and Shorts.";

                }else if(maxTemperature >20 && maxTemperature < 25) {

                    suggestion = "The weather is just right. It is perhaps a good weather for T-shirt and light pants.";

                }else if (maxTemperature <= 20 ){

                    suggestion = "The weather will be slightly cold. Consider wearing a light jumper ";
                } else if (maxTemperature <= 10){

                    suggestion = "The weather is quite cold. You should wear a thick jumper and a light jacket ";
                }else if (maxTemperature <= 0){

                    suggestion = "The weather will be freeying cold. You should wear a heavy coat ";
                }else if (maxTemperature <= 0){

                    suggestion = "The weather will be freezing cold. You should wear a heavy coat ";

                }else if (cloudiness <= 10){

                    suggestion = "It will be sunny today. Try wearing a light shirt and sunglasses. ";
                }else if (cloudiness > 10){

                    suggestion = "Try wearing a normal shirt. ";
                }

                //Toast.makeText(MainActivity.this, suggestion, Toast.LENGTH_LONG).show();

                Snackbar snackbar = Snackbar
                        .make(mCoordinatorLayout, suggestion, Snackbar.LENGTH_INDEFINITE);
                snackbar.show();

            } catch (JSONException e) {

                e.printStackTrace();


            }
//            Log.i("rsponse code", String.valueOf(HttpURLConnection.HTTP_NOT_FOUND));


        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCoordinatorLayout = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);
        mProgressBar = (ProgressBar) findViewById(R.id.pg_loading);
        mDescriptionTextView = (TextView) findViewById(R.id.tv_weatherDescription);
        mTemperatureTextView = (TextView) findViewById(R.id.tv_temperature);
        mLocationTextView = (TextView) findViewById(R.id.tv_location);
        mCurrentDate = (TextView) findViewById(R.id.tv_currentDate);
        mLowTemperature = (TextView) findViewById(R.id.tv_temperatureLow);
        mWeatherIcon = (ImageView) findViewById(R.id.weatherIcon);


        DownloadCurrentWeather task = new DownloadCurrentWeather();
        task.execute("http://api.openweathermap.org/data/2.5/forecast?q=Berlin,DE&units=metric&appid="+Connection_Utils.getApiKey() );
    }


}
