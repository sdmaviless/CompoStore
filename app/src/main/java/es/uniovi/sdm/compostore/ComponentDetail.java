package es.uniovi.sdm.compostore;

import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import es.uniovi.sdm.compostore.Database.Database;
import es.uniovi.sdm.compostore.Model.Component;
import es.uniovi.sdm.compostore.Model.Order;

public class ComponentDetail extends AppCompatActivity {

    TextView component_name, component_price, component_description;
    ImageView component_image;
    CollapsingToolbarLayout collapsingToolbarLayout;
    FloatingActionButton btnCart;
    ElegantNumberButton numberButton;

    String componentId="";

    FirebaseDatabase database;
    DatabaseReference components;

    Component currentComponent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_component_detail);

        //Firebase
        database = FirebaseDatabase.getInstance();
        components = database.getReference("Components");

        //Inicializar la vista
        numberButton = (ElegantNumberButton) findViewById(R.id.number_button);
        btnCart = (FloatingActionButton) findViewById(R.id.btnCart);

        btnCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Database(getBaseContext()).addToCart(new Order(
                        componentId,
                        currentComponent.getName(),
                        numberButton.getNumber(),
                        currentComponent.getPrice(),
                        currentComponent.getDiscount()
                ));

                Toast.makeText(ComponentDetail.this, "Added to Cart", Toast.LENGTH_SHORT).show();
            }
        });

        component_description = (TextView) findViewById(R.id.component_description);
        component_name = (TextView) findViewById(R.id.component_name);
        component_price = (TextView) findViewById(R.id.component_price);
        component_image = (ImageView) findViewById(R.id.img_component);

        collapsingToolbarLayout =(CollapsingToolbarLayout) findViewById(R.id.collapsing);
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        //Obtener el component id de el Intent

        if(getIntent() !=null){
            componentId = getIntent().getStringExtra("ComponentId");
            if(!componentId.isEmpty()){
                getDetailComponent(componentId);
            }
        }

    }

    private void getDetailComponent(String componentId) {
        components.child(componentId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                currentComponent = dataSnapshot.getValue(Component.class);

                //Configurar imagen
                Picasso.with(getBaseContext()).load(currentComponent.getImage())
                        .into(component_image);

                collapsingToolbarLayout.setTitle(currentComponent.getName());
                component_price.setText(currentComponent.getPrice());
                component_name.setText(currentComponent.getName());
                component_description.setText(currentComponent.getDescription());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
