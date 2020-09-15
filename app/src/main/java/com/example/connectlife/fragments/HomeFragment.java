package com.example.connectlife.fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connectlife.MainActivity;
import com.example.connectlife.R;
import com.example.connectlife.WelcomeActivity;
import com.example.connectlife.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;


public class HomeFragment extends Fragment {
    TextView nameView,userLocation;
    TextView donationsCount, requestsCount;
    Button browseButton,inviteButton;
    ImageButton addProfilePicture;
    ImageView profilePicture;
    String imgRef="";
    User user;
    FirebaseStorage storage;
    StorageReference storageReference;
    CardView verifyCard;
    int verified = 0;
    final static int PICK_PDF_CODE = 2342;
    final static int PICK_IMAGE_CODE = 71;


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
        addProfilePicture = view.findViewById(R.id.addPictureButton);
        profilePicture = view.findViewById(R.id.profilePicture);
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

        addProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProfilePicture();
            }
        });

        profilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog nagDialog = new Dialog(getActivity(),android.R.style.Theme_Translucent_NoTitleBar_Fullscreen);
                nagDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                nagDialog.setCancelable(false);
                nagDialog.setContentView(R.layout.dialogue_fullscreen);
                nagDialog.setCancelable(true);
                ImageView fsImage = nagDialog.findViewById(R.id.fsImage);
                BitmapDrawable drawable = (BitmapDrawable) profilePicture.getDrawable();
                Bitmap bitmap = drawable.getBitmap();
                fsImage.setImageBitmap(bitmap);
                nagDialog.show();
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
                imgRef = "";
                if(documentSnapshot.get("docRef")!=null&&!documentSnapshot.get("docRef").toString().equalsIgnoreCase("")){
                    docRef = documentSnapshot.get("docRef").toString();
                    verified=1;
                    verifyCard.setVisibility(View.GONE);
                }
                if(documentSnapshot.get("imgRef")!=null&&!documentSnapshot.get("imgRef").toString().equalsIgnoreCase("")){
                        imgRef = documentSnapshot.get("imgRef").toString();
                        Handler handler = new Handler();
                        Runnable r = new Runnable() {
                            public void run() {
                                Bitmap bitmap = getBitmapFromURL(imgRef);
                                if(bitmap!=null){
                                    Bitmap resizedBitmap = getResizedBitmap(bitmap, 1024, 1024);
                                    Bitmap circleBitmap = Bitmap.createBitmap(resizedBitmap.getWidth(), resizedBitmap.getHeight(), Bitmap.Config.ARGB_8888);

                                    BitmapShader shader = new BitmapShader (resizedBitmap,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                                    Paint paint = new Paint();
                                    paint.setShader(shader);
                                    paint.setAntiAlias(true);
                                    Canvas c = new Canvas(circleBitmap);
                                    c.drawCircle(resizedBitmap.getWidth()/2, resizedBitmap.getHeight()/2, resizedBitmap.getWidth()/2, paint);
                                    profilePicture.setImageBitmap(circleBitmap);
                                }

                            }
                        };
                    handler.postDelayed(r, 0);

                }

                city = city.substring(0,1).toUpperCase()+ city.substring(1);
                country = country.substring(0,1).toUpperCase()+ country.substring(1);
                user = new User(FirebaseAuth.getInstance().getCurrentUser().getUid(),name,city,country,coordinates,dob,phoneNumber,bloodGroup,docRef,imgRef);
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
            case R.id.action_donate:
                showDonationPopup();
                break;

            case R.id.action_delete:
                FirebaseFirestore.getInstance().collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(getContext(),WelcomeActivity.class));
                        }
                    }
                });
                break;


        }
        return super.onOptionsItemSelected(item);
    }


    public void showDonationPopup(){
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(getContext());
        final View popupView = getLayoutInflater().inflate(R.layout.donation_popup, null);
        dialogBuilder.setView(popupView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageButton closeButton = popupView.findViewById(R.id.close_btn);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
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

                uploadDoc(data.getData(), "userDocuments/");
            }else{
                Toast.makeText(getContext(), "No file chosen", Toast.LENGTH_SHORT).show();
            }
        } else if(requestCode == PICK_IMAGE_CODE && resultCode == getActivity().RESULT_OK && data != null && data.getData() != null){
            if (data.getData() != null) {
                try {
                  Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());
                  Bitmap resizedBitmap = getResizedBitmap(bitmap, 1024, 1024);

                    Bitmap circleBitmap = Bitmap.createBitmap(resizedBitmap.getWidth(), resizedBitmap.getHeight(), Bitmap.Config.ARGB_8888);

                    BitmapShader shader = new BitmapShader (resizedBitmap,  Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
                    Paint paint = new Paint();
                    paint.setShader(shader);
                    paint.setAntiAlias(true);
                    Canvas c = new Canvas(circleBitmap);
                    c.drawCircle(resizedBitmap.getWidth()/2, resizedBitmap.getHeight()/2, resizedBitmap.getWidth()/2, paint);
                    profilePicture.setImageBitmap(circleBitmap);

                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                uploadDoc(data.getData(), "userProfilePhotos/");
            }else{
                Toast.makeText(getContext(), "No image chosen", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void uploadDoc(Uri filePath, String folder) {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(getContext());
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child(folder+ FirebaseAuth.getInstance().getCurrentUser().getUid());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Uploaded", Toast.LENGTH_SHORT).show();
                            updateUrl(folder);
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

    private void updateUrl(final String folder){
        // Points to the root reference
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference dateRef = storageRef.child(folder + FirebaseAuth.getInstance().getCurrentUser().getUid());
        dateRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>()
        {
            @Override
            public void onSuccess(Uri downloadUrl)
            {
                if(folder.equalsIgnoreCase("userDocuments/")){
                FirebaseFirestore.getInstance().collection("users")
                        .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .update("docRef", downloadUrl.toString());
                user.setDocRef(downloadUrl.toString());
                verified=1;
                verifyCard.setVisibility(View.GONE);}
                else if(folder.equalsIgnoreCase("userProfilePhotos/")){
                    FirebaseFirestore.getInstance().collection("users")
                            .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .update("imgRef", downloadUrl.toString());
                    user.setImgRef(downloadUrl.toString());
                }
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

    public void getProfilePicture(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                    Uri.parse("package:" + getActivity().getPackageName()));
            startActivity(intent);
            return;
        }

//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_CODE);

        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , PICK_IMAGE_CODE);
    }

    public Bitmap getBitmapFromURL(String src) {
        try {
            java.net.URL url = new java.net.URL(src);
            HttpURLConnection connection = (HttpURLConnection) url
                    .openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }

}