package com.mindtoheart.licenta;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText editTextLoginEmail, editTextLoginPwd;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Objects.requireNonNull(getSupportActionBar()).setTitle("Logare");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#006600"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editTextLoginEmail=findViewById(R.id.editText_login_email);
        editTextLoginPwd=findViewById(R.id.editText_login_pwd);
        progressBar= findViewById(R.id.progressB);
        authProfile= FirebaseAuth.getInstance();
        // forgot password
        Button buttonForgotPassword= findViewById(R.id.button_forgot_password);
        buttonForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this, "Vă puteți recupera parola acum!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));

            }
        });
        // Show hide password using eye icon
        ImageView imageViewShowHidePwd=findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextLoginPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    // if pwd is visible then hide it
                    editTextLoginPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    editTextLoginPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });

        // login button
        Button buttonLogin= findViewById(R.id.button_login);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textEmail= editTextLoginEmail.getText().toString();
                String textPwd=editTextLoginPwd.getText().toString();

                if(TextUtils.isEmpty(textEmail)){
                    Toast.makeText(LoginActivity.this, "Vă rugăm introduceți e-mailul", Toast.LENGTH_LONG).show();
                    editTextLoginEmail.setError("E-mailul este necesar");
                    editTextLoginEmail.requestFocus();
                }else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()){
                    Toast.makeText(LoginActivity.this, "Vă rugăm re-introduceți e-mailul", Toast.LENGTH_LONG).show();
                    editTextLoginEmail.setError("Un e-mail valid este necesar");
                    editTextLoginEmail.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)){
                    Toast.makeText(LoginActivity.this, "Vă rugăm introduceți parola", Toast.LENGTH_LONG).show();
                    editTextLoginPwd.setError("Parola este necesară");
                    editTextLoginPwd.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    loginUser(textEmail, textPwd);
                }

            }
        });

    }

    private void loginUser(String textEmail, String textPwd) {
        authProfile.signInWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    //obține instanța utilizatorului curent
                    FirebaseUser firebaseUser= authProfile.getCurrentUser();
                    //Se verifică dacă e-mailul este verificat înainte ca utilizatorul să-și poată accesa profilul
                    if(firebaseUser.isEmailVerified()){
                        Toast.makeText(LoginActivity.this, "V-ați logat cu succes ", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(LoginActivity.this, MenuPrincipal.class));
                        finish(); //close
                    }else {
                        firebaseUser.sendEmailVerification();
                        authProfile.signOut(); // sign out user
                        showAlertDialog();
                    }

                }else {
                    try { //excepții
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthInvalidUserException e) {
                        editTextLoginEmail.setError("Utilizatorul nu există, sau nu mai este valid. Va rugăm să vă înregistrați din nou.");
                        editTextLoginEmail.requestFocus();
                    } catch (FirebaseAuthInvalidCredentialsException e){ //dacă parola este incorectă
                        editTextLoginPwd.setError("Parolă incorectă. Va rugăm încercați din nou.");
                        editTextLoginPwd.requestFocus();
                    } catch (Exception e) {

                    }

                }
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void showAlertDialog() {
        // setup alert builder
        AlertDialog.Builder builder=new AlertDialog.Builder(LoginActivity.this);
        builder.setTitle("E-mailul nu este verificat");
        builder.setMessage("Va rugăm verificați-vă e-mailul.");
        // Open email app if user clicks continue button
        builder.setPositiveButton("Continuă", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent= new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_APP_EMAIL);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //to email app in new window and not within our app
                startActivity(intent);
            }
        });
        // Create alertdialog
        AlertDialog alertDialog= builder.create();
        // show alertdialog
        alertDialog.show();
    }
// check if user is already logged in
    @Override
    protected void onStart() {
        super.onStart();
        if(authProfile.getCurrentUser() != null ){
            Toast.makeText(LoginActivity.this, "Sunteți deja logat", Toast.LENGTH_SHORT).show();
            // Start UserProfileActivity
            startActivity(new Intent(LoginActivity.this, MenuPrincipal.class));
            finish(); //close
        }else {
            Toast.makeText(LoginActivity.this, "Vă puteți loga acum", Toast.LENGTH_SHORT).show();
        }
    }
}