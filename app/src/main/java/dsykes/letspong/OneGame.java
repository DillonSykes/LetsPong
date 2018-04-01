package dsykes.letspong;

import android.content.Intent;
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
        if(!isThisaJoin){
            OpponentUserName = getIntent().getStringExtra(getString(R.string.OpponentName));
            TextViewOpp.setText(OpponentUserName);
            addMatchToFirebase(MatchID, CurrentUserName, OpponentUserName);
        } else {
            myRef.child(MatchID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    HashMap<Object, Object> data = (HashMap<Object, Object>) dataSnapshot.getValue();
                    OpponentUserName = (String) data.get("P1");
                    TextViewOpp.setText(OpponentUserName);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        myRef.child(MatchID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Match thisMatch = new Match();
                HashMap<Object, Object> data = (HashMap<Object, Object>) dataSnapshot.getValue();
                String OPPnewPoints = (String) data.get(OpponentUserName);
                OppenentPoints.setText(OPPnewPoints);
                String CURRnewPoints = (String) data.get(CurrentUserName);
                CurrentUserPoints.setText(CURRnewPoints);
                //thisMatch.setPlayer1Pts(dataSnapshot.child(CurrentUserName).getValue(Match.class).getPlayer1Pts());
                //thisMatch.setPlayer2Pts(dataSnapshot.child(OpponentUserName).getValue(Match.class).getPlayer2Pts());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        whoServesFirst();
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
                serveP1.setVisibility(View.VISIBLE);
                alert.dismiss();
            }
        });
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                serveP2.setVisibility(View.VISIBLE);
                alert.dismiss();
            }
        });
    }
    public void addMatchToFirebase(String matchID,String CUserName, String OUserName){
        myRef.child(MatchID).child(CUserName).setValue(CurrentUserPoints.getText());
        myRef.child(MatchID).child(OUserName).setValue(OppenentPoints.getText());
        myRef.child(MatchID).child("P1").setValue(CUserName);
        myRef.child(MatchID).child("P2").setValue(OUserName);
    }
    public void addPointToFirebase(String points){
        myRef.child(MatchID).child(CurrentUserName).setValue(points);
    }
    public void undoPointsToFirebase(String points){
        myRef.child(MatchID).child(CurrentUserName).setValue(points);
    }

    public void addPoint(View view) {
        String strPts = CurrentUserPoints.getText().toString();
        int pts = Integer.parseInt(strPts);
        pts++;
        String newPtsValue = Integer.toString(pts);
        addPointToFirebase(newPtsValue);
        CurrentUserPoints.setText(newPtsValue);
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
