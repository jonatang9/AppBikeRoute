package com.appbikeroute;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterDatosFotosRuta extends RecyclerView.Adapter<AdapterDatosFotosRuta.ViewHolderDatosFotos>
            implements View.OnClickListener{

    //private LayoutInflater inflater;
    private ArrayList<ItemFotoRuta> listaFotosRuta;
    private Context context;

    //private View.OnClickListener listener;

    public AdapterDatosFotosRuta(Context context, ArrayList<ItemFotoRuta> listaFotosRuta) {
        //this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listaFotosRuta = listaFotosRuta;
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public ViewHolderDatosFotos onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_fotos_ruta,parent,false);
        view.setOnClickListener(this);
        return new ViewHolderDatosFotos(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterDatosFotosRuta.ViewHolderDatosFotos holder, int position) {
        //holder.fotoRuta.setImageBitmap(listaFotosRuta.get(position).getFotoRuta());
        byte[] bytes = listaFotosRuta.get(position).getFotoRuta();
        holder.fotoRuta.setImageBitmap(BitmapFactory.decodeByteArray(bytes,0,bytes.length));
        //Glide.with(holder.fotoRuta.getContext()).load(listaFotosRuta.get(position).getFotoRuta()).into(holder.fotoRuta);
    }

    @Override
    public int getItemCount() {
        return (listaFotosRuta != null ? listaFotosRuta.size() : 0);
    }


    public class ViewHolderDatosFotos extends RecyclerView.ViewHolder {

        ImageView fotoRuta;
        //LinearLayout linearLayout;

        public ViewHolderDatosFotos(@NonNull @NotNull View itemView) {
            super(itemView);
            fotoRuta= itemView.findViewById(R.id.idimagen2);
            //linearLayout = (LinearLayout) itemView.findViewById(R.id.idLayout2);
        }
    }
}