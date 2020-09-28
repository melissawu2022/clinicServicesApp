package com.example.clinicservicesapp.EmployeeFeatures;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.clinicservicesapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class DisplayHours extends AppCompatActivity {

    private TextView from1, from2, from3, from4, from5, from6, from7;
    private TextView to1, to2, to3, to4, to5, to6, to7;

    private FirebaseFirestore db;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_hours);

        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();

        if(getIntent().getBundleExtra("information") != null) {
            bundle = getIntent().getBundleExtra("information");
        }

        from1 = findViewById(R.id.startTV1);
        from2 = findViewById(R.id.startTV2);
        from3 = findViewById(R.id.startTV3);
        from4 = findViewById(R.id.startTV4);
        from5 = findViewById(R.id.startTV5);
        from6 = findViewById(R.id.startTV6);
        from7 = findViewById(R.id.startTV7);

        to1 = findViewById(R.id.endTV1);
        to2 = findViewById(R.id.endTV2);
        to3 = findViewById(R.id.endTV3);
        to4 = findViewById(R.id.endTV4);
        to5 = findViewById(R.id.endTV5);
        to6 = findViewById(R.id.endTV6);
        to7 = findViewById(R.id.endTV7);

        db.collection("Employee")
                .document(bundle.getString("docId"))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String clinicId = documentSnapshot.get("clinicID").toString();

                        db.collection("Clinic")
                                .document(clinicId)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        String hoursId = documentSnapshot.get("hours").toString();

                                        db.collection("Working Hours")
                                                .document(hoursId)
                                                .get()
                                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                    @Override
                                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                        Log.d("DocSnap", documentSnapshot.toString());

                                                        Map<String, Object> read = documentSnapshot.getData();

                                                        Map<String, Object> day = (Map<String, Object>) read.get("monday");
                                                        if(checkMap(day)){
                                                            from1.setText(day.get("from").toString() + "AM");
                                                            to1.setText(day.get("to").toString() + "PM");
                                                        }

                                                        day = (Map<String, Object>) read.get("tuesday");
                                                        if(checkMap(day)){
                                                            from2.setText(day.get("from").toString() + "AM");
                                                            to2.setText(day.get("to").toString() + "PM");
                                                        }

                                                        day = (Map<String, Object>) read.get("wednesday");
                                                        if(checkMap(day)){
                                                            from3.setText(day.get("from").toString() + "AM");
                                                            to3.setText(day.get("to").toString() + "PM");
                                                        }

                                                        day = (Map<String, Object>) read.get("thursday");
                                                        if(checkMap(day)){
                                                            from4.setText(day.get("from").toString() + "AM");
                                                            to4.setText(day.get("to").toString() + "PM");
                                                        }

                                                        day = (Map<String, Object>) read.get("friday");
                                                        if(checkMap(day)){
                                                            from5.setText(day.get("from").toString() + "AM");
                                                            to5.setText(day.get("to").toString() + "PM");
                                                        }

                                                        day = (Map<String, Object>) read.get("saturday");
                                                        if(checkMap(day)){
                                                            from6.setText(day.get("from").toString() + "AM");
                                                            to6.setText(day.get("to").toString() + "PM");
                                                        }

                                                        day = (Map<String, Object>) read.get("sunday");
                                                        if(checkMap(day)){
                                                            from7.setText(day.get("from").toString() + "AM");
                                                            to7.setText(day.get("to").toString() + "PM");
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

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private boolean checkMap(Map<String, Object> day){
        return ! (day.get("from").equals("") && day.get("to").equals(""));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(DisplayHours.this, DisplayEmployee.class);
        intent.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(intent);
    }
}
