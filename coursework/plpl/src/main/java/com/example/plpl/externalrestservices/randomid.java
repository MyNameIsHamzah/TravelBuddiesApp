package com.example.plpl.externalrestservices;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

@RestController
@RequestMapping(path = "api/v1/getrandomid")
public class randomid<output> {

    ArrayList<String> randomIDs = new ArrayList<String>();

   public void getexternalapi() throws IOException{
       URL url = new URL("https://www.random.org/integers/?num=100&min=1&max=1000&col=1&base=10&format=plain&rnd=new");
       HttpURLConnection conn = (HttpURLConnection) url.openConnection();
       conn.setRequestMethod("GET");
       conn.setRequestProperty("Accept", "application/json");

       BufferedReader br = new BufferedReader(
               new InputStreamReader((conn.getInputStream())));

       StringBuilder response = new StringBuilder();
       String responseLine = null;


       String output;

       while ((responseLine = br.readLine()) != null) {
           //response.append(responseLine.trim());
           randomIDs.add(responseLine.trim());
          // br.close();

       }
   }

    public String getRandomID() throws IOException {

       if (randomIDs.isEmpty()){
           getexternalapi();
       }
        String theid;
        theid = randomIDs.get(0);
            randomIDs.remove(0);
            return theid;
        }

        @GetMapping
    public String generateint() throws IOException {
        return getRandomID();
    }
}
