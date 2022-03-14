package com.appbikeroute.ui.home;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.appbikeroute.DescripActivity;
import com.appbikeroute.R;
import com.appbikeroute.databinding.FragmentHomeBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.text.Normalizer;
import java.util.ArrayList;

import javax.security.auth.callback.Callback;

public class HomeFragment extends Fragment implements View.OnClickListener{

    private FragmentHomeBinding binding;

    public ArrayList<PersonajeVo> listaDatos,listaDatosFija;
    RecyclerView recycler;
    String name,name3;
    Button botonfiltro,botonAplicarFiltro;
    EditText filtroNombre, filtroDistanciaMax,filtroDistanciaMin,filtroAlturaMax,filtroAlturaMin,filtroPendienteMax,filtroPendientemin
            ,filtroDesnivelMax,filtroDesnivelmin;
    String numGrande="1000000000000",fDisMax=numGrande,fDisMin="0",fAltMax=numGrande,fAltMin="0",fPenMax=numGrande,fPenMin="0",fDesMax=numGrande,fDesMin="0";
    CheckBox cbdescargar,cbOnline;
    AdapterDatos adapter;
    int opdes;
    File imagen;
    int f1=0,f2=0,f3=1,f3NM=0,f3Nm=0,f4=1,f4NM=0,f4Nm=0,f5=1,f5NM=0,f5Nm=0,f6=1,f6NM=0,f6Nm=0;
    String[] temp = new String[1];
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    Switch sDistancia,sAltura,sPendiente,sDesnivel;
    LinearLayout lDistancia,lAltura,lPendiente,lDesnivel;

    private DatabaseReference mDataBase;



    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //getActivity().getActionBar().hide();


