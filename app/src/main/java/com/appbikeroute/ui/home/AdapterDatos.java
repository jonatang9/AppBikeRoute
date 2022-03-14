package com.appbikeroute.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.appbikeroute.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;

public class AdapterDatos extends RecyclerView.Adapter<AdapterDatos.ViewHolderDatos>
            implements View.OnClickListener{

    LayoutInflater inflater;
   ArrayList<PersonajeVo> listDatos;
    private View.OnClickListener listener;
    int positionMarcada= 0;
    final long ONE_MEGABYTE = 1024 * 1024*10;


    public AdapterDatos(Context context,ArrayList<PersonajeVo> listDatos) {
        this.inflater = LayoutInflater.from(context);
        this.listDatos = listDatos;

    }


    @Override
    public @NotNull ViewHolderDatos onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_list,parent,false);

        view.setOnClickListener(this);
        return new ViewHolderDatos(view);

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AdapterDatos.ViewHolderDatos holder, int position) {

    holder.EtiNombre.setText(listDatos.get(position).getNombre());
    holder.EtiDesnivel.setText(listDatos.get(position).getDesn());
    holder.EtiAltura.setText(listDatos.get(position).getAlt());
    holder.EtiDistancia.setText(listDatos.get(position).getDist());
    holder.EtiPendiente.setText(listDatos.get(position).getPend());
    holder.foto.setImageURI(Uri.fromFile(listDatos.get(position).getFoto()));
    holder.foto.setVisibility(View.GONE);

        holder.bDescargarRuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.bDescargarRuta.setVisibility(View.GONE);
                listDatos.get(position).setRevBoton(0);
                listDatos.get(position).setDescargando(1);
                holder.progressBar.setVisibility(View.VISIBLE);
                holder.linearLayout2.setVisibility(View.GONE);

                MyTaskParams params = new MyTaskParams(position, holder, v);
                DescargarArchivosRuta descargarArchivosRuta = new DescargarArchivosRuta();
                descargarArchivosRuta.execute(params);

            }
        });
    if (listDatos.get(position).getRevBoton() == 0 && listDatos.get(position).getDescargando()==0){
        holder.bDescargarRuta.setVisibility(View.GONE);
        holder.linearLayout2.setVisibility(View.VISIBLE);
        holder.bOpcionCardView.setVisibility(View.VISIBLE);
        holder.foto.setVisibility(View.VISIBLE);
    }else {
        if (listDatos.get(position).getRevBoton() == 1 && listDatos.get(position).getDescargando()==0){
            holder.progressBar.setVisibility(View.GONE);
            holder.linearLayout2.setVisibility(View.VISIBLE);
            holder.bDescargarRuta.setVisibility(View.VISIBLE);
            holder.bOpcionCardView.setVisibility(View.GONE);
            holder.foto.setVisibility(View.GONE);
        }else {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.linearLayout2.setVisibility(View.GONE);
            holder.bDescargarRuta.setVisibility(View.GONE);
            holder.bOpcionCardView.setVisibility(View.GONE);
        }

    }



        holder.bOpcionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(v.getContext(),holder.bOpcionCardView);
                popupMenu.inflate(R.menu.menu_card_view);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if (item.getItemId() == R.id.eliminarRuta) {
                            //Toast.makeText(context.getApplicationContext(), nombreruta, Toast.LENGTH_SHORT).show();
                            String temp = listDatos.get(position).getNombre();
                            //temp = limpiarCaracteresNoPermitidos(temp);
                            //temp = "r"+temp+".geojson";
                            //Log.e("ERROR Adapter 1","-"+temp+"-" );
                            //v.getContext().deleteFile(temp);
                            String path = v.getContext().getExternalFilesDir(null).toString() + "/Rutas/" + temp;
                            File archivo = new File(path);
                            deleteRecursive(archivo);
                            holder.progressBar.setVisibility(View.GONE);
                            holder.foto.setImageResource(R.drawable.ic_menu_camera);
                            holder.foto.setVisibility(View.GONE);
                            listDatos.get(position).setRevBoton(1);
                            holder.bDescargarRuta.setVisibility(View.VISIBLE);
                            holder.bOpcionCardView.setVisibility(View.GONE);
                            holder.linearLayout2.setVisibility(View.VISIBLE);
                            return true;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


    }
    private static class MyTaskParams {
        int posicion;
        ViewHolderDatos viewHolderDatos;
        View view;

        MyTaskParams(int posicion, ViewHolderDatos viewHolderDatos, View view) {
            this.posicion = posicion;
            this.viewHolderDatos = viewHolderDatos;
            this.view = view;
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class DescargarArchivosRuta extends AsyncTask<MyTaskParams,ViewHolderDatos,ViewHolderDatos> {

        @Override
        protected ViewHolderDatos doInBackground(MyTaskParams... parametro) {

            readData(0, parametro[0], null, new Mycallback() {
                @Override
                public void onCallback(String nombreCarpeta) {
                    readData(1, parametro[0], nombreCarpeta, new Mycallback() {
                        @Override
                        public void onCallback(String nombreFoto) {
                            File myFile = new File(parametro[0].view.getContext()
                                    .getExternalFilesDir(null).toString() + "/Rutas/"+nombreCarpeta+"/Fotos/"+nombreFoto);
                            listDatos.get(parametro[0].posicion).setFoto(myFile);
                            parametro[0].viewHolderDatos.foto.setImageURI(Uri.fromFile(myFile));
                            listDatos.get(parametro[0].posicion).setDescargando(0);
                            parametro[0].viewHolderDatos.foto.setVisibility(View.VISIBLE);
                            parametro[0].viewHolderDatos.progressBar.setVisibility(View.GONE);
                            parametro[0].viewHolderDatos.linearLayout2.setVisibility(View.VISIBLE);
                            parametro[0].viewHolderDatos.bOpcionCardView.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });

            return parametro[0].viewHolderDatos;
        }

        @Override
        protected void onPostExecute(ViewHolderDatos s) {
            //super.onPostExecute(s);

        }
    }

    //////////////////////////////////////////////////////////////////////////-->
    private interface Mycallback{
        void onCallback(String value);
    }


    public void readData(int opcion,MyTaskParams parametro,String nombre,Mycallback myCallback) {

        String nombreCarpeta = listDatos.get(parametro.posicion).getNombre();
        switch (opcion){
            case 0:


                StorageReference storageReference2 = FirebaseStorage.getInstance().getReference();
                StorageReference ref2 = storageReference2.child("Mapas/GeoJson");


                String nameRuta = "r"+listDatos.get(parametro.posicion).getNombre()+".geojson";
                //Toast.makeText(v.getContext(),nameRuta, Toast.LENGTH_SHORT).show();
                nameRuta = limpiarCaracteresNoPermitidos(nameRuta);

                String finalNameRuta = nameRuta;
                File file = new File(parametro.view.getContext().getExternalFilesDir(null).toString()+"/Rutas/"+nombreCarpeta);
                if(!file.exists()) {
                    file.mkdirs();
                }
                File file2 = new File(file,finalNameRuta);
                ref2.child(nameRuta).getFile(file2)
                        .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        myCallback.onCallback(nombreCarpeta);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.e("ERROR", "No ha sido posible descargar en el archivo" +e.toString());
                    }
                });
                break;
            case 1:
                storageReference2 = FirebaseStorage.getInstance().getReference();
                StorageReference ref3 = storageReference2.child("Mapas/Fotos");

                ref3.child(nombre).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        int contador=0;
                        for (StorageReference item : listResult.getItems()){
                            String nombreFoto= item.getName();
                            contador++;
                            int finalContador = contador;
                            readData(2, parametro, item.getName(), new Mycallback() {
                                @Override
                                public void onCallback(String value) {
                                    if (finalContador ==1){
                                        myCallback.onCallback(nombreFoto);
                                    }
                                }
                            });

                        }
                    }
                });
                break;
            case 2:
                StorageReference storageReference3 = FirebaseStorage.getInstance().getReference();
                StorageReference ref4 = storageReference3.child("Mapas/Fotos").child(nombreCarpeta);
                File file3 = new File(parametro.view.getContext().getExternalFilesDir(null).toString()+"/Rutas/"+nombreCarpeta+"/Fotos");
                if(!file3.exists()) {
                    file3.mkdirs();
                }
                File file4 = new File(file3,nombre);
                ref4.child(nombre).getFile(file4).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        myCallback.onCallback(null);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.e("ERROR", "No ha sido posible descargar en el archivo" +e.toString());
                    }
                });
                break;

            default:
                break;
        }


    }

