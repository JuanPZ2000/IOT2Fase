package com.example.iot;

import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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


public class prueba extends AppCompatActivity {
    private Handler mHandler = new Handler();
    private LineGraphSeries<DataPoint> series;
    private double lastXPoint = 0.4;
    private Random rnd = new Random();    // Write a message to the database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refHome = database.getReference("home");
    DatabaseReference  refSensores, refTemperatura,refTouch,refWata;
    TextView textbienvenido,textToggleButton;
    TextView textEstadoPulsador;
    ImageView botella;
    long temperatura,touch,wata;
    boolean f;
    long cont;
    long edad;
    long peso;
    long estatura;
    String sexo;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba);

        Intent intent = getIntent();
        String recuperamos_variable_string = getIntent().getStringExtra("correo");
        //db.collection("users").document(recuperamos_variable_string).get().addOnSuccessListener(){
        //  edad=it.get("edad") as String;
        //};


        refSensores = refHome.child("sensores");
        refTemperatura = refSensores.child("temperatura");
        refTouch = refSensores.child("touch");
        refWata = refSensores.child("wata");
        textEstadoPulsador = (TextView) findViewById(R.id.textViewPulsador);
        textToggleButton = (TextView) findViewById(R.id.textView);
        ImageView botella= (ImageView) findViewById(R.id.imageView4);

        Temperatura(refTemperatura, textEstadoPulsador);
        Touch(refTouch,textEstadoPulsador);
        Wata(refWata,textToggleButton,botella);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(0.1, 1),
                new DataPoint(0.2, 1),
                new DataPoint(0.3, 1),
                new DataPoint(0.4, 1)
        });
        graph.addSeries(series);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(1);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);
        addRandomDataPoint();
        obtenerDatos(recuperamos_variable_string);


    }
    public void goMain(View view){
        Intent i=new Intent(this,MainActivity.class);
        startActivity(i);
    }
    private void obtenerDatos(String correo){

        db.collection("users").document(correo).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    edad=documentSnapshot.getLong("edad");
                    peso=documentSnapshot.getLong("peso");
                    estatura=documentSnapshot.getLong("estatura");
                    sexo=documentSnapshot.getString("genero");
                    TextView textbienvenido  =   (TextView)  findViewById(R.id.textbienvenido);
                    textbienvenido.setText("Hola "+correo+", Bienvenido."+" "+edad+" "+peso+" "+estatura+" "+sexo);
                }
            }
        });
    }

    private void addRandomDataPoint(){
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lastXPoint=lastXPoint+0.1;
                if((f)){
                    cont=temperatura;
                }

                series.appendData(new DataPoint(lastXPoint,cont),true,1000);
                addRandomDataPoint();
            }
        },100);

    }

    private void Temperatura(final DatabaseReference refTemperatura, final TextView textEstadoPulsador) {

        refTemperatura.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long estado_pulsador = (long) (dataSnapshot.getValue());
                temperatura=estado_pulsador;
                f=true;


            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    private void Touch(final DatabaseReference refTouch, final TextView textEstadoPulsador) {

        refTouch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long estado_pulsador = (long) (dataSnapshot.getValue());
                touch=estado_pulsador;
                if(touch==1){
                    textEstadoPulsador.setText("Touch Activo");
                }else{
                    textEstadoPulsador.setText("Touch Inactivo");
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
    private void Wata(final DatabaseReference refWata, final TextView textToggleButton,final ImageView botella) {

        refWata.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long estado_pulsador = (long) (dataSnapshot.getValue());
                wata=estado_pulsador;
                if(wata==0){
                    botella.setImageResource(R.drawable.cero);
                    textToggleButton.setText("0%");
                }else if(wata==25){
                    botella.setImageResource(R.drawable.venticinco);
                    textToggleButton.setText("25%");
                }else if(wata==50){
                    botella.setImageResource(R.drawable.cincuenta);
                    textToggleButton.setText("50%");
                }else if(wata==75){
                    botella.setImageResource(R.drawable.sietecinco);
                    textToggleButton.setText("75%");
                }else if(wata==100){
                    botella.setImageResource(R.drawable.sien);
                    textToggleButton.setText("100%");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) { }
        });
    }
}