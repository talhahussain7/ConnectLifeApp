package com.example.connectlife.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.connectlife.R;
import com.example.connectlife.models.History;

import java.util.List;

public class HistoryItemAdapter extends RecyclerView.Adapter<HistoryItemAdapter.MyViewHolder> {
    Context context;
    List<History> myList;

    public HistoryItemAdapter(Context context, List<History> myList) {
        this.context = context;
        this.myList = myList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        History item = myList.get(position);

        holder.bloodgroup.setText(item.getBloodGroup());
        holder.status.setText(item.getStatus());
        holder.location.setText(item.getLocation());
        holder.date.setText(item.getDate());
        if(item.getStatus().equalsIgnoreCase("approved")){
            holder.backgroundColor.setBackgroundColor(Color.parseColor("#7EAE79"));
           holder.name.setText("Name: " + item.getAcceptedBy());
           holder.phoneNumber.setText("Phone Number: "+item.getOtherNum());
        }else{
            holder.backgroundColor.setBackgroundColor(Color.parseColor("#F36464"));
        }
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, location,status,bloodgroup,phoneNumber,date;
        View backgroundColor;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            location=itemView.findViewById(R.id.location);
            status = itemView.findViewById(R.id.status);
            bloodgroup = itemView.findViewById(R.id.blood_group);
            phoneNumber = itemView.findViewById(R.id.phoneNumber);
            backgroundColor = itemView.findViewById(R.id.view_bg);
            date=itemView.findViewById(R.id.date);
        }
    }
}
