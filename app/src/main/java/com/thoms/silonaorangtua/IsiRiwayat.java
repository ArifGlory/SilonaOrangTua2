package com.thoms.silonaorangtua;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.thoms.silonaorangtua.Adapters.DaftarRiwayatAnggota;
import com.thoms.silonaorangtua.Adapters.DaftarTergabungAnggota;
import com.thoms.silonaorangtua.Adapters.DetailRiwayat;
import com.thoms.silonaorangtua.Model.AnakDaftar;
import com.thoms.silonaorangtua.Model.RiwayatAnak;
import com.thoms.silonaorangtua.activity.RiwayatPerJamActivity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class IsiRiwayat extends AppCompatActivity {

    DatabaseReference senin, selasa, rabu, kamis, jumat, sabtu, minggu, currentReference, reference,connectedReference,usersReference;
    FirebaseAuth auth;
    FirebaseUser user;
    TextView lokasi, waktu;
    AnakDaftar anakDaftar;
    String userid, nama, date,alamatTerakir,kota;
    LocationRequest request;
    RiwayatAnak riwayatAnak;
    ArrayList<DetailRiwayat> listriwayat;
    Geocoder geocoder;
    List<Address> addressList;
    double lat,lon;
    LinearLayout lineSenin,lineSelasa,lineKamis,lineRabu,lineJumat,lineSabtu,lineMinggu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isi_riwayat);

        Intent intent = getIntent();

        if(intent != null) {
            nama = intent.getStringExtra("nama");
            userid = intent.getStringExtra("userid");
        }
        geocoder = new Geocoder(this, Locale.getDefault());
        lineSenin = findViewById(R.id.lineSenin);
        lineSelasa = findViewById(R.id.lineSelasa);
        lineKamis = findViewById(R.id.lineKamis);
        lineRabu = findViewById(R.id.lineRabu);
        lineJumat = findViewById(R.id.lineJumat);
        lineSabtu = findViewById(R.id.lineSabtu);
        lineMinggu = findViewById(R.id.lineMinggu);


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        connectedReference = FirebaseDatabase.getInstance().getReference().child("Informasi_Orangtua").child(user.getUid()).child("TergabungAnggota");
        usersReference = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak");
        senin = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(userid);
        selasa = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(userid);
        rabu = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(userid);
        kamis = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(userid);
        jumat = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(userid);
        sabtu = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(userid);
        minggu = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(userid);

        lineSenin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RiwayatPerJamActivity.class);
                intent.putExtra("idAnak",userid);
                intent.putExtra("hari","Senin");
                startActivity(intent);
            }
        });
        lineSelasa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RiwayatPerJamActivity.class);
                intent.putExtra("idAnak",userid);
                intent.putExtra("hari","Selasa");
                startActivity(intent);
            }
        });
        lineRabu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RiwayatPerJamActivity.class);
                intent.putExtra("idAnak",userid);
                intent.putExtra("hari","Rabu");
                startActivity(intent);
            }
        });
        lineKamis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RiwayatPerJamActivity.class);
                intent.putExtra("idAnak",userid);
                intent.putExtra("hari","Kamis");
                startActivity(intent);
            }
        });
        lineJumat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RiwayatPerJamActivity.class);
                intent.putExtra("idAnak",userid);
                intent.putExtra("hari","Jumat");
                startActivity(intent);
            }
        });
        lineSabtu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RiwayatPerJamActivity.class);
                intent.putExtra("idAnak",userid);
                intent.putExtra("hari","Sabtu");
                startActivity(intent);
            }
        });
        lineMinggu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RiwayatPerJamActivity.class);
                intent.putExtra("idAnak",userid);
                intent.putExtra("hari","Minggu");
                startActivity(intent);
            }
        });

        senin.child("Senin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String waktu = dataSnapshot.child("lasttime").getValue(String.class);
                String lokasi = dataSnapshot.child("latitude").getValue(String.class)+" "+dataSnapshot.child("longitude").getValue(String.class);

                String latitude = (String) dataSnapshot.child("latitude").getValue();
                String longitude = (String) dataSnapshot.child("longitude").getValue();


                if (latitude.length() > 3){
                    double lati = Double.parseDouble(latitude);
                    double longi = Double.parseDouble(longitude);
                    try {
                        alamatTerakir = getAlamatByLokasi(lati,longi);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    alamatTerakir = "";
                }

//
                TextView LokasiSenin = (TextView) findViewById(R.id.locSeninTxt);
                LokasiSenin.setText(lokasi+"\n"+alamatTerakir);
                TextView WaktuSenin = (TextView) findViewById(R.id.timeSeninTxt);
                WaktuSenin.setText(waktu);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        selasa.child("Selasa").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String waktu = dataSnapshot.child("lasttime").getValue(String.class);
                String lokasi = dataSnapshot.child("latitude").getValue(String.class)+" "+dataSnapshot.child("longitude").getValue(String.class);
                String latitude = (String) dataSnapshot.child("latitude").getValue();
                String longitude = (String) dataSnapshot.child("longitude").getValue();

                if (latitude.length() > 3){
                    double lati = Double.parseDouble(latitude);
                    double longi = Double.parseDouble(longitude);
                    try {
                        alamatTerakir = getAlamatByLokasi(lati,longi);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    alamatTerakir = "";
                }



//                Toast.makeText(getApplicationContext(), waktu,Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), lokasi,Toast.LENGTH_SHORT).show();
//
                TextView LokasiSelasa = (TextView) findViewById(R.id.locSelasaTxt);
                LokasiSelasa.setText(lokasi+"\n"+alamatTerakir);
                TextView WaktuSelasa = (TextView) findViewById(R.id.timeSelasaTxt);
                WaktuSelasa.setText(waktu);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        rabu.child("Rabu").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String waktu = dataSnapshot.child("lasttime").getValue(String.class);
                String lokasi = dataSnapshot.child("latitude").getValue(String.class)+" "+dataSnapshot.child("longitude").getValue(String.class);

                String latitude = (String) dataSnapshot.child("latitude").getValue();
                String longitude = (String) dataSnapshot.child("longitude").getValue();
                if (latitude.length() > 3){
                    double lati = Double.parseDouble(latitude);
                    double longi = Double.parseDouble(longitude);
                    try {
                        alamatTerakir = getAlamatByLokasi(lati,longi);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    alamatTerakir = "";
                }
//                Toast.makeText(getApplicationContext(), waktu,Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), lokasi,Toast.LENGTH_SHORT).show();
//
                TextView LokasiRabu = (TextView) findViewById(R.id.locRabuTxt);
                LokasiRabu.setText(lokasi+"\n"+alamatTerakir);
                TextView WaktuRabu = (TextView) findViewById(R.id.timeRabuTxt);
                WaktuRabu.setText(waktu);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        kamis.child("Kamis").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String waktu = dataSnapshot.child("lasttime").getValue(String.class);
                String lokasi = dataSnapshot.child("latitude").getValue(String.class)+" "+dataSnapshot.child("longitude").getValue(String.class);

                String latitude = (String) dataSnapshot.child("latitude").getValue();
                String longitude = (String) dataSnapshot.child("longitude").getValue();
                if (latitude.length() > 3){
                    double lati = Double.parseDouble(latitude);
                    double longi = Double.parseDouble(longitude);
                    try {
                        alamatTerakir = getAlamatByLokasi(lati,longi);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    alamatTerakir = "";
                }
//                Toast.makeText(getApplicationContext(), waktu,Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), lokasi,Toast.LENGTH_SHORT).show();
//
                TextView LokasiRabu = (TextView) findViewById(R.id.locKamisTxt);
                LokasiRabu.setText(lokasi+"\n"+alamatTerakir);
                TextView WaktuRabu = (TextView) findViewById(R.id.timeKamisTxt);
                WaktuRabu.setText(waktu);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        rabu.child("Jumat").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String waktu = dataSnapshot.child("lasttime").getValue(String.class);
                String lokasi = dataSnapshot.child("latitude").getValue(String.class)+" "+dataSnapshot.child("longitude").getValue(String.class);

                String latitude = (String) dataSnapshot.child("latitude").getValue();
                String longitude = (String) dataSnapshot.child("longitude").getValue();
                if (latitude.length() > 3){
                    double lati = Double.parseDouble(latitude);
                    double longi = Double.parseDouble(longitude);
                    try {
                        alamatTerakir = getAlamatByLokasi(lati,longi);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    alamatTerakir = "";
                }
//                Toast.makeText(getApplicationContext(), waktu,Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), lokasi,Toast.LENGTH_SHORT).show();
//
                TextView LokasiRabu = (TextView) findViewById(R.id.locJumatTxt);
                LokasiRabu.setText(lokasi+"\n"+alamatTerakir);
                TextView WaktuRabu = (TextView) findViewById(R.id.timeJumatTxt);
                WaktuRabu.setText(waktu);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        rabu.child("Sabtu").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String waktu = dataSnapshot.child("lasttime").getValue(String.class);
                String lokasi = dataSnapshot.child("latitude").getValue(String.class)+" "+dataSnapshot.child("longitude").getValue(String.class);

                String latitude = (String) dataSnapshot.child("latitude").getValue();
                String longitude = (String) dataSnapshot.child("longitude").getValue();
                if (latitude.length() > 3){
                    double lati = Double.parseDouble(latitude);
                    double longi = Double.parseDouble(longitude);
                    try {
                        alamatTerakir = getAlamatByLokasi(lati,longi);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    alamatTerakir = "";
                }
//                Toast.makeText(getApplicationContext(), waktu,Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), lokasi,Toast.LENGTH_SHORT).show();
//
                TextView LokasiRabu = (TextView) findViewById(R.id.locSabtuTxt);
                LokasiRabu.setText(lokasi+"\n"+alamatTerakir);
                TextView WaktuRabu = (TextView) findViewById(R.id.timeSabtuTxt);
                WaktuRabu.setText(waktu);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

        rabu.child("Minggu").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String waktu = dataSnapshot.child("lasttime").getValue(String.class);
                String lokasi = dataSnapshot.child("latitude").getValue(String.class)+" "+dataSnapshot.child("longitude").getValue(String.class);

                String latitude = (String) dataSnapshot.child("latitude").getValue();
                String longitude = (String) dataSnapshot.child("longitude").getValue();
                if (latitude.length() > 3){
                    double lati = Double.parseDouble(latitude);
                    double longi = Double.parseDouble(longitude);
                    try {
                        alamatTerakir = getAlamatByLokasi(lati,longi);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else {
                    alamatTerakir = "";
                }
//                Toast.makeText(getApplicationContext(), waktu,Toast.LENGTH_SHORT).show();
//                Toast.makeText(getApplicationContext(), lokasi,Toast.LENGTH_SHORT).show();
//
                TextView LokasiRabu = (TextView) findViewById(R.id.locMingguTxt);
                LokasiRabu.setText(lokasi+"\n"+alamatTerakir);
                TextView WaktuRabu = (TextView) findViewById(R.id.timeMingguTxt);
                WaktuRabu.setText(waktu);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }


    private String getAlamatByLokasi(double lati,double longi) throws IOException {

        String alamat = "";
        addressList = geocoder.getFromLocation(lati,longi,1);
        alamat =  addressList.get(0).getAddressLine(0);

        return  alamat;
    }



    public void rincianKamis(View v){
        String hari        = "Kamis";
        Intent myIntent = new Intent(IsiRiwayat.this,RiwayatMapsActivity.class);
        myIntent.putExtra("idAnak",userid);
        myIntent.putExtra("hari",hari);
        startActivity(myIntent);
    }

    public void rincianSelasa(View v){
        String hari        = "Selasa";
        Intent myIntent = new Intent(IsiRiwayat.this,RiwayatMapsActivity.class);
        myIntent.putExtra("idAnak",userid);
        myIntent.putExtra("hari",hari);
        startActivity(myIntent);
    }

    public void rincianSenin(View v){
        String hari        = "Senin";
        Intent myIntent = new Intent(IsiRiwayat.this,RiwayatMapsActivity.class);
        myIntent.putExtra("idAnak",userid);
        myIntent.putExtra("hari",hari);
        startActivity(myIntent);
    }

    public void rincianRabu(View v){
        String hari        = "Rabu";
        Intent myIntent = new Intent(IsiRiwayat.this,RiwayatMapsActivity.class);
        myIntent.putExtra("idAnak",userid);
        myIntent.putExtra("hari",hari);
        startActivity(myIntent);
    }



    public void rincianJumat(View v){
        String hari        = "Jumat";
        Intent myIntent = new Intent(IsiRiwayat.this,RiwayatMapsActivity.class);
        myIntent.putExtra("idAnak",userid);
        myIntent.putExtra("hari",hari);
        startActivity(myIntent);
    }
    public void rincianSabtu(View v){
        String hari        = "Sabtu";
        Intent myIntent = new Intent(IsiRiwayat.this,RiwayatMapsActivity.class);
        myIntent.putExtra("idAnak",userid);
        myIntent.putExtra("hari",hari);
        startActivity(myIntent);
    }

    public void rincianMinggu(View v){
        String hari        = "Minggu";
        Intent myIntent = new Intent(IsiRiwayat.this,RiwayatMapsActivity.class);
        myIntent.putExtra("idAnak",userid);
        myIntent.putExtra("hari",hari);
        startActivity(myIntent);
    }


    public void goToRiwayatMaps(View view) {
        Calendar calendar = Calendar.getInstance();
        Date dates = calendar.getTime();
        String tanggal     = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String day         = new SimpleDateFormat("EEEE", Locale.ENGLISH).format(dates.getTime());
        String hari        = convertDayToHari(day);

        Intent myIntent = new Intent(IsiRiwayat.this,RiwayatMapsActivity.class);
        myIntent.putExtra("idAnak",userid);
        myIntent.putExtra("hari",hari);
        startActivity(myIntent);
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