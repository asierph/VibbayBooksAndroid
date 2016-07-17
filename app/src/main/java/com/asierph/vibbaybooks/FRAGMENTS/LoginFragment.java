package com.asierph.vibbaybooks.FRAGMENTS;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.asierph.vibbaybooks.Settings;
import com.asierph.vibbaybooks.R;
import com.asierph.vibbaybooks.POJOS.Usuario;
import com.asierph.vibbaybooks.REST.UsuarioService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginFragment extends Fragment {

    private View view;
    private Button btnLogin;
    private EditText edtUser;
    private EditText edtPass;
    private String user;
    private String pass;


    public LoginFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_login, container, false);
        btnLogin = (Button) view.findViewById(R.id.btnLogin);
        edtUser = (EditText) view.findViewById(R.id.edtUser);
        edtPass = (EditText) view.findViewById(R.id.edtPass);

        return view;
    }

    public void onViewCreated (View view,
                               Bundle savedInstanceState){

        final Settings settings = new Settings(getActivity().getApplicationContext());
        final String URL = settings.getURL();

        // Al pulsar en Entrar se loguea en la aplicacion
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Capturamos los datos del formulario.
                user = edtUser.getText().toString();
                pass = edtPass.getText().toString();
                // Comprobamos que no sean nulos y de serlo, mandamos un mensaje.
                if(user.equals("")){
                    Toast t = Toast.makeText(getActivity(), "Introduzca un email", Toast.LENGTH_LONG);
                    t.show();
                }else if(pass.equals("")){
                    Toast t = Toast.makeText(getActivity(), "Introduzca una password", Toast.LENGTH_LONG);
                    t.show();
                }
                //<editor-fold defaultstate="collapsed" desc="RETROFIT">
                // Tratamiento de fechas
                    GsonBuilder builder = new GsonBuilder();
                    builder.setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                    builder.registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
                        public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
                            return new Date(json.getAsJsonPrimitive().getAsLong());
                        }
                    });
                    Gson gson = builder.create();

                    Retrofit retro = new Retrofit.Builder()
                            .baseUrl(URL)
                            .addConverterFactory(GsonConverterFactory.create(gson))
                            .build();

                    final UsuarioService service = retro.create(UsuarioService.class);

                    // Comprobamos en els ervidor que la combancion de user/pass exista.
                    Call<Usuario> call = service.getUsuarioPorEmailPass(user, pass);
                //</editor-fold>
                call.enqueue(new Callback<Usuario>() {
                    @Override
                    public void onResponse(Call<Usuario> call, Response<Usuario> response) {

                        Usuario usu = response.body();
                        // Si el usuario realmente existe con esa password.
                        if(usu != null){
                            // Guardar las SharedPreferences para mentener la sesion.
                            settings.Login(usu.getIdusuario(), usu.getEmail(), usu.getPassword(), usu.getNombre());
                            //Cambiar el menu
                            NavigationView navigationView = (NavigationView) getActivity().findViewById(R.id.nav_view);
                            navigationView.getMenu().clear(); //Limpiar menu
                            navigationView.inflateMenu(R.menu.activity_main_drawer);
                            // Poner en el titulo el nombre del usuario
                            TextView titulo = (TextView) getActivity().findViewById(R.id.txtTitulo);
                            titulo.setText(usu.getNombre());
                            // Cargar el fragmento de inicio
                            FragmentManager fragmentManager;
                            FragmentTransaction fragmentTransaction;
                            fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentTransaction = fragmentManager.beginTransaction();
                            InicioFragment inicioFG = new InicioFragment();
                            fragmentTransaction.replace(R.id.contenedor_frag, inicioFG);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();

                            // Metodo para oculpar el teclado.
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edtPass.getWindowToken(), 0);

                        } else{
                            Toast t = Toast.makeText(getActivity(), "Datos erroneos", Toast.LENGTH_LONG);
                            t.show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Usuario> call, Throwable t) {}
                });
            }
        });
    }
}