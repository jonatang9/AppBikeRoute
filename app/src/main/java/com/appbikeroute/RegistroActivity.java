package com.appbikeroute;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegistroActivity extends AppCompatActivity {
    private EditText textnombre;
    private EditText textapellido;
    private EditText textemail;
    private EditText textnumero;
    private EditText textpass;
    private EditText textpass2;



    //Variables de los datos a registrar
    private String name = "";
    private String apellido = "";
    private String email = "";
    private String numero = "";
    private String pass = "";
    private String pass2 = "";


    FirebaseAuth mAuth;
    DatabaseReference mDatabase;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar);

        textnombre = (EditText) findViewById(R.id.textnombre);
        textapellido = (EditText) findViewById(R.id.textapellido);
        textemail = (EditText) findViewById(R.id.textemail);
        textnumero = (EditText) findViewById(R.id.textnumero);
        textpass = (EditText) findViewById(R.id.textpass);
        textpass2 = (EditText) findViewById(R.id.textpass2);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        Button bvolver = (Button) findViewById(R.id.bvolver);
        bvolver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistroActivity.this, AuthActivity.class));
                finish();
            }
        });

        Button botonregistrar = (Button) findViewById(R.id.botonregistrar);
        botonregistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = textnombre.getText().toString();
                apellido = textapellido.getText().toString();
                email = textemail.getText().toString();
                numero = textnumero.getText().toString();
                pass = textpass.getText().toString();
                pass2 = textpass2.getText().toString();


                if (!name.isEmpty() && !apellido.isEmpty() && !email.isEmpty() && !numero.isEmpty() && !pass.isEmpty() && !pass2.isEmpty()){

                    if (!pass.equals(pass2)){
                        textpass2.setError("La contraseña no es la misma");
                        textpass.setError("");
                    }else{
                        if (pass.length() >= 6){
                            registerUser();

                        }else{
                            Toast.makeText(RegistroActivity.this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                        }
                    }



                }else {
                    if (name.isEmpty()) textnombre.setError("");
                    if (apellido.isEmpty()) textapellido.setError("");
                    if (email.isEmpty()) textemail.setError("");
                    if (numero.isEmpty()) textnumero.setError("");
                    if (pass.isEmpty()) textpass.setError("");
                    if (pass2.isEmpty()) textpass2.setError("");
                    Toast.makeText(RegistroActivity.this, "Debe completar todos los campos", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

    private void registerUser(){
        mAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull  Task<AuthResult> task) {
                if (task.isSuccessful()){

                    Map<String, Object> map = new HashMap<>();
                    map.put("nombre", name);
                    map.put("apellido", apellido);
                    map.put("email", email);
                    map.put("numero",numero);
                    map.put("password", pass);


                    String id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();

                    mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            if (task2.isSuccessful()){
                                startActivity(new Intent(RegistroActivity.this, AuthActivity.class));
                                finish();
                            }else {
                                Toast.makeText(RegistroActivity.this, "No se pudo registrar sus datos correctamente o el Usuario ya esta registrado", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(RegistroActivity.this, "No se pudo registrar el usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


}