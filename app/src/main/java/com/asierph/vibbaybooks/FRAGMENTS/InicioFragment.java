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
import android.widget.SearchView;
import android.widget.Toast;

import com.asierph.vibbaybooks.POJOS.Articulo;
import com.asierph.vibbaybooks.Settings;
import com.asierph.vibbaybooks.MainActivity;
import com.asierph.vibbaybooks.R;
import com.asierph.vibbaybooks.REST.ArticuloService;
import com.asierph.vibbaybooks.ADAPTERS.AdapterLibros;

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


public class InicioFragment extends Fragment {

    ArrayList<Articulo> listaLibros;

    private ListView listView;
    private View view;
    private SearchView edtBuscar;
    private Settings settings;

    public InicioFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bu = this.getArguments();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("VibbayBooks");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_inicio, container, false);
        listView = (ListView) view.findViewById(R.id.listView);
        edtBuscar = (SearchView) view.findViewById(R.id.edtBuscar);
        settings = new Settings(getActivity().getApplicationContext());

        // Listener del boton del filtrado de la parte superior del Fragment.
        edtBuscar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                AdapterLibros adapter = (AdapterLibros) listView.getAdapter();
                if (adapter != null) {
                    adapter.getFilter().filter(newText);
                    return false;
                }
                return false;
            }
        });

        // Si pulsamos un item, se va al fragment LibroFragment
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Para cargar el fragment en el contenedor:
                FragmentManager fragmentManager;
                FragmentTransaction fragmentTransaction;
                fragmentManager = getActivity().getSupportFragmentManager();
                fragmentTransaction = fragmentManager.beginTransaction();
                LibroFragment articuloFG = new LibroFragment();
                Articulo ar = (Articulo) listView.getAdapter().getItem(position);
                settings.putDatoInt("idArticulo", ar.getIdarticulo());
                // Controlamos el estado del articulo ( 0 = disponible, 1 = vendido)
                if (ar.getEstado().equals("D")) {
                    settings.putDatoInt("estadoArticulo", 0);
                } else {
                    settings.putDatoInt("estadoArticulo", 1);
                }
                fragmentTransaction.replace(R.id.contenedor_frag, articuloFG);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
        return view;
    }

    public void onViewCreated (View view,Bundle savedInstanceState){

        listaLibros = new ArrayList<Articulo>();

        Settings settings = new Settings(getActivity().getApplicationContext());
        final String URL = settings.getURL();

        // Tratamiento de fechas
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT,JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        Gson gson = builder.create();
        //<editor-fold defaultstate="collapsed" desc="RETROFIT">
            Retrofit retro = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            final ArticuloService service = retro.create(ArticuloService.class);

            Call<List<Articulo>> call = service.getTodosArticulos();
        //</editor-fold>
        call.enqueue(new Callback<List<Articulo>>() {
            @Override
            public void onResponse(Call<List<Articulo>> call, Response<List<Articulo>> response) {
                List<Articulo> lista = response.body();
                if (lista != null) {
                    // Bucle para añadir todos los libtos a la lista.
                    for (Articulo libro : lista) {
                        listaLibros.add(libro);
                    }
                    AdapterLibros adapter = new AdapterLibros(getActivity(), listaLibros);
                    listView.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Articulo>> call, Throwable t) {}
        });

    }
}