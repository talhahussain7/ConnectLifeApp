package com.example.connectlife.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectlife.R;
import com.example.connectlife.models.BloodRequest;
import com.example.connectlife.models.User;
import com.example.connectlife.notificationPack.APIService;
import com.example.connectlife.notificationPack.Client;
import com.example.connectlife.notificationPack.Data;
import com.example.connectlife.notificationPack.MyResponse;
import com.example.connectlife.notificationPack.NotificationSender;
import com.example.connectlife.notificationPack.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

//import com.google.firebase.messaging.FirebaseMessagingException;
//import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.RemoteMessage;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.tapadoo.alerter.Alerter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BloodRequestAdapter extends RecyclerView.Adapter<BloodRequestAdapter.MyViewHolder> {
    APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    Context context;
    List<BloodRequest> bloodRequestList;
    Activity activity;

    public BloodRequestAdapter(Context context, List<BloodRequest> bloodRequestList,Activity activity) {
        this.context = context;
        this.bloodRequestList = bloodRequestList;
        this.activity = activity;
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
        holder.acceptReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = item.getSenderId();
                DocumentReference targetUserDoc = FirebaseFirestore.getInstance().collection("users").document(userId);
                targetUserDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                               String token = document.getData().get("token").toString();

                                DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
                                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if(task.isSuccessful()){
                                            String name = task.getResult().getData().get("name").toString();
                                            Toast.makeText(context, token, Toast.LENGTH_SHORT).show();
                                            String title= "Congratulations!";
                                            String message = "Your Blood Request was accepted by " + name;
                                            sendNotifications(token,title,message);
                                            removeRequest(item,position);
                                            successfulMsg();
                                        }
                                    }
                                });

                            } else {
                                Toast.makeText(context, "No Document Found!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Failed ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });


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
        Button acceptReqBtn;
        MapView mapView;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            nameView = itemView.findViewById(R.id.name);
            bloodGroupView = itemView.findViewById(R.id.blood_group);
            locationView = itemView.findViewById(R.id.location);
            mapView = itemView.findViewById(R.id.mapView);
            acceptReqBtn = itemView.findViewById(R.id.accept_req_btn);
        }
    }


   public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);

        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    Toast.makeText(context, "Sent!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void successfulMsg(){
        Alerter.create(activity)
                .setBackgroundColorRes(R.color.colorGreen)
                .setTitle("Successful!")
                .setText("You have indeed done a great job!")
                .show();

    }
    public void removeRequest(BloodRequest item,int position){
        DocumentReference requestDoc = FirebaseFirestore.getInstance().collection("bloodRequests").document(item.getId());
        requestDoc.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful()){
                   bloodRequestList.remove(position);
                   notifyDataSetChanged();
               }
            }
        });
    }
}
