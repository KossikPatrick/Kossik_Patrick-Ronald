package com.mindtoheart.licenta;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.mindtoheart.licenta.anxietate.Informatii;
import com.mindtoheart.licenta.chat.ChatGroup;
import com.mindtoheart.licenta.jurnal.Jurnal;
import com.mindtoheart.licenta.meditatie.Meditatie;
import com.mindtoheart.licenta.muzica.Muzica;


public class MenuPrincipal extends AppCompatActivity {
    BottomNavigationView nav;

    TextView textName;
    ImageView imageViewInformatii, imageViewMeditatie, imageViewSunete, imageViewJurnal, imageViewChat;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        getSupportActionBar().setTitle("Pagina principală");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#006600"));
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        textName = findViewById(R.id.textName);
        imageViewInformatii = findViewById(R.id.imageViewInformatii);
        imageViewMeditatie = findViewById(R.id.imageViewMeditatie);
        imageViewSunete = findViewById(R.id.imageViewSunete);
        imageViewChat = findViewById(R.id.imageViewChat);
        imageViewJurnal = findViewById(R.id.imageViewJurnal);

        nav = findViewById(R.id.bottomNavigationMenu);
        authProfile = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = authProfile.getCurrentUser();
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()) {
                    case R.id.menuHome:
                        Intent intent = new Intent(MenuPrincipal.this, MenuPrincipal.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                        startActivity(intent);
                        finish();
                        break;

                    case R.id.menuProfil:
                        Intent intent1 = new Intent(MenuPrincipal.this, UserProfileActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent1);


                        break;

                    case R.id.menuSetari:
                        Intent intent2 = new Intent(MenuPrincipal.this, UpdateProfileActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent2);

                        break;

                    case R.id.menuLogout:
                        authProfile.signOut();
                        Toast.makeText(MenuPrincipal.this, "V-ați delogat cu succces!", Toast.LENGTH_SHORT).show();
                        Intent intent3 = new Intent(MenuPrincipal.this, MainActivity.class);
                        // clear stack to prevent user coming back after logout
                        intent3.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent3);
                        finish();

                        break;
                    default:
                }
                return true;
            }
        });


        imageViewInformatii.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, Informatii.class);
                startActivity(intent);
            }
        });
        imageViewMeditatie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, Meditatie.class);
                startActivity(intent);
            }
        });
        imageViewSunete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, Muzica.class);
                startActivity(intent);
            }
        });
        imageViewJurnal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, Jurnal.class);
                startActivity(intent);
            }
        });
        imageViewChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuPrincipal.this, ChatGroup.class);
                startActivity(intent);
            }
        });

    }
}
