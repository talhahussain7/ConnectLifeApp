package com.example.connectlife.fragments.WelcomeFragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.connectlife.R;
import com.example.connectlife.WelcomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Iterator;


public class LoginFragment extends Fragment {
    Button accountRegisterBtn, continueButton;
    EditText emailField,phoneField;
    FirebaseFirestore firebaseFirestore;



    public LoginFragment() {
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
        View root= inflater.inflate(R.layout.fragment_login, container, false);
        firebaseFirestore= FirebaseFirestore.getInstance();
        accountRegisterBtn = root.findViewById(R.id.acc_register_btn);
        continueButton=root.findViewById(R.id.continue_btn);
        emailField=root.findViewById(R.id.email_field);
        phoneField=root.findViewById(R.id.phone_field);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!(emailField.getText().toString().isEmpty()&&phoneField.getText().toString().isEmpty())){
                    FirebaseFirestore.getInstance().collection("users")
                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            Toast.makeText(getContext(), task.getResult().getDocuments().size(), Toast.LENGTH_SHORT).show();
                            if(task.isSuccessful()){
                                for(QueryDocumentSnapshot document :task.getResult()){
                                    Log.d("User Collection", document.getId() + " => " + document.getData());
                                }
                            }else{
                                Log.d("User Error","Error Fetching data");
                            }
                        }
                    });

                }

            }
        });

        accountRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WelcomeActivity.fragmentManager.beginTransaction().replace(R.id.welcome_container,new RegisterFragment(),"Register Fragment").commit();
            }
        });



        return root;
    }
}