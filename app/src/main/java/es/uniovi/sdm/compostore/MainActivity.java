package es.uniovi.sdm.compostore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import es.uniovi.sdm.compostore.Common.Common;
import es.uniovi.sdm.compostore.Model.User;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {

    Button btnSignIn;
    Button btnSignUp;
    Button btnContinueUnlogged;

    TextView txtSlogan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ShareButton
        FacebookSdk.sdkInitialize(this.getApplicationContext());

        setContentView(R.layout.activity_main);

        //printKeyHash();

        btnSignIn = (Button)findViewById(R.id.btnSignIn);
        btnSignUp = (Button)findViewById(R.id.btnSignUp);
        btnContinueUnlogged= (Button)findViewById((R.id.btnContinueUnlogged));

        txtSlogan = (TextView)findViewById(R.id.txtSlogan);
        Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Madeleina Sans.otf");
        txtSlogan.setTypeface(face);

        Paper.init(this);

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Common.isConnectedToInternet(getBaseContext())) {
                    Intent signIn = new Intent(MainActivity.this, SignIn.class);
                    startActivity(signIn);
                }else{
                    Toast.makeText(MainActivity.this,"Please check your connection!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                Intent signUp = new Intent(MainActivity.this, SignUp.class);
                startActivity(signUp);
                }else{
                    Toast.makeText(MainActivity.this,"Please check your connection!!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        });

        btnContinueUnlogged.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Common.isConnectedToInternet(getBaseContext())) {
                Intent notLogged = new Intent(MainActivity.this, UserNotLoggedActivity.class);
                startActivity(notLogged);
                }else{
                    Toast.makeText(MainActivity.this,"Please check your connection!!", Toast.LENGTH_SHORT).show();
                    return;
                }


            }
        });

        //Check shared preferences
        String user = Paper.book().read(Common.USER_KEY);
        String pwd = Paper.book().read(Common.PWD_KEY);
        if(user != null && pwd != null){
            if(!user.isEmpty() && !pwd.isEmpty()){
                login(user,pwd);
            }
        }
    }

    //Share button
    private void printKeyHash(){
        try{
            PackageInfo info = getPackageManager().getPackageInfo("es.uniovi.sdm.compostore",PackageManager.GET_SIGNATURES);

            for(Signature signature : info.signatures){

                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash", Base64.encodeToString(md.digest(),Base64.DEFAULT));

            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void login(final String phone, final String pwd) {

        final ProgressDialog mDialog = new ProgressDialog(MainActivity.this);
        mDialog.setMessage("Please wait...");
        mDialog.show();

        //Inicializando la base de datos
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        if(Common.isConnectedToInternet(getBaseContext())){

            table_user.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    //Comprobar que el usuario existe en la base de datos
                    if (dataSnapshot.child(phone).exists()) {
                        //Coger informacion del usuario
                        mDialog.dismiss();
                        User user = dataSnapshot.child(phone).getValue(User.class);
                        user.setPhone(pwd); //Asignar al usuario su telefono


                                //Launch UserLoggedActivity
                                Intent loggedIntent = new Intent(MainActivity.this, UserLoggedActivity.class);
                                Common.currentUser = user;
                                startActivity(loggedIntent);
                                finish();
                            }




        else{
            Toast.makeText(MainActivity.this,"Please check your connection!!", Toast.LENGTH_SHORT).show();
        }


}
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(MainActivity.this,"Problem with shared preferences", Toast.LENGTH_SHORT).show();
                }
            });}}}
