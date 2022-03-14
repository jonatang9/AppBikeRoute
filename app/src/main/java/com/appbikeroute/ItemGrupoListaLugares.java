package com.appbikeroute;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ItemGrupoListaLugares {

    private String descrip;
    private String titulo;
    private ArrayList<ItemFotoRuta> recyclerListfotos;
    //RecyclerView RecyclerFotosLugares;
    //private int color;

    public ItemGrupoListaLugares(String titulo,String descrip,/*, int fotos,*/ArrayList<ItemFotoRuta> recyclerListfotos) {
        this.descrip = descrip;
        this.titulo = titulo;
        //this.fotos = fotos;
        this.recyclerListfotos = recyclerListfotos;
        //this.color = color;

    }

    public ArrayList<ItemFotoRuta> getRecyclerListfotos() {
        return recyclerListfotos;
    }

    public void setRecyclerListfotos(ArrayList<ItemFotoRuta> recyclerListfotos) {
        this.recyclerListfotos = recyclerListfotos;
    }

    public String getDescrip() {
        return descrip;
    }
    public String getTitulo() {
        return titulo;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }
/*
    public int getFotos() {
        return fotos;
    }

    public void setFotos(int foto) {
        this.fotos = fotos;
    }
*/
}
