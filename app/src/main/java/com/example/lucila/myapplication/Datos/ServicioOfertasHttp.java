package com.example.lucila.myapplication.Datos;

import android.app.Activity;
import android.app.Fragment;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.lucila.myapplication.Entidades.Deporte;
import com.example.lucila.myapplication.Entidades.Oferta;
import com.example.lucila.myapplication.Entidades.Usuario;
import com.example.lucila.myapplication.http.ConstantesAcceso;
import com.example.lucila.myapplication.http.VolleySingleton;
import com.example.lucila.myapplication.http.deporteDeserializer;
import com.example.lucila.myapplication.http.ofertaDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
//jason  para las consultas al web service
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * Created by tino on 21/05/2016.
 */
public class ServicioOfertasHttp implements Parcelable{


   private static  Activity activity;

    private static Gson gson;

    private static List<Oferta>ofertas;

    private static List<Deporte>deportes;

    private static MapeoIdDeporte mapeo;

    private static int  reservado;

    private static CallBack callback;

    private static ServicioOfertasHttp singletonInstance;
    /**
     * primero se deben obtener los deportes, para luego hacer el mapeo de id deporte a deporte en las ofertas
     * */
    private  ServicioOfertasHttp()
    {
        mapeo= new MapeoIdDeporte();
        ofertas= new ArrayList<Oferta>();
        deportes= new ArrayList<Deporte>();
        reservado=0;
    }

   public static  ServicioOfertasHttp getInstanciaServicio(CallBack c, Activity a){

       if(singletonInstance==null){
           singletonInstance=new ServicioOfertasHttp();
       }
       singletonInstance.callback=c;
       singletonInstance.activity=a;
       return singletonInstance;
   }



    /*
      realiza la peticion de las ofertas y deportes al servidor
      obetenemos los deportes del servidor y los guardamos en la lista deportes
      luego de eso pide las ofertas.
      */
    public static void realizarPeticion(){

        realizarPeticionDeportes("get_deportes");
    }
    //----------PARSELEABLE-----------------

    public static final Parcelable.Creator<ServicioOfertasHttp> CREATOR
            = new Parcelable.Creator<ServicioOfertasHttp>() {
        public ServicioOfertasHttp createFromParcel(Parcel in) {
            return new ServicioOfertasHttp(in);
        }

        public ServicioOfertasHttp[] newArray(int size) {
            return new ServicioOfertasHttp[size];
        }
    };

    @Override
    public   int describeContents() {
        return 0;
    }
    /**
     * constructor que se usa cuando usas el parsel
     * */
    public   ServicioOfertasHttp(Parcel in){


         ofertas=(List<Oferta>)in.readValue(List.class.getClassLoader());
         deportes=(List<Deporte>)in.readValue(List.class.getClassLoader());
         mapeo=(MapeoIdDeporte)in.readValue(MapeoIdDeporte.class.getClassLoader());
         reservado=in.readInt();

    }

    @Override
    public  void writeToParcel(Parcel dest, int flags) {

        dest.writeList(ofertas);
        dest.writeList(deportes);
        dest.writeValue(mapeo);
        dest.writeInt(reservado);


    }

   //--------PARSELEABLE------------------
    /**
     * reservado es seteado en true, en procesarRespuestaReserva, si el json tiene estado=1
     * caso contrario reservado sera false
     * */

    public static Boolean reservarOferta(Oferta oferta, Usuario user) {
        realizarPeticionReserva(oferta,user.getIdUsuario());

       return true;
    }


    public static List<Oferta> getOfertas() {

        return ofertas;
    }


    public static List<Oferta> getOfertasUbicacion(String ubicacion) {
        List<Oferta> salida= new ArrayList<Oferta>();
        for(int i=0;i<ofertas.size();i++) {
            if(ofertas.get(i).getUbicacion().equals(ubicacion))
                salida.add(ofertas.get(i));

        }
        return salida;


    }


    public static List<Oferta> getOfertasDeporte(Deporte deporte) {
         List<Oferta> salida= new ArrayList<Oferta>();
            for(int i=0;i<ofertas.size();i++) {
                Oferta ofAux=ofertas.get(i);
                if(ofAux.getDeporte()!=null) {
                    if (ofAux.getDeporte().getNombre().equals(deporte.getNombre()))
                        salida.add(ofertas.get(i));

                }
                }
        return salida;
    }



    public static Oferta getOfertaCodigo(Long codigo) {

        for(int i=0;i<ofertas.size();i++) {
            if(ofertas.get(i).getCodigo().equals(codigo))
               return ofertas.get(i);

        }
        return null;
    }


    public static List<Deporte> getDeportes()
    {

        return deportes;

    }


    public static int cantidadDeportes() {
      return   deportes.size();
    }


    public static Deporte getDeporte(String nombre) {

        for(int i=0;i<deportes.size();i++){
            if (deportes.get(i).getNombre().equals(nombre))
                return deportes.get(i);

        }
        return null;
    }

