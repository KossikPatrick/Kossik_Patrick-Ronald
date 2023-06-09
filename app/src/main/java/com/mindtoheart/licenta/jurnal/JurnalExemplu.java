package com.mindtoheart.licenta.jurnal;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.mindtoheart.licenta.R;

import java.util.Objects;

public class JurnalExemplu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jurnal_exemplu);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Exemplu jurnal scris");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#006600"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}