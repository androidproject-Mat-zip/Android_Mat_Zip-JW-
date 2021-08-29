package com.MatZip.map;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


public class LoadingActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(500); //loading page is shown
            //splash display waiting time
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        startActivity(new Intent(this, MapActivity.class));
        //shows map activity(map page) after splash display is shown

        finish();
    }
}