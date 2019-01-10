package es.uniovi.sdm.compostore.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;

import es.uniovi.sdm.compostore.Common.Common;
import es.uniovi.sdm.compostore.Interface.ItemClickListener;
import es.uniovi.sdm.compostore.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener
        , View.OnCreateContextMenuListener {

    public TextView tx_cart_name, tx_price;
    public ElegantNumberButton btn_quantity;
    public ImageView cart_image;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;

    private ItemClickListener itemClickListener;

    public void setTx_cart_name(TextView tx_cart_name) {
        this.tx_cart_name = tx_cart_name;
    }


    public CartViewHolder(View itemView) {
        super(itemView);
        tx_cart_name = (TextView) itemView.findViewById(R.id.cart_item_name);
        tx_price = (TextView) itemView.findViewById(R.id.cart_item_Price);
        btn_quantity = (ElegantNumberButton) itemView.findViewById(R.id.btn_quantity);
        cart_image = (ImageView) itemView.findViewById(R.id.cart_image);
        view_background = (RelativeLayout) itemView.findViewById(R.id.view_background);
        view_foreground = (LinearLayout) itemView.findViewById(R.id.view_foreground);

        itemView.setOnCreateContextMenuListener(this);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Select action");
        menu.add(0, 0, getAdapterPosition(), Common.DELETE);
    }
}