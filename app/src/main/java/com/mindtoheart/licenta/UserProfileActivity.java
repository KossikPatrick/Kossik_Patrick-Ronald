package com.mindtoheart.licenta;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.Objects;

public class UserProfileActivity extends AppCompatActivity {
    BottomNavigationView nav;
    private TextView textViewWelcome, textViewFullName, textViewEmail, textViewDoB, textViewGender;
    private ProgressBar progressBar;
    private String fullName, email, doB, gender;
    private ImageView imageView;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Profilul dumneavoastră");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#006600"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        textViewWelcome=findViewById(R.id.textView_show_welcome);
        textViewFullName=findViewById(R.id.textView_show_full_name);
        textViewEmail=findViewById(R.id.textView_show_email);
        textViewDoB=findViewById(R.id.textView_show_dob);
        textViewGender=findViewById(R.id.textView_show_gender);
        progressBar=findViewById(R.id.progressBar);
        //set OnClickListener imageview to open uploadprofileactivity
        imageView=findViewById(R.id.imageView_profile_db);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(UserProfileActivity.this, UploadProfilePicture.class);
                startActivity(intent);
            }
        });
        
        authProfile= FirebaseAuth.getInstance();
        FirebaseUser firebaseUser= authProfile.getCurrentUser();
        if(firebaseUser==null){
            Toast.makeText(UserProfileActivity.this, "Ceva nu a funcționat corect.", Toast.LENGTH_SHORT).show();
        }else {
 //           checkIfEmailVerified(firebaseUser);
            progressBar.setVisibility(View.VISIBLE);
            showUserProfile(firebaseUser);
        }
        nav=findViewById(R.id.bottomNavigationMenu);
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.menuHome:
                        Intent intent=new Intent(UserProfileActivity.this, MenuPrincipal.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );

                        startActivity(intent);
                        finish();
                        break;

                    case R.id.menuProfil:
                        Intent intent1=new Intent(UserProfileActivity.this, UserProfileActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                        startActivity(intent1);
                        finish();

                        break;

                    case R.id.menuSetari:
                        Intent intent2=new Intent(UserProfileActivity.this, UpdateProfileActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                        startActivity(intent2);
                        finish();

                        break;

                    case R.id.menuLogout:
                        authProfile.signOut();
                        Toast.makeText(UserProfileActivity.this, "V-ați delogat cu succces!", Toast.LENGTH_SHORT).show();
                        Intent intent3=new Intent(UserProfileActivity.this, MainActivity.class );
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
    }

    private void showUserProfile(FirebaseUser firebaseUser) {
        String userID= firebaseUser.getUid();
        // Extracting user reference from database for "Registered user"
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users" );
        referenceProfile.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ReadWriteUserDetails readUserDetails= snapshot.getValue(ReadWriteUserDetails.class);
                if(readUserDetails != null){
                    fullName=firebaseUser.getDisplayName();
                    email=firebaseUser.getEmail();
                    doB=readUserDetails.doB;
                    gender=readUserDetails.gender;
                    textViewWelcome.setText("Bine ați venit, "+ fullName + "!");
                    textViewFullName.setText(fullName);
                    textViewEmail.setText(email);
                    textViewDoB.setText(doB);
                    textViewGender.setText(gender);
                    // set user DP ( after user has uploaded)
                    Uri uri= firebaseUser.getPhotoUrl();
                    //ImageView setImageURI() should not be used with regular uris. so we are using picasso

                    Picasso.with(UserProfileActivity.this).load(uri).transform(new RoundedCornersTransform()).into(imageView);

                } else {
                    Toast.makeText(UserProfileActivity.this, "Ceva nu a funcționat corect.", Toast.LENGTH_SHORT).show();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(UserProfileActivity.this, "Ceva nu a funcționat corect.", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}