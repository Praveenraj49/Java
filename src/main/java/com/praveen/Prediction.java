package com.praveen;

/**
 * Created by Praveen on 3/26/2017.
 */

import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.net.URL;
import java.net.MalformedURLException;
import java.net.HttpURLConnection;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZoneId;
import java.time.Instant;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/*
 * To execute Java, please define "static void main" on a class
 * named Solution.
 *
 * If you need more classes, simply define them inline.
 */

class Prediction {
    public static final String API_KEY="9Jz6tLIeJ0yY9vjbEUWaH9fsXA930J9hspPchute";
    public static final String PROTOCOL = "https";
    public static final String HOST = "api.nasa.gov";
    public static final String GETPATH= "/planetary/earth/assets";

    public static final String DATEFORMAT = "yyyy-MM-dd'T'HH:mm:ss";

    public static final long EPOCH_YEAR=1970;
    public static final long EPOCH_MONTH=1;
    public static final long EPOCH_DAY=1;

    /**
     * Sample JSON Response
     * {
     "count": 2,
     "results": [{
     "date": "2013-04-15T18:05:02",
     "id": "LC8_L1T_TOA/LC80370352013105LGN01"
     },
     {
     "date": "2013-05-01T18:05:00",
     "id": "LC8_L1T_TOA/LC80370352013121LGN01"
     }
     }]
     }
     * @param latitude
     * @param longitude
     * @return Prediction time in String
     * @throws Exception
     */

    public  String flyby(double latitude , double longitude) throws Exception{

        String response = getResponse(latitude , longitude);
        JSONParser parser = new JSONParser();
        JSONObject jsonObject  = (JSONObject) parser.parse(response);
        //Check the result  count
        int   count = Integer.parseInt(jsonObject.get("count").toString());
        if(count>0) {
            JSONArray results = (JSONArray) jsonObject.get("results");
            JSONObject[] objects = new JSONObject[results.size()];
            results.toArray(objects);
            List<LocalDateTime> dateTimeList = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATEFORMAT);
            for (JSONObject object : objects) {
                LocalDateTime dateTime = LocalDateTime.from(formatter.parse(object.get("date").toString()));
                dateTimeList.add(dateTime);
            }

            Collections.sort(dateTimeList);
            String prediction = getPrediction(dateTimeList).format(formatter);
            return prediction;
        }

        else
        {
            String message = "Returned Emtpy Results for Latitute =" +latitude + " and Longitude ="+longitude +
                              "\n Check the Inputs";
            System.out.println("Response from the Rest Call: " + response);
            throw new Exception(message);
        }

    }

    private  String getResponse(double latitude , double longitude) throws Exception {
        StringBuilder response = new  StringBuilder();
        try {

            URL url  = urlBuilder(latitude, longitude);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");
            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            while ((output = br.readLine()) != null) {
                response.append(output);

            }

            conn.disconnect();

        }

        catch (MalformedURLException e) {

            e.printStackTrace();

        }

        catch (IOException e) {

            e.printStackTrace();

        }

        return response.toString();

    }

    // Example query:
    //https://api.nasa.gov/planetary/earth/assets?lon=100.75&lat=1.5&begin=2014-02-1&api_key=DEMO_KEY

    private  URL urlBuilder(double latitude , double longitude) throws Exception {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("lon=");
        queryBuilder.append(Double.toString(longitude));
        queryBuilder.append("&");
        queryBuilder.append("lat=");
        queryBuilder.append(Double.toString(latitude));
        queryBuilder.append("&");
        queryBuilder.append("api_key=");
        queryBuilder.append(API_KEY);


        URI uri = new URI(PROTOCOL, null, HOST, -1, GETPATH, queryBuilder.toString(), null);
        URL url = uri.toURL();
        return url;
    }


    private  LocalDateTime getPrediction(List<LocalDateTime> dateTimeList) {
        //Compute the difference of time in seconds consecutive DateTime
         List<Long> secondsList = new ArrayList<>();
         for(int i=0;i<dateTimeList.size()-1;i++) {
             secondsList.add(
                     getTimeDifferenceInSeconds(dateTimeList.get(i + 1) ,dateTimeList.get(i)));
         }

         //Compute the average difference
         OptionalDouble average = secondsList.stream()
                                            .mapToDouble(a ->a)
                                            .average();
         long predictionSeconds = average.isPresent() ? Math.round(average.getAsDouble()):0;
         LocalDateTime lastTime  = dateTimeList.get(dateTimeList.size()-1); // Get the last occurence
         Instant instant = Instant.now();
         ZoneId systemZone = ZoneId.systemDefault(); // my timezone
         ZoneOffset currentOffsetForMyZone = systemZone.getRules().getOffset(instant);

       // The number of seconds from the epoch of 1970-01-01T00:00:00Z
        // Convert the predictions in seconds to Local Date Time and add to last flyby time.
         LocalDateTime epochTime = LocalDateTime.ofEpochSecond(predictionSeconds,0,currentOffsetForMyZone);
         return addLocalDateTime(lastTime,epochTime);
    }
    /**
     * Calculates the time difference between two DateTime in seconds
     */
    private  long getTimeDifferenceInSeconds(LocalDateTime dt1, LocalDateTime dt2) {

        long seconds= Math.abs(ChronoUnit.SECONDS.between(dt2,dt1));
        return seconds;
    }

    private  LocalDateTime addLocalDateTime(LocalDateTime dt1, LocalDateTime dt2) {
       // LocalDateTime is immutable
        LocalDateTime yearAdd= dt1.plusYears(dt2.getYear()-EPOCH_YEAR);
        LocalDateTime monthAdd = yearAdd.plusMonths(dt2.getMonthValue()-EPOCH_MONTH);
        LocalDateTime dayAdd=monthAdd.plusDays(dt2.getDayOfMonth()-EPOCH_DAY);
        LocalDateTime hourAdd=dayAdd.plusHours(dt2.getHour());
        LocalDateTime minuteAdd=hourAdd.plusMinutes(dt2.getMinute());
        LocalDateTime result =minuteAdd.plusSeconds(dt2.getSecond());

        return result;

    }


}