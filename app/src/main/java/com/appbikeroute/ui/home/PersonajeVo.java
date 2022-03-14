package com.appbikeroute.ui.home;

import java.io.File;

public class PersonajeVo {

    private String nombre;
    private String dist,alt,pend, des;
    private File foto;
    private int revBoton, descargando;

    public int getRevBoton() {
        return revBoton;
    }

    public void setRevBoton(int revBoton) {
        this.revBoton = revBoton;
    }

    public int getDescargando() {
        return descargando;
    }

    public void setDescargando(int descargando) {
        this.descargando = descargando;
    }

    public PersonajeVo(){

    }

    public String getDist() {
        return dist;
    }

    public void setDist(String dist) {
        this.dist = dist;
    }

    public String getAlt() {
        return alt;
    }

    public void setAlt(String alt) {
        this.alt = alt;
    }

    public String getPend() {
        return pend;
    }

    public void setPend(String pend) {
        this.pend = pend;
    }

    public String getDesn() {
        return des;
    }

    public void setDif(String des) {
        this.des = des;
    }

    public PersonajeVo(String nombre, String dist, String alt , String pend , String des, File foto, int revBoton, int descargando) {
        this.nombre = nombre;
        this.dist = dist;
        this.alt = alt;
        this.pend = pend;
        this.des = des;
        this.foto = foto;
        this.revBoton = revBoton;
        this.descargando = descargando;
        //this.color = color;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }


    public File getFoto() {
        return foto;
    }

    public void setFoto(File foto) {
        this.foto = foto;
    }
/*
    public int getColor() {
        return color;
    }

    public void setColor(int foto) {
        this.color = color;
    }

 */
}
