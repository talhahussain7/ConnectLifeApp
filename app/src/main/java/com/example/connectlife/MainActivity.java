package com.example.connectlife;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.connectlife.fragments.HomeFragment;
import com.example.connectlife.fragments.MembersNearby;
import com.example.connectlife.fragments.RequestsFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.mapbox.mapboxsdk.Mapbox;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    View mainContainer;
    DrawerLayout drawerLayout;
    Toolbar toolbar;
    public static NavigationView navigationView;
    TextView fragmentTitle;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Mapbox.getInstance(getApplicationContext(), getString(R.string.mapbox_access_token));

        mainContainer = findViewById(R.id.main_container);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        fragmentTitle=findViewById(R.id.frag_title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(null);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.nav_drawer_open,R.string.nav_drawer_close);
        actionBarDrawerToggle.syncState();


        Window window = this.getWindow();
// clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
// add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
// finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimary));
        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction().replace(R.id.main_container,new HomeFragment(),"Home Fragment").commit();
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
                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this, "Complete this", Toast.LENGTH_SHORT).show();

                            FirebaseFirestore.getInstance().collection("users").document(user.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){
                                    Toast.makeText(getApplicationContext(), "User Deleted", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MainActivity.this,WelcomeActivity.class));
                                    finish();
                                }     else{
                                    Toast.makeText(getApplicationContext(), "Could Not delete the user! Sorry", Toast.LENGTH_SHORT).show();
                                }
                                }
                            });


                           /* FirebaseFirestore.getInstance().document(user.getUid()).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(MainActivity.this, "Account Deleted", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainActivity.this,WelcomeActivity.class));
                                        finish();
                                    }
                                }
                            });*/




                        }else{
                            Toast.makeText(MainActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                break;
            default:
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        navigationView.setCheckedItem(item);
        return false;
    }




}