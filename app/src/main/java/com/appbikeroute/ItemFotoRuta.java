package com.appbikeroute;

import java.io.File;

public class ItemFotoRuta {

    private byte[] fotoRuta;

    public ItemFotoRuta(byte[] fotosRuta) {

        this.fotoRuta = fotosRuta;
    }
    public byte[] getFotoRuta() {
        return fotoRuta;
    }

    public void setFotoRuta(byte[] fotoRuta) {
        this.fotoRuta = fotoRuta;
    }


}
