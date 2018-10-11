package com.saganet.encuestas;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Vibrator;
import android.provider.BaseColumns;

import net.sqlcipher.Cursor;

import java.net.PortUnreachableException;
import java.util.ArrayList;

/**
 * Created by Hazael on 20/10/2016.
 */

public abstract class DataUpload implements BaseColumns{
    //public  static final String DATA_BASE_NAME="com.saganet.db" + DATA_BASE_EXTENTION;
    public  static String USER_NAME;
    public  static String USER_ID="1";
    public  static String USER_ID_CONTACT;
    public  static String USER_ACCOUNT_ID;
    public  static String USER_ACCOUNT_ID_PKEY;
    public  static final String DATA_BASE_EXTENTION=".db";
    public  static final String DATA_BASE_NAME="base96" + DATA_BASE_EXTENTION;
    public  static final String DATA_BASE_NAME_SYNC="upload-user-";
    public  static final String DATA_BASE_SECURITY="!XLwrwPYiDUwy3LufUC#JQ9V7q%Ay9MhFGk1AoZvLQ=)abFYNn";
    public  static final String DATA_BASE_SECURITY_SYNC=USER_ID + USER_ACCOUNT_ID;
    public  static final int DATA_BASE_VERSION = 1;
    public  static String[] USER_IDS_CONTACTS;
    public  static String[] USER_ENCUESTAS_TODO;
    public  static String[] USER_ENCUESTAS_CONTACTS_TODO;
    public  static String USER_NEW_ACCOUNT_ID;
    public  static int USER_TYPE_RESULT=0;
    public  static int USER_CUESTIONARIO_COUNT=0;
    public  static String USER_ACCOUNT_ENC_ID;
    public  static String USER_ACCOUNT_ENC_ESTADO_ID;
    public  static String USER_ACCOUNT_ENC_NAME;
    public  static String USER_ACCOUNT_VERSION;
    public  static int USER_NEW_ACCOUNT_ENC_ID;
    public  static int NUMBER_ANSWER_TO_SAVE=0;
    public  static ArrayList<String> pregList ;
    public  static ArrayList<String> ptypeList ;
    public  static ArrayList<String> pIDList ;
    public  static ArrayList<String> EncuestaEstadoList ;

    public  static final  String LOCATION_DATA_SYNC="/sdcard/SyncEncuestas/";
    //http://cgf.mx/syncdm/app/
    //http://192.168.1.67:8080/syncdm/app/avisoEnvioArchivo
    public  static final  String UPLOAD_URL_FILE="http://cgf.mx/syncdm/app/subir_archivo";
    public  static final  String SYNC_URL_USUARIOS="http://cgf.mx/syncdm/app/usuarioUpdate";
    public  static final  String SYNC_URL_USUARIOS_ENCUESTAS="http://cgf.mx/syncdm/app/usuarioEncuestaUpdate";
    public  static final  String SYNC_URL_USUARIOS_ENCUESTAS_CONTACTOS="http://cgf.mx/syncdm/app/usuariosEncuestaContactos";
    public  static final  String SYNC_URL_ENCUESTAS="http://cgf.mx/syncdm/app/encuestaUpdate";
    public  static final  String SYNC_URL_CONTACTOS="http://cgf.mx/syncdm/app/contactosUpdate";
    public  static final  String SYNC_URL_AVISO_REQUEST="http://cgf.mx/syncdm/app/avisoEnvioArchivo";
    public  static final  String SYNC_URL_PROCESS_REQUEST="http://cgf.mx/syncdm/app/avisoProcesarCarga";
    public  static        String SYNC_URL_FTP_TRANSFER="www.cgf.mx";
    public  static final  int SYNC_URL_FTP_TRANSFER_PORT=21;
    public  static        String SYNC_URL_FTP_TRANSFER_PASS="uno";
    public  static        String SYNC_URL_FTP_TRANSFER_USER="ccruz";
    public  static final  String ExeptionResultSeverClave="error_clave_incorrecta";
    public  static final  String ExeptionResultSeverPass="error_usuario_no_existe";
    public  static final  String TABLE_CONTACTOS="contactos";
    public  static final  String TABLE_CONTACTOS_UPDATE="contactos_update";
    public  static final  String COLUMN_PKEY="pkey";
    public  static final  String COLUMN_SERVER_URL="server";
    public  static final  String COLUMN_SERVER_USER="usuario";
    public  static final  String COLUMN_ID_COMMAND="id_comando";
    public  static final  String COLUMN_DATE_APPLICATION="fecha_aplicacion";
    public  static final  String COLUMN_TYPE="tipo";
    public  static final  String COLUMN_SERVER_PASSWORD="pswd";
    public  static final  String COLUMN_ID_PRECARGA="id_precarga";
    public  static final  String COLUMN_ID_ENCUESTA="id_encuesta";
    public  static final  String COLUMN_ID_ENCUESTA_ESTADO="id_encuesta_estado";
    public  static final  String COLUMN_ID_ENCUESTA_ESTADO_SYNC="id_encuesta_estado_sync";
    public  static final  String COLUMN_FOLIO_SYNC="folio";
    public  static final  String COLUMN_NOMBRE="nombre";
    public  static final  String COLUMN_PATERNO="paterno";
    public  static final  String COLUMN_MATERNO="materno";
    public  static final  String COLUMN_VERSION="version";
    public  static final  String COLUMN_CADUCIDAD="caducidad";
    public  static final  String COLUMN_CALLE="calle";
    public  static final  String COLUMN_COLONIA="colonia";
    public  static final  String COLUMN_NUM_EXT="numero_ext";
    public  static final  String COLUMN_NUM_INT="numero_int";
    public  static final  String COLUMN_COD_POSTAL="codigo_postal";
    public  static final  String COLUMN_ENTIDAD="entidad";
    public  static final  String COLUMN_MUNICIPIO="municipio";
    public  static final  String COLUMN_FECHA_NAC="fecha_nacimiento";
    public  static final  String COLUMN_GENERO="genero";

