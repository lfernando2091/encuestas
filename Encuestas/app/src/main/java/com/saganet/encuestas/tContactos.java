package com.saganet.encuestas;

import android.content.ContentValues;

import java.util.UUID;

/**
 * Created by Luis Fernando on 20/10/2016.
 */

public class tContactos {
    private String pKey;
    private int id_Precarga;
    private int id_Encuesta;
    private String Nombre;
    private String Paterno;
    private String Materno;
    private String Calle;
    private String Colonia;
    private String numero_Ext;
    private String numero_Int;
    private int codigo_Postal;
    private String Entidad;
    private String Minicipio;
    private String fecha_Nacimiento;
    private String Genero;
    private int Version;
    private  int idus;
    public tContactos()
    {}
    public tContactos(int _id_Precarga,
                      int _id_Encuesta,
                      int _id_us,
                      String _nom,
                      String _pat,
                      String _mat,
                      String _calle,
                      String _col,
                      String _next,
                      String _nint,
                      int _codp,
                      String _entidad,
                      String _mun,
                      String _f_nac,
                      String _gen) {
        this.pKey=_id_Precarga==0?  DataUpload.USER_ID_CONTACT:""; DataUpload.USER_NEW_ACCOUNT_ID=this.pKey;
        this.id_Precarga= _id_Precarga; this.id_Encuesta=_id_Encuesta; this.idus= _id_us; this.Nombre=_nom; this.Paterno= _pat;
        this.Materno= _mat; this.Calle=_calle; this.Colonia= _col; this.numero_Ext= _next; this.numero_Int= _nint;
        this.codigo_Postal= _codp; this.Entidad= _entidad; this.Minicipio= _mun; this.fecha_Nacimiento= _f_nac; this.Genero= _gen;
        this.Version=Integer.parseInt(DataUpload.USER_ACCOUNT_VERSION);
    }
    public tContactos(String kp,
                      int _id_Precarga,
                      int _id_Encuesta,
                      int _id_us,
                      String _nom,
                      String _pat,
                      String _mat,
                      String _calle,
                      String _col,
                      String _next,
                      String _nint,
                      int _codp,
                      String _entidad,
                      String _mun,
                      String _f_nac,
                      String _gen,
                      int v) {
        this.pKey=kp; this.id_Precarga= _id_Precarga; this.id_Encuesta=_id_Encuesta; this.Nombre=_nom; this.Paterno= _pat;
        this.Materno= _mat; this.Calle=_calle; this.Colonia= _col; this.numero_Ext= _next; this.numero_Int= _nint;
        this.codigo_Postal= _codp; this.Entidad= _entidad; this.Minicipio= _mun; this.fecha_Nacimiento= _f_nac; this.Genero= _gen;
        this.Version= v; this.idus= _id_us;
    }
    //----Respaldo contacto envio a server, con la información del contacto nuevo agregado
    public tContactos(String kp,
                      int _id_Encuesta,
                      int _id_us,
                      String _nom,
                      String _pat,
                      String _mat,
                      String _calle,
                      String _col,
                      String _next,
                      String _nint,
                      int _codp,
                      String _entidad,
                      String _mun,
                      String _f_nac,
                      String _gen,
                      int v) {
        this.pKey=kp; this.id_Encuesta=_id_Encuesta; this.Nombre=_nom; this.Paterno= _pat;
        this.Materno= _mat; this.Calle=_calle; this.Colonia= _col; this.numero_Ext= _next; this.numero_Int= _nint;
        this.codigo_Postal= _codp; this.Entidad= _entidad; this.Minicipio= _mun; this.fecha_Nacimiento= _f_nac; this.Genero= _gen;
        this.Version= v; this.idus= _id_us;
    }
    public ContentValues toContent(){
        ContentValues v= new ContentValues();
        v.put(DataUpload._ID, this.pKey);
        v.put(DataUpload.COLUMN_PKEY, this.pKey);
        v.put(DataUpload.COLUMN_ID_PRECARGA, this.id_Precarga);
        v.put(DataUpload.COLUMN_ID_ENCUESTA, this.id_Encuesta);
        v.put(DataUpload.COLUMN_ID_USUARIO, this.idus);
        v.put(DataUpload.COLUMN_NOMBRE, this.Nombre);
        v.put(DataUpload.COLUMN_PATERNO, this.Paterno);
        v.put(DataUpload.COLUMN_MATERNO, this.Materno);
        v.put(DataUpload.COLUMN_CALLE, this.Calle);
        v.put(DataUpload.COLUMN_COLONIA, this.Colonia);
        v.put(DataUpload.COLUMN_NUM_EXT, this.numero_Ext);
        v.put(DataUpload.COLUMN_NUM_INT, this.numero_Int);
        v.put(DataUpload.COLUMN_COD_POSTAL, this.codigo_Postal);
        v.put(DataUpload.COLUMN_ENTIDAD, this.Entidad);
        v.put(DataUpload.COLUMN_MUNICIPIO, this.Minicipio);
        v.put(DataUpload.COLUMN_FECHA_NAC, this.fecha_Nacimiento);
        v.put(DataUpload.COLUMN_GENERO, this.Genero);
        v.put(DataUpload.COLUMN_VERSION, this.Version);
        return v;
    }
    public ContentValues toContentUpload(){
        ContentValues v= new ContentValues();
        v.put(DataUpload.COLUMN_PKEY, this.pKey);
        v.put(DataUpload.COLUMN_ID_ENCUESTA, this.id_Encuesta);
        v.put(DataUpload.COLUMN_ID_USUARIO, this.idus);
        v.put(DataUpload.COLUMN_NOMBRE, this.Nombre);
        v.put(DataUpload.COLUMN_PATERNO, this.Paterno);
        v.put(DataUpload.COLUMN_MATERNO, this.Materno);
        v.put(DataUpload.COLUMN_CALLE, this.Calle);
        v.put(DataUpload.COLUMN_COLONIA, this.Colonia);
        v.put(DataUpload.COLUMN_NUM_EXT, this.numero_Ext);
        v.put(DataUpload.COLUMN_NUM_INT, this.numero_Int);
        v.put(DataUpload.COLUMN_COD_POSTAL, this.codigo_Postal);
        v.put(DataUpload.COLUMN_ENTIDAD, this.Entidad);
        v.put(DataUpload.COLUMN_MUNICIPIO, this.Minicipio);
        v.put(DataUpload.COLUMN_FECHA_NAC, this.fecha_Nacimiento);
        v.put(DataUpload.COLUMN_GENERO, this.Genero);
        v.put(DataUpload.COLUMN_VERSION, this.Version);
        return v;
    }
    public String getpKey() {
        return  this.pKey;
    }
    public int getId_Precarga() {
        return id_Precarga;
    }
    public int getId_Encuesta() {
        return id_Encuesta;
    }

    public int getIdus() {
        return idus;
    }

    public String getNombre() {
        return Nombre;
    }
    public String getPaterno() {
        return Paterno;
    }
    public String getMaterno() {
        return Materno;
    }
    public String getCalle() {
        return Calle;
    }
    public String getColonia() {
        return Colonia;
    }
    public String getNumero_Ext() {
        return numero_Ext;
    }
    public String getNumero_Int() {
        return numero_Int;
    }
    public int getCodigo_Postal() {
        return codigo_Postal;
    }
    public String getEntidad() {
        return Entidad;
    }
    public String getMinicipio() {
        return Minicipio;
    }
    public String getFecha_Nacimiento() {
        return fecha_Nacimiento;
    }
    public String getGenero() {
        return Genero;
    }

    public int getVersion() {
        return Version;
    }
}
