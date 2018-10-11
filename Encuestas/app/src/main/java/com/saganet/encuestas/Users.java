package com.saganet.encuestas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.util.CircularArray;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 //* {@link //Users.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Users#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Users extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    //private static final String ARG_PARAM1 = "param1";
    //private static final String ARG_PARAM2 = "param2";

    DataSesion dbUsersEncuesta;

    private ListView mUserEncuestaList;
    private ArrayList<String> htListUpdate;
    private UserCursorAdap mEncuestaAdapter;
    private FloatingActionButton fab;
    //private FloatingActionButton mAddButton;
    public static final int REQUEST_UPDATE_DELETE_USER = 2;
    ProgressDialog pd = null;
    ProgressDialog lod = null;
    private boolean REQUEST_START_IN_LOGING=false;
    private View linearLayout;

    public Users() {
        // Required empty public constructor
    }

    public static Users newInstance() {
        return new Users();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_users, container, false);

        View root =inflater.inflate(R.layout.fragment_users, container, false);
        // Instancia de helper
        dbUsersEncuesta = new DataSesion(getActivity());
        // Referencias UI
        mUserEncuestaList = (ListView) root.findViewById(R.id.contacto_list);
        mEncuestaAdapter = new UserCursorAdap(getActivity(), null);
        //mAddButton = (FloatingActionButton) getActivity().findViewById(R.id.fab);

        // Setup
        mUserEncuestaList.setAdapter(mEncuestaAdapter);
        linearLayout= (View)  root.findViewById(R.id.contacto_list);
        // Eventos
        mUserEncuestaList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor currentItem = (Cursor) mEncuestaAdapter.getItem(i);
                String currentLawyerId = currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_PKEY));
                DataUpload.USER_ACCOUNT_ENC_ID=currentLawyerId;
                Log.v("USER_ACCOUNT_ENC_ID",currentLawyerId);
                DataUpload.USER_ACCOUNT_ENC_NAME=currentItem.getString(
                        currentItem.getColumnIndex(DataUpload.COLUMN_ENCUESTA));
                showUsersScreen(currentLawyerId);
            }
        });

        fab = (FloatingActionButton) getActivity().findViewById(R.id.sync);
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
        DataUpload.CONNETION_ALLOWED=getStateNetworkConnection();
        // Carga de datos

        //mAddButton.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        showAddScreen(view);
        //    }
        //});
        //PRUEBA RESET DATA TO USE IN EXPERIMENT
        //getActivity().deleteDatabase(DataUpload.DATA_BASE_NAME);
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

    private void load() { new LoadTaskd().execute(); }

    private void showUsersScreen(String lawyerId) {
        try {
            Intent intent = new Intent(getActivity(), MenuEncuestasContent.class);
            intent.putExtra(Menu.EXTRA_MENU_ID, lawyerId);
            startActivityForResult(intent, MenuEncuestasContent.REQUEST_LOGING_RELOAD);
            getActivity().overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
        }
        catch (Exception e){
            Log.v("Error en:", e.getMessage());
        }
    }
    private void showAddScreen(View view) {
        Intent intent = new Intent(getActivity(), AddContacto.class);
        startActivityForResult(intent, AddContacto.REQUEST_ADD_ACCOUNT);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Activity.RESULT_OK == resultCode) {
            switch (requestCode) {
                case AddContacto.REQUEST_ADD_ACCOUNT:
                    //--Toast.makeText(getActivity(), "Contacto guardado correctamente", Toast.LENGTH_SHORT).show();
                    Snackbar.make(linearLayout, "Contacto guardado correctamente.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    load();
                    break;
                case REQUEST_UPDATE_DELETE_USER:
                    load();
                    break;
                case MenuEncuestasContent.REQUEST_LOGING_RELOAD:

                    getActivity().finish();
                    break;
            }
        }
    }

    private class LoadTaskd extends AsyncTask<Void, Void, Cursor> {
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
            //return dbUsersEncuesta.getAllClients();
            //return dbUsersEncuesta.getAllDataClinetes(new String[]{"1", "2", "3", "4"});
            return dbUsersEncuesta.getEncuestasId();
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
                try {
                    DataUpload.USER_TYPE_RESULT=1;
                    mEncuestaAdapter.swapCursor(cursor);
                } catch (Exception e) {
                    Log.v("Adaptacion : ",e.getMessage());
                }
            } else {
                if(!mEncuestaAdapter.isEmpty()){
                    mEncuestaAdapter.swapCursor(null);
                }
                // Mostrar empty state
            }
        }
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
                POST_DIRETORY_FILE_ENCUESTAS();
                READ_DIRECTORY_JSON(true, DataUpload.TABLE_ENCUESTAS_UPDATE);
                POST_DIRETORY_FILE();
                READ_DIRECTORY_JSON(true, DataUpload.TABLE_USUARIOS_ENCUESTAS_UPDATE);
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
                getActivity().finish();
            } else {
                load();
                DataUpload.AlertSoundVibrate(getActivity(), 1000);
            }
        }
    }

    private void POST_DIRETORY_FILE(){
        try {
            //primero especificaremos el origen de nuestro archivo a descargar utilizando
            //la ruta completa
            URL url = new URL(DataUpload.SYNC_URL_USUARIOS_ENCUESTAS);

            //establecemos la conexión con el destino
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //establecemos el método jet para nuestra conexión
            //el método setdooutput es necesario para este tipo de conexiones
            urlConnection.setReadTimeout(40000);
            urlConnection.setConnectTimeout(45000);
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
            File file = new File(SDCardRoot,DataUpload.TABLE_USUARIOS_ENCUESTAS_UPDATE+".json");
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

    private void POST_DIRETORY_FILE_ENCUESTAS(){
        try {
            //primero especificaremos el origen de nuestro archivo a descargar utilizando
            //la ruta completa
            URL url = new URL(DataUpload.SYNC_URL_ENCUESTAS);

            //establecemos la conexión con el destino
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //establecemos el método jet para nuestra conexión
            //el método setdooutput es necesario para este tipo de conexiones
            urlConnection.setReadTimeout(40000);
            urlConnection.setConnectTimeout(45000);
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
            File file = new File(SDCardRoot,DataUpload.TABLE_ENCUESTAS_UPDATE+".json");
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
                // Getting data JSON Array nodes
                if(dat.toString().equals(DataUpload.TABLE_ENCUESTAS_UPDATE)){
                    Log.v("Valor guardado id 1","all");
                    JSONArray data  = jsonObj.getJSONArray(DataUpload.TABLE_ENCUESTAS_UPDATE);
                    // looping through All nodes
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
                        htListUpdate= new ArrayList<String>();
                        int id = c.getInt("id");
                        String nombre = c.getString("nombre");
                        int version = c.getInt("version");
                        int  operacion= c.getInt("operacion");
                        String caducidad = c.getString("fecha_caducidad");
                        //use >  int id = c.getInt("duration"); if you want get an int
                        htListUpdate.add(String.valueOf(id));
                        htListUpdate.add(nombre);
                        htListUpdate.add(String.valueOf(version));
                        htListUpdate.add(String.valueOf(operacion));
                        htListUpdate.add(caducidad);
                        SAVE_DATA_JSON_DB(DataUpload.TABLE_ENCUESTAS_UPDATE,htListUpdate,false);
                        Log.v("Valor guardado id",String.valueOf(id));
                        htListUpdate.clear();
                        // do what do you want on your interface
                    }
                    Log.v("Valor guardado id 2","data sync");
                    JSONArray data4  = jsonObj.getJSONArray(DataUpload.TABLE_RESPUESTAS_UPDATE);
                    // looping through All nodes
                    for (int i = 0; i < data4.length(); i++) {
                        JSONObject c = data4.getJSONObject(i);
                        htListUpdate= new ArrayList<String>();
                        int pkey = c.getInt("pkey");
                        int id_tipo_pregunta = c.getInt("id_pregunta");
                        String id_encuesta = c.getString("respuesta_c");
                        String pregunta = c.getString("tipo_accion");
                        String valor_accion = c.getString("valor_accion");
                        int id_encuesta_estatus = c.getInt("id_encuesta_estatus");
                        int version = c.getInt("version");
                        int operacion = c.getInt("operacion");
                        //use >  int id = c.getInt("duration"); if you want get an int
                        htListUpdate.add(String.valueOf(pkey));
                        htListUpdate.add(String.valueOf(id_tipo_pregunta));
                        htListUpdate.add(id_encuesta);
                        htListUpdate.add(pregunta);
                        htListUpdate.add(valor_accion);
                        htListUpdate.add(String.valueOf(id_encuesta_estatus));
                        htListUpdate.add(String.valueOf(version));
                        htListUpdate.add(String.valueOf(operacion));
                        SAVE_DATA_JSON_DB(DataUpload.TABLE_RESPUESTAS_UPDATE,htListUpdate,false);
                        Log.v("Valor guardado id",String.valueOf(String.valueOf(pkey)));
                        htListUpdate.clear();
                        // do what do you want on your interface
                    }
                    Log.v("Valor guardado id 4","data sync");
                    JSONArray data3  = jsonObj.getJSONArray(DataUpload.TABLE_ENCUESTAS_ESTADO_UPDATE);
                    // looping through All nodes
                    for (int i = 0; i < data3.length(); i++) {
                        JSONObject c = data3.getJSONObject(i);
                        htListUpdate= new ArrayList<String>();
                        int id = c.getInt("id");
                        String estatus = c.getString("estatus");
                        int version = c.getInt("version");
                        int operacion = c.getInt("operacion");
                        //use >  int id = c.getInt("duration"); if you want get an int
                        htListUpdate.add(String.valueOf(id));
                        htListUpdate.add(estatus);
                        htListUpdate.add(String.valueOf(version));
                        htListUpdate.add(String.valueOf(operacion));
                        SAVE_DATA_JSON_DB(DataUpload.TABLE_ENCUESTAS_ESTADO_UPDATE,htListUpdate,false);
                        Log.v("Valor guardado id",String.valueOf(String.valueOf(id)));
                        htListUpdate.clear();
                        // do what do you want on your interface
                    }
                    Log.v("Valor guardado id 5","data sync");
                    JSONArray data5  = jsonObj.getJSONArray(DataUpload.TABLE_TIPO_PREGUNTA_UPDATE);
                    for (int i = 0; i < data5.length(); i++) {
                        JSONObject c = data5.getJSONObject(i);
                        htListUpdate= new ArrayList<String>();
                        String id = c.getString("pkey");
                        String tipo_pregunta = c.getString("tipo_pregunta");
                        String version = c.getString("version");
                        String operacion = c.getString("operacion");
                        //use >  int id = c.getInt("duration"); if you want get an int
                        htListUpdate.add(id);
                        htListUpdate.add(tipo_pregunta);
                        htListUpdate.add(version);
                        htListUpdate.add(operacion);
                        SAVE_DATA_JSON_DB(DataUpload.TABLE_TIPO_PREGUNTA_UPDATE,htListUpdate,false);
                        Log.v("Valor guardado id",String.valueOf(String.valueOf(id)));
                        htListUpdate.clear();
                        // do what do you want on your interface
                    }
                    Log.v("Valor guardado id 3","data sync");
                    JSONArray data2  = jsonObj.getJSONArray(DataUpload.TABLE_PREGUNTAS_UPDATE);
                    // looping through All nodes
                    for (int i = 0; i < data2.length(); i++) {
                        JSONObject c = data2.getJSONObject(i);
                        htListUpdate= new ArrayList<String>();
                        int pkey = c.getInt("pkey");
                        int id_tipo_pregunta = c.getInt("id_tipo_pregunta");
                        int id_encuesta = c.getInt("id_encuesta");
                        String pregunta = c.getString("pregunta");
                        int version = c.getInt("version");
                        int operacion = c.getInt("operacion");
                        //use >  int id = c.getInt("duration"); if you want get an int
                        htListUpdate.add(String.valueOf(pkey));
                        htListUpdate.add(String.valueOf(id_tipo_pregunta));
                        htListUpdate.add(String.valueOf(id_encuesta));
                        htListUpdate.add(pregunta);
                        htListUpdate.add(String.valueOf(version));
                        htListUpdate.add(String.valueOf(operacion));
                        SAVE_DATA_JSON_DB(DataUpload.TABLE_PREGUNTAS_UPDATE,htListUpdate,false);
                        Log.v("Valor guardado id",String.valueOf(String.valueOf(pkey)));
                        htListUpdate.clear();
                        // do what do you want on your interface
                    }
                    JSONArray server  = jsonObj.getJSONArray(DataUpload.TABLE_SERVER_UPDATE);
                    // looping through All nodes
                    for (int i = 0; i < server.length(); i++) {
                        JSONObject c = server.getJSONObject(i);
                        htListUpdate= new ArrayList<String>();
                        String ser = c.getString("server");
                        String usd = c.getString("usuario");
                        String pans = c.getString("pswd");
                        //use >  int id = c.getInt("duration"); if you want get an int
                        htListUpdate.add(ser);
                        htListUpdate.add(usd);
                        htListUpdate.add(pans);
                        SAVE_DATA_JSON_DB(DataUpload.TABLE_SERVER_UPDATE,htListUpdate,false);
                        htListUpdate.clear();
                        // do what do you want on your interface
                    }
                    try{
                        JSONArray servers  = jsonObj.getJSONArray(DataUpload.TABLE_DELETE_COMMAND);
                        boolean ActionDeleteAllInfoUserOneAction=true;
                        // looping through All nodes
                        for (int i = 0; i < servers.length(); i++) {
                            JSONObject c = servers.getJSONObject(i);
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
                    Log.v("Valor guardado id 1","usuarios encuesta");
                    JSONArray datan  = jsonObj.getJSONArray(dat);
                    // looping through All nodes
                    for (int i = 0; i < datan.length(); i++) {
                        JSONObject c = datan.getJSONObject(i);
                        htListUpdate= new ArrayList<String>();
                        int pkey = c.getInt("pkey");
                        int id_usuario = c.getInt("id_usuario");
                        int id_encuesta = c.getInt("id_encuesta");
                        int version = c.getInt("version");
                        int  operacion= c.getInt("operacion");
                        //use >  int id = c.getInt("duration"); if you want get an int
                        htListUpdate.add(String.valueOf(pkey));
                        htListUpdate.add(String.valueOf(id_usuario));
                        htListUpdate.add(String.valueOf(id_encuesta));
                        htListUpdate.add(String.valueOf(version));
                        htListUpdate.add(String.valueOf(operacion));
                        SAVE_DATA_JSON_DB(dat,htListUpdate,false);
                        Log.v("Valor guardado id",String.valueOf(pkey));
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
                Log.v("Valor error",e.getMessage());
                e.printStackTrace();
            }
        }
        if(isDeleted){
            file.delete();
        }
    }

    private boolean SAVE_DATA_JSON_DB(String casetable,ArrayList<String> RowListUpdate, boolean action){
        switch (casetable) {
            case DataUpload.TABLE_USUARIOS_ENCUESTAS_UPDATE:
                if(RowListUpdate.get(4).equals("0")){//0
                    //Insertar
                    dbUsersEncuesta.InsertUsuariosEncuestasSync(new tUsuarioEncuestas(
                            Integer.parseInt(RowListUpdate.get(0)),
                            Integer.parseInt(RowListUpdate.get(1)),
                            Integer.parseInt(RowListUpdate.get(2)),
                            RowListUpdate.get(3)));
                    Log.v("Guadado","TABLE_USUARIOS_ENCUESTAS_UPDATE");
                    return true;
                }
                else if(RowListUpdate.get(4).equals("1")){//1
                    //eliminar
                    dbUsersEncuesta.DeleteUsuariosEncuestasSync(
                            DataUpload.TABLE_USUARIOS_ENCUESTAS,
                            RowListUpdate.get(0),
                            RowListUpdate.get(1),
                            RowListUpdate.get(2));
                    return true;
                }
                break;
            case  DataUpload.TABLE_ENCUESTAS_UPDATE:
                if(RowListUpdate.get(3).equals("0")){//0
                    //Insertar
                    dbUsersEncuesta.InsertEncuestaSync(new tEncuestas(
                            Integer.parseInt(RowListUpdate.get(0)),
                            RowListUpdate.get(1),
                            Integer.parseInt(RowListUpdate.get(2)),
                            RowListUpdate.get(4)));
                    Log.v("Guadado","TABLE_ENCUESTAS_UPDATE");
                    return true;
                }
                else if(RowListUpdate.get(3).equals("1")){//1
                    //eliminar
                    dbUsersEncuesta.DeleteEncuestaSync(
                            DataUpload.TABLE_ENCUESTAS,
                            RowListUpdate.get(0),
                            RowListUpdate.get(1));
                    return true;
                }
                break;
            case  DataUpload.TABLE_RESPUESTAS_UPDATE:
                if(RowListUpdate.get(7).equals("0")){//0
                    //Insertar
                    dbUsersEncuesta.InsertRespuestaSync(new tRespuestas(
                            Integer.parseInt(RowListUpdate.get(0)),
                            Integer.parseInt(RowListUpdate.get(1)),
                            RowListUpdate.get(2),
                            RowListUpdate.get(3),
                            Integer.parseInt(RowListUpdate.get(4)),
                            Integer.parseInt(RowListUpdate.get(5)),
                            Integer.parseInt(RowListUpdate.get(6))));
                    Log.v("Guadado","TABLE_RESPUESTAS_UPDATE");
                    return true;
                }
                else if(RowListUpdate.get(7).equals("1")){//1
                    //eliminar
                    dbUsersEncuesta.DeleteRespuestaSync(
                            DataUpload.TABLE_RESPUESTAS,
                            RowListUpdate.get(0),
                            RowListUpdate.get(1));
                    return true;
                }
                break;
            case  DataUpload.TABLE_PREGUNTAS_UPDATE:
                if(RowListUpdate.get(5).equals("0")){//0
                    //Insertar
                    dbUsersEncuesta.InsertPreguntaSync(new tPreguntas(
                            Integer.parseInt(RowListUpdate.get(0)),
                            Integer.parseInt(RowListUpdate.get(1)),
                            Integer.parseInt(RowListUpdate.get(2)),
                            RowListUpdate.get(3),
                            Integer.parseInt(RowListUpdate.get(4))));
                    Log.v("Guadado","TABLE_PREGUNTAS_UPDATE");
                    return true;
                }
                else if(RowListUpdate.get(5).equals("1")){//1
                    //eliminar
                    dbUsersEncuesta.DeletePreguntaSync(
                            DataUpload.TABLE_PREGUNTAS,
                            RowListUpdate.get(0),
                            RowListUpdate.get(1),
                            RowListUpdate.get(2));
                    return true;
                }
                break;

            case  DataUpload.TABLE_ENCUESTAS_ESTADO_UPDATE:
                if(RowListUpdate.get(3).equals("0")){//0
                    //Insertar
                    dbUsersEncuesta.InsertEncuestaEstadoSync(new tEncuestasEstatus(
                            Integer.parseInt(RowListUpdate.get(0)),
                            RowListUpdate.get(1),
                            Integer.parseInt(RowListUpdate.get(2))));
                    Log.v("Guadado","TABLE_ENCUESTAS_ESTADO_UPDATE");
                    return true;
                }
                else if(RowListUpdate.get(3).equals("1")){//1
                    //eliminar
                    dbUsersEncuesta.DeleteEncuestaEstadoSync(
                            DataUpload.TABLE_ENCUESTAS_ESTADO,
                            RowListUpdate.get(0));
                    return true;
                }
                break;
            case  DataUpload.TABLE_TIPO_PREGUNTA_UPDATE:
                if(RowListUpdate.get(3).equals("0")){//0
                    //Insertar
                    dbUsersEncuesta.InsertTipoPreguntaSync(new tTipoPregunta(
                            Integer.parseInt(RowListUpdate.get(0)),
                            RowListUpdate.get(1),
                            Integer.parseInt(RowListUpdate.get(2))));
                    Log.v("Guadado","TABLE_TIPO_PREGUNTA_UPDATE");
                    return true;
                }
                else if(RowListUpdate.get(3).equals("1")){//1
                    //eliminar
                    dbUsersEncuesta.DeleteTipoPreguntaSync(
                            DataUpload.TABLE_TIPO_PREGUNTA,
                            RowListUpdate.get(0),
                            RowListUpdate.get(1));
                    return true;
                }
                break;
            case  DataUpload.TABLE_SERVER_UPDATE:
                    dbUsersEncuesta.InsertServerSync(RowListUpdate.get(0),RowListUpdate.get(1),RowListUpdate.get(2));
                    Log.v("Guadado","TABLE_SERVER");
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
