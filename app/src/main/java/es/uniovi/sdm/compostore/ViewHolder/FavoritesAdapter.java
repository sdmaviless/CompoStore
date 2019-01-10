package es.uniovi.sdm.compostore.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import es.uniovi.sdm.compostore.Common.Common;
import es.uniovi.sdm.compostore.ComponentDetail;
import es.uniovi.sdm.compostore.Database.Database;
import es.uniovi.sdm.compostore.Interface.ItemClickListener;
import es.uniovi.sdm.compostore.Model.Favorites;
import es.uniovi.sdm.compostore.Model.Order;
import es.uniovi.sdm.compostore.R;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesViewHolder> {

    private Context context;
    private List<Favorites> favoritesList;

    public FavoritesAdapter(Context context, List<Favorites> favoritesList) {
        this.context = context;
        this.favoritesList = favoritesList;
    }

    @Override
    public FavoritesViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.favorites_item, viewGroup, false);
        return new FavoritesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FavoritesViewHolder favoritesViewHolder, final int position) {
        favoritesViewHolder.component_name.setText(favoritesList.get(position).getComponentName());
        favoritesViewHolder.component_price.setText(String.format("€ %s", favoritesList.get(position).getComponentPrice().toString()));
        Picasso.with(context).load(favoritesList.get(position).getComponentImage())
                .into(favoritesViewHolder.component_image);

        //Carrito Rapido

        favoritesViewHolder.quick_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isExists = new Database(context).checkComponentExists(favoritesList.get(position).getComponentId(), Common.currentUser.getPhone());
                if (!isExists) {
                    new Database(context).addToCart(new Order(
                            Common.currentUser.getPhone(),
                            favoritesList.get(position).getComponentId(),
                            favoritesList.get(position).getComponentName(),
                            "1",
                            favoritesList.get(position).getComponentPrice(),
                            favoritesList.get(position).getComponentDiscount(),
                            favoritesList.get(position).getComponentImage()
                    ));
                } else {
                    new Database(context).increaseCart(Common.currentUser.getPhone(),
                            favoritesList.get(position).getComponentId());
                }
                Toast.makeText(context, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

        final Favorites local = favoritesList.get(position);
        favoritesViewHolder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onClick(View view, int position, boolean isLongClick) {
                //Iniciar una nueva activity
                Intent componentDetail = new Intent(context, ComponentDetail.class);
                componentDetail.putExtra("ComponentId", favoritesList.get(position).getComponentId()); //Mandarle el Component Id a la nueva activity
                context.startActivity(componentDetail);
            }
        });


    }

    @Override
    public int getItemCount() {
        return favoritesList.size();
    }

    public void removeItem(int position) {
        favoritesList.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Favorites item, int position) {
        favoritesList.add(position, item);
        notifyItemInserted(position);
    }

    public Favorites getItem(int position) {
        return favoritesList.get(position);
    }

}
