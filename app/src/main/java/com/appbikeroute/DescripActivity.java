package com.appbikeroute;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.maps.android.data.geojson.GeoJsonPoint;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

public class DescripActivity extends AppCompatActivity {

    ArrayList<ItemFotoRuta> listaFotoRuta;
    ArrayList<ItemGrupoListaLugares> listaLugares;
    ArrayList<ItemFotoRuta> listaFotosLugares;
    RecyclerView RecyclerFotosRuta;
    RecyclerView RecyclerLugares;
    ArrayList<ItemDatosPuertos> listaDatosPuertos;
    RecyclerView RecyclerDatosPuertos;
    TextView NombreRutaDescrip,dist,pend,altu,desni;

    private ArrayAdapter<String> adapter;
    List<LatLng> lineStringArray = new ArrayList<LatLng>();
    List<GeoJsonPoint> puntoStringArray = new ArrayList<GeoJsonPoint>();
    double[] longitud ;
    double[] latitud;
    double[] altitud;
    double[] longitud2 ;
    double[] latitud2;
    double[] altitud2;
    int p,p2;
    double[] disXY ;
    double[] disXYZ ;
    double[] distotal ;
    double[] desnivel ;
    double[] difAltura ;
    double[] pendiente ;

    String line;
    StringBuilder resulta;

    String[] Nombre = new String[1000],Tipo= new String[1000],Temperatura= new String[1000],Viento= new String[1000]
            ,Humedad= new String[1000],Poblacion= new String[1000],Gentilicio= new String[1000],Descripcion= new String[1000];
    double[] LatitudLugarT = new double[1000], LongitudLugarT = new double[1000], AlturaLugarT = new double[1000], PerimetroLugarT = new double[1000];

    final long ONE_MEGABYTE = 1024 * 1024 * 10;
    String nombreFoto;

