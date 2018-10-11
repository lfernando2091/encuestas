package com.saganet.encuestas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by Hazael on 24/10/2016.
 */

public class MenuEncuestasFragment extends Fragment {
    DataSesion dbUsersEncuesta;
    private static final String ARG_USER_ID="usersid";
    private String mEncuestaID;
    private EditText mSearch;
    private ListView mUserEncuestaList;
    private FloatingActionButton mAddButton;
    private UserCursorAdap mUseAdapter;
    public static final int REQUEST_UPDATE_DELETE_USER = 2;
    private static final String LOGTAG = "android-localizacion";
    private ArrayList<String> spinnerResultArray;
    public static int NumPreg=0;
    ProgressDialog pd = null;
    FloatingActionButton fab;
    ProgressDialog lod = null;
    private ArrayList<String> htListUpdate;
    private static final int PETICION_CONFIG_UBICACION = 201;
    public MenuEncuestasFragment(){    }
    private boolean REQUEST_START_IN_LOGING=false;
    private static MenuEncuestasContent conts;
    private View linearLayout;
    //public static MenuEncuestasFragment newInstance() {
        //return new MenuEncuestasFragment();
    //}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mEncuestaID = getArguments().getString(ARG_USER_ID);
        }
        setHasOptionsMenu(true);
    }

    public static MenuEncuestasFragment newInstance(String accountid, MenuEncuestasContent a) {
        MenuEncuestasFragment fragment = new MenuEncuestasFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USER_ID, accountid);
        conts=new MenuEncuestasContent();
        conts=a;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_users, container, false);

        View root =inflater.inflate(R.layout.fragment_users, container, false);
        // Instancia de helper
        dbUsersEncuesta = new DataSesion(getActivity());
        // Referencias UI
        mUserEncuestaList = (ListView) root.findViewById(R.id.contacto_list);
        linearLayout= (View)  root.findViewById(R.id.contacto_list);
        // Setup
        mUseAdapter = new UserCursorAdap(getActivity(), null);
        mUserEncuestaList.setAdapter(mUseAdapter);
        mUserEncuestaList.setTextFilterEnabled(true);
        mSearch= (EditText) getActivity().findViewById(R.id.text_search);
        mSearch.addTextChangedListener(new TextWatcher(){
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //if (mUseAdapter!=null) {
                Log.d("Data", "Filter:"+s);
                mUseAdapter.swapCursor(null);
                mUseAdapter.getFilter().filter(s);
                //}
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        mUseAdapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                Log.d("Data --Cursor", "Filter:"+constraint.toString());
                return dbUsersEncuesta.getAllClientesIDlike(
                        DataUpload.USER_ACCOUNT_ENC_ID,constraint.toString());
            }
        });
        mAddButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddScreen(view);
            }
        });

        fab = (FloatingActionButton) getActivity().findViewById(R.id.syncencuestas);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(getStateNetworkConnection()){
                    new AsyncReadData().execute();
                    Snackbar.make(view, "Actualizado", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else {
                    Snackbar.make(view, "No hay conexión a internet", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
        // Eventos
        mUserEncuestaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataUpload.IS_REDITABLE_ENCUESTA_ID=false;
                Cursor currentItem = (Cursor) mUseAdapter.getItem(i);
                String currentLawyerId = currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_PKEY));
                DataUpload.USER_ACCOUNT_ID_PKEY=currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_PKEY+ "_UC"));
                //String currentUserId = currentItem.getString(
                //        currentItem.getColumnIndex(DataUpload.COLUMN_ID_USUARIO));
                String currentName = currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_NOMBRE));
                currentName = currentName + " "+ currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_PATERNO));
                currentName = currentName + " "+ currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_MATERNO));
                DataUpload.USER_ACCOUNT_ID=currentLawyerId;
                ContactInfoFragment.sIdContactoUs=currentLawyerId;
                ContactInfoFragment.sIdPrecargaUs=currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_ID_PRECARGA));
                ContactInfoFragment.sNombreEncuestaUs=DataUpload.USER_ACCOUNT_ENC_NAME;
                ContactInfoFragment.sNombreUs=currentName;
                DataUpload.USER_NAME=currentName;
                ContactInfoFragment.sCalleUs=currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_CALLE));
                ContactInfoFragment.sColoniaUs=currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_COLONIA));
                ContactInfoFragment.sNumExtUs=currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_NUM_EXT));
                ContactInfoFragment.sNumIntUs=currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_NUM_INT));
                ContactInfoFragment.sCodPostalUs=currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_COD_POSTAL));
                ContactInfoFragment.sEntidadUs=currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_ENTIDAD));
                ContactInfoFragment.sMunicipioUs=currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_MUNICIPIO));
                ContactInfoFragment.sFechNacUs=currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_FECHA_NAC));
                ContactInfoFragment.sGeneroUs=currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_GENERO));
                showDetailScreen();
                //showOtherDetailScreen("");
            }
        });
        //PRUEBA RESET DATA TO USE IN EXPERIMENT
        //getActivity().deleteDatabase(DataUpload.DATA_BASE_NAME);
        //Log.v("-Valores de contactos","");
        load();
        return  root;
    }

    private boolean getStateNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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

    private void load() { new LoadTaskEnc().execute(); }

    private void showDetailScreen() {
        try {
            //Intent intent = new Intent(getActivity(), setcuestionario.class);
            //intent.putExtra(MenuEncuestasContent.EXTRA_MENU_ENCUESTA_ID, DataUpload.USER_ACCOUNT_ID);
            //startActivityForResult(intent, setcuestionario.REQUEST_SAVED_ENCUESTA);
            //Toast.makeText(getActivity(), "POSITION " + DataUpload.VALUE_LATITUDE_POSITION, Toast.LENGTH_SHORT).show();
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            DialogFragment newFragment = new ContactInfoFragment().newInstance("Información");
            newFragment.setTargetFragment(this,ContactInfoFragment.REQUEST_INPUT_TEST);
            newFragment.show(ft, "fragment_contact_info");
        }
        catch (Exception e){
            Log.v("Error en:", e.getMessage());
        }
    }

    private void showAddScreen(View view) {
        Intent intent = new Intent(getActivity(), AddContacto.class);
        startActivityForResult(intent, AddContacto.REQUEST_ADD_ACCOUNT);
        getActivity().overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PETICION_CONFIG_UBICACION:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        conts.startLocationUpdates();
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.i(LOGTAG, "El usuario no ha realizado los cambios de configuración necesarios");
                        break;
                }
                break;
        }
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case AddContacto.REQUEST_ADD_ACCOUNT:
                    //load();
                    new LoadTaskEnc().execute();
                    //--Toast.makeText(getActivity(), "Contacto guardado correctamente", Toast.LENGTH_SHORT).show();
                    Snackbar.make(linearLayout, "Contacto guardado correctamente.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    break;
                case setcuestionario.REQUEST_SAVED_ENCUESTA:
                    //load();
                    new LoadTaskEnc().execute();
                    //Toast.makeText(getActivity(), "Encuesta terminada correctamente", Toast.LENGTH_SHORT).show();
                    ShowAlertDialog("Encuesta terminada correctamente.");
                    break;
                case EncuestasRespuestaDoc.REQUEST_SYNC_DATA_ENC:
                    //load();
                    if(DataUpload.FINISH_ALL_AND_RESTART_LOGING){
                        DataUpload.FINISH_ALL_AND_RESTART_LOGING=false;
                        getActivity().setResult(Activity.RESULT_OK);
                        getActivity().finish();
                    }
                    else {
                        showDetailScreen();
                    }
                    break;
                case REQUEST_UPDATE_DELETE_USER:
                    load();
                    break;
            }
        }
        try {
            String sn="";
            if(!data.getStringExtra(ContactInfoFragment.RESULT_TYPE).equals("")){
                sn=data.getStringExtra(ContactInfoFragment.RESULT_TYPE);
            }
            switch (sn){
                case ContactInfoFragment.RESULT_OK:
                    DataUpload.VALUE_INITIALIZED_TIME=(DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date()).toString());
                    Log.v("HORA DE INICIO",DataUpload.VALUE_INITIALIZED_TIME);
                    Intent intent = new Intent(getActivity(), setcuestionario.class);
                    intent.putExtra(MenuEncuestasContent.EXTRA_MENU_ENCUESTA_ID, DataUpload.USER_ACCOUNT_ID);
                    startActivityForResult(intent, setcuestionario.REQUEST_SAVED_ENCUESTA);
                    //--getActivity().overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
                    break;
                case ContactInfoFragment.RESULT_CANCELED:
                    break;
                case ContactInfoFragment.RESULT_TAG:
                    FragmentTransaction ft = getFragmentManager().beginTransaction();
                    DialogFragment newFragment = new ContactTagFragment().newInstance("Motivo no encuesta");
                    newFragment.setTargetFragment(this,ContactTagFragment.REQUEST_INPUT_TAG);
                    newFragment.show(ft, "fragment_contact_tag");
                    break;
                case ContactTagFragment.RESULT_OK_TAG:
                    spinnerResultArray= new ArrayList<String>();
                    spinnerResultArray.add(0,"null");
                    //new LoadTaskNumPreg().execute();
                    NumPreg=dbUsersEncuesta.getNumbersAskEncuesta(DataUpload.USER_ACCOUNT_ENC_ID);
                    FillEmpty(0,NumPreg);
                    DataUpload.NUMBER_ANSWER_TO_SAVE=spinnerResultArray.size();
                    //DataUpload.NUMBER_ANSWER_TO_SAVE=NumPreg;
                    DataUpload.VALUE_INITIALIZED_TIME=(DateFormat.format("yyyy-MM-dd hh:mm:ss", new java.util.Date()).toString());
                    DataUpload.VALUE_FINALIZED_TIME=DataUpload.VALUE_INITIALIZED_TIME;
                    tRespuestasEncuesta tre= new tRespuestasEncuesta(Integer.parseInt(DataUpload.USER_ID),
                            DataUpload.USER_ACCOUNT_ID,
                            Integer.parseInt(DataUpload.USER_ACCOUNT_ENC_ID),
                            Integer.parseInt(ContactTagFragment.mResultDataRbtn_TRUE),
                            DataUpload.VALUE_INITIALIZED_TIME,
                            DataUpload.VALUE_FINALIZED_TIME,
                            DataUpload.VALUE_LATITUDE_POSITION,
                            DataUpload.VALUE_LONGITUDE_POSITION,
                            "0","0",
                            spinnerResultArray);
                    new SaveTaskDataEncuesta().execute(tre);
                    load();
                    break;
                case ContactTagFragment.RESULT_CANCELED_TAG:
                    showDetailScreen();
                    break;
                default:
                    break;
            }
        }catch (Exception e){
                //Log.v("ERROR",e.getMessage());
        }
    }

    private  void ShowAlertDialog(String s){
        AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
        builder1.setTitle("Aviso");
        builder1.setMessage(s);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                "Aceptar",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    private class LoadTaskEnc extends AsyncTask<Void, Void, Cursor> {
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
            return dbUsersEncuesta.getAllClientesID(DataUpload.USER_ACCOUNT_ENC_ID);
            //return dbUsersEncuesta.getAllDataClinetes(new String[]{"1"});
        }
        @Override
        protected void onPostExecute(Cursor cursor) {
            if (pd != null) {
                if (pd.isShowing()) {
                    pd.dismiss();
                }
                pd = null;
            }
            if (cursor != null && cursor.getCount() > 0) {
                //try {
                    DataUpload.USER_TYPE_RESULT=2;
                    //Log.v("-ID_ENCUESTA_ESTADO: ",cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_NOMBRE)));------i HATE YOU .-------
                    mUseAdapter.swapCursor(cursor);

                    //} catch (Exception e) {
                    //Log.v("Adaptacion : ",e.getMessage());
                //}
            } else {
                if(!mUseAdapter.isEmpty()){
                    mUseAdapter.swapCursor(null);
                }else {
                    //mUseAdapter=null;
                }
                Log.v("ESTADO DE : ","DATOS VACIOS");
                // Mostrar empty state
            }
        }
    }

    private class SaveTaskDataEncuesta extends AsyncTask<tRespuestasEncuesta, Void, Boolean> {
        @Override
        protected Boolean doInBackground(tRespuestasEncuesta... trespuestasencuest) {
            //if(mAccountId!=null){
            //} else {
            return dbUsersEncuesta.InsertEncuestaResult(trespuestasencuest[0])>0;
            //}
        }
        @Override
        protected void onPostExecute(Boolean res) {
            showFinishAsk(res);
        }
    }

    private void FillEmpty(int from, int to){
        for (int i=from+1;i<=to;i++){
            spinnerResultArray.add(i,"-");
            Log.v("Values filled" + i,"-");
        }
    }

    private void showFinishAsk(boolean q){
        if(!q){
            //-----Toast.makeText(getActivity(), "Error al marcada y guardada", Toast.LENGTH_SHORT).show();
            ShowAlertDialog("Error al marcada y guardada.");
        } else {
            //----Toast.makeText(getActivity(), "Encuesta marcada y guardada", Toast.LENGTH_SHORT).show();
            ShowAlertDialog("Encuesta marcada y guardada.");
        }
    }
    @Override
    public  boolean onOptionsItemSelected(MenuItem M){
        switch (M.getItemId()){
            case R.id.action_dataencuestas:
                //getNextAsk();
                //Toast.makeText(getActivity(),"Menu encuestas",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), EncuestasRespuestaDoc.class);
                startActivityForResult(intent, EncuestasRespuestaDoc.REQUEST_SYNC_DATA_ENC);
                getActivity().overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
                break;
        }
        return  super.onOptionsItemSelected(M);
    }

    private class AsyncReadData extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lod = new ProgressDialog(getActivity());
            lod.setTitle("Sincronizando...");
            lod.setMessage("Descargando archivos.");
            lod.setCancelable(false);
            lod.show();
        }
        @Override
        protected Boolean doInBackground(Void... voids) {
            if(getStateNetworkConnection()){
                POST_DIRETORY_FILE_CONTACTOS();
                READ_DIRECTORY_JSON(true, DataUpload.TABLE_CONTACTOS_UPDATE);
                POST_DIRETORY_FILE();
                READ_DIRECTORY_JSON(true, DataUpload.TABLE_USUARIOS_CONTACTOS_UPDATE);
            }
            return true;
        }
        @Override
        protected void onPostExecute(Boolean res) {
            if (lod != null) {
                if (lod.isShowing()) {
                    lod.dismiss();
                }
                lod = null;
            }
            if(REQUEST_START_IN_LOGING){
                REQUEST_START_IN_LOGING=false;
                getActivity().setResult(Activity.RESULT_OK);
                getActivity().finish();
            } else {
                load();
                DataUpload.AlertSoundVibrate(getActivity(), 1000);
            }
        }
    }

    private void POST_DIRETORY_FILE_CONTACTOS(){
        try {
            //primero especificaremos el origen de nuestro archivo a descargar utilizando
            //la ruta completa
            URL url = new URL(DataUpload.SYNC_URL_CONTACTOS);

            //establecemos la conexión con el destino
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //establecemos el método jet para nuestra conexión
            //el método setdooutput es necesario para este tipo de conexiones
            urlConnection.setReadTimeout(99000);
            urlConnection.setConnectTimeout(100000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("id_usuario", DataUpload.USER_ID)
                    .appendQueryParameter("imei", DataUpload.VALUE_IMEI_DATA_DEVICE)
                    .appendQueryParameter("longitud", DataUpload.VALUE_LONGITUDE_POSITION)
                    .appendQueryParameter("latitud", DataUpload.VALUE_LATITUDE_POSITION);
            String query = builder.build().getEncodedQuery();

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            //por último establecemos nuestra conexión y cruzamos los dedos
            urlConnection.connect();

            //vamos a establecer la ruta de destino para nuestra descarga
            //para hacerlo sencillo en este ejemplo he decidido descargar en
            //la raíz de la tarjeta SD
            File SDCardRoot= new File(DataUpload.LOCATION_DATA_SYNC);
            //Get the text file
            File file = new File(SDCardRoot,DataUpload.TABLE_CONTACTOS_UPDATE+".json");
            //vamos a crear un objeto del tipo de fichero
            //donde descargaremos nuestro fichero, debemos darle el nombre que
            //queramos, si quisieramos hacer esto mas completo
            //cogeríamos el nombre del origen

            //utilizaremos un objeto del tipo fileoutputstream
            //para escribir el archivo que descargamos en el nuevo
            FileOutputStream fileOutput = new FileOutputStream(file);

            //leemos los datos desde la url
            InputStream inputStream = urlConnection.getInputStream();

            //obtendremos el tamaño del archivo y lo asociaremos a una
            //variable de tipo entero
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;

            //creamos un buffer y una variable para ir almacenando el
            //tamaño temporal de este
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            //ahora iremos recorriendo el buffer para escribir el archivo de destino
            //siempre teniendo constancia de la cantidad descargada y el total del tamaño
            //con esto podremos crear una barra de progreso
            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {

                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                //podríamos utilizar una función para ir actualizando el progreso de lo
                //descargado
                //actualizaProgreso(downloadedSize, totalSize);

            }
            //cerramos
            fileOutput.close();

//y gestionamos errores
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void POST_DIRETORY_FILE(){
        try {
            //primero especificaremos el origen de nuestro archivo a descargar utilizando
            //la ruta completa
            URL url = new URL(DataUpload.SYNC_URL_USUARIOS_ENCUESTAS_CONTACTOS);

            //establecemos la conexión con el destino
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //establecemos el método jet para nuestra conexión
            //el método setdooutput es necesario para este tipo de conexiones
            urlConnection.setReadTimeout(45000);
            urlConnection.setConnectTimeout(50000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("id_usuario", DataUpload.USER_ID)
                    .appendQueryParameter("imei", DataUpload.VALUE_IMEI_DATA_DEVICE)
                    .appendQueryParameter("longitud", DataUpload.VALUE_LONGITUDE_POSITION)
                    .appendQueryParameter("latitud", DataUpload.VALUE_LATITUDE_POSITION)
                    .appendQueryParameter("id_encuesta", DataUpload.USER_ACCOUNT_ENC_ID);
            String query = builder.build().getEncodedQuery();

            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(query);
            writer.flush();
            writer.close();
            os.close();
            //por último establecemos nuestra conexión y cruzamos los dedos
            urlConnection.connect();

            //vamos a establecer la ruta de destino para nuestra descarga
            //para hacerlo sencillo en este ejemplo he decidido descargar en
            //la raíz de la tarjeta SD
            File SDCardRoot= new File(DataUpload.LOCATION_DATA_SYNC);
            //Get the text file
            File file = new File(SDCardRoot,DataUpload.TABLE_USUARIOS_CONTACTOS_UPDATE+".json");
            //vamos a crear un objeto del tipo de fichero
            //donde descargaremos nuestro fichero, debemos darle el nombre que
            //queramos, si quisieramos hacer esto mas completo
            //cogeríamos el nombre del origen

            //utilizaremos un objeto del tipo fileoutputstream
            //para escribir el archivo que descargamos en el nuevo
            FileOutputStream fileOutput = new FileOutputStream(file);

            //leemos los datos desde la url
            InputStream inputStream = urlConnection.getInputStream();

            //obtendremos el tamaño del archivo y lo asociaremos a una
            //variable de tipo entero
            int totalSize = urlConnection.getContentLength();
            int downloadedSize = 0;

            //creamos un buffer y una variable para ir almacenando el
            //tamaño temporal de este
            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            //ahora iremos recorriendo el buffer para escribir el archivo de destino
            //siempre teniendo constancia de la cantidad descargada y el total del tamaño
            //con esto podremos crear una barra de progreso
            while ( (bufferLength = inputStream.read(buffer)) > 0 ) {

                fileOutput.write(buffer, 0, bufferLength);
                downloadedSize += bufferLength;
                //podríamos utilizar una función para ir actualizando el progreso de lo
                //descargado
                //actualizaProgreso(downloadedSize, totalSize);

            }
            //cerramos
            fileOutput.close();

//y gestionamos errores
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void READ_DIRECTORY_JSON(boolean isDeleted, String dat){
        File dir= new File(DataUpload.LOCATION_DATA_SYNC);
        //Get the text file
        File file = new File(dir,dat+".json");
        if(file.exists()){
            //Read text from file
            //StringBuilder text = new StringBuilder();
            try {
                FileInputStream stream = new FileInputStream(file);
                String jsonStr = null;
                try {
                    FileChannel fc = stream.getChannel();
                    MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

                    jsonStr = Charset.defaultCharset().decode(bb).toString();
                }
                catch(Exception e){
                    e.printStackTrace();
                }
                finally {
                    stream.close();
                }
                JSONObject jsonObj = new JSONObject(jsonStr);
                if(dat.toString().equals(DataUpload.TABLE_CONTACTOS_UPDATE)){
                    // Getting data JSON Array nodes
                    JSONArray data  = jsonObj.getJSONArray(dat);
                    // looping through All nodes
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
                        htListUpdate= new ArrayList<String>();
                        String pkey = c.getString("id");
                        String id_encuesta = c.getString("id_encuesta");
                        String id_precarga = c.getString("id_precarga");
                        String nombre = c.getString("nombre");
                        String paterno = c.getString("paterno");
                        String materno = c.getString("materno");
                        String calle = c.getString("calle");
                        String colonia = c.getString("colonia");
                        String numero_exterior = c.getString("numero_exterior");
                        String numero_interior = c.getString("numero_interior");
                        String codigo_postal = c.getString("codigo_postal");
                        String entidad = c.getString("entidad");
                        String municipio = c.getString("municipio");
                        String fecha_nacimiento = c.getString("fecha_nacimiento");
                        String genero = c.getString("genero");
                        String version = c.getString("version");
                        String operacion = c.getString("operacion");
                        //use >  int id = c.getInt("duration"); if you want get an int
                        htListUpdate.add(pkey);
                        htListUpdate.add(id_precarga);
                        htListUpdate.add(id_encuesta);
                        htListUpdate.add(DataUpload.USER_ID);
                        htListUpdate.add(nombre);
                        htListUpdate.add(paterno);
                        htListUpdate.add(materno);
                        htListUpdate.add(calle);
                        htListUpdate.add(colonia);
                        htListUpdate.add(numero_exterior.toString().equals("")?"0":numero_exterior);
                        htListUpdate.add(numero_interior.toString().equals("")?"0":numero_interior);
                        htListUpdate.add(codigo_postal);
                        htListUpdate.add(entidad);
                        htListUpdate.add(municipio);
                        htListUpdate.add(fecha_nacimiento);
                        htListUpdate.add(genero);
                        htListUpdate.add(version);
                        htListUpdate.add(operacion);
                        for(String s: htListUpdate){
                            Log.v("INSERT VALUE",s);
                        }
                        SAVE_DATA_JSON_DB(dat,htListUpdate,false);
                        Log.v("Valorguardado id_cont",String.valueOf(pkey));
                        htListUpdate.clear();
                        // do what do you want on your interface
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
                            SAVE_DATA_JSON_DB(DataUpload.TABLE_DELETE_COMMAND, htListUpdate,ActionDeleteAllInfoUserOneAction);
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
                                    dbUsersEncuesta.DeleteContactStateSync(id_usuario);
                                    REQUEST_START_IN_LOGING=true;
                                //---Eliminados lo relacionado ha ese usuario
                                }else if(id_comando.equals("2")){
                                //---Eliminados completamente un reset
                                    dbUsersEncuesta.DeleteAllResultStateSync();
                                    REQUEST_START_IN_LOGING=true;
                                }
                            }
                            htListUpdate.clear();
                        }
                    }
                    catch (Exception e){
                        Log.v("Valor", "ERROR EN " + e.getMessage());
                    }
                }
                else {
                    // Getting data JSON Array nodes
                    JSONArray data2  = jsonObj.getJSONArray(dat);
                    // looping through All nodes
                    for (int i = 0; i < data2.length(); i++) {
                        JSONObject c = data2.getJSONObject(i);
                        htListUpdate= new ArrayList<String>();
                        String pkey = c.getString("pkey");
                        String id_usuario = c.getString("id_usuario");
                        String id_contacto = c.getString("id_contacto");
                        String id_encuesta = c.getString("id_encuesta");
                        String id_estatus_encuesta = c.getString("id_estatus_encuesta");
                        String version = c.getString("version");
                        String operacion = c.getString("operacion");
                        htListUpdate.add(pkey);
                        htListUpdate.add(id_usuario);
                        htListUpdate.add(id_contacto);
                        htListUpdate.add(id_encuesta);
                        htListUpdate.add(id_estatus_encuesta);
                        htListUpdate.add(version);
                        htListUpdate.add(operacion);
                        SAVE_DATA_JSON_DB(dat,htListUpdate,false);
                        Log.v("Valorguardadoid us_cont",String.valueOf(id_usuario));
                        htListUpdate.clear();
                        // do what do you want on your interface
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
                            SAVE_DATA_JSON_DB(DataUpload.TABLE_DELETE_COMMAND, htListUpdate,ActionDeleteAllInfoUserOneAction);
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
                                    dbUsersEncuesta.DeleteContactStateSync(id_usuario);
                                    REQUEST_START_IN_LOGING=true;
                                    //---Eliminados lo relacionado ha ese usuario
                                }else if(id_comando.equals("2")){
                                    //---Eliminados completamente un reset
                                    dbUsersEncuesta.DeleteAllResultStateSync();
                                    REQUEST_START_IN_LOGING=true;
                                }
                            }
                            htListUpdate.clear();
                        }
                    }
                    catch (Exception e){
                        Log.v("Valor", "ERROR EN " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                Log.v("Valorguardadoid error",e.getMessage());
                e.printStackTrace();
            }
        }
        if(isDeleted){
            file.delete();
        }
    }

    private boolean SAVE_DATA_JSON_DB(String casetable,ArrayList<String> RowListUpdate, boolean action){
        switch (casetable) {
            case DataUpload.TABLE_CONTACTOS_UPDATE:
                if(RowListUpdate.get(17).equals("0")){//0
                    //Insertar
                    Log.v("INSERT ","NOW CONTACT");
                    dbUsersEncuesta.InsertContactoSync(new tContactos(
                            RowListUpdate.get(0),
                            Integer.parseInt(RowListUpdate.get(1)),
                            Integer.parseInt(RowListUpdate.get(2)),
                            Integer.parseInt(RowListUpdate.get(3)),
                            RowListUpdate.get(4),
                            RowListUpdate.get(5),
                            RowListUpdate.get(6),
                            RowListUpdate.get(7),
                            RowListUpdate.get(8),
                            RowListUpdate.get(9),
                            RowListUpdate.get(10),
                            Integer.parseInt(RowListUpdate.get(11)),
                            RowListUpdate.get(12),
                            RowListUpdate.get(13),
                            RowListUpdate.get(14),
                            RowListUpdate.get(15),
                            Integer.parseInt(RowListUpdate.get(16))));
                    Log.v("INSERT","NOW FINISH");
                    return true;
                }
                else if(RowListUpdate.get(17).equals("1")){//1
                    //eliminar
                    dbUsersEncuesta.DeleteContactoSync(
                            DataUpload.TABLE_CONTACTOS,
                            RowListUpdate.get(0),
                            RowListUpdate.get(1),
                            RowListUpdate.get(2));
                    return true;
                }
                break;
            case DataUpload.TABLE_USUARIOS_CONTACTOS_UPDATE:
                if(RowListUpdate.get(6).equals("0")){//0
                    //Insertar
                    dbUsersEncuesta.InsertUsuarioContactosSync(new tUsuarioContactos(
                            Integer.parseInt(RowListUpdate.get(0)),
                            Integer.parseInt(RowListUpdate.get(1)),
                            Integer.parseInt(RowListUpdate.get(2)),
                            Integer.parseInt(RowListUpdate.get(3)),
                            Integer.parseInt(RowListUpdate.get(4)),
                            Integer.parseInt(RowListUpdate.get(5))));
                    return true;
                }
                else if(RowListUpdate.get(5).equals("1")){//1
                    //eliminar
                    dbUsersEncuesta.DeleteUsuarioContactosSync(
                            DataUpload.TABLE_USUARIOS_CONTACTOS,
                            RowListUpdate.get(0),
                            RowListUpdate.get(1),
                            RowListUpdate.get(2),
                            RowListUpdate.get(3));
                    return true;
                }
                break;
            case DataUpload.TABLE_DELETE_COMMAND:
                    dbUsersEncuesta.InsertDeleteActionsSync(
                            RowListUpdate.get(0),
                            RowListUpdate.get(1),
                            RowListUpdate.get(2),
                            RowListUpdate.get(3),
                            action);
                break;
        }
        return false;
    }
}
