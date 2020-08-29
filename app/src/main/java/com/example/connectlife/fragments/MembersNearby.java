package com.example.connectlife.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.connectlife.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager;
import com.mapbox.mapboxsdk.plugins.annotation.SymbolOptions;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.utils.BitmapUtils;

import org.w3c.dom.Document;

import java.util.List;

public class MembersNearby extends Fragment {

    MapView mapView;

    String mapStyle;
    private MapboxMap mapboxMap;
    public static final String ICON_ID = "location";
    public static List<Point> points;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    String colorRed  = "#E84E4E";


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
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        // Inflate the layout for this fragment
        mapView = root.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {
                MembersNearby.this.mapboxMap = mapboxMap;
                /*mapboxMap.addOnMapLongClickListener(new MapboxMap.OnMapLongClickListener() {
                    @Override
                    public boolean onMapLongClick(@NonNull final LatLng point) {
                        Toast.makeText(getContext(), point.toString(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });*/






                mapboxMap.setStyle(Style.DARK, new Style.OnStyleLoaded()
                {
                    @Override
                    public void onStyleLoaded(@NonNull final Style style) {
                        mapStyle = Style.DARK;

                        CollectionReference collectionReference = firebaseFirestore.collection("users");
                        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()){
                                    for (QueryDocumentSnapshot document : task.getResult()) {

                                        LatLng userCoord = fetchUserLocation(document.getData().get("LatLng").toString());

                                        if(userCoord.getLongitude()!=0 && userCoord.getLatitude()!=0){
                                            displayUsersLocation(style,userCoord.getLatitude(),userCoord.getLongitude(),colorRed);

                                        }

                                       // Log.d("TAG", document.getId() + " => " + document.getData().);
                                    }
                                } else {
                                    Log.d("TAG", "Error getting documents: ", task.getException());
                                }
                                }

                        });






                        //  MainActivity.RequestList.clear();
//                        Toast.makeText(getContext(),"LayerId to begin from is: "+countValueRoute, Toast.LENGTH_LONG).show();

                       /* final Handler handler = new Handler();

                        final Runnable r = new Runnable() {
                            public void run() {
                                handler.postDelayed(this, 1000);
                            }
                        };

                        handler.postDelayed(r, 1000);*/






                    }
                });



            }
        });







        return root;
    }


    public LatLng fetchUserLocation(String coodinateStr){
        String[] values = coodinateStr.split(",");
        double latitude = Double.valueOf(values[0]);
        double longitute = Double.valueOf(values[1]);
        LatLng coodinates = new LatLng(latitude,longitute);
        return coodinates;
    }

    public void displayUsersLocation(Style style,double Latitude,double Longitude,String color){

        style.addImage(ICON_ID,
                BitmapUtils.getBitmapFromDrawable( getResources().getDrawable(R.drawable.ic_baseline_location_on_24)),
                true);

        SymbolManager symbolManager = new SymbolManager(mapView, mapboxMap, style);
        symbolManager.setIconAllowOverlap(true);
        symbolManager.setIconIgnorePlacement(true);

        symbolManager.create(new SymbolOptions()
                        .withLatLng(new LatLng(Latitude, Longitude))
                        .withIconImage(ICON_ID)
                        .withIconColor(PropertyFactory.iconColor(color).getValue())
                        .withIconOpacity(1f)
                //.withIconSize(2.0f)
        );
    }

}