package com.example.connectlife;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connectlife.fragments.HomeFragment;
import com.example.connectlife.fragments.MembersNearby;
import com.example.connectlife.fragments.RequestsFragment;
import com.example.connectlife.fragments.WelcomeFragments.AddRequestFragment;
import com.example.connectlife.models.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    View mainContainer;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    public static NavigationView navigationView;
    static TextView fragmentTitle;
    FusedLocationProviderClient fusedLocationProviderClient;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth firebaseAuth;
    DocumentReference userDocument;
    public static User currentUser;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
      userDocument = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
       Log.i("userId",firebaseAuth.getCurrentUser().getUid());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        getLocation();


        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));

        mainContainer = findViewById(R.id.main_container);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        fragmentTitle = findViewById(R.id.frag_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        actionBarDrawerToggle.syncState();


        Window window = this.getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container, new HomeFragment(), "Home Fragment").commit();
        }


    }

    private void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location != null) {

                        try {
                            Geocoder geocoder = new Geocoder(MainActivity.this,
                                    Locale.getDefault());
                            LatLng coordinates = new LatLng(location.getLatitude(),location.getLongitude());
                            setLocation(coordinates);
                            Log.d("Location",location.getLatitude() +" , " +location.getLongitude());
                            //Toast.makeText(getApplicationContext(), location.getLatitude() + " , " + location.getLongitude(), Toast.LENGTH_SHORT).show();

                        } catch (Exception e) {
                            Toast.makeText(MainActivity.this, "Could Not fetch Location Data!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }else{
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    44);
        }

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_home:
                fragmentTitle.setText(R.string.app_name);
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new HomeFragment(),"Home Fragment").commit();
                break;
            case R.id.nav_requests:
                fragmentTitle.setText("Blood Requests");
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new RequestsFragment(),"Request Fragment").commit();
                break;
            case R.id.nav_nearby:
                fragmentTitle.setText("Members Nearby");
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new MembersNearby(),"Members Nearby Fragment").commit();
                break;
            case R.id.nav_delete_acc:
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if(user!=null){
                    userDocument.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "User Deleted", Toast.LENGTH_SHORT).show();
                                firebaseAuth.signOut();
                                startActivity(new Intent(MainActivity.this,WelcomeActivity.class));
                                finish();
                            }else {
                                Toast.makeText(getApplicationContext(), "User Deletion failed", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    Toast.makeText(getApplicationContext(), "User Deletion failed: USER IS NULL", Toast.LENGTH_SHORT).show();
                }


            case R.id.nav_signout:
                firebaseAuth.signOut();
                startActivity(new Intent(this,WelcomeActivity.class));
                finish();
                break;

            default:
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        navigationView.setCheckedItem(item);
        return false;
    }

    public void setLocation(LatLng point){
        Map<String,Object> userInfo = new HashMap<>();
        String pointStr = point.getLatitude()+ ","+point.getLongitude();
        userInfo.put("LatLng",pointStr);
        userDocument.update(userInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Location Updated!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }







}