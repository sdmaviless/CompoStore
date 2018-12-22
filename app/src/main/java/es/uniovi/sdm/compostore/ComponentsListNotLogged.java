package es.uniovi.sdm.compostore;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

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

    //Barra de busqueda
    FirebaseRecyclerAdapter<Component,ComponentViewHolder> searchAdapter;
    List<String> suggestList = new ArrayList<>();
    MaterialSearchBar materialSearchBar;

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

        //Barra de busqueda
        materialSearchBar = (MaterialSearchBar)findViewById(R.id.searchBar);
        materialSearchBar.setHint("Enter component name");
        loadSuggest();
        materialSearchBar.setLastSuggestions(suggestList);
        materialSearchBar.setCardViewElevation(10);
        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //Cuando escribimos el texto, sugerimos la lista

                List<String> suggest = new ArrayList<String>();
                for(String search:suggestList){
                    if(search.toLowerCase().contains(materialSearchBar.getText().toLowerCase())){
                        suggest.add(search);
                    }
                }
                materialSearchBar.setLastSuggestions(suggest);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {
                //Cuando la barra de busqueda esta cerrada
                //Restaura el adapter original
                if(!enabled){
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                //Cuando se termina la busqueda
                //Enseña el resultado del adapter
                startSearch(text);
            }

            @Override
            public void onButtonClicked(int buttonCode) {

            }
        });

    }

    private void startSearch(CharSequence text) {
        searchAdapter = new FirebaseRecyclerAdapter<Component, ComponentViewHolder>(
                Component.class,
                R.layout.component_item,
                ComponentViewHolder.class,
                componentListNotLogged.orderByChild("Name").equalTo(text.toString()) //Comparamos los nombres
        ) {
            @Override
            protected void populateViewHolder(ComponentViewHolder viewHolder, Component model, int position) {
                viewHolder.component_name.setText(model.getName());
                Picasso.with(getBaseContext()).load(model.getImage()).into(viewHolder.component_image);

                final Component local = model;
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        //Iniciar una nueva activity
                        Intent componentDetail = new Intent(ComponentsListNotLogged.this, ComponentDetail.class);
                        componentDetail.putExtra("ComponentId", searchAdapter.getRef(position).getKey()); //Mandarle el Component Id a la nueva activity
                        startActivity(componentDetail);
                    }
                });
            }
        };
        recyclerView.setAdapter(searchAdapter);
    }


    private void loadSuggest() {
        componentListNotLogged.orderByChild("CategoryId").equalTo(categoryId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                            Component item = postSnapshot.getValue(Component.class);
                            suggestList.add(item.getName()); //Añade un nombre de componente a la lista
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

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
