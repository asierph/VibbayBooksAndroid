package com.asierph.vibbaybooks.POJOS;

public class Usuario {
    private Integer idusuario;
    private String email;
    private String password;
    private String movil;
    private String nombre;

    public Usuario(Integer idusuario, String email, String password, String movil, String nombre) {
        this.idusuario = idusuario;
        this.email = email;
        this.password = password;
        this.movil = movil;
        this.nombre = nombre;
    }

    public Usuario(Integer idusuario, String nombre){
        this.idusuario = idusuario;
        this.nombre = nombre;
        this.email = "";
        this.password = "";
        this.movil = "";
    }

    //<editor-fold defaultstate="collapsed" desc="GETTERS & SETTERS">
    public Integer getIdusuario() {
        return idusuario;
    }

    public void setIdusuario(Integer idusuario) {
        this.idusuario = idusuario;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMovil() {
        return movil;
    }

    public void setMovil(String movil) {
        this.movil = movil;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    //</editor-fold>
}
