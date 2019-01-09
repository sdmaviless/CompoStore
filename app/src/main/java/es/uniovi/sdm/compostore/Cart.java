package es.uniovi.sdm.compostore;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.uniovi.sdm.compostore.Common.Common;
import es.uniovi.sdm.compostore.Database.Database;
import es.uniovi.sdm.compostore.Helper.RecyclerItemTouchHelper;
import es.uniovi.sdm.compostore.Interface.ItemClickListener;
import es.uniovi.sdm.compostore.Interface.RecyclerItemTouchHelperListener;
import es.uniovi.sdm.compostore.Model.Category;
import es.uniovi.sdm.compostore.Model.Order;
import es.uniovi.sdm.compostore.Model.Request;
import es.uniovi.sdm.compostore.ViewHolder.CartAdapter;
import es.uniovi.sdm.compostore.ViewHolder.CartViewHolder;
import es.uniovi.sdm.compostore.ViewHolder.MenuViewHolder;
import info.hoang8f.widget.FButton;

public class Cart extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, RecyclerItemTouchHelperListener {

    private DrawerLayout mDrawerLayout;

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    public TextView txTotalPrice;
    FButton btnPlace;

    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;


    TextView txFullName;
    FirebaseRecyclerAdapter<Category,MenuViewHolder> menuAdapter;
    DatabaseReference category;

    RelativeLayout rootLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        //setContentView(R.layout.cart_layout);

        rootLayout = (RelativeLayout)findViewById(R.id.root_layout);

        /*//Menu Drawer
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Menu");
        setSupportActionBar(toolbar);*/

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");
        category = database.getReference("Category");

        //Menu drawer
       /* mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Cambiar color y tamaño de titulo de menu

        Menu menu = navigationView.getMenu();
        MenuItem communicate = menu.findItem(R.id.itemCommunicate);
        SpannableString s = new SpannableString(communicate.getTitle());
        s.setSpan(new TextAppearanceSpan(this, R.style.itemTitle), 0, s.length(), 0);
        communicate.setTitle(s);*/

        /*//Mostrar nombre del usuario conectado
        View headerView = navigationView.getHeaderView(0);
        txFullName = (TextView)headerView.findViewById(R.id.txFullName);
        txFullName.setText(Common.currentUser.getName());*/