    public  static final  String TABLE_ENCUESTAS="encuestas";
    public  static final  String TABLE_ENCUESTAS_UPDATE="encuestas_update";
    public  static final  String TABLE_ENCUESTAS_ESTADO="encuestas_estado";
    public  static final  String TABLE_ENCUESTAS_ESTADO_SYNC="encuestas_estado_sync";
    public  static final  String TABLE_ENCUESTAS_ESTADO_UPDATE="encuestas_estatus_update";
    public  static final  String COLUMN_ENCUESTA="encuesta";
    public  static final  String COLUMN_ENCUESTA_ESTADO="t_encuesta_estado";
    public  static final  String COLUMN_ENCUESTA_ESTADO_SYNC="t_encuesta_estado_sync";

    public  static final  String TABLE_USUARIOS_CONTACTOS="usuarios_contactos";
    public  static final  String TABLE_USUARIOS_CONTACTOS_UPDATE="usuarios_encuesta_contactos_update";
    public  static final  String COLUMN_ID_USUARIO="id_usuario";
    public  static final  String COLUMN_ID_CONTACTO="id_contacto";

    public  static final  String TABLE_USUARIOS="usuarios";
    public  static final  String TABLE_USUARIOS_UPDATE="usuario_update";
    public  static final  String TABLE_SEND_DATA_SERVER="aviso_envio_archivo";
    public  static final  String TABLE_SEND_PROCESS_SERVER="aviso_proceso_carga";
    public  static final  String COLUMN_NICK="nick";
    public  static final  String COLUMN_PASSWORD="password";

    public  static final  String TABLE_USUARIOS_ENCUESTAS="usuarios_encuestas";
    public  static final  String TABLE_USUARIOS_ENCUESTAS_UPDATE="usuarios_encuesta_update";

    public  static final  String TABLE_PREGUNTAS="preguntas";
    public  static final  String TABLE_PREGUNTAS_UPDATE="encuestas_preguntas_update";
    public  static final  String TABLE_SERVER_UPDATE="datos_servidor";
    public  static final  String TABLE_DELETE_COMMAND="eliminacion";
    public  static final  String COLUMN_ID_TIPO_PREGUNTA="id_tipo_pregunta";
    public  static final  String COLUMN_PREGUNTA="pregunta";

