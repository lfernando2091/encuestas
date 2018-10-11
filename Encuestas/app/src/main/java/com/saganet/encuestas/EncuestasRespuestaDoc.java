package com.saganet.encuestas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

public class EncuestasRespuestaDoc extends AppCompatActivity {
    public static final int REQUEST_SYNC_DATA_ENC = 6;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encuestas_respuesta_doc);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        EncRealizadasFragmennt fragment = (EncRealizadasFragmennt)
                getSupportFragmentManager().findFragmentById(R.id.content_encuestas_respuesta_doc);
        if (fragment == null) {
            fragment = EncRealizadasFragmennt.newInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_encuestas_respuesta_doc, fragment)
                    .commit();
        }
    }
}
