package com.appbikeroute;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterDatosPuertos extends RecyclerView.Adapter<AdapterDatosPuertos.ViewHolderDatosPuertos> {

    //private LayoutInflater inflater;
    private final ArrayList<ItemDatosPuertos> listaDatosPuertos;
    private final Context context;

    //private View.OnClickListener listener;

    public AdapterDatosPuertos(Context context, ArrayList<ItemDatosPuertos> listaDatosPuertos) {
        //this.inflater = LayoutInflater.from(context);
        this.context = context;
        this.listaDatosPuertos = listaDatosPuertos;
    }


    @Override
    public @NotNull ViewHolderDatosPuertos onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_datos_puertos,parent,false);
        //view.setOnClickListener(this);
        return new ViewHolderDatosPuertos(view);
    }

    @Override
    public void onBindViewHolder(@NotNull AdapterDatosPuertos.ViewHolderDatosPuertos holder, int position) {

        holder.numero.setText(listaDatosPuertos.get(position).getNumero());
        holder.altura.setText(listaDatosPuertos.get(position).getAltura());
        holder.pendiente.setText(listaDatosPuertos.get(position).getPendiente());
        holder.desnivel.setText(listaDatosPuertos.get(position).getDesnivel());
    }


    @Override
    public int getItemCount() {
        return (listaDatosPuertos != null ? listaDatosPuertos.size() : 0);
    }


    public static class ViewHolderDatosPuertos extends RecyclerView.ViewHolder {

        TextView numero,altura,pendiente,desnivel;
        //LinearLayout linearLayout;

        public ViewHolderDatosPuertos(@NonNull @NotNull View itemView) {
            super(itemView);
            numero= itemView.findViewById(R.id.idNumeroPuerto);
            altura= itemView.findViewById(R.id.idAlturaMaxPuerto);
            pendiente= itemView.findViewById(R.id.idPendientePuerto);
            desnivel= itemView.findViewById(R.id.idDesnivelPuerto);
        }
    }
}