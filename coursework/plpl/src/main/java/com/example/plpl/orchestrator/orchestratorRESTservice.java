package com.example.plpl.orchestrator;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.File;  // Import the File class
import java.util.Scanner; // Import the Scanner class to read text files
import com.example.plpl.messagequeues.rabbitMQpublisher;
import com.example.plpl.messagequeues.rabbitMQsubscriber;


@RestController
@RequestMapping(path = "api/v1/orchestratorservice")
public class orchestratorRESTservice {

    public class userdetails{
        String userid;
        String msgid;
        String latlon;
        String date;
        String message;
        public String getUserid() {return userid;}
        public void setUserid(String userid) {this.userid = userid;}

        public String getMsgid() {return msgid;}
        public void setMsgid(String msgid) {this.msgid = msgid;}

        public String getLatlon() {return latlon;}
        public void setLatlon(String latlon) {this.latlon = latlon;}

        public String getDate() {return date;}
        public void setDate(String date) {this.date = date;}
    }

    public class userintents{
        String userid;
        String msgid;
        String proposaluserid;
        String message;

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getMsgid() {
            return msgid;
        }

        public void setMsgid(String msgid) {
            this.msgid = msgid;
        }

        public String getProposaluserid() {
            return proposaluserid;
        }

        public void setProposaluserid(String proposaluserid) {
            this.proposaluserid = proposaluserid;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }

    @RequestMapping(path = "/generateid")
    public String generateID() throws Exception{

        URL url = new URL(" http://localhost:8080/api/v1/getrandomid");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        BufferedReader br = new BufferedReader(
                new InputStreamReader((conn.getInputStream())));

        StringBuilder response = new StringBuilder();
        String responseLine = null;

        while ((responseLine = br.readLine()) != null) {
            response.append(responseLine.trim());
        }
        String output = response.toString();
        return output;
    }

    @RequestMapping(path = "/submitoffer")
    public void submitoffer(
            @RequestParam (value = "latlon") String latlon, @RequestParam (value = "date") String date, @RequestParam (value = "userid") String userid ) throws Exception{

        orchestratorRESTservice orchestratorRESTservice = new orchestratorRESTservice();

       // String userid = orchestratorRESTservice.generateID();
        String msgid = orchestratorRESTservice.generateID();
       // String latandlon = latlon;
        //String thedate = date;

        String thedata = "{\"data\": {\"fields\": [{\"userid\":\""+ userid+"\",\"msgid\":\""+msgid+"\",\"latlon\":\""+latlon+"\",\"date\":\""+date+"\"}]}}";

                //"userid: "+ userid + " latlon:  " + latandlon + " date:  " + thedate;

        rabbitMQpublisher.publish(thedata, "TRAVEL_OFFERS");

        //System.out.println(thedata);
    }

    @RequestMapping(path = "/querymessage")
    public List querymessage() throws Exception, JSONException {
        List<String> themessages = new ArrayList<String>();
        rabbitMQsubscriber.subscribe("TRAVEL_OFFERS");

        try {
                File messagesfile = new File("themessage.txt");
                Scanner myReader = new Scanner(messagesfile);
           // FileWriter myWriter = new FileWriter(messagesfile);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    //System.out.println("we got it: " +data);
                   // myWriter.write("");
                    //myWriter.close();

                    GsonBuilder builder = new GsonBuilder();
                    builder.setPrettyPrinting();
                    Gson gson = builder.create();
                    JSONObject json = new JSONObject(data);
                    JSONObject dataJsonArray = json.getJSONObject("data");
                    JSONArray xd = dataJsonArray.getJSONArray("fields");
                    List<String> list = new ArrayList<String>();
                    for (int i = 0; i < xd.length(); i++) {
                        list.add(xd.getString(i));
                    }
                    String[] stringArray = list.toArray(new String[list.size()]);
                    orchestratorRESTservice.userdetails info = gson.fromJson(stringArray[0], orchestratorRESTservice.userdetails.class);

                    String theUserID = info.userid;
                    String theMsgID = info.msgid;
                    String theLatlon = info.latlon;
                    String theDate = info.date;


                    //add weather details onto output:
                    URL url = new URL(" http://localhost:8080/api/v1/getweatherforecast?latlon=" + theLatlon + "&date=" + theDate);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Accept", "application/json");

                    BufferedReader br = new BufferedReader(
                            new InputStreamReader((conn.getInputStream())));

                    StringBuilder response = new StringBuilder();
                    String responseLine = null;

                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim());
                    }
                    String output = response.toString();
                    String theresponse = "travel offer: "+ "userid: " + theUserID +"\n"+ " msgid: " + theMsgID  +"\n"+ " latlon: " + theLatlon +"\n"+  " date: " + theDate +"\n"+ " weatherdetails: " + output + "\n\n";
                    themessages.add(theresponse);
                    System.out.println(theresponse);
                }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return themessages;
    }


    @RequestMapping(path = "/intentmessage")
    public void intentmessage( @RequestParam (value = "proposaluserid") String proposaluserid, @RequestParam (value = "message") String message, @RequestParam (value = "userid") String userid ) throws Exception, JSONException {

        // fields to send: userid, mgsid, otheruserid and a message

        orchestratorRESTservice orchestratorRESTservice = new orchestratorRESTservice();

        //String userid = orchestratorRESTservice.generateID();
        String msgid = orchestratorRESTservice.generateID();

        String thedata = "{\"data\": {\"fields\": [{\"userid\":\""+userid+"\",\"msgid\":\""+msgid+"\",\"proposaluserid\":\""+proposaluserid+"\",\"message\":\""+message+"\"}]}}";

        rabbitMQpublisher.publish(thedata, "TRAVEL_INTENT");

    }

    @RequestMapping(path = "/checkintentmessages")
    public List checkintentmessage() throws Exception, JSONException {
        List<String> intentmessages = new ArrayList<String>();
        rabbitMQsubscriber.subscribe("TRAVEL_INTENT");

        try {
            File messagesfile = new File("intentmessage.txt");
            Scanner myReader = new Scanner(messagesfile);

            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();


                GsonBuilder builder = new GsonBuilder();
                builder.setPrettyPrinting();
                Gson gson = builder.create();
                JSONObject json = new JSONObject(data);
                JSONObject dataJsonArray = json.getJSONObject("data");
                JSONArray xd = dataJsonArray.getJSONArray("fields");
                List<String> list = new ArrayList<String>();
                for (int i = 0; i < xd.length(); i++) {
                    list.add(xd.getString(i));
                }
                String[] stringArray = list.toArray(new String[list.size()]);
                orchestratorRESTservice.userintents info = gson.fromJson(stringArray[0], orchestratorRESTservice.userintents.class);

                String theUserID = info.userid;
                String theMsgID = info.msgid;
                String theMsg = info.message;
                String theProposalUserID = info.proposaluserid;

                String theintentmessage = "Intent Recieved: "+ "userid: " + theUserID +"\n"+ " msgid: " + theMsgID +"\n"+" message: " + theMsg +"\n"+  " Proposal User ID: " + theProposalUserID +"\n\n";
                intentmessages.add(theintentmessage);
                System.out.println(theintentmessage );

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return intentmessages;
    }




}
