package com.example.lucila.myapplication;
/**
 * Created by Agustin on 30/04/2016.
 * adaptador para la lista de ofertas
 * */

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.lucila.myapplication.Entidades.Oferta;


import java.util.List;
/*
public class MyAdapter extends ArrayAdapter<String> {
    Context context;
    int layoutResourseId;
    List<Oferta>data = null;

    public MyAdapter(Context c, int resource, List<Oferta>ofertas) {
        super(c, resource);
        context = c;
        layoutResourseId = resource;
        data = ofertas;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        MyHolder holder = null;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourseId, parent, false);
            holder = new MyHolder();

            holder.txtDeporte = (TextView) row.findViewById(R.id.deporte);
            row.setTag(holder);



        }
        else{
            holder=(MyHolder)row.getTag();

        }

        Oferta element=data.get(position);
        holder.getTxtTitle().setText(element.getDeporte());


        return row;
    }

    private static class MyHolder {


        TextView txtDeporte;


        public void setTxtTitle(TextView txtTitle) {
            this.txtDeporte = txtTitle;
        }

        public TextView getTxtTitle() {
            return txtDeporte;
        }

    }

}
*/
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>   implements View.OnClickListener{
   // private String[] mDataset;
    private List<Oferta>lista_ofertas;
    public Context contexto;
    private View.OnClickListener listener;
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView text_deporte;
        public TextView text_fecha;
        public TextView text_hora;
        public TextView text_ubicacion;
        public TextView id_oferta;
        public CardView cardview_lista;
        public ImageView imagenOferta;


        public ViewHolder(View v) {
            super(v);
            text_deporte = (TextView)v.findViewById(R.id.deporte);
            text_fecha = (TextView)v.findViewById(R.id.fecha);
            text_hora = (TextView)v.findViewById(R.id.hora);
            text_ubicacion = (TextView)v.findViewById(R.id.ubicacion);
            cardview_lista=(CardView)v.findViewById(R.id.cardview_lista);
            imagenOferta =(ImageView)v.findViewById(R.id.imagen_oferta);
            id_oferta=(TextView)v.findViewById(R.id.oferta_id);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter( List<Oferta>mis_datos, Context context) {
        contexto=context;
        lista_ofertas=mis_datos;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        // set the view's size, margins, paddings and layout parameters
        v.setOnClickListener(this);//le seteo el clink listener

        ViewHolder vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.text_deporte.setText(lista_ofertas.get(position).getDeporte());
        holder.text_ubicacion.setText(lista_ofertas.get(position).getUbicacion());
        holder.text_hora.setText(lista_ofertas.get(position).getHora().toString());
        holder.text_fecha.setText(lista_ofertas.get(position).getFecha().toString());
        holder.imagenOferta.setImageResource(lista_ofertas.get(position).getIdFoto());
        holder.id_oferta.setText(lista_ofertas.get(position).getCodigo().toString());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return lista_ofertas.size();
    }
    /*
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        //Toast.makeText(parent.getContext(),"item seleccionado"+lista_ofertas.get(position), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(" com.example.lucila.myapplication.ReservaOfertaActivity");
        contexto.startActivity(intent);
    }

    public  void onNothingSelected(AdapterView<?>vista){


    }
    */
    /**
     * establemcemos el clink listener
     *@param: listener. se asigna  desde fuera del adaptador
     * */

    public void ClickListener(View.OnClickListener listener) {
        this.listener = listener;
    }
    /**
     * redefinimos el metodo onClink
     * */
    @Override
    public void onClick(View view) {
        if(listener != null)
            listener.onClick(view);
    }
}