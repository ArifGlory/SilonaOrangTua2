package com.thoms.silonaorangtua;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.thoms.silonaorangtua.Model.AnakDaftar;
import com.thoms.silonaorangtua.Model.OrangtuaDaftar;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class NamaPengguna extends AppCompatActivity {
    String nama,email,password,type,imageUrl;
    EditText editText;
    CircleImageView circleImageView;
    Uri resultUri;
    Uri imageUri;

    FirebaseAuth auth;
    StorageReference storageReference;
    DatabaseReference databaseReferenceOrangtua, databaseReferenceAnak;
    FirebaseUser firebaseUser;

    String curent_user_id;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/GothamBook.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_nama_pengguna);

        circleImageView = (CircleImageView)findViewById(R.id.circleImageView);
        editText = (EditText)findViewById(R.id.edtNamaPengguna);
        Intent intent = getIntent();
        nama = intent.getStringExtra("nama");
        email = intent.getStringExtra("email");
        password = intent.getStringExtra("password");
        type = intent.getStringExtra("type");

        editText.setText(type);

        auth = FirebaseAuth.getInstance();

        databaseReferenceOrangtua = FirebaseDatabase.getInstance().getReference().child("Informasi_Orangtua");
        databaseReferenceAnak = FirebaseDatabase.getInstance().getReference().child("Informasi_Anak");
        storageReference = FirebaseStorage.getInstance().getReference().child("Image");
    }

    public void selectimage(View v){
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setActivityTitle("My Crop")
                .setCropShape(CropImageView.CropShape.OVAL)
                .setCropMenuCropButtonTitle("Done")
                .setAspectRatio(1,1)
                .start(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // handle result of CropImageActivity
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                circleImageView.setImageURI(resultUri);
                ((ImageView) findViewById(R.id.circleImageView)).setImageURI(result.getUri());
                Toast.makeText(
                        this, "Gambar Profile Berhasil", Toast.LENGTH_LONG)
                        .show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Gambar Profile Gagal ", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void BuatAkun(View v){
        final ProgressDialog progressDialog = new ProgressDialog(NamaPengguna.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Tunggu Sebentar...");
        progressDialog.show();

        //daftar orangtua
        if (type.equals("orangtua_")){


            Query queryOrangtua = databaseReferenceOrangtua.orderByChild("username").equalTo(editText.getText().toString());

            queryOrangtua.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Nama Pengguna Sudah Ada !", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();

                    }
                    else if (resultUri!=null) {

                        auth.createUserWithEmailAndPassword(email, password)
                                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                    @Override
                                    public void onSuccess(AuthResult authResult) {
                                        firebaseUser = auth.getCurrentUser();
                                        OrangtuaDaftar orangtuaDaftar = new OrangtuaDaftar(nama, editText.getText().toString(), email, password, imageUrl, firebaseUser.getUid());

                                        curent_user_id = firebaseUser.getUid();
                                        databaseReferenceOrangtua.child(curent_user_id).setValue(orangtuaDaftar)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        final StorageReference sr = storageReference.child(firebaseUser.getUid() + ".jpg");
                                                        sr.putFile(resultUri)
                                                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                                    @Override
                                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                                        sr.getDownloadUrl()
                                                                                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                                    @Override
                                                                                    public void onSuccess(Uri uri) {
                                                                                        final String downloadURL = uri.toString();
                                                                                        databaseReferenceOrangtua.child(firebaseUser.getUid()).child("imageUrl").setValue(downloadURL)
                                                                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                    @Override
                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                        if (task.isSuccessful()) {
                                                                                                            progressDialog.dismiss();
                                                                                                            Toast.makeText(getApplicationContext(), "Akun Orangtua Dibuat", Toast.LENGTH_LONG).show();

                                                                                                            finish();
                                                                                                            Intent myIntent = new Intent(NamaPengguna.this, Beranda.class);
                                                                                                            startActivity(myIntent);
                                                                                                            // Toast.makeText(getApplicationContext(), "Email Sent For Verification, Check Email", Toast.LENGTH_SHORT).show();
                                                                                                            //sendVerificationEmail();
                                                                                                        }
                                                                                                    }

                                                                                                });
                                                                                    }
                                                                                });
                                                                    }
                                                                });
                                                    }
                                                });

                                    }
                                });
                    }

                    else {
                        Toast.makeText(getApplicationContext(), "Silahkan Pilih Gambar Profile", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getApplicationContext(),databaseError.getMessage(),Toast.LENGTH_LONG).show();
                }
            });
        }

    }
    public void onBackPressed(){
        Intent intent = new Intent(NamaPengguna.this, DaftarOrangtua.class);
        startActivity(intent);
        finish();
    }
}
