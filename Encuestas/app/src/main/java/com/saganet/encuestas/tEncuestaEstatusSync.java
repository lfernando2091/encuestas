package com.saganet.encuestas;

/**
 * Created by Personal on 16/11/2016.
 */

public class tEncuestaEstatusSync {
    private int pKey;
    private String EstatusEncuestaSync;

    public tEncuestaEstatusSync(){
    }
    public tEncuestaEstatusSync(int pk, String status){
        this.pKey= pk; this.EstatusEncuestaSync= status;
    }

    public int getpKey() {
        return pKey;
    }

    public String getEstatusEncuestaSync() {
        return EstatusEncuestaSync;
    }
}
