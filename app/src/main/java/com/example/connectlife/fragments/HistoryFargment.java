package com.example.connectlife.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connectlife.R;
import com.example.connectlife.adapters.HistoryItemAdapter;
import com.example.connectlife.models.History;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class HistoryFargment extends Fragment {
FirebaseFirestore firebaseFirestore;
FirebaseAuth firebaseAuth;
RecyclerView recyclerView;
List<History> historyList = new ArrayList<>();
HistoryItemAdapter historyItemAdapter;



    public HistoryFargment() {
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
        View root = inflater.inflate(R.layout.fragment_history_fargment, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        recyclerView = root.findViewById(R.id.recycler_view);
        historyItemAdapter = new HistoryItemAdapter(getContext(),historyList);
        recyclerView.setAdapter(historyItemAdapter);
        getHistoryItems();
        return  root;
    }

    public void getHistoryItems(){
        firebaseFirestore.collection("history").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            String name ="",phoneNumber="",accpetedById = "";

            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for(QueryDocumentSnapshot document :task.getResult()){
                        if(document.getData().get("RequestorId")!=null &&document.getData().get("RequestorId").equals(firebaseAuth.getCurrentUser().getUid())){
                            String requestor = document.getData().get("Requestor").toString();
                            String requestorId = document.getData().get("RequestorId").toString();
                            String bloodGroup =document.getData().get("BloodGroup").toString();
                            String location = document.getData().get("Location").toString();
                            String status = document.getData().get("Status").toString();
                            String date = document.getData().get("Date").toString();
                            if(status.equalsIgnoreCase("approved")){
                                name = document.getData().get("AcceptedBy").toString();
                                accpetedById =document.getData().get("AcceptedById").toString();
                                phoneNumber = document.getData().get("OtherNum").toString();
                            }
                            History historyItem = new History(requestor,requestorId,name,accpetedById,bloodGroup,phoneNumber,status,location,date);
                            historyList.add(historyItem);
                        }

                    }
                    historyItemAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}