package com.example.iot;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link info#newInstance} factory method to
 * create an instance of this fragment.
 */
public class info extends Fragment {
    //Se definen las variables
    private Button botonqs;
    private Button botoncf;
    private Button botondb;
    long x=0;
    View vista;
    //Se define las variables referentes a los fragmentos
    qs Qs=new qs();
    comofunciona Comofunciona= new comofunciona();
    diagramadebloques Diagramadebloques= new diagramadebloques();
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public info() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment info.
     */
    // TODO: Rename and change types and number of parameters
    //Se obtiene el bundle de argumentos dados desde el activity en este caso no se obtiene nada, son los arugmentos por default
    public static info newInstance(String param1, String param2) {
        info fragment = new info();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        vista=inflater.inflate(R.layout.fragment_info, container, false);
        //Se asignan los id a los botones
        botonqs=vista.findViewById(R.id.buttonqs);
        botoncf=vista.findViewById(R.id.buttoncf);
        botondb=vista.findViewById(R.id.buttondb);
        //Este if se hace con el proposito de que cuando entre por primera vez a la pestana de info se vea el fragmento de quienes somos
        if(x==0){

            FragmentTransaction transition1= getChildFragmentManager().beginTransaction();
            transition1.replace(R.id.fragmentdelfragment,Qs);
            transition1.commit();
            x=1;
        }
        //cuando se presione el boton se muestra en pantalla el fragmento quienes somos
        botonqs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transition1= getChildFragmentManager().beginTransaction();
                transition1.replace(R.id.fragmentdelfragment,Qs);
                transition1.commit();
            }
        });
        //cuando se presione el boton se muestra en pantalla el fragmento como funciona
        botoncf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transition1= getChildFragmentManager().beginTransaction();
                transition1.replace(R.id.fragmentdelfragment,Comofunciona);
                transition1.commit();
            }
        });
        //cuando se presione el boton se muestra en pantalla el fragmento diagrama de bloques
        botondb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transition1 = getChildFragmentManager().beginTransaction();
                transition1.replace(R.id.fragmentdelfragment, Diagramadebloques);
                transition1.commit();
            }

        });
        // Inflate the layout for this fragment
        return vista;
    }
}