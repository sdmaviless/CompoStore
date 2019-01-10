package es.uniovi.sdm.compostore.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import es.uniovi.sdm.compostore.Cart;
import es.uniovi.sdm.compostore.Common.Common;
import es.uniovi.sdm.compostore.Database.Database;
import es.uniovi.sdm.compostore.Model.Order;
import es.uniovi.sdm.compostore.R;

public class CartAdapter extends  RecyclerView.Adapter<CartViewHolder>{

    private List<Order> listData = new ArrayList<>();
    private Cart cart;

    public CartAdapter(List<Order> listData,  Cart cart) {
        this.listData = listData;
        this.cart = cart;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(cart);
        View itemView = inflater.inflate(R.layout.cart_layout,viewGroup,false);
        return new CartViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(CartViewHolder cartViewHolder, final int position) {

        Picasso.with(cart.getBaseContext())
                .load(listData.get(position).getImage())
                .resize(70,70)
                .centerCrop()
                .into(cartViewHolder.cart_image);

        cartViewHolder.btn_quantity.setNumber(listData.get(position).getQuantity());
        cartViewHolder.btn_quantity.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(cart).updateCart(order);

                //Actualizar el txTotal
                //Calcular el precio total
                float total = 0;
                List<Order> orders = new Database(cart).getCarts(Common.currentUser.getPhone());
                for(Order item : orders){
                    total +=(Float.parseFloat(order.getPrice()))*(Integer.parseInt(item.getQuantity()));
                }

                Locale locale =  new Locale("es", "ES");
                NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

                cart.txTotalPrice.setText(fmt.format(total));
            }
        });

        Locale locale =  new Locale("es", "ES");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);

        //Calculando el precio total del carrito
        float price = (Float.parseFloat(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));
        cartViewHolder.tx_price.setText(fmt.format(price));

        cartViewHolder.tx_cart_name.setText(listData.get(position).getProductName());
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public Order getItem(int position){
        return  listData.get(position);
    }

    public void removeItem (int position){
        listData.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem (Order item, int position){
        listData.add(position,item);
        notifyItemInserted(position);
    }
}
