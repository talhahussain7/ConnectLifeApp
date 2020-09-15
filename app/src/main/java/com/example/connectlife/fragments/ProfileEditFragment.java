package com.example.connectlife.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connectlife.R;


public class ProfileEditFragment extends Fragment {
TextView nameView, bloodGroupView, locationView;
    public ProfileEditFragment() {
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
        String name = getArguments().get("Name").toString();
        String bloodGroup = getArguments().get("BloodGroup").toString();
        String city =  getArguments().get("City").toString();
        String country =  getArguments().get("Country").toString();
        View view =  inflater.inflate(R.layout.fragment_profile_edit, container, false);
        nameView = view.findViewById(R.id.name_view);
        bloodGroupView = view.findViewById(R.id.blood_group_view);
        locationView = view.findViewById(R.id.location_view);

        nameView.setText(name);
        bloodGroupView.setText("Blood Group: "+bloodGroup);
        locationView.setText(city+", "+country);

        //Toast.makeText(getContext(), name+" "+city +" "+ " "+ bloodGroup, Toast.LENGTH_SHORT).show();
        return view;

    }
}