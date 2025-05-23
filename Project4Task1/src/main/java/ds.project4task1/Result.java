package ds.project4task1;

/*import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;*/

// A simple class to wrap an RPC result.
public class Result {
    private int responseCode;
    private String responseText;

    public int getResponseCode() { return responseCode; }
    public void setResponseCode(int code) { responseCode = code; }
    public String getResponseText() { return responseText; }
    public void setResponseText(String msg) { responseText = msg; }

    public String toString() { return responseCode + ":" + responseText; }
}
// Android -> Web service -> Third party API(internet) -> Response 3rd party -> send back the response to android
// Log  Web service -> Third party API(internet) -> Response 3rd party -> send back the response to android in MongoDb
// Servlet which reads from mongodb -> HTML of the logs (http://localhost:8080/dashboard)
// Mongo Client will connect to Mongo DB Atlas ( http:// atlas....)


