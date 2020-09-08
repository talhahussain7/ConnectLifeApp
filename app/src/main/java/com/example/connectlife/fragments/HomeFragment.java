package com.example.connectlife.fragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connectlife.MainActivity;
import com.example.connectlife.R;
import com.example.connectlife.WelcomeActivity;
import com.example.connectlife.models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {
    TextView nameView,userLocation;
    TextView donationsCount, requestsCount;
    Button browseButton,inviteButton;
    User user;
    //private Uri filePath;
    FirebaseStorage storage;
    StorageReference storageReference;
    CardView verifyCard;
    int verified = 0;
    final static int PICK_PDF_CODE = 2342;


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
           /* final Handler handler = new Handler();
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
            }, 500);*/

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        nameView= view.findViewById(R.id.user_name);
        userLocation= view.findViewById(R.id.user_location);
        requestsCount= view.findViewById(R.id.reqNum);
        donationsCount=view.findViewById(R.id.livesSavedNum);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        browseButton = view.findViewById(R.id.browseButton);
        verifyCard = view.findViewById(R.id.verifyCard);
        inviteButton = view.findViewById(R.id.invite_btn);
        setHasOptionsMenu(true);


        inviteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inviteUsers();
            }
        });

        browseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDoc();
            }
        });


        final DocumentReference docRef = FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid());
      docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                String name = documentSnapshot.get("name").toString();
                String phoneNumber = documentSnapshot.get("phoneNumber").toString();
                String city = documentSnapshot.get("city").toString();
                String country = documentSnapshot.get("country").toString();
                String dob = documentSnapshot.get("dob").toString();
                String bloodGroup = documentSnapshot.get("bloodGroup").toString();
                LatLng coordinates = fetchUserLocation(documentSnapshot.get("LatLng").toString());
                String  donationCountStr = documentSnapshot.get("donationsCount").toString();
                String requestCountStr = documentSnapshot.get("requestsCount").toString();
                String docRef = "";
                if(documentSnapshot.get("docRef")!=null){
                    docRef = documentSnapshot.get("docRef").toString();
                    verified=1;
                }
                city = city.substring(0,1).toUpperCase()+ city.substring(1);
                country = country.substring(0,1).toUpperCase()+ country.substring(1);
                user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),name,city,country,coordinates,dob,phoneNumber,bloodGroup,docRef);
                nameView.setText(user.getName());
                userLocation.setText(user.getCity() +", "+user.getCountry());
                donationsCount.setText(donationCountStr);
                requestsCount.setText(requestCountStr);
            }

        });

        if(verified==1){
            verifyCard.setVisibility(View.GONE);
        }
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.toolbar_menu_home, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_about:
                MainActivity.showAboutApplicationFragment();
                break;

            case R.id.action_signout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getContext(), WelcomeActivity.class));
                break;

            case R.id.action_delete:
                // TO be added later, just to avoid mistakingly deleting the account for now.
                Toast.makeText(getContext(), "Delete Account", Toast.LENGTH_SHORT).show();
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    public LatLng fetchUserLocation(String coodinateStr){
        String[] values = coodinateStr.split(",");
        double latitude = Double.valueOf(values[0]);
        double longitute = Double.valueOf(values[1]);
        LatLng coodinates = new LatLng(latitude,longitute);
        return coodinates;
    }

    private void getDoc() {
        //for greater than lolipop versions we need the permissions asked on runtime
        //so if the permission is not available user will go to the screen to allow storage permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getActivity().getPackageName()));
            startActivity(intent);
            return;
        }

        //creating an intent for file chooser
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_PDF_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_CODE && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null) {
            //if a file is selected
            if (data.getData() != null) {
                //uploading the file
                uploadDoc(data.getData());
            }else{
                Toast.makeText(getContext(), "No file chosen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void uploadDoc(Uri filePath) {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("userDocuments/"+ FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            updateUrl();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
    }

    private void updateUrl(){
        // Points to the root reference
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child("userDocuments/" + FirebaseAuth.getInstance().getCurrentUser().getUid());
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                Map<String,String> userMap = new HashMap<>();
                userMap.put("docRef",downloadUrl.toString());
                FirebaseFirestore.getInstance().collection("users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .update("docRef", downloadUrl.toString());
                user.setDocRef(downloadUrl.toString());
                verified=1;
                verifyCard.setVisibility(View.GONE);
            }
        });
    }

    public void inviteUsers(){
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
}