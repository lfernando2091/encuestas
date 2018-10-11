package com.saganet.encuestas;

/**
 * Created by Luis Fernando on 20/10/2016.
 */

public class tTipoPregunta {
    private  int pKey;
    private  String TipoPregunta;
    private  int Version;
    public tTipoPregunta(){}

    public tTipoPregunta(int key, String tp, int v) {
        this.pKey= key; this.TipoPregunta= tp; this.Version= v;
    }

    public int getpKey() {
        return pKey;
    }

    public String getTipoPregunta() {
        return TipoPregunta;
    }

    public int getVersion() {
        return Version;
    }
}
