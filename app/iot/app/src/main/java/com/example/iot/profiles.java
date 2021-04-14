package com.example.iot;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

//En la funcion profiles, se crean las variables a utilizar en este fragmento
public class profiles extends Fragment {
    //Variables que tomaran los datos ingresados por el usuario en la app
    long edad;
    long peso;
    long estatura;
    String sexo;
    private String correogg;
    // Variables de los textos que se verian en cada boton
    TextView bienvenidotxt,edadtxt,pesotxt,estaturatxt,generotxt;
    ImageButton botonhome1,botonperfil1,botoninfo1;
    View vista;
    //Conexion con firebase (base de datos) y las variables que se quieren leer de firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    @Override
    //* En la funcion onCreate se hace la lectura en este fragmento, de los datos que fueron ingresados por el usuario y se obtuvieron en la actividad registraroiniciarActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {//En caso de que getArguments() no este vacio, se entra al if
            //Haciendo uso de la funcion getArguments().getString("correo"), se obtiene el dato de correo para este caso.
            correogg = getArguments().getString("correo");

        }
    }
    //En la funcion onCreateView se asigna el fragment profiles a un XML y se obtienen los datos dados por el usuario
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        vista=inflater.inflate(R.layout.fragment_profiles, container, false);// Se asocia el codigo de profiles con un layout en XML

        obtenerDatos(correogg); // Se obtienen los datos dados por el usuario

        return vista;
    }
    //En la funciona obtenerDatos se obtendran los datos ingresados por el usuario
    private void obtenerDatos(String correo){

        db.collection("users").document(correo).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() { //Se evalua si llega un nuevo dato de los ingresados por el usuario
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) { //Se toman los datos ingresados por el usuario en la aplicacion
                if(documentSnapshot.exists()){ //Si los datos ingresados no esta vacio se entra al if
                    //Se toman las variables long y string ingresadas por el usuario, ademas se pone en field, el dato que se quiere leer
                    edad=documentSnapshot.getLong("edad"); //
                    peso=documentSnapshot.getLong("peso");
                    estatura=documentSnapshot.getLong("estatura");
                    sexo=documentSnapshot.getString("genero");
                    //Se asignan las variables a cada texto que se mostrara en el boton que se observa en: (R.id."Nombre del Boton")
                    bienvenidotxt = (TextView) vista.findViewById(R.id.bienvenidoEditar);
                    edadtxt = (TextView) vista.findViewById(R.id.EdadEditar);
                    pesotxt = (TextView) vista.findViewById(R.id.PesoEditar);
                    estaturatxt = (TextView) vista.findViewById(R.id.EstaturaEditar);
                    generotxt   =  (TextView) vista.findViewById(R.id.GeneroEditar);
                    //Se asignan los textos que se mostratan en cada boton
                    bienvenidotxt.setText("Hola "+correo+", tus datos actuales son:");
                    edadtxt.setText("Edad: "+edad);
                    pesotxt.setText("Peso: "+peso);
                    estaturatxt.setText("Estatura: "+estatura);
                    generotxt.setText("Genero: "+sexo);

                }
            }
        });
    }
}