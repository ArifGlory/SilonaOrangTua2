package com.thoms.silonaorangtua;

import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class TutorialGabungAnggota extends AppCompatActivity {

    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial_gabung_anggota);


        viewPager = (ViewPager)findViewById(R.id.viewpager1);
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);
    }

    public void goToBeranda (View v){
        Intent myIntent = new Intent (TutorialGabungAnggota.this, Bantuan_Dalam.class);
        startActivity(myIntent);
        finish();
        return;
    }


    public void onBackPressed(){
        Intent intent = new Intent(TutorialGabungAnggota.this, Bantuan.class);
        startActivity(intent);
        finish();
    }
}
