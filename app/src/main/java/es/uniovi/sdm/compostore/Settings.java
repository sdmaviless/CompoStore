package es.uniovi.sdm.compostore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Set;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    public void onBackPressed() {
        //Go back to products
        Intent loggedIntent = new Intent(Settings.this, UserLoggedActivity.class);
        startActivity(loggedIntent);
        finish();
    }
}
