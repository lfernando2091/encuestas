package com.saganet.encuestas;

/**
 * Created by Oblinaty on 30/10/2016.
 */

public class tEncuestasEstatus {
    private int pKey;
    private String EstatusEncuesta;
    private int Version;

    public tEncuestasEstatus(){}

    public tEncuestasEstatus(int pk, String status, int v){
        this.pKey= pk; this.EstatusEncuesta= status; this.Version= v;
    }

    public int getpKey() {
        return pKey;
    }

    public String getEstatusEncuesta() {
        return EstatusEncuesta;
    }

    public int getVersion() {
        return Version;
    }
}
