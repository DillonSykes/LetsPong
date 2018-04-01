package dsykes.letspong;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class NewGame extends AppCompatActivity implements Spinner.OnItemSelectedListener {
    Spinner listOfUsers;
    ArrayList<Users> users;
    private JSONArray result;
    boolean userSelected;
    String OpponentUserName;
    String OpponentUID;
    String CurrentUID;
    ProgressBar progressBar;
    String theMatchID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        listOfUsers = (Spinner) findViewById(R.id.list_of_friends);
        users = new ArrayList<>();
        listOfUsers.setOnItemSelectedListener(this);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        getData();
        userSelected = false;
        CurrentUID = Users.getCurrentUID();
    }

    private void getData() {
        progressBar.setVisibility(View.VISIBLE);
        //Creating a string request
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Config.DATA_URL_GET_USERS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.INVISIBLE);

                        try {
                            //Parsing the fetched Json String to JSON Object
                            JSONObject j = new JSONObject(response);

                            //Storing the Array of JSON String to our JSON Array
                            result = j.getJSONArray(Config.JSON_ARRAY);

                            //Calling method getUsers to get the students from the JSON Array
                            getUserName(result);
                            setUsersAdapter();
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
    public void getUID(JSONArray j) {

    }

    public void setUsersAdapter(){
        ArrayList<String> userNames = new ArrayList<>();
        for (Users u : users){
            String name = u.getUserName();
            userNames.add(name);
        }
        //Setting adapter to show the items in the spinner
        listOfUsers.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, userNames));
        ArrayAdapter myAdap = (ArrayAdapter) listOfUsers.getAdapter(); //cast to an ArrayAdapter
        int spinnerPosition = myAdap.getPosition(getString(R.string.PleaseSelectOpponent));
        //set the default according to value
        listOfUsers.setSelection(spinnerPosition);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        OpponentUserName = listOfUsers.getItemAtPosition(position).toString();
        OpponentUID = users.get(position).getUserId();
        if (OpponentUserName.equals(getString(R.string.PleaseSelectOpponent))) {
            userSelected = false;
        } else {
            userSelected = true;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    public void createNewMatch() {
        String type = "newMatch";
        CreateNewMatchInDatabase newMatchInDatabase = new CreateNewMatchInDatabase(this);
        progressBar.setVisibility(View.VISIBLE);
        try {
            theMatchID = newMatchInDatabase.execute(type, CurrentUID, OpponentUID).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        finish();
        progressBar.setVisibility(View.INVISIBLE);
    }
    public void playOneGame(View view) {
        if (userSelected) {
            createNewMatch();
            Intent playGameIntent = new Intent(this, OneGame.class).putExtra(getString(R.string.OpponentName), OpponentUserName).putExtra(getString(R.string.OpponentUID),OpponentUID).putExtra("MatchID",theMatchID).putExtra("join",false);
            startActivity(playGameIntent);
        } else {
            Toast.makeText(getApplicationContext(), R.string.PleaseSelectOpponent,Toast.LENGTH_SHORT).show();
        }
    }
    public void setMatchID(String result){
        theMatchID = result;
    }

}

