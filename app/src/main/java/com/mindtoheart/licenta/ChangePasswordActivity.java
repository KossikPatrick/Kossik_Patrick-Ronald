package com.mindtoheart.licenta;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
import java.util.regex.Pattern;

public class ChangePasswordActivity extends AppCompatActivity {
    private FirebaseAuth authProfile;
    private EditText editTextPwdCurr, editTextPwdNew, editTextPwdConfirmNew;
    private TextView textViewAuthenticated;
    private Button buttonChangePwd, buttonReAuthenticate;
    private ProgressBar progressBar;
    private String userPwdCurr;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[A-Z])" +     // at least 1 upper case
                    "(?=\\S+$)" +            // no white spaces
                    ".{4,}" +                // at least 4 characters
                    "$");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Schimbaţi parola");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#006600"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editTextPwdNew=findViewById(R.id.editText_change_pwd_new);
        editTextPwdCurr=findViewById(R.id.editText_change_pwd_current);
        editTextPwdConfirmNew=findViewById(R.id.editText_change_pwd_new_confirm);
        textViewAuthenticated=findViewById(R.id.textView_change_pwd_authenticated);
        progressBar=findViewById(R.id.progressBar);
        buttonReAuthenticate=findViewById(R.id.button_change_pwd_authenticate);
        buttonChangePwd=findViewById(R.id.button_change_pwd);
        // disable edit text for new password, confirm password and button
        editTextPwdNew.setEnabled(false);
        editTextPwdConfirmNew.setEnabled(false);
        buttonChangePwd.setEnabled(false);
        authProfile= FirebaseAuth.getInstance();
        FirebaseUser firebaseUser=authProfile.getCurrentUser();
        if(firebaseUser.equals("")){
            Toast.makeText(ChangePasswordActivity.this, "Ceva nu a funcționat", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(ChangePasswordActivity.this, UserProfileActivity.class);
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
                if(editTextPwdCurr.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    // if pwd is visible then hide it
                    editTextPwdCurr.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    editTextPwdCurr.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
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
                userPwdCurr=editTextPwdCurr.getText().toString();
                if(TextUtils.isEmpty(userPwdCurr)){
                    Toast.makeText(ChangePasswordActivity.this, "Parola este necesară", Toast.LENGTH_SHORT).show();
                    editTextPwdCurr.setError("Vă rugăm introduceți parola actuală pentru autentificare");
                    editTextPwdCurr.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    // ReAuthenticate credential
                    AuthCredential credential= EmailAuthProvider.getCredential(firebaseUser.getEmail(), userPwdCurr);
                    firebaseUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                           if(task.isSuccessful()){
                               progressBar.setVisibility(View.GONE);
                               // disable editText for current pwd , enable editText for new pwd and confirm pwd
                               editTextPwdCurr.setEnabled(false);
                               editTextPwdNew.setEnabled(true);
                               editTextPwdConfirmNew.setEnabled(true);
                               // disable authenticate button, enable change pwd button
                               buttonChangePwd.setEnabled(true);
                               buttonReAuthenticate.setEnabled(false);
                               // set textview to show user is authenticated/verified
                               textViewAuthenticated.setText("Sunteți autentificat/verificat. Puteți schimba parola acum!");
                               Toast.makeText(ChangePasswordActivity.this, "Parola a fost verificată" + " Puteți schimba parola acum", Toast.LENGTH_SHORT).show();
                               // change password button 

                               
                               buttonChangePwd.setOnClickListener(new View.OnClickListener() {
                                   @Override
                                   public void onClick(View v) {
                                       changePwd(firebaseUser);
                                   }
                               });

                           } else {
                               try {
                                   throw Objects.requireNonNull(task.getException());
                               } catch (Exception e) {

                                   editTextPwdCurr.setError("Parola este incorecta");
                                   editTextPwdCurr.requestFocus();
                               }
                           }
                           progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
        
    }

    private void changePwd(FirebaseUser firebaseUser) {
        String userPwdNew = editTextPwdNew.getText().toString();
        String userPwdConfirmNew= editTextPwdConfirmNew.getText().toString();
        
        if(TextUtils.isEmpty(userPwdNew)){
            Toast.makeText(ChangePasswordActivity.this, "O nouă parolă este necesară", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Va rugăm introduceți noua parolă");
            editTextPwdNew.requestFocus();
        } else if (TextUtils.isEmpty(userPwdConfirmNew)){
            Toast.makeText(ChangePasswordActivity.this, "Va rugăm confirmați noua parolă", Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("Va rugăm reintroduceți noua parolă");
            editTextPwdConfirmNew.requestFocus();
        } else if (!PASSWORD_PATTERN.matcher(userPwdNew).matches()){
            editTextPwdNew.setError("Parola nu corespunde cerințelor");
            editTextPwdNew.requestFocus();
            }
         else if(!userPwdNew.matches(userPwdConfirmNew)){
            Toast.makeText(ChangePasswordActivity.this, "Parola nu corespunde", Toast.LENGTH_SHORT).show();
            editTextPwdConfirmNew.setError("Va rugăm reintroduceți aceeași parolă");
            editTextPwdConfirmNew.requestFocus();
        } else if(userPwdCurr.matches(userPwdNew)){
            Toast.makeText(ChangePasswordActivity.this, "Parola nouă nu poate fi aceeași cu cea veche", Toast.LENGTH_SHORT).show();
            editTextPwdNew.setError("Va rugăm introduceți o nouă parolă");
            editTextPwdNew.requestFocus();
        } else {
            progressBar.setVisibility(View.VISIBLE);
            firebaseUser.updatePassword(userPwdNew).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                   if(task.isSuccessful()){
                       Toast.makeText(ChangePasswordActivity.this, "Parola a fost schimbată", Toast.LENGTH_SHORT).show();
                       Intent intent= new Intent(ChangePasswordActivity.this, MenuPrincipal.class);
                       startActivity(intent);
                       finish();
                   } else {
                       try {
                           throw Objects.requireNonNull(task.getException());
                       } catch (Exception e){
                           Toast.makeText(ChangePasswordActivity.this, "Parola nu a fost schimbată", Toast.LENGTH_SHORT).show();
                       }
                   }
                   progressBar.setVisibility(View.GONE);
                }
            });
        }
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
            NavUtils.navigateUpFromSameTask(ChangePasswordActivity.this);
        }
        else if(id==R.id.menu_refresh){
            //Refresh activity
            startActivity(getIntent());
            finish();
            overridePendingTransition(0,0);
        } else if(id==R.id.menu_update_profile){
            Intent intent=new Intent(ChangePasswordActivity.this, UpdateProfileActivity.class);
            startActivity(intent);
            finish();
        }  else if(id==R.id.menu_update_email){
            Intent intent=new Intent(ChangePasswordActivity.this, UpdateEmailActivity.class );
            startActivity(intent);
            finish();
        }  else if(id==R.id.menu_change_password){
            Intent intent=new Intent(ChangePasswordActivity.this, ChangePasswordActivity.class );
            startActivity(intent);
            finish();
        } else if (id==R.id. menu_delete_profile){
            Intent intent=new Intent(ChangePasswordActivity.this, DeleteProfileActivity.class );
            startActivity(intent);
            finish();
        } else if (id==R.id.menu_logout) {
            authProfile.signOut();
            Toast.makeText(ChangePasswordActivity.this, "V-ați delogat cu succces!", Toast.LENGTH_SHORT).show();
            Intent intent=new Intent(ChangePasswordActivity.this, MainActivity.class );
            // clear stack to prevent user coming back after logout
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(ChangePasswordActivity.this, "Ceva nu a funcționat corect!", Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }
}