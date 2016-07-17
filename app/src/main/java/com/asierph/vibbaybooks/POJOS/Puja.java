package com.asierph.vibbaybooks.POJOS;

import java.util.Date;

public class Puja {
    private Integer idpuja;
    private String preciopuja;
    private Date fechapuja;
    private Usuario idusuariopujador;
    private Articulo idarticulo;

    public Puja(Integer idpuja, String preciopuja, Date fechapuja, Usuario idusuariopujador, Articulo idarticulo) {
        this.idpuja = idpuja;
        this.preciopuja = preciopuja;
        this.fechapuja = fechapuja;
        this.idusuariopujador = idusuariopujador;
        this.idarticulo = idarticulo;
    }

    //<editor-fold defaultstate="collapsed" desc="GETTERS & SETTERS">
    public Puja(Integer idpuja){
        this.idpuja = idpuja;
    }

    public Integer getIdpuja() {
        return idpuja;
    }

    public void setIdpuja(Integer idpuja) {
        this.idpuja = idpuja;
    }

    public String getPreciopuja() {
        return preciopuja;
    }

    public void setPreciopuja(String preciopuja) {
        this.preciopuja = preciopuja;
    }

    public Date getFechapuja() {
        return fechapuja;
    }

    public void setFechapuja(Date fechapuja) {
        this.fechapuja = fechapuja;
    }

    public Usuario getIdusuariopujador() {
        return idusuariopujador;
    }

    public void setIdusuariopujador(Usuario idusuariopujador) {
        this.idusuariopujador = idusuariopujador;
    }

    public Articulo getIdarticulo() {
        return idarticulo;
    }

    public void setIdarticulo(Articulo idarticulo) {
        this.idarticulo = idarticulo;
    }
    //</editor-fold>
}
