package com.saganet.encuestas;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;

public class setcuestionario extends AppCompatActivity {

    public static final int REQUEST_SAVED_ENCUESTA = 5;
    Preguntas p;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setcuestionario);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String id= getIntent().getStringExtra(MenuEncuestasContent.EXTRA_MENU_ENCUESTA_ID);
        //Log.v("EL CAMPO SELECIONADO:", id);
        p= (Preguntas)
                getSupportFragmentManager().findFragmentById(R.id.cont_pregunt);
        if(p==null) {
            p= Preguntas.newInstance(id);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.cont_pregunt, p)
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu n)
    {
        getMenuInflater().inflate(R.menu.menu_cuestionar, n);
        return  super.onCreateOptionsMenu(n);
    }

    @Override
    public boolean onSupportNavigateUp() {
        Log.v("Valor", "Reotnando button a la posiscion anterior");
        p.getForceFinish();
        if(DataUpload.CAN_RETURN_EMPTY_ENCUESTA){
            onBackPressed();
        }
        return true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch(keyCode){
            case KeyEvent.KEYCODE_BACK:
                Log.v("Valor", "Reotnando hardware a la posiscion anterior");
                p.getForceFinish();
                if(DataUpload.CAN_RETURN_EMPTY_ENCUESTA){
                    onBackPressed();
                }
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
