package com.example.iot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import android.os.Handler;

import java.util.Random;

public class loginActivity extends AppCompatActivity {
    private EditText correo;
    private EditText contrasena;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //Creacion de variables
        mAuth = FirebaseAuth.getInstance();
        correo=findViewById(R.id.correoS);
        contrasena=findViewById(R.id.contrasenaS);
    }
    //Funcion para volver al menu principal cuando se oprima el boton de back
    public void goMain(View view){
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
    }
    //Funcion para iniciar sesion cuando oprima el boton de log in (definido en onclick en xml)
    public void iniciarSesion(View view){
        mAuth.signInWithEmailAndPassword(correo.getText().toString(), contrasena.getText().toString()) //Verifica si el correo y la contrasenha coinciden en el sistema
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Inicio de sesion exitoso
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(getApplicationContext(),"Sesion iniciada.",Toast.LENGTH_SHORT).show();
                            // Pasaremos de la actividad actual al Activity principal
                            Intent i= new Intent(getApplicationContext(),registraroiniciarActivity.class);
                            i.putExtra("correo", correo.getText().toString());
                            startActivity(i);
                            //updateUI(user);
                        } else {
                            //Inicio de sesion no exitoso, muestra mensaje en pantalla
                            Toast.makeText(getApplicationContext(), "Authentication failed.",

                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                            // ...
                        }

                        // ...
                    }
                });
    }
}