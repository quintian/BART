package edu.cmu.kunt.bart2;

/*
 * @author Quinn Tian
 * this is for Project4Task2
 */
import com.google.gson.Gson;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;

//this is for Task2
@WebServlet(name = "BARTServlet",
        urlPatterns = {"/trainSchedule/*" ,"/dashboard"})
public class BARTServlet extends HttpServlet {

    BARTModel bartModel = null;  // The "business model" for this app

    // Initiate this servlet by instantiating the model that it will use.
    @Override
    public void init() {
        bartModel = new BARTModel ();
    }

    // This servlet will reply to HTTP GET requests via this doGet method
    @Override
    // Make an HTTP GET request
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        //if the url has /trainSchedule/* would get schedule for Android or web users
        //and save the data in MongoDB
        if (request.getRequestURL().toString().contains("trainSchedule")) {
            Timestamp timestamp=new Timestamp(System.currentTimeMillis());
            TrainSchedule returnedSchedule=null;
            String date=null; //train date is also current date
            String trainNo=null;
            String station=null;
            String from3pApi=null; //status from 3p Api

            String input=null; //input form user
            Gson gson=new Gson();
            String reply = null; //reply to user
            String jsonReply = "";
            PrintWriter out=response.getWriter();

            String url = request.getRequestURI();
            System.out.println("Request url from Android:"+url);

            if (url.contains("trainSchedule/")){
                String[] inputs=url.split("Schedule/");
                if (inputs.length>1) {
                    input=inputs[1];
                    System.out.println("Android input: "+input);
                }
            }
            //1.taking care of invalid mobile app input
            if (input==null || input.isEmpty()) {
                reply="Null input. Please submit the search term.";
            }
            if (input!=null && !input.isEmpty() ){ //get the schedule if available
                from3pApi=String.valueOf(BARTModel.fetchData().getResponseCode());
                returnedSchedule=BARTModel.getSchedule(input);
                if (returnedSchedule==null){ //taking care of the case that schedule is not available
                    reply="No this train today. Try a different No.";
                }
                else { //read into TrainSchedule for each variable that will be saved in log
                    date=returnedSchedule.date;
                    trainNo=returnedSchedule.trainNo;
                    station=returnedSchedule.station;
                    //this is reply message to user with retrieved schedule time
                    reply="Requested train schedule is: "+returnedSchedule.origTime;
                }
            }
            jsonReply=gson.toJson(reply);
            System.out.println("Json reply to Android: "+jsonReply);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            out.print(jsonReply); //send reply to user
            out.close();
            //create a new Log to save into DB
            Log log=new Log(timestamp, date, trainNo, station, input, from3pApi, reply);

            bartModel.addToMongo(log); //call method to save Log into DB
        }
        //if url is with /dashboard, present analytics and log history to webpage via View file
        if (request.getRequestURL().toString().contains("dashboard")) {
            PrintWriter out=response.getWriter();
            String url = request.getRequestURI();
            System.out.println("Request url :"+url);
            bartModel.analytics();  //get all analytics results and load into bartModel
            //set dashboard attributes with all values from analytics results
            request.setAttribute("totalLogs", bartModel.totalLogs);
            request.setAttribute("nullInputs", bartModel.nullInputs);
            request.setAttribute("api200", bartModel.api200);
            request.setAttribute("api200Pct", bartModel.api200Pct);
            request.setAttribute("trainNo", bartModel.mostPopularTrain);
            request.setAttribute("maxTrainCount", bartModel.maxTrainCount);

            request.setAttribute("logs", bartModel.logs); //send all logs to View
            String nextView="dashboard.jsp";
            RequestDispatcher view = request.getRequestDispatcher(nextView);
            view.forward(request, response); //give view with request, response

        }
    }

}


