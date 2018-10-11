package com.saganet.encuestas;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import net.sqlcipher.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Loging extends AppCompatActivity {

    private DataSesion dbsUsers;
    //private Spinner lusers;
    private EditText lusers;
    private EditText lpass;
    private String CrypPW;
    private ArrayList<String> htList;
    private ArrayList<String> htListP;
    private ArrayList<String> htListI;
    private ArrayList<String> htListUpdate;
    private ArrayList<String> RowListUpdate;
    private ProgressDialog pd = null;
    private ProgressDialog lod = null;
    private String ex = "";
    private ScrollView sv;
    private Boolean AutoLoging = true;
    private boolean REQUEST_NO_START_IN_LOGING=false;
    private static final int REQUEST_CODE_EMAIL = 1;
    private View linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loging);
        //activity_loging
        sv = (ScrollView) findViewById(R.id.activity_loging);
        //AddGoogleAccountNecesary();
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        DataUpload.VALUE_IMEI_DATA_DEVICE = telephonyManager.getDeviceId();
        //lusers=(Spinner) findViewById(R.id.txtusers) ;
        lusers = (EditText) findViewById(R.id.txtusers);
        lpass = (EditText) findViewById(R.id.txtcontrasenia);
        linearLayout= (View)  findViewById(R.id.linear_loging);
        // Instancia de helper
        dbsUsers = new DataSesion(this);
        getUsersData();
    }

    // @Override
    // public boolean onCreateOptionsMenu(android.view.Menu n) {
    //     getMenuInflater().inflate(R.menu.fist_sync, n);
    //     return super.onCreateOptionsMenu(n);
    // }
    private void ShowCurrentVideo(){
        Intent intent = new Intent(this, VideoBase.class);
        startActivity(intent);
    }
    // @Override
    //public boolean onOptionsItemSelected(MenuItem M) {
    //    switch (M.getItemId()) {
    //        case R.id.action_sync_first:
    //            ShowCurrentVideo();
    //            break;
    //    }
    //    return super.onOptionsItemSelected(M);
    //}

    private boolean getStateNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

    private class AsyncReadData extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            lod = new ProgressDialog(Loging.this);
            lod.setTitle("Sincronizando...");
            lod.setMessage("Descargando archivos.");
            lod.setCancelable(false);
            lod.show();
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            CREATE_DIRECTORY_FILES();
            if (getStateNetworkConnection()) {
                POST_DIRETORY_FILE();
                READ_DIRECTORY_JSON(true, DataUpload.TABLE_USUARIOS_UPDATE);
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
            if (!ex.isEmpty()) {
                //--Toast.makeText(Loging.this, ex, Toast.LENGTH_LONG).show();
                Snackbar.make(linearLayout, ex, Snackbar.LENGTH_LONG).setAction("Action", null).show();
                ex = "";
            } else {
                if(!REQUEST_NO_START_IN_LOGING){
                    AutoLoging = false;
                    getUsersData();
                }else {
                    //--Toast.makeText(Loging.this, "Usuario bloqueado, contacte al provedor.", Toast.LENGTH_SHORT).show();
                    Snackbar.make(linearLayout, "Usuario bloqueado, contacte al proveedor.", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                    DataUpload.ShowPushNotification(Loging.this,"Usuario bloqueado","Contacte a su proveedor de servicios SaGaNet.", DataUpload.eTypeIcon.CantSync);
                    REQUEST_NO_START_IN_LOGING=false;
                }
            }
        }
    }

    public void actloging(View v) {
        correction(v);
    }

    private void ShowEncuestasView(){
        Intent intent = new Intent(this, Menu.class);
        lpass.setText("");
        startActivity(intent);
        overridePendingTransition(R.anim.zoom_forward_in, R.anim.zoom_forward_out);
    }

    private void correction(View v) {
        //String ss= String.valueOf(lusers.getSelectedItemId());
        try {
            if (!lusers.getText().equals("") && !lpass.getText().toString().equals("")) {
                if (htList != null) {
                    if (htList.contains(lusers.getText().toString())) {
                        if (htListP.contains(lpass.getText().toString())) {
                            if (htList.contains(lusers.getText().toString())) {
                                String n = String.valueOf(htListP.get(
                                        htList.indexOf(
                                                lusers.getText().toString()
                                        )));
                                if (lpass.getText().toString().equals(n)) {
                                    //dbsUsers.getAllTablesName();
                                    DataUpload.USER_ID = String.valueOf(htListI.get(htList.indexOf(lusers.getText().toString())));
                                    //dbsUsers.getAllClientsData();
                                    ShowEncuestasView();
                                } else {
                                    Snackbar.make(v, "La contraseña no es la correcta ", Snackbar.LENGTH_LONG)
                                            .setAction("Action", null).show();
                                }
                            }
                        } else {
                            Snackbar.make(v, "La contraseña no es la correcta ", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    } else {
                        CrypPW = getMD5(lpass.getText().toString());
                        //Realizar la sync
                        new AsyncReadData().execute();
                    }
                } else {
                    CrypPW = getMD5(lpass.getText().toString());
                    //Realizar la sync
                    new AsyncReadData().execute();
                }
            } else {
                Snackbar.make(v, "Campos vacios ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        } catch (Exception e) {

        }
    }

    private String getMD5(String s) {
        MessageDigest m;
        try {
            m = MessageDigest.getInstance("MD5");
            m.update(s.getBytes(), 0, s.length());
            //System.out.println("MD5: "+new BigInteger(1,m.digest()).toString(16));
            return String.valueOf(new BigInteger(1, m.digest()).toString(16));
        } catch (NoSuchAlgorithmException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "";
    }

    private void getUsersData() {
        new LoadTaskEnc().execute();
    }

    private class LoadTaskEnc extends AsyncTask<Void, Void, Cursor> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Loging.this);
            pd.setTitle("Cargando...");
            pd.setMessage("Espere por favor.");
            pd.setCancelable(false);
            pd.show();
        }

        @Override
        protected Cursor doInBackground(Void... voids) {
            return dbsUsers.getAllUsuarios();
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
                htList = new ArrayList<String>();
                htListP = new ArrayList<String>();
                htListI = new ArrayList<String>();
                try {
                    if (cursor.moveToFirst()) {
                        do {
                            // Adding contact to list
                            htList.add(cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_NICK)));
                            htListP.add(cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_PASSWORD)));
                            htListI.add(cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_PKEY)));
                            DataUpload.USER_ACCOUNT_VERSION=cursor.getString(cursor.getColumnIndex(DataUpload.COLUMN_VERSION));
                        } while (cursor.moveToNext());
                        if (!AutoLoging) {
                            correction(sv);
                            AutoLoging = true;
                        }
                    }
                } catch (Exception e) {
                    //Log.v("Adaptacion : ",e.getMessage());
                }
                //cursor.close();
            } else {
                htList = new ArrayList<String>();
                htListP = new ArrayList<String>();
                htListI = new ArrayList<String>();
                htList = null;
                htListP = null;
                htListI = null;
                // Mostrar empty state
            }
        }
    }

    private void CREATE_DIRECTORY_FILES() {
        File dir = new File(DataUpload.LOCATION_DATA_SYNC);
        if (!dir.exists()) {
            dir.mkdirs();
            File outfile = new File(dir, "config.saganet");
            try {
                FileOutputStream fos = new FileOutputStream(outfile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            Log.v("Directorio creado:", dir.toString());
            //return true;
        }
        //--/storage/sdcard1/
        //return  false;
        //File outfile= new File(dir,DataUpload.TABLE_CONTACTOS+".txt");
    }

    private void POST_DIRETORY_FILE() {
        try {
            //primero especificaremos el origen de nuestro archivo a descargar utilizando
            //la ruta completa
            URL url = new URL(DataUpload.SYNC_URL_USUARIOS);

            //establecemos la conexión con el destino
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            //establecemos el método jet para nuestra conexión
            //el método setdooutput es necesario para este tipo de conexiones
            urlConnection.setReadTimeout(20000);
            urlConnection.setConnectTimeout(25000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("usuario", lusers.getText().toString())
                    .appendQueryParameter("pw", CrypPW)
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
            File SDCardRoot = new File(DataUpload.LOCATION_DATA_SYNC);
            //Get the text file
            File file = new File(SDCardRoot, DataUpload.TABLE_USUARIOS_UPDATE + ".json");
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
            while ((bufferLength = inputStream.read(buffer)) > 0) {

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

    private void READ_DIRECTORY_JSON(boolean isDeleted, String dat) {

        File dir = new File(DataUpload.LOCATION_DATA_SYNC);
        //Get the text file
        File file = new File(dir, dat + ".json");
        if (file.exists()) {
            //Read text from file
            //StringBuilder text = new StringBuilder();
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
                JSONArray exuser = null;
                JSONArray expass = null;
                // Getting data JSON Array nodes
                try {
                    data = jsonObj.getJSONArray(dat);
                } catch (Exception e) {
                    try {
                        exuser = jsonObj.getJSONArray(DataUpload.ExeptionResultSeverPass);
                    } catch (Exception ex) {
                        try {
                            expass = jsonObj.getJSONArray(DataUpload.ExeptionResultSeverClave);
                        } catch (Exception exe) {
                        }
                    }
                }
                if (exuser != null) {
                    // looping through All nodes
                    for (int i = 0; i < exuser.length(); i++) {
                        JSONObject c = exuser.getJSONObject(i);
                        ex = c.getString("descripcion");
                    }
                    Log.v("descripcion", ex);
                    Toast.makeText(this, ex, Toast.LENGTH_LONG).show();
                } else if (expass != null) {
                    // looping through All nodes
                    for (int i = 0; i < expass.length(); i++) {
                        JSONObject c = expass.getJSONObject(i);
                        ex = c.getString("descripcion");
                    }
                    Log.v("descripcion", ex);
                } else if (data != null) {
                    // looping through All nodes
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject c = data.getJSONObject(i);
                        htListUpdate = new ArrayList<String>();
                        int id = c.getInt("id");
                        String nick = c.getString("nick");
                        String nombre = c.getString("nombre");
                        String paterno = c.getString("paterno");
                        String materno = c.getString("materno");
                        String password = c.getString("password");
                        int version = c.getInt("version");
                        int operacion = c.getInt("operacion");
                        //use >  int id = c.getInt("duration"); if you want get an int
                        htListUpdate.add(String.valueOf(id));
                        htListUpdate.add(nick);
                        htListUpdate.add(nombre);
                        htListUpdate.add(paterno);
                        htListUpdate.add(materno);
                        htListUpdate.add(password);
                        htListUpdate.add(String.valueOf(version));
                        htListUpdate.add(String.valueOf(operacion));
                        SAVE_DATA_JSON_DB(dat, htListUpdate,false);
                        Log.v("Valor guardado id", String.valueOf(id));
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
                                    REQUEST_NO_START_IN_LOGING=true;
                                    dbsUsers.DeleteContactStateSync(id_usuario);
                                    //---Eliminados lo relacionado ha ese usuario
                                }else if(id_comando.equals("2")){
                                    REQUEST_NO_START_IN_LOGING=true;
                                    //---Eliminados completamente un reset
                                    dbsUsers.DeleteAllResultStateSync();
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
                e.printStackTrace();
            }
        }
        if (isDeleted) {
            file.delete();
        }
    }

    private boolean SAVE_DATA_JSON_DB(String casetable, ArrayList<String> RowListUpdate, boolean action) {
        switch (casetable) {
            case DataUpload.TABLE_USUARIOS_UPDATE:
                if (RowListUpdate.get(7).equals("0")) {//0
                    //Insertar
                    dbsUsers.InsertUsuariosSync(new tUsuarios(
                            Integer.parseInt(RowListUpdate.get(0)),
                            RowListUpdate.get(1),
                            RowListUpdate.get(2),
                            RowListUpdate.get(3),
                            RowListUpdate.get(4),
                            RowListUpdate.get(5),
                            Integer.parseInt(RowListUpdate.get(6))));
                    return true;
                } else if (RowListUpdate.get(7).equals("1")) {//1
                    //eliminar
                    dbsUsers.DeleteUsuariosSync(
                            DataUpload.TABLE_USUARIOS,
                            RowListUpdate.get(0),
                            RowListUpdate.get(1));
                    return true;
                }
                break;
            case DataUpload.TABLE_DELETE_COMMAND:
                dbsUsers.InsertDeleteActionsSync(
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
