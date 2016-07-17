package com.asierph.vibbaybooks.POJOS;

import com.google.gson.annotations.SerializedName;

import java.util.Date;


public class Articulo {

    @SerializedName("idarticulo")
    private Integer idarticulo;

    @SerializedName("denominacion")
    private String denominacion;

    @SerializedName("imagen")
    private String imagen;

    @SerializedName("precio")
    private String precio;

    @SerializedName("fechapubli")
    private Date fechapubli;

    @SerializedName("estado")
    private String estado;

    @SerializedName("idvendedor")
    private Usuario idvendedor;


    public Articulo(Integer idarticulo, String denominacion, String imagen, String precio, Date fechapubli, String estado, Usuario u) {
        this.idarticulo = idarticulo;
        this.denominacion = denominacion;
        this.imagen = imagen;
        this.precio = precio;
        this.fechapubli = fechapubli;
        this.estado = estado;
        this.idvendedor = u;
    }

    public Articulo(Integer idarticulo, String denominacion, String estado){
        this.idarticulo = idarticulo;
        this.denominacion = denominacion;
        this.estado = estado;
    }

    public Articulo(Integer idarticulo){
        this.idarticulo = idarticulo;
    }

    public Articulo(){

    }

    //<editor-fold defaultstate="collapsed" desc="GETTERS & SETTERS">
    public Integer getIdarticulo() {
        return idarticulo;
    }

    public String getEstado() {
        return estado;
    }

    public Date getFechapubli() {
        return fechapubli;
    }

    public String getPrecio() {
        return precio;
    }

    public String getImagen() {
        return imagen;
    }

    public String getDenominacion() {
        return denominacion;
    }

    public void setIdarticulo(Integer idarticulo) {
        this.idarticulo = idarticulo;
    }

    public void setDenominacion(String denominacion) {
        this.denominacion = denominacion;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public void setFechapubli(Date fechapubli) {
        this.fechapubli = fechapubli;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Usuario getIdvendedor() {
        return idvendedor;
    }

    public void setIdvendedor(Usuario idvendedor) {
        this.idvendedor = idvendedor;
    }
    //</editor-fold>
}
