package com.example.connectlife.fragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.connectlife.MainActivity;
import com.example.connectlife.R;
import com.example.connectlife.adapters.BloodRequestAdapter;
import com.example.connectlife.fragments.WelcomeFragments.AddRequestFragment;
import com.example.connectlife.models.BloodRequest;
import com.example.connectlife.models.User;
import com.example.connectlife.notificationPack.APIService;
import com.example.connectlife.notificationPack.Client;
import com.example.connectlife.notificationPack.Data;
import com.example.connectlife.notificationPack.MyResponse;
import com.example.connectlife.notificationPack.NotificationSender;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.tapadoo.alerter.Alerter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RequestsFragment extends Fragment {

FirebaseFirestore firebaseFirestore;
FirebaseAuth firebaseAuth;
User user;
List<BloodRequest> requestList;
BloodRequestAdapter bloodRequestAdapter;
RecyclerView recyclerView;
    APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);

    public RequestsFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        setHasOptionsMenu(true);
        View view =  inflater.inflate(R.layout.fragment_requests, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        requestList = new ArrayList<>();
        recyclerView = view.findViewById(R.id.recycler_view);

        final DocumentReference docRef =FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        getUserDetails();

        bloodRequestAdapter = new BloodRequestAdapter(getContext(),requestList,getActivity());
        recyclerView.setAdapter(bloodRequestAdapter);
        populateRequests();

        return view;
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.blood_request_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()){
            case  R.id.add_request:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
// ...Irrelevant code for customizing the buttons and title
                final View popupView = getLayoutInflater().inflate(R.layout.add_blood_req_popup, null);
                dialogBuilder.setView(popupView);
                final AlertDialog alertDialog = dialogBuilder.create();
                LayoutInflater inflater = this.getLayoutInflater();
                alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final CheckBox onBehalfCheck = popupView.findViewById(R.id.someone_else_check);
                final View onBehalfLayout = popupView.findViewById(R.id.someone_else_layout);
                ImageButton closeBtn = popupView.findViewById(R.id.close_btn);
                Button submitBtn = popupView.findViewById(R.id.submit_request);
                final EditText nameField = popupView.findViewById(R.id.name_field);
                final EditText phoneField = popupView.findViewById(R.id.phoneNumber_field);

                final String[] arraySpinner = new String[] {
                        "A+","A-","B+","B-","AB+","AB-","O+","O-","OH+","Other"
                };
               final Spinner s = popupView.findViewById(R.id.blood_spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, arraySpinner);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s.setAdapter(adapter);

                onBehalfCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            onBehalfLayout.setVisibility(View.VISIBLE);
                            nameField.setText("");
                            phoneField.setText("");

                        }else {
                            onBehalfLayout.setVisibility(View.GONE);
                        }
                    }
                });

                alertDialog.show();


                closeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                submitBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                            String nameStr = "";
                            String phoneNumberStr = "";
                            try{
                                nameStr = user.getName();
                                phoneNumberStr = user.getPhoneNumber();
                            }catch (Exception e){

                            }


                            if(onBehalfCheck.isChecked()){


                                if(isValid(nameField)&& isValid(phoneField)){
                                    nameStr = nameField.getText().toString();
                                    phoneNumberStr = phoneField.getText().toString();



                                }else{
                                    if(!isValid(nameField)){
                                        nameField.setError("Name Field is Mandatory!");
                                    }
                                    if (!isValid(phoneField)){
                                        phoneField.setError("Phone Number is required!");
                                    }
                                    return;

                                }
                            }


                            Map<String,String> dataPacket = new HashMap<>();
                            dataPacket.put("nameOfReqPerson",nameStr);
                            dataPacket.put("phoneNumber",phoneNumberStr);
                            dataPacket.put("bloodGroup",s.getSelectedItem().toString());
                            dataPacket.put("senderId",user.getId());
                            dataPacket.put("Location",user.getCity()+", "+user.getCountry());
                            dataPacket.put("LatLng",user.getCoordinates().getLatitude()+","+user.getCoordinates().getLongitude());

                            Toast.makeText(getContext(), dataPacket.toString(), Toast.LENGTH_SHORT).show();
                            final DocumentReference documentReference = firebaseFirestore.collection("bloodRequests").document();
                            documentReference.set(dataPacket).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {


                               /* FirebaseMessaging.getInstance().subscribeToTopic(documentReference.getId())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()) {
                                                    Toast.makeText(getContext(), "Could not Subscribe to the request!", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });*/
                                    firebaseFirestore.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            boolean exists = false;
                                            if(task.isSuccessful()){
                                                for(QueryDocumentSnapshot document :task.getResult()){
                                                    if(!document.getId().equals(firebaseAuth.getCurrentUser().getUid())){
                                                        //  Toast.makeText(getContext(), document.getId(), Toast.LENGTH_SHORT).show();
                                                        String token = document.getData().get("token").toString();
                                                        sendNotifications(token,"New Blood Request",s.getSelectedItem().toString() + " required urgently!");
                                                    }else{
                                                        Toast.makeText(getContext(), "I got skipped", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }
                                        }

                                    });

                                    alertDialog.dismiss();
                                }
                            });



                    }
                });








              //  MainActivity.addRequestFragmentReplace();
               // Toast.makeText(getContext(), "I was called", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

    public boolean isValid (EditText editText){
        if (editText.getText().toString().isEmpty()){
            return false;
        }
        return true;
    }



    public void populateRequests(){
        firebaseFirestore.collection("bloodRequests").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        try{
                            if (!document.getData().get("senderId").equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                String nameReqStr = document.getData().get("nameOfReqPerson").toString();
                                String bloodGroup = document.getData().get("bloodGroup").toString();
                                String phoneNumberStr = document.getData().get("phoneNumber").toString();
                                String senderIdStr = document.getData().get("senderId").toString();
                                String locationStr = document.getData().get("Location").toString();
                                String coordStr = document.getData().get("LatLng").toString();
                                BloodRequest bloodRequest = new BloodRequest(document.getId(),nameReqStr,bloodGroup,phoneNumberStr,senderIdStr,locationStr,fetchUserLocation(coordStr));
                                requestList.add(bloodRequest);
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }

                    }

                    bloodRequestAdapter.notifyDataSetChanged();

                }
            }
        });






    }



    public LatLng fetchUserLocation(String coodinateStr){
        String[] values = coodinateStr.split(",");
        double latitude = Double.valueOf(values[0]);
        double longitute = Double.valueOf(values[1]);
        LatLng coodinates = new LatLng(latitude,longitute);
        return coodinates;
    }


    public void sendNotifications(String usertoken, String title, String message) {
        Data data = new Data(title, message);
        NotificationSender sender = new NotificationSender(data, usertoken);

        apiService.sendNotifcation(sender).enqueue(new Callback<MyResponse>() {
            @Override
            public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                if (response.code() == 200) {
                    Toast.makeText(getContext(), "Sent!", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<MyResponse> call, Throwable t) {
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public boolean getUserDetails(){
        final DocumentReference docRef =FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                try {
                    String name = documentSnapshot.get("name").toString();
                    String phoneNumber = documentSnapshot.get("phoneNumber").toString();
                    String city = documentSnapshot.get("city").toString();
                    String country = documentSnapshot.get("country").toString();
                    String dob = documentSnapshot.get("dob").toString();
                    String bloodGroup = documentSnapshot.get("bloodGroup").toString();
                    String donationsCount = documentSnapshot.get("donationsCount").toString();
                    String requestsCount = documentSnapshot.get("requestsCount").toString();
                    LatLng coordinates = fetchUserLocation(documentSnapshot.get("LatLng").toString());
                    String docRef = "";
                    String imgRef = "";
                    if(documentSnapshot.get("docRef")!=null||!documentSnapshot.get("docRef").toString().equalsIgnoreCase("")){
                        docRef = documentSnapshot.get("docRef").toString();
                    }
                    if(documentSnapshot.get("imgRef")!=null||!documentSnapshot.get("imgRef").toString().equalsIgnoreCase("")){
                        imgRef = documentSnapshot.get("imgRef").toString();
                    }

                    city = city.substring(0,1).toUpperCase()+ city.substring(1);
                    country = country.substring(0,1).toUpperCase()+ country.substring(1);

                    user = new User(firebaseAuth.getCurrentUser().getUid(),name,city,country,coordinates,dob,phoneNumber,bloodGroup, docRef, imgRef);

                }catch (Exception e){
                    e.printStackTrace();
                }

            }

        });
        return true;
    }

}