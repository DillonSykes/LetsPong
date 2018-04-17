package dsykes.letspong;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.crypto.Cipher;

public class Record extends AppCompatActivity {
    String UID;
    String Score;
    String Opponent;
    private JSONArray result;
    ArrayList<Users> users;
    ArrayList<Match> matches;
    ListView my_listview;
    TextView myRecord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        users = new ArrayList<>();
        matches = new ArrayList<>();
        UID = Users.getCurrentUID();
        my_listview = findViewById(R.id.lv);
        myRecord = findViewById(R.id.Record);
        GetMatchData thread = new GetMatchData(Record.this);
        GetRecord thread1 = new GetRecord(Record.this);
        try {
            String response =  thread.execute(Users.getCurrentUID()).get();
            getMatchFRomJSON(response);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            String response = thread1.execute(Users.getCurrentUID()).get();
            getRecordFromJSON(response);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        geUserData();
    }

    private void getRecordFromJSON(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray j = (JSONArray) jsonObject.get("record");
            JSONObject jo = j.getJSONObject(0);
            String wins = jo.getString("Wins");
            String loses = jo.getString("Loses");
            myRecord.setText(wins + " - " + loses);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void geUserData() {
        //progressBar.setVisibility(View.VISIBLE);
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.DATA_URL_GET_USERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //progressBar.setVisibility(View.INVISIBLE);

                        try {
                            //Parsing the fetched Json String to JSON Object
                            JSONObject j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray(Config.JSON_ARRAY);

                            //Calling method getUsers to get the students from the JSON Array
                            getUserName(result);
                            CustomAdapter myAdapter = new CustomAdapter(Record.this, R.layout.listview_row_layout, matches, users);
                            my_listview.setAdapter(myAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //Creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
    public void getMatchFRomJSON(String response){

        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray j = (JSONArray) jsonObject.get(Config.JSON_ARRAY);

            for(int i = 0; i < j.length(); i++){
                JSONObject JsonMatch = j.getJSONObject(i);
                String player1Score = JsonMatch.getString(Config.TAG_P1_SCORE);
                String player2Score = JsonMatch.getString(Config.TAG_P2_SCORE);
                String matchType = JsonMatch.getString(Config.TAG_MATCH_TYPE);
                String winner = JsonMatch.getString(Config.TAG_WINNER);
                String player1UID = JsonMatch.getString(Config.TAG_UID1);
                String player2UID = JsonMatch.getString(Config.TAG_UID2);
                String matchID = JsonMatch.getString(Config.TAG_MATCH_ID);
                Match aMatch = new Match(player1Score,player2Score,matchType,winner,player1UID,player2UID,matchID);
                matches.add(aMatch);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public void getUserName(JSONArray j) {
        //Traversing through all the items in the json array
        for (int i = 0; i < j.length(); i++) {
            try {
                //Getting json object
                JSONObject JsonUserObject = j.getJSONObject(i);
                String username = JsonUserObject.getString(Config.TAG_USERNAME);
                String userId = JsonUserObject.getString(Config.TAG_USERID);
                Users aUser = new Users(username,userId);
                users.add(aUser);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
