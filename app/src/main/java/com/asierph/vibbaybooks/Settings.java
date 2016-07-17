package com.asierph.vibbaybooks;

import android.content.Context;
import android.content.SharedPreferences;

public class Settings {
    //"http://10.0.3.2:8080/vibbaybooks/";
    // Path servicio web
    private static final String URL = "http://10.0.3.2:8087/vibbaybooks/webresources/";
    // Path imagenes
    private static final String URL_IMAGENES = "http://10.0.3.2:8087/vibbaybooks/IMG/";
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "DatosUsuario";
    public Settings(Context context) {
        this.context = context;
        // Creamos las preferencias con el nombre asignado anteriormente.
        preferences = this.context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public String getURL(){
        return URL;
    }
    public String getURL_IMAGENES(){
        return URL_IMAGENES;
    }
    public void Login(int idUsuario, String correo, String contrasena, String nombre){

        // Variable usuario logueado
        editor.putBoolean("logueado", true);

        // Almacenar datos usuario
        editor.putInt("idUsuario", idUsuario);
        editor.putString("email", correo);
        editor.putString("pass", contrasena);
        editor.putString("nombre", nombre);

        editor.commit();
    }
    // Metodo que comprueba si el usuario esta logueado.
    public boolean estaLogueado(){
        return preferences.getBoolean("logueado", false);
    }

    public String getNombre(){return preferences.getString("nombre", "");}

    public int getIdUsuario(){
        return preferences.getInt("idUsuario", 0);
    }

    public String getEmail(){
        return preferences.getString("email", "");
    }

    public int getDatoInt(String clave, int valorDefecto){return preferences.getInt(clave, valorDefecto);}

    public String getDatoString(String clave, String valorDefecto){return preferences.getString(clave, valorDefecto);}

    public void putDatoInt(String clave, int valor){
        editor.putInt(clave, valor);
        editor.commit();
    }

    public void putDatoString(String clave, String valor){
        editor.putString(clave, valor);
        editor.commit();
    }

    public String formatPrecio(float precio){
        //Formatear el precio con dos decimales
        String s = String.format("%.2f", precio);
        return s;
    }

    public void cerrarSesion(){
        // Eliminar datos usuario
        editor.clear();
        editor.commit();
    }

    // Metodo que convierte el estado.
    public static String estado(String pEstado){
        String result = "FalloEstado";
        switch ( pEstado ) {
            case "D":
                result = "Disponible";
                break;
            case "V":
                result = "Vendido";
                break;
        }
        return result;
    }
}
