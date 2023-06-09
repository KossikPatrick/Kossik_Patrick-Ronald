package com.mindtoheart.licenta;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class UpdateEmailActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private FirebaseUser firebaseUser;
    private ProgressBar progressBar;
    private TextView textViewAuthenticated;
    private String userOldEmail, userNewEmail,userPwd;
    private Button buttonUpdateEmail;
    private EditText editTextNewEmail, editTextPwd;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_email);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Actualizați e-mailul");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#006600"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar=findViewById(R.id.progressBar);
        editTextPwd=findViewById(R.id.editText_update_email_verify_password);
        editTextNewEmail=findViewById(R.id.editText_update_email_new);
        textViewAuthenticated=findViewById(R.id.textView_update_email_authenticated);
        buttonUpdateEmail=findViewById(R.id.button_update_email);
        buttonUpdateEmail.setEnabled(false); // disable button
        editTextNewEmail.setEnabled(false);
        
        authProfile=FirebaseAuth.getInstance();
        firebaseUser= authProfile.getCurrentUser();
        // set old email id on textview
        userOldEmail=firebaseUser.getEmail();
        TextView textViewOldEmail= findViewById(R.id.textView_update_email_old);
        textViewOldEmail.setText(userOldEmail);
        if(firebaseUser.equals("")){
            Toast.makeText(UpdateEmailActivity.this, "Ceva nu a funcționat! Detaliile utilizatorului nu sunt disponibile.", Toast.LENGTH_SHORT).show();
        } else {
            reAuthenticate(firebaseUser);
        }
        // Show hide password using eye icon
        ImageView imageViewShowHidePwd=findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    // if pwd is visible then hide it
                    editTextPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    editTextPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });


    }
    //reAuthenticate user before updating email
    private void reAuthenticate(FirebaseUser firebaseUser) {
        Button buttonVerifyUser= findViewById(R.id.button_authenticate_user);
        buttonVerifyUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // obtain password for authentication
                userPwd=editTextPwd.getText().toString();
                if(TextUtils.isEmpty(userPwd)){
                    Toast.makeText(UpdateEmailActivity.this, "Parola este necesară pentru a continua", Toast.LENGTH_SHORT).show();
                    editTextPwd.setError("Va rugăm introduceți parola");
                    editTextPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    AuthCredential credential= EmailAuthProvider.getCredential(userOldEmail, userPwd);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               progressBar.setVisibility(View.GONE);
                               Toast.makeText(UpdateEmailActivity.this, "Parola a fost verificată.", Toast.LENGTH_SHORT).show();
                               textViewAuthenticated.setText("Sunteți autentificat. Va puteți actualiza e-mailul acum.");
                               editTextNewEmail.setEnabled(true);
                               editTextPwd.setEnabled(false);
                               buttonVerifyUser.setEnabled(false);
                               buttonUpdateEmail.setEnabled(true);
                               // change color of update email button

                               buttonUpdateEmail.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       userNewEmail= editTextNewEmail.getText().toString();
                                       if(TextUtils.isEmpty(userNewEmail)){
                                           Toast.makeText(UpdateEmailActivity.this, "Este necesar un nou e-mail", Toast.LENGTH_SHORT).show();
                                           editTextNewEmail.setError("Va rugăm introduceți noul e-mail");
                                           editTextNewEmail.requestFocus();
                                       } else if (!Patterns.EMAIL_ADDRESS.matcher(userNewEmail).matches()){
                                           Toast.makeText(UpdateEmailActivity.this, "Va rugăm introduceți un e-mail valid", Toast.LENGTH_SHORT).show();
                                           editTextNewEmail.setError("Vă rugăm să furnizați un e-mail valid");
                                           editTextNewEmail.requestFocus();
                                       } else if(userOldEmail.matches(userNewEmail)){
                                           Toast.makeText(UpdateEmailActivity.this, "Noul e-mail nu poate fi același cu cel vechi", Toast.LENGTH_SHORT).show();
                                           editTextNewEmail.setError("Va rugăm introduceți un alt e-mail");
                                           editTextNewEmail.requestFocus();
                                       } else {
                                           progressBar.setVisibility(View.GONE);
                                           updateEmail(firebaseUser);
                                       }
                                   }
                               }); 
                           }else {
                               try {
                                   throw Objects.requireNonNull(task.getException());
                               } catch (Exception e){
                                   Toast.makeText(UpdateEmailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                               }
                               
                           }
                        }
                    });
                }

            }

        });
        
    }

    private void updateEmail(FirebaseUser firebaseUser) {
        firebaseUser.updateEmail(userNewEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isComplete()){
                    firebaseUser.sendEmailVerification();
                    Toast.makeText(UpdateEmailActivity.this,
                            "E-mailul a fost actualizat. Va rugăm verificați-vă noul e-mail.", Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(UpdateEmailActivity.this, MenuPrincipal.class);
                    startActivity(intent);
                    finish();
                } else {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (Exception e) {
                        Toast.makeText(UpdateEmailActivity.this, "E-mailul nu a fost actualizat.", Toast.LENGTH_SHORT).show();
                    }
                }
                progressBar.setVisibility(View.GONE);
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
            NavUtils.navigateUpFromSameTask(UpdateEmailActivity.this);
        }
        else if(id==R.id.menu_refresh){
            //Refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        } else if(id==R.id.menu_update_profile){
            Intent intent=new Intent(UpdateEmailActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }  else if(id==R.id.menu_update_email){
            Intent intent=new Intent(UpdateEmailActivity.this, UpdateEmailActivity.class );
            startActivity(intent);
            finish();
        }  else if(id==R.id.menu_change_password){
            Intent intent=new Intent(UpdateEmailActivity.this, ChangePasswordActivity.class );
            startActivity(intent);
            finish();
        } else if (id==R.id. menu_delete_profile){
            Intent intent=new Intent(UpdateEmailActivity.this, DeleteProfileActivity.class );
            startActivity(intent);
            finish();
        }else if (id==R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(UpdateEmailActivity.this, "V-ați delogat cu succces!", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(UpdateEmailActivity.this, MainActivity.class );
            // clear stack to prevent user coming back after logout
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(UpdateEmailActivity.this, "Ceva nu a funcționat corect!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}