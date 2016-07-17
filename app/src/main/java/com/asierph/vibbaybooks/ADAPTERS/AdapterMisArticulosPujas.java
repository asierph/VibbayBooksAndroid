package com.asierph.vibbaybooks.ADAPTERS;


import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import com.asierph.vibbaybooks.FRAGMENTS.MiLibroFragment;
import com.asierph.vibbaybooks.POJOS.Puja;
import com.asierph.vibbaybooks.R;
import com.asierph.vibbaybooks.Settings;
import com.asierph.vibbaybooks.POJOS.Articulo;
import com.asierph.vibbaybooks.REST.ArticuloService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdapterMisArticulosPujas extends ArrayAdapter<Puja> implements Filterable {

    private final Activity context;

    Puja puja;

    private ArrayList<Puja> listaPujas;
    private ArrayList<Puja> listaOrdenadaPujas;

    private TextView txtMiUsuarioPuja;
    private TextView txtMiPrecioPuja;
    private TextView txtMiFechaPuja;
    private Button btnVender;

    private int estadoMiArticulo;

    private Settings settings;

    public AdapterMisArticulosPujas(Activity context, ArrayList<Puja> lista) {
        super(context, R.layout.item_mipuja,lista);
        this.context = context;
        this.listaPujas = lista;
        this.listaOrdenadaPujas = lista;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View pujaView = inflater.inflate(R.layout.item_mipuja, null, true);

        // Asignamos los valores
        txtMiUsuarioPuja = (TextView) pujaView.findViewById(R.id.txtMiUsuarioPuja);
        txtMiPrecioPuja = (TextView) pujaView.findViewById(R.id.txtMiPrecioPuja);
        txtMiFechaPuja = (TextView) pujaView.findViewById(R.id.txtMiFechaPuja);

        btnVender = (Button) pujaView.findViewById(R.id.btnVender);

        puja = listaPujas.get(position);
        if (listaPujas.size() > 0) {
            final int id = puja.getIdpuja();
            settings = new Settings(context);
            // Recopilamos el dato sobre el estado dela rticulo para determinar si es posible pujar por el.
            estadoMiArticulo = settings.getDatoInt("estadoMiArticulo", 0); //Disponible = 0 , Vendido = 1
            // Control de estado.
            if(estadoMiArticulo != 0){
                btnVender.setEnabled(false);
            }
            txtMiUsuarioPuja.setText("Usuario: " + puja.getIdusuariopujador().getEmail());
            txtMiPrecioPuja.setText("Precio: " + puja.getPreciopuja() + "€");
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String textoFecha = df.format(puja.getFechapuja());
            txtMiFechaPuja.setText("Fecha puja: " + textoFecha);
            // Listener del boton de vender.
            btnVender.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    venderArticulo(puja);
                }
            });
        }

        return pujaView;
    }

    @Override
    public int getCount() {

        return listaOrdenadaPujas.size();
    }

    @Override
    public Puja getItem(int position) {

        return listaOrdenadaPujas.get(position);
    }

    @Override
    public Filter getFilter() {

        return super.getFilter();
    }

    //<editor-fold defaultstate="collapsed" desc="METODO QUE VENDE UN ARTICULO">
        // Metodo que gestiona la venta, cuando se pulsa el boton de vender.
        public void venderArticulo(Puja puja){
            String URL = settings.getURL();
            //<editor-fold defaultstate="collapsed" desc="RETROFIT">
                // Tratamiento de DATETIME ( Date en Java )
                GsonBuilder builder = new GsonBuilder();
                builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                    public Date deserialize(JsonElement json, Type typeOfT,JsonDeserializationContext context) throws JsonParseException {return new Date(json.getAsJsonPrimitive().getAsLong());}
                });
                Gson gson = builder.create();

                final Retrofit retro = new Retrofit.Builder()
                        .baseUrl(URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
                // Creamos el servicio rest
                final ArticuloService service = retro.create(ArticuloService.class);
                final Articulo a = new Articulo(puja.getIdarticulo().getIdarticulo(), puja.getIdarticulo().getDenominacion(), puja.getIdarticulo().getImagen(), puja.getIdarticulo().getPrecio(),
                        puja.getIdarticulo().getFechapubli(), "V", puja.getIdarticulo().getIdvendedor());
                Call<String> call = service.putArticulo(puja.getIdarticulo().getIdarticulo(),a);
            //</editor-fold>
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if(response.isSuccessful()){

                        Toast t = Toast.makeText(context, "¡Libro vendido!", Toast.LENGTH_LONG);
                        t.show();
                        // Recargar el fragment actual con el estado cambiado
                        FragmentManager fragmentManager;
                        FragmentTransaction fragmentTransaction;
                        fragmentManager = ((FragmentActivity)context).getSupportFragmentManager();
                        fragmentTransaction = fragmentManager.beginTransaction();

                        settings.putDatoInt("idMiArticulo", a.getIdarticulo());

                        MiLibroFragment miFG = new MiLibroFragment();
                        fragmentTransaction.replace(R.id.contenedor_frag, miFG);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                }
                @Override
                public void onFailure(Call<String> call, Throwable t) {}
            });
        }
    //</editor-fold>

}
