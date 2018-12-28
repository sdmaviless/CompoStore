package es.uniovi.sdm.compostore;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.uniovi.sdm.compostore.Common.Common;
import es.uniovi.sdm.compostore.Database.Database;
import es.uniovi.sdm.compostore.Model.Order;
import es.uniovi.sdm.compostore.Model.Request;
import es.uniovi.sdm.compostore.ViewHolder.CartAdapter;
import info.hoang8f.widget.FButton;

public class Cart extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference requests;

    TextView txTotalPrice;
    FButton btnPlace;

    List<Order> cart = new ArrayList<>();

    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        //Firebase
        database = FirebaseDatabase.getInstance();
        requests = database.getReference("Requests");

        //Inicializacion
        recyclerView = (RecyclerView) findViewById(R.id.listCart);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

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
                new Database(getBaseContext()).cleanCart();
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
        cart = new Database(this).getCarts();
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
        new Database(this).cleanCart();
        //Al final actualzaremos los nuevos datos a la List<Order> de SQLite
        for(Order item :cart){
            new Database(this).addToCart(item);
        }
        //Refresh
        loadListComponents();

    }

}
