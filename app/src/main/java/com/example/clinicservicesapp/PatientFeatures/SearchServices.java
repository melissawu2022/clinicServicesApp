package com.example.clinicservicesapp.PatientFeatures;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.example.clinicservicesapp.Helpers.Auxiliary;
import com.example.clinicservicesapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SearchServices extends AppCompatActivity {

    private Spinner searchOptions;
    private Button searchComplete;
    private EditText searchTxt;
    private ListView services;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> arrayList;
    private FirebaseFirestore db;
    private ArrayList<String> clinicID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search__services);
        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();

        searchComplete = (Button)findViewById(R.id.btnSearch);
        searchTxt = findViewById(R.id.editSearch);

        services= (ListView) findViewById(R.id.searchedServices);
        arrayList = new ArrayList<String>(); clinicID = new ArrayList<>();
        adapter = new ArrayAdapter<>(SearchServices.this, android.R.layout.simple_list_item_1,arrayList);
        services.setAdapter(adapter);

        final Spinner searchOptions = (Spinner)findViewById(R.id.spinner3);
        ArrayAdapter<String> myAdapter3 = new ArrayAdapter<String>(SearchServices.this,android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.SearchType));

        myAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        searchOptions.setAdapter(myAdapter3);

        searchOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {

                if (pos == 1){
                    searchTxt.setHint("123 Example St");
                }
                else if (pos == 2){
                    searchTxt.setHint("8h00-10h00");
                }
                else if (pos == 3){
                    searchTxt.setHint("Urine Test");
                }
                else if (pos == 4){
                    searchTxt.setHint("Clinic Name");
                }

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        services.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AlertDialog.Builder search_dialog = new AlertDialog.Builder(SearchServices.this);
                search_dialog.setMessage("View Clinic or Book Appointment");
                search_dialog.setCancelable(true);
                final String idLink = clinicID.get(position);
                final String nameLink = arrayList.get(position);
                Log.d("Clinic ID Linked", idLink);
                Log.d("Clinic Name Linked", nameLink);
                search_dialog.setPositiveButton("Book Appointment",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Takes to book Appointment.xml, not implemented yet
                                bookAppointment(idLink, nameLink);
                            }
                        });

                search_dialog.setNegativeButton(
                        "View Clinic",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                viewClinic(idLink, nameLink);
                            }
                        });

                AlertDialog alert11 = search_dialog.create();
                alert11.show();
            }
        });

        searchComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String search = searchTxt.getText().toString().trim();

                String option = searchOptions.getSelectedItem().toString().trim();
                int position = searchOptions.getSelectedItemPosition();


                boolean checkOption = false;
                boolean checkSearch = false;

                if (position == 2 && !Auxiliary.validHours(search)){
                    searchTxt.setError("Invalid Time Format");
                    return;
                }
                else {
                    checkSearch = true;
                }

                if (position == 1 && Auxiliary.checkEmpty(search) || !Auxiliary.validAddress(search)){
                    searchTxt.setError("Enter valid text to search.");
                    return;
                }
                else {
                    checkSearch = true;
                }

                if (Auxiliary.checkEmpty(option)) {
                    TextView errorText = (TextView) searchOptions.getSelectedView();
                    errorText.setError("Select an Option");
                } else {
                    checkOption = true;
                }

                if( checkOption && checkSearch){
                    searchResult(position, search);
                }




            }
        });
    }


    public void onBackPressed() {

        Intent i = new Intent(SearchServices.this, DisplayPatient.class);
        i.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(i);

        //Toast.makeText(DisplayPatient.this, "BackButton Pressed", Toast.LENGTH_SHORT).show();
    }

    public void viewClinic(String clinicId, String clinicName) {

        Intent i = new Intent(SearchServices.this, ViewClinicDialog.class);
        Bundle sendBundle = getIntent().getBundleExtra("information");
        sendBundle.putString("clinicId", clinicId);
        sendBundle.putString("clinicName", clinicName);
        i.putExtra("information", sendBundle);
        finish();
        startActivity(i);

    }

    public void bookAppointment(String clinicId, String clinicName) {

        //Need to change this to Book Appointments xml
        Intent i = new Intent(SearchServices.this, BookAppointment.class);
        Bundle extraBundle = getIntent().getBundleExtra("information");
        extraBundle.putString("clinicId", clinicId);
        extraBundle.putString("clinicName", clinicName);
        i.putExtra("information", extraBundle);
        startActivity(i);

    }

    public void searchResult(int Type, final String SearchText){
        searchComplete.setEnabled(false);
        if (Type == 1){
            //Address
            String toast = "Search Complete!";

            arrayList.clear(); clinicID.clear();
            adapter.notifyDataSetChanged();

            db.collection("Employee").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> emps = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot snapshot: emps){
                        if(!snapshot.getId().equals("AA")){
                            String profile = snapshot.get("profile").toString();
                            Log.d("Profile IDS",profile);
                            final String clinicId = snapshot.get("clinicID").toString();

                            if(!profile.equals("")){
                                db.collection("Profile").document(profile).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String addy = documentSnapshot.get("StreetAddress").toString().toLowerCase();
                                        String post = documentSnapshot.get("PostalCode").toString().toLowerCase();

                                        if (addy.contains(SearchText.toLowerCase()) || post.contains(SearchText.toLowerCase())){
                                            clinicID.add(clinicId);
                                            arrayList.add(documentSnapshot.get("NameClinic").toString());
                                            adapter.notifyDataSetChanged();
                                        }


                                    }
                                });
                            }
                        }

                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    searchComplete.setEnabled(true);
                }
            });


            Toast.makeText(SearchServices.this,"Search Complete!", Toast.LENGTH_SHORT).show();
        }

        else if (Type == 2){
            //Working Hours
            arrayList.clear(); clinicID.clear();
            adapter.notifyDataSetChanged();

            db.collection("Employee").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> emps = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot snapshot: emps){
                        if(!snapshot.getId().equals("AA")){
                            final String clinic = snapshot.get("clinicID").toString();
                            final String profile = snapshot.get("profile").toString();
                            Log.d("Clinic IDS",clinic);


                            if(!clinic.equals("")){
                                db.collection("Clinic").document(clinic).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String hours = documentSnapshot.get("hours").toString();

                                        if(! hours.equals("")){
                                            db.collection("Working Hours").document(hours).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                    String toSearch = SearchText.replace('h', ':');
                                                    Map<String, Object> read = documentSnapshot.getData();
                                                    Log.d("Working Hour Map", read.toString());
                                                    String[] split = toSearch.split("-"); boolean found = false;
                                                    split[0] += ":00"; split[1] += ":00";
                                                    Map<String, Object> day = (Map<String, Object>) read.get("monday");
                                                    if(day.get("from").toString().equals(split[0]) && day.get("to").equals(split[1])){
                                                        found = true;
                                                    }
                                                    day = (Map<String, Object>) read.get("tuesday");
                                                    if(day.get("from").toString().equals(split[0]) && day.get("to").equals(split[1])){
                                                        found = true;
                                                    }
                                                    day = (Map<String, Object>) read.get("wednesday");
                                                    if(day.get("from").toString().equals(split[0]) && day.get("to").equals(split[1])){
                                                        found = true;
                                                    }
                                                    day = (Map<String, Object>) read.get("thursday");
                                                    if(day.get("from").toString().equals(split[0]) && day.get("to").equals(split[1])){
                                                        found = true;
                                                    }
                                                    day = (Map<String, Object>) read.get("friday");
                                                    if(day.get("from").toString().equals(split[0]) && day.get("to").equals(split[1])){
                                                        found = true;
                                                    }
                                                    day = (Map<String, Object>) read.get("saturday");
                                                    if(day.get("from").toString().equals(split[0]) && day.get("to").equals(split[1])){
                                                        found = true;
                                                    }
                                                    day = (Map<String, Object>) read.get("sunday");
                                                    if(day.get("from").toString().equals(split[0]) && day.get("to").equals(split[1])){
                                                        found = true;
                                                    }

                                                    Log.d("Found Time", String.valueOf(found));

                                                    if(found){
                                                        clinicID.add(clinic);
                                                        db.collection("Profile").document(profile)
                                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                arrayList.add(documentSnapshot.get("NameClinic").toString());
                                                                adapter.notifyDataSetChanged();
                                                            }
                                                        });
                                                    }

                                                }
                                            });
                                        }

                                    }
                                });
                            }
                        }

                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    searchComplete.setEnabled(true);
                }
            });

            Toast.makeText(SearchServices.this, "Search Complete", Toast.LENGTH_SHORT).show();
        }

        else if (Type == 3){
            //Type of Service
            arrayList.clear(); clinicID.clear();
            adapter.notifyDataSetChanged();


            db.collection("Employee").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<DocumentSnapshot> emps = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot snapshot: emps){
                        if(!snapshot.getId().equals("AA")){
                            final String clinic = snapshot.get("clinicID").toString();
                            final String profile = snapshot.get("profile").toString();
                            Log.d("Clinic IDS",clinic);


                            if(!clinic.equals("")){
                                db.collection("Clinic").document(clinic).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        List<String> services = (List<String>)documentSnapshot.get("services");

                                        for (int i = 0; i<services.size(); i++){
                                            db.collection("Operations").document(services.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                @Override
                                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                    String serv = documentSnapshot.get("service").toString().toLowerCase();

                                                    if (serv.contains(SearchText.toLowerCase())){
                                                        db.collection("Profile")
                                                                .document(profile)
                                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                clinicID.add(clinic);
                                                                arrayList.add(documentSnapshot.get("NameClinic").toString());
                                                                adapter.notifyDataSetChanged();
                                                            }
                                                        });
                                                    }
                                                }
                                            });
                                        }

                                    }
                                });
                            }
                        }

                    }
                }
            }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    searchComplete.setEnabled(true);
                }
            });

            Toast.makeText(SearchServices.this, "Search Complete", Toast.LENGTH_SHORT).show();
        }
        else if (Type == 4){
            arrayList.clear(); clinicID.clear();
            adapter.clear();

            db.collection("Employee").get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            List<DocumentSnapshot> documentSnapshots = queryDocumentSnapshots.getDocuments();

                            for(DocumentSnapshot docSnap : documentSnapshots){
                                if(! docSnap.getId().equals("AA")){
                                    final String clinicId = docSnap.get("clinicID").toString();
                                    final String profileId = docSnap.get("profile").toString();

//                                    Log.d("EmployeeID Search", docSnap.getId());
//                                    Log.d("ClinicID Search", clinicId);
//                                    Log.d("ProfileID Search", profileId);
                                    if(! profileId.equals("")){

                                        db.collection("Profile").document(profileId)
                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                                Log.d("Snapshot Reading", documentSnapshot.toString());
                                                String name = documentSnapshot.get("NameClinic").toString().toLowerCase();
                                                if(name.contains(SearchText.toLowerCase())){
                                                    clinicID.add(clinicId);
                                                    arrayList.add(name);
                                                    adapter.notifyDataSetChanged();
                                                }
                                            }
                                        })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.d("Exception", e.getMessage());
                                                    }
                                                });

                                    }
                                }
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            searchComplete.setEnabled(true);
                        }
                    });
        }



    }
}
