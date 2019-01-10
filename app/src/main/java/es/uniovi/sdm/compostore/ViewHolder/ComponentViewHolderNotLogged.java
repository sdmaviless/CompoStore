package es.uniovi.sdm.compostore.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import es.uniovi.sdm.compostore.Interface.ItemClickListener;
import es.uniovi.sdm.compostore.R;

public class ComponentViewHolderNotLogged
        extends RecyclerView.ViewHolder implements View.OnClickListener {

    public TextView component_name;
    public ImageView component_image, fav_image;

    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public ComponentViewHolderNotLogged(View itemView) {
        super(itemView);

        component_name = (TextView) itemView.findViewById(R.id.component_name);
        component_image = (ImageView) itemView.findViewById(R.id.component_image);
        fav_image = (ImageView) itemView.findViewById(R.id.fav);

        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        itemClickListener.onClick(view, getAdapterPosition(), false);
    }
}
