package ds.project4task1;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class BARTModel {
    public static Map<String, TrainSchedule> scheduleMap=new HashMap<>();
    //URL of 3p API
    static String urlString="https://api.bart.gov/api/sched.aspx?cmd=routesched&route=12&key=MW9S-E7SL-26DU-VV8V&json=y";

    public static void loadScheduleMap(String response){

        //use org.json library to extract info from Json string extracted from 3p Api
        JSONTokener token = new JSONTokener(response); //split response
        JSONObject ob = new JSONObject(token);

        JSONObject root = (JSONObject)ob.get("root");
        String date=(String)root.get("date");
        System.out.println("\nroot: "+root);
        System.out.println("date: "+date);

        JSONObject route = (JSONObject)root.get("route");

        JSONArray trainArray=(JSONArray)route.get("train");
        for (int i=0; i<trainArray.length(); i++){
            JSONObject trainOb=(JSONObject)trainArray.get(i);
            String index= (String) trainOb.get("@index");//index is the train NO.
            System.out.println(trainOb.get("@index"));
            JSONArray stopArray=(JSONArray)trainOb.get("stop");
            for (int j=0; j<stopArray.length(); j++){
                JSONObject stopOb=(JSONObject) stopArray.get(j);
                String station= (String) stopOb.get("@station");
                String origTime= (String) stopOb.get("@origTime");

                //create a TrainSchedule object for each (trainNo + station)
                TrainSchedule schedule=new TrainSchedule( index, date, station, origTime);
                String key=index+station; //use combined string as key
                scheduleMap.put(key, schedule); //add each schedule to map
            }
        }
        //return scheduleMap;
    }
    // return a TrainSchedule as given input
    public static TrainSchedule getSchedule(String input){
        if (fetchData().getResponseCode()!=200){
            return null;  //4. taking care of 3p API unavailable
        }
        //call fetchData() to retrieve data from 3p Api
      String fetchedData= fetchData().getResponseText();

      loadScheduleMap(fetchedData);
      if (scheduleMap.containsKey(input)){
          return scheduleMap.get(input);
      }
      else{ //5. taking care of invalid data from API or invalid input from user
          return null;
      }
    }


    // Make an HTTP GET request  to fetch data from 3p Api
    //Citation: some syntax is from lab file provided by professor
    public static Result fetchData() {

        HttpURLConnection conn;
        int status = 0;
        Result result = new Result();
        try {

            URL url = new URL (urlString);
            //URL url=new URL("https://api.bart.gov/api/bsa.aspx?cmd=count&key=MW9S-E7SL-26DU-VV8V");
            //key: ZA2V-5LSA-9P2T-DWEI, https://api.bart.gov/api/route.aspx?cmd=routeinfo&route=8&key=MW9S-E7SL-26DU-VV8V
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            // we are sending plain text
            conn.setRequestProperty("Content-Type", "text/plain; charset=utf-8");
            // tell the server what format we want back
            conn.setRequestProperty("Accept", "text/plain");

            // wait for response
            status = conn.getResponseCode();

            // set http response code
            result.setResponseCode(status);
            // set http response message - this is just a status message
            // and not the body returned by GET
            result.setResponseText(conn.getResponseMessage());

            if (status == 200) {
                String responseBody = getResponseBody(conn);
                result.setResponseText(responseBody);
            }  //4. taking care of 3p API unavailable
            else result.setResponseText("Abnormal 3p API");
        }
        // 2. handle exceptions, taking care of invalid server-side input
        catch (MalformedURLException e) {
            System.out.println("URL Exception thrown" + e);
        } catch (IOException e) {
            System.out.println("IO Exception thrown" + e);
        } catch (Exception e) {
            System.out.println("IO Exception thrown" + e);
        }
        return result;
    }
    //Citation: this is from lab file provided by professor
    // Gather up a response body from the connection
    // and close the connection.
    public static String getResponseBody(HttpURLConnection conn) {
        String responseText = "";
        try {
            String output = "";
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            while ((output = br.readLine()) != null) {
                responseText += output;
            }
            conn.disconnect();
        } catch (IOException e) {
            System.out.println("Exception caught " + e);
        }
        return responseText;
    }
}
