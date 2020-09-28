package com.example.clinicservicesapp.EmployeeFeatures;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.clinicservicesapp.Models.Appointment;
import com.example.clinicservicesapp.PatientFeatures.SearchServices;
import com.example.clinicservicesapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.security.AllPermission;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AllAppointments extends AppCompatActivity {
    private ListView clinicappointments;
    private ArrayAdapter<Appointment> adapter;
    private ArrayList<Appointment> arrayList;

    private FirebaseFirestore db;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_appointments);
        clinicappointments = findViewById(R.id.clinicAppointments);
        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();

        if(getIntent().getBundleExtra("information") != null){
            bundle = getIntent().getBundleExtra("information");
        }

        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(AllAppointments.this, android.R.layout.simple_list_item_1, arrayList);
        clinicappointments.setAdapter(adapter);

        clinicappointments.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                AlertDialog.Builder appointmentMenu = new AlertDialog.Builder(AllAppointments.this);
                appointmentMenu.setMessage("Appointment Menu");
                appointmentMenu.setCancelable(true);
                appointmentMenu.setPositiveButton("Dismiss Appointment",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                    // need to be implemented

                                final Appointment appointment = arrayList.get(position);

                                db.collection("Appointments")
                                        .document(appointment.getDocumentId())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                db.collection("Clinic")
                                                        .document(appointment.getClinicId())
                                                        .get()
                                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                Map<String, Object> read = documentSnapshot.getData();
                                                                List<String> ids = (List<String>) read.get("appointments");

                                                                ids.remove(appointment.getDocumentId());

                                                                read.put("appointments", ids);

                                                                db.collection("Clinic")
                                                                        .document(appointment.getClinicId())
                                                                        .set(read)
                                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void aVoid) {
                                                                                arrayList.remove(position);
                                                                                adapter.notifyDataSetChanged();
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                Toast.makeText(AllAppointments.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                            }
                                                                        });
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(AllAppointments.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AllAppointments.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });

                            }
                        });

                appointmentMenu.setNegativeButton(
                        "Back", null);

                AlertDialog alert2 = appointmentMenu.create();
                alert2.show();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        db.collection("Appointments")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        final List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                        db.collection("Employee")
                                .document(bundle.getString("docId"))
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        final String clinicId = documentSnapshot.get("clinicID").toString();

                                        for(DocumentSnapshot docSnap : documentSnapshots){
                                            if(! docSnap.getId().equals("AA")){
                                                Map<String, Object> read = docSnap.getData();

                                                if(read.get("clinicId").equals(clinicId)){
                                                    Appointment appointment = new Appointment(
                                                            docSnap.getId(),
                                                            (String) read.get("clinicId"),
                                                            (String) read.get("clinicName"),
                                                            (String) read.get("date"),
                                                            (String) read.get("patientID"),
                                                            (String) read.get("time"),
                                                            true
                                                    );

                                                    arrayList.add(appointment);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        }

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AllAppointments.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {

        Intent i = new Intent(AllAppointments.this, DisplayEmployee.class);
        i.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(i);

        //Toast.makeText(DisplayPatient.this, "BackButton Pressed", Toast.LENGTH_SHORT).show();
    }


}
