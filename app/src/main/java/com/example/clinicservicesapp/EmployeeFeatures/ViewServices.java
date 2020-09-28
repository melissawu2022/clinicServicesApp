package com.example.clinicservicesapp.EmployeeFeatures;

import androidx.annotation.NonNull;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewServices extends AppCompatActivity {

    private ArrayList<Services> arrayList;
    private ArrayAdapter<Services> adapter;
    private ListView lstServices;
    private FirebaseFirestore db;

    private Bundle bundle;
    private String clinicId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_services);
        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();

        if(getIntent().getBundleExtra("information") != null){
            bundle = getIntent().getBundleExtra("information");
        }

        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(ViewServices.this, android.R.layout.simple_list_item_1, arrayList);
        lstServices = findViewById(R.id.lstServices);
        lstServices.setAdapter(adapter);

        lstServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final Services services = arrayList.get(position);
                db.collection("Clinic")
                        .document(clinicId)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Map<String, Object> update = documentSnapshot.getData();
                                List<String> lstServices = (List<String>)update.get("services");

                                lstServices.remove(services.getDocumentID());

                                update.put("services", lstServices);


                                db.collection("Clinic")
                                        .document(clinicId)
                                        .set(update)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ViewServices.this, "Service Deleted", Toast.LENGTH_SHORT).show();
                                                refresh();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ViewServices.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                refresh();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(ViewServices.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        refresh();
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(ViewServices.this, DisplayEmployee.class);
        i.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(i);
    }

    public void refresh(){
        arrayList.clear();
        adapter.clear();
        db.collection("Employee")
                .document(bundle.getString("docId"))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        clinicId = documentSnapshot.get("clinicID").toString();
                        Log.d("CLINC_ID", clinicId);

                        db.collection("Clinic")
                                .document(clinicId)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                                        Map<String, Object> read = documentSnapshot.getData();
                                        Log.d("Read Map", read.toString());
                                        List<String> list = (List<String>) read.get("services");
                                        Log.d("Read Service List", list.toString());

                                        for(final String docs : list){
                                            db.collection("Operations")
                                                    .document(docs)
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            Map<String, Object> read_serv = documentSnapshot.getData();
                                                            Log.d("Reading Services", read_serv.toString());
                                                            Services services = new Services(
                                                                    (String) read_serv.get("role"),
                                                                    (String) read_serv.get("service"),
                                                                    (String) read_serv.get("rate")
                                                            );
                                                            services.setDocumentID(docs);

                                                            Log.d("Adding List", "Adding to ArrayList");
                                                            arrayList.add(services);
                                                            Log.d("ArrayList Contents", arrayList.toString());
                                                            adapter.notifyDataSetChanged();
                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                        }
                                                    });
                                        }
                                        Log.d("Notify Adapter", "Notifying Adapter");
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
}
