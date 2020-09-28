package com.example.clinicservicesapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.clinicservicesapp.AdminFeatures.AdminControlPanel;
import com.example.clinicservicesapp.EmployeeFeatures.DisplayEmployee;
import com.example.clinicservicesapp.Models.Account;
import com.example.clinicservicesapp.Models.Employee;
import com.example.clinicservicesapp.Models.Patient;
import com.example.clinicservicesapp.PatientFeatures.DisplayPatient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.*;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText txtUsername, txtPassword;
    TextView txtLinkLogin;

    //Firebase Objects
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = FirebaseFirestore.getInstance();

        getSupportActionBar().hide();

        txtUsername = findViewById(R.id.usernameField);
        txtPassword = findViewById(R.id.passwordField);

        cancel();

        txtLinkLogin = findViewById(R.id.linkSignup);
        txtLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent signUpIntent = new Intent(MainActivity.this, SignupActivity.class);
                finish();
                startActivity(signUpIntent);

            }
        });



    }



    public void cancel(){
        txtUsername.setText("");
        txtPassword.setText("");
    }



    public void openWelcomePage(View view) throws NoSuchAlgorithmException{
        if (view.getId() == R.id.login) {
            //This checks to see if the username equals Admin and checks the password

            //SHA256 Hashing
            final String pass = txtPassword.getText().toString();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(pass.getBytes(StandardCharsets.UTF_8));
            byte[] bt = digest.digest();

            final String sha256 = String.format("%064x", new BigInteger(1,bt));


            //Admin pass = 5T5ptQ = 36e0d001bb89f979bf685399fea558db3c269155a02c77416fa06d79c03f3d39

            final String username = txtUsername.getText().toString();

            if(username.equals("admin") && sha256.equals("36e0d001bb89f979bf685399fea558db3c269155a02c77416fa06d79c03f3d39")) {

                Intent i = new Intent(MainActivity.this, AdminControlPanel.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", "admin");
                bundle.putString("role", "Administrator");
                i.putExtra("information", bundle);
                startActivity(i);
            } else {

                final Task<QuerySnapshot> employee = db.collection("Employee").get();
                final Task<QuerySnapshot> patient = db.collection("Patient").get();

                Task combined = Tasks.whenAll(employee, patient)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                boolean foundUser = false;

                                List<DocumentSnapshot> docEmployeeList = employee.getResult().getDocuments();
                                List<DocumentSnapshot> docPatientList = patient.getResult().getDocuments();

                                Log.d("docEmployeeList", docEmployeeList.toString());
                                Log.d("docPatientList", docPatientList.toString());

                                Map<String, Object> read = new HashMap<>();
                                //Employee
                                for(DocumentSnapshot docSnap : docEmployeeList){
                                    if(! docSnap.getId().equals("AA")){
                                        read = docSnap.getData();

                                        Log.d("ReadEmployee", read.toString());

                                        Account account = new Employee(
                                                (String) read.get("name"),
                                                (String) read.get("username"),
                                                (String) read.get("password"),
                                                docSnap.getId()
                                        );

                                        if(username.equals(account.getUsername()) && sha256.equals(account.getPassword())){
                                            foundUser = true;
                                            navigateEmployee(
                                                    account.getName(),
                                                    account.getRole().toString(),
                                                    docSnap.getId()
                                            );
                                        }
                                    }
                                }

                                //Patient
                                for(DocumentSnapshot docSnap : docPatientList){
                                    if(! docSnap.getId().equals("AA")){
                                        read = docSnap.getData();

                                        Log.d("ReadPatient", read.toString());

                                        Account account = new Patient(
                                                (String) read.get("name"),
                                                (String) read.get("username"),
                                                (String) read.get("password"),
                                                docSnap.getId()
                                        );

                                        if(username.equals(account.getUsername()) && sha256.equals(account.getPassword())){
                                            foundUser = true;
                                            navigatePatient(
                                                    account.getName(),
                                                    account.getRole().toString(),
                                                    docSnap.getId()
                                            );
                                        }
                                    }
                                }

                                if(! foundUser){
                                    txtUsername.setError("Invalid username");
                                    txtPassword.setError("Invalid password");
                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });

            }

        }
    }

    private void navigateEmployee(String name, String role, String docId){
        Intent intent = new Intent(MainActivity.this, DisplayEmployee.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("role", role);
        bundle.putString("docId", docId);
        intent.putExtra("information", bundle);
        finish();
        startActivity(intent);
    }

    private void navigatePatient(String name, String role, String docId){
        Intent intent = new Intent(MainActivity.this, DisplayPatient.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("role", role);
        bundle.putString("docId", docId);
        intent.putExtra("information", bundle);
        finish();
        startActivity(intent);
    }
}

//    final Task<QuerySnapshot> clinic = db.collection("Clinic").get();
//    final Task<QuerySnapshot> employee = db.collection("Employee").get();
//    final Task<QuerySnapshot> operations = db.collection("Operations").get();
//    final Task<QuerySnapshot> patient = db.collection("Patient").get();
//    final Task<QuerySnapshot> profile = db.collection("Profile").get();
//    final Task<QuerySnapshot> workingHours = db.collection("Working Hours").get();
//
//    Task combined = Tasks.whenAll(clinic, employee, operations, patient, profile, workingHours)
//            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                @Override
//                public void onSuccess(Void aVoid) {
//                    Log.d("Clinic", clinic.getResult().getDocuments().toString());
//                    Log.d("Employee", employee.getResult().getDocuments().toString());
//                    Log.d("Operations", operations.getResult().getDocuments().toString());
//                    Log.d("Patient", patient.getResult().getDocuments().toString());
//                    Log.d("Profile", profile.getResult().getDocuments().toString());
//                    Log.d("Working Hours", workingHours.getResult().getDocuments().toString());
//                }
//            });
