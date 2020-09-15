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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connectlife.fragments.AboutFragment;
import com.example.connectlife.fragments.HistoryFargment;
import com.example.connectlife.fragments.HomeFragment;
import com.example.connectlife.fragments.MembersNearby;
import com.example.connectlife.fragments.ProfileEditFragment;
import com.example.connectlife.fragments.RequestsFragment;
import com.example.connectlife.fragments.WelcomeFragments.AddRequestFragment;
import com.example.connectlife.models.User;
import com.example.connectlife.notificationPack.Token;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    View headerView;
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
    public static FragmentManager fragmentManager;
    TextView userFullNameTextView, locationTextView;
    ImageView profilePicture;
    String nameGlobal,bloodGroupGlobal, locationCity, locationCountry;
    String imageRef;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        fragmentManager = getSupportFragmentManager();
        userDocument = firebaseFirestore.collection("users").document(firebaseAuth.getCurrentUser().getUid());
        Log.i("userId",firebaseAuth.getCurrentUser().getUid());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getApplicationContext());
        getLocation();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);



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
        headerView = navigationView.getHeaderView(0);
        userFullNameTextView = headerView.findViewById(R.id.user_name_text);
        locationTextView = headerView.findViewById(R.id.location_text);
        profilePicture = headerView.findViewById(R.id.imageView);
        setNavigationHeader();
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

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("MyNotifications","MyNotifications", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "getInstanceID failed", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Toast.makeText(getApplicationContext(), "GET new Instance ID Token", Toast.LENGTH_SHORT).show();
                    }
                });

                 updateToken();

        headerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                Bundle bundle = new Bundle();
                bundle.putString("Name",userFullNameTextView.getText().toString());
                bundle.putString("BloodGroup",bloodGroupGlobal);
                bundle.putString("City",locationCity);
                bundle.putString("Country",locationCountry);
                showEditProfileFragment(bundle);


            }
        });


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

            case R.id.nav_histoy:
                fragmentTitle.setText("My History");
                getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new HistoryFargment(),"History Fragment").commit();
                break;

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




    private void updateToken(){
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        String refreshToken= FirebaseInstanceId.getInstance().getToken();
        Map<String,Object> updateData = new HashMap<>();
        updateData.put("token",refreshToken);
        FirebaseFirestore.getInstance().collection("users").document(firebaseUser.getUid()).update(updateData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(MainActivity.this, "Token Updated!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    public static void showAboutApplicationFragment(){
        fragmentTitle.setText("About Application");
        fragmentManager.beginTransaction().replace(R.id.main_container,new AboutFragment(),"About Fragment").commit();
    }

    public static void showEditProfileFragment(Bundle bundle){
        fragmentTitle.setText("PROFILE");
        Fragment profileFragment = new ProfileEditFragment();
        profileFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.main_container,profileFragment,"Edit Profile").commit();
    }


    @Override
    public void onBackPressed() {

    }

    public void setNavigationHeader(){
        firebaseFirestore.collection("users")
                .document(firebaseAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String fullName = task.getResult().getData().get("name").toString();
                    String city =  task.getResult().getData().get("city").toString();
                    String country =  task.getResult().getData().get("country").toString();
                    String bgroup =  task.getResult().getData().get("bloodGroup").toString();
                    locationCity= city;
                    locationCountry =country;
                    bloodGroupGlobal  = bgroup;
                    userFullNameTextView.setText(fullName);
                    locationTextView.setText(city +", " +country);


                    if(task.getResult().getData().get("imgRef")!=null&&!task.getResult().getData().get("imgRef").toString().equalsIgnoreCase("")){
                        imageRef = task.getResult().getData().get("imgRef").toString();
                        Handler handler = new Handler();
                        Runnable r = new Runnable() {
                            public void run() {
                                Bitmap bitmap = getBitmapFromURL(imageRef);
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


                }
            }
        });

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