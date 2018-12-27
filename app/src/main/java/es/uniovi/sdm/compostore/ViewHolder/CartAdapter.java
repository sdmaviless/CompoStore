package es.uniovi.sdm.compostore.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.uniovi.sdm.compostore.Common.Common;
import es.uniovi.sdm.compostore.Interface.ItemClickListener;
import es.uniovi.sdm.compostore.Model.Order;
import es.uniovi.sdm.compostore.R;

class  CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    , View.OnCreateContextMenuListener {

    public TextView tx_cart_name, tx_price;
    public ImageView img_cart_count;

    private ItemClickListener itemClickListener;

    public void setTx_cart_name(TextView tx_cart_name){
        this.tx_cart_name = tx_cart_name;
    }


    public CartViewHolder(View itemView) {
        super(itemView);
        tx_cart_name = (TextView)itemView.findViewById(R.id.cart_item_name);
        tx_price = (TextView)itemView.findViewById(R.id.cart_item_Price);
        img_cart_count = (ImageView)itemView.findViewById(R.id.cart_item_count);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select action");
        menu.add(0,0,getAdapterPosition(), Common.DELETE);
    }
}


public class CartAdapter extends  RecyclerView.Adapter<CartViewHolder>{

    private List<Order> listData = new ArrayList<>();
    private Context context;

    public CartAdapter(List<Order> listData, Context context) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout,viewGroup,false);
        return new CartViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(CartViewHolder cartViewHolder, int i) {
        TextDrawable drawable = TextDrawable.builder()
                .buildRound(""+listData.get(i).getQuantity(), Color.RED);
        cartViewHolder.img_cart_count.setImageDrawable(drawable);

        Locale locale =  new Locale("es", "ES");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        //Calculando el precio total del carrito
        float price = (Float.parseFloat(listData.get(i).getPrice())) * (Integer.parseInt(listData.get(i).getQuantity()));
        cartViewHolder.tx_price.setText(fmt.format(price));

        cartViewHolder.tx_cart_name.setText(listData.get(i).getProductName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }
}
