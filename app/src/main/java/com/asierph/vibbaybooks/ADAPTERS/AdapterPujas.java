package com.asierph.vibbaybooks.ADAPTERS;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.asierph.vibbaybooks.POJOS.Puja;
import com.asierph.vibbaybooks.R;
import com.asierph.vibbaybooks.Settings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterPujas extends ArrayAdapter<Puja> implements Filterable {

    private final Activity context;

    Puja puja;

    private ArrayList<Puja> listaPujas;
    private ArrayList<Puja> listaPujasOrdenada;

    private TextView denominacion;
    private TextView precio;
    private TextView fecha;

    private Settings settings;

    public AdapterPujas(Activity context, ArrayList<Puja> lista) {
        super(context, R.layout.item_puja,lista);

        this.context = context;
        this.listaPujas = lista;
        this.listaPujasOrdenada = lista;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View itemView = inflater.inflate(R.layout.item_puja, null, true);

        // Asignamos los valores
        denominacion = (TextView) itemView.findViewById(R.id.txtDenomPuja);
        precio = (TextView) itemView.findViewById(R.id.txtPrecioPuja);
        fecha = (TextView) itemView.findViewById(R.id.txtFechaPuja);

        settings = new Settings(context);

        puja = listaPujas.get(position);
        // Controlador de sin existe alguna puja.
        if(listaPujas.size() == 1 && puja.getIdpuja() == 0){
            denominacion.setText("¿No te gustaria leer una nueva aventura? Puja por algun libro :D");
            precio.setText("");
            fecha.setText("");
        } else{
        // Existe puja asignamos los valores
            denominacion.setText(puja.getIdarticulo().getDenominacion());
            precio.setText("Precio pujado: " + puja.getPreciopuja() + "€");
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String textoFecha = df.format(puja.getFechapuja());
            fecha.setText("Fecha de puja: " + textoFecha);
        }
        return itemView;
    }

    @Override
    public int getCount() {
        return listaPujasOrdenada.size();
    }
    @Override
    public Puja getItem(int position) {return listaPujasOrdenada.get(position);}
    @Override
    public Filter getFilter() {return super.getFilter();}
}
