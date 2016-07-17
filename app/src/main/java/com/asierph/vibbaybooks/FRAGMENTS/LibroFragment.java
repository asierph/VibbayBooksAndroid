package com.asierph.vibbaybooks.FRAGMENTS;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.asierph.vibbaybooks.POJOS.Articulo;
import com.asierph.vibbaybooks.Settings;
import com.asierph.vibbaybooks.MainActivity;
import com.asierph.vibbaybooks.R;
import com.asierph.vibbaybooks.DecodificarImagen;
import com.asierph.vibbaybooks.REST.ArticuloService;
import com.asierph.vibbaybooks.REST.PujaService;
import com.asierph.vibbaybooks.POJOS.Usuario;
import com.asierph.vibbaybooks.POJOS.Puja;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LibroFragment extends Fragment {

    private Articulo libro;
    int idArticulo = 0;
    int estadoArticulo = 0;
    float importe = 0;

    private TextView txtEstado;
    private TextView txtDenominacion;
    private TextView txtPrecio;
    private TextView txtFecha;
    private Button btnPujar;
    private ImageView imgArticulo;
    private EditText edtPuja;

    private Settings settings;
    private String URL;

    public LibroFragment() {}

    private void puedePujar(int idArticulo){
        //<editor-fold defaultstate="collapsed" desc="RETROFIT">
            // Tratamiento de fechas
            GsonBuilder builder = new GsonBuilder();
            builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                public Date deserialize(JsonElement json, Type typeOfT,JsonDeserializationContext context) throws JsonParseException {return new Date(json.getAsJsonPrimitive().getAsLong());}
            });
            Gson gson = builder.create();

            Retrofit retro = new Retrofit.Builder()
                    .baseUrl(URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            final PujaService service = retro.create(PujaService.class);

            // Realizamos la llamada a puedePujar, que determina si el usuario ya realizo alguna puja sobre el articulo.
            Call<JsonElement> call = service.puedePujar(idArticulo, settings.getIdUsuario());
        //</editor-fold>
        call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                JsonElement jsonInput = response.body();
                int result = jsonInput.getAsJsonObject().get("puedePujar").getAsInt();
                // Controlador de si es posible pujar.
                if(result == 1){
                    realizarPuja(libro, importe);
                }else{
                    Toast t = Toast.makeText(getActivity(), "Ya has pujado por este articulo", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {}
        });
    }
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
        View v = inflater.inflate(R.layout.fragment_libro, container, false);

        settings = new Settings(getActivity().getApplicationContext());
        idArticulo = settings.getDatoInt("idArticulo", 0);
        estadoArticulo = settings.getDatoInt("estadoArticulo", 0);

        imgArticulo = (ImageView) v.findViewById(R.id.imgArticulo);
        txtEstado = (TextView) v.findViewById(R.id.txtEstado);
        txtDenominacion = (TextView) v.findViewById(R.id.txtDenom);
        txtPrecio = (TextView) v.findViewById(R.id.txtPrecio);
        txtFecha = (TextView) v.findViewById(R.id.txtFecha);
        btnPujar = (Button) v.findViewById(R.id.btnPujar);
        edtPuja = (EditText) v.findViewById(R.id.edtPuja);

        if(estadoArticulo != 0){
            edtPuja.setHint("¡Libro vendido!");
            edtPuja.setEnabled(false);
            btnPujar.setEnabled(false);
        }

        txtEstado.setText("");

        btnPujar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Controlador de la puja. Al pulsar el boton pujar se realizan las siguientes comprobaciones.
                String mensajeResultado = "";
                if (settings.estaLogueado()) {
                    // Si el articulo es de uno mismo
                    if (libro.getIdvendedor().getIdusuario() == settings.getIdUsuario()) {
                        mensajeResultado = "¡Especular con tus articulos no esta bien!";
                    } else {
                        boolean importeValido = true;
                        try{ // Formateamos correctamente el importe.
                            String importePunto = edtPuja.getText().toString().replace(',', '.');
                            importe = Float.parseFloat(importePunto);
                        }catch(NumberFormatException nfe){
                            importeValido = false;
                        }
                        // Si el importe es correcto
                        if(importeValido){
                            String str = libro.getPrecio().replace(',', '.');
                            // Comprobamos que la puja supere el precio minimo.
                            if(importe < Float.parseFloat(str)){
                                mensajeResultado = "La puja minima es de: " + libro.getPrecio() + "€";
                            }else{
                                // Compruebo si se puede pujar y si se puede se puja
                                puedePujar(libro.getIdarticulo());
                            }
                        }else{
                            mensajeResultado = "Introduce un importe válido";
                        }
                    }
                } else {
                    mensajeResultado = "Identificate antes de pujar";
                }
                if(!mensajeResultado.equals("")){
                    // Imprimimos el resultado.
                    Toast to = Toast.makeText(getActivity(), mensajeResultado, Toast.LENGTH_SHORT);
                    to.show();
                }
            }
        });
        URL = settings.getURL();
        //<editor-fold defaultstate="collapsed" desc="RETROFIT">
            // Formateo de fechas.
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

            Call<Articulo> call = service.getArticulo(idArticulo);
        //</editor-fold>
        call.enqueue(new Callback<Articulo>() {
            @Override
            public void onResponse(Call<Articulo> call, Response<Articulo> response) {

                if(response.isSuccessful()){
                    libro = response.body();
                    txtEstado.setText(Settings.estado(libro.getEstado()));
                    txtDenominacion.setText(libro.getDenominacion());
                    String url = settings.getURL_IMAGENES() + libro.getImagen() + ".jpg";
                    new DecodificarImagen(imgArticulo).execute(url);
                    txtPrecio.setText("Precio inicial: " + libro.getPrecio() + "€");
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    String textoFecha = df.format(libro.getFechapubli());
                    txtFecha.setText("Fecha publicación: " + textoFecha);
                }
            }
            @Override
            public void onFailure(Call<Articulo> call, Throwable t) {}
        });
        return v;
    }

    public void onViewCreated(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
    }

    // Metodo que hace un post de la PUJA.
    private void realizarPuja(Articulo a, float importePuja){
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

            final PujaService service = retro.create(PujaService.class);
            Usuario u = new Usuario(settings.getIdUsuario(),settings.getNombre());
            Date d = new Date();
            Puja p = new Puja(1,settings.formatPrecio(importePuja),d,u,a);

            Call<String> call = service.postPuja(p);
        //</editor-fold>
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                if(response.isSuccessful()){
                    Toast to = Toast.makeText(getActivity(), "¡Puja realizada!", Toast.LENGTH_LONG);
                    to.show();
                    //Cargar el fragmento de inicio
                    FragmentManager fragmentManager;
                    FragmentTransaction fragmentTransaction;
                    fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    InicioFragment inicioFG = new InicioFragment();

                    fragmentTransaction.replace(R.id.contenedor_frag, inicioFG);

                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {}
        });
    }


}
