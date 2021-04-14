package com.example.iot;

import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import android.os.Handler;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static java.lang.Integer.parseInt;


public class registraroiniciarActivity extends AppCompatActivity {
    //inicialización de variables
    ImageButton botonhome,botonperfil,botoninfo,botonedit;
    long edad;
    long peso;
    long estatura;
    String sexo;
    //declaración de fragments
    home2 Home2=new home2();
    profiles Profiles= new profiles();
    edit Edit= new edit();
    info Info= new info();
    //conectividad con firestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    long x=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registraroiniciar);

        Bundle bundle= new Bundle();//se crea un bundle para pasar variables a un fragment
        Intent intent = getIntent();//recuperación de variables de activity anterior
        String recuperamos_variable_string = getIntent().getStringExtra("correo");//Recuperación de usuario
        bundle.putString("correo",recuperamos_variable_string);//Envío de variable correo a fragment
        //Envío de bundle a fragments Home2, profiles y edit
        Home2.setArguments(bundle);
        Profiles.setArguments(bundle);
        Edit.setArguments(bundle);
        //Identificación de botones por id
        botonhome=(ImageButton) findViewById(R.id.imageButton);
        botonperfil=(ImageButton) findViewById(R.id.imageButton2);
        botoninfo=(ImageButton) findViewById(R.id.imageButton3);
        botonedit=(ImageButton) findViewById(R.id.imageButton4);
        //Obtención de datos registrados por el usuario por medio de firestore
        db.collection("users").document(recuperamos_variable_string).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    edad=documentSnapshot.getLong("edad");
                    peso=documentSnapshot.getLong("peso");
                    estatura=documentSnapshot.getLong("estatura");
                    sexo=documentSnapshot.getString("genero");
                    bundle.putLong("edad",edad);
                    bundle.putLong("peso",peso);
                    bundle.putLong("estatura",estatura);
                    bundle.putString("sexo",sexo);
                }
            }
        });

        //las siguientes líneas de código hacen que cuando se entre a la aplicación el primer fragment que se vea sea Home2
        if(x==0){
            FragmentTransaction transition1= getSupportFragmentManager().beginTransaction();
            transition1.replace(R.id.fragment11,Home2);
            transition1.commit();
            x=1;
        }
        //lectura de boton home para cambio de fragment
        botonhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transition1= getSupportFragmentManager().beginTransaction();
                transition1.replace(R.id.fragment11,Home2);
                transition1.commit();
            }
        });
        //lectura de boton perfil para cambio de fragment
        botonperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transition1= getSupportFragmentManager().beginTransaction();
                transition1.replace(R.id.fragment11,Profiles);
                transition1.commit();
            }
        });
        //lectura de boton edit para cambio de fragment
        botonedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transition1= getSupportFragmentManager().beginTransaction();
                transition1.replace(R.id.fragment11,Edit);
                transition1.commit();
            }
        });
        //lectura de boton info para cambio de fragment
        botoninfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transition1= getSupportFragmentManager().beginTransaction();
                transition1.replace(R.id.fragment11,Info);
                transition1.commit();
            }
        });
    }
    //esta función hace que cuando se presione el botón sing out se vaya al inicio de sesión
    public void goMain(View view){
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
    }

}