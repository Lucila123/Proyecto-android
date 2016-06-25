package com.example.lucila.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import com.example.lucila.myapplication.Fragmentos.PerfilFragment;
import com.example.lucila.myapplication.Fragmentos.ReservasUsuarioFragment;
import com.example.lucila.myapplication.R;

/**
 * Created by Lucila on 25/6/2016.
 */
public class MenuActivity extends AppCompatActivity {

    private Toolbar toolbar;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        setupToolbar();

        Intent i = getIntent();
        int elegido = i.getExtras().getInt("opcion");

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch (elegido)

        {
            case 1: {


                getSupportActionBar().setTitle("Mi perfil");
                PerfilFragment perfilFragment = new PerfilFragment();
                fragmentTransaction.replace(R.id.containerView_Menu, perfilFragment);
                fragmentTransaction.commit();
                break;
            }
            case 2:
                break;
            case 3:

            {
                getSupportActionBar().setTitle("Mis reservas");
                ReservasUsuarioFragment reservasFragment = new ReservasUsuarioFragment();
                fragmentTransaction.replace(R.id.containerView_Menu, reservasFragment);
                fragmentTransaction.commit();
                break;
            }

        }
    }

    void setupToolbar() {


        toolbar = (Toolbar) findViewById(R.id.toolbar_menu); //encontramos la instancia de la toolbar
        setSupportActionBar(toolbar);   //la setamos a la actividad
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); //el boton de back

    }

}