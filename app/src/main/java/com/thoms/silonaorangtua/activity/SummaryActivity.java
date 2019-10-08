package com.thoms.silonaorangtua.activity;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thoms.silonaorangtua.Model.AnakDaftar;
import com.thoms.silonaorangtua.Model.SharedVariable;
import com.thoms.silonaorangtua.R;
import com.thoms.silonaorangtua.TergabungAnggota;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class SummaryActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FirebaseUser firebaseUser;
    DatabaseReference ref,anakRef;
    FirebaseAuth auth;
    private ArrayList<String> listIdAnggota = new ArrayList<>();
    private ArrayList<AnakDaftar> anakList = new ArrayList<>();
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    AnakDaftar anakDaftar;
    Intent intent;
    MarkerOptions myOptions;
    Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        anakList = SharedVariable.listAnak;

        pDialogLoading = new SweetAlertDialog(SummaryActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference().child("Informasi_Orangtua").child(firebaseUser.getUid()).child("TergabungAnggota");
        anakRef = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        for (int c= 0;c < anakList.size(); c++){
            AnakDaftar anak = anakList.get(c);

            Double lat          = Double.valueOf(anak.latitude);
            Double lon          = Double.valueOf(anak.longitude);
            LatLng posisiAnak   = new LatLng(lat,lon);
            Log.d("posisi:",""+posisiAnak);

            mMap.addMarker(new MarkerOptions()
                    .position(posisiAnak)
                    .title(anak.nama).snippet("Last time : "+anak.lasttime));

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posisiAnak, 14));

          /*  myOptions.position(posisiAnak);
            myOptions.snippet(" "+anak.lasttime);
            myOptions.title(anak.nama);

            marker.setPosition(posisiAnak);
            marker = mMap.addMarker(myOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(posisiAnak, 15));*/


        }

    }
}
