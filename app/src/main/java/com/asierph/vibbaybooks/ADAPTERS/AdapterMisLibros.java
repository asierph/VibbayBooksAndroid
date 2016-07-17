package com.asierph.vibbaybooks.ADAPTERS;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.asierph.vibbaybooks.DecodificarImagen;
import com.asierph.vibbaybooks.POJOS.Articulo;
import com.asierph.vibbaybooks.R;
import com.asierph.vibbaybooks.Settings;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class AdapterMisLibros extends ArrayAdapter<Articulo> implements Filterable {

    private final Activity context;

    Articulo articulo;

    private ArrayList<Articulo> listaArticulos;
    private ArrayList<Articulo> listaLibrosOrdenada;

    private TextView miEstado;
    private TextView miDenominacion;
    private TextView miFecha;
    private ImageView miImagen;
    private Settings settings;

    public AdapterMisLibros(Activity context, ArrayList<Articulo> lista) {
        super(context, R.layout.item_milibro,lista);

        this.context = context;
        this.listaArticulos = lista;
        this.listaLibrosOrdenada = lista;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View libroView = inflater.inflate(R.layout.item_milibro, null, true);

        // Asignamos los valores
        miImagen = (ImageView) libroView.findViewById(R.id.miImagenArticulo);
        miEstado = (TextView) libroView.findViewById(R.id.miEstado);
        miDenominacion = (TextView) libroView.findViewById(R.id.miDenominacion);
        miFecha = (TextView) libroView.findViewById(R.id.miFecha);

        articulo = listaArticulos.get(position);
        if (listaArticulos.size() == 1 && articulo.getIdarticulo() == 0) {
            miDenominacion.setText("¡Sube tu primer libro cuanto antes!");
            miEstado.setText("");
            miFecha.setText("");
            miImagen.setVisibility(View.INVISIBLE);
        } else {
            final int id = articulo.getIdarticulo();
            settings = new Settings(context);

            miEstado.setText(Settings.estado(articulo.getEstado()));
            miDenominacion.setText(articulo.getDenominacion());

            // Decodificamos la imagen.
            String url = settings.getURL_IMAGENES() + articulo.getImagen() + ".jpg";
            new DecodificarImagen(miImagen).execute(url);

            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String textoFecha = df.format(articulo.getFechapubli());
            miFecha.setText("Fecha publicación: " + textoFecha);
        }
        return libroView;
    }

    @Override
    public int getCount() {return listaLibrosOrdenada.size();}
    @Override
    public Articulo getItem(int position) {return listaLibrosOrdenada.get(position);}
    @Override
    public Filter getFilter() {return super.getFilter();}


}
