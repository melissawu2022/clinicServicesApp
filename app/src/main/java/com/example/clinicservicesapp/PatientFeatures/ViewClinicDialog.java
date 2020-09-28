package com.example.clinicservicesapp.PatientFeatures;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.clinicservicesapp.Models.ClinicHours;
import com.example.clinicservicesapp.Models.Services;
import com.example.clinicservicesapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ViewClinicDialog extends AppCompatActivity {

    private ListView clinicsServices;
    private TextView clinicsName;
    private TextView showHours;
    private ArrayAdapter<Services> adapter;
    private ArrayList<Services> arrayList;
    private Spinner daySelection;
    private Button makeAppointment;
    private FirebaseFirestore db;
    private Bundle bundle;
    private ClinicHours clinicHours;
    private Button Rating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view__clinic);
        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();

        Rating = (Button) findViewById(R.id.Rating);
        Rating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //opens rating page
                Intent i = new Intent(ViewClinicDialog.this,RatingClinic.class);
                Bundle extraBundle = getIntent().getBundleExtra("information");
                i.putExtra("information", extraBundle);
                finish();
                startActivity(i);
            }
        });

        if(getIntent().getBundleExtra("information") != null){
            bundle = getIntent().getBundleExtra("information");
        }

        clinicsServices = findViewById(R.id.servicesClinic);
        clinicsName = findViewById(R.id.textClinicName);
        showHours = findViewById(R.id.txtHours);
        arrayList = new ArrayList<>();
        adapter = new ArrayAdapter<>(ViewClinicDialog.this, android.R.layout.simple_list_item_1, arrayList);
        clinicsServices.setAdapter(adapter);


        Spinner daySelection = (Spinner)findViewById(R.id.spinner4);
        ArrayAdapter<String> myAdapter4 = new ArrayAdapter<String>(ViewClinicDialog.this,android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Days));

        myAdapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySelection.setAdapter(myAdapter4);

        daySelection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                Map<String, Object> day;
                if (pos == 1){
                    showHours.setHint("Monday: ");
                    if(clinicHours != null) {
                        day = clinicHours.getDay("monday");

                        showHours.setText(day.get("from") + " to " + day.get("to"));
                    }
                }
                else if (pos == 2){
                    showHours.setHint("Tues: ");
                    if(clinicHours != null) {
                        day = clinicHours.getDay("tuesday");

                        showHours.setText(day.get("from") + " to " + day.get("to"));
                    }
                }
                else if (pos == 3){
                    showHours.setHint("Wed: ");
                    if(clinicHours != null) {
                        day = clinicHours.getDay("wednesday");

                        showHours.setText(day.get("from") + " to " + day.get("to"));
                    }
                }
                else if (pos == 4){
                    showHours.setHint("Thurs: ");
                    if(clinicHours != null) {
                        day = clinicHours.getDay("thursday");

                        showHours.setText(day.get("from") + " to " + day.get("to"));
                    }
                }
                else if (pos == 5){
                    showHours.setHint("Fri: ");
                    if(clinicHours != null) {
                        day = clinicHours.getDay("friday");

                        showHours.setText(day.get("from") + " to " + day.get("to"));
                    }
                }
                else if (pos == 6){
                    showHours.setHint("Sat: ");
                    if(clinicHours != null) {
                        day = clinicHours.getDay("saturday");

                        showHours.setText(day.get("from") + " to " + day.get("to"));
                    }
                }
                else if (pos == 7){
                    showHours.setHint("Sun: ");
                    if(clinicHours != null) {
                        day = clinicHours.getDay("sunday");

                        showHours.setText(day.get("from") + " to " + day.get("to"));
                    }
                }
                else if (pos == 8){
                    showHours.setHint("Select A Day: ");
                }

            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        makeAppointment = (Button) findViewById(R.id.btn_patient_view_service_appointment);
        makeAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appointmentPopup();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();

        db.collection("Clinic")
                .document(bundle.getString("clinicId"))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        final String employeeId = documentSnapshot.get("employeeId").toString();
                        final String workingHoursId = documentSnapshot.get("hours").toString();
                        final List<String> services = (List<String>) documentSnapshot.get("services");

                        db.collection("Employee").document(employeeId)
                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String profile = documentSnapshot.get("profile").toString();

                                db.collection("Profile").document(profile)
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String clinicName = documentSnapshot.get("NameClinic").toString();

                                        clinicsName.setText(clinicName);
                                    }
                                });
                            }
                        });

                        if(! workingHoursId.equals("")){
                            db.collection("Working Hours").document(workingHoursId)
                                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    Map<String, Object> read = documentSnapshot.getData();

                                    clinicHours = new ClinicHours(read);
                                }
                            });
                        }else{
                            showHours.setText("Clinic Has No Working Hours Currently");
                        }

                        if(services.size() != 0){
                            for(String str : services){
                                db.collection("Operations").document(str)
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Services services1 = new Services(
                                                (String) documentSnapshot.get("role"),
                                                (String) documentSnapshot.get("service"),
                                                (String) documentSnapshot.get("rate")
                                        );

                                        arrayList.add(services1);
                                        adapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    }
                });
    }

    public void onBackPressed() {

        Intent i = new Intent(ViewClinicDialog.this, SearchServices.class);
        Bundle extraBundle = getIntent().getBundleExtra("information");
        extraBundle.remove("clinicId");
        extraBundle.remove("clinicName");
        i.putExtra("information", extraBundle);
        finish();
        startActivity(i);
    }

    private void appointmentPopup(){
        Intent i = new Intent(ViewClinicDialog.this,BookAppointment.class);
        i.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(i);


    }


}
