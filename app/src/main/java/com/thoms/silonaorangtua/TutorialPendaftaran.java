package com.thoms.silonaorangtua;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TutorialPendaftaran extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_pendaftaran);


        viewPager = (ViewPager)findViewById(R.id.viewpager);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
    }

    public void goToBeranda (View v){
        Intent myIntent = new Intent (TutorialPendaftaran.this, Bantuan .class);
        startActivity(myIntent);
        finish();
        return;
    }


    public void onBackPressed(){
        Intent intent = new Intent(TutorialPendaftaran.this, Bantuan.class);
        startActivity(intent);
        finish();
    }
}
