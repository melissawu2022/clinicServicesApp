package com.example.clinicservicesapp.AdminFeatures;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.example.clinicservicesapp.Helpers.Auxiliary;
import com.example.clinicservicesapp.Models.Services;
import com.example.clinicservicesapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Admin extends AppCompatActivity {

    private EditText role;
    private EditText service_name;
    private EditText service_rate;
    private Button serviceBtn,backBtn;
    private ListView listv;


    private ArrayList<Services> arrayList;
    private ArrayAdapter<Services> adapter;

    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_service);
        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();

        role = (EditText) findViewById(R.id.roleService);
        service_name = (EditText) findViewById(R.id.serviceName);
        service_rate = (EditText) findViewById(R.id.serviceRate);
        backBtn = (Button) findViewById(R.id.back);
        serviceBtn = (Button) findViewById(R.id.serviceAdd);
        listv = (ListView) findViewById(R.id.serviceList);

        arrayList = new ArrayList<Services>();

        adapter = new ArrayAdapter<>(Admin.this, android.R.layout.simple_list_item_1, arrayList);

        listv.setAdapter(adapter);

        listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openDialog(arrayList.get(position), position);
            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        onBtnClick();
    }

    @Override
    protected void onStart() {
        super.onStart();

        refresh();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Admin.this, AdminControlPanel.class);
        intent.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(intent);
    }

    public void refresh(){
        db.collection("Operations")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                        arrayList.clear();

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

                                Log.d("Service", services.toString());

                                arrayList.add(services);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    public void onBtnClick(){
        serviceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             String role_result = role.getText().toString().trim();
             String service_result = service_name.getText().toString().trim();
             String serviceRate = service_rate.getText().toString().trim();

             boolean checkRole = false;
             boolean checkServName = false;
             boolean checkRate = false;
             if(Auxiliary.checkEmpty(role_result)){
                 role.setError("Need to put a value in");
             } else {
                 checkRole = true;
             }

             if(Auxiliary.checkEmpty(service_result)){
                 service_name.setError("Needs to put a value in");
             } else {
                 checkServName = true;
             }

                if(Auxiliary.checkEmpty(serviceRate)){
                    service_rate.setError("Needs to put a value in");
                } else {
                    checkRate = true;
                }



             if(checkRole && checkServName && checkRate){
                 String docId = db.collection("Operations").document().getId();

                 Services services = new Services(role_result, service_result, serviceRate);
                 services.setDocumentID(docId);

                 Map<String, Object> data = new HashMap<>();
                 data.put("role", services.getStaff());
                 data.put("service", services.getService());
                 data.put("rate", services.getRate());

                 db.collection("Operations")
                         .document(docId)
                         .set(data)
                         .addOnSuccessListener(new OnSuccessListener<Void>() {
                             @Override
                             public void onSuccess(Void aVoid) {
                                 Toast.makeText(Admin.this, "Service Added", Toast.LENGTH_LONG).show();
                                 role.setText("");
                                 service_name.setText("");
                                 refresh();

                             }
                         })
                         .addOnFailureListener(new OnFailureListener() {
                             @Override
                             public void onFailure(@NonNull Exception e) {
                                 Toast.makeText(Admin.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                 role.setText("");
                                 service_name.setText("");
                             }
                         });


             }

            }
        });
    }




    public void openDialog(final Services oldItem, final int index){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.edit_services_dialog, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Edit Services");
        final AlertDialog dialog = dialogBuilder.create();

        Log.i("DocID", oldItem.getDocumentID());

        ((TextView)dialogView.findViewById(R.id.editRole)).setText(oldItem.getStaff());
        ((TextView)dialogView.findViewById(R.id.editService)).setText(oldItem.getService());
        ((TextView)dialogView.findViewById(R.id.editRate)).setText(oldItem.getRate());

        Button set_Button = dialogView.findViewById(R.id.btnEdit);
        Button delete_Button = dialogView.findViewById(R.id.btnDelete);

        delete_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.remove(oldItem);
                db.collection("Operations")
                        .document(oldItem.getDocumentID())
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(Admin.this, "Service Deleted", Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                                refresh();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Admin.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                dialog.dismiss();
                            }
                        });
            }
        });

        set_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText editR = dialogView.findViewById(R.id.editRole);
                EditText editS = dialogView.findViewById(R.id.editService);
                EditText editRate = dialogView.findViewById(R.id.editRate);



                String role_result = editR.getText().toString().trim();
                String service_result = editS.getText().toString().trim();
                String rate_result = editRate.getText().toString().trim();

                boolean checkRate = false;
                boolean checkRole = false;
                boolean checkServName = false;

                if(Auxiliary.checkEmpty(role_result)){
                    editR.setError("Need to put a value in");
                } else {
                    checkRole = true;
                }

                if(Auxiliary.checkEmpty(service_result)){
                    editS.setError("Needs to put a value in");
                } else {
                    checkServName = true;
                }

                if(Auxiliary.checkEmpty(rate_result)){
                    editRate.setError("Need to put a value in");
                } else {
                    checkRate = true;

                }

                //ADD RATE OF SERVICE STARTING HERE
                if(checkRole && checkServName && checkRate){

                    oldItem.setStaff(role_result);
                    oldItem.setService(service_result);
                    oldItem.setRate(rate_result);

                    Map<String, Object> data = new HashMap<>();
                    data.put("role", oldItem.getStaff());
                    data.put("service", oldItem.getService());
                    data.put("rate", oldItem.getRate());

                    db.collection("Operations")
                            .document(oldItem.getDocumentID())
                            .set(data)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(Admin.this, "Service Updated", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    refresh();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Admin.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                }
                            });
                }
            }
        });

        dialog.show();
    }
}
