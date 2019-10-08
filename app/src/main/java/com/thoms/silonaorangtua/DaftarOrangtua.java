package com.thoms.silonaorangtua;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.ProviderQueryResult;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class DaftarOrangtua extends AppCompatActivity {

    EditText ed_nama,ed_email,ed_password;

    FirebaseAuth auth;
    TextView title;
    ProgressDialog dialog;

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daftar_orangtua);
        dialog = new ProgressDialog(this);

//        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
//                .setDefaultFontPath("fonts/GothamBook.ttf")
//                .setFontAttrId(R.attr.fontPath)
//                .build());


        ed_nama = (EditText)findViewById(R.id.input_nama);
        ed_email = (EditText)findViewById(R.id.input_email);
        ed_password = (EditText)findViewById(R.id.input_password);

        auth = FirebaseAuth.getInstance();
    }

    public void buatAkun(View v){

        if (!validate()) {
            //onSignupFailed();
            return;
        }

        final ProgressDialog progressDialog = new ProgressDialog(this,R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Tunggu Sebentar...");
        progressDialog.show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onDaftarSukess();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    private void onDaftarSukess() {

        auth.fetchProvidersForEmail(ed_email.getText().toString())
                .addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                    @Override
                    public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                        if(task.isSuccessful())
                        {
                            dialog.dismiss();
                            boolean check = !task.getResult().getProviders().isEmpty();

                            if(!check)
                            {
                                Intent myIntent = new Intent(DaftarOrangtua.this, NamaPengguna.class);
                                myIntent.putExtra("nama", ed_nama.getText().toString());
                                myIntent.putExtra("email", ed_email.getText().toString());
                                myIntent.putExtra("password", ed_password.getText().toString());
                                myIntent.putExtra("type","orangtua_");

                                startActivity(myIntent);
                                finish();
                            }
                            else
                            {
                                dialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Email ini sudah terdaftar", Toast.LENGTH_SHORT).show();

                            }
                        }
                    }
                });


    }

    private boolean validate() {
        boolean valid = true ;

        String nama = ed_nama.getText().toString();
        String email = ed_email.getText().toString();
        String password = ed_password.getText().toString();

        if (nama.isEmpty() || nama.length() < 5) {
            ed_nama.setError("5 karakter");
            valid = false;
        } else {
            ed_nama.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ed_email.setError("Masukkan Email yang Benar");
            valid = false;
        } else {
            ed_email.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 15) {
            ed_password.setError("antara 6 dan 15 karakter alfanumerik");
            valid = false;
        } else {
            ed_password.setError(null);
        }


        return valid;
    }

    public void onBackPressed(){
        Intent intent = new Intent(DaftarOrangtua.this, Beranda.class);
        startActivity(intent);
        finish();
    }

    public void gotoMasuk(View v)
    {
        Intent myIntent = new Intent(DaftarOrangtua.this,Beranda.class);
        startActivity(myIntent);
        finish();
    }
}
