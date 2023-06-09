package com.mindtoheart.licenta.anxietate;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mindtoheart.licenta.R;

public class Anxietate2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anxietate2);
        getSupportActionBar().setTitle("Fiziopatologia tulburărilor anxioase");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#006600"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}