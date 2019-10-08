package com.thoms.silonaorangtua;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.karan.churi.PermissionManager.PermissionManager;
import com.thoms.silonaorangtua.Model.SharedVariable;

import java.util.ArrayList;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class Beranda extends AppCompatActivity {

    EditText Email, Password;

    PermissionManager manager;
    FirebaseAuth auth;
    FirebaseUser user;
    TextView lupaPassword;


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beranda);
        Email = (EditText)findViewById(R.id.edtEmail);
        Password = (EditText)findViewById(R.id.edtPassword);
        lupaPassword = findViewById(R.id.lupaPassword);

       /* CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/gothambook.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
*/


        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        if (user == null){


            manager = new PermissionManager() {};
            manager.checkAndRequestPermissions(this);
        }
        else {
            Intent myIntent = new Intent(Beranda.this,Home.class);
            startActivity(myIntent);
            finish();
        }

        lupaPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(Beranda.this,LupaPasswordActivity.class);
                startActivity(myIntent);
            }
        });

    }

    public void goToTentang (View v){
        Intent myIntent = new Intent (Beranda.this, Tentang.class);
        startActivity(myIntent);
        finish();

    }

    public void goToBantuan (View v){
        Intent myIntent = new Intent (Beranda.this, Bantuan.class);
        startActivity(myIntent);
        finish();

    }

    public void goToDaftar (View v){
        Intent myIntent = new Intent (Beranda.this, DaftarOrangtua.class);
        startActivity(myIntent);
        finish();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        manager.checkResult(requestCode,permissions,grantResults);


        ArrayList<String> denied_permissions = manager.getStatus().get(0).denied;

        if(denied_permissions.isEmpty())
        {
            Toast.makeText(getApplicationContext(),"Izin lokasi diberikan.",Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Izin lokasi tidak diberikan. Mohon berikan izin.",Toast.LENGTH_SHORT).show();
        }
    }

    public void loginOrangtua(View view){
        login();
    }

    private void login() {
        if (!validate()) {
            onLoginFailed();
            return;
        }
        //   mLoginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(Beranda.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();
        //onLoginSuccess();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onLoginSuccess();
                        progressDialog.dismiss();
                    }
                }, 3000);
    }

    public void LupaPassword (View view){
        //Intent myIntent = new Intent (Beranda.this, ResetPassword.class);
        // startActivity(myIntent);
    }

    public boolean validate() {
        boolean valid = true;

        String email = Email.getText().toString();
        String password = Password.getText().toString();
        SharedVariable.tempPassword = password;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Email.setError("Masukkan Email dengan Benar");
            valid = false;
        } else {
            Password.setError(null);
        }

        if (password.isEmpty() || password.length() < 6 || password.length() > 15) {
            Password.setError("antara 6 dan 15 karakter alfanumerik ");
            valid = false;
        } else {
            Password.setError(null);
        }

        return valid;
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Gagal Masuk", Toast.LENGTH_LONG).show();
    }


    public void onLoginSuccess() {

        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();
        if (!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password)){
            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){
                        SharedVariable.userId = auth.getCurrentUser().getUid();
                        startActivity(new Intent(Beranda.this,Home.class));
                        finish();
                    }
                    else if (!task.isSuccessful()){
                        Toast.makeText(Beranda.this, "Akun Tidak Ada", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }





}
