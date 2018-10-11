package com.saganet.encuestas;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by Personal on 28/10/2016.
 */

public class EncuestasRealizadasCursorAdapt extends CursorAdapter {

    private TextView nameUserEncuestaEstatus;
    //---private int count=0;
    public EncuestasRealizadasCursorAdapt(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.item_encuesta, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //TextView nameEncuesta = (TextView) view.findViewById(R.id.tv_name_enc);
        TextView nameUserEncuesta = (TextView) view.findViewById(R.id.tv_user_enc);
        TextView nameUserEncuestaTime = (TextView) view.findViewById(R.id.tv_tiempo_enc);
        TextView nameUserEncuestaSync = (TextView) view.findViewById(R.id.tv_sync_enc);
        nameUserEncuestaEstatus = (TextView) view.findViewById(R.id.tv_status_enc);
        //TextView nameStatusEnc = (TextView) view.findViewById(R.id.tv_status_enc);
        //COLUMN_ID_USUARIO
        //String name1 = DataUpload.USER_ACCOUNT_ENC_NAME;
        String name2 = cursor.getString(cursor.getColumnIndex("nombre_c")) +" ";
        name2=name2+cursor.getString(cursor.getColumnIndex("paterno_c")) +" ";
        name2=name2+cursor.getString(cursor.getColumnIndex("materno_c"));
        int d= Integer.parseInt(cursor.getString(cursor.getColumnIndex("id_estado_re_en")));
        getStatus(d);
        nameUserEncuestaEstatus.setText(cursor.getString(cursor.getColumnIndex("text_estado")));
        nameUserEncuestaTime.setText(cursor.getString(cursor.getColumnIndex("fin_re_en")));
        nameUserEncuestaSync.setText(
                cursor.getString(cursor.getColumnIndex("id_estado_sync_re_en")) + " Folio: " +
                        cursor.getString(cursor.getColumnIndex("folio_re_en")
                ));
        //--nameUserEncuestaTime.setText(DataUpload.TIME_REDITABLE_ENCUESTA_LIST.get(count));
        //--count++;
        //nameEncuesta.setText(name1);
        nameUserEncuesta.setText(name2);
    }

    private void getStatus(int b){
        switch (b){
            case 1:
                nameUserEncuestaEstatus.setTextColor(Color.parseColor("#018410"));
                break;
            case 2:
                nameUserEncuestaEstatus.setTextColor(Color.parseColor("#ff2828"));
                break;
            case 3:
                nameUserEncuestaEstatus.setTextColor(Color.parseColor("#016b4b"));
                break;
            case 4:
                nameUserEncuestaEstatus.setTextColor(Color.parseColor("#0579bc"));
                break;
            case 5:
                nameUserEncuestaEstatus.setTextColor(Color.parseColor("#a809f2"));
                break;
            case 6:
                nameUserEncuestaEstatus.setTextColor(Color.parseColor("#f26609"));
                break;
            case 7:
                nameUserEncuestaEstatus.setTextColor(Color.parseColor("#0e913e"));
                break;
            case 8:
                nameUserEncuestaEstatus.setTextColor(Color.parseColor("#212121"));
                break;
            default:
                nameUserEncuestaEstatus.setTextColor(Color.parseColor("#8E5A02"));
                break;
        }
    }
}
