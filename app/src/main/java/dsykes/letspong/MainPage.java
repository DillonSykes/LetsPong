package dsykes.letspong;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainPage extends AppCompatActivity {
    Button signOutButton;
    FirebaseAuth mAuth;
    Button newGameButton;
    FirebaseAuth.AuthStateListener mAuthListener;
    AlertDialog.Builder builder;
    EditText editText;
    AlertDialog alert;
    String joinID;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("match");
    ArrayList<String> listOfMatchIDs;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            // do something on back pressed.


            return true;
        }

        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        signOutButton = (Button) findViewById(R.id.SignOutButton);
        newGameButton = (Button) findViewById(R.id.newGame);
        editText = new EditText(this);
        builder = new AlertDialog.Builder(this);
        builder.setTitle("Join Game");
        builder.setMessage("Enter Match ID");
        builder.setPositiveButton("Join", null);
        builder.setNegativeButton("Cancel", null);
        builder.setView(editText);
        alert = builder.create();
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainPage.this, LoginActivity.class));
                }
            }
        };
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
            }
        });
        listOfMatchIDs = new ArrayList<>();
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    listOfMatchIDs.add(data.getKey().toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }



    public void goToNewGameActivity(View view) {
        startActivity(new Intent(this, NewGame.class));
    }

    public void goToRecordActivity(View view) {
        Intent intent = new Intent(MainPage.this,Record.class);
        startActivity(intent);
    }

    public void JoinGameActivity(View view) {
        alert.show();
        alert.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String MatchIDToJoinGame = editText.getText().toString().trim();
                String regex = "\\d+";
                if(MatchIDToJoinGame.length() == 0){
                    Toast.makeText(getApplicationContext(),"Please enter the Match ID",Toast.LENGTH_LONG).show();
                }
                else if(MatchIDToJoinGame.matches(regex)){
                    joinID = MatchIDToJoinGame;
                    if(listOfMatchIDs.contains(joinID)){
                        Intent joinGameIntent = new Intent(MainPage.this, OneGame.class).putExtra("MatchID",joinID).putExtra("join",true);
                        startActivity(joinGameIntent);
                    }else {
                        Toast.makeText(getApplicationContext(),"Match ID does not exist!",Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),"Match ID should be a number",Toast.LENGTH_LONG).show();
                }

            }
        });
        alert.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }
}
