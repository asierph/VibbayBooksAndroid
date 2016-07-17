package com.asierph.vibbaybooks.POJOS;

import com.google.gson.annotations.SerializedName;

public class Imagen {


    @SerializedName("imagenCodificada")
    private String imagenCodificada;
    @SerializedName("nombre")
    private String nombre;

    public Imagen(String imagenCodificada, String nombre) {
        this.imagenCodificada = imagenCodificada;
        this.nombre = nombre;
    }

    //<editor-fold defaultstate="collapsed" desc="GETTERS & SETTERS">
    public String getImagenCodificada() {
        return imagenCodificada;
    }

    public void setImagenCodificada(String imagenCodificada) {
        this.imagenCodificada = imagenCodificada;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    //</editor-fold>
}
