package com.example.iot;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

public class signupActivity extends AppCompatActivity {
    //Creacion de variables
    private FirebaseAuth mAuth;
    private EditText correo;
    private EditText contrasena;
    private EditText contrasenaconfirmacion;
    private EditText edad;
    private EditText peso;
    private EditText estatura;
    RadioGroup radioGroup;
    RadioButton radioButton;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DatabaseReference refHome = database.getReference("home");
    DatabaseReference refSensores, refTemperatura, refTouch, refWata;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        //Asociacion de variables a sus id en xml
        mAuth = FirebaseAuth.getInstance();
        correo=findViewById(R.id.correo);
        contrasena=findViewById(R.id.contrasena);
        contrasenaconfirmacion=findViewById(R.id.contrasenaconfirmacion);
        edad=findViewById(R.id.textoedadsignup);
        peso=findViewById(R.id.textopesosignup);
        estatura=findViewById(R.id.textoestaturasignup);
        radioGroup = findViewById(R.id.radiogroupsignup);

    }
    //Funcion para volver al menu principal cuando se oprima el boton de back
    public void goMain(View view){
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
    }
    public void onStart() {
        super.onStart();
        //Verifica si el usuario no es nulo
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);
    }
    //Funcion registrar al usuario a Firebase
    public void registrarUsuario(View view){

        if(contrasena.getText().toString().equals(contrasenaconfirmacion.getText().toString())){ //Verifica si las contrasenhas coinciden

            mAuth.createUserWithEmailAndPassword(correo.getText().toString(), contrasena.getText().toString()) //Crea el usuario con el correo y contrasena
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() { //se agrega un listener para cuando la tarea sea completada con exito
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) { //Pregunta si la tarea fue completada con exito
                                int radioId = radioGroup.getCheckedRadioButtonId(); //se obtiene el id del boton que fue presionado
                                radioButton = findViewById(radioId); //se asocia la variable con el id del boton seleccionado
                                Map<String, Object> map = new HashMap<>(); //se crea un mapa para enviar los datos a firestore y almacenarlos
                                map.put("correo",correo.getText().toString() ); //Se le asocia un valor a cada clave
                                map.put("contraseña", contrasena.getText().toString());
                                map.put("edad", parseInt(edad.getText().toString()));
                                map.put("peso", parseInt(peso.getText().toString()));
                                map.put("estatura", parseInt(estatura.getText().toString()));
                                map.put("genero",radioButton.getText().toString() );
                                db.collection("users").document(correo.getText().toString()).set(map); //se le asignan los valores al usuario creado

                                Toast.makeText(getApplicationContext(),"Usuario creado.",Toast.LENGTH_SHORT).show(); //Muestra notificacion en pantalla
                                FirebaseUser user = mAuth.getCurrentUser();
                                Intent i= new Intent(getApplicationContext(),MainActivity.class); //Se va al main activity
                                startActivity(i); //Ejecuta el intent i
                                //updateUI(user);
                            } else {
                                //Si falla el registro muestra notificacion en pantalla
                                Toast.makeText(getApplicationContext(),"Authentication failed.",Toast.LENGTH_SHORT).show();
                                //updateUI(null);
                            }

                            // ...
                        }
                    });
        }else{
            Toast.makeText(this,"Las contraseñas no coinciden",Toast.LENGTH_SHORT).show();//Muestra notificacion en pantalla
        }

    }
}