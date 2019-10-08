package com.thoms.silonaorangtua;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import retrofit2.http.Url;

public class LiveMapsActivity extends AppCompatActivity implements OnMapReadyCallback{

    String latitude,longitude,nama,userid,date,alamat,kota,state,country,postalCode,knownName;
    DatabaseReference reference;

    GoogleMap mMap;

    String myNama,myLat,myLng,myDate,imageUrl;
    URL myUrl;
    Bitmap bmp;
    LatLng friendLatLng;
    MarkerOptions myOptions;
    Marker marker;
    double lat,lon;

    Geocoder geocoder;
    List<Address> addressList;
    public static android.app.AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_maps);

        Intent intent = getIntent();
        myOptions = new MarkerOptions();
        if(intent!=null)
        {
            latitude=intent.getStringExtra("latitude");
            longitude = intent.getStringExtra("longitude");
            nama = intent.getStringExtra("nama");
            userid = intent.getStringExtra("userid");
            date = intent.getStringExtra("date");
            imageUrl = intent.getStringExtra("imageUrl");

            lat = Double.parseDouble(latitude);
            lon = Double.parseDouble(longitude);
        }


        reference = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(userid);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        geocoder = new Geocoder(this,Locale.getDefault());
        try {
            addressList = geocoder.getFromLocation(lat,lon,1);

            alamat  = addressList.get(0).getAddressLine(0);
            kota    = addressList.get(0).getLocality();
        } catch (IOException e) {
            e.printStackTrace();
        }


        reference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        myNama = dataSnapshot.child("nama").getValue(String.class);
                        myLat = dataSnapshot.child("latitude").getValue(String.class);
                        myLng = dataSnapshot.child("longitude").getValue(String.class);
                        myDate = dataSnapshot.child("lasttime").getValue(String.class);


                        friendLatLng = new LatLng(Double.parseDouble(myLat),Double.parseDouble(myLng));

                        myOptions.position(friendLatLng);
                        myOptions.snippet(" "+myDate);
                        myOptions.title(myNama);

                        if (marker == null){
                            marker = mMap.addMarker(myOptions);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(friendLatLng, 15));
                        }
                        else {
                            marker.setPosition(friendLatLng);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {


                View row = getLayoutInflater().inflate(R.layout.custom_snippet, null);
                TextView nameTxt = row.findViewById(R.id.snippetName);
                TextView dateTxt = row.findViewById(R.id.snippetDate);
                TextView lokasiTxt = row.findViewById(R.id.snippetlokasi);
                TextView snippetAlamat = row.findViewById(R.id.snippetAlamat);

                lokasiTxt.setText(lokasiTxt.getText().toString() + latitude +" , " + longitude);


                if (myNama == null && myDate == null) {
                    nameTxt.setText(nama);
                    dateTxt.setText(dateTxt.getText().toString() + date);
                }
                else  {
                    nameTxt.setText(myNama);
                    dateTxt.setText(dateTxt.getText().toString() + myDate);
                }

                if (alamat != null || alamat.length() > 0){
                    snippetAlamat.setText(alamat);
                }

                return row;
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                LayoutInflater minlfater = LayoutInflater.from(LiveMapsActivity.this);
                View v2 = minlfater.inflate(R.layout.dialog_detail_anak, null);
                dialog = new android.app.AlertDialog.Builder(LiveMapsActivity.this).create();
                dialog.setView(v2);

                final TextView txtNama =  v2.findViewById(R.id.txtNama);
                final ImageView ivProfilPict =  v2.findViewById(R.id.ivProfilPict);

                if (imageUrl != null && imageUrl.length() > 10){
                    Glide.with(getApplicationContext())
                            .load(imageUrl)
                            .into(ivProfilPict);
                }else if (imageUrl.equals("na")){
                    ivProfilPict.setImageResource(R.drawable.img_placeholder);
                }

                txtNama.setText(nama);



                dialog.show();
            }
        });

        friendLatLng = new LatLng(Double.parseDouble(latitude),Double.parseDouble(longitude));
        MarkerOptions optionsnew = new MarkerOptions();

        optionsnew.position(friendLatLng);
        optionsnew.title(nama);
        optionsnew.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker));
        //optionsnew.icon(BitmapDescriptorFactory.fromBitmap(bmp));


        if(marker == null)
        {
            marker = mMap.addMarker(optionsnew);
        }
        else
        {
            marker.setPosition(friendLatLng);
        }


        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(friendLatLng,15));


    }
}
