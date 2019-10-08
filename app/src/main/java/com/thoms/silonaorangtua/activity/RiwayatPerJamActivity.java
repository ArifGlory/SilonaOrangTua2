package com.thoms.silonaorangtua.activity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thoms.silonaorangtua.Adapters.AdapterRiwayatPerjam;
import com.thoms.silonaorangtua.Model.RiwayatPerJam;
import com.thoms.silonaorangtua.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class RiwayatPerJamActivity extends AppCompatActivity {

    RecyclerView rvPerJam;
    AdapterRiwayatPerjam adapter;
    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference ref;
    Geocoder geocoder;
    List<Address> addressList;
    double latitude,longitude;
    String idAnak,hari,alamat;
    Intent intent;
    private List<RiwayatPerJam> riwayatPerJamList;
    private SweetAlertDialog pDialogLoading,pDialodInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riwayat_per_jam);

        rvPerJam = findViewById(R.id.rvPerJam);
        intent = getIntent();
        idAnak = intent.getStringExtra("idAnak");
        hari = intent.getStringExtra("hari");
        alamat = "";

        geocoder = new Geocoder(this, Locale.getDefault());
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        ref = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(idAnak).child(hari).child("listJam");
        riwayatPerJamList = new ArrayList<>();
        adapter = new AdapterRiwayatPerjam(RiwayatPerJamActivity.this,riwayatPerJamList);

        rvPerJam.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        rvPerJam.setHasFixedSize(true);
        rvPerJam.setItemAnimator(new DefaultItemAnimator());
        rvPerJam.setAdapter(adapter);

        pDialogLoading = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);
        pDialogLoading.show();


        getDataRiwayat();
    }

    private void getDataRiwayat(){

        riwayatPerJamList.clear();

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pDialogLoading.dismiss();
                if (dataSnapshot.exists()){

                    for (DataSnapshot child : dataSnapshot.getChildren()){
                        Log.d("isiPerjam:",child.toString());
                        String jam      = child.getKey();
                        String latlon   = child.getValue().toString();

                        int separator   = latlon.indexOf(",");
                        String lat      = latlon.substring(0,separator);
                        String lon      = latlon.substring(separator+1);
                        Log.d("latPerjam:",lat);
                        Log.d("lonPerjam:",lon);
                        latitude = Double.valueOf(lat);
                        longitude = Double.valueOf(lon);
                        try {
                            alamat = getAlamatByLokasi(latitude,longitude);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        RiwayatPerJam riwayatPerJam = new RiwayatPerJam(jam,alamat);
                        riwayatPerJamList.add(riwayatPerJam);
                    }
                    adapter.notifyDataSetChanged();

                }else {
                    Toast.makeText(getApplicationContext(),"Belum ada riwayat",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                pDialogLoading.dismiss();
            }
        });
    }

    private String getAlamatByLokasi(double lati,double longi) throws IOException {

        String alamat = "";
        addressList = geocoder.getFromLocation(lati,longi,1);
        alamat =  addressList.get(0).getAddressLine(0);

        return  alamat;
    }
}
