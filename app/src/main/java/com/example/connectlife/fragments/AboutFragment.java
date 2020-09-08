package com.example.connectlife.fragments;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.connectlife.R;


public class AboutFragment extends Fragment {

    View rateBtn,shareBtn,moreAppsBtn;

    public AboutFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_about, container, false);
        rateBtn = view.findViewById(R.id.rate_us_btn);
        shareBtn = view.findViewById(R.id.share_btn);
        moreAppsBtn = view.findViewById(R.id.more_apps_btn);
        shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        rateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri= Uri.parse("market://details?id="+getContext().getPackageName());
                Intent goToMarket = new Intent(Intent.ACTION_VIEW,uri);
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP){
                    goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY
                            |Intent.FLAG_ACTIVITY_NEW_DOCUMENT|
                            Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                }
                try{
                    startActivity(goToMarket);
                }
                catch (ActivityNotFoundException e){
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://play.google.com/store/apps/details?id="+getContext().getPackageName())));
                }
            }
        });

        moreAppsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://play.google.com/store/apps/developer?id=talhahussain")));
                }catch(android.content.ActivityNotFoundException anfe){
                    startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse("http://play.google.com/store/apps/developer?id=talhahussain")));
                }
            }
        });
        return view;
    }

}