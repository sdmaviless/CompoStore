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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import es.uniovi.sdm.compostore.Common.Common;
import es.uniovi.sdm.compostore.Interface.ItemClickListener;
import es.uniovi.sdm.compostore.Model.Component;
import es.uniovi.sdm.compostore.ViewHolder.ComponentViewHolder;

public class ComponentsListNotLogged extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    //Menu drawer
    private DrawerLayout mDrawerLayout;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference componentListNotLogged;

    String categoryId="";

    FirebaseRecyclerAdapter<Component,ComponentViewHolder> adapter;

    //Barra de busqueda
    FirebaseRecyclerAdapter<Component,ComponentViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_components_list_not_logged);

        //Menu drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //Firebase
        database = FirebaseDatabase.getInstance();
        componentListNotLogged = database.getReference("Components");

        //Menu drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        //Menu drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_component);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Recibimos el intent aqui
        if(getIntent() != null){
            categoryId = getIntent().getStringExtra("CategoryId");
        }
        if(!categoryId.isEmpty() && categoryId != null){
            if(Common.isConnectedToInternet(getBaseContext())){
                loadListComponents(categoryId);
            }else{
                Toast.makeText(ComponentsListNotLogged.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        //Barra de busqueda
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter component name");
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Cuando escribimos el texto, sugerimos la lista

                List<String> suggest = new ArrayList<String>();
                for(String search:suggestList){
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())){
                        suggest.add(search);
                    }
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //Cuando la barra de busqueda esta cerrada
                //Restaura el adapter original
                if(!enabled){
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //Cuando se termina la busqueda
                //Enseña el resultado del adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Component, ComponentViewHolder>(
                Component.class,
                R.layout.component_item,
                ComponentViewHolder.class,
                componentListNotLogged.orderByChild("Name").equalTo(text.toString()) //Comparamos los nombres
        ) {
            @Override
            protected void populateViewHolder(ComponentViewHolder viewHolder, Component model, int position) {
                viewHolder.component_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.component_image);

                final Component local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Iniciar una nueva activity
                        Intent componentDetail = new Intent(ComponentsListNotLogged.this, ComponentDetail.class);
                        componentDetail.putExtra("ComponentId", searchAdapter.getRef(position).getKey()); //Mandarle el Component Id a la nueva activity
                        startActivity(componentDetail);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);
    }


    private void loadSuggest() {
        componentListNotLogged.orderByChild("CategoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                            Component item = postSnapshot.getValue(Component.class);
                            suggestList.add(item.getName()); //Añade un nombre de componente a la lista
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }

    private void loadListComponents(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Component, ComponentViewHolder>(
                Component.class,
                R.layout.component_item,
                ComponentViewHolder.class,
                //Mismo que: Select * from Components where CategoryId = ..
                componentListNotLogged.orderByChild("CategoryId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(ComponentViewHolder viewHolder, Component model, int position) {
                viewHolder.component_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.component_image);

                final Component local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Iniciar una nueva activity
                        Intent componentDetailNotLogged = new Intent(ComponentsListNotLogged.this, ComponentDetailNotLogged.class);
                        componentDetailNotLogged.putExtra("ComponentId", adapter.getRef(position).getKey()); //Mandarle el Component Id a la nueva activity
                        startActivity(componentDetailNotLogged);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);

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
            super.onBackPressed();
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
        Intent loggedIntent = new Intent(ComponentsListNotLogged.this, c);
        startActivity(loggedIntent);
        finish();
    }
    private void launchSignOut() {
        //Logout
        Intent signIn = new Intent(ComponentsListNotLogged.this, SignIn.class);
        signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signIn);
    }
}
