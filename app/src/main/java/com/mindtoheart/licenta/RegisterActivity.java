package com.mindtoheart.licenta;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;
import java.util.regex.Pattern;


public class RegisterActivity extends AppCompatActivity {
    private EditText editTextRegisterFullName, editTextRegisterEmail, editTextRegisterDoB,
            editTextRegisterPwd, editTextRegisterConfirmPwd;
    private ProgressBar progressBar;
    private RadioGroup radioGroupRegisterGender;
    private RadioButton radioButtonRegisterGenderSelected;

    private DatePickerDialog picker;
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[A-Z])" +     // at least 1 upper case
                    "(?=\\S+$)" +            // no white spaces
                    ".{4,}" +                // at least 4 characters
                    "$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        Objects.requireNonNull(getSupportActionBar()).setTitle("Înregistrare");
        ColorDrawable colorDrawable
                = new ColorDrawable(Color.parseColor("#006600"));

        // Set BackgroundDrawable
        getSupportActionBar().setBackgroundDrawable(colorDrawable);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toast.makeText(RegisterActivity.this, "Vă puteți înregistra acum", Toast.LENGTH_LONG).show();
        //editText
        editTextRegisterFullName=findViewById(R.id.editText_register_full_name);
        editTextRegisterEmail=findViewById(R.id.editText_register_email);
        editTextRegisterDoB=findViewById(R.id.editText_register_dop);
        editTextRegisterPwd=findViewById(R.id.editText_register_password);
        editTextRegisterConfirmPwd=findViewById(R.id.editText_register_confirm_password);
        //radioButton
        radioGroupRegisterGender=findViewById(R.id.radio_group_register_gender);
        radioGroupRegisterGender.clearCheck();
        progressBar=findViewById(R.id.progressBar);
        // setting  up DatePicker on edit text
        editTextRegisterDoB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar= Calendar.getInstance();
                int day= calendar.get(Calendar.DAY_OF_MONTH);
                int month=calendar.get(Calendar.MONTH);
                int year=calendar.get(Calendar.YEAR);

                // date picker dialog
                picker= new DatePickerDialog(RegisterActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                       editTextRegisterDoB.setText(dayOfMonth+"/"+(month+1)+"/"+year);
                    }
                }, year, month, day);
                picker.show();
            }
        });


        Button buttonRegister=findViewById(R.id.btn_register);
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedGenderId= radioGroupRegisterGender.getCheckedRadioButtonId();
                radioButtonRegisterGenderSelected=findViewById(selectedGenderId);
                // Obtain the entered data
                String textFullName= editTextRegisterFullName.getText().toString();
                String textEmail=editTextRegisterEmail.getText().toString();
                String textDoB=editTextRegisterDoB.getText().toString();
                String textPwd=editTextRegisterPwd.getText().toString();

                String textConfirmPwd=editTextRegisterConfirmPwd.getText().toString();
                String textGender;
                if(TextUtils.isEmpty(textFullName) )
                {

                    editTextRegisterFullName.setError("Câmpul nu poate fi gol");
                    editTextRegisterFullName.requestFocus();
                }
                else if(!textFullName.matches("[a-zA-Z\\s-]+"))
                {
                    editTextRegisterFullName.requestFocus();
                    editTextRegisterFullName.setError("Introduceți numai caractere alfabetice, excepție făcând:-");
                }  else if (TextUtils.isEmpty(textEmail)){

                    editTextRegisterEmail.setError("Vă rugăm introduceți e-mailul");
                    editTextRegisterEmail.requestFocus();
                } else if (!Patterns.EMAIL_ADDRESS.matcher(textEmail).matches()) {
                    Toast.makeText(RegisterActivity.this, "Vă rugăm reintroduceți e-mailul", Toast.LENGTH_LONG).show();
                    editTextRegisterEmail.setError("Un email valid este necesar");
                    editTextRegisterEmail.requestFocus();
                }else if (TextUtils.isEmpty(textDoB)){
                    Toast.makeText(RegisterActivity.this, "Vă rugăm introduceți data nașterii", Toast.LENGTH_LONG).show();
                    editTextRegisterDoB.setError("Data nașterii este necesară");
                    editTextRegisterDoB.requestFocus();
                } else if(radioGroupRegisterGender.getCheckedRadioButtonId()== -1){
                    Toast.makeText(RegisterActivity.this, "Vă rugăm selectați genul", Toast.LENGTH_LONG).show();
                    radioButtonRegisterGenderSelected.setError("Genul este necesar");
                    radioButtonRegisterGenderSelected.requestFocus();
                } else if (TextUtils.isEmpty(textPwd)){

                    editTextRegisterPwd.setError("Vă rugăm introduceți parola");
                    editTextRegisterPwd.requestFocus();
                } else if (!PASSWORD_PATTERN.matcher(textPwd).matches()){

                    editTextRegisterPwd.setError("Parola nu corespunde cerințelor");
                    editTextRegisterPwd.requestFocus();
                } else if(TextUtils.isEmpty(textConfirmPwd)){

                    editTextRegisterConfirmPwd.setError("Vă rugăm confirmați parola");
                    editTextRegisterConfirmPwd.requestFocus();
                } else if(!textPwd.equals(textConfirmPwd)){

                    editTextRegisterConfirmPwd.setError("Vă rugăm introduceți aceeași parolă");
                    editTextRegisterConfirmPwd.requestFocus();
                    // clear entered passwords
                    editTextRegisterPwd.clearComposingText();
                    editTextRegisterConfirmPwd.clearComposingText();
                } else {
                    textGender=radioButtonRegisterGenderSelected.getText().toString();
                    progressBar.setVisibility(View.VISIBLE);
                    registerUser(textFullName, textEmail, textDoB, textGender, textPwd);
                }
            }
        });
        // Show hide password using eye icon
        ImageView imageViewShowHidePwd=findViewById(R.id.imageView_show_hide_pwd);
        imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
        imageViewShowHidePwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextRegisterPwd.getTransformationMethod().equals(HideReturnsTransformationMethod.getInstance())){
                    // if pwd is visible then hide it
                    editTextRegisterPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    // change icon
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_hide_pwd);
                }else{
                    editTextRegisterPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    imageViewShowHidePwd.setImageResource(R.drawable.ic_show_pwd);
                }
            }
        });



    }
    // Register user using the credentials given
    private void registerUser(String textFullName, String textEmail, String textDoB, String textGender, String textPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        //Se crează userul
        auth.createUserWithEmailAndPassword(textEmail, textPwd).addOnCompleteListener(RegisterActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){

                            FirebaseUser firebaseUser= auth.getCurrentUser();
                            //Se updatează numele
                            UserProfileChangeRequest profileChangeRequest= new UserProfileChangeRequest.Builder().setDisplayName(textFullName).build();
                            firebaseUser.updateProfile(profileChangeRequest);

                            // Introducem datele în  firebase realtime database
                            ReadWriteUserDetails writeUserDetails=new ReadWriteUserDetails( textFullName, textDoB, textGender);
                            // Extragem userul din "Registered users"
                            DatabaseReference referenceProfile= FirebaseDatabase.getInstance().getReference("Registered Users");
                            referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        // se trimite e-mailul de verificare
                                        firebaseUser.sendEmailVerification();
                                        Toast.makeText(RegisterActivity.this, "V-ați înregistrat cu succes. Va rugăm să verificați e-mailul.", Toast.LENGTH_LONG).show();
                                        // se deschide profilul după înregistrare
                                        Intent intent=new Intent(RegisterActivity.this, MenuPrincipal.class);
                                          // pentru a preveni userul să reintre în înregistrare după ce apasă butonul de back
                                         intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK
                                         | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                         finish(); // pentru a închide register activity

                                    }else{
                                        Toast.makeText(RegisterActivity.this, "Înregistrarea nu a reușit. Va rugăm încercați din nou.", Toast.LENGTH_LONG).show();


                                    }
                                    // ascundem progress bar-ul
                                    progressBar.setVisibility(View.GONE);

                                }
                            });

                        } else {
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch(FirebaseAuthWeakPasswordException e)
                            {
                                editTextRegisterPwd.setError("Parola dumneavostră este prea scurtă. Va rugăm introduceți o colecție de litere, numere și caractere speciale.");
                                editTextRegisterPwd.requestFocus();
                            } catch (FirebaseAuthInvalidCredentialsException e){
                                editTextRegisterEmail.setError("E-mailul dumneavostră este invalid, sau este deja folosit. ");
                                editTextRegisterEmail.requestFocus();
                            } catch( FirebaseAuthUserCollisionException e) {
                                editTextRegisterEmail.setError("Numele de utilizator este deja înregistrat cu această adresă de e-mail. ");
                                editTextRegisterEmail.requestFocus();
                            } catch (Exception e){

                            }
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });

    }
}