        //Inicializacion del carrito
        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);


        //Swipe para eliminar producto del carrito
        ItemTouchHelper.SimpleCallback itemTouchHelperCallBack = new RecyclerItemTouchHelper(0,ItemTouchHelper.LEFT,this);
        new ItemTouchHelper(itemTouchHelperCallBack).attachToRecyclerView(recyclerView);

        txTotalPrice = (TextView) findViewById(R.id.total);
        btnPlace = (FButton)findViewById(R.id.btnPlaceOrder);

        btnPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(cart.size() > 0){
                    showAlertDialog();
                }else{
                    Toast.makeText(Cart.this, "Your cart is empty !!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loadListComponents();

//        recyclerView = (RecyclerView) findViewById(R.id.recycler_component);
//        recyclerView.setHasFixedSize(true);
//        layoutManager = new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(layoutManager);

//        //Cargar menu
//        recycler_menu = (RecyclerView)findViewById(R.id.recycler_menu);
//        recycler_menu.setHasFixedSize(true);
//        menuLayoutManager = new LinearLayoutManager(this);
//        recycler_menu.setLayoutManager(menuLayoutManager);


        if(Common.isConnectedToInternet(this)) {
            loadMenu();
        }else{
            Toast.makeText(this, "Please check your connection !!", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    //
    private void loadMenu() {
        menuAdapter = new FirebaseRecyclerAdapter<Category, MenuViewHolder>(Category.class, R.layout.menu_item, MenuViewHolder.class,category) {
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
                        Intent componentsList = new Intent(Cart.this, ComponentsList.class);
                        //El id de la categoria es una key asi que solo obtenemos la key de este item
                        componentsList.putExtra("CategoryId",menuAdapter.getRef(position).getKey());
                        startActivity(componentsList);
                    }
                });
            }
        };
        recyclerView.setAdapter(adapter);
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
        Intent loggedIntent = new Intent(Cart.this, UserLoggedActivity.class);
        startActivity(loggedIntent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.user_logged, menu);
        return true;
    }

    private void showAlertDialog(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(Cart.this);
        alertDialog.setTitle("One more step!");
        alertDialog.setMessage("Enter your address: ");

        final EditText editAddress = new EditText(Cart.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        editAddress.setLayoutParams(lp);
        alertDialog.setView(editAddress); //Añadimos el edit text al alert Dialog
        alertDialog.setIcon(R.drawable.ic_shopping_cart_black_24dp);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Creamos una nueva peticion de la base de datos
                Request request = new Request(
                        Common.currentUser.getPhone(),
                        Common.currentUser.getName(),
                        editAddress.getText().toString(),
                        txTotalPrice.getText().toString(),
                        cart
                );

                //La mandamos a Firebase, usaremos System.CurrentMillis como clave
                requests.child(String.valueOf(System.currentTimeMillis()))
                        .setValue(request);

                //Borrar carrito
                new Database(getBaseContext()).cleanCart(Common.currentUser.getPhone());
                Toast.makeText(Cart.this, "Thank you, your order was successfully completed"
                    ,Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void loadListComponents() {
        cart = new Database(this).getCarts(Common.currentUser.getPhone());
        adapter = new CartAdapter(cart,this);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);

        //Calcular el precio total
        int total = 0;
        for(Order order:cart){
            total +=(Float.parseFloat(order.getPrice()))*(Integer.parseInt(order.getQuantity()));
        }

        Locale locale =  new Locale("es", "ES");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        txTotalPrice.setText(fmt.format(total));

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if(item.getTitle().equals(Common.DELETE))
            deleteCart(item.getOrder());
        return true;
    }


    private void deleteCart(int position) {
        //Elimninaremos el item de la lista List<Order> por posicion
        cart.remove(position);
        //Despues de eso eliminaremos los datos viejos de SQLite
        new Database(this).cleanCart(Common.currentUser.getPhone());
        //Al final actualzaremos los nuevos datos a la List<Order> de SQLite
        for(Order item :cart){
            new Database(this).addToCart(item);
        }
        //Refresh
        loadListComponents();

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.
        int id = menuItem.getItemId();

        if (id == R.id.nav_products) {
            goProducts();
        } else if (id == R.id.nav_favorites) {
            startActivity(new Intent(Cart.this, FavoritesActivity.class));
        } else if (id == R.id.nav_cart) {
            onBackPressed();
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

    private void launchSignOut() {
        //Logout
        Intent signIn = new Intent(Cart.this, SignIn.class);
        signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(signIn);
    }

    public void launch(Class c){
        Intent loggedIntent = new Intent(Cart.this, c);
        startActivity(loggedIntent);
        finish();
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if(viewHolder instanceof CartViewHolder){
            String name = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition()).getProductName();

            final Order deleteItem = ((CartAdapter)recyclerView.getAdapter()).getItem(viewHolder.getAdapterPosition());
            final int deleteIndex = viewHolder.getAdapterPosition();

            adapter.removeItem(deleteIndex);
            new Database(getBaseContext()).removeFromCart(deleteItem.getProductId(), Common.currentUser.getPhone());

            //Actualizar el txTotal
            //Calcular el precio total
            int total = 0;
            List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
            for(Order item : orders){
                total +=(Float.parseFloat(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
            }

            Locale locale =  new Locale("es", "ES");
            NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

            txTotalPrice.setText(fmt.format(total));

            //Creacion de Snackbar
            Snackbar snackbar = Snackbar.make(rootLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    adapter.restoreItem(deleteItem, deleteIndex);
                    new Database(getBaseContext()).addToCart(deleteItem);

                    //Actualizar el txTotal
                    //Calcular el precio total
                    int total = 0;
                    List<Order> orders = new Database(getBaseContext()).getCarts(Common.currentUser.getPhone());
                    for(Order item : orders){
                        total +=(Float.parseFloat(item.getPrice()))*(Integer.parseInt(item.getQuantity()));
                    }

                    Locale locale =  new Locale("es", "ES");
                    NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                    txTotalPrice.setText(fmt.format(total));
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }
}
