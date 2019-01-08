package es.uniovi.sdm.compostore;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import es.uniovi.sdm.compostore.Common.Common;
import es.uniovi.sdm.compostore.Model.Component;

public class ComponentDetailNotLogged extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Menu drawer
    private DrawerLayout mDrawerLayout;

    TextView component_name, component_price, component_description;
    ImageView component_image;
    CollapsingToolbarLayout collapsingToolbarLayout;

    String componentId="";

    FirebaseDatabase database;
    DatabaseReference components;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_detail_not_logged);

        //Menu drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //Firebase
        database = FirebaseDatabase.getInstance();
        components = database.getReference("Components");

        //Menu drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Menu drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Inicializar la vista

        component_description = (TextView) findViewById(R.id.component_description);
        component_name = (TextView) findViewById(R.id.component_name);
        component_price = (TextView) findViewById(R.id.component_price);
        component_image = (ImageView) findViewById(R.id.img_component);

        collapsingToolbarLayout =(CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Obtener el component id de el Intent

        if(getIntent() !=null){
            componentId = getIntent().getStringExtra("ComponentId");
            if(!componentId.isEmpty()){
                if(Common.isConnectedToInternet(getBaseContext())){
                    getDetailComponent(componentId);
                }else{
                    Toast.makeText(ComponentDetailNotLogged.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

    }

    private void getDetailComponent(String componentId) {
        components.child(componentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Component component = dataSnapshot.getValue(Component.class);

                //Configurar imagen
                Picasso.with(getBaseContext()).load(component.getImage())
                        .into(component_image);

                collapsingToolbarLayout.setTitle(component.getName());
                component_price.setText(component.getPrice());
                component_name.setText(component.getName());
                component_description.setText(component.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //Menu drawer
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_products) {
            lauchProducts();
        } else if (id == R.id.nav_settings) {
            launch(Settings.class);
        } else if (id == R.id.nav_login) {
            launchSignOut();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    public void launch(Class c){
        Intent loggedIntent = new Intent(ComponentDetailNotLogged.this, c);
        startActivity(loggedIntent);
        finish();
    }
    private void launchSignOut() {
        //Logout
        Intent signIn = new Intent(ComponentDetailNotLogged.this, SignIn.class);
        signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signIn);
    }
    private void lauchProducts(){
        Intent products = new Intent(ComponentDetailNotLogged.this, UserNotLoggedActivity.class);
        products.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(products);
    }
}
