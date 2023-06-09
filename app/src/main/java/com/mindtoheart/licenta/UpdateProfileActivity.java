package com.mindtoheart.licenta;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class UpdateProfileActivity extends AppCompatActivity {
    BottomNavigationView nav;
    private EditText editTextUpdateName, editTextUpdateDoB;
    private RadioGroup radioGroupUpdateGender;
    private RadioButton radioButtonUpdateGenderSelected;
    private String textFullName, textDoB, textGender;
    private FirebaseAuth authProfile;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Actualizați profilul");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#006600"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar=findViewById(R.id.progressBar);
        editTextUpdateName=findViewById(R.id.editText_update_profile_name);
        editTextUpdateDoB=findViewById(R.id.editText_update_profile_dob);

         radioGroupUpdateGender=findViewById(R.id.radio_group_update_gender);
         authProfile=FirebaseAuth.getInstance();
        FirebaseUser firebaseUser= authProfile.getCurrentUser();
        //show profile data
        assert firebaseUser != null;
        showProfile(firebaseUser);

        // upload profile pic
        Button buttonUploadProfilePic= findViewById(R.id.button_upload_profile_pic);
        buttonUploadProfilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, UploadProfilePicture.class);
                startActivity(intent);
                finish();
            }
        });
        // update email
       Button buttonUpdateEmail= findViewById(R.id.button_profile_update_email );
        buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class);
                startActivity(intent);
                finish();
            }
        });
        // setting up date picker
        editTextUpdateDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // extracting saved dd, mm, yyyy, into different variables by creating an array
                String[] textSADoB = textDoB.split("/");
                int day= Integer.parseInt(textSADoB[0]);
                int month=Integer.parseInt(textSADoB[1]) -1 ;
                int year=Integer.parseInt(textSADoB[2]);


                DatePickerDialog picker;

                // date picker dialog
                picker= new DatePickerDialog(UpdateProfileActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        editTextUpdateDoB.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                }, year, month, day);
                picker.show();
            }
        });
        // update Profile
        Button buttonUpdateProfile= findViewById(R.id.button_update_profile);
        buttonUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(firebaseUser);
            }
        });
        nav=findViewById(R.id.bottomNavigationMenu);
        nav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId())
                {
                    case R.id.menuHome:
                        Intent intent=new Intent(UpdateProfileActivity.this, MenuPrincipal.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                        startActivity(intent);

                        finish();
                        break;

                    case R.id.menuProfil:
                        Intent intent1=new Intent(UpdateProfileActivity.this, UserProfileActivity.class);
                        intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );

                        startActivity(intent1);
                        finish();
                        break;

                    case R.id.menuSetari:
                        Intent intent2=new Intent(UpdateProfileActivity.this, UpdateProfileActivity.class);
                        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );

                        startActivity(intent2);
                        finish();
                        break;

                    case R.id.menuLogout:
                        authProfile.signOut();
                        Toast.makeText(UpdateProfileActivity.this, "V-ați delogat cu succces!", Toast.LENGTH_SHORT).show();
                        Intent intent3=new Intent(UpdateProfileActivity.this, MainActivity.class );
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
    // update profile
    private void updateProfile(FirebaseUser firebaseUser) {
        int selectedGenderID= radioGroupUpdateGender.getCheckedRadioButtonId();
        radioButtonUpdateGenderSelected= findViewById(selectedGenderID);
        textFullName=editTextUpdateName.getText().toString();
        textGender=radioButtonUpdateGenderSelected.getText().toString();

        textDoB=editTextUpdateDoB.getText().toString();
        if(TextUtils.isEmpty(textFullName))
        {
            editTextUpdateName.setError("Câmpul nu poate fi gol");
            editTextUpdateName.requestFocus();

        }
        else if(!textFullName.matches("[a-zA-Z\\s-]+"))
        {
            editTextUpdateName.requestFocus();
            editTextUpdateName.setError("Introduceți numai caractere alfabetice, excepție făcând:-");
        } else if (TextUtils.isEmpty(textDoB)){
            Toast.makeText(UpdateProfileActivity.this, "Vă rugăm introduceți data nașterii", Toast.LENGTH_LONG).show();
            editTextUpdateDoB.setError("Data nașterii este necesară");
            editTextUpdateDoB.requestFocus();
        } else if(TextUtils.isEmpty(radioButtonUpdateGenderSelected.getText())){
            Toast.makeText(UpdateProfileActivity.this, "Vă rugăm selectați genul", Toast.LENGTH_LONG).show();
            radioButtonUpdateGenderSelected.setError("Genul este necesar");
            radioButtonUpdateGenderSelected.requestFocus();
        } else {

            // introducem in baza de date ce a schimbat utilizatorul
            ReadWriteUserDetails writeUserDetails= new ReadWriteUserDetails(textFullName, textDoB, textGender);
            DatabaseReference referenceProfile=FirebaseDatabase.getInstance().getReference("Registered Users");
            String userID= firebaseUser.getUid();
            progressBar.setVisibility(View.VISIBLE);
            referenceProfile.child(userID).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                   if(task.isSuccessful()){
                       // seta noul nume
                       UserProfileChangeRequest profileUpdate= new UserProfileChangeRequest.Builder().
                               setDisplayName(textFullName).build();
                       firebaseUser.updateProfile(profileUpdate);
                       Toast.makeText(UpdateProfileActivity.this, "Actualizare reusită", Toast.LENGTH_SHORT).show();

                       Intent intent= new Intent(UpdateProfileActivity.this, MenuPrincipal.class);
                       intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                       startActivity(intent);
                       finish();
                   } else {
                       try{
                           throw Objects.requireNonNull(task.getException());
                       } catch (Exception e){
                           Toast.makeText(UpdateProfileActivity.this,"Actualizare nereusită" , Toast.LENGTH_LONG).show();
                       }
                   }
                   progressBar.setVisibility(View.GONE);
                }
            });

        }

    }

    //fetch data from database
    private void showProfile(FirebaseUser firebaseUser) {
        String userIDofRegistered= firebaseUser.getUid();
        //extracting user reference from database
        DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
        progressBar.setVisibility(View.VISIBLE);
        referenceProfile.child(userIDofRegistered).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
               ReadWriteUserDetails readUserDetails= snapshot.getValue(ReadWriteUserDetails.class);
               if(readUserDetails != null ){
                   textFullName=readUserDetails.fullName;
                   textDoB=readUserDetails.doB;
                   textGender=readUserDetails.gender;

                   editTextUpdateName.setText(textFullName);
                   editTextUpdateDoB.setText(textDoB);

                   // show gender radio button
                   if(textGender.equals("Masculin")){
                       radioButtonUpdateGenderSelected=findViewById(R.id.radio_male);
                   } else if(textGender.equals("Feminin")){
                       radioButtonUpdateGenderSelected=findViewById(R.id.radio_female);
                   } else {
                       radioButtonUpdateGenderSelected=findViewById(R.id.radio_other);
                   }
                   radioButtonUpdateGenderSelected.setChecked(true);
               } else {
                   Toast.makeText(UpdateProfileActivity.this, "Ceva nu a funcționat corect!", Toast.LENGTH_SHORT).show();
               }
               progressBar.setVisibility(View.GONE);
               
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(UpdateProfileActivity.this, "Ceva nu a funcționat corect!", Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.GONE);


            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate menu item
        getMenuInflater().inflate(R.menu.common_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    // when any menu item is selected
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if(id==android.R.id.home){
            NavUtils.navigateUpFromSameTask(UpdateProfileActivity.this);
        }
        else
        if(id==R.id.menu_refresh){
            //Refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        } else if(id==R.id.menu_update_profile){
            Intent intent=new Intent(UpdateProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }  else if(id==R.id.menu_update_email){
            Intent intent=new Intent(UpdateProfileActivity.this, UpdateEmailActivity.class );
            startActivity(intent);
            finish();
        } else if(id==R.id.menu_change_password){
            Intent intent=new Intent(UpdateProfileActivity.this, ChangePasswordActivity.class );
            startActivity(intent);
            finish();
        } else if (id==R.id. menu_delete_profile){
            Intent intent=new Intent(UpdateProfileActivity.this, DeleteProfileActivity.class );
            startActivity(intent);
            finish();
        } else if (id==R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(UpdateProfileActivity.this, "V-ați delogat cu succces!", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(UpdateProfileActivity.this, MainActivity.class );
            // clear stack to prevent user coming back after logout
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(UpdateProfileActivity.this, "Ceva nu a funcționat corect!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}