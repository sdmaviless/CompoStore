package es.uniovi.sdm.compostore.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import es.uniovi.sdm.compostore.Interface.ItemClickListener;
import es.uniovi.sdm.compostore.R;

public class FavoritesViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView component_name, component_price;
    public ImageView component_image,fav_image,share_image, quick_cart;

    private ItemClickListener itemClickListener;

    public RelativeLayout view_background;
    public LinearLayout view_foreground;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public FavoritesViewHolder(View itemView) {
        super(itemView);

        component_name = (TextView) itemView.findViewById(R.id.component_name);
        component_image =(ImageView) itemView.findViewById(R.id.component_image);
        fav_image =(ImageView) itemView.findViewById(R.id.fav);
        share_image =(ImageView) itemView.findViewById(R.id.btnShare);
        component_price = (TextView) itemView.findViewById(R.id.component_price);
        quick_cart = (ImageView) itemView.findViewById(R.id.btn_quick_cart);

        view_background = (RelativeLayout)itemView.findViewById(R.id.view_background);
        view_foreground = (LinearLayout)itemView.findViewById(R.id.view_foreground);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view,getAdapterPosition(),false);
    }
}
