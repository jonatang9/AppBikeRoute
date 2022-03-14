package com.appbikeroute;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.data.geojson.GeoJsonPoint;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class MapFragment extends Fragment {


    int selec = 0;
    List<LatLng> lineStringArray = new ArrayList<LatLng>();
    List<GeoJsonPoint> puntoStringArray = new ArrayList<GeoJsonPoint>();
    LatLngBounds.Builder builder2 = new LatLngBounds.Builder();
    GoogleMap map;
    MarkerOptions markerOptions = new MarkerOptions();
    List<Marker> AllMarkers = new ArrayList<Marker>();
    Button bSeleccionarInicio;
    Boolean actualPosition = true;
    JSONObject jso;
    double longitudOrigen, latitudOrigen,longitudDestino,latitudDestino;


    public MapFragment() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        MapFragment mapFragment = new MapFragment();
        Bundle args2 = new Bundle();
        args2.putParcelableArrayList("lineStringArray", (ArrayList<? extends Parcelable>) lineStringArray);
        args2.putInt("Puntos", selec);
        mapFragment.setArguments(args2);
        //Toast.makeText(getActivity().getApplicationContext(), String.format("Entro 1 line : "), Toast.LENGTH_SHORT).show();
        if (getArguments() != null) {
            lineStringArray = getArguments().<LatLng>getParcelableArrayList("lineStringArray");
            selec = getArguments().getInt("Puntos");
            //Toast.makeText(getActivity().getApplicationContext(), String.format("Entro 2 line : "+lineStringArray.get(10).toString()), Toast.LENGTH_SHORT).show();

            markerOptions.position(lineStringArray.get(0)).title(lineStringArray.get(0).toString().replace("lat/lng:", ""));
            markerOptions.position(lineStringArray.get(lineStringArray.size() - 1));

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        //iniciar map fragment
        SupportMapFragment supportMapFragment = (SupportMapFragment)
                this.getChildFragmentManager().findFragmentById(R.id.idmap);
        //View root = view.getRoot();
        bSeleccionarInicio = view.findViewById(R.id.bSeleccionarInicio);
        //bSeleccionarInicio.setVisibility(View.GONE);
        bSeleccionarInicio.setEnabled(false);


        //Async map
        assert supportMapFragment != null;
        supportMapFragment.getMapAsync(new OnMapReadyCallback() {
            @SuppressLint("PotentialBehaviorOverride")
            @Override
            public void onMapReady(@NotNull GoogleMap googleMap) {
                map = googleMap;
                googleMap.addMarker(markerOptions);

                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION)){

                    }else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},1);
                    }

                    if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.ACCESS_FINE_LOCATION)){

                    }else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
                    }

                    return;
                }
                map.setMyLocationEnabled(true);

                map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                    @Override
                    public void onMyLocationChange(@NonNull @NotNull Location location) {
                        if (actualPosition){
                            latitudOrigen=location.getLatitude();
                            longitudOrigen = location.getLongitude();
                            actualPosition=false;
                            LatLng miposicion = new LatLng(latitudOrigen,longitudOrigen);
                            map.addMarker(new MarkerOptions().position(miposicion));


                        }
                    }
                });


                int cont = 0;
                if (selec == 1) {
                    bSeleccionarInicio.setVisibility(View.VISIBLE);
                }

                for (LatLng latLng1 : lineStringArray) {
                    builder2.include(latLng1);
                    cont++;
                    if (cont == 500 && selec == 1) {

                        //set position of marker
                        markerOptions.position(latLng1);

                        //set title of marker
                        //markerOptions.title(latLng1.latitude + " : "+ latLng1.longitude);
                        //remove all marker
                        //googleMap.clear();
                        //animatting to zoom the marker
                        //googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10));
                        //add marker on map
                        //googleMap.addMarker(markerOptions);
                        Marker mLocationMarker = googleMap.addMarker(markerOptions);
                        AllMarkers.add(mLocationMarker); // add the marker to array

                        //puntoStringArray.add(new GeoJsonPoint(latLng));
                        cont = 0;
                    }
                }


                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(@NonNull @NotNull Marker marker) {
                        //bSeleccionarInicio.findViewById(R.id.bSeleccionarInicio);
                        bSeleccionarInicio.setEnabled(true);
                        bSeleccionarInicio.setBackgroundResource(R.drawable.rounded_corner_blanco);
                        longitudDestino = marker.getPosition().longitude;
                        latitudDestino = marker.getPosition().latitude;
                        return false;
                    }
                });

                insertarEnMapa(lineStringArray,puntoStringArray,googleMap);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(builder2.build().getCenter(), 10));


            }




        });

        bSeleccionarInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bSeleccionarInicio.setVisibility(View.GONE);
                removeAllMarkers();
                LatLng miposicion = new LatLng(latitudOrigen,longitudOrigen);
                map.addMarker(new MarkerOptions().position(miposicion));
                LatLng posicionDestino = new LatLng(latitudDestino,longitudDestino);
                map.addMarker(new MarkerOptions().position(posicionDestino));

                //////traza ruta de ubicacion a puntomarcado

                String url = "https://maps.googleapis.com/maps/api/directions/json?" +
                        "origin=" + latitudOrigen + "," + longitudOrigen +
                        "&destination=" + latitudDestino + "," + longitudDestino +
                        "&key=AIzaSyADuw3u4a4w1g-0xOjhJTU6FNLNr9RdPak";

                RequestQueue queue = Volley.newRequestQueue(getActivity());
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            jso = new JSONObject(response);
                            trazarRuta(jso);
                            //Log.i("jsonRuta", "Ruta:  "+response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

                queue.add(stringRequest);




            }
        });



        return view;
    }

    private void trazarRuta(JSONObject jso) {

        JSONArray jRoutes;
        JSONArray jLegs;
        JSONArray jSteps;

        try {
            jRoutes = jso.getJSONArray("routes");
            for (int i=0; i<jRoutes.length();i++){
                jLegs = ((JSONObject)(jRoutes.get(i))).getJSONArray("legs");
                for (int j=0; j<jLegs.length();j++){
                    jSteps = ((JSONObject)(jLegs.get(j))).getJSONArray("steps");
                    for (int k=0; k<jSteps.length();k++){

                        String polyline = ""+((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
                        List<LatLng> list = PolyUtil.decode(polyline);
                        map.addPolyline(new PolylineOptions().addAll(list).color(Color.BLUE).width(15));
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        switch (requestCode) {
            case 1:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                }  else {
                }
                return;
        }
    }

    private void insertarEnMapa(List<LatLng> lineStringArray,List<GeoJsonPoint> list2,GoogleMap googleMap) {

        Polyline polyline = googleMap.addPolyline(new PolylineOptions().clickable(true).addAll(lineStringArray));
        polyline.setStartCap(new RoundCap());
        polyline.setColor(Color.RED);
        polyline.setWidth(15);

    }
    private void removeAllMarkers() {
        for (Marker mLocationMarker: AllMarkers) {
            mLocationMarker.remove();
        }
        AllMarkers.clear();

    }

}