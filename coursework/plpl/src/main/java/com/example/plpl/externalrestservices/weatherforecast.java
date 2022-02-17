package com.example.plpl.externalrestservices;

import org.json.JSONException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;


@RestController
@RequestMapping(path = "api/v1/getweatherforecast")
public class weatherforecast {

    public class Weather{
        public String date;
        public String maxtempC;
        public String maxtempF;
        public String mintempC;
        public String mintempF;
        public String avgtempC;
        public String avgtempF;
        public String totalSnow_cm;
        public String sunHour;
        public String uvIndex;

        public String getMaxtempC(){
            return maxtempC;
        }
        public void setMaxtempC(String maxtempC) {
            this.maxtempC = maxtempC;
        }

        public String getAvgtempC(){
            return avgtempC;
        }
        public void setAvgtempC(String avgtempC){
            this.avgtempC = avgtempC;
        }


        public String toString(){
            return "average temp: " + avgtempC;
        }
    }

    //creating the txt file
    File myObj = new File("weatherdata.txt");

    @GetMapping
    public String getForecast(
            @RequestParam (value = "latlon") String latlon, @RequestParam (value = "date") String date) throws IOException, JSONException {

        FileWriter myWriter = new FileWriter("weatherdata.txt");
        //String latlon = "52.9108067,-1.1829861";
       //String date = "2022-01-19";

        URL url = new URL("http://api.worldweatheronline.com/premium/v1/weather.ashx?key=8dc78fd9e708440e87f201726221101&q=" + latlon + "&format=json&date=" + date);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        BufferedReader br = new BufferedReader(
                new InputStreamReader((conn.getInputStream())));

        StringBuilder weather_response = new StringBuilder();
        String weather_call = null;


        while ((weather_call = br.readLine()) != null) {

            weather_response.append(weather_call.trim());

        }

        String apiResponse = weather_response.toString();

        //System.out.println(apiResponse);

        myWriter.write(apiResponse);

        GsonBuilder builder = new GsonBuilder();

        builder.setPrettyPrinting();
        Gson gson = builder.create();
        JSONObject json = new JSONObject(apiResponse);
        JSONObject dataJsonArray = json.getJSONObject("data");
        JSONArray xd = dataJsonArray.getJSONArray("weather");
        List<String> list = new ArrayList<String>();
        for (int i=0; i<xd.length(); i++) {
            list.add( xd.getString(i) );
        }
        String[] stringArray = list.toArray(new String[list.size()]);
        Weather weather = gson.fromJson(stringArray[0], Weather.class);
        //System.out.println("Average Temperature: " + weather.avgtempC + "°C");
        //System.out.println("Max Temperature: " + weather.maxtempC + "°C");
        //System.out.println("Min Temperature: " + weather.mintempC + "°C");

        String avgTemp =  "Average Daily Temperature: " + weather.avgtempC + "°C";
        String maxTemp = "Max Temperature: " + weather.maxtempC + "°C";
        String minTemp = "Min Temperature: " + weather.mintempC + "°C";

        return avgTemp + ", " + maxTemp + ", " + minTemp;

    }



}