//////////////////////////////////////////////////////////////////////////<----

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);
        fileOrDirectory.delete();
    }

    @Override
    public int getItemCount() {
        return listDatos.size();
    }

    public void setOnClickListener( View.OnClickListener listener2){
        this.listener = listener2;
    }

    @Override
    public void onClick(View v) {
        if (listener != null){
            listener.onClick(v);
        }
    }

    public void filtrar(ArrayList<PersonajeVo> filtrarLista) {
        listDatos.clear();
        /*Collections.sort(filtrarLista, new Comparator<PersonajeVo>() {
            @Override
            public int compare(PersonajeVo a, PersonajeVo b) {
                return a.getRevBoton() - b.getRevBoton();
            }
        });*/
        this.listDatos.addAll(filtrarLista);
        notifyDataSetChanged();
    }


    static class ViewHolderDatos extends RecyclerView.ViewHolder {
        TextView EtiNombre, EtiDistancia,EtiAltura,EtiPendiente, EtiDesnivel;
        ImageView foto;

        LinearLayout linearLayout,linearLayout2;
        Button bDescargarRuta;
        ImageView bOpcionCardView;
        Context context;
        ProgressBar progressBar;


        public ViewHolderDatos(@NonNull @NotNull View itemView) {
            super(itemView);
            context = itemView.getContext();
            EtiNombre = (TextView) itemView.findViewById(R.id.idNombre);
            EtiDistancia = (TextView) itemView.findViewById(R.id.idTextDistancia);
            EtiAltura = (TextView) itemView.findViewById(R.id.idTextAltura);
            EtiPendiente = (TextView) itemView.findViewById(R.id.idTextPendiente);
            EtiDesnivel = (TextView) itemView.findViewById(R.id.idTextDesnivel);
            foto = (ImageView) itemView.findViewById(R.id.idImagen);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.idLayout);
            linearLayout2 = (LinearLayout) itemView.findViewById(R.id.idLinearLayout);
            bDescargarRuta = (Button) itemView.findViewById(R.id.bDescargarRuta);
            bOpcionCardView= (ImageButton) itemView.findViewById(R.id.bMenuOpcCardView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);

        }

    }


    private String limpiarCaracteresNoPermitidos(String nombre) {
        nombre = nombre.toLowerCase();
        nombre = nombre.replace(" ", "_");
        nombre = nombre.replace("ñ", "n");
        nombre = nombre.replace("(", "");
        nombre = nombre.replace(")", "");
        // Normalizar texto para eliminar acentos, dieresis, cedillas y tildes
        nombre = Normalizer.normalize(nombre, Normalizer.Form.NFD);
        // Quitar caracteres no ASCII excepto la enie, interrogacion que abre, exclamacion que abre, grados, U con dieresis.
        nombre = nombre.replaceAll("[^\\p{ASCII}(N\u0303)(n\u0303)(\u00A1)(\u00BF)(\u00B0)(U\u0308)(u\u0308)]", "");
        // Regresar a la forma compuesta, para poder comparar la enie con la tabla de valores
        nombre = Normalizer.normalize(nombre, Normalizer.Form.NFC);
        return nombre;
    }

}