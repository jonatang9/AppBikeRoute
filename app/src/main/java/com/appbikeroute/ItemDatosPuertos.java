package com.appbikeroute;

public class ItemDatosPuertos {

    private String numero,altura,pendiente,desnivel;

    public ItemDatosPuertos(String numero,String altura,String pendiente,String desnivel) {

        this.numero = numero;
        this.altura = altura;
        this.pendiente = pendiente;
        this.desnivel = desnivel;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getAltura() {
        return altura;
    }

    public void setAltura(String altura) {
        this.altura = altura;
    }

    public String getPendiente() {
        return pendiente;
    }

    public void setPendiente(String pendiente) {
        this.pendiente = pendiente;
    }

    public String getDesnivel() {
        return desnivel;
    }

    public void setDesnivel(String desnivel) {
        this.desnivel = desnivel;
    }

}
