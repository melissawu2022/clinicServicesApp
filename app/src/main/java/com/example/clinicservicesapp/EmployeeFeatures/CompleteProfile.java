package com.example.clinicservicesapp.EmployeeFeatures;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.clinicservicesapp.Helpers.Auxiliary;
import com.example.clinicservicesapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompleteProfile extends AppCompatActivity {

    private Button btnComplete;
    private EditText address;
    private EditText province;
    private EditText suite;
    private EditText city;
    private EditText postal;
    private EditText phone;
    private EditText clinic;
    private Spinner insurance;
    private Spinner payment;

    private Bundle bundle;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();

        if(getIntent().getBundleExtra("information") != null){
            bundle = getIntent().getBundleExtra("information");
        }

        btnComplete = (Button)findViewById(R.id.btnComplete);
        address = (EditText)findViewById(R.id.editAddress);
        suite = (EditText) findViewById(R.id.editOptional);
        province = (EditText)findViewById(R.id.editProvince);
        city = (EditText)findViewById(R.id.editCity);
        postal = (EditText)findViewById(R.id.editPostal);
        phone = (EditText)findViewById(R.id.editPhone);
        clinic = (EditText)findViewById(R.id.editClinic);
        insurance = (Spinner)findViewById(R.id.spinner);
        payment = (Spinner)findViewById(R.id.spinner2);

        Spinner mySpinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(CompleteProfile.this,android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Insurances));

        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner.setAdapter(myAdapter);

        Spinner mySpinner2 = (Spinner)findViewById(R.id.spinner2);
        ArrayAdapter<String> myAdapter2 = new ArrayAdapter<String>(CompleteProfile.this,android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Payments));

        myAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinner2.setAdapter(myAdapter2);


        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String addy = address.getText().toString().trim();
                String prov = province.getText().toString().trim();
                String cty = city.getText().toString().trim();
                String post = postal.getText().toString().trim();
                String phne = phone.getText().toString().trim();
                String clnc = clinic.getText().toString().trim();
                String apartment = suite.getText().toString().trim();

                String ins = insurance.getSelectedItem().toString().trim();
                String pay = payment.getSelectedItem().toString().trim();


                boolean checkAddress = false;
                boolean checkProv = false;
                boolean checkCity = false;
                boolean checkPostal = false;
                boolean checkPhone = false;
                boolean checkClinic = false;

                boolean checkInsurance = false;
                boolean checkPayment = false;

                if(Auxiliary.checkEmpty(addy)){
                    address.setError("Need to put a value in.");
                } else {
                    checkAddress = true;
                }

                if(Auxiliary.checkEmpty(prov)){
                    province.setError("Needs to put a value in.");
                } else {
                    checkProv = true;
                }

                if(Auxiliary.checkEmpty(cty)){
                    city.setError("Needs to put a value in.");
                } else {
                    checkCity = true;
                }

                if(Auxiliary.checkEmpty(post)){
                    postal.setError("Needs to put a value in.");
                } else {
                    checkPostal = true;
                }

                if(Auxiliary.checkEmpty(phne)){
                    phone.setError("Needs to put a value in.");
                } else {
                    checkPhone = true;
                }

                if(Auxiliary.checkEmpty(clnc)){
                    clinic.setError("Needs to put a value in.");
                } else {
                    checkClinic = true;
                }

                if(Auxiliary.checkEmpty(ins)){
                    TextView errorText = (TextView)insurance.getSelectedView();
                    errorText.setError("Select an Option");

                } else {
                    checkInsurance = true;
                }

                if(Auxiliary.checkEmpty(pay)){
                    TextView errTxt = (TextView)payment.getSelectedView();
                    errTxt.setError("Select an Option");

                } else {
                    checkPayment = true;
                }


                if((checkAddress && checkCity && checkClinic && checkInsurance && checkPayment && checkPhone && checkPostal && checkProv)){
                    Map<String, Object> profile = new HashMap<>();

                    profile.put("Apartment", apartment);
                    profile.put("Insurance", ins);
                    profile.put("NameClinic", clnc);
                    profile.put("Payment", pay);
                    profile.put("PhoneNumber", phne);
                    profile.put("PostalCode", post);
                    profile.put("Province", prov);
                    profile.put("StreetAddress", addy);
                    profile.put("Town", cty);

                    final String docId = FirebaseFirestore.getInstance().collection("Profile")
                            .document().getId();
                    Log.d("DocID", docId);

                    db.collection("Profile")
                            .document(docId)
                            .set(profile)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(CompleteProfile.this, "Profile Created", Toast.LENGTH_LONG).show();

                                    db.collection("Employee")
                                            .document(bundle.getString("docId"))
                                            .get()
                                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    Map<String, Object> read = documentSnapshot.getData();

                                                    read.put("profile", docId);

                                                    db.collection("Employee")
                                                            .document(bundle.getString("docId"))
                                                            .set(read)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(CompleteProfile.this, "Profile Updated", Toast.LENGTH_LONG).show();
                                                                    navigateBack();
                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(CompleteProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(CompleteProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            });
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(CompleteProfile.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                }
                            });
                }

            }
        });

        db.collection("Employee")
                .document(bundle.getString("docId"))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String profileId = documentSnapshot.get("profile").toString();
                        Log.d("Profile ID", profileId);

                        if(! profileId.equals("")){
                            db.collection("Profile")
                                    .document(profileId)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                        @Override
                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                            Map<String, Object> read = documentSnapshot.getData();

                                            address.setText(read.get("StreetAddress").toString());
                                            suite.setText(read.get("Apartment").toString());
                                            clinic.setText(read.get("NameClinic").toString());
                                            phone.setText(read.get("PhoneNumber").toString());
                                            postal.setText(read.get("PostalCode").toString());
                                            province.setText(read.get("Province").toString());
                                            city.setText(read.get("Town").toString());

                                            String[] lst = getResources().getStringArray(R.array.Insurances);
                                            int position = 0;
                                            for(int i = 0; i < lst.length; i++){
                                                if(lst[i].equals(read.get("Insurance"))){
                                                    position = i;
                                                }
                                            }
                                            insurance.setSelection(position);

                                            lst = getResources().getStringArray(R.array.Payments);
                                            position = 0;
                                            for(int i = 0; i < lst.length; i++){
                                                if(lst[i].equals(read.get("Payment"))){
                                                    position = i;
                                                }
                                            }
                                            payment.setSelection(position);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {

                                        }
                                    });
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CompleteProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        navigateBack();
    }

    public void navigateBack(){
        Intent intent = new Intent(CompleteProfile.this, DisplayEmployee.class);
        intent.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(intent);
    }
}
