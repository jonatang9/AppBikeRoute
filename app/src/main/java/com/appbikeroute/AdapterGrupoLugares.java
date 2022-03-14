package com.appbikeroute;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class AdapterGrupoLugares extends RecyclerView.Adapter<AdapterGrupoLugares.MyViewHolder> {

    private Context context;

    private ArrayList<ItemGrupoListaLugares> listafotos;


    public AdapterGrupoLugares(Context context, ArrayList<ItemGrupoListaLugares> listafotos) {
        this.context = context;
        this.listafotos = listafotos;
    }

    @NonNull
    @NotNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grupo_lugares,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterGrupoLugares.MyViewHolder holder, int position) {

        holder.textDescrip.setText(listafotos.get(position).getDescrip());
        holder.textTitulo.setText(listafotos.get(position).getTitulo());

        //ArrayList<ArrayList<ItemFotoRuta>> itemsFotoRuta = ;
        ArrayList<ItemFotoRuta> itemFotoRuta = listafotos.get(position).getRecyclerListfotos();

        AdapterDatosFotosRuta adapterDatosFotosRuta = new AdapterDatosFotosRuta(context, itemFotoRuta);
        holder.recycler_fotos.setHasFixedSize(true);
        holder.recycler_fotos.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        holder.recycler_fotos.setAdapter(adapterDatosFotosRuta);



    }

    @Override
    public int getItemCount() {
        return (listafotos != null ? listafotos.size() : 0);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        TextView textDescrip;
        TextView textTitulo;
        RecyclerView recycler_fotos;

        public MyViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            textTitulo = (TextView) itemView.findViewById(R.id.tituloText);
            textDescrip = (TextView) itemView.findViewById(R.id.textLugares);
            recycler_fotos = (RecyclerView) itemView.findViewById(R.id.recyclerfotoslugares);

        }
    }
}
