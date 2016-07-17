package com.asierph.vibbaybooks.FRAGMENTS;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
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
import com.asierph.vibbaybooks.REST.ArticuloService;
import com.asierph.vibbaybooks.POJOS.Usuario;
import com.asierph.vibbaybooks.POJOS.Imagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SubirFragment extends Fragment {

    private View view;
    private TextView lblDenom;
    private EditText edtDenom;
    private TextView lblPrecio;
    private EditText edtPrecioSubir;
    private Button btnFotoSubir;
    private Button btnPublicar;
    private ImageView imgSubir;

    private Uri imagen;
    private boolean imagenSeleccionada = false;

    public SubirFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Subir artículo");
    }

    @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        // Cargar la vista
        view = inflater.inflate(R.layout.fragment_subir, container, false);

        lblDenom = (TextView) view.findViewById(R.id.lblDenom);
        edtDenom = (EditText) view.findViewById(R.id.edtDenom);
        lblPrecio = (TextView) view.findViewById(R.id.lblPrecio);
        edtPrecioSubir = (EditText) view.findViewById(R.id.edtPrecioSubir);
        btnFotoSubir = (Button) view.findViewById(R.id.btnFotoSubir);
        btnPublicar = (Button) view.findViewById(R.id.btnPublicar);
        imgSubir = (ImageView) view.findViewById(R.id.imgSubir);

        //Listener del botón para seleccionar imagen

        btnFotoSubir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Abrir galeria
                ImageView i;
                Intent intentGaleria = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intentGaleria, 1); //El 1 significa DESDE_GALERIA
            }
        });

        // Al pulsar el boton publicar, realizamos las siguientes comprobaciones.
        btnPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edtDenom.getText().toString().equals("")){
                    Toast t = Toast.makeText(getActivity(), "El artículo debe tener un titulo", Toast.LENGTH_LONG);
                    t.show();
                }else if(edtPrecioSubir.getText().toString().equals("")){
                    Toast t = Toast.makeText(getActivity(), "El artículo debe tener un precio minimo", Toast.LENGTH_LONG);
                    t.show();
                }else if(imagenSeleccionada == false){
                    Toast t = Toast.makeText(getActivity(), "No se puede publicar un artículo sin imagen", Toast.LENGTH_LONG);
                    t.show();
                }else{
                    subirArticulo();
                }
            }
        });

        return view;
    }

    public void subirArticulo(){
        final Settings settings = new Settings(getActivity().getApplicationContext());

        //<editor-fold defaultstate="collapsed" desc="RETROFIT">
        // Tratamiento fechas.
        GsonBuilder builder = new GsonBuilder();
        builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
            public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                return new Date(json.getAsJsonPrimitive().getAsLong());
            }
        });
        Gson gson = builder.create();

        Retrofit retro = new Retrofit.Builder()
                .baseUrl(settings.getURL())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        final ArticuloService service = retro.create(ArticuloService.class);

        // Codificar imagen.
        BitmapDrawable drawable = (BitmapDrawable) imgSubir.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        String imagenCodif = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);

        Imagen i = new Imagen(imagenCodif,edtDenom.getText().toString());
        Call<Imagen> call2 = service.subirImagen(i);
        //</editor-fold>
        call2.enqueue(new Callback<Imagen>() {
            @Override
            public void onResponse(Call<Imagen> call, Response<Imagen> response) {

                Imagen i = response.body();
                if (response.isSuccessful()) {

                    Usuario u = new Usuario(settings.getIdUsuario(), settings.getNombre());

                    Date d = new Date();
                    String importePunto = edtPrecioSubir.getText().toString().replace(',', '.');
                    Articulo a = new Articulo(1, edtDenom.getText().toString(), i.getNombre(), settings.formatPrecio(Float.parseFloat(importePunto)), d, "D", u);

                    // Hacemos la llamada post para publicar el articulo.
                    Call<String> call2 = service.postArticulo(a);
                    call2.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(Call<String> call, Response<String> response) {
                            if (response.isSuccessful()) {
                                Toast t = Toast.makeText(getActivity(), "¡Articulo subido!", Toast.LENGTH_LONG);
                                t.show();
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
                } else {
                    Toast t = Toast.makeText(getActivity(), "Error al subir la imagen", Toast.LENGTH_SHORT);
                    t.show();
                }
            }
            @Override
            public void onFailure(Call<Imagen> call, Throwable t) {}
        });
    }

    //Gestionar el resultado que se recibe de la galería
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == getActivity().RESULT_OK && data != null){
            imagen = data.getData();
            imgSubir.setImageURI(imagen);
            imagenSeleccionada = true; // Controlar que la imagen este subida.
        }
    }
    public void onViewCreated(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {}

}
