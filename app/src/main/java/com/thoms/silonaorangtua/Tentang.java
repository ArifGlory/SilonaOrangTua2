package com.thoms.silonaorangtua;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Tentang extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tentang);
    }

    public void goToBeranda (View v){
        Intent myIntent = new Intent (Tentang.this, Beranda.class);
        startActivity(myIntent);
        finish();
        return;
    }

    public void onBackPressed(){
        Intent intent = new Intent(Tentang.this, Beranda.class);
        startActivity(intent);
        finish();
    }
}