        ////// agregar buscador
        filtroNombre = root.findViewById(R.id.editTextNombreRuta);
        filtroNombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void afterTextChanged(Editable s) {
                filtrar(s.toString(),0);
            }
        });

        botonfiltro = root.findViewById(R.id.botonFiltros);
        botonfiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarFiltro();
            }
        });



        listaDatos = new ArrayList<>();
        listaDatosFija = new ArrayList<>();
        recycler = root.findViewById(R.id.rvRutas);
        recycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        ////////Agregar al recycler view las rutas de la base de datos realtime firebase
        mDataBase = FirebaseDatabase.getInstance().getReference();
        vectorRealTimeDatabseRutas();


        //Inicializar objeto storage
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        //se trae la referencia de la carpeta
        StorageReference ref = storageReference.child("Mapas/GeoJson");
        //se trae la referencia de los nombres de las rutas
        ref.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()){
                    name= item.getName();
                    //llamarArchivos(name);
                }

            }
        }).addOnFailureListener(e -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("Ha ocurrido un error, revisa tu conexion a internet");
            builder.setCancelable(true);
            builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        });

        return root;
    }

    public void filtrar(String texto,int op){
        ArrayList<PersonajeVo> filtrarLista = new ArrayList<>();
        switch (op){
            case 0:
            for (PersonajeVo personajeVo: listaDatosFija) {
                if (personajeVo.getNombre().toLowerCase().contains(texto.toLowerCase())
                        || personajeVo.getDist().toLowerCase().contains(texto.toLowerCase())
                        || personajeVo.getAlt().toLowerCase().contains(texto.toLowerCase())
                        || personajeVo.getPend().toLowerCase().contains(texto.toLowerCase())
                        || personajeVo.getDesn().toLowerCase().contains(texto.toLowerCase()) ){
                    filtrarLista.add(personajeVo);
                }
            }
            break;
            case 1:
                for (PersonajeVo personajeVo: listaDatosFija) {
                    if (personajeVo.getRevBoton() == 0 && cbdescargar.isChecked()){
                        filtrarLista.add(personajeVo);
                    } else if (personajeVo.getRevBoton() == 1 && cbOnline.isChecked()){
                        filtrarLista.add(personajeVo);
                    }
                }
                break;
            case 2:
                for (PersonajeVo personajeVo: listaDatosFija) {
                    if (fDisMax.isEmpty())fDisMax=numGrande;
                    if (fDisMin.isEmpty())fDisMin="0";
                    if ( Double.parseDouble(personajeVo.getDist()
                            .replace("Distancia:\n","").replace(" km","").replace(",","."))
                            <= Double.parseDouble(fDisMax)
                            &&  Double.parseDouble(personajeVo.getDist()
                            .replace("Distancia:\n","").replace(" km","").replace(",","."))
                            >= Double.parseDouble(fDisMin)  ){
                        filtrarLista.add(personajeVo);
                    }
                }
                break;
            case 3:
                for (PersonajeVo personajeVo: listaDatosFija) {
                    if (fAltMax.isEmpty())fAltMax=numGrande;
                    if (fAltMin.isEmpty())fAltMin="0";
                    if ( Double.parseDouble(personajeVo.getAlt()
                            .replace("Altura Max:\n","").replace(" m","").replace(",",""))
                            <= Double.parseDouble(fAltMax)
                            &&  Double.parseDouble(personajeVo.getAlt()
                            .replace("Altura Max:\n","").replace(" m","").replace(",",""))
                            >= Double.parseDouble(fAltMin)  ){
                        filtrarLista.add(personajeVo);
                    }
                }
                break;
            case 4:
                for (PersonajeVo personajeVo: listaDatosFija) {
                    if (fPenMax.isEmpty())fPenMax=numGrande;
                    if (fPenMin.isEmpty())fPenMin="0";
                    if ( Double.parseDouble(personajeVo.getPend()
                            .replace("Pendiente Max:\n","").replace(" %","").replace(",","."))
                            <= Double.parseDouble(fPenMax)
                            &&  Double.parseDouble(personajeVo.getPend()
                            .replace("Pendiente Max:\n","").replace(" %","").replace(",","."))
                            >= Double.parseDouble(fPenMin)  ){
                        filtrarLista.add(personajeVo);
                    }
                }
                break;
            case 5:
                for (PersonajeVo personajeVo: listaDatosFija) {
                    if (fDesMax.isEmpty())fDesMax=numGrande;
                    if (fDesMin.isEmpty())fDesMin="0";
                    if ( Double.parseDouble(personajeVo.getDesn()
                            .replace("Desnivel Acum:\n","").replace(" m","").replace(",",""))
                            <= Double.parseDouble(fDesMax)
                            &&  Double.parseDouble(personajeVo.getDesn()
                            .replace("Desnivel Acum:\nt","").replace(" m","").replace(",",""))
                            >= Double.parseDouble(fDesMin)  ){
                        filtrarLista.add(personajeVo);
                    }
                }
                break;
            default:
                break;
        }


        recycler.setAdapter(adapter);
        adapter.filtrar(filtrarLista);
    }


    private void vectorRealTimeDatabseRutas() {

        mDataBase.child("Rutas").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

                if(snapshot.exists()){

                    for (DataSnapshot ds: snapshot.getChildren()){
                        String i2 = ds.child("Nombre").getValue().toString();
                        String i = "r"+ds.child("Nombre").getValue().toString();
                        i = limpiarCaracteresNoPermitidos(i);
                        //int idarchivo = getResources().getIdentifier(i,"raw", getActivity().getPackageName());


                        //if (idarchivo==0) {
                        if (ArchivoExiste(i2)) {
                            StorageReference storageReference2 = FirebaseStorage.getInstance().getReference();
                            StorageReference ref2 = storageReference2.child("Mapas/Fotos");
                            ref2.child(ds.child("Nombre").getValue().toString()).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                @Override
                                public void onSuccess(ListResult listResult) {
                                    temp[0] = listResult.getItems().get(0).getName();
                                    imagen= new File(getContext()
                                            .getExternalFilesDir(null).
                                                    toString() + "/Rutas/"+ds.child("Nombre").getValue().toString()+"/Fotos/" + temp[0]);
                                    opdes=0;

                                    listaDatosFija.add(new PersonajeVo(ds.child("Nombre").getValue().toString()
                                            , "Distancia:\n" + ds.child("Distancia").getValue().toString() + " km"
                                            , "Altura Max:\n" + ds.child("Altura").getValue().toString() + " m"
                                            , "Pendiente Max:\n" + ds.child("Pendiente").getValue().toString() + " %"
                                            , "Desnivel Acum:\n" + ds.child("Desnivel").getValue().toString() + " m"
                                            ,imagen
                                            ,opdes,0));

                                    agregarItemRecyclerView();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Log.e("ERROR", "No ha sido posible ver en el archivo" +e.toString());
                                }
                            });
                            ///////////////

                        }else{
                            imagen= new File(String.valueOf(getContext().getDrawable(R.drawable.ic_menu_camera)));
                            opdes=1;
                            listaDatosFija.add(new PersonajeVo(ds.child("Nombre").getValue().toString()
                                    , "Distancia:\n" + ds.child("Distancia").getValue().toString() + " km"
                                    , "Altura Max:\n" + ds.child("Altura").getValue().toString() + " m"
                                    , "Pendiente Max:\n" + ds.child("Pendiente").getValue().toString() + " %"
                                    , "Desnivel Acum:\n" + ds.child("Desnivel").getValue().toString() + " m"
                                    ,imagen
                                    ,opdes,0));
                            agregarItemRecyclerView();
                        }


                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage("Ha ocurrido un error,\nNo se puedo obtener las rutas desde la nube, revisa tu conexion a internet");
                    builder.setCancelable(true);
                    builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

    }

    public String limpiarCaracteresNoPermitidos(String nombre) {
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

    private boolean ArchivoExiste(String i2){
        File archivo = new File(getContext().getExternalFilesDir(null).toString() +"/Rutas/"+i2);
        return archivo.exists();
    }

    private void agregarItemRecyclerView() {
        listaDatos.clear();
        listaDatos.addAll(listaDatosFija);
        adapter = new AdapterDatos(getContext(), listaDatos);
        recycler.setAdapter(adapter);
        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name3 = listaDatos.get(recycler.getChildAdapterPosition(v)).getNombre() ;
                //Intent i = new Intent(getActivity().getApplicationContext(),DescripActivity.class);
                //Toast.makeText(v.getContext(),name3, Toast.LENGTH_SHORT).show();
                v.setPressed(true);

                /// datos a enviar a Descripcion

                Intent intent = new Intent(getActivity(),DescripActivity.class);
                intent.putExtra("NOMBRE RUTA", name3);
                intent.putExtra("distotal",listaDatos.get(recycler.getChildAdapterPosition(v)).getDist());
                intent.putExtra("desnivel",listaDatos.get(recycler.getChildAdapterPosition(v)).getDesn());
                intent.putExtra("pendiente",listaDatos.get(recycler.getChildAdapterPosition(v)).getPend());
                intent.putExtra("altura",listaDatos.get(recycler.getChildAdapterPosition(v)).getAlt());
                intent.putExtra("descargado",listaDatos.get(recycler.getChildAdapterPosition(v)).getRevBoton());
                startActivity(intent);
            }
        });
    }

    private int revertirChecked(int pos){
        if(pos==0){ return 1;}else { return 0;}
    }
    private int visibleOrGone(int pos){
        if(pos==0){ return View.VISIBLE;}else { return View.GONE;}
    }

    private void mostrarFiltro(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflaterFiltro = getLayoutInflater();

        View viewFiltro = layoutInflaterFiltro.inflate(R.layout.filtro_personalizado,null);
        builder.setView(viewFiltro);

        final AlertDialog dialog = builder.create();
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();
        wmlp.gravity = Gravity.RIGHT | Gravity.TOP;
        wmlp.y = 250;
        wmlp.setTitle("Filtrar por:");
        dialog.show();
        dialog.getWindow().setLayout(800,1200);


        ////////////////////////////////////////////////////////////////////

        cbdescargar = viewFiltro.findViewById(R.id.cButtonDescargadas);
        cbdescargar.setChecked(f1 == 0);
        cbdescargar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f1=revertirChecked(f1);
                filtrar("",1);
            }
        });
        cbOnline= viewFiltro.findViewById(R.id.cButtonOnline);
        cbOnline.setChecked(f2 == 0);
        cbOnline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f2=revertirChecked(f2);
                filtrar("",1);
            }
        });
        sDistancia=viewFiltro.findViewById(R.id.switchDistancia);
        sAltura=viewFiltro.findViewById(R.id.switchAltura);
        sPendiente=viewFiltro.findViewById(R.id.switchPendiente);
        sDesnivel=viewFiltro.findViewById(R.id.switchDesnivel);
        lDistancia =viewFiltro.findViewById(R.id.layoutDistancia);
        lAltura=viewFiltro.findViewById(R.id.layoutAltura);
        lPendiente=viewFiltro.findViewById(R.id.layoutPendiente);
        lDesnivel=viewFiltro.findViewById(R.id.layoutDesnivel);

        sDistancia.setChecked(f3 == 0);
        lDistancia.setVisibility(visibleOrGone(f3));
        sDistancia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f3=revertirChecked(f3);
                lDistancia.setVisibility(visibleOrGone(f3));
            }
        });
        sAltura.setChecked(f4 == 0);
        lAltura.setVisibility(visibleOrGone(f4));
        sAltura.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f4=revertirChecked(f4);
                lAltura.setVisibility(visibleOrGone(f4));
            }
        });
        sPendiente.setChecked(f5 == 0);
        lPendiente.setVisibility(visibleOrGone(f5));
        sPendiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f5=revertirChecked(f5);
                lPendiente.setVisibility(visibleOrGone(f5));
            }
        });
        sDesnivel.setChecked(f6 == 0);
        lDesnivel.setVisibility(visibleOrGone(f6));
        sDesnivel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f6=revertirChecked(f6);
                lDesnivel.setVisibility(visibleOrGone(f6));
            }
        });

        filtroDistanciaMax = viewFiltro.findViewById(R.id.editTextDisMax);
        if (f3NM==0){ filtroDistanciaMax.setText("");
        }else { filtroDistanciaMax.setText(String.format(""+f3NM)); }
        filtroDistanciaMax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                fDisMax=s.toString();
                if(fDisMax.equals("")){ f3NM = 0; }else{ f3NM = Integer.parseInt(fDisMax); }
                filtrar(s.toString(),2);
            }
        });

        filtroDistanciaMin = viewFiltro.findViewById(R.id.editTextDisMin);
        if (f3Nm==0){ filtroDistanciaMin.setText("");
        }else { filtroDistanciaMin.setText(String.format(""+f3Nm)); }
        filtroDistanciaMin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                fDisMin=s.toString();
                if(fDisMin.equals("")){ f3Nm = 0; }else{ f3Nm= Integer.parseInt(fDisMin); }
                filtrar(s.toString(),2);
            }
        });


        filtroAlturaMax = viewFiltro.findViewById(R.id.editTextAltMax);
        if (f4NM==0){ filtroAlturaMax.setText("");
        }else { filtroAlturaMax.setText(String.format(""+f4NM)); }
        filtroAlturaMax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                fAltMax=s.toString();
                if(fAltMax.equals("")){ f4NM= 0; }else{ f4NM= Integer.parseInt(fAltMax); }
                filtrar(s.toString(),3);
            }
        });

        filtroAlturaMin = viewFiltro.findViewById(R.id.editTextAltMin);
        if (f4Nm==0){ filtroAlturaMin.setText("");
        }else { filtroAlturaMin.setText(String.format(""+f4Nm)); }
        filtroAlturaMin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                fAltMin=s.toString();
                if(fAltMin.equals("")){ f4Nm= 0; }else{ f4Nm= Integer.parseInt(fAltMin); }
                filtrar(s.toString(),3);
            }
        });


        filtroPendienteMax = viewFiltro.findViewById(R.id.editTextPenMax);
        if (f5NM==0){ filtroPendienteMax.setText("");
        }else { filtroPendienteMax.setText(String.format(""+f5NM)); }
        filtroPendienteMax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                fPenMax=s.toString();
                if(fPenMax.equals("")){ f5NM= 0; }else{ f5NM= Integer.parseInt(fPenMax); }
                filtrar(s.toString(),4);
            }
        });

        filtroPendientemin = viewFiltro.findViewById(R.id.editTextPenMin);
        if (f5Nm==0){ filtroPendientemin.setText("");
        }else { filtroPendientemin.setText(String.format(""+f5Nm)); }
        filtroPendientemin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                fPenMin=s.toString();
                if(fPenMin.equals("")){ f5Nm= 0; }else{ f5Nm= Integer.parseInt(fPenMin); }
                filtrar(s.toString(),4);
            }
        });


        filtroDesnivelMax = viewFiltro.findViewById(R.id.editTextDesMax);
        if (f6NM==0){ filtroDesnivelMax.setText("");
        }else { filtroDesnivelMax.setText(String.format(""+f6NM)); }
        filtroDesnivelMax.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                fDesMax=s.toString();
                if(fDesMax.equals("")){ f6NM= 0; }else{ f6NM= Integer.parseInt(fDesMax); }
                filtrar(s.toString(),5);
            }
        });

        filtroDesnivelmin = viewFiltro.findViewById(R.id.editTextDesMin);
        if (f6Nm==0){ filtroDesnivelmin.setText("");
        }else { filtroDesnivelmin.setText(String.format(""+f6Nm)); }
        filtroDesnivelmin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                fDesMin=s.toString();
                if(fDesMin.equals("")){ f6Nm= 0; }else{ f6Nm= Integer.parseInt(fDesMin); }
                filtrar(s.toString(),5);
            }
        });

        botonAplicarFiltro = viewFiltro.findViewById(R.id.buttonfiltros);
        botonAplicarFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }


    @Override
    public void onClick(View v) {

    }
}