    public  static final  String TABLE_RESPUESTAS="respuestas";
    public  static final  String TABLE_RESPUESTAS_UPDATE="encuestas_preguntas_respuestas_update";
    public  static final  String COLUMN_ID_PREGUNTA="id_pregunta";
    public  static final  String COLUMN_ACCION_TIPO="accion_tipo";
    public  static final  String COLUMN_ACCION_VALOR="accion_valor";
    public  static final  String COLUMN_RESPUESTA="respuesta";

    public  static final  String TABLE_TIPO_PREGUNTA="pregunta_tipo";
    public  static final  String TABLE_TIPO_PREGUNTA_UPDATE="encuestas_tipos_pregunta_update";
    public  static final  String COLUMN_TIPO_PREGUNTA="tipo_pregunta";

    public  static final  String TABLE_RESPUESTAS_ENCUESTA_ID="respuestas_encuesta_id_";
    public  static final  String COLUMN_ENCUESTA_INICIO="inicio_encuesta";
    public  static final  String COLUMN_ENCUESTA_FIN="fin_encuesta";
    public  static final  String COLUMN_ENCUESTA_LATITUD="latitud_encuesta";
    public  static final  String COLUMN_ENCUESTA_LONGITUD="longitud_encuesta";

    public  static enum tipoPregunta
    {
        CerradaMultiple,
        CerradaSimple,
        CerradaFecha,
        AbiertaMultipleLinea,
        AbiertaSimpleLinea,
        None
    }

    public static enum eTypeAction{
        Siguiente,
        SaltarAPregunta,
        Terminar,
        None
    }
    public static eTypeAction tipoAccion= eTypeAction.None;
    public static tipoPregunta TypeAsk= tipoPregunta.None;

    public  static int ANSWERS_MINIMAL_TO_BLUILT=6;
    public  static boolean UPDATE_PREGUNTAS_COMPONENT=false;
    public  static boolean isMultiple=false;
    public  static boolean CAN_RETURN_EMPTY_ENCUESTA=false;
    public static String VALUE_INITIALIZED_TIME;
    public static String VALUE_FINALIZED_TIME;
    public static String VALUE_TIME;
    public static String VALUE_LATITUDE_POSITION="0.0";
    public static String VALUE_LONGITUDE_POSITION="0.0";
    public static String VALUE_IMEI_DATA_DEVICE="0000000";
    public static Boolean IS_REDITABLE_ENCUESTA_ID=false;
    public static String  ID_REDITABLE_ENCUESTA_ID="";
    public  static ArrayList<String> ID_REDITABLE_ENCUESTA_LIST ;
    public  static ArrayList<String> TIME_REDITABLE_ENCUESTA_LIST ;

    public static int SYNC_COLUMNS_DYMANICALLI_BLUIT=0;
    public static Cursor SYNC_COLUMNS_DATA_RESERVATION=null;
    public static boolean CONNETION_ALLOWED=false;
    public static boolean FINISH_ALL_AND_RESTART_LOGING= false;

    //Message alert system
    private static Vibrator vib;
    private static MediaPlayer mp;
    public static void AlertSoundVibrate(Context s, int Time){
        mp = MediaPlayer.create(s, R.raw.click);
        vib = (Vibrator) s.getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(Time);
        mp.start();
    }
    public static enum eTypeIcon{
        ErrorSync,
        SucessfulSync,
        CantSync,
        Checked,
        Help,
        None
    }

    public static NotificationManager notif;
    public static void ShowPushNotification(Context c,String msgTitle, String msgInfo,eTypeIcon ic) {
        notif=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);
        int iconc=0;
        switch (ic){
            case ErrorSync:
                iconc=R.drawable.ic_error_sync;
                break;
            case SucessfulSync:
                iconc=R.drawable.ic_can_sync;
                break;
            case Help:
                iconc=R.drawable.ic_help_icon;
                break;
            case CantSync:
                iconc=R.drawable.ic_cant_sync;
                break;
            case Checked:
                iconc=R.drawable.ic_check;
                break;
            default:
                break;
        }
        Notification notify=new Notification.Builder
                (c.getApplicationContext()).setContentTitle("Notificación").setContentText(msgInfo).
                setContentTitle(msgTitle).setSmallIcon(iconc).build();
        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notif.notify(0, notify);
    }
}
