package com.appbikeroute;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.appbikeroute.databinding.ActivityPrincipalBinding;
import com.appbikeroute.ui.home.HomeFragment;
import com.appbikeroute.ui.salud.SaludFragment;
import com.appbikeroute.ui.slideshow.SlideshowFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.maps.android.data.kml.KmlPoint;

import org.jetbrains.annotations.NotNull;

public class PrincipalActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityPrincipalBinding binding;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toolbar = findViewById(R.id.toolbar);

        binding = ActivityPrincipalBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarPrincipal.toolbar);
        /*
        binding.appBarPrincipal.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        */

        mAuth = FirebaseAuth.getInstance();

        ///////////////////////////////////////////
        //time time = new time();
        //time.execute();
        //init();


        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_misrutas, R.id.nav_salud)
                .setOpenableLayout(drawer)
                //.setDrawerLayout(drawer)
                .build();

        final NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_content_principal);
        final NavController navController = navHostFragment.getNavController();

        //BottomNavigationView bottomNavigationView = findViewById(R.id.);
        //NavigationUI.setupWithNavController(bottomNavigationView, navController);

        //navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_principal);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);



        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                FragmentManager fm =  getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();

                int opc = item.getItemId();
                int title=R.id.nav_misrutas;
                switch(opc){
                    case R.id.nav_home:
                        ft.replace(R.id.nav_host_fragment_content_principal, new HomeFragment());
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        title=R.id.nav_home;
                        ft.addToBackStack(null).commit();
                        break;
                    case R.id.nav_salud:
                        ft.replace(R.id.nav_host_fragment_content_principal, new SaludFragment());
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        title=R.id.nav_salud;
                        ft.addToBackStack(null).commit();
                        break;
                    case R.id.nav_misrutas:
                        ft.replace(R.id.nav_host_fragment_content_principal, new SlideshowFragment());
                        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                        title=R.id.nav_misrutas;
                        ft.addToBackStack(null).commit();
                        break;
                    case R.id.bcerrarsesion:
                        mAuth.signOut();
                        startActivity(new Intent(PrincipalActivity.this, AuthActivity.class));
                        finish();
                        break;
                    case R.id.botonSalir:
                        finishAffinity();
                        System.exit(0);
                        break;
                }

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                setTitle(getString(title));
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.principal, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_principal);

        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    /////////////////////////////////////////////////

    @Override
    protected void onResume() {
        super.onResume();

    }



}

