package com.example.clinicservicesapp;


import android.content.Intent;
import android.os.Bundle;

import com.example.clinicservicesapp.Helpers.Auxiliary;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.*;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class SignupActivity extends AppCompatActivity {

    private EditText userNameField,passwordField,lastNameField,firstNameField,confirmpasswordField;
    private TextView t;
    private CheckBox box;
    private Button btnSignUp;

    private String fName, lName;
    private String username, password, cpassword;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();

        userNameField = findViewById(R.id.username);
        passwordField = findViewById(R.id.passwordField);
        lastNameField = findViewById(R.id.lastname);
        firstNameField = findViewById(R.id.firstname);
        confirmpasswordField = findViewById(R.id.confirmPassword);

        box = findViewById(R.id.checkBox);
        btnSignUp = findViewById(R.id.Signup);

        t = findViewById(R.id.Cancel);

        t.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SignupActivity.this,MainActivity.class);
                finish();
                startActivity(i);
            }
        });


    }

    public void navigateBack(View view){
        Intent i = new Intent(SignupActivity.this,MainActivity.class);
        finish();
        startActivity(i);
    }

    public void login(View view) throws NoSuchAlgorithmException {
        //These next few codes check to see if the input they have is valid or not
        Log.d("CreateAccountChecker", "Inside login(View view) method");
        boolean usernameChecked = false;
        boolean passwordChecked = false;
        boolean firstNameChecked = false;
        boolean lastNameChecked = false;
        boolean confirmpassChecked = false;

        if(!Auxiliary.validName(firstNameField.getText().toString().trim()) ){
            firstNameField.setError("Not a valid first name");
        }else{
            firstNameChecked = true;
            fName = firstNameField.getText().toString();
        }

        if(!Auxiliary.validName(lastNameField.getText().toString())) {
            lastNameField.setError("Not a valid last name");
        }else{
            lastNameChecked = true;
            lName = lastNameField.getText().toString();
        }

        if(userNameField.getText().length() < 3) {
            userNameField.setError("Needs to be more than 2 digits");
        }else{
            usernameChecked = true;
            username = userNameField.getText().toString();
        }

        if(passwordField.getText().length() < 6){
            passwordField.setError("Needs to be greater than 5 digits");
        }else{
            passwordChecked = true;
            password = passwordField.getText().toString();
        }

        if(confirmpasswordField.getText().length() == 0 || !passwordField.getText().toString().equals(confirmpasswordField.getText().toString())){
            confirmpasswordField.setError("Needs to be the same password");
        } else{
            confirmpassChecked = true;
            cpassword = confirmpasswordField.getText().toString();
        }

        boolean isEmployee = box.isChecked();

        if(firstNameChecked && lastNameChecked && usernameChecked && passwordChecked && confirmpassChecked){

            Map<String, Object> newAccount = new HashMap<>();

            btnSignUp.setText("Submitting");
            btnSignUp.setEnabled(false);

            String toHash = password;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(password.getBytes(StandardCharsets.UTF_8));
            byte[] bt = digest.digest();

            final String hashedPassword = String.format("%064x", new BigInteger(1,bt));

            if(isEmployee){

                newAccount.put("name", (fName + " " + lName));
                newAccount.put("username", username);
                newAccount.put("password", hashedPassword);
                newAccount.put("profile", "");

                final String clinicId = db.collection("Clinic")
                        .document().getId();

                newAccount.put("clinicID", clinicId);
                Log.d("Clinic Id", clinicId);

                final String id = db.collection("Employee")
                        .document().getId();

                Log.e("newAccount Map", newAccount.toString());

                final Map<String, Object> clinicAccount = new HashMap<>();
                clinicAccount.put("employeeId", id);
                clinicAccount.put("hours", "");
                clinicAccount.put("services", new ArrayList<String>());

                Log.e("clinicAccount", clinicAccount.toString());

                db.collection("Employee")
                        .document(id)
                        .set(newAccount)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SignupActivity.this, "User Created", Toast.LENGTH_LONG).show();


                                db.collection("Clinic")
                                        .document(clinicId)
                                        .set(clinicAccount)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(SignupActivity.this, "Clinic Profile Updated", Toast.LENGTH_LONG).show();
                                                btnSignUp.setText("Create Account");
                                                btnSignUp.setEnabled(true);
                                                onBackPressed();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                btnSignUp.setText("Create Account");
                                                btnSignUp.setEnabled(true);
                                                onBackPressed();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                btnSignUp.setText("Create Account");
                                btnSignUp.setEnabled(true);
                            }
                        });

            }else{

                toHash = password;
                digest = MessageDigest.getInstance("SHA-256");
                digest.update(password.getBytes(StandardCharsets.UTF_8));
                bt = digest.digest();

                final String hashedPassword1 = String.format("%064x", new BigInteger(1,bt));

                newAccount.put("name", (fName + " " + lName));
                newAccount.put("username", username);
                newAccount.put("password", hashedPassword1);

                String id = db.collection("Patient")
                        .document().getId();

                db.collection("Patient")
                        .document(id)
                        .set(newAccount)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(SignupActivity.this, "Patient account created", Toast.LENGTH_LONG).show();
                                btnSignUp.setText("Create Account");
                                btnSignUp.setEnabled(true);
                                onBackPressed();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(SignupActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                btnSignUp.setText("Create Account");
                                btnSignUp.setEnabled(true);
                            }
                        });
            }

        }

    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(SignupActivity.this, MainActivity.class);
        finish();
        startActivity(i);

    }
}
