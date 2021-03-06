package es.uniovi.sdm.compostore;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import es.uniovi.sdm.compostore.Common.Common;
import es.uniovi.sdm.compostore.Model.User;
import io.paperdb.Paper;

public class SignIn extends AppCompatActivity {

    EditText editPhone, editPassword;
    Button btnSignIn;

    //Shared preferences stuff
    //private SharedPreferences mySp;
    CheckBox checkBoxRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editPhone = (EditText) findViewById(R.id.editPhone);
        btnSignIn = (Button) findViewById(R.id.btnSignIn);

        //mySp = getSharedPreferences("My preferences", Context.MODE_PRIVATE);
        checkBoxRememberMe = (CheckBox) findViewById(R.id.checkBoxRememberMe);
        Paper.init(this);


        //Inicializando la base de datos
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");

        loadPreferences();

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                    mDialog.setMessage("Please wait...");
                    mDialog.show();

                    table_user.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            //Comprobar que el usuario existe en la base de datos
                            if (dataSnapshot.child(editPhone.getText().toString()).exists()) {
                                //Coger informacion del usuario
                                mDialog.dismiss();
                                User user = dataSnapshot.child(editPhone.getText().toString()).getValue(User.class);
                                user.setPhone(editPhone.getText().toString()); //Asignar al usuario su telefono

                                if (user.getPassword().equals(editPassword.getText().toString())) {
                                    {
                                        //Save preferences if the user says so
                                        if (checkBoxRememberMe.isChecked()) {
                                            savePreferences();
                                        }

                                        //Launch UserLoggedActivity
                                        Intent loggedIntent = new Intent(SignIn.this, UserLoggedActivity.class);
                                        Common.currentUser = user;
                                        startActivity(loggedIntent);
                                        finish();
                                    }
                                } else {
                                    Toast.makeText(SignIn.this, "Incorrect Password", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                mDialog.dismiss();
                                Toast.makeText(SignIn.this, "User does not exist", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
        });

    }

    @Override
    public void onBackPressed() {
        //Go back to products
        Intent loggedIntent = new Intent(SignIn.this, MainActivity.class);
        startActivity(loggedIntent);
        finish();
    }

    public void savePreferences() { //when the user and password is correct, save them in shared preferences
        Paper.book().write(Common.USER_KEY,editPhone.getText().toString());
        Paper.book().write(Common.PWD_KEY,editPassword.getText().toString());

    }

    public void loadPreferences() {

        //retrieve preferences
        String phoneNumberPref = Paper.book().read(Common.USER_KEY);
        String passwordPref = Paper.book().read(Common.PWD_KEY);

        editPhone.setText(phoneNumberPref);
        editPassword.setText(passwordPref);
    }
}
