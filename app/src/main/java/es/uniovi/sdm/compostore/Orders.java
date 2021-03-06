package es.uniovi.sdm.compostore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Orders extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
    }

    @Override
    public void onBackPressed() {
        //Go back to products
        Intent loggedIntent = new Intent(Orders.this, UserLoggedActivity.class);
        startActivity(loggedIntent);
        finish();
    }
}
