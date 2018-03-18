package dsykes.letspong;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;

public class MainPage extends AppCompatActivity {
    Button signOutButton;
    FirebaseAuth mAuth;
    Button newGameButton;
    FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_page);
        signOutButton = (Button) findViewById(R.id.SignOutButton);
        newGameButton = (Button) findViewById(R.id.newGame);

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null){
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
    }

    public void goToNewGameActivity(View view) {
        startActivity(new Intent(this, NewGame.class));
    }

    public void goToRecordActivity(View view) {
    }

    public void goToFriendsActivity(View view) {
        startActivity((new Intent(this, Friends.class)));
    }
}
