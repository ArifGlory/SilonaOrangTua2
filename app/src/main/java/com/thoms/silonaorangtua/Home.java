package com.thoms.silonaorangtua;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.LocationRequest;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.android.gms.location.LocationListener;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.thoms.silonaorangtua.Model.OrangtuaDaftar;

public class Home extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener{

    FirebaseAuth auth;
    FirebaseUser user;
    Marker currentMarker;
    LatLng latLng;
    ImageView img_profile;

    TextView nama_txt, email_txt;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;

    StorageReference storageReference;
    DatabaseReference orangtuaReference,anakReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        orangtuaReference = FirebaseDatabase.getInstance().getReference().child("Informasi_Orangtua").child(auth.getCurrentUser().getUid());
        anakReference = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(auth.getCurrentUser().getUid());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image").child(auth.getCurrentUser().getUid());


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        final View header = navigationView.getHeaderView(0);

        nama_txt = (TextView) header.findViewById(R.id.nama_txt);
        email_txt = (TextView) header.findViewById(R.id.email_txt);
        img_profile = (ImageView) header.findViewById(R.id.img_profile);



        orangtuaReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               OrangtuaDaftar orangtuaDaftar =dataSnapshot.getValue(OrangtuaDaftar.class);



                nama_txt.setText(orangtuaDaftar.nama);
                email_txt.setText(orangtuaDaftar.email);
                Picasso.get().load(orangtuaDaftar.imageUrl).into(img_profile);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void logout(){
        FirebaseUser user = auth.getCurrentUser();
        if (auth != null) {
            auth.signOut();
            Intent myIntent = new Intent(Home.this,Beranda.class);
            startActivity(myIntent);
            Toast.makeText(getApplicationContext(), "Anda Telah Keluar", Toast.LENGTH_LONG).show();
        }

    }

    public void gotoEditProfile(View v)
    {
        Intent myIntent = new Intent(Home.this,EditProfile.class);
        startActivity(myIntent);
        finish();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.gab_anggota) {
            Intent myIntent = new Intent(Home.this, GabungAnggota.class);
            startActivity(myIntent);


        } else if (id == R.id.t_gabung) {
            Intent myIntent = new Intent(Home.this, TergabungAnggota.class);
            startActivity(myIntent);


        } else if (id == R.id.d_riwayat) {
            Intent myIntent = new Intent(Home.this, Riwayat.class);
            startActivity(myIntent);


        } else if (id == R.id.b_bantuan) {
                Intent myIntent = new Intent(Home.this, Bantuan_Dalam.class);
                startActivity(myIntent);


        } else if (id == R.id.keluar) {
            logout();
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }




}
