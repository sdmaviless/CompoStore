package es.uniovi.sdm.compostore;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import es.uniovi.sdm.compostore.Common.Common;
import es.uniovi.sdm.compostore.Model.Request;
import es.uniovi.sdm.compostore.ViewHolder.OrderViewHolder;

public class OrderStatus extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;

    private RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    TextView txFullName;
    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;

    FirebaseDatabase database;
    DatabaseReference requests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Menu Drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Orders");
        setSupportActionBar(toolbar);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Menu drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Mostrar nombre del usuario conectado
        View headerView = navigationView.getHeaderView(0);
        txFullName = (TextView)headerView.findViewById(R.id.txFullName);
        txFullName.setText(Common.currentUser.getName());

        recyclerView = (RecyclerView)findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        loadOrders(Common.currentUser.getPhone());

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            goProducts();

        }
    }

    private void goProducts() {
        Intent loggedIntent = new Intent(OrderStatus.this, UserLoggedActivity.class);
        startActivity(loggedIntent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_products) {
            goProducts();
        } else if (id == R.id.nav_favorites) {
            startActivity(new Intent(OrderStatus.this, FavoritesActivity.class));
        } else if (id == R.id.nav_cart) {
            launch(Cart.class);
        } else if (id == R.id.nav_orders) {
            //Intent orderIntent = new Intent(UserLoggedActivity.this, OrderStatus.class);
            //startActivity(orderIntent);
            onBackPressed();
        } else if (id == R.id.nav_settings){
            //launchSettings();
            launch(Settings.class);

        } else if (id == R.id.nav_sign_out) {
            launchSignOut();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    private void launchSignOut() {
        //Logout
        Intent signIn = new Intent(OrderStatus.this, SignIn.class);
        signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signIn);
    }

    public void launch(Class c){
        Intent loggedIntent = new Intent(OrderStatus.this, c);
        startActivity(loggedIntent);
        finish();
    }
    private void loadOrders(String phone) {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests.orderByChild("phone")
                .equalTo(phone)

        ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, Request model, int position) {
                viewHolder.txOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txOrderStatus.setText(convertCodeToStatus(model.getStatus()));
                viewHolder.txOrderAddress.setText(model.getAddress());
                viewHolder.txOrderPhone.setText(model.getPhone());
            }
        };

        recyclerView.setAdapter(adapter);
    }

    private String convertCodeToStatus(String status){

        if(status.equals("0")){
            return "Placed";
        }else if (status.equals("1")){
            return "On my way";
        }else{
            return "Shipped";
        }
    }

}
