package es.uniovi.sdm.compostore;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import es.uniovi.sdm.compostore.Common.Common;
import es.uniovi.sdm.compostore.Interface.ItemClickListener;
import es.uniovi.sdm.compostore.Model.Category;
import es.uniovi.sdm.compostore.ViewHolder.MenuViewHolder;

public class UserLoggedActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    FirebaseDatabase database;
    DatabaseReference category;
    TextView txFullName;

    RecyclerView recycler_menu;
    RecyclerView.LayoutManager layoutManager;

    FirebaseRecyclerAdapter<Category,MenuViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_logged);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //Iniciar Firebase
        database = FirebaseDatabase.getInstance();
        category = database.getReference("Category");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(UserLoggedActivity.this, Cart.class);
                startActivity(cartIntent);
            }
        });

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Cambiar color y tamaño de titulo de menu
        Menu menu = navigationView.getMenu();
        MenuItem communicate = menu.findItem(R.id.itemCommunicate);
        SpannableString s = new SpannableString(communicate.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.itemTitle), 0, s.length(), 0);
        communicate.setTitle(s);

        //Mostrar nombre del usuario conectado
        View headerView = navigationView.getHeaderView(0);
        txFullName = (TextView)headerView.findViewById(R.id.txFullName);
        txFullName.setText(Common.currentUser.getName());

        //Cargar menu
        recycler_menu = (RecyclerView)findViewById(R.id.recycler_menu);
        recycler_menu.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);


        if(Common.isConnectedToInternet(this)) {
            loadMenu();
        }else{
            Toast.makeText(this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private void loadMenu() {
         adapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class,category) {
            @Override
            protected void populateViewHolder(MenuViewHolder viewHolder, Category model, int position) {
                viewHolder.txMenuName.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage())
                        .into(viewHolder.imageView);
                final Category clickItem = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Obtener el ID de la categoria en la que se ha clickado y mandar a la nueva activity
                        Intent componentsList = new Intent(UserLoggedActivity.this, ComponentsList.class);
                        //El id de la categoria es una key asi que solo obtenemos la key de este item
                        componentsList.putExtra("CategoryId",adapter.getRef(position).getKey());
                        startActivity(componentsList);
                    }
                });
            }
        };
        recycler_menu.setAdapter(adapter);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_logged, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == R.id.refresh){
            loadMenu();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_products) {
            // Handle the camera action
        } else if (id == R.id.nav_favourites) {
            launchFavourites();
        } else if (id == R.id.nav_cart) {
            Intent cartIntent = new Intent(UserLoggedActivity.this, Cart.class);
            startActivity(cartIntent);
        } else if (id == R.id.nav_orders) {
            Intent orderIntent = new Intent(UserLoggedActivity.this, OrderStatus.class);
            startActivity(orderIntent);
        } else if (id == R.id.nav_settings){
            launchSettings();
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_sign_out) {
            //Logout
            Intent signIn = new Intent(UserLoggedActivity.this, SignIn.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }



    public void launchFavourites(){
        //Launch FavouritesActivity
        Intent loggedIntent = new Intent(UserLoggedActivity.this, Favourites.class);
        startActivity(loggedIntent);
        finish();
    }

    public void launchSettings(){
        //Launch CartActivity
        Intent loggedIntent = new Intent(UserLoggedActivity.this, Settings.class);
        startActivity(loggedIntent);
        finish();
    }
}


