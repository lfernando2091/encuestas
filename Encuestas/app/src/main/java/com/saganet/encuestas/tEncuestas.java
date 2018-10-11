package com.saganet.encuestas;

/**
 * Created by Luis Fernando on 20/10/2016.
 */

public class tEncuestas {
    private int pKey;
    private  String Encuesta;
    private  int Version;
    private String Caducidad;
    public  tEncuestas() {    }
    public  tEncuestas(int _key, String encuesta, int v, String cad) {
        this.pKey= _key; this.Encuesta= encuesta; this.Version= v;
        this.Caducidad= cad;
    }
    public int getpKey() {
        return pKey;
    }
    public String getEncuesta() { return Encuesta; }

    public int getVersion() {
        return Version;
    }

    public String getCaducidad() {
        return Caducidad;
    }
}
