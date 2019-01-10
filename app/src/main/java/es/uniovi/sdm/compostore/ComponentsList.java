package es.uniovi.sdm.compostore;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareDialog;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.List;

import es.uniovi.sdm.compostore.Common.Common;
import es.uniovi.sdm.compostore.Database.Database;
import es.uniovi.sdm.compostore.Interface.ItemClickListener;
import es.uniovi.sdm.compostore.Model.Component;
import es.uniovi.sdm.compostore.Model.Favorites;
import es.uniovi.sdm.compostore.Model.Order;
import es.uniovi.sdm.compostore.ViewHolder.ComponentViewHolder;

public class ComponentsList extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private DrawerLayout mDrawerLayout;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    TextView txFullName;
    FirebaseDatabase database;
    DatabaseReference componentList;
    String categoryName;

    String categoryId="";

    FirebaseRecyclerAdapter<Component,ComponentViewHolder> adapter;

    //Barra de busqueda
    FirebaseRecyclerAdapter<Component,ComponentViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

    CounterFab fab;

    //favourites
    Database localDB;

    //Share button
    //FaceBook share
    CallbackManager callbackManager;
    ShareDialog shareDialog;

    //Create Target from Picasso
    Target target = new Target(){

        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            //Create photo from bitmap
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(bitmap)
                    .build();
            if(ShareDialog.canShow(SharePhotoContent.class)){
                SharePhotoContent content = new SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .build();
                shareDialog.show(content);
            }
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_components_list);

        //Share Button - Init facebook
        callbackManager = CallbackManager.Factory.create();
        shareDialog = new ShareDialog(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);

        //Firebase
        database = FirebaseDatabase.getInstance();
        componentList = database.getReference("Components");

        //Recibimos el intent aqui
        if(getIntent() != null){
            categoryId = getIntent().getStringExtra("CategoryId");
            categoryName = getIntent().getStringExtra("CategoryName");
            toolbar.setTitle(categoryName);
        }


        localDB = new Database(this);

        fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(ComponentsList.this, Cart.class);
                startActivity(cartIntent);
            }
        });

        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Mostrar nombre del usuario conectado
        View headerView = navigationView.getHeaderView(0);
        txFullName = (TextView)headerView.findViewById(R.id.txFullName);
        txFullName.setText(Common.currentUser.getName());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_component);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        if(!categoryId.isEmpty() && categoryId != null){
            if(Common.isConnectedToInternet(getBaseContext())){
                loadListComponents(categoryId);
            }else{
                Toast.makeText(ComponentsList.this, "Please check your connection!!", Toast.LENGTH_SHORT).show();
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
                componentList.orderByChild("Name").equalTo(text.toString()) //Comparamos los nombres
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
                        Intent componentDetail = new Intent(ComponentsList.this, ComponentDetail.class);
                        componentDetail.putExtra("ComponentId", searchAdapter.getRef(position).getKey()); //Mandarle el Component Id a la nueva activity
                        startActivity(componentDetail);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);
    }


    private void loadSuggest() {
        componentList.orderByChild("CategoryId").equalTo(categoryId)
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
                componentList.orderByChild("CategoryId").equalTo(categoryId)) {
        @Override
        protected void populateViewHolder(final ComponentViewHolder viewHolder, final Component model, final int position) {
            viewHolder.component_name.setText(model.getName());
            viewHolder.component_price.setText(String.format("€ %s", model.getPrice().toString()));
            Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.component_image);

            //Quick cart

                viewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        boolean isExists = new Database(getBaseContext()).checkComponentExists(adapter.getRef(position).getKey(), Common.currentUser.getPhone());
                        if (!isExists) {
                            new Database(getBaseContext()).addToCart(new Order(
                                    Common.currentUser.getPhone(),
                                    adapter.getRef(position).getKey(),
                                    model.getName(),
                                    "1",
                                    model.getPrice(),
                                    model.getDiscount(),
                                    model.getImage()
                            ));
                        } else {
                            new Database(getBaseContext()).increaseCart(Common.currentUser.getPhone(), adapter.getRef(position).getKey());
                        }
                        Toast.makeText(ComponentsList.this, "Added to Cart", Toast.LENGTH_SHORT).show();
                    }
                });

            //add favourites
            if(localDB.isFavorite(adapter.getRef(position).getKey(), Common.currentUser.getPhone()))
                viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);

            //Share button
            viewHolder.share_image.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if(isFacebookInstalled(getApplicationContext())){
                        Picasso.with(getApplicationContext()).load(model.getImage()).into(target);
                    }else{
                        Toast.makeText(ComponentsList.this, "Please install Facebook app to start sharing!", Toast.LENGTH_SHORT).show();
                    }

                }
            });

            //click to change state of favourites
            viewHolder.fav_image.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {

                    Favorites favorites = new Favorites();
                    favorites.setComponentId(adapter.getRef(position).getKey());
                    favorites.setComponentName(model.getName());
                    favorites.setComponentDescription(model.getDescription());
                    favorites.setComponentDiscount(model.getDiscount());
                    favorites.setComponentImage(model.getImage());
                    favorites.setComponentCategoryId(model.getCategoryId());
                    favorites.setUserPhone(Common.currentUser.getPhone());
                    favorites.setComponentPrice(model.getPrice());


                    if(!localDB.isFavorite(adapter.getRef(position).getKey(), Common.currentUser.getPhone())){
                        localDB.addToFavorites(favorites);
                        viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_black_24dp);
                        Toast.makeText(ComponentsList.this, model.getName()+" was added to favorites",Toast.LENGTH_SHORT).show();
                    }else{
                        localDB.removeFromFavorites(adapter.getRef(position).getKey(), Common.currentUser.getPhone());
                        viewHolder.fav_image.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                        Toast.makeText(ComponentsList.this, model.getName()+" was removed from favorites",Toast.LENGTH_SHORT).show();
                    }
                }
            });

            final Component local = model;
            viewHolder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onClick(View view, int position, boolean isLongClick) {
                   //Iniciar una nueva activity
                    Intent componentDetail = new Intent(ComponentsList.this, ComponentDetail.class);
                    componentDetail.putExtra("ComponentId", adapter.getRef(position).getKey()); //Mandarle el Component Id a la nueva activity
                    startActivity(componentDetail);
                }
            });
        }
    };

        recyclerView.setAdapter(adapter);

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_products) {
            // Handle the camera action
            super.onBackPressed();
        } else if (id == R.id.nav_favorites) {
            startActivity(new Intent(ComponentsList.this, FavoritesActivity.class));
        } else if (id == R.id.nav_cart) {
            //Intent cartIntent = new Intent(UserLoggedActivity.this, Cart.class);
            //startActivity(cartIntent);
            launch(Cart.class);
        } else if (id == R.id.nav_orders) {
            //Intent orderIntent = new Intent(UserLoggedActivity.this, OrderStatus.class);
            //startActivity(orderIntent);
            launch(OrderStatus.class);
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
    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));
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
    private void launchSignOut() {
        //Logout
        Intent signIn = new Intent(ComponentsList.this, SignIn.class);
        signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signIn);
    }

    public void launch(Class c){
        Intent loggedIntent = new Intent(ComponentsList.this, c);
        startActivity(loggedIntent);
        finish();
    }

    private boolean isFacebookInstalled(Context context){
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo("com.facebook.katana", PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
}
