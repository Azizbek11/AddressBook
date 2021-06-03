package com.azizbek.myyandexmapapp.view;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.azizbek.myyandexmapapp.R;

import java.util.Objects;


public class SplashActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        Objects.requireNonNull(getSupportActionBar()).hide();

       checkInternet();

    }
    private void pushDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No Internet connection!")
                .setMessage("Opps...Retry again")
                .setCancelable(false)
                .setIcon(R.drawable.ic_baseline_network_check_24)
                .setPositiveButton("ОК", (dialog, id) -> {
                    checkInternet();
                });
        builder.setNegativeButton("No", (dialog, id) -> {
            finish();
        });
        builder.show();
        }

    void checkInternet(){
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                    connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
                //we are connected to a network

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this, SearchActivity.class));
                        finish();
                    }
                }, 2000);
            } else{
                pushDialog();
            }
        }

}
