package com.azizbek.myyandexmapapp.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.azizbek.myyandexmapapp.R;


public class SplashActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getSupportActionBar().hide();
     new Handler().postDelayed(new Runnable() {
         @Override
         public void run() {
             startActivity(new Intent(SplashActivity.this, SearchActivity.class));
             finish();
         }
     }, 2000);
    }
}
