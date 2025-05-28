package ds.cmu.edu.bart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import com.google.gson.Gson;
import android.widget.RadioButton;
import ds.cmu.edu.interestingpicture.R;
//Citation: a lot is learned from Lab8 files
public class BartController extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
         * The click listener will need a reference to this object, so that upon successfully finding a picture from Flickr, it
         * can callback to this object with the resulting picture Bitmap.  The "this" of the OnClick will be the OnClickListener, not
         * this InterestingPicture.
         */
        final BartController ma = this;

        /*
         * Find the "submit" button, and add a listener to it
         */
        Button submitButton = (Button)findViewById(R.id.submit);
        RadioGroup radioNoGroup=(RadioGroup) findViewById(R.id.radioNo);


        // Add a listener to the send button
        submitButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View viewParam) {
                String trainNO = ((EditText)findViewById(R.id.searchTerm)).getText().toString();

                // get selected radio button from radioGroup
                int selected = radioNoGroup.getCheckedRadioButtonId();

                // find the radiobutton by returned id
                RadioButton radioNoButton = (RadioButton) findViewById(selected);
                String station=radioNoButton.getText().toString();
                String searchTerm=trainNO+station; //search term is concatenation

                System.out.println("searchTerm = " + searchTerm);

                BartModel gp = new BartModel();
                gp.search(searchTerm, ma); // Done asynchronously in another thread.  It calls ip.pictureReady() in this thread when complete.
            }
        });
    }

    /*
     * This is called by the GetPicture object when the response from API is ready.  This allows for passing back the Bitmap picture for updating the ImageView
     */

    public void scheduleReady(String response){

        TextView responseView = findViewById(R.id.response);
        if (response=="") { //address the case not connecting to server
            responseView.setText("No response from Server. ");

            responseView.setVisibility(View.VISIBLE);
        }
        //if response is available from web service, present it to user
        else {
            Gson gson=new Gson();
            //convert Json response to regular String
            String schedule=gson.fromJson(response,  String.class);
            //responseView.setText("Departure time: " +schedule.origTime);
            responseView.setText(schedule);
            responseView.setVisibility(View.VISIBLE);
            System.out.println( schedule);
        }
        //searchView.setText("");
        responseView.invalidate();

    }

}
