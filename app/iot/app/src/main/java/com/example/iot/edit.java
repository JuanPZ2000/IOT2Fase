package com.example.iot;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import static java.lang.Integer.parseInt;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link edit#newInstance} factory method to
 * create an instance of this fragment.
 */

//En la funcion edit, se crean las variables a utilizar en este fragmento
public class edit extends Fragment {
    //Se crean las variables que tomaran algunos datos ingresados por el usuario
    private EditText edad;
    private EditText peso;
    private EditText estatura;
    private String correogg;
    //Se crea variable que guardara el boton
    private Button btneditar;
    //Se crea variable para editar un texto de un boton
    TextView editartxt;
    RadioGroup radioGroup;
    RadioButton radioButton;
    //Conexion con firebase (base de datos)
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    View vista;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public edit() {
        // Required empty public constructor
    }


    public static edit newInstance(String param1, String param2) {
        edit fragment = new edit();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    // la funcion onCreate toma los datos de correo del usuario
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {//En caso de que getArguments() no este vacio, se entra al if
            mParam1 = getArguments().getString(ARG_PARAM1);
            correogg = getArguments().getString("correo");//Haciendo uso de la funcion getArguments().getString("correo"), se obtiene el dato de correo para este caso.
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    //En la funcion onCreateView se realiza la obtencion de los datos ingresador por el usuario.
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista=inflater.inflate(R.layout.fragment_edit, container, false); // Se asocia el codigo de edit con un layout en XML
        editartxt= (TextView) vista.findViewById(R.id.textViewEditar); // Se asigna el texto del boton textViewEditar a editartxt
        editartxt.setText("Editando datos de: "+correogg); // Se establece el texto que mostrara el boton textViewEditar
        edad=vista.findViewById(R.id.textoedadeditar); //Se asigna a la variable el boton textoedadeditar
        peso=vista.findViewById(R.id.textopesoseditar);//Seasigna a la variable el boton textopesoeditar
        estatura=vista.findViewById(R.id.textoestaturaeditar);//Se asigna a la variable el boton textoestaturaeditar
        radioGroup = vista.findViewById(R.id.radiogroupeditar); //Se asigna a la variable el boton donde se ingresa el genero
        btneditar=vista.findViewById(R.id.editarBtn); //Se asigna a la variable el boton editarBtn
        btneditar.setOnClickListener(new View.OnClickListener() { //Se evalua si se ha presionado el boton btneditar
            @Override

            public void onClick(View v) { //Se ejecuta la funcion cuando btneditar se presiona
                int radioId = radioGroup.getCheckedRadioButtonId();
                radioButton = vista.findViewById(radioId);
                //Se actualizan los datos del usuario en la aplicacion
                Map<String, Object> map = new HashMap<>();
                map.put("edad", parseInt(edad.getText().toString()));
                map.put("peso", parseInt(peso.getText().toString()));
                map.put("estatura", parseInt(estatura.getText().toString()));
                map.put("genero",radioButton.getText().toString() );
                db.collection("users").document(correogg).set(map);//Se actualizan los datos en la base de datos con el correo asociado al usuario
                Toast.makeText(getContext(),"Datos actualizados con éxito.",Toast.LENGTH_LONG).show(); //Se muestra un mensaje en pantalla de datos actualizados con exito
            }
        });
        // Inflate the layout for this fragment
        return vista;
    }
}