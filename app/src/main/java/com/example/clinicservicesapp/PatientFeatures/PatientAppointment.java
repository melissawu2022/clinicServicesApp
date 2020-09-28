package com.example.clinicservicesapp.PatientFeatures;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.clinicservicesapp.Models.Appointment;
import com.example.clinicservicesapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class PatientAppointment extends AppCompatActivity {

    private ListView appointments;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;

    private FirebaseFirestore db;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_appointment);
        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();

        if(getIntent().getBundleExtra("information") != null){
            bundle = getIntent().getBundleExtra("information");
        }

        appointments = findViewById(R.id.patientAppointments);
        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(PatientAppointment.this, android.R.layout.simple_list_item_1, arrayList);
        appointments.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        db.collection("Appointments")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                        for(DocumentSnapshot documentSnapshot : documentSnapshots){
                            if(! documentSnapshot.getId().equals("AA")){

                                Map<String, Object> read = documentSnapshot.getData();

                                if(read.get("patientID").equals(bundle.getString("docId"))){
                                    Appointment appointment = new Appointment(
                                            documentSnapshot.getId(),
                                            (String) read.get("clinicId"),
                                            (String) read.get("clinicName"),
                                            (String) read.get("date"),
                                            (String) read.get("patientID"),
                                            (String) read.get("time"),
                                            false
                                    );

                                    arrayList.add(appointment.toString());
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

    @Override
    public void onBackPressed() {

        Intent i = new Intent(PatientAppointment.this, DisplayPatient.class);
        i.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(i);

        //Toast.makeText(DisplayPatient.this, "BackButton Pressed", Toast.LENGTH_SHORT).show();
    }
}
