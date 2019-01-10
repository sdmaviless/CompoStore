package es.uniovi.sdm.compostore;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import es.uniovi.sdm.compostore.Common.Common;
import es.uniovi.sdm.compostore.Database.Database;
import es.uniovi.sdm.compostore.Interface.ItemClickListener;
import es.uniovi.sdm.compostore.Model.Banner;
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

    CounterFab fab;

    //Slider
    HashMap<String,String> image_list;
    SliderLayout mSlider;

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
                        componentsList.putExtra("CategoryName", adapter.getItem(position).getName());
                        startActivity(componentsList);
                    }
                });
            }
        };


        fab = (CounterFab) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cartIntent = new Intent(UserLoggedActivity.this, Cart.class);
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
        layoutManager = new LinearLayoutManager(this);
        recycler_menu.setLayoutManager(layoutManager);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(recycler_menu.getContext(),
                R.anim.layout_fall_down);
        recycler_menu.setLayoutAnimation(controller);

        if(Common.isConnectedToInternet(this)) {
            loadMenu();
        }else{
            Toast.makeText(this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
            return;
        }

        //Configurar Slider
        //Se necesita llamar a esta funcion despues de iniciar la base de datos firebase
        setUpSlider();

    }

    private void setUpSlider() {
        mSlider = (SliderLayout)findViewById(R.id.slider);
        image_list = new HashMap<>();

        final DatabaseReference banners = database.getReference("Banner");

        banners.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for(DataSnapshot postSnapShot:dataSnapshot.getChildren()){
                    Banner banner = postSnapShot.getValue(Banner.class);
                    //Vamos a concatenar el nombre string y id de la siguiente forma
                    // HARD_DRIVE_1 => Y usaremos HARD DRIVE para enseñar la descripcion, 1 para hacer click en el id del componente
                    image_list.put(banner.getName()+"_"+banner.getId(), banner.getImage());
                }
                for(String key:image_list.keySet()){
                    String[] keySplit = key.split("_");
                    String nameOfComponent = keySplit[0];
                    String idOfComponent = keySplit[1];

                    //Crear el Slider
                    final TextSliderView textSliderView = new TextSliderView(getBaseContext());
                    textSliderView
                            .description(nameOfComponent)
                            .image(image_list.get(key))
                            .setScaleType(BaseSliderView.ScaleType.Fit)
                            .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                @Override
                                public void onSliderClick(BaseSliderView slider) {
                                    Intent intent = new Intent (UserLoggedActivity.this, ComponentDetail.class);
                                    //Mandamos el id del componente a ComponentDetail
                                    intent.putExtras(textSliderView.getBundle());
                                    startActivity(intent);
                                }
                            });
                    //Añadir un bundle extra
                    textSliderView.bundle(new Bundle());
                    textSliderView.getBundle().putString("ComponentId", idOfComponent);

                    mSlider.addSlider(textSliderView);

                    //Eliminar el evento al terminar
                    banners.removeEventListener(this);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mSlider.setPresetTransformer(SliderLayout.Transformer.Background2Foreground);
        mSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mSlider.setCustomAnimation(new DescriptionAnimation());
        mSlider.setDuration(4000); //Duracion de cada imagen antes de pasar a la siguiente
    }

    private void loadMenu() {
        recycler_menu.setAdapter(adapter);

        //Animation
        recycler_menu.getAdapter().notifyDataSetChanged();
        recycler_menu.scheduleLayoutAnimation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setCount(new Database(this).getCountCart(Common.currentUser.getPhone()));
    }

    @Override
    protected void onStop() {
        super.onStop();
        mSlider.stopAutoCycle();
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
        } else if (id == R.id.nav_favorites) {
           startActivity(new Intent(UserLoggedActivity.this, FavoritesActivity.class));
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
        //Logout
        Intent signIn = new Intent(UserLoggedActivity.this, SignIn.class);
        signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signIn);
    }

    public void launch(Class c){
        Intent loggedIntent = new Intent(UserLoggedActivity.this, c);
        startActivity(loggedIntent);
        finish();
    }
}


