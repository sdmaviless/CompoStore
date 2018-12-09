package es.uniovi.sdm.compostore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import es.uniovi.sdm.compostore.Common.Common;

public class Favourites extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourites);
    }

    @Override
    public void onBackPressed() {
        //Go back to products
        Intent loggedIntent = new Intent(Favourites.this, UserLoggedActivity.class);
        startActivity(loggedIntent);
        finish();
    }
}
