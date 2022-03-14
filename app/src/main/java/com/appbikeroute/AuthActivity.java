package com.appbikeroute;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import org.jetbrains.annotations.NotNull;

public class AuthActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private TextView emailText;
    private TextView passText;
    private String email;
    private String pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        //Auth Event
        mAuth = FirebaseAuth.getInstance();

        //Analytics Event
        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    Bundle bundle = new Bundle();
    bundle.putString("message","Integracion de FireBase completa");
    mFirebaseAnalytics.logEvent("InitScreen",bundle);


        Button lboton = findViewById(R.id.loginButton);
        Button rboton = findViewById(R.id.registroboton);
        emailText = (EditText) findViewById(R.id.emailEditText);
        passText = (EditText) findViewById(R.id.passwordEditText);


        rboton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AuthActivity.this, RegistroActivity.class));
            }
        });

        lboton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emailText.getText().toString().isEmpty() || passText.getText().toString().isEmpty()){
                    if (emailText.getText().toString().isEmpty()){
                        emailText.setError("Correo Obligatorio");
                    }
                    if (passText.getText().toString().isEmpty()){
                        passText.setError("Error Contraseña");
                    }
                }else {
                    email = emailText.getText().toString();
                    pass = passText.getText().toString();

                    loginUser();

                }
            }
        });





    }

    private void loginUser(){
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    startActivity(new Intent(AuthActivity.this, PrincipalActivity.class));
                    finish();
                }else{
                    Toast.makeText(AuthActivity.this, "Error, no se pudo iniciar sesion, compruebe Email y/o Contraseña", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /////// AutoLogeo
    @Override
    protected void onStart() {
        super.onStart();
        if(mAuth.getCurrentUser() != null){
            startActivity(new Intent(AuthActivity.this, PrincipalActivity.class));
            finish();
        }
    }


}

