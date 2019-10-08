package com.thoms.silonaorangtua;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thoms.silonaorangtua.Model.LatLonKirim;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RiwayatMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PolygonOptions polygon;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference ref;
    private SweetAlertDialog pDialogLoading,pDialodInfo;
    private String idAnak,hari;
    Intent intent;
    List<LatLonKirim> listLatLon = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ref  =  FirebaseDatabase.getInstance().getReference();
        polygon = new PolygonOptions();


        intent = getIntent();
        idAnak = intent.getStringExtra("idAnak");
        hari = intent.getStringExtra("hari");
        Log.d("hari:",hari);

        pDialogLoading = new SweetAlertDialog(RiwayatMapsActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);
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
        LatLng unila = new LatLng(-5.369987, 105.239415);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(unila));

        getDataAnak();


    }

    private void getDataAnak(){
        pDialogLoading.show();
        Date date = new Date();
        Calendar calendar = Calendar.getInstance();
        Date dates = calendar.getTime();
        String tanggal     = new SimpleDateFormat("yyyyMMdd").format(new Date());

        listLatLon.clear();

        ref.child("Informasi_Anak").child(idAnak).child(hari).child("listLatLon").orderByKey().limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    String lastTanggal = dataSnapshot.getValue().toString();
                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        lastTanggal = child.getKey();
                    }
                    Log.d("lastTanggal:",lastTanggal);

                    ref.child("Informasi_Anak").child(idAnak).child(hari).child("listLatLon").child(lastTanggal).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            if (dataSnapshot.exists()){
                                for (DataSnapshot child : dataSnapshot.getChildren()){
                                    LatLonKirim latLonKirim = child.getValue(LatLonKirim.class);
                                    listLatLon.add(latLonKirim);
                                    Log.d("latlon:",latLonKirim.latitude);

                                }
                                pDialogLoading.dismiss();
                                //draw polygon
                                for (int c=0;c<listLatLon.size();c++){
                                    LatLonKirim latlon = listLatLon.get(c);
                                    double lat = Double.parseDouble(latlon.getLatitude());
                                    double lon = Double.parseDouble(latlon.getLongitude());

                                    polygon.add(new LatLng(lat,lon));
                                }
                                polygon.fillColor(Color.parseColor("#8005B7E8")).strokeWidth(5).strokeColor(Color.RED);
                                mMap.addPolygon(polygon);

                                int indexMid    = listLatLon.size() / 2;
                                LatLonKirim latlonCenter   = listLatLon.get(indexMid);
                                LatLng center = new LatLng(Double.parseDouble(latlonCenter.getLatitude()),Double.parseDouble(latlonCenter.getLongitude()));
                                mMap.addMarker(new MarkerOptions().position(center)
                                        .title("Area yang dilewati anak").icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, 15));

                            }else {
                                Log.d("getDataAnak","data tidak ada");
                                pDialogLoading.dismiss();
                                new SweetAlertDialog(RiwayatMapsActivity.this, SweetAlertDialog.ERROR_TYPE)
                                        .setTitleText("Oops...")
                                        .setContentText("Belum ada data")
                                        .show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            pDialogLoading.dismiss();
                        }
                    });

                }else {
                    Log.d("getDataAnak","data tidak ada");
                    pDialogLoading.dismiss();
                    new SweetAlertDialog(RiwayatMapsActivity.this, SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Oops...")
                            .setContentText("Belum ada data")
                            .show();
                }





            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pDialogLoading.dismiss();
            }
        });
    }

    private String convertDayToHari(String day){
        String hari = "";

        switch (day){
            case "Monday":
                hari = "Senin";
                break;
            case "Tuesday":
                hari = "Selasa";
                break;
            case "Wednesday":
                hari = "Rabu";
                break;
            case "Thursday":
                hari = "Kamis";
                break;
            case "Friday":
                hari = "Jumat";
                break;
            case "Saturday":
                hari = "Sabtu";
                break;
            case "Sunday":
                hari = "Minggu";
                break;
        }

        return hari;
    }


}
