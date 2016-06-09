package com.example.lucila.turnosPP.actividades;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.example.lucila.turnosPP.beans.Oferta;
import com.example.lucila.turnosPP.constantes.Constantes;
import com.example.lucila.turnosPP.fragmentos.OfertasFragment;
import com.example.lucila.myapplication.R;
import com.example.lucila.turnosPP.beans.VolleySingleton;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class OfertasActivity extends AppCompatActivity implements OfertasFragment.OnListFragmentInteractionListener {

    private static final String TAG= OfertasActivity.class.getSimpleName();
    private static final String GSON= "gson";

    private Gson gson;
    private Oferta[] ofertas;
    private int idEstablecimiento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofertas);

        idEstablecimiento= getIntent().getIntExtra("id", 0);

        if(savedInstanceState == null) {
            gson = new Gson();
            cargarAdaptador();
        }
        else {
            ofertas= (Oferta[]) savedInstanceState.getSerializable("ofertas");
            settearFragmentListaOfertas();
        }

    }

    /**
     * Carga el adaptador con las metas obtenidas
     * en la respuesta
     */
    public void cargarAdaptador() {
        // Petición GET
        VolleySingleton.getInstance(this).
                        addToRequestQueue(
                            new JsonObjectRequest(
                                Request.Method.GET,
                                Constantes.GET_OFERTAS_ESTABLECIMIENTO + "&idEstablecimiento="+ idEstablecimiento,
                                null,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        procesarRespuesta(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(TAG, "Error Volley: " + error.getMessage());
                                    }
                                }

                        )
                );
    }

    /**
     * Interpreta los resultados de la respuesta y así
     * realizar las operaciones correspondientes
     *
     * @param response Objeto Json con la respuesta
     */
    private void procesarRespuesta(JSONObject response) {
        try {
            // Obtener atributo "estado"
            String estado = response.getString("estado");
            switch (estado) {
                case "1": // EXITO
                    // Obtener array "metas" Json
                    JSONArray mensaje = response.getJSONArray("ofertas");
                    // Parsear con Gson
                    ofertas = gson.fromJson(mensaje.toString(), Oferta[].class);
                    settearFragmentListaOfertas();
                    break;
                case "2": // FALLIDO
                    String mensajeError = response.getString("mensaje") + "id "+ idEstablecimiento;
                    Toast.makeText(
                            this,
                            mensajeError,
                            Toast.LENGTH_LONG).show();
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void settearFragmentListaOfertas() {
        Fragment listaOfertasFragment= OfertasFragment.newInstance(ofertas);
        FragmentTransaction transaction= getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.ver_ofertas_container, listaOfertasFragment);
        transaction.commit();
    }

    @Override
    public void onListFragmentInteraction(Oferta item) {

    }
}
