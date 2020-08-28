package com.example.connectlife.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.connectlife.R;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;

public class MembersNearby extends Fragment {

    MapView mapView;

    String mapStyle;
    private MapboxMap mapboxMap;
    public static final String ICON_ID = "location";
    public static List<Point> points;


    public MembersNearby() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_members_nearby, container, false);
        // Inflate the layout for this fragment
        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                MembersNearby.this.mapboxMap = mapboxMap;
                mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public boolean onMapLongClick(@NonNull final LatLng point) {
                        Toast.makeText(getContext(), point.toString(), Toast.LENGTH_SHORT).show();


                        return false;
                    }
                });

                mapboxMap.setStyle(Style.DARK, new Style.OnStyleLoaded()
                {
                    @Override
                    public void onStyleLoaded(@NonNull final Style style) {
                        mapStyle = Style.DARK;
                        //  MainActivity.RequestList.clear();
//                        Toast.makeText(getContext(),"LayerId to begin from is: "+countValueRoute, Toast.LENGTH_LONG).show();

                        final Handler handler = new Handler();

                        final Runnable r = new Runnable() {
                            public void run() {
                                handler.postDelayed(this, 1000);
                            }
                        };

                        handler.postDelayed(r, 1000);





                    }
                });



            }
        });


        return root;
    }
}