package com.saganet.encuestas;

import android.content.ContentValues;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Luis Fernando on 20/10/2016.
 */

public class tRespuestasEncuesta {
    private String pKey;
    private int idUsuario;
    private int idEncuesta;
    private String idContacto;
    private int idEncuestaEstado;
    private String Inicio;
    private String Fin;
    private String Latitud;
    private String Longitud;
    private String idEncuestaSync;
    private String folioSync;
    private ArrayList<String> pn;
   public  tRespuestasEncuesta(int idus, String idcont,
                               int idenc, int idencestado,
                               String in, String f,
                               String lat, String lon,String id_estado_sync,String folio_sync,
                               ArrayList<String> p) {
       this.pn= new ArrayList<String>();
       this.pKey=DataUpload.IS_REDITABLE_ENCUESTA_ID?DataUpload.ID_REDITABLE_ENCUESTA_ID:UUID.randomUUID().toString();
       this.idUsuario= idus;
       this.idContacto= idcont;
       this.idEncuesta= idenc;
       this.idEncuestaEstado= idencestado;
       this.Inicio= in;
       this.Fin= f;
       this.Latitud= lat;
       this.Longitud= lon;
       this.idEncuestaSync= id_estado_sync;
       this.folioSync= folio_sync;
       this.pn=p;
   }
    public  tRespuestasEncuesta(String pk,int idus, String idcont,
                                int idenc, int idencestado,
                                String in, String f,
                                String lat, String lon,String id_estado_sync,
                                ArrayList<String> p) {
        this.pn= new ArrayList<String>();
        this.pKey=pk;
        this.idUsuario= idus;
        this.idContacto= idcont;
        this.idEncuesta= idenc;
        this.idEncuestaEstado= idencestado;
        this.Inicio= in;
        this.Fin= f;
        this.Latitud= lat;
        this.Longitud= lon;
        this.idEncuestaSync= id_estado_sync;
        this.pn=p;
    }
    //Para el upload
    public  tRespuestasEncuesta(String pk,int idus, String idcont,
                                int idenc, int idencestado,
                                String in, String f,
                                String lat, String lon,
                                ArrayList<String> p) {
        this.pn= new ArrayList<String>();
        this.pKey=pk;
        this.idUsuario= idus;
        this.idContacto= idcont;
        this.idEncuesta= idenc;
        this.idEncuestaEstado= idencestado;
        this.Inicio= in;
        this.Fin= f;
        this.Latitud= lat;
        this.Longitud= lon;
        this.pn=p;
    }
    public  tRespuestasEncuesta() {
    }

    public ContentValues toContent(){
        ContentValues v= new ContentValues();
        v.put(DataUpload.COLUMN_PKEY, this.pKey);
        v.put(DataUpload.COLUMN_ID_USUARIO, this.idUsuario);
        v.put(DataUpload.COLUMN_ID_CONTACTO, this.idContacto);
        v.put(DataUpload.COLUMN_ID_ENCUESTA, this.idEncuesta);
        v.put(DataUpload.COLUMN_ID_ENCUESTA_ESTADO, this.idEncuestaEstado);
        v.put(DataUpload.COLUMN_ENCUESTA_INICIO, this.Inicio);
        v.put(DataUpload.COLUMN_ENCUESTA_FIN, this.Fin);
        v.put(DataUpload.COLUMN_ENCUESTA_LATITUD, this.Latitud);
        v.put(DataUpload.COLUMN_ENCUESTA_LONGITUD, this.Longitud);
        v.put(DataUpload.COLUMN_ID_ENCUESTA_ESTADO_SYNC, this.idEncuestaSync);
        v.put(DataUpload.COLUMN_FOLIO_SYNC, this.folioSync);
        for(int i=1;i<this.pn.size();i++){
            v.put("p_"+String.valueOf(i), this.pn.get(i));
            Log.v("Saved p_"+String.valueOf(i),this.pn.get(i));
        }
        return v;
    }
    public ContentValues toContentUpload(){
        ContentValues v= new ContentValues();
        v.put(DataUpload.COLUMN_PKEY, this.pKey);
        v.put(DataUpload.COLUMN_ID_USUARIO, this.idUsuario);
        v.put(DataUpload.COLUMN_ID_CONTACTO, this.idContacto);
        v.put(DataUpload.COLUMN_ID_ENCUESTA, this.idEncuesta);
        v.put(DataUpload.COLUMN_ID_ENCUESTA_ESTADO, this.idEncuestaEstado);
        v.put(DataUpload.COLUMN_ENCUESTA_INICIO, this.Inicio);
        v.put(DataUpload.COLUMN_ENCUESTA_FIN, this.Fin);
        v.put(DataUpload.COLUMN_ENCUESTA_LATITUD, this.Latitud);
        v.put(DataUpload.COLUMN_ENCUESTA_LONGITUD, this.Longitud);
        for(int i=1;i<this.pn.size();i++){
            v.put("p_"+String.valueOf(i), this.pn.get(i));
            //Log.v("Saved p_"+String.valueOf(i),this.pn.get(i));
        }
        return v;
    }
    public String getpKey() {
        return pKey;
    }

    public int getIdEncuesta() {
        return idEncuesta;
    }

    public String getIdContacto() { return idContacto; }

    public String getPN(int n) {return pn.get(n);}

    public String getInicio() {
        return Inicio;
    }

    public String getFin() {
        return Fin;
    }

    public String getLatitud() {
        return Latitud;
    }

    public String getLongitud() {
        return Longitud;
    }

    public String getFolioSync() {
        return folioSync;
    }
}
