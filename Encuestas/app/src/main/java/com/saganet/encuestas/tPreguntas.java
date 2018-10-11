package com.saganet.encuestas;

import android.database.Cursor;

/**
 * Created by Luis Fernando on 20/10/2016.
 */

public class tPreguntas {
    private  int pKey;
    private  int idTipoPregunta;
    private  int idEncuesta;
    private  String Pregunta;
    private  int Version;
    public  tPreguntas(){}
    public tPreguntas(int key, int idtpreg,int iden, String pregunta, int v) {
            this.pKey= key;
            this.idTipoPregunta= idtpreg;
            this.idEncuesta= iden;
            this.Pregunta= pregunta;
            this.Version= v;
    }
    public tPreguntas(Cursor c) {
        this.pKey= c.getInt(c.getColumnIndex(DataUpload.COLUMN_PKEY));
        this.idTipoPregunta= c.getInt(c.getColumnIndex(DataUpload.COLUMN_ID_TIPO_PREGUNTA));
        this.idEncuesta= c.getInt(c.getColumnIndex(DataUpload.COLUMN_ID_ENCUESTA));
        this.Pregunta= c.getString(c.getColumnIndex(DataUpload.COLUMN_PREGUNTA));
    }

    public int getpKey() {
        return pKey;
    }

    public int getIdTipoPregunta() {
        return idTipoPregunta;
    }

    public int getIdEncuesta() {
        return idEncuesta;
    }

    public String getPregunta() {
        return Pregunta;
    }

    public int getVersion() {
        return Version;
    }
}
