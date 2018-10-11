package com.saganet.encuestas;

import android.content.ContentValues;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteException;
import net.sqlcipher.database.SQLiteOpenHelper;
import net.sqlcipher.Cursor;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Luis Fernando on 20/10/2016.
 */

public class DataSesion extends SQLiteOpenHelper {

    private static DataSesion instance;
    public static synchronized DataSesion getHelper(Context context) {
        if (instance == null)
            instance = new DataSesion(context);
        return instance;
    }
    Context c=null;
    public DataSesion(Context context) {
        super(context, DataUpload.DATA_BASE_NAME, null, DataUpload.DATA_BASE_VERSION);
        c=context;
        SQLiteDatabase.loadLibs(context);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        CREATE_TABLES_TODB(sqLiteDatabase);
        SaveDefaultData(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    SQLiteDatabase getReadableDatabase() {
        return(super.getReadableDatabase(DataUpload.DATA_BASE_SECURITY));
    }

    SQLiteDatabase getWritableDatabase() {
        return(super.getWritableDatabase(DataUpload.DATA_BASE_SECURITY));
    }

    private  void CREATE_TABLES_TODB(SQLiteDatabase db)
    {
        String CREATE_TABLE_CONTACTOS =
                "CREATE TABLE IF NOT EXISTS " + DataUpload.TABLE_CONTACTOS +
                        "(" +
                        DataUpload._ID + " TEXT PRIMARY KEY," +
                        DataUpload.COLUMN_PKEY + " TEXT NOT NULL," +
                        DataUpload.COLUMN_ID_PRECARGA + " INTEGER," +
                        DataUpload.COLUMN_ID_ENCUESTA + " INTEGER," +
                        DataUpload.COLUMN_ID_USUARIO + " INTEGER NOT NULL," +
                        DataUpload.COLUMN_NOMBRE + " TEXT NOT NULL," +
                        DataUpload.COLUMN_PATERNO + " TEXT NOT NULL," +
                        DataUpload.COLUMN_MATERNO + " TEXT NOT NULL," +
                        DataUpload.COLUMN_CALLE + " TEXT NOT NULL," +
                        DataUpload.COLUMN_COLONIA + " TEXT NOT NULL," +
                        DataUpload.COLUMN_NUM_EXT + " TEXT," +
                        DataUpload.COLUMN_NUM_INT + " TEXT," +
                        DataUpload.COLUMN_COD_POSTAL + " INTEGER," +
                        DataUpload.COLUMN_ENTIDAD + " TEXT NOT NULL," +
                        DataUpload.COLUMN_MUNICIPIO + " TEXT NOT NULL," +
                        DataUpload.COLUMN_FECHA_NAC + " TEXT NOT NULL," +
                        DataUpload.COLUMN_GENERO + " TEXT NOT NULL," +
                        DataUpload.COLUMN_VERSION + " TEXT)" ;
        String CREATE_TABLE_ENCUESTAS =
                "CREATE TABLE IF NOT EXISTS " + DataUpload.TABLE_ENCUESTAS +
                        "(" +
                        DataUpload._ID + " INTEGER PRIMARY KEY," +
                        DataUpload.COLUMN_PKEY + " INTEGER NOT NULL, " +
                        DataUpload.COLUMN_ENCUESTA + " TEXT NOT NULL, " +
                        DataUpload.COLUMN_VERSION + " INTEGER NOT NULL, " +
                        DataUpload.COLUMN_CADUCIDAD + " TEXT NOT NULL)" ;
        String CREATE_TABLE_ENCUESTAS_ESTADO =
                "CREATE TABLE IF NOT EXISTS " + DataUpload.TABLE_ENCUESTAS_ESTADO +
                        "(" +
                        DataUpload._ID + " INTEGER PRIMARY KEY," +
                        DataUpload.COLUMN_PKEY + " INTEGER NOT NULL, " +
                        DataUpload.COLUMN_ENCUESTA_ESTADO + " TEXT NOT NULL, " +
                        DataUpload.COLUMN_VERSION + " INTEGER NOT NULL)" ;
        String CREATE_TABLE_ENCUESTAS_ESTADO_SYNC =
                "CREATE TABLE IF NOT EXISTS " + DataUpload.TABLE_ENCUESTAS_ESTADO_SYNC +
                        "(" +
                        DataUpload.COLUMN_PKEY + " INTEGER NOT NULL, " +
                        DataUpload.COLUMN_ENCUESTA_ESTADO_SYNC + " TEXT NOT NULL)";
        String CREATE_TABLE_USUARIOS =
                "CREATE TABLE IF NOT EXISTS " + DataUpload.TABLE_USUARIOS +
                        "(" +
                        DataUpload.COLUMN_PKEY + " INTEGER PRIMARY KEY, " +
                        DataUpload.COLUMN_NICK + " TEXT NOT NULL," +
                        DataUpload.COLUMN_PASSWORD + " TEXT NOT NULL," +
                        DataUpload.COLUMN_NOMBRE + " TEXT NOT NULL," +
                        DataUpload.COLUMN_PATERNO + " TEXT NOT NULL," +
                        DataUpload.COLUMN_MATERNO + " TEXT NOT NULL," +
                        DataUpload.COLUMN_VERSION + " INTEGER NOT NULL)" ;
        String CREATE_TABLE_USUARIOS_CONTACTOS =
                "CREATE TABLE IF NOT EXISTS " + DataUpload.TABLE_USUARIOS_CONTACTOS +
                        "(" +
                        DataUpload.COLUMN_PKEY + "_UC TEXT PRIMARY KEY," +
                        DataUpload.COLUMN_ID_USUARIO + " INTEGER NOT NULL," +
                        DataUpload.COLUMN_ID_CONTACTO + " TEXT NOT NULL," +
                        DataUpload.COLUMN_ID_ENCUESTA + " INTEGER NOT NULL," +
                        DataUpload.COLUMN_ID_ENCUESTA_ESTADO + " INTEGER," +
                        DataUpload.COLUMN_VERSION + " INTEGER)" ;
        String CREATE_TABLE_USUARIOS_ENCUESTAS =
                "CREATE TABLE IF NOT EXISTS " + DataUpload.TABLE_USUARIOS_ENCUESTAS +
                        "(" + DataUpload.COLUMN_PKEY + " INTEGER PRIMARY KEY, " +
                        DataUpload.COLUMN_ID_USUARIO + " INTEGER NOT NULL," +
                        DataUpload.COLUMN_ID_ENCUESTA + " INTEGER NOT NULL," +
                        DataUpload.COLUMN_VERSION + " TEXT NOT NULL)" ;
        String CREATE_TABLE_PREGUNTAS =
                "CREATE TABLE IF NOT EXISTS " + DataUpload.TABLE_PREGUNTAS +
                        "(" + DataUpload.COLUMN_PKEY + " INTEGER PRIMARY KEY, " +
                        DataUpload.COLUMN_ID_TIPO_PREGUNTA + " INTEGER NOT NULL," +
                        DataUpload.COLUMN_ID_ENCUESTA + " INTEGER NOT NULL," +
                        DataUpload.COLUMN_PREGUNTA + " TEXT NOT NULL," +
                        DataUpload.COLUMN_VERSION + " INTEGER NOT NULL)" ;
        String CREATE_TABLE_RESPUESTAS =
                "CREATE TABLE IF NOT EXISTS " + DataUpload.TABLE_RESPUESTAS +
                        "(" + DataUpload.COLUMN_PKEY + " INTEGER PRIMARY KEY, " +
                        DataUpload.COLUMN_ID_PREGUNTA + " INTEGER NOT NULL," +
                        DataUpload.COLUMN_RESPUESTA + " TEXT NOT NULL," +
                        DataUpload.COLUMN_ACCION_TIPO + " TEXT NOT NULL," +
                        DataUpload.COLUMN_ACCION_VALOR + " INTEGER," +
                        DataUpload.COLUMN_ID_ENCUESTA_ESTADO + " INTEGER," +
                        DataUpload.COLUMN_VERSION + " INTEGER)" ;
        String CREATE_TABLE_TIPO_PREGUNTA =
                "CREATE TABLE IF NOT EXISTS " + DataUpload.TABLE_TIPO_PREGUNTA +
                        "(" +
                        DataUpload.COLUMN_PKEY + " INTEGER PRIMARY KEY, " +
                        DataUpload.COLUMN_TIPO_PREGUNTA + " TEXT NOT NULL, " +
                        DataUpload.COLUMN_VERSION + " TEXT NOT NULL)" ;
        String CREATE_SERVER =
                "CREATE TABLE IF NOT EXISTS " + DataUpload.TABLE_SERVER_UPDATE +
                        "(" +
                        DataUpload.COLUMN_SERVER_URL + " TEXT PRIMARY KEY, " +
                        DataUpload.COLUMN_SERVER_USER + " TEXT NOT NULL, " +
                        DataUpload.COLUMN_SERVER_PASSWORD + " TEXT NOT NULL)" ;
        String CREATE_TABLE_DELETE_ACTIONS =
                "CREATE TABLE IF NOT EXISTS " + DataUpload.TABLE_DELETE_COMMAND +
                        "(" +
                        DataUpload.COLUMN_ID_USUARIO + " TEXT NOT NULL, " +
                        DataUpload.COLUMN_ID_COMMAND + " TEXT NOT NULL, " +
                        DataUpload.COLUMN_DATE_APPLICATION + " TEXT NOT NULL, " +
                        DataUpload.COLUMN_TYPE + " TEXT NOT NULL)" ;
        db.execSQL(CREATE_SERVER);
        db.execSQL(CREATE_TABLE_DELETE_ACTIONS);
        db.execSQL(CREATE_TABLE_CONTACTOS);
        db.execSQL(CREATE_TABLE_ENCUESTAS);
        db.execSQL(CREATE_TABLE_ENCUESTAS_ESTADO);
        db.execSQL(CREATE_TABLE_ENCUESTAS_ESTADO_SYNC);
        db.execSQL(CREATE_TABLE_USUARIOS);
        db.execSQL(CREATE_TABLE_USUARIOS_CONTACTOS);
        db.execSQL(CREATE_TABLE_USUARIOS_ENCUESTAS);
        db.execSQL(CREATE_TABLE_PREGUNTAS);
        db.execSQL(CREATE_TABLE_RESPUESTAS);
        db.execSQL(CREATE_TABLE_TIPO_PREGUNTA);
    }

    private void SaveDefaultData(SQLiteDatabase db){
        InsertEstadoSync(db,new tEncuestaEstatusSync(0,"Pendiente"));
        InsertEstadoSync(db,new tEncuestaEstatusSync(1,"Encuesta Empaquetada"));
        InsertEstadoSync(db,new tEncuestaEstatusSync(2,"Enviando Encuesta"));
        InsertEstadoSync(db,new tEncuestaEstatusSync(3,"Encuesta Recibida En Servidor"));
        InsertEstadoSync(db,new tEncuestaEstatusSync(4,"Procesando Encuesta"));
        InsertEstadoSync(db,new tEncuestaEstatusSync(5,"Importacion Exitosa"));
        InsertEstadoSync(db,new tEncuestaEstatusSync(6,"Actualizando Encuesta en Local"));
        InsertEstadoSync(db,new tEncuestaEstatusSync(7,"Sincronizada"));
        InsertEstadoSync(db,new tEncuestaEstatusSync(8,"Error al cargar el archivo"));
        InsertEstadoSync(db,new tEncuestaEstatusSync(9,"Error al insertar"));
        InsertEstadoSync(db,new tEncuestaEstatusSync(10,"Encuesta Existente"));
    }
    private void InsertEstadoSync(SQLiteDatabase db,tEncuestaEstatusSync contact) {
        ContentValues values=new ContentValues();
        values.put(DataUpload.COLUMN_PKEY , contact.getpKey());
        values.put(DataUpload.COLUMN_ENCUESTA_ESTADO_SYNC, contact.getEstatusEncuestaSync());
        db.insert(DataUpload.TABLE_ENCUESTAS_ESTADO_SYNC, null, values);
    }
    //Section to adding default data values to data base
    public void InsertContacto(SQLiteDatabase db,tContactos contact) {
        ContentValues values=new ContentValues();
        values.put(DataUpload._ID, contact.getpKey());
        Log.v("pKey",contact.getpKey());
        values.put(DataUpload.COLUMN_ID_PRECARGA, contact.getId_Precarga());
        Log.v("Id_Precarga",String.valueOf(contact.getId_Precarga()));
        values.put(DataUpload.COLUMN_ID_ENCUESTA, contact.getId_Encuesta());
        values.put(DataUpload.COLUMN_NOMBRE, contact.getNombre());
        values.put(DataUpload.COLUMN_PATERNO, contact.getPaterno());
        values.put(DataUpload.COLUMN_MATERNO, contact.getMaterno());
        values.put(DataUpload.COLUMN_CALLE, contact.getCalle());
        values.put(DataUpload.COLUMN_COLONIA, contact.getColonia());
        values.put(DataUpload.COLUMN_NUM_EXT, contact.getNumero_Ext());
        values.put(DataUpload.COLUMN_NUM_INT, contact.getNumero_Int());
        values.put(DataUpload.COLUMN_COD_POSTAL, contact.getCodigo_Postal());
        values.put(DataUpload.COLUMN_ENTIDAD, contact.getEntidad());
        values.put(DataUpload.COLUMN_MUNICIPIO, contact.getMinicipio());
        values.put(DataUpload.COLUMN_FECHA_NAC, contact.getFecha_Nacimiento());
        values.put(DataUpload.COLUMN_GENERO, contact.getGenero());
        values.put(DataUpload.COLUMN_VERSION, contact.getVersion());
        db.insert(DataUpload.TABLE_CONTACTOS, null, values);
    }

    public void InsertContactoSync(tContactos contact) {
        SQLiteDatabase db= getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(DataUpload._ID, contact.getId_Precarga());
        values.put(DataUpload.COLUMN_PKEY , contact.getId_Precarga());
        values.put(DataUpload.COLUMN_ID_PRECARGA, contact.getId_Precarga());
        Log.v("Id_Precarga",String.valueOf(contact.getId_Precarga()));
        values.put(DataUpload.COLUMN_ID_ENCUESTA, contact.getId_Encuesta());
        values.put(DataUpload.COLUMN_ID_USUARIO, contact.getIdus());
        values.put(DataUpload.COLUMN_NOMBRE, contact.getNombre());
        values.put(DataUpload.COLUMN_PATERNO, contact.getPaterno());
        values.put(DataUpload.COLUMN_MATERNO, contact.getMaterno());
        values.put(DataUpload.COLUMN_CALLE, contact.getCalle());
        values.put(DataUpload.COLUMN_COLONIA, contact.getColonia());
        values.put(DataUpload.COLUMN_NUM_EXT, contact.getNumero_Ext());
        values.put(DataUpload.COLUMN_NUM_INT, contact.getNumero_Int());
        values.put(DataUpload.COLUMN_COD_POSTAL, contact.getCodigo_Postal());
        values.put(DataUpload.COLUMN_ENTIDAD, contact.getEntidad());
        values.put(DataUpload.COLUMN_MUNICIPIO, contact.getMinicipio());
        values.put(DataUpload.COLUMN_FECHA_NAC, contact.getFecha_Nacimiento());
        values.put(DataUpload.COLUMN_GENERO, contact.getGenero());
        values.put(DataUpload.COLUMN_VERSION, contact.getVersion());
        db.insert(DataUpload.TABLE_CONTACTOS, null, values);
    }
    public void DeleteContactoSync(String tbl, String pkey, String id_p, String id_enc) {
        SQLiteDatabase db= getWritableDatabase();
        db.delete(tbl,DataUpload.COLUMN_PKEY+"_UC=? AND " +
                DataUpload.COLUMN_ID_PRECARGA+ "=? AND "+
                DataUpload.COLUMN_ID_ENCUESTA + "=? AND " +
                DataUpload.COLUMN_ID_USUARIO+ "=? ",new String[]{pkey, id_p, id_enc, DataUpload.USER_ID});
    }
    public long InsertContacto(tContactos c){
        SQLiteDatabase sqld= getWritableDatabase();
        sqld.insert(DataUpload.TABLE_USUARIOS_CONTACTOS,null,tUsuarioContactos.toContent());
        return sqld.insert(DataUpload.TABLE_CONTACTOS,null,c.toContent());
    }

    public void BackupSyncEncuestaResult(){
        int rowcount = 0;
        int colcount = 0;
        //Cursor c = this.getReadableDatabase().query(
        //        DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID+
        //                DataUpload.USER_ACCOUNT_ENC_ID ,
        //        null,
        //        DataUpload.COLUMN_ID_USUARIO + "=? ",
        //        new String[] { DataUpload.USER_ID},
        //        null, null, null, null);
        Cursor c = this.getReadableDatabase().rawQuery("select * from " +
                DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID+
                DataUpload.USER_ACCOUNT_ENC_ID,null);
        if(c != null && c.getCount() > 0){
            File dir= new File(DataUpload.LOCATION_DATA_SYNC);
            //Get the text file
            File file = new File(dir,"SyncData.sqlite");
            try {
                FileWriter fw = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(fw);
                rowcount = c.getCount();
                Log.v("rowcount",String.valueOf(c.getCount()));
                colcount = c.getColumnCount();
                Log.v("colcount",String.valueOf(c.getColumnCount()));
                if (rowcount > 0) {
                    c.moveToFirst();
                    for (int i = 0; i < colcount; i++) {
                        if (i != colcount - 1) {
                            bw.write(c.getColumnName(i) + "|");
                        } else {
                            bw.write(c.getColumnName(i));
                        }
                    }
                    bw.newLine();
                    for (int i = 0; i < rowcount; i++) {
                        c.moveToPosition(i);
                        for (int j = 0; j < colcount-1; j++) {
                            if (j != colcount - 1){
                                bw.write(c.getString(j) + "|");
                            }
                            else{
                                bw.write(c.getString(j));
                            }
                        }
                        bw.newLine();
                    }
                    bw.flush();
                }

            }
            catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    public void BackupSyncEncuestaResultCall(){
        try {
            Cursor c = this.getReadableDatabase().query(
                    DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID+
                            DataUpload.USER_ACCOUNT_ENC_ID ,
                    null,
                    DataUpload.COLUMN_ID_USUARIO
                            + "=? AND "+
                            DataUpload.COLUMN_ID_ENCUESTA_ESTADO_SYNC
                            + " IN(?,?,?,?,?,?,?,?,?)",
                    new String[] { DataUpload.USER_ID, "0", "1", "2", "3", "4", "5", "6","8","9"},
                    null, null, null, null);
            //ESTADO 7 Y 10 NO SUBIR
            //Cursor c = this.getReadableDatabase().rawQuery("select * from " +
            //        DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID+
            //        DataUpload.USER_ACCOUNT_ENC_ID,null);
            if(c != null && c.getCount() > 0){
                Log.v("Valor"," pendientes a cargar");
                DataUpload.SYNC_COLUMNS_DYMANICALLI_BLUIT=c.getColumnCount();
                DataUpload.SYNC_COLUMNS_DYMANICALLI_BLUIT-=11;
                DataUpload.SYNC_COLUMNS_DATA_RESERVATION=c;
            }
        }
        catch (Exception e){
            Log.v("Valor:","Error en BackupSyncEncuestaResultCall? "+e.getMessage());
        }
    }

    public long InsertEncuestaResult(tRespuestasEncuesta c){
        SQLiteDatabase sqld= getWritableDatabase();
        //COLOCARLE UN ESTADO AL USUARIO EN TAL ENCUESTA
        UPDATE_TABLE_US_CONTACTS(sqld);
        Log.v("Columns : ",String.valueOf(DataUpload.NUMBER_ANSWER_TO_SAVE));
        Log.v("Columns cre: ",buildColumnsDinamically(DataUpload.NUMBER_ANSWER_TO_SAVE).toString());
        String CREATE_RESPUESTA_ENCUESTA = "CREATE TABLE IF NOT EXISTS " +
                DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID+
                DataUpload.USER_ACCOUNT_ENC_ID +
                "(" +
                DataUpload._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DataUpload.COLUMN_PKEY + " TEXT NOT NULL, " +
                DataUpload.COLUMN_ID_USUARIO + " INTEGER NOT NULL, " +
                DataUpload.COLUMN_ID_CONTACTO + " TEXT NOT NULL, " +
                DataUpload.COLUMN_ID_ENCUESTA + " INTEGER NOT NULL, "+
                DataUpload.COLUMN_ID_ENCUESTA_ESTADO + " INTEGER NOT NULL, "+
                DataUpload.COLUMN_ENCUESTA_INICIO + " TEXT NOT NULL, " +
                DataUpload.COLUMN_ENCUESTA_FIN + " TEXT NOT NULL, " +
                DataUpload.COLUMN_ENCUESTA_LATITUD + " TEXT NOT NULL, " +
                DataUpload.COLUMN_ENCUESTA_LONGITUD+ " TEXT NOT NULL, " +
                DataUpload.COLUMN_ID_ENCUESTA_ESTADO_SYNC+ " INTEGER NOT NULL, " +
                DataUpload.COLUMN_FOLIO_SYNC+ " TEXT NOT NULL, " +
                buildColumnsDinamically(DataUpload.NUMBER_ANSWER_TO_SAVE).toString()+
                ")";
        Log.v("Columns created: ",CREATE_RESPUESTA_ENCUESTA);
        sqld.execSQL(CREATE_RESPUESTA_ENCUESTA);
        Log.v("INSERTED: ","VALUES TO");
        return sqld.insert(DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID+
                DataUpload.USER_ACCOUNT_ENC_ID,null,c.toContent());
        //return 0;
    }
    //Al actualizar el estado de la encuesta
    public long UpdateEncuestaResult(tRespuestasEncuesta c){
        Log.v("UPDATED: ",DataUpload.ID_REDITABLE_ENCUESTA_ID);
        SQLiteDatabase sqld= getWritableDatabase();
        //COLOCARLE UN ESTADO AL USUARIO EN TAL ENCUESTA
        UPDATE_TABLE_US_CONTACTS(sqld);
        return sqld.update(DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID+
                DataUpload.USER_ACCOUNT_ENC_ID,c.toContent(),
                DataUpload.COLUMN_PKEY + "=? AND "+
                        DataUpload.COLUMN_ID_USUARIO + "=? AND " +
                DataUpload.COLUMN_ID_CONTACTO + "=? " ,
                new String[]{DataUpload.ID_REDITABLE_ENCUESTA_ID,DataUpload.USER_ID,
                        DataUpload.USER_ACCOUNT_ID});
        //MODIFICACIONES REALIZADAS AGREGADO DataUpload.COLUMN_ID_USUARIO + "=? AND " +
        //MODIFICACIONES REALIZADAS AGREGADO DataUpload.USER_ID
        //return 0;
    }
    //Depues de sincronizar
    public long UpdateEncuestaResultStateSync(String c, String id, String id_contact, String folio){
        //Log.v("Valor: UPDATE",DataUpload.ID_REDITABLE_ENCUESTA_ID);
        ContentValues cv = new ContentValues();
        cv.put(DataUpload.COLUMN_ID_ENCUESTA_ESTADO_SYNC ,c);
        cv.put(DataUpload.COLUMN_FOLIO_SYNC ,folio);
        Log.v("Valor","Guardando en estado sync folio " + folio);
        SQLiteDatabase sqld= getWritableDatabase();
        return sqld.update(DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID+
                        DataUpload.USER_ACCOUNT_ENC_ID,cv,
                DataUpload.COLUMN_PKEY + "=? AND "+
                        DataUpload.COLUMN_ID_USUARIO + "=? AND " +
                        DataUpload.COLUMN_ID_CONTACTO + "=? " ,
                new String[]{id,DataUpload.USER_ID,
                        id_contact});
    }
    //Despues de sincronizar eliminar toda la info relacionado a ello
    public long DeleteEncuestaResultStateSync(String id, String id_contact){
        Log.v("Valor","eliminar datos relacionados " + id);
        SQLiteDatabase sqld= getWritableDatabase();
        //Realizar todas las actualizaciones
        sqld.delete(DataUpload.TABLE_CONTACTOS,
                DataUpload.COLUMN_PKEY + "=? AND "+
                        DataUpload._ID + "=? ",
                new String[]{id_contact,id_contact});

        sqld.delete(DataUpload.TABLE_USUARIOS_CONTACTOS,
                DataUpload.COLUMN_PKEY + "_UC=? AND "+
                        DataUpload.COLUMN_ID_CONTACTO + "=? ",
                new String[]{id_contact,id_contact});

        return sqld.delete(DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID+
                        DataUpload.USER_ACCOUNT_ENC_ID,
                DataUpload.COLUMN_PKEY + "=? AND "+
                        DataUpload.COLUMN_ID_USUARIO + "=? AND " +
                        DataUpload.COLUMN_ID_CONTACTO + "=? " ,
                new String[]{id,DataUpload.USER_ID,
                        id_contact});
    }
    public void DeleteContactStateSync(String id){
        Log.v("Valor","eliminar datos relacionados " + id);
        SQLiteDatabase sqld= getWritableDatabase();
        //Realizar todas las actualizaciones
        sqld.delete(DataUpload.TABLE_CONTACTOS,
                DataUpload.COLUMN_ID_USUARIO+ "=? ",
                new String[]{id});

        sqld.delete(DataUpload.TABLE_USUARIOS_CONTACTOS,
                DataUpload.COLUMN_ID_USUARIO+ "=? ",
                new String[]{id});

        sqld.delete(DataUpload.TABLE_USUARIOS_ENCUESTAS,
                DataUpload.COLUMN_ID_USUARIO+ "=? ",
                new String[]{id});
    }
    //Despues de sincronizar eliminar toda la info relacionado a ello
    public void DeleteAllResultStateSync(){
        Log.v("Valor","eliminar todo ");
        SQLiteDatabase sqld= getWritableDatabase();
        String rawQuery = "SELECT "+DataUpload.COLUMN_PKEY+" FROM " +
                DataUpload.TABLE_ENCUESTAS;
        Cursor cursor = sqld.rawQuery(rawQuery,null);
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    sqld.execSQL("DROP TABLE IF EXISTS " + DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID
                            +cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_PKEY)));
                } while (cursor.moveToNext());
            }
        }
        sqld.execSQL("DROP TABLE IF EXISTS " + DataUpload.TABLE_CONTACTOS);
        sqld.execSQL("DROP TABLE IF EXISTS " + DataUpload.TABLE_SERVER_UPDATE);
        sqld.execSQL("DROP TABLE IF EXISTS " + DataUpload.TABLE_USUARIOS);
        sqld.execSQL("DROP TABLE IF EXISTS " + DataUpload.TABLE_ENCUESTAS_ESTADO_SYNC);
        sqld.execSQL("DROP TABLE IF EXISTS " + DataUpload.TABLE_ENCUESTAS_ESTADO);
        sqld.execSQL("DROP TABLE IF EXISTS " + DataUpload.TABLE_ENCUESTAS);
        sqld.execSQL("DROP TABLE IF EXISTS " + DataUpload.TABLE_PREGUNTAS);
        sqld.execSQL("DROP TABLE IF EXISTS " + DataUpload.TABLE_TIPO_PREGUNTA);
        sqld.execSQL("DROP TABLE IF EXISTS " + DataUpload.TABLE_RESPUESTAS);
        sqld.execSQL("DROP TABLE IF EXISTS " + DataUpload.TABLE_USUARIOS_CONTACTOS);
        sqld.execSQL("DROP TABLE IF EXISTS " + DataUpload.TABLE_USUARIOS_ENCUESTAS);
        CREATE_TABLES_TODB(sqld);
        SaveDefaultData(sqld);
    }
    public long UpdateEncuestaResultContactoInfoSync(String newid, String lastid, String lastidrepenc){
        //Log.v("Valor: UPDATE",DataUpload.ID_REDITABLE_ENCUESTA_ID);
        //Valores para la encuesta respuesta
        ContentValues cv = new ContentValues();
        cv.put(DataUpload.COLUMN_ID_CONTACTO ,newid);

        //Valores para el nuevo id del contacto
        ContentValues cb = new ContentValues();
        cb.put(DataUpload.COLUMN_PKEY ,newid);
        cb.put(DataUpload._ID ,newid);

        //Valores para el nuevo id del usuario contacto
        ContentValues cf = new ContentValues();
        cf.put(DataUpload.COLUMN_PKEY+"_UC",newid);
        cf.put(DataUpload.COLUMN_ID_CONTACTO ,newid);

        SQLiteDatabase sqld= getWritableDatabase();

        //Realizar todas las actualizaciones
        sqld.update(DataUpload.TABLE_CONTACTOS,cb,
                DataUpload.COLUMN_PKEY + "=? AND "+
                        DataUpload._ID + "=? ",
                new String[]{lastid,lastid});

        sqld.update(DataUpload.TABLE_USUARIOS_CONTACTOS,cf,
                DataUpload.COLUMN_PKEY + "_UC=? AND "+
                        DataUpload.COLUMN_ID_CONTACTO + "=? ",
                new String[]{lastid,lastid});

        return sqld.update(DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID+
                        DataUpload.USER_ACCOUNT_ENC_ID,cv,
                DataUpload.COLUMN_PKEY + "=? ",
                new String[]{lastidrepenc});
    }

    private void UPDATE_TABLE_US_CONTACTS(SQLiteDatabase myDB){
        ContentValues cv = new ContentValues();
        cv.put(DataUpload.COLUMN_ID_ENCUESTA_ESTADO ,DataUpload.USER_ACCOUNT_ENC_ESTADO_ID);
        Log.v("Valor",DataUpload.USER_ACCOUNT_ENC_ESTADO_ID);
        myDB.update(DataUpload.TABLE_USUARIOS_CONTACTOS, cv,
                DataUpload.COLUMN_ID_ENCUESTA + "=? AND " +
                        DataUpload.COLUMN_ID_USUARIO + "=? AND " +
                        DataUpload.COLUMN_PKEY+ "_UC=? AND " +
                        DataUpload.COLUMN_ID_CONTACTO + "=? ",
                new String[]{
                        DataUpload.USER_ACCOUNT_ENC_ID,
                        DataUpload.USER_ID,
                        DataUpload.USER_ACCOUNT_ID_PKEY,
                        DataUpload.USER_ACCOUNT_ID});

        Log.v("Finish Like",DataUpload.USER_ACCOUNT_ENC_ESTADO_ID);
        Log.v("Finish Like cont",DataUpload.USER_ACCOUNT_ID);
        Log.v("Finish Like cont_UC",DataUpload.USER_ACCOUNT_ID_PKEY);
    }

    public void getAllTablesName(){
        SQLiteDatabase sqld= getReadableDatabase();
        Cursor g = sqld.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (g.moveToFirst()) {
            while ( !g.isAfterLast() ) {
                Log.v("Table exist in db:",  g.getString( g.getColumnIndex("name")));
                g.moveToNext();
            }
        }
    }

    public static StringBuilder buildColumnsDinamically(int myNum){
        StringBuilder fullString=new StringBuilder();
        //fullString.append("(");
        for(int i=1;i<myNum;i++){
            fullString.append("p_" +String.valueOf(i)+" TEXT ");
            if(i!=myNum-1){
                fullString.append(",");
            }
        }
        //fullString.append(")");
        return fullString;
    }

    public Cursor getEncuestasResultId() {
        try{
            Cursor cursor = this.getReadableDatabase().query(
                    DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID+
                            DataUpload.USER_ACCOUNT_ENC_ID ,
                    null,
                    DataUpload.COLUMN_ID_USUARIO + "=?",
                    new String[] { DataUpload.USER_ID},
                    null, null, null, null);
            int n=0;
            if (cursor != null && cursor.getCount() > 0) {
                if (cursor.moveToFirst()) {
                    DataUpload.USER_ENCUESTAS_CONTACTS_TODO = new String[cursor.getCount()];
                    DataUpload.EncuestaEstadoList= new ArrayList<String>();
                    DataUpload.ID_REDITABLE_ENCUESTA_LIST= new ArrayList<String>();
                    DataUpload.TIME_REDITABLE_ENCUESTA_LIST= new ArrayList<String>();
                    do {
                        DataUpload.USER_ENCUESTAS_CONTACTS_TODO[n] = cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_ID_CONTACTO));
                        DataUpload.EncuestaEstadoList.add(
                                cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_ID_ENCUESTA_ESTADO)
                                ));
                        DataUpload.ID_REDITABLE_ENCUESTA_LIST.add(
                                cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_PKEY)
                                ));
                        DataUpload.TIME_REDITABLE_ENCUESTA_LIST.add(
                                cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_ENCUESTA_FIN)
                                ));
                        Log.v("Reservation: ",DataUpload.USER_ENCUESTAS_CONTACTS_TODO[n]);
                        Log.v("Reservation ESTADO: ",DataUpload.EncuestaEstadoList.get(n));
                        n++;
                    } while (cursor.moveToNext());
                }
                return getAllDataClinetes(DataUpload.USER_ENCUESTAS_CONTACTS_TODO);
            }
        }catch (SQLiteException e){
        }
        return null;
    }

    public Cursor getEncuestasAnsIdCompleteStructure(String name){
        String rawQuery="";
        if (name.toString().equals("")  &&  name.length() == 0) {
            rawQuery =
                    "SELECT " +
                            DataUpload.TABLE_USUARIOS_CONTACTOS+"."+ DataUpload.COLUMN_PKEY + "_UC," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_PKEY + " AS pkey_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_ID_PRECARGA+ " AS id_precarga_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_ID_ENCUESTA + " AS id_encuesta_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_ID_USUARIO + " AS id_usuario_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_NOMBRE + " AS nombre_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_PATERNO + " AS paterno_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_MATERNO + " AS materno_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_CALLE + " AS calle_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_COLONIA + " AS colonia_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_NUM_EXT + " AS numero_ext_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_NUM_INT + " AS numero_int_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_COD_POSTAL + " AS codigo_postal_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_ENTIDAD + " AS entidad_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_MUNICIPIO + " AS municipio_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_FECHA_NAC + " AS fecha_nacimiento_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_GENERO + " AS genero_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_VERSION + " AS version_c," +
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload._ID+ ","+
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_PKEY + " AS pkey_re_en,"+
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_ENCUESTA_FIN+ " AS fin_re_en,"+
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_ID_ENCUESTA_ESTADO+ " AS id_estado_re_en,"+
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_FOLIO_SYNC+ " AS folio_re_en,"+
                            DataUpload.TABLE_ENCUESTAS_ESTADO_SYNC+"."+DataUpload.COLUMN_ENCUESTA_ESTADO_SYNC+ " AS id_estado_sync_re_en,"+
                            DataUpload.TABLE_ENCUESTAS_ESTADO+"."+DataUpload.COLUMN_ENCUESTA_ESTADO+ " AS text_estado "
                            +" FROM " +
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID
                            + " JOIN " + DataUpload.TABLE_CONTACTOS
                            + " ON " +
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_ID_CONTACTO + " = " +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_PKEY +
                            " JOIN " + DataUpload.TABLE_ENCUESTAS_ESTADO
                            + " ON " +
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_ID_ENCUESTA_ESTADO + " = " +
                            DataUpload.TABLE_ENCUESTAS_ESTADO+"."+ DataUpload.COLUMN_PKEY
                            + " JOIN " +
                            DataUpload.TABLE_ENCUESTAS_ESTADO_SYNC
                            + " ON " +
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_ID_ENCUESTA_ESTADO_SYNC + " = " +
                            DataUpload.TABLE_ENCUESTAS_ESTADO_SYNC+"."+ DataUpload.COLUMN_PKEY
                            +" JOIN " + DataUpload.TABLE_USUARIOS_CONTACTOS
                            + " ON " +
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_ID_CONTACTO + " = " +
                            DataUpload.TABLE_USUARIOS_CONTACTOS+"."+ DataUpload.COLUMN_ID_CONTACTO +
                            " WHERE " +
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_ID_USUARIO + " = " +
                            DataUpload.USER_ID
                            + " AND "+
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_ID_ENCUESTA + " = " +
                            DataUpload.USER_ACCOUNT_ENC_ID +
                            " ORDER BY " +  DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload._ID +" DESC";
        } else {
            rawQuery =
                    "SELECT " +
                            DataUpload.TABLE_USUARIOS_CONTACTOS+"."+ DataUpload.COLUMN_PKEY + "_UC," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_PKEY + " AS pkey_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_ID_PRECARGA+ " AS id_precarga_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_ID_ENCUESTA + " AS id_encuesta_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_ID_USUARIO + " AS id_usuario_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_NOMBRE + " AS nombre_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_PATERNO + " AS paterno_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_MATERNO + " AS materno_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_CALLE + " AS calle_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_COLONIA + " AS colonia_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_NUM_EXT + " AS numero_ext_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_NUM_INT + " AS numero_int_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_COD_POSTAL + " AS codigo_postal_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_ENTIDAD + " AS entidad_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_MUNICIPIO + " AS municipio_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_FECHA_NAC + " AS fecha_nacimiento_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_GENERO + " AS genero_c," +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_VERSION + " AS version_c," +
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload._ID+ ","+
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_PKEY + " AS pkey_re_en,"+
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_ENCUESTA_FIN+ " AS fin_re_en,"+
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_ID_ENCUESTA_ESTADO+ " AS id_estado_re_en,"+
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_FOLIO_SYNC+ " AS folio_re_en,"+
                            DataUpload.TABLE_ENCUESTAS_ESTADO_SYNC+"."+DataUpload.COLUMN_ENCUESTA_ESTADO_SYNC+ " AS id_estado_sync_re_en,"+
                            DataUpload.TABLE_ENCUESTAS_ESTADO+"."+DataUpload.COLUMN_ENCUESTA_ESTADO+ " AS text_estado "
                            +" FROM " +
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID
                            + " JOIN " + DataUpload.TABLE_CONTACTOS
                            + " ON " +
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_ID_CONTACTO + " = " +
                            DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_PKEY +
                            " JOIN " + DataUpload.TABLE_ENCUESTAS_ESTADO
                            + " ON " +
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_ID_ENCUESTA_ESTADO + " = " +
                            DataUpload.TABLE_ENCUESTAS_ESTADO+"."+ DataUpload.COLUMN_PKEY
                            + " JOIN " +
                            DataUpload.TABLE_ENCUESTAS_ESTADO_SYNC
                            + " ON " +
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_ID_ENCUESTA_ESTADO_SYNC + " = " +
                            DataUpload.TABLE_ENCUESTAS_ESTADO_SYNC+"."+ DataUpload.COLUMN_PKEY
                            +" JOIN " + DataUpload.TABLE_USUARIOS_CONTACTOS
                            + " ON " +
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_ID_CONTACTO + " = " +
                            DataUpload.TABLE_USUARIOS_CONTACTOS+"."+ DataUpload.COLUMN_ID_CONTACTO +
                            " WHERE " +
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_ID_USUARIO + " = " +
                            DataUpload.USER_ID
                            + " AND "+
                            DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload.COLUMN_ID_ENCUESTA + " = " +
                            DataUpload.USER_ACCOUNT_ENC_ID
                            + " AND "+
                            DataUpload.TABLE_CONTACTOS+"."+DataUpload.COLUMN_NOMBRE + " LIKE '"+ name + "%' "+
                            " ORDER BY " +  DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID + DataUpload.USER_ACCOUNT_ENC_ID+"."+DataUpload._ID +" DESC";
        }
        //---Log.v("Consult",rawQuery);
        try {
            Cursor cursor = this.getReadableDatabase().rawQuery(rawQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                int n=0;
                if (cursor.moveToFirst()) {
                    DataUpload.ID_REDITABLE_ENCUESTA_LIST= new ArrayList<String>();
                    do {
                        DataUpload.ID_REDITABLE_ENCUESTA_LIST.add(
                                cursor.getString(cursor.getColumnIndex("pkey_re_en")
                                ));
                        Log.v("VALUE DATA",String.valueOf(cursor.getString(cursor.getColumnIndex("pkey_re_en"))));
                        n++;
                    } while (cursor.moveToNext());
                }
                Log.v("VALUE FISRT",String.valueOf(cursor.getCount()));
                return cursor;
            }
        }catch (Exception E)
        {        }
        return null;
    }
    public Cursor getRespuestasDone(){
        return this.getReadableDatabase().query(
                DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID+
                        DataUpload.USER_ACCOUNT_ENC_ID,
                null,
                        DataUpload.COLUMN_PKEY + "=? AND "+
                        DataUpload.COLUMN_ID_USUARIO + "=? AND " +
                        DataUpload.COLUMN_ID_CONTACTO + "=? ",
                new String[]{
                        DataUpload.ID_REDITABLE_ENCUESTA_ID,
                        DataUpload.USER_ID,
                        DataUpload.USER_ACCOUNT_ID},
                null,
                null,
                null
        );
    }

    public void InsertDeleteActionsSync(String us,String command, String date, String type, boolean action) {
        SQLiteDatabase db= getWritableDatabase();
        //Buscamos si existe el ya, luego eliminarlo los que consincida con tal id_usuario y luego insertar
        if(action){
            db.delete(DataUpload.TABLE_DELETE_COMMAND, DataUpload.COLUMN_ID_USUARIO + "=? ", new String[]{us});
        }
        //----Luego selecionamos al hacer el loging con sql todos las fechas menores o guales al dia de hoy, los resultados
        //....ejecuamos la eccion de eliminado o borrado
        ContentValues values=new ContentValues();
        values.put(DataUpload.COLUMN_ID_USUARIO,us);
        values.put(DataUpload.COLUMN_ID_COMMAND, command);
        values.put(DataUpload.COLUMN_DATE_APPLICATION, date);
        values.put(DataUpload.COLUMN_TYPE, type);
        db.insert(DataUpload.TABLE_DELETE_COMMAND, null, values);
    }
    public void InsertServerSync(String ur, String us, String pa) {
        SQLiteDatabase db= getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + DataUpload.TABLE_SERVER_UPDATE);
        String CREATE_SERVER =
                "CREATE TABLE IF NOT EXISTS " + DataUpload.TABLE_SERVER_UPDATE +
                        "(" +
                        DataUpload.COLUMN_SERVER_URL + " TEXT PRIMARY KEY, " +
                        DataUpload.COLUMN_SERVER_USER + " TEXT NOT NULL, " +
                        DataUpload.COLUMN_SERVER_PASSWORD + " TEXT NOT NULL)" ;
        db.execSQL(CREATE_SERVER);
        ContentValues values=new ContentValues();
        values.put(DataUpload.COLUMN_SERVER_URL, ur);
        values.put(DataUpload.COLUMN_SERVER_USER, us);
        values.put(DataUpload.COLUMN_SERVER_PASSWORD, pa);
        db.insert(DataUpload.TABLE_SERVER_UPDATE, null, values);
    }

    public void InsertEncuesta(SQLiteDatabase db,tEncuestas v) {
        ContentValues values=new ContentValues();
        values.put(DataUpload.COLUMN_PKEY, v.getpKey());
        values.put(DataUpload.COLUMN_ENCUESTA, v.getEncuesta());
        db.insert(DataUpload.TABLE_ENCUESTAS, null, values);
    }
    public void InsertEncuestaSync(tEncuestas v) {
        SQLiteDatabase db= getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(DataUpload._ID, v.getpKey());
        values.put(DataUpload.COLUMN_PKEY, v.getpKey());
        values.put(DataUpload.COLUMN_ENCUESTA, v.getEncuesta());
        values.put(DataUpload.COLUMN_VERSION, v.getVersion());
        values.put(DataUpload.COLUMN_CADUCIDAD, v.getCaducidad());
        db.insert(DataUpload.TABLE_ENCUESTAS, null, values);
    }
    public void DeleteEncuestaSync(String tbl, String pkey, String id_enc) {
        SQLiteDatabase db= getWritableDatabase();
        db.delete(tbl,DataUpload.COLUMN_PKEY+"=? AND " +
                        DataUpload.COLUMN_ENCUESTA+ "=? ",
                new String[]{pkey,id_enc});
    }

    public void InsertUsuarioContactos(SQLiteDatabase db,tUsuarioContactos v) {
        ContentValues values=new ContentValues();
        values.put(DataUpload.COLUMN_ID_USUARIO, v.getIdUsuario());
        values.put(DataUpload.COLUMN_ID_CONTACTO, v.getIdContacto());
        values.put(DataUpload.COLUMN_ID_ENCUESTA, v.getIdEncuesta());
        values.put(DataUpload.COLUMN_ID_ENCUESTA_ESTADO, v.getIdEncuestaEstado());
        db.insert(DataUpload.TABLE_USUARIOS_CONTACTOS, null, values);
    }
    public void InsertUsuarioContactosSync(tUsuarioContactos v) {
        SQLiteDatabase db= getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(DataUpload.COLUMN_PKEY+"_UC", v.getPkey());
        values.put(DataUpload.COLUMN_ID_USUARIO, v.getIdUsuario());
        values.put(DataUpload.COLUMN_ID_USUARIO, v.getIdUsuario());
        values.put(DataUpload.COLUMN_ID_CONTACTO, v.getIdContacto());
        values.put(DataUpload.COLUMN_ID_ENCUESTA, v.getIdEncuesta());
        values.put(DataUpload.COLUMN_ID_ENCUESTA_ESTADO, v.getIdEncuestaEstado());
        values.put(DataUpload.COLUMN_VERSION, v.getVersion());
        db.insert(DataUpload.TABLE_USUARIOS_CONTACTOS, null, values);
    }
    public void DeleteUsuarioContactosSync(String tbl, String pkey, String id_us, String id_con, String id_enc) {
        SQLiteDatabase db= getWritableDatabase();
        db.delete(tbl,
                DataUpload.COLUMN_PKEY+ "_UC=? AND "+
                DataUpload.COLUMN_ID_USUARIO+ "=? AND "+
                DataUpload.COLUMN_ID_CONTACTO+ "=? AND "+
                DataUpload.COLUMN_ID_ENCUESTA + "=? ",
                new String[]{pkey,id_us, id_con, id_enc});
    }

    public void InsertUsuarios(SQLiteDatabase db,tUsuarios v) {
        ContentValues values=new ContentValues();
        values.put(DataUpload.COLUMN_PKEY, v.getpKey());
        values.put(DataUpload.COLUMN_NICK, v.getNick());
        values.put(DataUpload.COLUMN_PASSWORD, v.getPassword());
        values.put(DataUpload.COLUMN_NOMBRE, v.getNombre());
        db.insert(DataUpload.TABLE_USUARIOS, null, values);
    }
    public void InsertUsuariosSync(tUsuarios v) {
        SQLiteDatabase db= getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(DataUpload.COLUMN_PKEY, v.getpKey());
        values.put(DataUpload.COLUMN_NICK, v.getNick());
        values.put(DataUpload.COLUMN_PASSWORD, v.getPassword());
        values.put(DataUpload.COLUMN_NOMBRE, v.getNombre());
        values.put(DataUpload.COLUMN_PATERNO, v.getPat());
        values.put(DataUpload.COLUMN_MATERNO, v.getMat());
        values.put(DataUpload.COLUMN_VERSION, v.getVersion());
        db.insert(DataUpload.TABLE_USUARIOS, null, values);
    }
    public void DeleteUsuariosSync(String tbl, String pkey, String nick) {
        SQLiteDatabase db= getWritableDatabase();
        db.delete(tbl,DataUpload.COLUMN_PKEY+"=? AND " +
                DataUpload.COLUMN_NICK + "=? ",new String[]{pkey, nick});
    }

    public void InsertUsuariosEncuestas(SQLiteDatabase db,tUsuarioEncuestas v) {
        ContentValues values=new ContentValues();
        values.put(DataUpload.COLUMN_PKEY, v.getpKey());
        values.put(DataUpload.COLUMN_ID_USUARIO, v.getIdUsuario());
        values.put(DataUpload.COLUMN_ID_ENCUESTA, v.getIdEncuesta());
        db.insert(DataUpload.TABLE_USUARIOS_ENCUESTAS, null, values);
    }
    public void InsertUsuariosEncuestasSync(tUsuarioEncuestas v) {
        SQLiteDatabase db= getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(DataUpload.COLUMN_PKEY, v.getpKey());
        values.put(DataUpload.COLUMN_ID_USUARIO, v.getIdUsuario());
        values.put(DataUpload.COLUMN_ID_ENCUESTA, v.getIdEncuesta());
        values.put(DataUpload.COLUMN_VERSION, v.getVersion());
        db.insert(DataUpload.TABLE_USUARIOS_ENCUESTAS, null, values);
    }
    public void DeleteUsuariosEncuestasSync(String tbl, String pkey, String idus, String iden) {
        SQLiteDatabase db= getWritableDatabase();
        db.delete(tbl,DataUpload.COLUMN_PKEY+"=? AND " +
                DataUpload.COLUMN_ID_USUARIO + "=? AND " +
                DataUpload.COLUMN_ID_ENCUESTA + "=? ",new String[]{pkey, idus, iden});
    }

    public void InsertPreguntaSync(tPreguntas v) {
        SQLiteDatabase db= getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(DataUpload.COLUMN_PKEY, v.getpKey());
        values.put(DataUpload.COLUMN_ID_TIPO_PREGUNTA, v.getIdTipoPregunta());
        values.put(DataUpload.COLUMN_ID_ENCUESTA, v.getIdEncuesta());
        values.put(DataUpload.COLUMN_PREGUNTA, v.getPregunta());
        values.put(DataUpload.COLUMN_VERSION, v.getVersion());
        db.insert(DataUpload.TABLE_PREGUNTAS, null, values);
    }
    public void DeletePreguntaSync(String tbl, String pkey, String idtippreg, String iden) {
        SQLiteDatabase db= getWritableDatabase();
        db.delete(tbl,DataUpload.COLUMN_PKEY+"=? AND " +
                DataUpload.COLUMN_ID_TIPO_PREGUNTA + "=? AND " +
                DataUpload.COLUMN_ID_ENCUESTA + "=? ",new String[]{pkey, idtippreg, iden});
    }

    public void InsertRespuestaSync(tRespuestas v) {
        SQLiteDatabase db= getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(DataUpload.COLUMN_PKEY, v.getpKey());
        values.put(DataUpload.COLUMN_ID_PREGUNTA, v.getIdPregunta());
        values.put(DataUpload.COLUMN_RESPUESTA, v.getRespuesta());
        values.put(DataUpload.COLUMN_ACCION_TIPO, v.getAccionTipo());
        values.put(DataUpload.COLUMN_ACCION_VALOR, v.getAccionValor());
        values.put(DataUpload.COLUMN_ID_ENCUESTA_ESTADO, v.getIdEncuestaEstado());
        values.put(DataUpload.COLUMN_VERSION, v.getVersion());
        db.insert(DataUpload.TABLE_RESPUESTAS, null, values);
    }
    public void DeleteRespuestaSync(String tbl, String pkey, String idus) {
        SQLiteDatabase db= getWritableDatabase();
        db.delete(tbl,DataUpload.COLUMN_PKEY+"=? AND " +
                DataUpload.COLUMN_ID_PREGUNTA + "=? ",new String[]{pkey, idus});
    }

    public void InsertTipoPreguntaSync(tTipoPregunta v) {
        SQLiteDatabase db= getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(DataUpload.COLUMN_PKEY, v.getpKey());
        values.put(DataUpload.COLUMN_TIPO_PREGUNTA, v.getTipoPregunta());
        values.put(DataUpload.COLUMN_VERSION, v.getVersion());
        db.insert(DataUpload.TABLE_TIPO_PREGUNTA, null, values);
    }
    public void DeleteTipoPreguntaSync(String tbl, String pkey, String tip) {
        SQLiteDatabase db= getWritableDatabase();
        db.delete(tbl,DataUpload.COLUMN_PKEY+"=? AND " +
                DataUpload.COLUMN_TIPO_PREGUNTA + "=? ",new String[]{pkey, tip});
    }

    public void InsertEncuestaEstadoSync(tEncuestasEstatus v) {
        SQLiteDatabase db= getWritableDatabase();
        ContentValues values=new ContentValues();
        values.put(DataUpload._ID, v.getpKey());
        values.put(DataUpload.COLUMN_PKEY, v.getpKey());
        values.put(DataUpload.COLUMN_ENCUESTA_ESTADO, v.getEstatusEncuesta());
        values.put(DataUpload.COLUMN_VERSION, v.getVersion());
        db.insert(DataUpload.TABLE_ENCUESTAS_ESTADO, null, values);
    }
    public void DeleteEncuestaEstadoSync(String tbl, String pkey) {
        SQLiteDatabase db= getWritableDatabase();
        db.delete(tbl,DataUpload.COLUMN_PKEY+"=? ",new String[]{pkey});
    }

    public  Cursor getAllClientesIDPOST(String id){
        Cursor cursor = this.getReadableDatabase().query(DataUpload.TABLE_USUARIOS_CONTACTOS , null,
                DataUpload.COLUMN_ID_USUARIO + "=? AND " +
                DataUpload.COLUMN_ID_ENCUESTA + "=? AND " +
                        DataUpload.COLUMN_ID_ENCUESTA_ESTADO + "=? ",
                new String[] { DataUpload.USER_ID, id, "0"}, null, null, null, null);
        int n=0;
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                DataUpload.USER_IDS_CONTACTS = new String[cursor.getCount()];
                do {
                    DataUpload.USER_IDS_CONTACTS[n] = cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_ID_CONTACTO));
                    //Log.v("USER_IDS_CONTACTS:--",DataUpload.USER_IDS_CONTACTS[n]);
                    n++;
                } while (cursor.moveToNext());
            }
            return getAllDataClinetes(DataUpload.USER_IDS_CONTACTS);
        }
        //return getAllClientsId(new String[]{DataUpload.USER_IDS_CONTACTS});
        return null;
    }
    private Cursor Reversed(String id){
        String rawQuery = "SELECT * FROM " +
                DataUpload.TABLE_USUARIOS_CONTACTOS +
                " JOIN " + DataUpload.TABLE_CONTACTOS
                + " ON " +
                DataUpload.TABLE_USUARIOS_CONTACTOS+"."+ DataUpload.COLUMN_ID_USUARIO + " = " +
                DataUpload.TABLE_CONTACTOS+"."+DataUpload.COLUMN_ID_USUARIO
                + " WHERE " +
                DataUpload.TABLE_USUARIOS_CONTACTOS+"."+DataUpload.COLUMN_ID_USUARIO + " = " +  DataUpload.USER_ID + " AND "+
                DataUpload.TABLE_USUARIOS_CONTACTOS+"."+DataUpload.COLUMN_ID_ENCUESTA + " = " +  id + " AND "+
                DataUpload.TABLE_USUARIOS_CONTACTOS+"."+DataUpload.COLUMN_ID_ENCUESTA_ESTADO + " = 0 "
                +" ORDER BY " +  DataUpload.TABLE_CONTACTOS+"."+DataUpload.COLUMN_NOMBRE +" ASC";
        Cursor cursor = this.getReadableDatabase().rawQuery(rawQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            Log.v("VALUE FISRT",String.valueOf(cursor.getCount()));
            return cursor;
        }
        //return getAllClientsId(new String[]{DataUpload.USER_IDS_CONTACTS});
        return null;
    }

    private void getServerInfo(){
        String rawQueryd = "SELECT * FROM " +
                DataUpload.TABLE_SERVER_UPDATE ;
        Cursor cursorf = this.getReadableDatabase().rawQuery(rawQueryd, null);
        if (cursorf != null && cursorf.getCount() > 0) {
            if (cursorf.moveToFirst()) {
                do {
                    DataUpload.SYNC_URL_FTP_TRANSFER=cursorf.getString(cursorf.getColumnIndex(DataUpload.COLUMN_SERVER_URL));
                    DataUpload.SYNC_URL_FTP_TRANSFER_USER=cursorf.getString(cursorf.getColumnIndex(DataUpload.COLUMN_SERVER_USER));
                    DataUpload.SYNC_URL_FTP_TRANSFER_PASS=cursorf.getString(cursorf.getColumnIndex(DataUpload.COLUMN_SERVER_PASSWORD));
                    Log.v("Valor","server ftp" +DataUpload.SYNC_URL_FTP_TRANSFER);
                } while (cursorf.moveToNext());
            }
        }
    }

    public  Cursor getAllClientesID(String id){
            //FULL OUTER JOIN   TABLE_CONTACTOS
        getServerInfo();
        String rawQuery = "SELECT * FROM " +
                DataUpload.TABLE_CONTACTOS +
                " JOIN " + DataUpload.TABLE_USUARIOS_CONTACTOS
                + " ON " +
                DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_ID_USUARIO + " = " +
                DataUpload.TABLE_USUARIOS_CONTACTOS+"."+DataUpload.COLUMN_ID_USUARIO + " AND "+
                DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_PKEY + " = " +
                DataUpload.TABLE_USUARIOS_CONTACTOS+"."+DataUpload.COLUMN_ID_CONTACTO + " AND "+
                DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_ID_ENCUESTA + " = " +
                DataUpload.TABLE_USUARIOS_CONTACTOS+"."+DataUpload.COLUMN_ID_ENCUESTA
                + " WHERE " +
                DataUpload.TABLE_CONTACTOS+"."+DataUpload.COLUMN_ID_USUARIO + " = " +  DataUpload.USER_ID + " AND "+
                DataUpload.TABLE_USUARIOS_CONTACTOS+"."+DataUpload.COLUMN_ID_USUARIO + " = " +  DataUpload.USER_ID + " AND "+
                DataUpload.TABLE_USUARIOS_CONTACTOS+"."+DataUpload.COLUMN_ID_ENCUESTA + " = " +  id + " AND "+
                DataUpload.TABLE_USUARIOS_CONTACTOS+"."+DataUpload.COLUMN_ID_ENCUESTA_ESTADO + " = 0 "
                +" ORDER BY " +  DataUpload.TABLE_CONTACTOS+"."+DataUpload.COLUMN_NOMBRE +" ASC";
        Cursor cursor = this.getReadableDatabase().rawQuery(rawQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            Log.v("VALUE FISRT",String.valueOf(cursor.getCount()));
            return cursor;
        }
        return null;
    }
    public  Cursor getAllClientesIDlike(String id, String name){
        //FULL OUTER JOIN
        if (name.toString().equals("")  &&  name.length() == 0) {
            Cursor cursor = getAllClientesID(id);
            if (cursor != null && cursor.getCount() > 0) {return cursor;}
        } else {
            String rawQuery = "SELECT * FROM " +
                    DataUpload.TABLE_CONTACTOS +
                    " JOIN " + DataUpload.TABLE_USUARIOS_CONTACTOS
                    + " ON " +
                    DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_ID_USUARIO + " = " +
                    DataUpload.TABLE_USUARIOS_CONTACTOS+"."+DataUpload.COLUMN_ID_USUARIO + " AND "+
                    DataUpload.TABLE_CONTACTOS+"."+ DataUpload.COLUMN_PKEY + " = " +
                    DataUpload.TABLE_USUARIOS_CONTACTOS+"."+DataUpload.COLUMN_ID_CONTACTO
                    + " WHERE " +
                    DataUpload.TABLE_CONTACTOS+"."+DataUpload.COLUMN_ID_USUARIO + " = " +  DataUpload.USER_ID + " AND "+
                    DataUpload.TABLE_USUARIOS_CONTACTOS+"."+DataUpload.COLUMN_ID_USUARIO + " = " +  DataUpload.USER_ID + " AND "+
                    DataUpload.TABLE_USUARIOS_CONTACTOS+"."+DataUpload.COLUMN_ID_ENCUESTA + " = " +  id + " AND "+
                    DataUpload.TABLE_USUARIOS_CONTACTOS+"."+DataUpload.COLUMN_ID_ENCUESTA_ESTADO + " = 0 AND " +
                    DataUpload.TABLE_CONTACTOS+"."+DataUpload.COLUMN_NOMBRE + " LIKE '"+ name + "%' "+
                            " ORDER BY " +  DataUpload.TABLE_CONTACTOS+"."+DataUpload.COLUMN_NOMBRE +" ASC";
            Cursor cursor = this.getReadableDatabase().rawQuery(rawQuery, null);
            if (cursor != null && cursor.getCount() > 0) {
                Log.v("VALUE SEARCH",String.valueOf(cursor.getCount()));
                return cursor;}
        }
        return null;
    }
    public  Cursor getClientesID(String id){
        String rawQuery = "SELECT " +
                DataUpload.COLUMN_PKEY + ","+
                DataUpload.COLUMN_ID_ENCUESTA + ","+
                DataUpload.COLUMN_ID_USUARIO + ","+
                DataUpload.COLUMN_NOMBRE + ","+
                DataUpload.COLUMN_PATERNO + ","+
                DataUpload.COLUMN_MATERNO + ","+
                DataUpload.COLUMN_CALLE + ","+
                DataUpload.COLUMN_COLONIA + ","+
                DataUpload.COLUMN_NUM_EXT + ","+
                DataUpload.COLUMN_NUM_INT + ","+
                DataUpload.COLUMN_COD_POSTAL + ","+
                DataUpload.COLUMN_ENTIDAD + ","+
                DataUpload.COLUMN_MUNICIPIO + ","+
                DataUpload.COLUMN_FECHA_NAC + ","+
                DataUpload.COLUMN_GENERO + ","+
                DataUpload.COLUMN_VERSION
                +" FROM " +
                DataUpload.TABLE_CONTACTOS
                + " WHERE " +
                DataUpload.TABLE_CONTACTOS+"."+DataUpload.COLUMN_PKEY + " = '" +  id+ "'";
        Cursor cursor = this.getReadableDatabase().rawQuery(rawQuery, null);
        if (cursor != null && cursor.getCount() > 0) {
            Log.v("VALUE FISRT",String.valueOf(cursor.getCount()));
            return cursor;
        }
        //return getAllClientsId(new String[]{DataUpload.USER_IDS_CONTACTS});
        return null;
    }

    public int getNumbersAskEncuesta(String id){
        Cursor cursor = this.getReadableDatabase().query(
                DataUpload.TABLE_PREGUNTAS,
                null,
                DataUpload.COLUMN_ID_ENCUESTA + "=?",
                new String[] { id},
                null, null, null, null);
        if (cursor != null && cursor.getCount() > 0){
            //MenuEncuestasFragment.NumPreg=cursor.getCount();
            //Log.v("1 NUMERO DE ELEMENTOS ",String.valueOf(cursor.getCount()));
            return cursor.getCount();
        }
        return 0;
    }

    private Cursor getAllClientsId(String[] id) {
        return this.getReadableDatabase().query(
                DataUpload.TABLE_CONTACTOS,
                null,
                DataUpload.COLUMN_PKEY+ "=?" ,
                id,
                null,
                null,
                null
        );
    }
    public Cursor getAllClients() {
        Cursor cursor = this.getReadableDatabase().query(DataUpload.TABLE_USUARIOS_CONTACTOS , null,
                DataUpload.COLUMN_ID_USUARIO + "=?",
                new String[] { DataUpload.USER_ID }, null, null, null, null);
        int n=0;
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                DataUpload.USER_IDS_CONTACTS = new String[cursor.getCount()];
                do {
                    DataUpload.USER_IDS_CONTACTS[n] = cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_ID_CONTACTO));
                    n++;
                } while (cursor.moveToNext());
            }
            return getAllDataClinetes(DataUpload.USER_IDS_CONTACTS);
        }
        return null;
        //return getAllDataClinetes(new String[]{"1","8"});
    }
    public void  getAllClientsData() {
        Cursor cursor = this.getReadableDatabase().query(DataUpload.TABLE_CONTACTOS , null,
                null,
                null, null, null, null, null);
        int n=0;
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                //DataUpload.USER_IDS_CONTACTS = new String[cursor.getCount()];
                do {
                    //DataUpload.USER_IDS_CONTACTS[n] = cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_ID_CONTACTO));
                    //Log.v("ID_ENCUESTA_ESTADO: ",cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_ID_ENCUESTA_ESTADO)));
                    //Log.v("ID_ENCUESTA: ",cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_ID_ENCUESTA)));
                    //Log.v("Nombre: ",cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_NOMBRE)));
                    //Log.v("ID_PRECARGA: ",cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_ID_PRECARGA)));
                    //Log.v("PKEY: ",cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_PKEY)));
                    n++;
                } while (cursor.moveToNext());
            }
        }
        //return getAllDataClinetes(new String[]{"1","8"});
    }

    private Cursor getAllDataClinetes(String[] id) {
        return this.getReadableDatabase().query(
                DataUpload.TABLE_CONTACTOS,
                null,
                DataUpload.COLUMN_PKEY + " IN " + buildInClause(id),
                id,
                null,
                null,
                null
        );
    }
    private static StringBuilder buildInClause(String[] myStringArray){
        StringBuilder fullString=new StringBuilder();
        fullString.append("(");
        for(int i=0;i<myStringArray.length;i++){
            fullString.append("?");
            if(i!=myStringArray.length-1){
                fullString.append(",");
            }
        }
        fullString.append(")");
        return fullString;
    }
    public Cursor getPreguntaId(String id) {
        Cursor cursor = this.getReadableDatabase().query(
                DataUpload.TABLE_PREGUNTAS ,
                null,
                DataUpload.COLUMN_ID_ENCUESTA + " LIKE ?",
                new String[] { id},
                null, null, null, null);
        DataUpload.pregList= new ArrayList<String>();
        DataUpload.ptypeList= new ArrayList<String>();
        DataUpload.pIDList= new ArrayList<String>();
        if (cursor != null && cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    DataUpload.pregList.add(cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_PREGUNTA)));
                    DataUpload.ptypeList.add(cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_ID_TIPO_PREGUNTA)));
                    DataUpload.pIDList.add(cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_PKEY)));
                } while (cursor.moveToNext());
            }
            return this.getReadableDatabase().query(
                    DataUpload.TABLE_TIPO_PREGUNTA,
                    null,
                    DataUpload.COLUMN_PKEY + " LIKE ?",
                    new String[]{DataUpload.ptypeList.get(0)},
                    null,
                    null,
                    null
            );
        }
        return null;
    }

    public Cursor getPreguntaIdIn(String id) {
        return this.getReadableDatabase().query(
                DataUpload.TABLE_TIPO_PREGUNTA,
                null,
                DataUpload.COLUMN_PKEY + " LIKE ?",
                new String[]{id},
                null,
                null,
                null
        );
    }
    public Cursor getRespuestasPId(String id){
        return this.getReadableDatabase().query(
                DataUpload.TABLE_RESPUESTAS,
                null,
                DataUpload.COLUMN_ID_PREGUNTA + " LIKE ?",
                new String[]{id},
                null,
                null,
                null
        );
    }
    public Cursor getEstadoEncuesta(){
        return this.getReadableDatabase().query(
                DataUpload.TABLE_ENCUESTAS_ESTADO,
                null,
                DataUpload.COLUMN_PKEY
                        + " IN(?,?,?,?,?,?)",
                new String[]{"3","4","5","6","7","10"},
                null,
                null,
                null
        );
    }

    private void getEncuestasIdSeenLasstEncuestas(){
        String rawQuery2 = "SELECT " +
                DataUpload.TABLE_ENCUESTAS+"."+ DataUpload.COLUMN_PKEY + " AS pkey_en," +
                DataUpload.TABLE_ENCUESTAS+"."+ DataUpload.COLUMN_ENCUESTA+ " AS name_en"
                +" FROM " +
                DataUpload.TABLE_USUARIOS_ENCUESTAS +
                " JOIN " +
                DataUpload.TABLE_ENCUESTAS
                + " ON " +
                DataUpload.TABLE_USUARIOS_ENCUESTAS+"."+DataUpload.COLUMN_ID_ENCUESTA
                + " = " +
                DataUpload.TABLE_ENCUESTAS+"."+DataUpload.COLUMN_PKEY
                + " WHERE " +
                DataUpload.TABLE_USUARIOS_ENCUESTAS+"."+DataUpload.COLUMN_ID_USUARIO
                + " = " +
                DataUpload.USER_ID
                +" AND "+
                DataUpload.TABLE_ENCUESTAS+"."+DataUpload.COLUMN_CADUCIDAD
                + " = '"+DataUpload.VALUE_TIME +"'";
        Log.v("Consult",rawQuery2);
        Cursor cursorS = this.getReadableDatabase().rawQuery(rawQuery2,null);
        if (cursorS != null && cursorS.getCount() > 0) {
            Log.v("Valor","La encuesta caduco enviando encuestas en cola");
            if (cursorS.moveToFirst()) {
                do {
                    DataUpload.USER_ACCOUNT_ENC_ID=cursorS.getString(
                            cursorS.getColumnIndex("pkey_en"));
                    Log.v("USER_ACCOUNT_ENC_ID",cursorS.getString(
                            cursorS.getColumnIndex("pkey_en")));
                    DataUpload.USER_ACCOUNT_ENC_NAME=cursorS.getString(
                            cursorS.getColumnIndex("name_en"));
                    BackupSyncEncuestaResultCall();
                    if(DataUpload.SYNC_COLUMNS_DATA_RESERVATION != null && DataUpload.SYNC_COLUMNS_DATA_RESERVATION.getCount() > 0) {
                        EncRealizadasFragmennt.InitializeSQLCipher();
                    }
                } while (cursorS.moveToNext());
            }
            DataUpload.ShowPushNotification(c,"La encuesta caduco","Se enviaron encuestas pendientes.", DataUpload.eTypeIcon.SucessfulSync);
        }
    }

    private void getDeleteActions(){
        Cursor cursorr = this.getReadableDatabase().query(
                DataUpload.TABLE_DELETE_COMMAND,
                null,
                DataUpload.COLUMN_ID_USUARIO
                        + "=? ",
                new String[]{DataUpload.USER_ID},
                null,
                null,
                null
                );
        if (cursorr != null && cursorr.getCount() > 0) {
            if (cursorr.moveToFirst()) {
                do {
                    String action=cursorr.getString(cursorr.getColumnIndex(DataUpload.COLUMN_ID_COMMAND));
                    String user=cursorr.getString(cursorr.getColumnIndex(DataUpload.COLUMN_ID_USUARIO));
                    String application=cursorr.getString(cursorr.getColumnIndex(DataUpload.COLUMN_DATE_APPLICATION));
                    String date=(DateFormat.format("yyyy-MM-dd HH:mm:ss", new java.util.Date()).toString());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String firstStr = date;
                    Date first = null;
                    Date second = null;
                    try {
                        first = sdf.parse(firstStr);
                        second = sdf.parse(application);
                    } catch (ParseException e) {
                        Log.v("Valor", "ERROR EN " + e.getMessage());
                        e.printStackTrace();
                    }
                    boolean before = (first.before(second));
                    if(before){
                        //---------Log.v("Valor", "La fecha " + second.toString() + " no ha pasado, hoy es " + first.toString());
                    }
                    else {
                        //---------Log.v("Valor", "La fecha " + second.toString() + " ya ha pasado, hoy es " +first.toString());
                        //---------Execution action delete
                        if(action.equals("1")){
                            DeleteContactStateSync(user);
                            //---Eliminados lo relacionado ha ese usuario
                        }else if(action.equals("2")){
                            //---Eliminados completamente un reset
                            DeleteAllResultStateSync();
                        }
                    }
                } while (cursorr.moveToNext());
            }
        }

    }

    public Cursor getEncuestasId() {
        DataUpload.VALUE_TIME=(DateFormat.format("yyyy-MM-dd", new java.util.Date()).toString());
        //Enviar resultados siempre y cuando haiga conexión a internet de la encuesta caduca
        if (DataUpload.CONNETION_ALLOWED) {
            getEncuestasIdSeenLasstEncuestas();
            DataUpload.CONNETION_ALLOWED=false;
        }
        //Verficar acciones de eliminado de datos
        getDeleteActions();
        String rawQuery = "SELECT * FROM " +
                DataUpload.TABLE_USUARIOS_ENCUESTAS +
                " JOIN " +
                DataUpload.TABLE_ENCUESTAS
                + " ON " +
                DataUpload.TABLE_USUARIOS_ENCUESTAS+"."+DataUpload.COLUMN_ID_ENCUESTA
                + " = " +
                DataUpload.TABLE_ENCUESTAS+"."+DataUpload.COLUMN_PKEY
                + " WHERE " +
                DataUpload.TABLE_USUARIOS_ENCUESTAS+"."+DataUpload.COLUMN_ID_USUARIO
                + " = " +
                DataUpload.USER_ID
                +" AND "+
                " date('"+
                DataUpload.VALUE_TIME +"')"
                + " < date("+DataUpload.TABLE_ENCUESTAS+"."+DataUpload.COLUMN_CADUCIDAD +")";
        Log.v("Consult",rawQuery);
        Cursor cursor = this.getReadableDatabase().rawQuery(rawQuery,null);
        if (cursor != null && cursor.getCount() > 0) {
           return cursor;
        }
        return null;
        //return choiseEncuestasData(new String[]{"1","3"});
    }
    private Cursor choiseEncuestasData(String[] id) {
        return this.getReadableDatabase().query(
                DataUpload.TABLE_ENCUESTAS,
                null,
                DataUpload.COLUMN_PKEY + " IN" + buildInClause(id),
                id,
                null,
                null,
                null
        );
    }
    public Cursor getAllEncuestas() {
        String selectQuery = "SELECT * FROM " + DataUpload.TABLE_ENCUESTAS ;
        return this.getReadableDatabase().rawQuery(selectQuery, null);
    }

    public Cursor getAllUsuarios() {
        String selectQuery = "SELECT * FROM " + DataUpload.TABLE_USUARIOS ;
        return this.getReadableDatabase().rawQuery(selectQuery, null);
    }
}
