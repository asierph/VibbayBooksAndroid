package com.asierph.vibbaybooks.FRAGMENTS;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.asierph.vibbaybooks.Settings;
import com.asierph.vibbaybooks.MainActivity;
import com.asierph.vibbaybooks.R;
import com.asierph.vibbaybooks.REST.PujaService;
import com.asierph.vibbaybooks.POJOS.Puja;
import com.asierph.vibbaybooks.ADAPTERS.AdapterPujas;

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


public class MisPujasFragment extends Fragment {

    private ListView listView;
    ArrayList<Puja> listaPujas;
    private View view;

    private Settings settings;

    public MisPujasFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bu = this.getArguments();
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("VibbayBooks");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_mispujas, container, false);
        listView = (ListView) view.findViewById(R.id.listaPujas);
        settings = new Settings(getActivity().getApplicationContext());
        return view;
    }

    public void onViewCreated (View view, Bundle savedInstanceState){

        listaPujas = new ArrayList<Puja>();
        final String URL = settings.getURL();

        //<editor-fold defaultstate="collapsed" desc="RETROFIT">
            // Tratamiento fechas.
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

            final PujaService service = retro.create(PujaService.class);

            // Llamada a todas las pujas.
            Call<List<Puja>> call = service.getTodasMisPujas(settings.getIdUsuario());
        //</editor-fold>
        call.enqueue(new Callback<List<Puja>>() {
            @Override
            public void onResponse(Call<List<Puja>> call, Response<List<Puja>> response) {

                List<Puja> lista = response.body();
                if (lista != null) {
                    // Llenamos la lista con nuestras pujas.
                    for (Puja puja : lista) {
                        listaPujas.add(puja);
                    }
                    if(listaPujas.size() == 0){
                        Puja puja = new Puja(0);
                        listaPujas.add(puja);
                    }
                    AdapterPujas adapter = new AdapterPujas(getActivity(), listaPujas);
                    listView.setAdapter(adapter);
                }
            }
            @Override
            public void onFailure(Call<List<Puja>> call, Throwable t) {}
        });

    }
}