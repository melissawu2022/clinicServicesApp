package com.example.clinicservicesapp.EmployeeFeatures;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.clinicservicesapp.MainActivity;
import com.example.clinicservicesapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class DisplayEmployee extends AppCompatActivity {

    private TextView txtMsg1;
    private TextView txtMsg2;
    private Button btnManageServ;
    private Button btnEditWorkingHour;
    private Button btnDisplayHours;
    private Button btnComplete;
    private Button btnViewServices;
    private Button btnAll;

    private FirebaseFirestore db;
    private String docId;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_employee);

        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();

        txtMsg1 = findViewById(R.id.inputUserName3);
        txtMsg2 = findViewById(R.id.role3);
        btnManageServ = findViewById(R.id.btnAddServices);
        btnComplete = findViewById(R.id.btnComplete);
        btnEditWorkingHour= findViewById(R.id.btnEditWorkingHour);
        btnDisplayHours = findViewById(R.id.btnViewHours);
        btnViewServices = findViewById(R.id.btnViewServices);
        btnAll = findViewById(R.id.btnAppointments);

        if(getIntent().getBundleExtra("information") != null){
            bundle = getIntent().getBundleExtra("information");
        }

        txtMsg1.setText(bundle.getString("name"));
        txtMsg2.setText(bundle.getString("role"));
        docId = bundle.getString("docId");

        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateCompleteProfile();
            }
        });

        btnManageServ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateAddServices();
            }
        });

        btnViewServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateViewServices();
            }
        });

        btnEditWorkingHour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateEditWorkingHours();
            }
        });

        btnDisplayHours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateDisplayWorkingHours();
            }
        });

        btnAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateAllAppointments();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        db.collection("Employee")
                .document(docId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Log.d("DocSnap", documentSnapshot.getData().toString());
                        boolean profile = documentSnapshot.get("profile").equals("");
                        String clinicId = documentSnapshot.get("clinicID").toString();

                        Log.d("Profile InComplete", String.valueOf(profile));

                        if(profile){
                            AlertDialog.Builder alert = new AlertDialog.Builder(DisplayEmployee.this)
                                    .setTitle("Profile Incomplete")
                                    .setMessage("Profile Incomplete - \n" +
                                            "Select Yes to navigate to profile page\n" +
                                            "Select No to close dialog")
                                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            //Don't forget to the intent Put Extra Bundle
                                            navigateCompleteProfile();
                                        }
                                    })
                                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            btnViewServices.setEnabled(false);
                                            btnManageServ.setEnabled(false);
                                            btnDisplayHours.setEnabled(false);
                                            btnEditWorkingHour.setEnabled(false);
                                        }
                                    });

                            alert.show();
                        }

                        db.collection("Clinic")
                                .document(clinicId)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        boolean workingHoursId = documentSnapshot.get("hours").equals("");

                                        if(workingHoursId){
                                            btnDisplayHours.setEnabled(false);
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                    }
                });
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DisplayEmployee.this)
                .setTitle("Confirmation")
                .setMessage("Do you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent logOut = new Intent(DisplayEmployee.this, MainActivity.class);
                        finish();
                        startActivity(logOut);
                    }
                })
                .setNegativeButton("No", null);

        alertDialog.show();
    }

    private void navigateAllAppointments(){
        Intent intent = new Intent(DisplayEmployee.this, AllAppointments.class);
        intent.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(intent);
    }

    private void navigateCompleteProfile(){
        Intent intent = new Intent(DisplayEmployee.this, CompleteProfile.class);
        intent.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(intent);
    }

    private void navigateAddServices(){
        Intent intent = new Intent(DisplayEmployee.this, AddServices.class);
        intent.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(intent);
    }

    private void navigateViewServices(){
        Intent intent = new Intent(DisplayEmployee.this, ViewServices.class);
        intent.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(intent);
    }

    private void navigateEditWorkingHours(){
        Intent intent = new Intent(DisplayEmployee.this, WorkingHour.class);
        intent.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(intent);
    }

    private void navigateDisplayWorkingHours(){
        Intent intent = new Intent(DisplayEmployee.this, DisplayHours.class);
        intent.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(intent);
    }
}
