package com.asierph.vibbaybooks.ADAPTERS;

import com.asierph.vibbaybooks.POJOS.Articulo;
import com.asierph.vibbaybooks.R;
import com.asierph.vibbaybooks.Settings;
import com.asierph.vibbaybooks.DecodificarImagen;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class AdapterLibros extends ArrayAdapter<Articulo> implements Filterable {

    private final Activity context;

    Articulo libro;

    private ArrayList<Articulo> listaLibros;
    private ArrayList<Articulo> listaLibrosOrdenada;


    private TextView estado;
    private TextView denominacion;
    private TextView precio;
    private TextView fecha;
    private ImageView imagen;

    FiltroArticulos filtro;

    private Settings settings;

    public AdapterLibros(Activity context, ArrayList<Articulo> lista) {
        super(context, R.layout.item_libro,lista);

        this.context = context;
        this.listaLibros = lista;
        this.listaLibrosOrdenada = lista;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View itemView = inflater.inflate(R.layout.item_libro, null, true);


        // Asignamos los valores de las Views.
        imagen = (ImageView) itemView.findViewById(R.id.imagenArticulo);
        estado = (TextView) itemView.findViewById(R.id.estado);
        denominacion = (TextView) itemView.findViewById(R.id.denominacion);
        precio = (TextView) itemView.findViewById(R.id.precio);
        fecha = (TextView) itemView.findViewById(R.id.fecha);

        libro = listaLibrosOrdenada.get(position);
        final int id = libro.getIdarticulo();
        settings = new Settings(context);
        estado.setText(Settings.estado(libro.getEstado()));
        denominacion.setText(libro.getDenominacion());


        // Decodificamos la imagen.
        String url = settings.getURL_IMAGENES() + libro.getImagen() + ".jpg";
        new DecodificarImagen(imagen).execute(url);
        // Establecemos el precio y su texto
        precio.setText("Precio inicial: " + libro.getPrecio() + "€");
        // Establecemos la fecha y su texto
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String textoFecha = df.format(libro.getFechapubli());
        fecha.setText("Fecha publicación: " + textoFecha);

        return itemView;
    }

    @Override
    public int getCount() {
        return listaLibrosOrdenada.size();
    }

    @Override
    public Articulo getItem(int position) {
        return listaLibrosOrdenada.get(position);
    }

    @Override
    public Filter getFilter() {
        if (filtro == null) {
            filtro = new FiltroArticulos();
        }
        return filtro;
    }

    //<editor-fold defaultstate="collapsed" desc="FILTRADO DE ARTICULOS">
    private class FiltroArticulos extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            listaLibrosOrdenada = new ArrayList<Articulo>();
            if (constraint != null && constraint.length() > 0) {
                // Recorrremos la lista ordenando los items.
                for (int i = 0; i < listaLibros.size(); i++) {
                    if (listaLibros.get(i).getDenominacion().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        listaLibrosOrdenada.add(listaLibros.get(i));
                    }
                }
                results.count = listaLibrosOrdenada.size();
                results.values = listaLibrosOrdenada;
            } else {
                results.count = listaLibros.size();
                results.values = listaLibros;
            }
            return results;
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results.count > 0) {
                listaLibrosOrdenada = (ArrayList<Articulo>) results.values;
            } else {
                listaLibrosOrdenada = listaLibros;
            }
            notifyDataSetChanged();
        }
    }
    //</editor-fold>
}
