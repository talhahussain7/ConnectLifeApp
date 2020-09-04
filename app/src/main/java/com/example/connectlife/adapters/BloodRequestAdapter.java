package com.example.connectlife.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectlife.R;
import com.example.connectlife.models.BloodRequest;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import java.util.List;

public class BloodRequestAdapter extends RecyclerView.Adapter<BloodRequestAdapter.MyViewHolder> {
    Context context;
    List<BloodRequest> bloodRequestList;

    public BloodRequestAdapter(Context context, List<BloodRequest> bloodRequestList) {
        this.context = context;
        this.bloodRequestList = bloodRequestList;
    }

    @NonNull
    @Override
    public BloodRequestAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BloodRequestAdapter.MyViewHolder holder, int position) {
        final BloodRequest item =  bloodRequestList.get(position);
        holder.nameView.setText(item.getName());
        holder.locationView.setText(item.getLocation());
        holder.bloodGroupView.setText(item.getBloodGroup());
        holder.mapView.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull final MapboxMap mapboxMap) {

                mapboxMap.setStyle(Style.DARK, new Style.OnStyleLoaded()
                {
                    @Override
                    public void onStyleLoaded(@NonNull final Style style) {

                        CameraPosition cameraPosition = new CameraPosition.Builder()
                                .target(item.getCoordinates())
                                .zoom(12)
                                .tilt(20)
                                .build();
                        mapboxMap.addMarker(new MarkerOptions()
                                .position(new LatLng(item.getCoordinates().getLatitude(), item.getCoordinates().getLongitude())));

                        mapboxMap.setCameraPosition(cameraPosition);



                    }
                });



            }
        });






    }

    @Override
    public int getItemCount() {
        return bloodRequestList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView nameView, bloodGroupView, locationView;
        MapView mapView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.name);
            bloodGroupView = itemView.findViewById(R.id.blood_group);
            locationView = itemView.findViewById(R.id.location);
            mapView = itemView.findViewById(R.id.mapView);
        }
    }
}
