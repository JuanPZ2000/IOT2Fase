package com.example.iot;

import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link home2#newInstance} factory method to
 * create an instance of this fragment.
 */


/**
 En la funcion home2 se inicializan las variables a utilizar en el codigo y se realiza la conexion con firabase

 */
public class home2 extends Fragment {
    //Variables utilizadas para graficacion en tiempo real
    private Handler mHandler = new Handler();
    private LineGraphSeries<DataPoint> series;
    private double lastXPoint = 0.4;
    // Variables de los textos que se verian en cada boton
    TextView textbienvenido, textToggleButton;
    TextView textEstadoPulsador;
    // Variables de los iconos que se verian en cada boton
    ImageButton botonhome, botonperfil, botoninfo;
    ImageView botella;
    //Variables que obtienen la lectura de cada sensor
    long temperatura, touch, wata;
    boolean f;
    long cont;
    //Conexion con firebase y las variables que se quieren leer de firebase
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference refHome = database.getReference("home");
    DatabaseReference refSensores, refTemperatura, refTouch, refWata;
    View vista;

    //Variables creadas por Android al crear el fragmento
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //Variables que tomaran los datos ingresados por el usuario en la app
    long edadgg;
    long pesogg;
    long estaturagg;
    String sexogg;
    private String correogg;

    public home2() {
        // Required empty public constructor
    }


