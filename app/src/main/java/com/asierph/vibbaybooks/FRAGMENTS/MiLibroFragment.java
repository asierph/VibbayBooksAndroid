package com.asierph.vibbaybooks.FRAGMENTS;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.asierph.vibbaybooks.POJOS.Articulo;
import com.asierph.vibbaybooks.Settings;
import com.asierph.vibbaybooks.MainActivity;
import com.asierph.vibbaybooks.R;
import com.asierph.vibbaybooks.DecodificarImagen;
import com.asierph.vibbaybooks.REST.ArticuloService;
import com.asierph.vibbaybooks.REST.PujaService;
import com.asierph.vibbaybooks.POJOS.Puja;
import com.asierph.vibbaybooks.ADAPTERS.AdapterMisArticulosPujas;

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
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MiLibroFragment extends Fragment {

    private Articulo libro;
    ArrayList<Puja> listaPujas;
    int idArticulo = 0;

    private TextView txtMiEstado;
    private TextView txtMiDenominacion;
    private TextView txtMiPrecio;
    private TextView txtMiFecha;
    private ImageView imgMiArticulo;
    private ListView miListView;

    private Settings settings;
    private String URL;

    public MiLibroFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bu = this.getArguments();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Artículo");
    }

    @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);

        // Cargar la vista
        View v = inflater.inflate(R.layout.fragment_milibro, container, false);

        settings = new Settings(getActivity().getApplicationContext());
        idArticulo = settings.getDatoInt("idMiArticulo", 0);

        imgMiArticulo = (ImageView) v.findViewById(R.id.imgMiArticulo);
        txtMiEstado = (TextView) v.findViewById(R.id.txtMiEstado);
        txtMiDenominacion = (TextView) v.findViewById(R.id.txtMiDenom);
        txtMiPrecio = (TextView) v.findViewById(R.id.txtMiPrecio);
        txtMiFecha = (TextView) v.findViewById(R.id.txtMiFecha);
        miListView = (ListView) v.findViewById(R.id.miListView);

        listaPujas = new ArrayList<Puja>();

        txtMiEstado.setText("");

        URL = settings.getURL();

        //<editor-fold defaultstate="collapsed" desc="RETROFIT">
            // Tratamiento de fechas
            GsonBuilder builder = new GsonBuilder();
            builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT,JsonDeserializationContext context) throws JsonParseException {
                    return new Date(json.getAsJsonPrimitive().getAsLong());
                }
            });
            Gson gson = builder.create();

            final Retrofit retro = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            final ArticuloService service = retro.create(ArticuloService.class);

            // Recopilamos el articulo indicando el idArticulo
            Call<Articulo> call = service.getArticulo(idArticulo);
        //</editor-fold>
        call.enqueue(new Callback<Articulo>() {
            @Override
            public void onResponse(Call<Articulo> call, Response<Articulo> response) {

                if(response.isSuccessful()){
                    libro = response.body();
                    txtMiEstado.setText(Settings.estado(libro.getEstado()));

                    if(libro.getEstado().equals("D")){  //Disponible = 0 , Vendido = 1
                        settings.putDatoInt("estadoMiArticulo", 0);
                    }else{
                        settings.putDatoInt("estadoMiArticulo", 1);
                    }
                    txtMiDenominacion.setText(libro.getDenominacion());
                    // Decodificar imagen
                    String url = settings.getURL_IMAGENES() + libro.getImagen() + ".jpg";
                    new DecodificarImagen(imgMiArticulo).execute(url);
                    txtMiPrecio.setText("Precio inicial: " + libro.getPrecio() + "€");
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String textoFecha = df.format(libro.getFechapubli());
                    txtMiFecha.setText("Fecha publicación: " + textoFecha);

                    final PujaService service = retro.create(PujaService.class);

                    Call<List<Puja>> call2 = service.getTodasPujasPorArticulo(idArticulo);
                    call2.enqueue(new Callback<List<Puja>>() {
                        @Override
                        public void onResponse(Call<List<Puja>> call, Response<List<Puja>> response) {

                            List<Puja> lista = response.body();
                            if (lista != null) {
                                // Añadimos todas las pujas a la lista.
                                for (Puja puja : lista) {
                                    listaPujas.add(puja);
                                }
                                AdapterMisArticulosPujas adapter = new AdapterMisArticulosPujas(getActivity(), listaPujas);
                                miListView.setAdapter(adapter);
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Puja>> call, Throwable t) {
                            Toast to = Toast.makeText(getActivity(), "Error al cargar las pujas", Toast.LENGTH_LONG);
                            to.show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<Articulo> call, Throwable t) {}
        });
        return v;
    }
    public void onViewCreated(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {}
}
