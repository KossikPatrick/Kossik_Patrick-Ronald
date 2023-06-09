package com.mindtoheart.licenta;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

public class ForgotPasswordActivity extends AppCompatActivity {
    public Button buttonPwdReset;
    private EditText editTextPwdResetEmail;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Resetare parolă");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#006600"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        editTextPwdResetEmail= findViewById(R.id.editText_password_reset_email);
        buttonPwdReset=findViewById(R.id.button_password_reset);
        progressBar=findViewById(R.id.progressBar);
        buttonPwdReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editTextPwdResetEmail.getText().toString();
                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(ForgotPasswordActivity.this, "Vă rugăm introduceți e-mailul înregistrat!", Toast.LENGTH_SHORT).show();
                    editTextPwdResetEmail.setError("E-mailul este necesar!");
                    editTextPwdResetEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(ForgotPasswordActivity.this, "Vă rugăm introduceți un e-mail valid!", Toast.LENGTH_SHORT).show();
                    editTextPwdResetEmail.setError("E-mailul este necesar!");
                    editTextPwdResetEmail.requestFocus();
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    resetPassword(email);
                }
            }
        });

    }

    private void resetPassword(String email) {
        authProfile= FirebaseAuth.getInstance();
        authProfile.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               if(task.isSuccessful())
               {
                   Toast.makeText(ForgotPasswordActivity.this, "Vă rugăm să verificați căsuța dvs. de e-mail pentru linkul de resetare a parolei",
                           Toast.LENGTH_LONG).show();
                   Intent intent=new Intent(ForgotPasswordActivity.this, MainActivity.class );
                   // pentru a preveni revenirea utilizatorului după resetarea parolei
                   intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                   startActivity(intent);
                   finish();
               } else {
                   try {
                       throw Objects.requireNonNull(task.getException());
                   } catch(FirebaseAuthInvalidUserException e) {
                       editTextPwdResetEmail.setError("Utilizatorul nu există sau nu mai este valid. Vă rugăm să vă înregistrați din nou.");

                   } catch (Exception e){

                   }
               }
               progressBar.setVisibility(View.GONE);
            }
        });
    }
}