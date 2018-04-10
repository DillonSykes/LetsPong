package dsykes.letspong;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;

import java.util.HashMap;
import java.util.Objects;

public class OneGame extends AppCompatActivity {
    String OpponentUserName;
    String OpponentUID;
    String CurrentUserName;
    String CurrentUID;
    ProgressBar progressBar;
    TextView TextViewCurr;
    TextView TextViewOpp;
    Button CurrentUserPtButton;
    TextView MatchIDTextView;
    String MatchID;
    TextView CurrentUserPoints;
    TextView OppenentPoints;
    ImageView serveP1;
    ImageView serveP2;
    Button UndoLast;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("match");
    boolean isThisaJoin;
    AlertDialog.Builder builder;
    AlertDialog alert;
    String firstServe;
    String CurrServe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one_game);
        OpponentUID = getIntent().getStringExtra(getString(R.string.OpponentUID));
        CurrentUserName = Users.getCurrentUserName();
        CurrentUID = Users.getCurrentUID();
        progressBar = findViewById(R.id.oneGamePb);
        TextViewCurr = findViewById(R.id.player1Name);
        TextViewOpp = findViewById(R.id.player2Name);
        TextViewCurr.setText(CurrentUserName);
        serveP1 = findViewById(R.id.serveP1);
        serveP2 = findViewById(R.id.serveP2);
        serveP1.setVisibility(View.INVISIBLE);
        serveP2.setVisibility(View.INVISIBLE);
        CurrentUserPtButton = findViewById(R.id.player1Button);
        MatchIDTextView = findViewById(R.id.myMatchId);
        MatchID = getIntent().getStringExtra("MatchID");
        isThisaJoin = getIntent().getExtras().getBoolean("join");
        MatchIDTextView.setText(MatchID);
        CurrentUserPoints = findViewById(R.id.myPoints);
        CurrentUserPoints.setText("0");
        OppenentPoints = findViewById(R.id.OppPoints);
        OppenentPoints.setText("0");
        UndoLast = findViewById(R.id.undo);
        CurrServe = "noOne";
        addlastButtonHit("nothing");
        if(!isThisaJoin){
            OpponentUserName = getIntent().getStringExtra(getString(R.string.OpponentName));
            TextViewOpp.setText(OpponentUserName);
            addMatchToFirebase(MatchID, CurrentUserName, OpponentUserName);
            whoServesFirst();
        } else {
            myRef.child(MatchID).child("P2").setValue(CurrentUserName);
            myRef.child(MatchID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<Object, Object> data = (HashMap<Object, Object>) dataSnapshot.getValue();
                    OpponentUserName = (String) data.get("P1");
                    TextViewOpp.setText(OpponentUserName);
                    whoServesFirstJoin();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        whoServesNow(false);
        myRef.child(MatchID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<Object, Object> data = (HashMap<Object, Object>) dataSnapshot.getValue();
                //OpponentUserName = (String) data.get("P2");
                String OPPnewPoints = (String) data.get(OpponentUserName);
                OppenentPoints.setText(OPPnewPoints);
                String CURRnewPoints = (String) data.get(CurrentUserName);
                CurrentUserPoints.setText(CURRnewPoints);
                CurrServe = (String) data.get("CurrServe");
                if(Objects.equals(CurrServe, CurrentUserName)){
                    TextViewCurr.setTextColor(Color.RED);
                    TextViewOpp.setTextColor(Color.BLACK);
                    //CurrServe = OpponentUserName;
                    //editCurrentServer(CurrServe);
                } else if(CurrServe.equals(OpponentUserName)) {
                    TextViewOpp.setTextColor(Color.RED);
                    TextViewCurr.setTextColor(Color.BLACK);
                    //CurrServe = CurrentUserName;
                    //editCurrentServer(CurrServe);
                }
                isGameOver();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
    public String isGameOver(){
        String ender = "nobody won";
        if(getCurrPoints() >= 11 && (getCurrPoints() - getOppPoints()) >= 2){
            gameOverDialog(CurrentUserName);
            ender = CurrentUserName;
        } else if(getOppPoints() >=11 && (getOppPoints() - getCurrPoints()) >= 2){
            gameOverDialog(OpponentUserName);
            ender = OpponentUserName;
        }
        return ender;
    }
    public void whoServesFirst(){
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Who Serves First?");
        builder.setMessage("Choose who serves First.");
        builder.setPositiveButton(CurrentUserName,null);
        builder.setNegativeButton(OpponentUserName,null);
        alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFirstServeToFirebase(CurrentUserName);
                editCurrentServer(CurrentUserName);
                whoServesNow(false);
                alert.dismiss();
            }
        });
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addFirstServeToFirebase(OpponentUserName);
                editCurrentServer(OpponentUserName);
                whoServesNow(false);
                alert.dismiss();
            }
        });
    }
    public void whoServesFirstJoin(){
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Who Serves First?");
        builder.setMessage("Player 1 chooses first server");
        builder.setPositiveButton("Ok",null);
        alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                whoServesNow(false);
                alert.dismiss();
            }
        });
    }
    public void editCurrentServer(String s){
        myRef.child(MatchID).child("CurrServe").setValue(s);
    }
    public void addFirstServeToFirebase(String s){
        myRef.child(MatchID).child("firstServe").setValue(s);
    }
    public int getCurrPoints(){
        return Integer.parseInt(CurrentUserPoints.getText().toString());
    }
    public int getOppPoints(){
        return Integer.parseInt(OppenentPoints.getText().toString());
    }
    public int getTotalPoints(){
        return  getCurrPoints() + getOppPoints();
    }
    public void whoServesNow(boolean undo){
        int total = getTotalPoints();
        if(total == 0){
            myRef.child(MatchID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<Object, Object> data = (HashMap<Object, Object>) dataSnapshot.getValue();
                    firstServe = (String) data.get("firstServe");
                    CurrServe = (String) data.get("CurrServe");
                    if(firstServe == CurrentUserName){
                        TextViewCurr.setTextColor(Color.RED);
                    }
                    if(firstServe == OpponentUserName) {
                        TextViewOpp.setTextColor(Color.RED);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
        if(total % 2 == 0 && total < 20 && total > 0 && !undo){
            myRef.child(MatchID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<Object, Object> data = (HashMap<Object, Object>) dataSnapshot.getValue();
                    CurrServe = (String) data.get("CurrServe");
                    changeServeIcon();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        if(total % 2 == 1 && total < 20 && total > 0 && undo){
            myRef.child(MatchID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<Object, Object> data = (HashMap<Object, Object>) dataSnapshot.getValue();
                    CurrServe = (String) data.get("CurrServe");
                    changeServeIcon();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        if(total > 20){
            changeServeIcon();
        }
    }
    public void gameOverDialog(String winner){
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Game Over!");
        builder.setMessage(winner + " won! \n Hit Ok to save the match.");
        builder.setPositiveButton("Ok",null);
        alert = builder.create();
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Saved!",Toast.LENGTH_SHORT).show();
                //Run thread to save match
                startActivity(new Intent(OneGame.this,MainPage.class));
            }
        });
    }
    public void changeServeIcon(){
        if(Objects.equals(CurrServe, CurrentUserName)){
            //TextViewCurr.setTextColor(Color.BLACK);
            //TextViewOpp.setTextColor(Color.RED);
            CurrServe = OpponentUserName;
            editCurrentServer(CurrServe);
        } else if(CurrServe.equals(OpponentUserName)) {
            //TextViewOpp.setTextColor(Color.BLACK);
            //TextViewCurr.setTextColor(Color.RED);
            CurrServe = CurrentUserName;
            editCurrentServer(CurrServe);
        }
    }
    public void addMatchToFirebase(String matchID,String CUserName, String OUserName){
        myRef.child(MatchID).child(CUserName).setValue(CurrentUserPoints.getText());
        myRef.child(MatchID).child(OUserName).setValue(OppenentPoints.getText());
        myRef.child(MatchID).child("P1").setValue(CUserName);
        myRef.child(MatchID).child("P2").setValue(OUserName);
        myRef.child(MatchID).child("CurrServe").setValue("null");
    }
    public void addPointToFirebase(String points){
        myRef.child(MatchID).child(CurrentUserName).setValue(points);
    }
    public void undoPointsToFirebase(String points){
        myRef.child(MatchID).child(CurrentUserName).setValue(points);
    }
    public void addlastButtonHit(String s){
            myRef.child(MatchID).child("lastBtn").setValue(s);
    }
    public void addPoint(View view) {
        String strPts = CurrentUserPoints.getText().toString();
        int pts = Integer.parseInt(strPts);
        pts++;
        String newPtsValue = Integer.toString(pts);
        addPointToFirebase(newPtsValue);
        CurrentUserPoints.setText(newPtsValue);
        addlastButtonHit("add");
        whoServesNow(false);
    }

    public void UndoMyLastPoint(View view) {
        String strPts = CurrentUserPoints.getText().toString();
        int pts = Integer.parseInt(strPts);
        if(pts > 0){
            pts--;
        } else {
            Toast.makeText(getApplicationContext(), R.string.YouHaveNoPoints, Toast.LENGTH_SHORT).show();
        }
        String newPtsValue = Integer.toString(pts);
        undoPointsToFirebase(newPtsValue);
        CurrentUserPoints.setText(newPtsValue);
        addlastButtonHit("sub");
        whoServesNow(true);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            builder = new AlertDialog.Builder(this);
            builder.setTitle("Exit?");
            builder.setMessage("If you exit all match data will be lost.");
            builder.setPositiveButton("Exit", null);
            builder.setNegativeButton("Keep Playing", null);
            alert = builder.create();
            alert.show();
            alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(OneGame.this,MainPage.class));
                }
            });
            alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alert.dismiss();
                }
            });
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

}
