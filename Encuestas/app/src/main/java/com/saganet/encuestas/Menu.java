package com.saganet.encuestas;

import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

public class Menu extends AppCompatActivity {

    public static final String EXTRA_MENU_ID = "extra_menu_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Users fragment = (Users)
                getSupportFragmentManager().findFragmentById(R.id.content_menu);
        if (fragment == null) {
          fragment = Users.newInstance();
          getSupportFragmentManager()
                  .beginTransaction()
                  .add(R.id.content_menu, fragment)
                  .commit();
        }
    }
}
