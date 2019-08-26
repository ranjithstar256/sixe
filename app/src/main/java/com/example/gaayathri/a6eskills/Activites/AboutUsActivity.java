package com.example.gaayathri.a6eskills.Activites;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.example.gaayathri.a6eskills.R;

public class AboutUsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        Window window = AboutUsActivity.this.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(AboutUsActivity.this, R.color.colorPrimary));

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("About us");


        SharedPreferences sharedpreferences;

        sharedpreferences = this.getSharedPreferences("mypref", 0); // 0 - for private mode

        String apikey = sharedpreferences.getString("secretkey", "");

        Log.i("aaapikey",apikey);

        /*Button back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent aboutusintent = new Intent(AboutUsActivity.this, LoginActivity.class);
                startActivity(aboutusintent);
                overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_left);
            }
        });*/
    }
}
