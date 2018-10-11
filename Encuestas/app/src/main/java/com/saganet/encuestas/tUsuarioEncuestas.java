package com.saganet.encuestas;

/**
 * Created by Luis Fernando on 20/10/2016.
 */

public class tUsuarioEncuestas {
    private  int pKey;
    private  int idUsuario;
    private  int idEncuesta;
    private  String Version;

    public tUsuarioEncuestas(){}
    public  tUsuarioEncuestas(int key, int idus, int iden, String op) {
        this.pKey= key; this.idUsuario= idus; this.idEncuesta= iden; this.Version= op;
    }

    public int getpKey() {
        return pKey;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getIdEncuesta() { return idEncuesta; }

    public String getVersion() {
        return Version;
    }
}
