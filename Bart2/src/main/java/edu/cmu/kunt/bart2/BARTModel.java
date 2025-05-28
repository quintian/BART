package edu.cmu.kunt.bart2;
/*
        * @author Quinn Tian
        * this is for Project4Task2
        */
import com.google.gson.Gson;
import com.mongodb.client.*;

import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

//this is for Task2
public class BARTModel {
    public static Map<String, TrainSchedule> scheduleMap=new HashMap<>();
       // Load API key from environment variable
    static String apiKey = System.getenv("BART_API_KEY");

    // Construct the API URL using the API key
    static String urlString = "https://api.bart.gov/api/sched.aspx?cmd=routesched&route=12&key=" + apiKey + "&json=y";

    //load scheduleMap with String response received from 3p Api
    public static void loadScheduleMap(String response){

        //use org.json library to extract info from Json string extracted from 3p Api
        JSONTokener token = new JSONTokener(response); //split response
        JSONObject ob = new JSONObject(token);

        JSONObject root = (JSONObject)ob.get("root");
        String date=(String)root.get("date");
        System.out.println("Json root from Api: "+root);

        JSONObject route = (JSONObject)root.get("route");
        JSONArray trainArray=(JSONArray)route.get("train");
        for (int i=0; i<trainArray.length(); i++){
            JSONObject trainOb=(JSONObject)trainArray.get(i);
            String index= (String) trainOb.get("@index"); //index is the train NO.
            //System.out.println(trainOb.get("@index"));
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

    }
    // return a TrainSchedule as given input
    public static TrainSchedule getSchedule(String input){
        if (fetchData().getResponseCode()!=200){
            return null;  //4. taking care of 3p API unavailable
        }
        //call fetchData() to retrieve data from 3p Api
        String fetchedData= fetchData().getResponseText();
        loadScheduleMap(fetchedData); //load data to map
        if (scheduleMap.containsKey(input)){
            return scheduleMap.get(input);
        }
        else{ //5. taking care of invalid data from API or invalid input from user
            return null;
        }
    }


    // Make an HTTP GET request to fetch data from 3p Api
    //Citation: some syntax is from lab file provided by professor
    public static Result fetchData() {

        HttpURLConnection conn;
        int status = 0;
        Result result = new Result();
        try {

            URL url = new URL (urlString);

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
            }
            //4. taking care of 3p API unavailable
            else result.setResponseText("Abnormal 3p API"); // set http response message
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
    //Citation: some mongoDB code is learned from
    //https://www.mongodb.com/developer/quickstart/java-setup-crud-operations/
    public void addToMongo(Log log){
        try (MongoClient mongoClient = MongoClients.create(
                "mongodb+srv://quinntian:Pang0902@cluster0.uu6lc.mongodb.net/BART?retryWrites=true&w=majority")){
            System.out.println("Connect to MongoDB");
            MongoDatabase database = mongoClient.getDatabase("BART"); //create database
            MongoCollection<Document> trainSchedule = database.getCollection("doc");//create the collection
            Gson gson=new Gson();
            Document doc=new Document("doc",gson.toJson(log));
            trainSchedule.insertOne(doc);

            System.out.println("doc is inserted to MongoDB");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public  List<Log> logs=new ArrayList<Log>(); //all logs stored in MongoDB
    public double api200Pct; //% of Api 200/total logs
    public  int totalLogs=0;
    public int nullInputs=0;
    public int api200=0; //total counts of logs with Api status=200
    public TreeMap<String, Integer> trainCounts=new TreeMap<String, Integer>();
    public String mostPopularTrain; //trainNo
    int maxTrainCount;//the highest counts of a train that have been retrieved from Api
    public void analytics(){
        try (MongoClient mongoClient = MongoClients.create(
                "mongodb+srv://quinntian:Pang0902@cluster0.uu6lc.mongodb.net/BART?retryWrites=true&w=majority")){
            System.out.println("Connect to MongoDB");
            MongoDatabase database = mongoClient.getDatabase("BART"); //create database
            MongoCollection<Document> trainSchedule = database.getCollection("doc");//create the collection

            Gson gson=new Gson();
            FindIterable<Document> iterDoc = trainSchedule.find();
            Iterator it = iterDoc.iterator();
            //MongoCursor<Document> cursor=iterDoc.iterator(); //another way to loop through collection
           /* while (cursor.hasNext()) {
                System.out.println(cursor.next());
            }*/

            //Citation: https://www.tutorialspoint.com/how-to-retrieve-all-the-documents-from-a-mongodb-collection-using-java
            try{ //loop through each record in logs saved and get the values for the above variables
                while (it.hasNext()) {
                    Document d1= (Document) it.next();
                    String s= (String) d1.get("doc");
                    //Gson gson=new Gson();
                    Log log=gson.fromJson(s, Log.class);
                    logs.add(log);
                    System.out.println(log.timestamp);

                    totalLogs++;
                    if (log.input==null) nullInputs++;
                    if (log.from3pApi!=null && log.from3pApi.equals("200")) api200++;
                    System.out.println("line 194 "+log.trainNo);
                    if (log.trainNo!=null && !trainCounts.containsKey(log.trainNo)) {

                        trainCounts.put(log.trainNo, 1);
                    }
                    if (log.trainNo!=null && trainCounts.containsKey(log.trainNo)) {
                        trainCounts.put(log.trainNo, (trainCounts.get(log.trainNo) + 1));
                    }
                }
                for (String train: trainCounts.keySet()){
                    if (trainCounts.get(train)>maxTrainCount) {
                        maxTrainCount=trainCounts.get(train);
                        mostPopularTrain=train;
                    }
                }
                System.out.println("logs size "+logs.size());

                api200Pct=(double) Math.round(api200*100/totalLogs*10.0)/10.0;
                System.out.println(api200+" out of "+totalLogs+" returned data from Api");
                System.out.println("Total of null input is "+nullInputs);
                System.out.println(api200Pct+"% of total have 200 status from 3p Api");
                System.out.println("The most popular train is " + mostPopularTrain+ ", with "
                + maxTrainCount + " times searched." );
            }
            finally {
                mongoClient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