    /**
    get representa la funcion que se quiere llamar cuya url se saca de constantes oferta
     param representa el parametro especifico para la funcion, en caso que se quieran todas las ofertas estara null.
     */
    private static void realizarPeticionOfertas(String get, String param){


        // Petición GET
        VolleySingleton.getInstance(activity).addToRequestQueue(
                        new JsonObjectRequest(
                                Request.Method.GET,
                                ConstantesAcceso.getURL(get,param),
                                null,
                                new Response.Listener<JSONObject>() {

                                    @Override
                                    public void onResponse(JSONObject response) {
                                        // Procesar la respuesta Json
                                        String respuesta=response.toString();

                                        procesarRespuestaOfertas(response);
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Log.d(activity.getClass().getSimpleName(), "Error Volley en ofertas: " + error.getMessage());
                                    }
                                }

                        )
                );

    }

    /**
     * Interpreta los resultados de la respuesta para las ofertasy así
     * realizar las operaciones correspondientes
     *
     * @param response Objeto Json con la respuesta
     */
    private  static void procesarRespuestaOfertas(JSONObject response) {
        Oferta[] ofertaArray=null;
        try {

            // Obtener atributo "estado"
            String estado = response.getString("estado");
          //  Log.d(activity.getClass().getSimpleName(), "estado " + estado);
            switch (estado) {
                case "1": // EXITO
                    // Obtener array "ofertas" Json
                    JSONArray mensaje = response.getJSONArray("ofertas");
                    String cadenaRecibida=mensaje.toString();

                    GsonBuilder gBuilder = new GsonBuilder();
                    gBuilder.registerTypeAdapter(Oferta.class,new ofertaDeserializer(mapeo));
                    gson = gBuilder.create();
                     ofertaArray= gson.fromJson(cadenaRecibida, Oferta[].class);
                    ofertas=new ArrayList<Oferta>();
                    for(int i=0;i<ofertaArray.length;i++){

                        ofertas.add(ofertaArray[i]);

                    }
                    break;
                case "2": // FALLIDO
                    String mensaje2 = response.getString("mensaje");

                    Toast.makeText(
                            activity,
                            mensaje2,
                            Toast.LENGTH_LONG).show();
                    break;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        callback.dibujarListaOfertas();

    }

    /**
     get representa la funcion que se quiere llamar cuya url se saca de constantes oferta
     param representa el parametro especifico para la funcion, en caso que se quieran todas las ofertas estara null.
     */
    private static void realizarPeticionDeportes(String get){


        // Petición GET
        VolleySingleton.getInstance(activity).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        ConstantesAcceso.getURL(get,null),
                        null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta Json
                                String respuesta=response.toString();
                                //  Log.d(activity.getClass().getSimpleName(), "Recibido del serv: " + respuesta);
                            procesarRespuestaDeportes(response);

                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(activity.getClass().getSimpleName(), "Error Volley en deportes: " + error.getMessage());
                            }
                        }

                )
        );

    }

/**
 * Interpreta los resultados de la respuesta y así
 * realizar las operaciones correspondientes a la obtencion de deportes
 *
 * @param response Objeto Json con la respuesta
 */
private static void  procesarRespuestaDeportes(JSONObject response) {
    Deporte[]deportesArray= null;

    try {


        String estado = response.getString("estado");

        switch (estado) {
            case "1": // EXITO
                // Obtener array "deportes" Json
                JSONArray mensaje = response.getJSONArray("deportes");
                String cadenaRecibida=mensaje.toString();

               // Log.d(activity.getClass().getSimpleName(), "Deportes " + cadenaRecibida);

                GsonBuilder gBuilder = new GsonBuilder();
                gBuilder.registerTypeAdapter(Deporte.class,new deporteDeserializer());
                gson = gBuilder.create();
                deportesArray= gson.fromJson(cadenaRecibida, Deporte[].class);

                break;
            case "2": // FALLIDO
                String mensaje2 = response.getString("mensaje");

                Toast.makeText(
                        activity,
                        mensaje2,
                        Toast.LENGTH_LONG).show();
                break;
        }

    }
    catch (JSONException e) {
        e.printStackTrace();
    }
    deportes=new ArrayList<Deporte>();
    for ( int i=0;i<deportesArray.length;i++)
    {
        deportes.add( deportesArray[i]);
        mapeo.insert(deportesArray[i].getIdDeporte(),deportesArray[i]);  //establecemos el mapeo

    }

    realizarPeticionOfertas("get_ofertas", null);

    }

    /**
     * realiza la peticion de la reserva, tiene que recibir que oferta y que usuario
     * */
    private static void realizarPeticionReserva(final Oferta oferta, String idUser){
        long idOferta=oferta.getCodigo();
        String url = ConstantesAcceso.getURLRerserva(idOferta+"",idUser);
        Log.d("reserva oferta ","url "+url);
        VolleySingleton.getInstance(activity).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.GET,
                        url,
                        null,
                        new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta Json
                                String respuesta=response.toString();

                                procesarRespuestaReserva(response,oferta);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(activity.getClass().getSimpleName(), "Error Volley: " + error.getMessage());
                            }
                        }

                )
        );
    }


  private static void procesarRespuestaReserva(JSONObject response,Oferta oferta){


           try {
                  String estado = response.getString("estado");

               switch (estado) {
                   case "1": // EXITO
                       callback.reservaExito();
                       break;
                   case "2": // FALLIDO
                       callback.reservaFallo();
                       break;
               }

           }
           catch (JSONException e) {
               e.printStackTrace();
           }

   }
    /**
     * interface para establecer los callbacks
     * */
     public interface CallBack{

         public void reservaExito();

         public void reservaFallo();

         public void dibujarListaOfertas();
    }
}
