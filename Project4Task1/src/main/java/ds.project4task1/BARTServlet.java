package ds.project4task1;
/*
 * @author Quinn Tian
 */

import com.google.gson.Gson;
import jakarta.servlet.ServletException;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(name = "BARTServlet",
        urlPatterns = {"/trainSchedule/*"})
public class BARTServlet extends HttpServlet {
    //public static Map<String, TrainSchedule> scheduleMap=new HashMap<>();

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
        System.out.println("here");
        String input=null;
        Gson gson=new Gson();
        String reply = null;
        String jsonReply = "";
        PrintWriter out=response.getWriter();
        //String url = request.getServletPath();
        String url = request.getRequestURI();
        System.out.println("Url:"+url);
        //String[] inputs=url.split("trainSchedule/");
        if (url.contains("trainSchedule/")){

            String[] inputs=url.split("Schedule/");
            if (inputs.length>1) {
                input=inputs[1];
                System.out.println(input);
            }
        }
        //1.ctaking care of invalid mobile app input
        if (input==null || input.isEmpty()) {
            reply="Null input. Please submit the search term.";

            System.out.println("null input");
        }
        if (input!=null && !input.isEmpty() ){//get the schedule if available
            TrainSchedule returnedSchedule=BARTModel.getSchedule(input);
            if (returnedSchedule==null){
                reply="No this train today. Try a different No.";
            }
            else {//this is reply message to user with retrieved schedule time
                reply="Requested train schedule is: "+returnedSchedule.origTime;
            }
        }
        jsonReply=gson.toJson(reply);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(jsonReply);
        out.close();

    }
}
