package com.mindtoheart.licenta.anxietate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.mindtoheart.licenta.R;

public class InformatiiAdapter extends BaseAdapter {
    Informatii informatii;
    String[] title;
    Animation animation;

    public InformatiiAdapter(Informatii informatii, String[] title) {
        this.informatii = informatii;
        this.title = title;
    }
    public static int getRandom(int max)
    {
        return (int )(Math.random()*max);
    }
    @Override
    public int getCount() {
        return title.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView= LayoutInflater.from(informatii).inflate(R.layout.new_item_layout, parent, false);
        animation= AnimationUtils.loadAnimation(informatii, R.anim.animation1);
        TextView textView;
        LinearLayout ll_bg;
        ll_bg=convertView.findViewById(R.id.ll_bg);
        textView=convertView.findViewById(R.id.textView);
        int number=getRandom(8);
        if(number==1)
        {
            ll_bg.setBackground(ContextCompat.getDrawable(informatii,R.drawable.gradient_1));
        } else if(number==2){
            ll_bg.setBackground(ContextCompat.getDrawable(informatii,R.drawable.gradient_2));
        } else if(number==3){
            ll_bg.setBackground(ContextCompat.getDrawable(informatii,R.drawable.gradient_3));
        } else if(number==4){
            ll_bg.setBackground(ContextCompat.getDrawable(informatii,R.drawable.gradient_4));
        } else if(number==5){
            ll_bg.setBackground(ContextCompat.getDrawable(informatii,R.drawable.gradient_5));
        } else if(number==6){
            ll_bg.setBackground(ContextCompat.getDrawable(informatii,R.drawable.gradient_6));
        } else if(number==7){
            ll_bg.setBackground(ContextCompat.getDrawable(informatii,R.drawable.gradient_7));
        } else if(number==8){
            ll_bg.setBackground(ContextCompat.getDrawable(informatii,R.drawable.gradient_8));
        } else {
            ll_bg.setBackground(ContextCompat.getDrawable(informatii,R.drawable.gradient_1));
        }

        textView.setText(title[position]);
        textView.setAnimation(animation);
        return convertView;
    }
}
