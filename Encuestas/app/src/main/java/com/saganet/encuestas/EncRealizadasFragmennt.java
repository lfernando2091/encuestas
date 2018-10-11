package com.saganet.encuestas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EncRealizadasFragmennt.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EncRealizadasFragmennt#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EncRealizadasFragmennt extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    //private String mParam1;
    //private String mParam2;
    private static DataSesion dbEncuestaRealizada;
    private ListView mEncuestasList;
    private EditText mSearch;
    private EncuestasRealizadasCursorAdapt mCursorAdapter;
    private Toolbar bar;
    private static ProgressDialog lod = null;
    private FloatingActionButton fab;
    private ProgressDialog pd = null;
    private static ArrayList<String> htListUpdate;
    private static String ActualIDUpdateSyncState="";
    private static String ActualIDContactoUpdateSyncState="";
    private boolean HasItems=false;
    private static String FolioSyncRequest="";
    private static boolean ContactUploadCreated=false;
    private static boolean REQUEST_START_IN_LOGING=false;

    //private OnFragmentInteractionListener mListener;
    public EncRealizadasFragmennt() {
        // Required empty public constructor
    }
    public static EncRealizadasFragmennt newInstance() {
        return new EncRealizadasFragmennt();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EncRealizadasFragmennt.
     */
    // TODO: Rename and change types and number of parameters
    public static EncRealizadasFragmennt newInstance(String param1, String param2) {
        EncRealizadasFragmennt fragment = new EncRealizadasFragmennt();
        Bundle args = new Bundle();
        //args.putString(ARG_PARAM1, param1);
        //args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //mParam1 = getArguments().getString(ARG_PARAM1);
            //mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root= inflater.inflate(R.layout.fragment_enc_realizadas_fragmennt, container, false);
        // Instancia de helper
        dbEncuestaRealizada = new DataSesion(getActivity());
        bar=(Toolbar)  getActivity().findViewById(R.id.toolbar_enc);
        bar.setTitle(DataUpload.USER_ACCOUNT_ENC_NAME);
        // Referencias UI
        mEncuestasList = (ListView) root.findViewById(R.id.encuestas_list);
        mCursorAdapter = new EncuestasRealizadasCursorAdapt(getActivity(), null);
        mEncuestasList.setAdapter(mCursorAdapter);
        mEncuestasList.setTextFilterEnabled(true);
        mCursorAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                Log.d("Data --Cursor", "Filter:"+constraint.toString());
                return dbEncuestaRealizada.getEncuestasAnsIdCompleteStructure(constraint.toString());
            }
        });
        mSearch= (EditText) getActivity().findViewById(R.id.text_search_real_enc);
        mSearch.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if (mUseAdapter!=null) {
                Log.d("Data", "Filter:"+s);
                mCursorAdapter.swapCursor(null);
                mCursorAdapter.getFilter().filter(s);
                //}
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        // Eventos
        mEncuestasList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentItem = (Cursor) mCursorAdapter.getItem(i);
                //Log.v("getItem(i)",String.valueOf(i)) ;
                DataUpload.ID_REDITABLE_ENCUESTA_ID=DataUpload.ID_REDITABLE_ENCUESTA_LIST.get(i);
                DataUpload.IS_REDITABLE_ENCUESTA_ID=true;
                DataUpload.USER_ACCOUNT_ID_PKEY=currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_PKEY+ "_UC"));
                String currentLawyerId = currentItem.getString(
                        currentItem.getColumnIndex("pkey_c"));
                String currentName = currentItem.getString(
                        currentItem.getColumnIndex("nombre_c"));
                currentName = currentName + " "+ currentItem.getString(
                        currentItem.getColumnIndex("paterno_c"));
                currentName = currentName + " "+ currentItem.getString(
                        currentItem.getColumnIndex("materno_c"));
                DataUpload.USER_ACCOUNT_ID=currentLawyerId;
                ContactInfoFragment.sIdContactoUs=currentLawyerId;
                ContactInfoFragment.sIdPrecargaUs=currentItem.getString(
                        currentItem.getColumnIndex("id_precarga_c"));
                ContactInfoFragment.sNombreEncuestaUs=DataUpload.USER_ACCOUNT_ENC_NAME;
                ContactInfoFragment.sNombreUs=currentName;
                DataUpload.USER_NAME=currentName;
                ContactInfoFragment.sCalleUs=currentItem.getString(
                        currentItem.getColumnIndex("calle_c"));
                ContactInfoFragment.sColoniaUs=currentItem.getString(
                        currentItem.getColumnIndex("colonia_c"));
                ContactInfoFragment.sNumExtUs=currentItem.getString(
                        currentItem.getColumnIndex("numero_ext_c"));
                ContactInfoFragment.sNumIntUs=currentItem.getString(
                        currentItem.getColumnIndex("numero_int_c"));
                ContactInfoFragment.sCodPostalUs=currentItem.getString(
                        currentItem.getColumnIndex("codigo_postal_c"));
                ContactInfoFragment.sEntidadUs=currentItem.getString(
                        currentItem.getColumnIndex("entidad_c"));
                ContactInfoFragment.sMunicipioUs=currentItem.getString(
                        currentItem.getColumnIndex("municipio_c"));
                ContactInfoFragment.sFechNacUs=currentItem.getString(
                        currentItem.getColumnIndex("fecha_nacimiento_c"));
                ContactInfoFragment.sGeneroUs=currentItem.getString(
                        currentItem.getColumnIndex("genero_c"));
                if(currentItem.getString(currentItem.getColumnIndex("id_estado_re_en")).equals("2")){
                    showScreen(true);
                }
            }
        });
        fab = (FloatingActionButton) getActivity().findViewById(R.id.fabsyncencuestaresp);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getStateNetworkConnection()){
                    if(HasItems){
                        new AsyncData().execute();
                        Snackbar.make(view, "Datos sincronizados", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                    else {
                        //--Toast.makeText(getActivity(), "No hay encuestas pendientes por subir.", Toast.LENGTH_SHORT).show();
                        Snackbar.make(view, "No hay encuestas pendientes por subir.", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                }
                else {
                    //---Toast.makeText(getActivity(), "No conexión a internet.", Toast.LENGTH_SHORT).show();
                    Snackbar.make(view, "No conexión a internet.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        //mAddButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        new LoadTaskId().execute();
        return root;
    }

    private boolean getStateNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    private class LoadTaskId extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(getActivity());
            pd.setTitle("Cargando...");
            pd.setMessage("Espere por favor.");
            pd.setCancelable(false);
            pd.show();
        }
        @Override
        protected Cursor doInBackground(Void... voids) {
            //return dbEncuestaRealizada.getEncuestasResultId();
            Cursor c=null;
            try {
                c= dbEncuestaRealizada.getEncuestasAnsIdCompleteStructure("");
            }catch (Exception e){
                Log.v("Valor", e.getMessage());
            }
            return c;
        }
        @Override
        protected void onPostExecute(Cursor cursor) {
            if (pd != null) {
                if (pd.isShowing()) {
                  pd.dismiss();
                }
                pd = null;
            }
            HasItems=false;
            if (cursor != null && cursor.getCount() > 0) {
                try {
                    HasItems=true;
                    //--DataUpload.USER_TYPE_RESULT=1;
                    mCursorAdapter.swapCursor(cursor);
                } catch (Exception e) {
                    Log.v("Adaptacion : ",e.getMessage());
                }
            } else {
                mCursorAdapter.swapCursor(null);
                // Mostrar empty state
            }
        }
    }

    private void showScreen(Boolean b){
        if(!b){
            getActivity().setResult(Activity.RESULT_CANCELED);
        }else {
            getActivity().setResult(Activity.RESULT_OK);
        }
        ///ABRIR EL CUESTIONARIO CORRESPONDIENTE
        getActivity().finish();
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private class AsyncData extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            showprogress("Subiendo...","Archivos pendientes espere unos minutos." );
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            boolean v=false;
            dbEncuestaRealizada.BackupSyncEncuestaResultCall();
            if(DataUpload.SYNC_COLUMNS_DATA_RESERVATION != null && DataUpload.SYNC_COLUMNS_DATA_RESERVATION.getCount() > 0) {
                Log.v("Valor"," inciando cipher");
                v= InitializeSQLCipher();
            }
            return v;
        }
        @Override
        protected void onPostExecute(Boolean res) {
            hideprogress();
            if(!res){
                Toast.makeText(getActivity(), "No hay encuestas pendientes por subir.", Toast.LENGTH_SHORT).show();
            }
            if(REQUEST_START_IN_LOGING){
                DataUpload.FINISH_ALL_AND_RESTART_LOGING=true;
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            }
            else {
                new LoadTaskId().execute();
                DataUpload.AlertSoundVibrate(getActivity(), 1000);
            }
        }
    }

    private void showprogress(String t, String c){
        lod = new ProgressDialog(getActivity());
        lod.setTitle(t);
        lod.setMessage(c);
        lod.setCancelable(false);
        lod.show();
    }

    private void hideprogress(){
        if (lod != null) {
            if (lod.isShowing()) {
                lod.dismiss();
            }
            lod = null;
        }
    }

    public static boolean InitializeSQLCipher() {
        //ndroid.database.sqlite.SQLiteDatabase.loadLibs(this);
        File dir= new File(DataUpload.LOCATION_DATA_SYNC);
        //Get the text file
        File databaseFile = new File(dir,DataUpload.DATA_BASE_NAME_SYNC+
                DataUpload.USER_ID
                + DataUpload.DATA_BASE_EXTENTION);
        //databaseFile.mkdirs();
        if(databaseFile.exists()){
            databaseFile.delete();
        }
        String CREATE_RESPUESTA_ENCUESTA = "CREATE TABLE IF NOT EXISTS " +
                DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID+
                DataUpload.USER_ACCOUNT_ENC_ID +
                "(" +
                DataUpload.COLUMN_PKEY + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DataUpload.COLUMN_ID_USUARIO + " INTEGER NOT NULL, " +
                DataUpload.COLUMN_ID_CONTACTO + " TEXT NOT NULL, " +
                DataUpload.COLUMN_ID_ENCUESTA + " INTEGER NOT NULL, "+
                DataUpload.COLUMN_ID_ENCUESTA_ESTADO + " INTEGER NOT NULL, "+
                DataUpload.COLUMN_ENCUESTA_INICIO + " TEXT NOT NULL, " +
                DataUpload.COLUMN_ENCUESTA_FIN + " TEXT NOT NULL, " +
                DataUpload.COLUMN_ENCUESTA_LATITUD + " TEXT NOT NULL, " +
                DataUpload.COLUMN_ENCUESTA_LONGITUD+ " TEXT NOT NULL, " +
                DataSesion.buildColumnsDinamically(DataUpload.SYNC_COLUMNS_DYMANICALLI_BLUIT).toString()+
                ")";
        //        android.database.sqlite.SQLiteDatabase databases = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(databaseFile, DataUpload.DATA_BASE_SECURITY_SYNC, null);
        android.database.sqlite.SQLiteDatabase databases = android.database.sqlite.SQLiteDatabase.openOrCreateDatabase(databaseFile, null);
        databases.execSQL(CREATE_RESPUESTA_ENCUESTA);
        //database.execSQL("insert into t1(a, b) values(?, ?)", new Object[]{"one for the money", "two for the show"});
        InsertDataToSyncUpload(databases,databaseFile);
        databases.close();
        DataUpload.SYNC_COLUMNS_DATA_RESERVATION.close();
        DataUpload.SYNC_COLUMNS_DATA_RESERVATION=null;
        if(databaseFile.exists()){
            databaseFile.delete();
        }
        //UPLOAD_DATABASE_SYNC(databaseFile);
        //-----UPLOAD_DATABASE_FILE_FTP(databaseFile);
        return true;
    }

    private static boolean ValidateNewContactValues(android.database.sqlite.SQLiteDatabase database,String s){
        boolean ActionNewUserLocal=false;
        //Comprovar si el contacto es nuevo (agregado manualmente)
        try {
            int val= Integer.parseInt(s);
            ActionNewUserLocal=false;
        }catch (Exception e){
            ActionNewUserLocal=true;
        }
        if(ActionNewUserLocal){
            //Metodo cuando el usuario sea nuevo y id=04hjf-dshywer0-der-rewr-6759h
            String CREATE_CONTACTS_UPLOAD= "CREATE TABLE IF NOT EXISTS " +
                    DataUpload.TABLE_CONTACTOS +
                    "(" +
                    DataUpload.COLUMN_PKEY + " TEXT PRIMARY KEY, " +
                    DataUpload.COLUMN_ID_ENCUESTA + " INTEGER NOT NULL, " +
                    DataUpload.COLUMN_ID_USUARIO + " INTEGER NOT NULL, " +
                    DataUpload.COLUMN_NOMBRE + " TEXT NOT NULL, "+
                    DataUpload.COLUMN_PATERNO + " TEXT NOT NULL, "+
                    DataUpload.COLUMN_MATERNO + " TEXT NOT NULL, " +
                    DataUpload.COLUMN_CALLE + " TEXT NOT NULL, " +
                    DataUpload.COLUMN_COLONIA + " TEXT NOT NULL, " +
                    DataUpload.COLUMN_NUM_EXT + " TEXT NOT NULL, " +
                    DataUpload.COLUMN_NUM_INT + " TEXT NOT NULL, " +
                    DataUpload.COLUMN_COD_POSTAL + " TEXT NOT NULL, " +
                    DataUpload.COLUMN_ENTIDAD + " TEXT NOT NULL, " +
                    DataUpload.COLUMN_MUNICIPIO + " TEXT NOT NULL, " +
                    DataUpload.COLUMN_FECHA_NAC+ " INTEGER NOT NULL, " +
                    DataUpload.COLUMN_GENERO + " INTEGER NOT NULL, " +
                    DataUpload.COLUMN_VERSION+ " INTEGER NOT NULL"+
                    ")";
            database.execSQL(CREATE_CONTACTS_UPLOAD);
            Cursor c=dbEncuestaRealizada.getClientesID(s);
            if (c != null && c.getCount() > 0) {
                if (c.moveToFirst()) {
                    do {
                        tContactos con=new tContactos(
                                c.getString(c.getColumnIndex(DataUpload.COLUMN_PKEY)),
                                Integer.parseInt(c.getString(c.getColumnIndex(DataUpload.COLUMN_ID_ENCUESTA))),
                                Integer.parseInt(c.getString(c.getColumnIndex(DataUpload.COLUMN_ID_USUARIO))),
                                c.getString(c.getColumnIndex(DataUpload.COLUMN_NOMBRE)),
                                c.getString(c.getColumnIndex(DataUpload.COLUMN_PATERNO)),
                                c.getString(c.getColumnIndex(DataUpload.COLUMN_MATERNO)),
                                c.getString(c.getColumnIndex(DataUpload.COLUMN_CALLE)),
                                c.getString(c.getColumnIndex(DataUpload.COLUMN_COLONIA)),
                                c.getString(c.getColumnIndex(DataUpload.COLUMN_NUM_EXT)),
                                c.getString(c.getColumnIndex(DataUpload.COLUMN_NUM_INT)),
                                Integer.parseInt(c.getString(c.getColumnIndex(DataUpload.COLUMN_COD_POSTAL))),
                                c.getString(c.getColumnIndex(DataUpload.COLUMN_ENTIDAD)),
                                c.getString(c.getColumnIndex(DataUpload.COLUMN_MUNICIPIO)),
                                c.getString(c.getColumnIndex(DataUpload.COLUMN_FECHA_NAC)),
                                c.getString(c.getColumnIndex(DataUpload.COLUMN_GENERO)),
                                Integer.parseInt(c.getString(c.getColumnIndex(DataUpload.COLUMN_VERSION)))
                        );
                        Log.v("Valor ","Contacto agregado valor " +  c.getString(c.getColumnIndex(DataUpload.COLUMN_PKEY)));
                        database.insert(DataUpload.TABLE_CONTACTOS,null,con.toContentUpload());
                    } while (c.moveToNext());
                }
            }
            //---ActionNewUserLocal=false;
        } else {
            ActionNewUserLocal=false;
        }
        return ActionNewUserLocal;
    }

    public static void InsertDataToSyncUpload(android.database.sqlite.SQLiteDatabase database, final File sourceFile){
        int rowcount = 0, colcount = 0;
        String id_counter="";
        if(DataUpload.SYNC_COLUMNS_DATA_RESERVATION != null && DataUpload.SYNC_COLUMNS_DATA_RESERVATION.getCount() > 0) {
            rowcount = DataUpload.SYNC_COLUMNS_DATA_RESERVATION.getCount();
            colcount = DataUpload.SYNC_COLUMNS_DATA_RESERVATION.getColumnCount();
            for (int i = 0; i < rowcount; i++) {
                //---String d= String.valueOf(i+1);
                //---lod.setMessage("Archivo " + d + " de " + rowcount);
                ArrayList<String> genesis= new ArrayList<String>();
                ArrayList<String> ultimate= new ArrayList<String>();
                ultimate.add(0,"null");
                DataUpload.SYNC_COLUMNS_DATA_RESERVATION.moveToPosition(i);
                for (int j = 0; j < colcount; j++) {
                    if(j<=11){
                        genesis.add(DataUpload.SYNC_COLUMNS_DATA_RESERVATION.getString(j));
                    } else{
                        ultimate.add(DataUpload.SYNC_COLUMNS_DATA_RESERVATION.getString(j));
                    }}
                //MODIFIERS DATA CURRENT
                id_counter=DataUpload.SYNC_COLUMNS_DATA_RESERVATION.getString(
                        DataUpload.SYNC_COLUMNS_DATA_RESERVATION.getColumnIndex(DataUpload._ID));
                //Acciones para nuevo contacto agregado manualmente id=123jbsijo-234sdf-werijj34-te
                if (ValidateNewContactValues(database,genesis.get(3))) {
                    ContactUploadCreated=true;
                    Log.v("Valor ","Contacto agregado manualmente " + String.valueOf(ContactUploadCreated));
                }else {
                    ContactUploadCreated=false;
                    Log.v("Valor ","Contacto no agregado manualmente " + String.valueOf(ContactUploadCreated));
                }
                tRespuestasEncuesta tre= new tRespuestasEncuesta(
                        genesis.get(0),
                        Integer.parseInt(genesis.get(2)),
                        genesis.get(3),
                        Integer.parseInt(genesis.get(4)),
                        Integer.parseInt(genesis.get(5)),
                        genesis.get(6),
                        genesis.get(7),
                        genesis.get(8),
                        genesis.get(9),
                        ultimate);
                database.insert(DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID+ DataUpload.USER_ACCOUNT_ENC_ID,null,tre.toContentUpload());
                ActualIDUpdateSyncState=genesis.get(1).toString();
                ActualIDContactoUpdateSyncState=genesis.get(3).toString();
                UPLOAD_DATABASE_FILE_FTP(sourceFile, id_counter);
                boolean tx=true, delete_reg_error=true;
                int reload=0;
                do{
                        Log.v("Valor","Enviando primer aviso verificar estado archivo");
                        //--Enviarle al server si el archivo esta cargado
                        POST_SEND_DATA_SERVER(genesis.get(0), id_counter, true);
                    //--Leer la notificación del archivo
                    if(!READ_DIRECTORY_JSON(true, DataUpload.TABLE_SEND_DATA_SERVER)){
                        //--Realizar accion cuando el server responda como negativo a la existencia del archivo
                        //--Si el server retorna (*3*) entonces la carga se realizo,
                        //--si es (*10*) enotonces no existe el archivo en el server
                        Log.v("Valor","Error reenviando archivo--intento " +id_counter);
                        UPLOAD_DATABASE_FILE_FTP(sourceFile, id_counter);
                        reload+=1;tx=true;delete_reg_error=true;
                    } else {
                        Log.v("Valor","Enviando segundo aviso procesar");
                        //--Notificar al server de procesar archivo enviado
                        POST_SEND_DATA_SERVER(genesis.get(0), id_counter, false);
                        if(!READ_DIRECTORY_JSON_PROCESS_SERVER(true, DataUpload.TABLE_SEND_PROCESS_SERVER)){
                            //--Si el server devuelve (*7*) enonces enviarle de nuevo el paquete
                            Log.v("Valor","Error reenviando archivo por error insercion--intento " + id_counter);
                            UPLOAD_DATABASE_FILE_FTP(sourceFile, id_counter);
                            reload+=1;tx=true;delete_reg_error=true;
                        }else {
                            Log.v("Valor","terminado y guardado");
                            //eliminamos registro
                            database.delete(DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID+
                                    DataUpload.USER_ACCOUNT_ENC_ID,DataUpload.COLUMN_PKEY +"=?",new String[]{genesis.get(0)});
                            if (ContactUploadCreated) {
                                ContactUploadCreated=false;
                            }
                            database.execSQL("DROP TABLE IF EXISTS " + DataUpload.TABLE_CONTACTOS);
                            tx=false;reload=4;
                        }
                    }
                }while(tx && reload<4);

                //Eliminamos registro aun cuando se genere un exception e
                if(delete_reg_error){
                    //eliminamos registro
                    database.delete(DataUpload.TABLE_RESPUESTAS_ENCUESTA_ID+
                            DataUpload.USER_ACCOUNT_ENC_ID,DataUpload.COLUMN_PKEY +"=?",new String[]{genesis.get(0)});
                    if (ContactUploadCreated) {
                        ContactUploadCreated=false;
                    }
                    database.execSQL("DROP TABLE IF EXISTS " + DataUpload.TABLE_CONTACTOS);
                    delete_reg_error=false;
                }
            }
        }
    }

    public static boolean UPLOAD_DATABASE_FILE_FTP(final File sourceFile, String value){
        try {
            String date=(DateFormat.format("yyyy-MM-dd", new java.util.Date()).toString());
            FTPClient ftpClient = new FTPClient();
            ftpClient.connect(InetAddress.getByName(DataUpload.SYNC_URL_FTP_TRANSFER), DataUpload.SYNC_URL_FTP_TRANSFER_PORT);
            ftpClient.enterLocalActiveMode();
            ftpClient.login(DataUpload.SYNC_URL_FTP_TRANSFER_USER, DataUpload.SYNC_URL_FTP_TRANSFER_PASS);
            if(!ftpClient.changeWorkingDirectory("/"+date)){
                ftpClient.makeDirectory("/"+date);
            }
            ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            BufferedInputStream buffIn=null;
            buffIn=new BufferedInputStream(new FileInputStream(sourceFile));
            //ftpClient.enterLocalPassiveMode();
            ftpClient.enterLocalActiveMode();
            ftpClient.storeFile("/"+date + "/" + DataUpload.USER_ID + "_" + DataUpload.VALUE_IMEI_DATA_DEVICE +
                    "_"+value + ".sn", buffIn);
            try {if (ftpClient.isConnected()) {
                buffIn.close();
                ftpClient.logout();
                ftpClient.disconnect();
            }
            } catch (IOException ex) {
                ex.printStackTrace();
                return false;
            }
            return true;
        } catch (Exception e){
            Log.i("consola","Ups..." + e.getMessage());
            return  false;
        }
    }

    public static void POST_SEND_DATA_SERVER(String s,String value, boolean type) {
        try {
            URL url;
            if (type) {
                url = new URL(DataUpload.SYNC_URL_AVISO_REQUEST);
            }else {
                url = new URL(DataUpload.SYNC_URL_PROCESS_REQUEST);
            }
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(20000);
            urlConnection.setConnectTimeout(25000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("consecutivo_local", s)
                    .appendQueryParameter("nombre_archivo",  DataUpload.USER_ID + "_" + DataUpload.VALUE_IMEI_DATA_DEVICE +
                            "_"+value +".sn")
                    .appendQueryParameter("id_encuesta", DataUpload.USER_ACCOUNT_ENC_ID)
                    .appendQueryParameter("id_usuario", DataUpload.USER_ID)
                    .appendQueryParameter("imei", DataUpload.VALUE_IMEI_DATA_DEVICE)
                    .appendQueryParameter("longitud", DataUpload.VALUE_LONGITUDE_POSITION)
                    .appendQueryParameter("latitud", DataUpload.VALUE_LATITUDE_POSITION)
                    .appendQueryParameter("archivo_serial", s)
                    .appendQueryParameter("fecha", (DateFormat.format("yyyy-MM-dd", new java.util.Date()).toString()))
                    .appendQueryParameter("nuevo", ContactUploadCreated?"1":"0");
            String query = builder.build().getEncodedQuery();
            Log.v("Valor primer dato",query);
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            urlConnection.connect();
            File SDCardRoot = new File(DataUpload.LOCATION_DATA_SYNC);
            //TABLE_SEND_PROCESS_SERVER
            File file;
            if (type) {
                file = new File(SDCardRoot, DataUpload.TABLE_SEND_DATA_SERVER + ".json");
            }else {
                file = new File(SDCardRoot, DataUpload.TABLE_SEND_PROCESS_SERVER + ".json");
            }
            FileOutputStream fileOutput = new FileOutputStream(file);
            InputStream inputStream = urlConnection.getInputStream();
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;
            byte[] buffer = new byte[1024];
            int bufferLength = 0;
            while ((bufferLength = inputStream.read(buffer)) > 0) {
                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                //actualizaProgreso(downloadedSize, totalSize);
            }
            //cerramos
            fileOutput.close();
        } catch (MalformedURLException e) {
            Log.v("Valor error",e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            Log.v("Valor error",e.getMessage());
            e.printStackTrace();
        }
    }

    public static boolean READ_DIRECTORY_JSON(boolean isDeleted, String dat) {
        boolean res=false;
        File dir = new File(DataUpload.LOCATION_DATA_SYNC);
        //Get the text file
        File file = new File(dir, dat + ".json");
        if (file.exists()) {
            try {
                FileInputStream stream = new FileInputStream(file);
                String jsonStr = null;
                try {
                    FileChannel fc = stream.getChannel();
                    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                    jsonStr = Charset.defaultCharset().decode(bb).toString();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    stream.close();
                }
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray data = null;
                // Getting data JSON Array nodes
                try {data = jsonObj.getJSONArray(dat);
                } catch (Exception e) {}
                if (data != null) {
                    // looping through All nodes
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
                        String estatus = c.getString("estatus");
                        String descripcion = c.getString("descripcion");
                        //use >  int id = c.getInt("duration"); if you want get an int
                        if(estatus.toString().equals("3")){
                            FolioSyncRequest="0";
                            //--------------------------------------------------------------------------------
                            ///------------SAVE_DATA_JSON_DB(dat, htListUpdate,ActualIDUpdateSyncState,ActualIDContactoUpdateSyncState, "0");
                            Log.v("Valor archivo subido", descripcion);
                            res=true;
                        }else if(estatus.toString().equals("10")){
                            FolioSyncRequest="0";
                            //----SAVE_DATA_JSON_DB(dat, htListUpdate,ActualIDUpdateSyncState,ActualIDContactoUpdateSyncState,"0");
                            Log.v("Valor archivo no subido", descripcion);
                            res=false;
                        }
                            SAVE_DATA_JSON_DB(dat, estatus,ActualIDUpdateSyncState,ActualIDContactoUpdateSyncState,FolioSyncRequest);
                        // do what do you want on your interface
                    }
                }

                try{
                    JSONArray server  = jsonObj.getJSONArray(DataUpload.TABLE_DELETE_COMMAND);
                    boolean ActionDeleteAllInfoUserOneAction=true;
                    // looping through All nodes
                    for (int i = 0; i < server.length(); i++) {
                        JSONObject c = server.getJSONObject(i);
                        htListUpdate= new ArrayList<String>();
                        String id_usuario = c.getString("id_usuario");
                        String id_comando = c.getString("id_comando");
                        String fecha_aplicacion = c.getString("fecha_aplicacion");
                        String tipo = c.getString("tipo");
                        htListUpdate.add(id_usuario);
                        htListUpdate.add(id_comando);
                        htListUpdate.add(fecha_aplicacion);
                        htListUpdate.add(tipo);
                        SAVE_DATA(DataUpload.TABLE_DELETE_COMMAND, htListUpdate,ActionDeleteAllInfoUserOneAction);
                        if(ActionDeleteAllInfoUserOneAction)ActionDeleteAllInfoUserOneAction=false;
                        String date=(DateFormat.format("yyyy-MM-dd HH:mm:ss", new java.util.Date()).toString());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String firstStr = date;
                        Date first = sdf.parse(firstStr);
                        Date second = sdf.parse(fecha_aplicacion);
                        boolean before = (first.before(second));
                        if(before){
                            //---------Log.v("Valor", "La fecha " + second.toString() + " no ha pasado, hoy es " + first.toString());
                        }
                        else {
                            //---------Log.v("Valor", "La fecha " + second.toString() + " ya ha pasado, hoy es " +first.toString());
                            //---------Execution action delete
                            if(id_comando.equals("1")){
                                dbEncuestaRealizada.DeleteContactStateSync(id_usuario);
                                REQUEST_START_IN_LOGING=true;
                                //---Eliminados lo relacionado ha ese usuario
                            }else if(id_comando.equals("2")){
                                //---Eliminados completamente un reset
                                dbEncuestaRealizada.DeleteAllResultStateSync();
                                REQUEST_START_IN_LOGING=true;
                            }
                        }
                        htListUpdate.clear();
                    }
                }
                catch (Exception e){
                    Log.v("Valor", "ERROR EN " + e.getMessage());
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (isDeleted) {
            if (file.exists()) {
                file.delete();
            }
        }
        return res;
    }

    public static boolean READ_DIRECTORY_JSON_PROCESS_SERVER(boolean isDeleted, String dat) {
        boolean res=false;
        File dir = new File(DataUpload.LOCATION_DATA_SYNC);
        //Get the text file
        File file = new File(dir, dat + ".json");
        if (file.exists()) {
            try {
                FileInputStream stream = new FileInputStream(file);
                String jsonStr = null;
                try {
                    FileChannel fc = stream.getChannel();
                    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
                    jsonStr = Charset.defaultCharset().decode(bb).toString();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    stream.close();
                }
                JSONObject jsonObj = new JSONObject(jsonStr);
                JSONArray data = null;
                // Getting data JSON Array nodes
                try {data = jsonObj.getJSONArray(dat);
                } catch (Exception e) {}
                if (data != null) {
                    // looping through All nodes
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
                        String estatus = c.getString("estatus");
                        String estatus_folio = c.getString("folio_sincronizada");
                        String id_contacto_nuevo=c.getString("id_contacto_nuevo");
                        Log.v("Valor estado ", estatus);
                        if(estatus.equals("7")){
                            Log.v("Valor folio ", estatus_folio);
                            FolioSyncRequest=estatus_folio;
                            //------SAVE_DATA_JSON_DB(dat, htListUpdate,ActualIDUpdateSyncState,
                              //----      ActualIDContactoUpdateSyncState,estatus_folio);
                            Log.v("Valor procesado", estatus);
                            res=true;
                        }else if(estatus.equals("9")){
                            Log.v("Valor folio ", estatus_folio);
                            FolioSyncRequest=estatus_folio;
                            //----SAVE_DATA_JSON_DB(dat, htListUpdate,ActualIDUpdateSyncState,
                                    ///------ActualIDContactoUpdateSyncState, "0");
                            Log.v("Valor no procesado"," el server no pudo guadar registros "+ estatus);
                            res=false;
                        } else if(estatus.equals("10")){
                            Log.v("Valor folio ", estatus_folio);
                            FolioSyncRequest=estatus_folio;
                            Log.v("Valor procesado ", " repetido " + estatus);
                            res=true;
                        }
                        SAVE_DATA_JSON_DB(dat, estatus,ActualIDUpdateSyncState,
                                ActualIDContactoUpdateSyncState, FolioSyncRequest);
                        //Actualizar todos los datos el usuario principalmente su pkey
                        if(ContactUploadCreated){
                            if(!id_contacto_nuevo.equals("0")){
                                dbEncuestaRealizada.UpdateEncuestaResultContactoInfoSync(id_contacto_nuevo,ActualIDContactoUpdateSyncState,ActualIDUpdateSyncState);
                            }
                        }
                        // do what do you want on your interface
                    }
                }
                try{
                    JSONArray server  = jsonObj.getJSONArray(DataUpload.TABLE_DELETE_COMMAND);
                    boolean ActionDeleteAllInfoUserOneAction=true;
                    // looping through All nodes
                    for (int i = 0; i < server.length(); i++) {
                        JSONObject c = server.getJSONObject(i);
                        htListUpdate= new ArrayList<String>();
                        String id_usuario = c.getString("id_usuario");
                        String id_comando = c.getString("id_comando");
                        String fecha_aplicacion = c.getString("fecha_aplicacion");
                        String tipo = c.getString("tipo");
                        htListUpdate.add(id_usuario);
                        htListUpdate.add(id_comando);
                        htListUpdate.add(fecha_aplicacion);
                        htListUpdate.add(tipo);
                        SAVE_DATA(DataUpload.TABLE_DELETE_COMMAND, htListUpdate,ActionDeleteAllInfoUserOneAction);
                        if(ActionDeleteAllInfoUserOneAction)ActionDeleteAllInfoUserOneAction=false;
                        String date=(DateFormat.format("yyyy-MM-dd HH:mm:ss", new java.util.Date()).toString());
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String firstStr = date;
                        Date first = sdf.parse(firstStr);
                        Date second = sdf.parse(fecha_aplicacion);
                        boolean before = (first.before(second));
                        if(before){
                            //---------Log.v("Valor", "La fecha " + second.toString() + " no ha pasado, hoy es " + first.toString());
                        }
                        else {
                            //---------Log.v("Valor", "La fecha " + second.toString() + " ya ha pasado, hoy es " +first.toString());
                            //---------Execution action delete
                            if(id_comando.equals("1")){
                                dbEncuestaRealizada.DeleteContactStateSync(id_usuario);
                                REQUEST_START_IN_LOGING=true;
                                //---Eliminados lo relacionado ha ese usuario
                            }else if(id_comando.equals("2")){
                                //---Eliminados completamente un reset
                                dbEncuestaRealizada.DeleteAllResultStateSync();
                                REQUEST_START_IN_LOGING=true;
                            }
                        }
                        htListUpdate.clear();
                    }
                }
                catch (Exception e){
                    Log.v("Valor", "ERROR EN " + e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (isDeleted) {
            if (file.exists()) {
                file.delete();
            }
        }
        return res;
    }

    public static boolean SAVE_DATA_JSON_DB(String casetable,
                                      String RowListUpdate,
                                      String id,
                                      String id_contact,
                                            String folio) {
        switch (casetable) {
            case DataUpload.TABLE_SEND_DATA_SERVER:
                //Insertar
                dbEncuestaRealizada.UpdateEncuestaResultStateSync(
                        RowListUpdate,
                        id,
                        id_contact,
                        folio);
                break;
            case DataUpload.TABLE_SEND_PROCESS_SERVER:
                //Insertar
                dbEncuestaRealizada.UpdateEncuestaResultStateSync(
                        RowListUpdate,
                        id,
                        id_contact,
                        folio);
                break;
        }
        return true;
    }
    public static void SAVE_DATA(String casetable,ArrayList<String> RowListUpdate, boolean action){
        switch (casetable){
            case DataUpload.TABLE_DELETE_COMMAND:
                dbEncuestaRealizada.InsertDeleteActionsSync(
                        RowListUpdate.get(0),
                        RowListUpdate.get(1),
                        RowListUpdate.get(2),
                        RowListUpdate.get(3),
                        action);
                break;
        }
    }
}
