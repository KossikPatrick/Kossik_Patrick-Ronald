package com.mindtoheart.licenta.meditatie;

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

public class Meditatie extends AppCompatActivity {

    ListView listView;
    Animation animation;
    String[] title1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditatie);
        getSupportActionBar().setTitle("Meditație ghidată");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#006600"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listView=findViewById(R.id.list_view);
        title1=getResources().getStringArray(R.array.title1);
        MeditatiiAdapter adapter=new MeditatiiAdapter(Meditatie.this, title1);
        animation= AnimationUtils.loadAnimation(this,R.anim.animation1);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position==0){
                    Intent intent= new Intent(Meditatie.this, Meditatie1.class);
                    startActivity(intent);
                }
                else if(position==1){
                    Intent intent= new Intent(Meditatie.this, Meditatie2.class);
                    startActivity(intent);
                }
                else if(position==2){
                    Intent intent= new Intent(Meditatie.this, Meditatie3.class);
                    startActivity(intent);
                }
                else if(position==3){
                    Intent intent= new Intent(Meditatie.this, Meditatie4.class);
                    startActivity(intent);
                }
                else if(position==4){
                    Intent intent= new Intent(Meditatie.this, Meditatie5.class);
                    startActivity(intent);
                }
                else if(position==5){
                    Intent intent= new Intent(Meditatie.this, Meditatie6.class);
                    startActivity(intent);
                }
                else if(position==6){
                    Intent intent= new Intent(Meditatie.this, Meditatie7.class);
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