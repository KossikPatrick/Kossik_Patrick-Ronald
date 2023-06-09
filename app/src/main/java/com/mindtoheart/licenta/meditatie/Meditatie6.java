package com.mindtoheart.licenta.meditatie;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mindtoheart.licenta.R;

import java.util.concurrent.TimeUnit;

public class Meditatie6 extends AppCompatActivity {
    Button play_btn, back_btn, forward_btn, stop_btn;
    TextView time_txt;
    SeekBar seekBar;

    MediaPlayer mediaPlayer;
    Handler handler= new Handler();
    double startTime=0;
    double finalTime=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meditatie6);
        getSupportActionBar().setTitle("Vindecarea anxietății");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#006600"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        play_btn=findViewById(R.id.playButton);
        back_btn=findViewById(R.id.rewindButton);
        forward_btn=findViewById(R.id.forwardButton);
        stop_btn=findViewById(R.id.pauseButton);
        time_txt=findViewById(R.id.textView2);

        seekBar=findViewById(R.id.seekBar);
        TextView yttext=findViewById(R.id.textYoutube);
        String youtubeLink = "https://youtu.be/KIMLzAlz9iM";
        yttext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(youtubeLink));
                startActivity(intent);
                finish();
            }
        });

        mediaPlayer= MediaPlayer.create(this, R.raw.vindecare );

        seekBar.setClickable(false);
        // adding functionalities for the buttons

        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayMusic();
            }

            private void PlayMusic() {
                mediaPlayer.start();
                finalTime=mediaPlayer.getDuration();
                startTime=mediaPlayer.getCurrentPosition();
                seekBar.setMax((int) finalTime);
                time_txt.setText(String.format(
                        "%d min, %d sec", TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime)-
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long)finalTime))
                ));
                seekBar.setProgress((int) startTime);
                handler.postDelayed(UpdateSongTime, 100);
            }
            //creating runnable
            private Runnable UpdateSongTime=new Runnable() {
                @Override
                public void run() {
                    startTime=mediaPlayer.getCurrentPosition();
                    time_txt.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime)
                                    -TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime))));

                    seekBar.setProgress((int) startTime);
                    handler.postDelayed(this, 100);

                }
            };
        });

        stop_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.pause();
            }
        });
        forward_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int newPosition = currentPosition + 5000; // 5 seconds
                if (newPosition > mediaPlayer.getDuration()) {
                    newPosition = mediaPlayer.getDuration();
                }
                mediaPlayer.seekTo(newPosition);
            }
        });
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = mediaPlayer.getCurrentPosition();
                int newPosition = currentPosition - 5000; // 5 seconds
                if (newPosition < 0) {
                    newPosition = 0;
                }
                mediaPlayer.seekTo(newPosition);
            }
        });

    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }
}
