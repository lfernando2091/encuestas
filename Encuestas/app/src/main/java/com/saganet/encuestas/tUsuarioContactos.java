package com.saganet.encuestas;

import android.content.ContentValues;

import java.util.UUID;

/**
 * Created by Hazael on 20/10/2016.
 */

public class tUsuarioContactos {
    //In moment I need to conver to string values to save into the db 'cause pKey is a Text Field
    ///////////////////////////
    private static  int pkey;
    private  int idUsuario;
    private  int idContacto;
    private  int idEncuesta;
    private  int idEncuestaEstado;
    private  int version;
    public  tUsuarioContactos(){}
    public  tUsuarioContactos(int p,int idus, int idcon, int idenc,int idencestado, int v)
    {
        this.pkey= p;
        this.idUsuario= idus;
        this.idContacto= idcon;
        this.idEncuesta= idenc;
        this.idEncuestaEstado= idencestado;
        this.version= v;
    }

    public static ContentValues toContent(){
        ContentValues c= new ContentValues();
        //c.put(DataUpload._ID, DataUpload.USER_ID_CONTACT);
        c.put(DataUpload.COLUMN_PKEY + "_UC", DataUpload.USER_ID_CONTACT);
        c.put(DataUpload.COLUMN_ID_USUARIO, DataUpload.USER_ID);
        c.put(DataUpload.COLUMN_ID_CONTACTO,DataUpload.USER_NEW_ACCOUNT_ID);
        c.put(DataUpload.COLUMN_ID_ENCUESTA,DataUpload.USER_NEW_ACCOUNT_ENC_ID);
        c.put(DataUpload.COLUMN_ID_ENCUESTA_ESTADO,"0");
        c.put(DataUpload.COLUMN_VERSION,DataUpload.USER_ACCOUNT_VERSION);
        return c;
    }

    public static int getPkey() {
        return pkey;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getIdContacto() {
        return idContacto;
    }

    public int getIdEncuesta() {
        return idEncuesta;
    }

    public int getIdEncuestaEstado() {
        return idEncuestaEstado;
    }

    public int getVersion() {
        return version;
    }
}
