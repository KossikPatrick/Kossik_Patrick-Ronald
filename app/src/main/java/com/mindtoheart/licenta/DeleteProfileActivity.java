package com.mindtoheart.licenta;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

public class DeleteProfileActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private EditText editTextUserPwd;
    private TextView textViewAuthenticated;
    private ProgressBar progressBar;
    private String userPwd;
    private Button buttonReAuthenticate, buttonDeleteUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_profile);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Ștergeți profilul dvs.");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#006600"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar=findViewById(R.id.progressBar);
        editTextUserPwd=findViewById(R.id.editText_delete_user_pwd);
        textViewAuthenticated=findViewById(R.id.textView_delete_user_authenticated);
        buttonDeleteUser=findViewById(R.id.button_delete_user);
        buttonReAuthenticate=findViewById(R.id.button_delete_user_authenticate);
        
        // disable delete button until authenticated
        buttonDeleteUser.setEnabled(false);
        authProfile= FirebaseAuth.getInstance();
        firebaseUser=authProfile.getCurrentUser();
        if(firebaseUser.equals("")) {
            Toast.makeText(DeleteProfileActivity.this, "Ceva nu a funcționat  corect", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(DeleteProfileActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        } else {
            reAuthenticateUser(firebaseUser);
        }
        // Show hide password using eye icon
        ImageView imageViewShowHidePwd=findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextUserPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    // if pwd is visible then hide it
                    editTextUserPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    editTextUserPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });
    }

    // ReAuthenticate user before changing password
    private void reAuthenticateUser(FirebaseUser firebaseUser) {
        buttonReAuthenticate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPwd=editTextUserPwd.getText().toString();
                if(TextUtils.isEmpty(userPwd)){
                    Toast.makeText(DeleteProfileActivity.this, "Parola este necesară", Toast.LENGTH_SHORT).show();
                    editTextUserPwd.setError("Vă rugăm introduceți parola actuală pentru autentificare");
                    editTextUserPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    // ReAuthenticate credential
                    AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwd);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                // disable editText for current pwd
                                editTextUserPwd.setEnabled(false);

                                // enable delete pwd button
                                buttonDeleteUser.setEnabled(true);
                                buttonReAuthenticate.setEnabled(false);
                                // set textview to show user is authenticated/verified
                                textViewAuthenticated.setText("Sunteți autentificat/verificat! Puteți șterge profilul acum!");
                                Toast.makeText(DeleteProfileActivity.this, "Parola a fost verificată" + " Puteți schimba parola acum", Toast.LENGTH_SHORT).show();
                                // change password button


                                buttonDeleteUser.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                       showAlertDialog();
                                    }
                                });
                            } else {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (Exception e) {

                                    editTextUserPwd.setError("Parola este incorectă");
                                    editTextUserPwd.requestFocus();
                                }
                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });

    }

    private void showAlertDialog() {
        // alertă
        AlertDialog.Builder builder=new AlertDialog.Builder(DeleteProfileActivity.this);
        builder.setTitle("Ștergeți utilizatorul?");
        builder.setMessage("Doriți să vă ștergeți profilul și datele aferente? Această acțiune este ireversibilă!");
        // în caz de stergere se apelează metoda deleteUserData
        builder.setPositiveButton("Continuă", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                deleteUserData(firebaseUser);
            }
        });
        // returnează utilizatorul către profil, în caz ca anulează
        builder.setNegativeButton("Anulare", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
             Intent intent=new Intent(DeleteProfileActivity.this, UserProfileActivity.class);
              startActivity(intent);
              finish();
            }
        });
        // se creaza alerta
        AlertDialog alertDialog= builder.create();
        // se schimbă culoarea butonului de continuă
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.red));
            }
        });
        // se afizează alerta
        alertDialog.show();
    }

    private void deleteUser() {
        firebaseUser.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    authProfile.signOut();
                    Toast.makeText(DeleteProfileActivity.this, "Utilizatorul a fost șters", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(DeleteProfileActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {

                    }
                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }
// stergerea tuturor datelor din baza de date
    private void deleteUserData(FirebaseUser firebaseUser) {
        // stergerea pozei de profil
        if(firebaseUser.getPhotoUrl() !=null) {
            FirebaseStorage firebaseStorage= FirebaseStorage.getInstance();
            StorageReference storageReference=firebaseStorage.getReferenceFromUrl(firebaseUser.getPhotoUrl().toString());
            storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                }
            });
        }

        // stergere data din realtime database
        DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference("Registered Users" );
        databaseReference.child(firebaseUser.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                // stergere user
                deleteUser();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    // creating actionbar menu
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
            NavUtils.navigateUpFromSameTask(DeleteProfileActivity.this);
        }
        else if(id==R.id.menu_refresh){
            //Refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        }else if(id==R.id.menu_update_profile){
            Intent intent=new Intent(DeleteProfileActivity.this, UpdateProfileActivity.class);
            startActivity(intent);

        }  else if(id==R.id.menu_update_email){
            Intent intent=new Intent(DeleteProfileActivity.this, UpdateEmailActivity.class );
            startActivity(intent);

        } else if(id==R.id.menu_change_password){
            Intent intent=new Intent(DeleteProfileActivity.this, ChangePasswordActivity.class );
            startActivity(intent);

        } else if (id==R.id. menu_delete_profile){
            Intent intent=new Intent(DeleteProfileActivity.this, DeleteProfileActivity.class );
            startActivity(intent);
        } else if (id==R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(DeleteProfileActivity.this, "V-ați delogat cu succces!", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(DeleteProfileActivity.this, MainActivity.class );
            // clear stack to prevent user coming back after logout
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(DeleteProfileActivity.this, "Ceva nu a funcționat corect!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}