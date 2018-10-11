package com.saganet.encuestas;

/**
 * Created by Luis Fernando on 20/10/2016.
 */

public class tRespuestas {
    private  int pKey;
    private  int idPregunta;
    private String Respuesta;
    private String AccionTipo;
    private int AccionValor;
    private int idEncuestaEstado;
    private int Version;
    public tRespuestas(){}

    public tRespuestas(int key, int idpreg, String res, String actipo, int acvalor, int idencestado, int v) {
        this.pKey= key; this.idPregunta= idpreg; this.Respuesta= res;
        this.AccionTipo= actipo; this.AccionValor= acvalor; this.idEncuestaEstado= idencestado;
        this.Version= v;
    }

    public int getpKey() {
        return pKey;
    }

    public int getIdPregunta() { return idPregunta; }

    public String getRespuesta() {
        return Respuesta;
    }

    public String getAccionTipo() {
        return AccionTipo;
    }

    public int getAccionValor() {
        return AccionValor;
    }

    public int getIdEncuestaEstado() {
        return idEncuestaEstado;
    }

    public int getVersion() {
        return Version;
    }
}
