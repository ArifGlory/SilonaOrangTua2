package com.thoms.silonaorangtua;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.thoms.silonaorangtua.Model.AnakDaftar;
import com.thoms.silonaorangtua.Model.AnggotaBergabung;

import java.lang.reflect.Array;

public class GabungAnggota extends AppCompatActivity {

    EditText editText;

    FirebaseAuth auth;
    FirebaseUser user;
    DatabaseReference reference,currentReference,circleReference,joinedReference,senin,selasa,rabu,kamis,jumat,sabtu,minggu;



    String current_userid,gabunguserid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gabung_anggota);
        editText = (EditText)findViewById(R.id.input_nama);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak");
        currentReference = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(user.getUid());

        currentReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                current_userid = dataSnapshot.child("userid").getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_SHORT).show();

            }
        });

    }

    public void gabung (View v){
        current_userid = user.getUid();


        Query query = reference.orderByChild("username").equalTo(editText.getText().toString());

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               if (dataSnapshot.exists()){
                   AnakDaftar anakDaftar = null;
                   for (DataSnapshot childss : dataSnapshot.getChildren()){
                       anakDaftar = childss.getValue(AnakDaftar.class);
                   }

                   gabunguserid = anakDaftar.userid;

                   circleReference = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(gabunguserid).child("DaftarAnggota");
                   senin = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(gabunguserid).child("Senin");
                   selasa = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(gabunguserid).child("Selasa");
                   rabu = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(gabunguserid).child("Rabu");
                   kamis = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(gabunguserid).child("Kamis");
                   jumat = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(gabunguserid).child("Jumat");
                   sabtu = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(gabunguserid).child("Sabtu");
                   minggu = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak").child(gabunguserid).child("Minggu");


                   joinedReference = FirebaseDatabase.getInstance().getReference().child("Informasi_Orangtua").child(user.getUid()).child("TergabungAnggota");
                   final AnggotaBergabung anggotaBergabung = new AnggotaBergabung(current_userid);
                   final AnggotaBergabung anggotaBergabung1 = new AnggotaBergabung(gabunguserid);

                   senin.child("lasttime").setValue("-");
                   senin.child("latitude").setValue("-");
                   senin.child("longitude").setValue("-");

                   selasa.child("lasttime").setValue("-");
                   selasa.child("latitude").setValue("-");
                   selasa.child("longitude").setValue("-");

                   rabu.child("lasttime").setValue("-");
                   rabu.child("latitude").setValue("-");
                   rabu.child("longitude").setValue("-");

                   kamis.child("lasttime").setValue("-");
                   kamis.child("latitude").setValue("-");
                   kamis.child("longitude").setValue("-");

                   jumat.child("lasttime").setValue("-");
                   jumat.child("latitude").setValue("-");
                   jumat.child("longitude").setValue("-");

                   sabtu.child("lasttime").setValue("-");
                   sabtu.child("latitude").setValue("-");
                   sabtu.child("longitude").setValue("-");

                   minggu.child("lasttime").setValue("-");
                   minggu.child("latitude").setValue("-");
                   minggu.child("longitude").setValue("-");

                   circleReference.child(user.getUid()).setValue(anggotaBergabung)
                           .addOnCompleteListener(new OnCompleteListener<Void>() {
                               @Override
                               public void onComplete(@NonNull Task<Void> task) {
                                   if (task.isSuccessful()){
                                       joinedReference.child(gabunguserid).setValue(anggotaBergabung1)
                                               .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {
                                                       Toast.makeText(getApplicationContext(),"Anda Berhasil Bergabung Dengan Pengguna..",Toast.LENGTH_SHORT).show();
                                                       finish();
                                                       Intent myIntent = new Intent(GabungAnggota.this,Home.class);
                                                       startActivity(myIntent);
                                                   }
                                               });
                                   }
                                   else {
                                       Toast.makeText(getApplicationContext(),"Tidak Dapat Bergabung, Coba Lagi",Toast.LENGTH_SHORT).show();
                                   }

                               }
                           });
               }
               else {
                   Toast.makeText(getApplicationContext(),"Tidak Dapat Menemukan Nama Pengguna...",Toast.LENGTH_LONG).show();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();

            }
        });
    }


}
