package com.asierph.vibbaybooks.FRAGMENTS;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.asierph.vibbaybooks.POJOS.Articulo;
import com.asierph.vibbaybooks.Settings;
import com.asierph.vibbaybooks.MainActivity;
import com.asierph.vibbaybooks.R;
import com.asierph.vibbaybooks.REST.ArticuloService;
import com.asierph.vibbaybooks.ADAPTERS.AdapterMisLibros;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MisLibrosFragment extends Fragment {

    private ListView listView;
    ArrayList<Articulo> listaLibros;
    private View view;

    private Settings settings;

    public MisLibrosFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bu = this.getArguments();  //PARA LEER LOS PARAMETROS

        ((MainActivity) getActivity()).getSupportActionBar().setTitle("VibbayBooks");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mislibros, container, false);
        listView = (ListView) view.findViewById(R.id.listaMisArticulos);
        settings = new Settings(getActivity().getApplicationContext());

        // Al pulsar sobre un articulo del ListView, se muestra la información de dicho articulo
        // en el fragment MiLibroFragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Para cargar el fragment en el contenedor:
                FragmentManager fragmentManager;
                FragmentTransaction fragmentTransaction;
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                MiLibroFragment miArticuloFG = new MiLibroFragment(); //Por cada fragment crear una clase


                Articulo ar = (Articulo) listView.getAdapter().getItem(position);
                settings.putDatoInt("idMiArticulo", ar.getIdarticulo());

                fragmentTransaction.replace(R.id.contenedor_frag, miArticuloFG);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    public void onViewCreated (View view, Bundle savedInstanceState){

        listaLibros = new ArrayList<Articulo>();
        final String URL = settings.getURL();

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

            Retrofit retro = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            final ArticuloService service = retro.create(ArticuloService.class);

            Call<List<Articulo>> call = service.getTodosMisArticulos(settings.getIdUsuario());
        //</editor-fold>
        call.enqueue(new Callback<List<Articulo>>() {
            @Override
            public void onResponse(Call<List<Articulo>> call, Response<List<Articulo>> response) {

                List<Articulo> lista = response.body();
                if (lista != null) {
                    // Rellenamos la lista con libros.
                    for (Articulo libro : lista) {
                        listaLibros.add(libro);
                    }
                    if(listaLibros.size() == 0){
                        Articulo libro = new Articulo(0);
                        listaLibros.add(libro);
                    }
                    AdapterMisLibros adapter = new AdapterMisLibros(getActivity(), listaLibros);
                    listView.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Articulo>> call, Throwable t) {}
        });

    }
}