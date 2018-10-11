package com.saganet.encuestas;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class AddContacto extends AppCompatActivity {

    public static final int REQUEST_ADD_ACCOUNT = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contacto);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String actId = getIntent().getStringExtra(MenuEncuestasContent.EXTRA_MENU_ENCUESTA_ID);

        setTitle(actId == null ? "Añadir Contacto" : "Editar Contacto");
                    AddContactoFragment addEditLawyerFragment = (AddContactoFragment)
                    getSupportFragmentManager().findFragmentById(R.id.content_add_contacto);
        if (addEditLawyerFragment == null) {
            addEditLawyerFragment = AddContactoFragment.newInstance(actId);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content_add_contacto, addEditLawyerFragment)
                    .commit();
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
