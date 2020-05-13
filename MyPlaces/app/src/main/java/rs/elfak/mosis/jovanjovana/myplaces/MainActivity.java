package rs.elfak.mosis.jovanjovana.myplaces;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import android.content.Intent;

public class MainActivity extends AppCompatActivity {

    private SignInButton signInButton;
    GoogleSignInClient mGoogleSignInClient;
    private String TAG="MainActivity";
    private FirebaseAuth mAuth;
    private Button btnSignOut;
    private int RC_SIGN_IN=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        signInButton = findViewById(R.id.sign_in_button);
        mAuth = FirebaseAuth.getInstance();
        btnSignOut = findViewById(R.id.sign_out_button);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        btnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mGoogleSignInClient.signOut();
                Toast.makeText(MainActivity.this,"Izlogovani ste.", Toast.LENGTH_SHORT).show();
                btnSignOut.setVisibility(View.INVISIBLE);
            }
        });



        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                Intent i=new Intent(MainActivity.this,EditMyPlaceActivity.class);
                startActivityForResult(i,NEW_PLACE);
            }
        });
    }

    private void signIn()
    {
        Intent signInIntent= mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    static int NEW_PLACE=1;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.show_map_item)
        {
            Intent i = new Intent(this, MyPlacesMapsActivity.class);
            i.putExtra("state", MyPlacesMapsActivity.SHOW_MAP);
            startActivity(i);
        }
        else if(id == R.id.new_place_item)
        {
            Intent i=new Intent(this,EditMyPlaceActivity.class);
            startActivityForResult(i,NEW_PLACE);
        }else if(id == R.id.my_places_list_item)
        {
            Intent i = new Intent(this,MyPlacesList.class);
            startActivity(i);
        }else if(id == R.id.about_item)
        {
            Intent i = new Intent(this,About.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override

    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        if(resultCode== Activity.RESULT_OK)
        {
            Toast.makeText(this,"New Place added",Toast.LENGTH_SHORT).show();

        }
        if(requestCode==RC_SIGN_IN)
        {
            Task<GoogleSignInAccount> task= GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask)
    {
        try {
            GoogleSignInAccount acc= completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this,"Uspesno logovanje.", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(acc);
        }
        catch (ApiException e)
        {
            Toast.makeText(MainActivity.this,"Neuspesno logovanje.", Toast.LENGTH_SHORT).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct)
    {
        AuthCredential authCredential= GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(MainActivity.this,"Uspesno", Toast.LENGTH_SHORT).show();
                    FirebaseUser user=mAuth.getCurrentUser();
                    updateUI(user);

                }
                else
                {
                    Toast.makeText(MainActivity.this,"Neuspesno", Toast.LENGTH_SHORT).show();
                    updateUI(null);

                }
            }
        });

    }

    private void updateUI(FirebaseUser u)
    {
        btnSignOut.setVisibility(View.VISIBLE);
        GoogleSignInAccount account= GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account!=null)
        {
            String personName=account.getDisplayName();
            String personGivenName=account.getGivenName();
            String personFamilyName=account.getFamilyName();
            String personEmail=account.getEmail();
            String personId=account.getId();
            Uri personPhoto=account.getPhotoUrl();
            Toast.makeText(MainActivity.this,personName+" "+personEmail, Toast.LENGTH_SHORT).show();

        }
    }
}
