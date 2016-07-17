package com.asierph.vibbaybooks;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.asierph.vibbaybooks.FRAGMENTS.*;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private Settings settings;

    // Metodo que cierra la sesion.
    public void cerrarSesion(FragmentManager fragmentManager, FragmentTransaction fragmentTransaction){
        settings.cerrarSesion();

        //Cambiar el menú.
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.login_drawer);
        //Cambiar el titulo
        TextView titulo = (TextView) findViewById(R.id.txtTitulo);
        titulo.setText("Invitado");
        //Cargar el fragmento de inicio
        InicioFragment inicioFG = new InicioFragment();
        fragmentTransaction.replace(R.id.contenedor_frag, inicioFG);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        getSupportActionBar().setTitle("Vibbay");

        navigationView.setNavigationItemSelectedListener(this);

        settings = new Settings(getApplicationContext());

        navigationView.getMenu().clear(); //Limpiar menu

        if(settings.estaLogueado()){
            navigationView.inflateMenu(R.menu.activity_main_drawer);
        }else{
            navigationView.inflateMenu(R.menu.login_drawer);
        }

        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;

        //Cargar el fragmento de inicio
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        InicioFragment inicioFG = new InicioFragment();

        fragmentTransaction.replace(R.id.contenedor_frag, inicioFG);

        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void onViewCreated (View view,
                               Bundle savedInstanceState){
        TextView titulo = (TextView) findViewById(R.id.txtTitulo);
        if(settings.estaLogueado()){
            //Poner en el titulo el email del usuario
            titulo.setText(settings.getEmail());
        }else{
            titulo.setText("Invitado");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (item.getItemId()) {
            case android.R.id.home:
                Log.i("ActionBar", "Atrás!");
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        FragmentManager fragmentManager;
        FragmentTransaction fragmentTransaction;
        // AL PULSAR EN UNA DE LAS OPCIONES, CONTROLAMOS A QUE FRAGMENT NOS TENEMOS QUE DIRIGIR;
        if(id == R.id.nav_inicio){

            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            InicioFragment inicioFG = new InicioFragment();
            fragmentTransaction.replace(R.id.contenedor_frag, inicioFG);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }else if (id == R.id.nav_articulos) {

            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            MisLibrosFragment misArtFG = new MisLibrosFragment();
            fragmentTransaction.replace(R.id.contenedor_frag, misArtFG);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_subir) {

            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            SubirFragment subirFG = new SubirFragment();

            fragmentTransaction.replace(R.id.contenedor_frag, subirFG);

            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_cerrar) {
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            cerrarSesion(fragmentManager, fragmentTransaction);

        }else if (id == R.id.nav_pujas) {

            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            MisPujasFragment mispFG = new MisPujasFragment();
            fragmentTransaction.replace(R.id.contenedor_frag, mispFG);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        }  else if (id == R.id.nav_iniciarSesion) {

            LoginFragment loginFG = new LoginFragment();
            fragmentManager = getSupportFragmentManager();
            fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.contenedor_frag, loginFG);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
