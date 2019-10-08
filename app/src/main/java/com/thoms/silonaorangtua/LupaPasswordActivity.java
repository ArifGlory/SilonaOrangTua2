package com.thoms.silonaorangtua;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class LupaPasswordActivity extends AppCompatActivity {

    EditText etEmail;
    Button btnKirim;
    FirebaseAuth auth;
    FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupa_password);

        etEmail     = findViewById(R.id.etEmail);
        btnKirim    = findViewById(R.id.btnKirim);
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        btnKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkValidation();
            }
        });

    }

    private void checkValidation(){
        String getEmail = etEmail.getText().toString();


        if (etEmail.equals("") || etEmail.length() == 0) {

            new SweetAlertDialog(LupaPasswordActivity.this, SweetAlertDialog.ERROR_TYPE)
                    .setTitleText("Oops...")
                    .setContentText("Semua Field Harus diisi")
                    .show();
        }else {
            auth.sendPasswordResetEmail(getEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Log.d("reset password", "Email sent.");
                        new SweetAlertDialog(LupaPasswordActivity.this, SweetAlertDialog.SUCCESS_TYPE)
                                .setTitleText("Terkirim")
                                .setContentText("Email pemulihan berhasil dikirim")
                                .show();
                    }else{
                        Log.d("reset password", "Email gagal.");
                        new SweetAlertDialog(LupaPasswordActivity.this, SweetAlertDialog.ERROR_TYPE)
                                .setTitleText("Oops...")
                                .setContentText("Terjadi kesalahan, coba lagi nanti")
                                .show();
                    }
                }
            });
        }
    }
    public void onBackPressed(){
        Intent intent = new Intent(LupaPasswordActivity.this, Beranda.class);
        startActivity(intent);
        finish();
    }
}
