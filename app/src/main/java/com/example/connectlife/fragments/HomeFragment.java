package com.example.connectlife.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connectlife.R;
import com.example.connectlife.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mapbox.mapboxsdk.geometry.LatLng;


public class HomeFragment extends Fragment {
    TextView nameView,userLocation;
    TextView livesSavedNum, requestsNum;
    User user;

    public HomeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState== null){

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
// ...Irrelevant code for customizing the buttons and title
                    LayoutInflater inflater = getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.invite_dialog, null);
                    dialogBuilder.setView(dialogView);
                    ImageButton closeBtn = dialogView.findViewById(R.id.close_btn);
                    Button inviteToAppBtn = dialogView.findViewById(R.id.invite_app_btn);
                    final AlertDialog alertDialog = dialogBuilder.create();

                    inviteToAppBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            try{
                                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                                shareIntent.setType("text/plain");
                                shareIntent.putExtra(Intent.EXTRA_SUBJECT,getString(R.string.app_name));
                                String shareMessage = "http://play.google.com/store/apps/details?id="+ getContext().getPackageName();
                                shareIntent.putExtra(Intent.EXTRA_TEXT,shareMessage);
                                startActivity(Intent.createChooser(shareIntent,"Share with"));
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    });

                    closeBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();

                }
            }, 500);

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        nameView= view.findViewById(R.id.user_name);
        userLocation= view.findViewById(R.id.user_location);
        requestsNum= view.findViewById(R.id.reqNum);
        livesSavedNum=view.findViewById(R.id.livesSavedNum);



        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
      docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String name = documentSnapshot.get("name").toString();
                String phoneNumber = documentSnapshot.get("phoneNumber").toString();
                String city = documentSnapshot.get("city").toString();
                String country = documentSnapshot.get("country").toString();
                String dob = documentSnapshot.get("dob").toString();
                String email = documentSnapshot.get("email").toString();
                LatLng coordinates = fetchUserLocation(documentSnapshot.get("LatLng").toString());
                String livesSavedNumStr = documentSnapshot.get("donationsCount").toString();
                String requestsNumStr = documentSnapshot.get("requestsCount").toString();


                city = city.substring(0,1).toUpperCase()+ city.substring(1);
                country = country.substring(0,1).toUpperCase()+ country.substring(1);

                user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),name,city,country,coordinates,dob,phoneNumber,email);
                nameView.setText(user.getName());
                userLocation.setText(user.getCity() +", "+user.getCountry());
                livesSavedNum.setText(livesSavedNumStr);
                requestsNum.setText(requestsNumStr);

            }

        });





        return view;
    }

    public LatLng fetchUserLocation(String coodinateStr){
        String[] values = coodinateStr.split(",");
        double latitude = Double.valueOf(values[0]);
        double longitute = Double.valueOf(values[1]);
        LatLng coodinates = new LatLng(latitude,longitute);
        return coodinates;
    }
}