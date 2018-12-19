package es.uniovi.sdm.compostore;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import es.uniovi.sdm.compostore.Interface.ItemClickListener;
import es.uniovi.sdm.compostore.Model.Component;
import es.uniovi.sdm.compostore.ViewHolder.ComponentViewHolder;

public class ComponentsListNotLogged extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;

    FirebaseDatabase database;
    DatabaseReference componentListNotLogged;

    String categoryId="";

    FirebaseRecyclerAdapter<Component,ComponentViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_components_list_not_logged);

        //Firebase
        database = FirebaseDatabase.getInstance();
        componentListNotLogged = database.getReference("Components");

        recyclerView = (RecyclerView) findViewById(R.id.recycler_component);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Recibimos el intent aqui
        if(getIntent() != null){
            categoryId = getIntent().getStringExtra("CategoryId");
        }
        if(!categoryId.isEmpty() && categoryId != null){
            loadListComponents(categoryId);
        }

    }

    private void loadListComponents(String categoryId) {
        adapter = new FirebaseRecyclerAdapter<Component, ComponentViewHolder>(
                Component.class,
                R.layout.component_item,
                ComponentViewHolder.class,
                //Mismo que: Select * from Components where CategoryId = ..
                componentListNotLogged.orderByChild("CategoryId").equalTo(categoryId)) {
            @Override
            protected void populateViewHolder(ComponentViewHolder viewHolder, Component model, int position) {
                viewHolder.component_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.component_image);

                final Component local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Iniciar una nueva activity
                        Intent componentDetailNotLogged = new Intent(ComponentsListNotLogged.this, ComponentDetailNotLogged.class);
                        componentDetailNotLogged.putExtra("ComponentId", adapter.getRef(position).getKey()); //Mandarle el Component Id a la nueva activity
                        startActivity(componentDetailNotLogged);
                    }
                });
            }
        };

        recyclerView.setAdapter(adapter);

    }


}