    private String nombreRuta,distanciatext,desniveltext,alturatext,pendietetext;
    private int descargado;
    private StorageReference storageReference;
    /// datos a enviar a la grafica
    Bundle args = new Bundle();
    /// datos a enviar al mapa
    Bundle args2 = new Bundle();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /////Recibir datos de home
        nombreRuta = getIntent().getStringExtra("NOMBRE RUTA");
        distanciatext=getIntent().getStringExtra("distotal");
        desniveltext=getIntent().getStringExtra("desnivel");
        pendietetext=getIntent().getStringExtra("pendiente");
        alturatext=getIntent().getStringExtra("altura");
        descargado=getIntent().getIntExtra("descargado",1);
        setContentView(R.layout.activity_descrip);

//boton iniciar ruta
        Button iboton = findViewById(R.id.biniciarruta);
        iboton.setEnabled(false);
        iboton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,1600);
                FrameLayout lf = (FrameLayout) findViewById(R.id.fragmentLayoutDescrip);
                lf.removeAllViews();
                lf.setLayoutParams(params);
                args2.putInt("Puntos",1);
                MapFragment mapFragment = new MapFragment();
                mapFragment.setArguments(args2);
                ft.replace(R.id.fragmentLayoutDescrip,mapFragment);
                ft.commit();
            }
        });

        listaLugares = new ArrayList<>();

        try {
            extraerDatosGeojson(nombreRuta);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }


        NombreRutaDescrip = findViewById(R.id.nombreRuta);
        NombreRutaDescrip.setText(nombreRuta);

        dist = findViewById(R.id.idTextDistanciaDesc);
        dist.setText(distanciatext);
        pend = findViewById(R.id.idTextPendienteDesc);
        pend.setText(pendietetext);
        altu = findViewById(R.id.idTextAlturaDesc);
        altu.setText(alturatext);
        desni = findViewById(R.id.idTextDesnivelDesc);
        desni.setText(desniveltext);

    }

    private void iniciarMapaYGrafica() {
        AltimetriaFragment altimetriaFragment = new AltimetriaFragment();
        altimetriaFragment.setArguments(args);
        MapFragment mapFragment = new MapFragment();
        mapFragment.setArguments(args2);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.fragContainerViewGrafica, altimetriaFragment);
        ft.add(R.id.fragContainerViewMapa, mapFragment);
        ft.commit();
    }

    void iniciar(){
        goneProgressBar();
        activarGones();
        calculoDatosCoordenadasMapa();
        enviarDatosGraficaYMapa();
        llenarLugares();
        iniciarMapaYGrafica();
        Button iboton = findViewById(R.id.biniciarruta);
        iboton.setEnabled(true);
    }



    ///////////////////////////////////// leer Archivo geojson y extraer datos --->

    private interface Mycallback{
        void onCallback(String value) throws JSONException;
    }
    private interface Mycallback2{
        void onCallback(String value,int value2) throws JSONException;
    }

    private void activarGones() {
        RecyclerFotosRuta = findViewById(R.id.recyclerFotosRuta);
        RecyclerFotosRuta.setVisibility(View.VISIBLE);
        FrameLayout layoutMapa = findViewById(R.id.fragContainerViewMapa);
        layoutMapa.setVisibility(View.VISIBLE);
        FrameLayout layoutGrafica = findViewById(R.id.fragContainerViewGrafica);
        layoutGrafica.setVisibility(View.VISIBLE);
        RecyclerDatosPuertos= findViewById(R.id.recyclerDatosPuertos);
        RecyclerDatosPuertos.setVisibility(View.VISIBLE);
        RecyclerLugares = findViewById(R.id.recyclerLugaresTuristicos);
        RecyclerLugares.setVisibility(View.VISIBLE);
    }

    private void goneProgressBar() {
        ProgressBar progressBar;
        progressBar = findViewById(R.id.progressBar2);
        progressBar.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar3);
        progressBar.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar4);
        progressBar.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar5);
        progressBar.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar6);
        progressBar.setVisibility(View.GONE);
    }

    public void readData(String archivo, Mycallback myCallback)  {
        String archivo2;
        archivo2 = "r"+archivo+".geojson";
        archivo2 = limpiarCaracteresNoPermitidos(archivo2);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference ref = storageReference.child("Mapas/GeoJson");
        ref.child(archivo2).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                String line;
                StringBuilder resulta = new StringBuilder();

                //ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
                //BufferedInputStream bufferedInputStream = new BufferedInputStream(byteArrayInputStream);
                InputStream inputStream = new ByteArrayInputStream(bytes);
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader reader = new BufferedReader(inputStreamReader);
                //BufferedReader reader = new BufferedReader(new InputStreamReader(openFileInput(archivo)));

                try {
                    while ((line = reader.readLine()) != null){
                        resulta.append(line);
                    }
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    myCallback.onCallback(resulta.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.e("ERROR, ","no se pudo descargo archivo, Error: "+e );
            }
        });

    }

    void descargarDatosTemporalesGeojsonFirebase(String archivo) throws IOException, JSONException {
        readData(archivo, new Mycallback() {
            @Override
            public void onCallback(String value) throws JSONException{
                String jSonString ;
                jSonString =value;
                String  lgeometry;
                JSONObject object = new JSONObject(jSonString);
                JSONArray jsonArray = object.getJSONArray("features");
                int tamajson = jsonArray.length();
                JSONObject obj = jsonArray.getJSONObject(tamajson-1);
                lgeometry = obj.getString("geometry");
                JSONObject object2 = new JSONObject(lgeometry);
                JSONArray jsonArray2 = object2.getJSONArray("coordinates");
                longitud = new double[jsonArray2.length()];
                latitud= new double[jsonArray2.length()];
                altitud= new double[jsonArray2.length()];
                for (int i=0; i< jsonArray2.length() ;i++){
                    JSONArray jsonArray3 = jsonArray2.getJSONArray(i);
                    longitud[i] = Double.parseDouble(jsonArray3.getString(0));
                    latitud[i]  = Double.parseDouble(jsonArray3.getString(1));
                    altitud[i]  = Double.parseDouble(jsonArray3.getString(2));

                    lineStringArray.add(new LatLng(latitud[i], longitud[i]));

                    if (i==0){
                        puntoStringArray.add(new GeoJsonPoint(new LatLng(latitud[i], longitud[i])));

                    }

                }

                puntoStringArray.add(new GeoJsonPoint(new LatLng(latitud[jsonArray2.length()-1], longitud[jsonArray2.length()-1])));
                p = jsonArray2.length();
                Button iboton = findViewById(R.id.biniciarruta);
                iboton.setText("INICIAR RUTA");
                iboton.setTextColor(Color.parseColor("#FF9800"));
                iniciar();
            }
        });

    }

    void extraerDatosGeojsonArchivoDescargado(String archivo) throws IOException, JSONException  {
        String line;
        StringBuilder resulta = new StringBuilder();
        String archivo2;
        archivo2 = "r"+archivo+".geojson";
        archivo2 = limpiarCaracteresNoPermitidos(archivo2);
        File fileArchivo = new File(getApplicationContext().getExternalFilesDir(null).toString() +"/Rutas/"+archivo+"/"+archivo2);
        FileInputStream fileInputStream = new FileInputStream(fileArchivo);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader reader = new BufferedReader(inputStreamReader);
        while ((line = reader.readLine()) != null){
            resulta.append(line);
        }
        reader.close();
        String  jSonString;
        jSonString = resulta.toString();

        String  lgeometry;

        JSONObject object = new JSONObject(jSonString);
        JSONArray jsonArray = object.getJSONArray("features");
        int tamajson = jsonArray.length();
        JSONObject obj = jsonArray.getJSONObject(tamajson-1);
        lgeometry = obj.getString("geometry");
        JSONObject object2 = new JSONObject(lgeometry);
        JSONArray jsonArray2 = object2.getJSONArray("coordinates");
        longitud = new double[jsonArray2.length()];
        latitud= new double[jsonArray2.length()];
        altitud= new double[jsonArray2.length()];
        for (int i=0; i< jsonArray2.length() ;i++){
            JSONArray jsonArray3 = jsonArray2.getJSONArray(i);
            longitud[i] = Double.parseDouble(jsonArray3.getString(0));
            latitud[i]  = Double.parseDouble(jsonArray3.getString(1));
            altitud[i]  = Double.parseDouble(jsonArray3.getString(2));

            lineStringArray.add(new LatLng(latitud[i], longitud[i]));

            if (i==0){
                puntoStringArray.add(new GeoJsonPoint(new LatLng(latitud[i], longitud[i])));

            }

        }

        puntoStringArray.add(new GeoJsonPoint(new LatLng(latitud[jsonArray2.length()-1], longitud[jsonArray2.length()-1])));
        p = jsonArray2.length();

        iniciar();
    }

    private void enviarDatosGraficaYMapa() {
        /// datos a enviar a la grafica
        args.putInt("p2",p2);
        args.putDoubleArray("distotal",distotal);
        args.putDoubleArray("desnivel",desnivel);
        args.putDoubleArray("pendiente",pendiente);
        /// datos a enviar al mapa
        args2.putParcelableArrayList("lineStringArray", (ArrayList<? extends Parcelable>) lineStringArray);
        args2.putInt("Puntos",0);
    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void extraerDatosGeojson(String archivo) throws IOException, JSONException {
        if (descargado==0){
            extraerDatosGeojsonArchivoDescargado(archivo);
            llenarFotosRutaDescargadas(archivo);
        } else {
            Button iboton = findViewById(R.id.biniciarruta);
            iboton.setText("Descargando...");
            iboton.setTextColor(Color.GRAY);
            descargarDatosTemporalesGeojsonFirebase(archivo);
            //llenarFotosRutaTemp();
            llamarFotos(archivo);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void llenarFotosRutaDescargadas(String Nombre) {
        listaFotoRuta = new ArrayList<>();
        if (carpetaFotosRutaExiste(Nombre)){
            File archivo = new File(getApplicationContext().getExternalFilesDir(null).toString() +"/Rutas/"+Nombre+"/Fotos");
            for (File file: archivo.listFiles()) {
                File file2 = new File(getApplicationContext().getExternalFilesDir(null).toString() +"/Rutas/"+Nombre+"/Fotos/"+file.getName());
                try {
                    byte[] fileByte = Files.readAllBytes(file2.toPath());
                    listaFotoRuta.add(new ItemFotoRuta(fileByte));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            RecyclerFotosRuta = (RecyclerView) findViewById(R.id.recyclerFotosRuta);
            RecyclerFotosRuta.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
            AdapterDatosFotosRuta adapterDatosFotosRuta = new AdapterDatosFotosRuta(getApplicationContext(),listaFotoRuta);
            RecyclerFotosRuta.setAdapter(adapterDatosFotosRuta);
        }
    }

    private void calculoDatosCoordenadasMapa() {
        disXY = new double[p];
        disXYZ = new double[p];
        distotal =new double[p];
        desnivel =new double[p];
        difAltura =new double[p];
        pendiente =new double[p];
        desnivel[0]= 0;
        distotal[0]=0;
        optimizarPuntosMapas();
        for (int i=1; i<p2;i++ ){
            Location locationA = new Location("punto A");
            Location locationB = new Location("punto B");

            locationA.setLongitude(longitud2[i-1]);
            locationA.setLatitude(latitud2[i-1]);
            locationB.setLongitude(longitud2[i]);
            locationB.setLatitude(latitud2[i]);
            disXY[i] = locationA.distanceTo(locationB);
            difAltura[i]= altitud2[i] - altitud2[i-1];
            disXYZ[i]= Math.sqrt(Math.pow(disXY[i] ,2)+Math.pow(difAltura[i] ,2));
            distotal[i]=distotal[i-1]+(disXYZ[i]/1000);
            desnivel[i]= difAltura[i]+desnivel[i-1];
            if (disXY[i] != 0){
                pendiente[i]= (difAltura[i]/disXY[i])*100;
            }else{
                pendiente[i]=0;
            }


        }
    }

    public void optimizarPuntosMapas(){

        listaDatosPuertos = new ArrayList<>();
        double alturaPuerto = 0,pendientePuerto= 0,desnivelPuerto= 0;
        int temp= 0;

        longitud2 = new double[longitud.length];
        latitud2= new double[longitud.length];
        altitud2= new double[longitud.length];
        int k=0;
        distotal[0]=0;
        longitud2[0]=longitud[0];
        latitud2[0]=latitud[0];
        altitud2[0]=altitud[0];

        for (int i=1; i<p;i++ ){
            Location locationA = new Location("punto A");
            Location locationB = new Location("punto B");
            locationA.setLongitude(longitud2[k]);
            locationA.setLatitude(latitud2[k]);
            locationB.setLongitude(longitud[i]);
            locationB.setLatitude(latitud[i]);
            disXY[k] = locationA.distanceTo(locationB);
            difAltura[k]= altitud[i] - altitud2[k];
            temp++;
            alturaPuerto = alturaPuerto+difAltura[k];
            disXYZ[k]= Math.sqrt(Math.pow(disXY[k] ,2)+Math.pow(difAltura[k] ,2));
            if (disXYZ[k] >= 1000 ){

                alturaPuerto = alturaPuerto/temp;
                pendientePuerto=(difAltura[k]/disXYZ[k])*100;
                desnivelPuerto=difAltura[k];

                k++;
                longitud2[k]=longitud[i];
                latitud2[k]=latitud[i];
                altitud2[k]=altitud[i];
                p2=k;
                listaDatosPuertos.add(new ItemDatosPuertos(String.format("%d",p2),String.format("%.2f",alturaPuerto)
                        ,String.format("%.2f %%",pendientePuerto),String.format("%.2f",desnivelPuerto)));
                alturaPuerto = 0;
                temp= 0;

            }
        }
        RecyclerDatosPuertos = (RecyclerView) findViewById(R.id.recyclerDatosPuertos);
        RecyclerDatosPuertos.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));
        AdapterDatosPuertos adapterDatosPuertos = new AdapterDatosPuertos(getApplicationContext(),listaDatosPuertos);
        RecyclerDatosPuertos.setAdapter(adapterDatosPuertos);

    }

    ////////////////////////////////////////////////////////////<---


    private void llenarFotosRutaTemp() {
        listaFotoRuta = new ArrayList<>();
        //Inicializar objeto storage
        storageReference = FirebaseStorage.getInstance().getReference();
        //se trae la referencia de la carpeta
        String temp;
        temp =nombreRuta;
        StorageReference ref = storageReference.child("Mapas/Fotos").child(temp);
        //se trae la referencia de los nombres de las rutas
        ref.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()){
                    nombreFoto= item.getName();
                    nombreFoto = nombreFoto.substring(0, nombreFoto.length()-4);
                }
                RecyclerFotosRuta = (RecyclerView) findViewById(R.id.recyclerFotosRuta);
                RecyclerFotosRuta.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
                AdapterDatosFotosRuta adapterDatosFotosRuta = new AdapterDatosFotosRuta(getApplicationContext(),listaFotoRuta);
                RecyclerFotosRuta.setAdapter(adapterDatosFotosRuta);
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(getApplicationContext(),
                    "Error en la descarga de archivos, Revise su conexión a internet",
                    Toast.LENGTH_SHORT).show();
        });

    }

    public void llamarFotos(String names){
        listaFotoRuta = new ArrayList<>();
        StorageReference ref2 = FirebaseStorage.getInstance().getReference().child("Mapas/Fotos");
        String finalNames = names;
        ref2.child(names).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for (StorageReference item : listResult.getItems()){
                    StorageReference ref3 = FirebaseStorage.getInstance().getReference().child("Mapas/Fotos").child(finalNames);
                    ref3.child(item.getName()).getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                        @Override
                        public void onSuccess(byte[] bytes) {
                            listaFotoRuta.add(new ItemFotoRuta(bytes));
                            RecyclerFotosRuta = (RecyclerView) findViewById(R.id.recyclerFotosRuta);
                            RecyclerFotosRuta.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false));
                            AdapterDatosFotosRuta adapterDatosFotosRuta = new AdapterDatosFotosRuta(getApplicationContext(),listaFotoRuta);
                            RecyclerFotosRuta.setAdapter(adapterDatosFotosRuta);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(getApplicationContext(),
                                    "Error en la descarga de archivos, Revise su conexión a internet", Toast.LENGTH_SHORT)
                                    .show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getApplicationContext(),
                        "Error en la descarga de archivos, Revise su conexión a internet", Toast.LENGTH_SHORT)
                        .show();
            }
        });


    }

    private void llenarLugares() {
        DatabaseReference mDataBase = FirebaseDatabase.getInstance().getReference();
        mDataBase.child("Lugares").addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    int i=0;
                    for (DataSnapshot ds: snapshot.getChildren()) {
                        Nombre[i] = ds.child("Nombre").getValue().toString();
                        Tipo[i] = ds.child("Tipo").getValue().toString();
                        PerimetroLugarT[i] = Double.parseDouble(ds.child("Perimetro").getValue().toString());
                        AlturaLugarT[i] = Double.parseDouble(ds.child("Altura").getValue().toString());
                        Temperatura[i] = ds.child("Temperatura").getValue().toString();
                        Viento[i] = ds.child("Viento").getValue().toString();
                        Humedad[i] = ds.child("Humedad").getValue().toString();
                        Poblacion[i] = ds.child("Poblacion").getValue().toString();
                        Gentilicio[i] = ds.child("Gentilicio").getValue().toString();
                        LatitudLugarT[i] = Double.parseDouble(ds.child("Latitud").getValue().toString());
                        LongitudLugarT[i] = Double.parseDouble(ds.child("Longitud").getValue().toString());
                        Descripcion[i] = ds.child("Descripcion ").getValue().toString();
                        try {
                            if (compararConCoordenadasRuta(LongitudLugarT[i], LatitudLugarT[i], PerimetroLugarT[i])) {
                                if (!carpetaFotosLugarTExiste(Nombre[i])) {
                                    StorageReference storageReference2 = FirebaseStorage.getInstance().getReference();
                                    StorageReference ref2 = storageReference2.child("Sitios Turisticos");
                                    final int i3 = i;
                                    int[] finalCont = new int[100];
                                    finalCont[i3]=0;
                                    ref2.child(ds.child("Nombre").getValue().toString()).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                        @Override
                                        public void onSuccess(ListResult listResult) {
                                            //int cont1=0;
                                            String[][] fotostemp= new String[1000][listResult.getItems().size()];
                                            for (StorageReference item : listResult.getItems()){
                                                //cont1++;
                                                File file = new File(getApplicationContext().getExternalFilesDir(null).toString()+"/Sitios Turisticos/"+ds.child("Nombre").getValue().toString());
                                                if(!file.exists()) {
                                                    file.mkdirs();
                                                }
                                                File file2 = new File(file,item.getName());
                                                StorageReference storageReference3 = FirebaseStorage.getInstance().getReference();
                                                StorageReference ref3 = storageReference3.child("Sitios Turisticos").child(ds.child("Nombre").getValue().toString()).child(item.getName());
                                                readData2(ref3,file2,i3, new Mycallback2() {
                                                    @RequiresApi(api = Build.VERSION_CODES.O)
                                                    @Override
                                                    public void onCallback(String value, int value2) throws JSONException {
                                                        fotostemp[value2][finalCont[value2]]=value;
                                                        if (finalCont[value2] ==listResult.getItems().size()-1){
                                                            listaFotosLugares = new ArrayList<>();
                                                            for (String foto: fotostemp[value2]) {
                                                                File file1 = new File(foto);
                                                                byte[] fileContent = new byte[0];
                                                                try {
                                                                    fileContent = Files.readAllBytes(file1.toPath());
                                                                } catch (IOException e) {
                                                                    e.printStackTrace();
                                                                }
                                                                listaFotosLugares.add(new ItemFotoRuta(fileContent));
                                                            }
                                                            agregarLugarRecycler(i3,listaFotosLugares);
                                                        }
                                                        finalCont[value2]++;
                                                    }
                                                });
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull @NotNull Exception e) {
                                            Log.e("ERROR", "No ha sido posible ver en el archivo" + e.toString());
                                        }
                                    });

                                    ///////////////

                                } else {
                                    llenarFotosLugares(Nombre[i],i);
                                }
                                i++;
                            }
                        } catch (JSONException | IOException e) {
                            e.printStackTrace();
                        }

                    }
                }else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
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

    public void readData2(StorageReference ref3,File file2,int i, Mycallback2 myCallback)  {
        ref3.getFile(file2).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                try {
                    myCallback.onCallback(file2.toString(),i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Log.e("ERROR", "No ha sido posible descargar la imagen" + e.toString());
            }
        });

    }

    void agregarLugarRecycler(int i,ArrayList<ItemFotoRuta> listaFotosLugares2){
            String tintuloTexto = Nombre[i];
            String texto = "Tipo: "+ Tipo[i]+ "\n"+
                    "Altura: "+ AlturaLugarT[i]+ "      ";
            if (!Temperatura[i].equals("NA")){
                texto = texto +
                        "Temperatura: "+ Temperatura[i]+ "\n";
            }
            if (!Viento[i].equals("NA")){
                texto = texto +
                        "Viento: "+Viento[i]+ "      ";
            }
            if (!Humedad[i].equals("NA")){
                texto = texto +
                        "Humedad: "+ Humedad[i]+ "\n";
            }
            if (!Poblacion[i].equals("NA")){
                texto = texto +
                        "Poblacion: "+ Poblacion[i]+ "      ";
            }
            if (!Gentilicio[i].equals("NA")){
                texto = texto +
                        "Gentilicio: "+ Gentilicio[i]+ "\n";
            }

            texto = texto +"\n"+"Descripcion : "+ Descripcion[i];

            listaLugares.add(new ItemGrupoListaLugares(tintuloTexto,texto,listaFotosLugares2));

        RecyclerLugares = (RecyclerView) findViewById(R.id.recyclerLugaresTuristicos);
        RecyclerLugares.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        AdapterGrupoLugares adapterGrupoLugares = new AdapterGrupoLugares(this,listaLugares);
        RecyclerLugares.setAdapter(adapterGrupoLugares);

    }

    public boolean compararConCoordenadasRuta(double longitude, double Latitude, double perimetroe) throws JSONException, IOException {
        double distancia;
        Location locationA = new Location("punto A");
        Location locationB = new Location("punto B");
       for (int i=0; i< longitud.length ;i++){
            locationA.setLongitude(longitude);
            locationA.setLatitude(Latitude);
            locationB.setLongitude(longitud[i]);
            locationB.setLatitude(latitud[i]);
            distancia = locationA.distanceTo(locationB)/1000;
            if (distancia <= perimetroe){
                return true;
            }
        }
        return false;
        //return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void llenarFotosLugares(String nombre, int i) {
        listaFotosLugares = new ArrayList<>();
        File file = new File(getApplicationContext().getExternalFilesDir(null).toString()+"/Sitios Turisticos/"+nombre);
        if (file.exists()) {
            File[] files = file.listFiles();
            for (File file1 : files) {
                File file3 = new File(getApplicationContext().getExternalFilesDir(null).
                        toString() +"/Sitios Turisticos/"+nombre+"/"+file1.getName());
                byte[] fileContent = new byte[0];
                try {
                    fileContent = Files.readAllBytes(file3.toPath());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                listaFotosLugares.add(new ItemFotoRuta(fileContent));
            }
            agregarLugarRecycler(i,listaFotosLugares);
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

    private boolean carpetaFotosLugarTExiste(String i2) {
        File archivo = new File(getApplicationContext().getExternalFilesDir(null).toString() +"/Sitios Turisticos/"+i2);
        return archivo.exists();
    }

    private boolean carpetaFotosRutaExiste(String i2) {
        File archivo = new File(getApplicationContext().getExternalFilesDir(null).toString() +"/Rutas/"+i2);
        return archivo.exists();
    }


}