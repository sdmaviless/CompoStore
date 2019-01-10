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
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import es.uniovi.sdm.compostore.Common.Common;
import es.uniovi.sdm.compostore.Database.Database;
import es.uniovi.sdm.compostore.Model.Component;
import es.uniovi.sdm.compostore.Model.Order;
import io.paperdb.Paper;

public class ComponentDetail extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;

    TextView component_name, component_price, component_description;
    ImageView component_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    CounterFab btnCart;
    ElegantNumberButton numberButton;

    TextView txFullName;

    String componentId="";

    FirebaseDatabase database;
    DatabaseReference components;

    Component currentComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);
        //Firebase
        database = FirebaseDatabase.getInstance();
        components = database.getReference("Components");

        //Inicializar la vista
        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        btnCart = (CounterFab) findViewById(R.id.btnCart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        Common.currentUser.getPhone(),
                        componentId,
                        currentComponent.getName(),
                        numberButton.getNumber(),
                        currentComponent.getPrice(),
                        currentComponent.getDiscount(),
                        currentComponent.getImage()
                ));

                Toast.makeText(ComponentDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

        btnCart.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        component_description = (TextView) findViewById(R.id.component_description);
        component_name = (TextView) findViewById(R.id.component_name);
        component_price = (TextView) findViewById(R.id.component_price);
        component_image = (ImageView) findViewById(R.id.img_component);

        collapsingToolbarLayout =(CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        //Obtener el component id de el Intent

        //Mostrar nombre del usuario conectado
        View headerView = navigationView.getHeaderView(0);
        txFullName = (TextView)headerView.findViewById(R.id.txFullName);
        txFullName.setText(Common.currentUser.getName());
        if(getIntent() !=null){
            componentId = getIntent().getStringExtra("ComponentId");
            if(!componentId.isEmpty()){
                if(Common.isConnectedToInternet(getBaseContext())){
                    getDetailComponent(componentId);
                }else{
                    Toast.makeText(ComponentDetail.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

    }

    private void getDetailComponent(String componentId) {
        components.child(componentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentComponent = dataSnapshot.getValue(Component.class);

                //Configurar imagen
                Picasso.with(getBaseContext()).load(currentComponent.getImage())
                        .into(component_image);

                collapsingToolbarLayout.setTitle(currentComponent.getName());
                component_price.setText(currentComponent.getPrice());
                component_name.setText(currentComponent.getName());
                component_description.setText(currentComponent.getDescription());
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                toolbar.setTitle(currentComponent.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
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
            // Handle the camera action
            launch(UserLoggedActivity.class);
        } else if (id == R.id.nav_favorites) {
            startActivity(new Intent(ComponentDetail.this, FavoritesActivity.class));
        } else if (id == R.id.nav_cart) {
            launch(Cart.class);
        } else if (id == R.id.nav_orders) {
            launch(OrderStatus.class);
        } else if (id == R.id.nav_sign_out) {
            launchSignOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void launchSignOut() {
        //Borrar el recordar usuario y la contraseña
        Paper.book().destroy();

        //Logout
        Intent mainActivity = new Intent(ComponentDetail.this, MainActivity.class);
        mainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainActivity);
    }

    public void launch(Class c){
        Intent loggedIntent = new Intent(ComponentDetail.this, c);
        startActivity(loggedIntent);
        finish();
    }
}
