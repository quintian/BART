package ds.cmu.edu.bart;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;

import java.net.URL;

import android.os.AsyncTask;


/*
 * This class provides capabilities to search for an image on Flickr.com given a search term.  The method "search" is the entry to the class.
 * Network operations cannot be done from the UI thread, therefore this class makes use of an AsyncTask inner class that will do the network
 * operations in a separate worker thread.  However, any UI updates should be done in the UI thread so avoid any synchronization problems.
 * onPostExecution runs in the UI thread, and it calls the ImageView pictureReady method to do the update.
 *
 */
//Citation: a lot is learned from Lab8 files
public class BartModel {
    BartController ip = null;

    /*
     * search is the public GetPicture method.  Its arguments are the search term, and the InterestingPicture object that called it.  This provides a callback
     * path such that the pictureReady method in that object is called when the picture is available from the search.
     */
    public void search(String searchTerm, BartController ip) {
        this.ip = ip;
        new AsyncCallAPI().execute(searchTerm);
    }
    public class AsyncCallAPI extends AsyncTask<String, String, String> {

        public AsyncCallAPI(){
            //set context variables if required
        }
        @Override
        //citation: https://stackoverflow.com/questions/2938502/sending-post-data-in-android
        protected String doInBackground(String... urls) { //params ??
            return getSchedule(urls[0]);
        }

        protected void onPostExecute(String response) {
            ip.scheduleReady(response);
        }

        private String getSchedule(String searchTerm)  {
            String response = "";


            try {
                //call on Heroku web service in different address
                //for task 1:
                //URL url=new URL("https://serene-stream-01792.herokuapp.com/trainSchedule/"
                //for task2:
                URL url=new URL("https://still-fjord-16808.herokuapp.com/trainSchedule/"
                //URL url = new URL( "http://192.168.0.5:8080/Bart2-1.0-SNAPSHOT/trainSchedule/"
                        +searchTerm); //for my local test
                System.out.println("URL = "+ url);
                System.out.println("Search term = " + searchTerm);
                /*
                 * Create an HttpURLConnection.  This is useful for setting headers
                 * and for getting the path of the resource that is returned (which
                 * may be different than the URL above if redirected).
                 * HttpsURLConnection (with an "s") can be used if required by the site.
                 */
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();


                connection.setRequestMethod("GET"); //added!

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                String str;
                // Read each line of "in" until done, adding each to "response"
                while ((str = in.readLine()) != null) {
                    // str is one line of text readLine() strips newline characters
                    response += str;
                }
                in.close();

            }
            catch (ConnectException e) {
                System.out.println("url connection failed");


            }catch (ProtocolException e ) {
                e.printStackTrace();

            } catch (MalformedURLException e){
                e.printStackTrace();

            } catch (IOException e ){
                e.printStackTrace();

            }
            return response;
        }
    }

}
