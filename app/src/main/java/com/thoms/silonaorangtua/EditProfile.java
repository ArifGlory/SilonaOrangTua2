package com.thoms.silonaorangtua;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.thoms.silonaorangtua.Model.OrangtuaDaftar;
import com.thoms.silonaorangtua.Model.SharedVariable;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class EditProfile extends AppCompatActivity {
    FirebaseAuth auth;
    FirebaseUser user;
    Marker currentMarker;
    LatLng latLng;
    ImageView ep_profil;
    Uri resultUri;
    Uri imageUri;

    EditText etNama,etPassword;
    DatabaseReference reference;
    FirebaseStorage firebaseStorage;
    Button btnUbah;

    StorageReference storageReference;
    DatabaseReference orangtuaReference;
    private SweetAlertDialog pDialogLoading,pDialodInfo;

    Bitmap compressedImageBitmap;
    static final int RC_PERMISSION_READ_EXTERNAL_STORAGE = 1;
    static final int RC_IMAGE_GALLERY = 2;
    Uri uriGambar,file;
    File fileGambar;
    OrangtuaDaftar orangtuaDaftar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        orangtuaReference = FirebaseDatabase.getInstance().getReference().child("Informasi_Orangtua").child(auth.getCurrentUser().getUid());
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image").child(auth.getCurrentUser().getUid());

        ep_profil = (ImageView)findViewById(R.id.ep_profil);
        etNama = findViewById(R.id.etNama);
        etPassword = findViewById(R.id.etPassword);
        btnUbah = findViewById(R.id.btnUbah);
        SpannableString content = new SpannableString("Content");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);

        pDialogLoading = new SweetAlertDialog(EditProfile.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialogLoading.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
        pDialogLoading.setTitleText("Loading..");
        pDialogLoading.setCancelable(false);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButtonProfile);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(EditProfile.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditProfile.this, new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE}, RC_PERMISSION_READ_EXTERNAL_STORAGE);
                } else {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, RC_IMAGE_GALLERY);

                }
            }
        });
        btnUbah.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });


        orangtuaReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                orangtuaDaftar = dataSnapshot.getValue(OrangtuaDaftar.class);


                etNama.setText(orangtuaDaftar.nama);
                etPassword.setText(orangtuaDaftar.password);
                Picasso.get().load(orangtuaDaftar.imageUrl).into(ep_profil);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkValidation(){
        String getNama = etNama.getText().toString();
        String getPassword = etPassword.getText().toString();

        if ( (orangtuaDaftar.nama.equals(getNama)) && (orangtuaDaftar.password.equals(getPassword)) ){
            Toast.makeText(getApplicationContext(),"Anda tidak melakukan perubahan apapun",Toast.LENGTH_SHORT).show();
        }else {
            if (getNama.equals("") || getNama.length() == 0
                    || getPassword.equals("") || getPassword.length() == 0
            ) {

                new SweetAlertDialog(EditProfile.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Oops...")
                        .setContentText("Semua Field Harus diisi")
                        .show();

            }else if (uriGambar == null){
                updateWithoutGambar();
            }else {
                uploadData(uriGambar);
            }
        }


    }

    private void updateWithoutGambar(){
        pDialogLoading.show();
        final String nama     = etNama.getText().toString();
        final String newPassword = etPassword.getText().toString();
        etNama.setEnabled(false);
        etPassword.setEnabled(false);

        orangtuaReference.child("nama").setValue(nama);
        orangtuaReference.child("password").setValue(newPassword);

        AuthCredential credential = EmailAuthProvider.getCredential(auth.getCurrentUser().getEmail(), SharedVariable.tempPassword);

        auth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            auth.getCurrentUser().updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        pDialogLoading.dismiss();
                                        new SweetAlertDialog(EditProfile.this, SweetAlertDialog.SUCCESS_TYPE)
                                                .setTitleText("Sukses!")
                                                .setContentText("Perubahan Berhasil Disimpan")
                                                .show();
                                    } else {
                                        pDialogLoading.dismiss();
                                        Log.d("gantipassword", "Error password not updated");
                                        new SweetAlertDialog(EditProfile.this, SweetAlertDialog.ERROR_TYPE)
                                                .setTitleText("Oops...")
                                                .setContentText("perubahan gagal, coba lagi nanti")
                                                .show();
                                    }
                                }
                            });
                        }else {
                            Log.d("gantipassword", "Error auth failed");
                        }
                    }
                });



    }

    private void uploadData(final Uri uri){
        pDialogLoading.show();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        StorageReference imagesRef = storageRef.child("Image");
        StorageReference userRef = imagesRef.child(auth.getUid());
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String filename = auth.getUid() + "_" + timeStamp;
        final StorageReference fileRef = userRef.child(filename);

        final String nama     = etNama.getText().toString();
        final String password = etPassword.getText().toString();

        etNama.setEnabled(false);
        etPassword.setEnabled(false);

        UploadTask uploadTask = fileRef.putFile(uri);


        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(EditProfile.this, "Upload failed!\n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                pDialogLoading.dismiss();
                etNama.setEnabled(true);
                etPassword.setEnabled(true);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //pDialogLoading.dismiss();


                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        pDialogLoading.dismiss();
                        Toast.makeText(EditProfile.this, "Upload selesai!\n", Toast.LENGTH_SHORT).show();
                        Uri downloadUrl = uri;

                        orangtuaReference.child("imageUrl").setValue(downloadUrl.toString());
                        orangtuaReference.child("nama").setValue(etNama.getText().toString());
                        orangtuaReference.child("password").setValue(etPassword.getText().toString());

                        etNama.setEnabled(true);
                        etPassword.setEnabled(true);

                        updatePassword(etPassword.getText().toString());

                        new SweetAlertDialog(EditProfile.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Sukses!")
                                .setContentText("Data Berhasil Disimpan")
                                .show();

                    }
                });

            }
        });

    }

    public void updatePassword(final String newPassword){
        AuthCredential credential = EmailAuthProvider.getCredential(auth.getCurrentUser().getEmail(), SharedVariable.tempPassword);

        auth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            auth.getCurrentUser().updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("gantipassword", "Password updated");
                                    } else {
                                        Log.d("gantipassword", "Error password not updated");
                                    }
                                }
                            });
                        }else {
                            Log.d("gantipassword", "Error auth failed");
                        }
                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RC_PERMISSION_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, RC_IMAGE_GALLERY);
            }
        }
    }



    public static String GetMimeType(Context context, Uri uriImage)
    {
        String strMimeType = null;

        Cursor cursor = context.getContentResolver().query(uriImage,
                new String[] { MediaStore.MediaColumns.MIME_TYPE },
                null, null, null);

        if (cursor != null && cursor.moveToNext())
        {
            strMimeType = cursor.getString(0);
        }

        return strMimeType;
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
                ep_profil.setImageURI(resultUri);
                StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Image").child(auth.getCurrentUser().getUid() + ".jpg");

                ((ImageView) findViewById(R.id.ep_profil)).setImageURI(result.getUri());
                Toast.makeText(
                        this, "Gambar Profile Berhasil DiPerbaharui", Toast.LENGTH_LONG)
                        .show();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(this, "Gambar Profile Gagal ", Toast.LENGTH_LONG).show();
            }
        }else  if (requestCode == RC_IMAGE_GALLERY && resultCode == RESULT_OK) {
            uriGambar = data.getData();
            ep_profil.setImageURI(uriGambar);
        }
    }


    public void onBackPressed(){
        Intent intent = new Intent(EditProfile.this, Home.class);
        startActivity(intent);
        finish();
    }
}
