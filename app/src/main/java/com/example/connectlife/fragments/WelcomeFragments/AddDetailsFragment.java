package com.example.connectlife.fragments.WelcomeFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.connectlife.MainActivity;
import com.example.connectlife.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class AddDetailsFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore;
    String userId;
    EditText nameField,dobField,addressField,cityField,countryField;
    Button saveButton;

    public AddDetailsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.fragment_add_details, container, false);
        nameField= view.findViewById(R.id.name_field);
        dobField=view.findViewById(R.id.dob_field);
        addressField=view.findViewById(R.id.address_field);
        cityField= view.findViewById(R.id.city_field);
        countryField=view.findViewById(R.id.country_field);
        saveButton=view.findViewById(R.id.save_btn);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        userId = firebaseAuth.getCurrentUser().getUid();
        final DocumentReference documentReference = firebaseFirestore.collection("users").document(userId);


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!(
                        nameField.getText().toString().isEmpty()
                        &&dobField.getText().toString().isEmpty()
                        &&addressField.getText().toString().isEmpty()
                        &&cityField.getText().toString().isEmpty()
                        &&countryField.getText().toString().isEmpty()
                        )){

                    String name = nameField.getText().toString();
                    String dob= dobField.getText().toString();
                    String address =addressField.getText().toString();
                    String city = cityField.getText().toString();
                    String country = countryField.getText().toString();

                    Map<String,Object> user = new HashMap<>();
                    user.put("name",name);
                    user.put("dob",dob);
                    user.put("address",address);
                    user.put("city",city);
                    user.put("country",country);
                    user.put("country",country);

                    documentReference.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(getContext(), "Error occured", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        return view;
    }
}