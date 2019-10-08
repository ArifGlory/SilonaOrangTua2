package com.thoms.silonaorangtua;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {
    private ImageView silona,family;

    Button btnMasuk, btnDaftar;
    RelativeLayout rootLayout;

    FirebaseAuth auth;
    FirebaseDatabase db;
    DatabaseReference users;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        silona = (ImageView) findViewById(R.id.silona);
        family = (ImageView) findViewById(R.id.family);
        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mytransition);
        silona.startAnimation(myanim);
        family.startAnimation(myanim);

        final Intent i = new Intent(this,Beranda.class);
        Thread timer = new Thread(){
            public void run (){
                try {
                    sleep(3000);
                } catch (InterruptedException e){
                    e.printStackTrace();
                }
                finally {
                    startActivity(i);
                    finish();
                }
            }
        };
        timer.start();
    }


}