package es.uniovi.sdm.compostore;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import es.uniovi.sdm.compostore.Database.Database;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn;
    Button btnSignUp;
    Button btnContinueUnlogged;

    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new Database(getBaseContext()).cleanCart();

        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        btnContinueUnlogged= (Button)findViewById((R.id.btnContinueUnlogged));

        txtSlogan = (TextView)findViewById(R.id.txtSlogan);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Madeleina Sans.otf");
        txtSlogan.setTypeface(face);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signIn = new Intent(MainActivity.this, SignIn.class);
                startActivity(signIn);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signUp = new Intent(MainActivity.this, SignUp.class);
                startActivity(signUp);
            }
        });

        btnContinueUnlogged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent notLogged = new Intent(MainActivity.this, UserNotLoggedActivity.class);
                startActivity(notLogged);
            }
        });
    }
}