    //
    public static home2 newInstance(String param1, String param2) {
        home2 fragment = new home2();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    //* En la funcion onCreate se hace la lectura en este fragmento, de los datos que fueron ingresados por el usuario y se obtuvieron en la actividad registraroiniciarActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) { //En caso de que getArguments() no este vacio, se entra al if
            //Haciendo uso de la funcion getArguments().getString("correo"), se obtiene el dato de correo para este caso.
            correogg = getArguments().getString("correo");
            edadgg = getArguments().getLong("edad");
            pesogg = getArguments().getLong("peso");
            estaturagg = getArguments().getLong("estatura");
            sexogg = getArguments().getString("sexo");

        }
    }

    @Override
    //En la funcion onCreateView se realiza la lectura de los sensores y permite la visualizacion de la lectura de estos, junto con la visualizacion de la grafica de temperatura.
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        vista = inflater.inflate(R.layout.fragment_home2, container, false); // Se asocia el codigo de home2 con un layout en XML
        //Lectura de sensores, asociando las variables al tema en firebase correspondiente para cada sensor
        refSensores = refHome.child("sensores");// Se asocia el tema de sensores, donde estara la lectura de todos los sensores
        //Se asocian los temas que estan dentro de "sensores", donde cada uno de estos es la lectura de un sensor
        refTemperatura = refSensores.child("temperatura");
        refTouch = refSensores.child("touch");
        refWata = refSensores.child("wata");
        //Se asigna una variable a cada texto que se mostrara en el boton que se observa en: (R.id."Nombre del Boton")
        textEstadoPulsador = (TextView) vista.findViewById(R.id.textViewPulsador);
        textToggleButton = (TextView) vista.findViewById(R.id.textView);
        textbienvenido = (TextView) vista.findViewById(R.id.textbienvenido);
        //Se asigna una variable a la imagen que se mostrara en el image view que se observa en: (R.id."Nombre del Boton")
        ImageView botella = (ImageView) vista.findViewById(R.id.imageView4);
        //Se llama cada una de las funciones que mostraran la lectura de los sensores en la aplicacion
        Temperatura(refTemperatura, textEstadoPulsador);
        Touch(refTouch, textEstadoPulsador);
        Wata(refWata, textToggleButton, botella);
        //Las siguientes lineas de codigo son para la graficacion del sensor de temperatura
        GraphView graph = (GraphView) vista.findViewById(R.id.graph); //Se asigna la variable a donde se mostrara el grafico en XML

        //Se crean primero 5 puntos iniciales en la grafica
        series = new LineGraphSeries<DataPoint>(new DataPoint[]{
                new DataPoint(0, 22),
                new DataPoint(0.1, 22),
                new DataPoint(0.2, 22),
                new DataPoint(0.3, 22),
                new DataPoint(0.4, 22)
        });
        graph.addSeries(series); //Se agregan los puntos creados al grafico
        //Se establecen las configuraciones en los ejes X y Y que tendra la grafica
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(1);
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setScalable(true);
        graph.getViewport().setScrollable(true);
        graph.getViewport().setScalableY(true);
        graph.getViewport().setScrollableY(true);
        addRandomDataPoint(); //Se llama a la funcion encargada de graficar la lectura del sensor de temperatura
        textbienvenido.setText("Hola" + " " + correogg + ", Bienvenido!"); //Se establece el mensaje mostrado en la aplicacion
        return vista;

    }

    //La funcion addRandomDataPoint se encargara de graficar el sensor de temperatura
    private void addRandomDataPoint() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                lastXPoint = lastXPoint + 0.1; //Se suma 0.1 al ultimo punto leido en X
                if ((f)) {
                    cont = temperatura;
                }

                series.appendData(new DataPoint(lastXPoint, cont), true, 1000); //Se agrega los puntos de X y Y que se desean graficar
                addRandomDataPoint(); //Se vuelve a llamar a la misma funcion para repetir este mismo proceso varias veces
            }
        }, 100);

    }

    //En la funcion Temperatura se establece cuando se agregara un nuevo punto a la grafica y cuando no
    private void Temperatura(final DatabaseReference refTemperatura, final TextView textEstadoPulsador) {

        refTemperatura.addValueEventListener(new ValueEventListener() { //Se evalua si se ha recibido un nuevo dato del sensor de temperatura
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) { //
                long estado_pulsador = (long) (dataSnapshot.getValue()); //Se toma el dato leido por temperatura y se asigna a la variable estado_pulsador
                temperatura = estado_pulsador; //Se actualiza la variable que lleva los datos que se grafican en la funcion addRandomDataPoint
                f = true; //Se pone f en true para que grafique el nuevo dato de temperatura


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //En la funcion Touch se muestra en la aplicacion la lectura del sensor touch
    private void Touch(final DatabaseReference refTouch, final TextView textEstadoPulsador) {

        refTouch.addValueEventListener(new ValueEventListener() {//Se evalua si se ha recibido un nuevo dato del sensor de touch
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long estado_pulsador = (long) (dataSnapshot.getValue()); //Se toma el dato leido por el touch y se asigna a la variable estado_pulsador
                touch = estado_pulsador;//Se actualiza la variable que lleva los datos del touch
                if (touch == 1) {
                    textEstadoPulsador.setText("Touch Activo"); //Si el sensor touch esta en alto, se imprime en pantalla touch activo
                } else {
                    textEstadoPulsador.setText("Touch Inactivo");//Si el sensor touch esta en bajo, se imprime en pantalla touch inactivo
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    //En la funcion Wata se realiza el mostrado del porcentaje de la botella tanto en imagen como en texto
    private void Wata(final DatabaseReference refWata, final TextView textToggleButton, final ImageView botella) {

        refWata.addValueEventListener(new ValueEventListener() {//Se evalua si se ha recibido un nuevo dato del circuito medidor de agua
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long estado_pulsador = (long) (dataSnapshot.getValue());//Se toma el dato leido por el circuito medidor de agua y se asigna a la variable estado_pulsador
                wata = estado_pulsador;//Se actualiza la variable que lleva los datos del touch
                //En caso de ser wata igual a 0,25,50,75,100. Se muestra en la aplicacion una botella que demuestra el porcentaje de llenado y tambien se muestra en un texto
                if (wata == 0) {
                    botella.setImageResource(R.drawable.cero);
                    textToggleButton.setText("0%");
                } else if (wata == 25) {
                    botella.setImageResource(R.drawable.venticinco);
                    textToggleButton.setText("25%");
                } else if (wata == 50) {
                    botella.setImageResource(R.drawable.cincuenta);
                    textToggleButton.setText("50%");
                } else if (wata == 75) {
                    botella.setImageResource(R.drawable.sietecinco);
                    textToggleButton.setText("75%");
                } else if (wata == 100) {
                    botella.setImageResource(R.drawable.sien);
                    textToggleButton.setText("100%");
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}