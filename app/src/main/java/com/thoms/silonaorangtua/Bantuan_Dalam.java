package com.thoms.silonaorangtua;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Bantuan_Dalam extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bantuan__dalam);
    }


    public void gotoHome (View v){
        Intent myIntent = new Intent (Bantuan_Dalam.this, Home.class);
        startActivity(myIntent);
        finish();
        return;
    }

    public void goToTutorialGabungAnggota (View v){
        Intent myIntent = new Intent (Bantuan_Dalam.this, TutorialGabungAnggota.class);
        startActivity(myIntent);
        finish();
        return;
    }

    public void onBackPressed(){
        Intent intent = new Intent(Bantuan_Dalam.this, Home.class);
        startActivity(intent);
        finish();
    }
}
