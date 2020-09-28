package com.example.clinicservicesapp.EmployeeFeatures;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.clinicservicesapp.Models.Services;
import com.example.clinicservicesapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddServices extends AppCompatActivity {

    private ListView lstServices;
    private ArrayList<Services> arrayList;
    private ArrayAdapter<Services> adapter;

    private FirebaseFirestore db;
    private Bundle bundle;
    private String clinicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_services);
        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();

        if(getIntent().getBundleExtra("information") != null){
            bundle = getIntent().getBundleExtra("information");
        }

        lstServices = findViewById(R.id.serviceList);
        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<Services>(AddServices.this, android.R.layout.simple_list_item_1, arrayList);
        lstServices.setAdapter(adapter);

        lstServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Services services = arrayList.get(position);

                Log.d("Service", services.toString());
                Log.d("CLINIC_ID", clinicId);

                db.collection("Clinic")
                        .document(clinicId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Map<String, Object> read = documentSnapshot.getData();
                                List<String> list = (List<String>)read.get("services");
                                boolean foundDocID = false;
                                for(String str : list){
                                    if(str.equals(services.getDocumentID())){
                                        foundDocID = true;
                                    }
                                }

                                Log.d("List", list.toString());
                                if(! foundDocID) {
                                    list.add(services.getDocumentID());
                                }
                                Log.d("List_After", list.toString());

                                read.put("services", list);

                                db.collection("Clinic")
                                        .document(clinicId)
                                        .set(read)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(AddServices.this, "Service Added", Toast.LENGTH_LONG).show();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(AddServices.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AddServices.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        db.collection("Employee")
                .document(getIntent().getBundleExtra("information").getString("docId"))
                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                clinicId = documentSnapshot.get("clinicID").toString();
                Log.d("CLINIC_ID", clinicId);
            }
        });

    }

    @Override
    public void onBackPressed() {
        navigateBack();
    }

    @Override
    protected void onStart() {
        super.onStart();
        db.collection("Operations").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                arrayList.clear();

                List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                Map<String, Object> read = new HashMap<>();
                for(DocumentSnapshot docSnap : documentSnapshots){
                    if(! docSnap.getId().equals("AA")){
                        read = docSnap.getData();

                        Services services = new Services(
                                (String) read.get("role"),
                                (String) read.get("service"),
                                (String) read.get("rate")
                        );
                        services.setDocumentID(docSnap.getId());

                        arrayList.add(services);
                    }
                }

                adapter.notifyDataSetChanged();
            }
        });
    }

    public void navigateBack(){
        Intent intent = new Intent(AddServices.this, DisplayEmployee.class);
        intent.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(intent);
    }
}
