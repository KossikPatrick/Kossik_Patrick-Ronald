package com.mindtoheart.licenta.muzica;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.mindtoheart.licenta.R;

public class Muzica extends AppCompatActivity {

    ListView listView;
    Animation animation;
    String[] title2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_muzica);
        getSupportActionBar().setTitle("Muzică terapeutică");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#006600"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView=findViewById(R.id.list_view);
        title2=getResources().getStringArray(R.array.title2);
        MuzicaAdapter adapter=new MuzicaAdapter(Muzica.this, title2);
        animation= AnimationUtils.loadAnimation(this,R.anim.animation1);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent intent= new Intent(Muzica.this, Muzica1.class);
                    startActivity(intent);
                }
                else if(position==1){
                    Intent intent= new Intent(Muzica.this, Muzica2.class);
                    startActivity(intent);
                }
                else if(position==2){
                    Intent intent= new Intent(Muzica.this, Muzica3.class);
                    startActivity(intent);
                }
                else if(position==3){
                    Intent intent= new Intent(Muzica.this, Muzica4.class);
                    startActivity(intent);
                }
                else if(position==4){
                    Intent intent= new Intent(Muzica.this, Muzica5.class);
                    startActivity(intent);
                }
                else if(position==5){
                    Intent intent= new Intent(Muzica.this, Muzica6.class);
                    startActivity(intent);
                }
                else if(position==6){
                    Intent intent= new Intent(Muzica.this, Muzica7.class);
                    startActivity(intent);
                }
                else if(position==7){
                    Intent intent= new Intent(Muzica.this, Muzica8.class);
                    startActivity(intent);
                }
                else if(position==8){
                    Intent intent= new Intent(Muzica.this, Muzica9.class);
                    startActivity(intent);
                }
                else if(position==9){
                    Intent intent= new Intent(Muzica.this, Muzica10.class);
                    startActivity(intent);
                }

            }
        });
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}