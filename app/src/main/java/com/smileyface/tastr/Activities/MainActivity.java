package com.smileyface.Tastr.Activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.smileyface.Tastr.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Setup buttons for the main menu
        Button tActivityButton = (Button) findViewById(R.id.touchViewButton);
        Button sActivityButton = (Button) findViewById(R.id.settingsViewButton);

        tActivityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TouchActivity.class));
            }
        });

//
    }//On Create

}//MainActivity Class
