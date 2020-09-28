package com.example.clinicservicesapp.EmployeeFeatures;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.clinicservicesapp.Helpers.Auxiliary;
import com.example.clinicservicesapp.Models.ClinicHours;
import com.example.clinicservicesapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;

/*
 Nice to have:features

 ONLY allows different usernames when creating accounts

 let user know what went wrong when signing up or whenever a text field is invalid

NOTE: the returned 'time' by default is "" empty string

**/

public class WorkingHour extends AppCompatActivity {

    private Button btn_add;
    private LinearLayout row1,row2,row3,row4,row5,row6,row7;
    private Switch s1,s2,s3;

    private Button delete1,delete2,delete3,delete4,delete5,delete6,delete7;
    private Button save;

    private TextView from1,from2,from3,from4,from5,from6,from7;
    private TextView to1,to2,to3,to4,to5,to6,to7;

    private String clinicId;
    private String workingHoursId;
    private boolean changesToHours;
    private Bundle bundle;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_work_hours);
        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();

        if(getIntent().getBundleExtra("information") != null){
            bundle = getIntent().getBundleExtra("information");
        }

        btn_add = (Button) findViewById(R.id.btn_add);
        row1 = (LinearLayout) findViewById(R.id.row1);
        row2 = (LinearLayout) findViewById(R.id.row2);
        row3 = (LinearLayout) findViewById(R.id.row3);
        row4 = (LinearLayout) findViewById(R.id.row4);
        row5 = (LinearLayout) findViewById(R.id.row5);
        row6 = (LinearLayout) findViewById(R.id.row6);
        row7 = (LinearLayout) findViewById(R.id.row7);

        from1 = (EditText) findViewById(R.id.from_time1);
        from2 = (EditText) findViewById(R.id.from_time2);
        from3 = (EditText) findViewById(R.id.from_time3);
        from4 = (EditText) findViewById(R.id.from_time4);
        from5 = (EditText) findViewById(R.id.from_time5);
        from6 = (EditText) findViewById(R.id.from_time6);
        from7 = (EditText) findViewById(R.id.from_time7);

        to1 = (EditText) findViewById(R.id.to_time1);
        to2 = (EditText) findViewById(R.id.to_time2);
        to3 = (EditText) findViewById(R.id.to_time3);
        to4 = (EditText) findViewById(R.id.to_time4);
        to5 = (EditText) findViewById(R.id.to_time5);
        to6 = (EditText) findViewById(R.id.to_time6);
        to7 = (EditText) findViewById(R.id.to_time7);

        s1 = (Switch) findViewById(R.id.switch1);
        s2 = (Switch) findViewById(R.id.switch2);
        s3 = (Switch) findViewById(R.id.switch3);

        save = (Button) findViewById(R.id.btn_save);
        delete1 = (Button) findViewById(R.id.delete1);
        delete2 = (Button) findViewById(R.id.delete2);
        delete3 = (Button) findViewById(R.id.delete3);
        delete4 = (Button) findViewById(R.id.delete4);
        delete5 =(Button) findViewById(R.id.delete5);
        delete6 = (Button) findViewById(R.id.delete6);
        delete7 = (Button) findViewById(R.id.delete7);


        delete1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row1.setVisibility(View.GONE);
                from1.setText("");
                to1.setText("");
            }
        });

        delete2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row2.setVisibility(View.GONE);
                from2.setText("");
                to2.setText("");
            }
        });

        delete3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row3.setVisibility(View.GONE);
                from3.setText("");
                to3.setText("");
            }

        });

        delete4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row4.setVisibility(View.GONE);
                from4.setText("");
                to4.setText("");
            }
        });

        delete5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row5.setVisibility(View.GONE);
                from5.setText("");
                to5.setText("");
            }
        });

        delete6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                row6.setVisibility(View.GONE);
                from6.setText("");
                to6.setText("");
            }
        });

        delete7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                row7.setVisibility(View.GONE);
                from7.setText("");
                to7.setText("");
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBtnPressed();
            }
        });

        s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    row6.setVisibility(View.VISIBLE);
                    row7.setVisibility(View.VISIBLE);


                }
                else {
                    row6.setVisibility(View.GONE);
                    delete6.performClick();
                    row7.setVisibility(View.GONE);
                    delete7.performClick();

                }
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addBtnPressed();
            }
        });
        s2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                printError(from1,to1);
                if(isChecked && row1.isShown()&& (Auxiliary.validTime(from1.getText().toString())&& Auxiliary.validTime(to1.getText().toString()))){



                    if(row2.isShown()){
                        from2.setText(from1.getText().toString());
                        to2.setText(to1.getText().toString());

                    }
                    if(row3.isShown()){
                        from3.setText(from1.getText().toString());
                        to3.setText(to1.getText().toString());

                    }
                    if(row4.isShown()){
                        from4.setText(from1.getText().toString());
                        to4.setText(to1.getText().toString());

                    }
                    if(row5.isShown()){
                        from5.setText(from1.getText().toString());
                        to5.setText(to1.getText().toString());

                    }
                    if(row6.isShown()){
                        from6.setText(from1.getText().toString());
                        to6.setText(to1.getText().toString());
                    }
                    if(row7.isShown()){
                        from7.setText(from1.getText().toString());
                        to7.setText(to1.getText().toString());
                    }

                }
            }
        });




        s3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    row1.setVisibility(View.VISIBLE);
                    row2.setVisibility(View.VISIBLE);
                    row3.setVisibility(View.VISIBLE);
                    row4.setVisibility(View.VISIBLE);
                    row5.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        db.collection("Employee")
                .document(bundle.getString("docId"))
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        clinicId = documentSnapshot.get("clinicID").toString();

                        db.collection("Clinic")
                                .document(clinicId)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        workingHoursId = documentSnapshot.get("hours").toString();

                                        if(! workingHoursId.equals("")){
                                            //Toast.makeText(ClinicHours.this, workingHoursId, Toast.LENGTH_LONG).show();
                                            getWorkingHours();
                                        }else{
                                            Toast.makeText(WorkingHour.this, "No Working Hours Set", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(WorkingHour.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WorkingHour.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(WorkingHour.this, DisplayEmployee.class);
        i.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(i);
    }

    public void getWorkingHours(){
        db.collection("Working Hours")
                .document(workingHoursId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> read = new HashMap<>();
                        read = documentSnapshot.getData();

                        Map<String, Object> day = (Map<String, Object>) read.get("monday");
                        if(checkMap(day)){
                            row1.setVisibility(View.VISIBLE);
                            from1.setText(day.get("from").toString());
                            to1.setText(day.get("to").toString());
                        }else{
                            row1.setVisibility(View.GONE);
                        }

                        day = (Map<String, Object>) read.get("tuesday");
                        if(checkMap(day)){
                            row2.setVisibility(View.VISIBLE);
                            from2.setText(day.get("from").toString()); to2.setText(day.get("to").toString());
                        }else{
                            row2.setVisibility(View.GONE);
                        }

                        day = (Map<String, Object>) read.get("wednesday");
                        if(checkMap(day)){
                            row3.setVisibility(View.VISIBLE);
                            from3.setText(day.get("from").toString()); to3.setText(day.get("to").toString());
                        }else{
                            row3.setVisibility(View.GONE);
                        }

                        day = (Map<String, Object>) read.get("thursday");
                        if(checkMap(day)){
                            row4.setVisibility(View.VISIBLE);
                            from4.setText(day.get("from").toString()); to4.setText(day.get("to").toString());
                        }else{
                            row4.setVisibility(View.GONE);
                        }

                        day = (Map<String, Object>) read.get("friday");
                        if(checkMap(day)){
                            row5.setVisibility(View.VISIBLE);
                            from5.setText(day.get("from").toString()); to5.setText(day.get("to").toString());
                        }else{
                            row4.setVisibility(View.GONE);
                        }

                        day = (Map<String, Object>) read.get("saturday");
                        if(checkMap(day)){
                            row6.setVisibility(View.VISIBLE);
                            from6.setText(day.get("from").toString()); to6.setText(day.get("to").toString());
                        }else{
                            row6.setVisibility(View.GONE);
                        }

                        day = (Map<String, Object>) read.get("sunday");
                        if(checkMap(day)){
                            row7.setVisibility(View.VISIBLE);
                            from7.setText(day.get("from").toString()); to7.setText(day.get("to").toString());
                        }else{
                            row7.setVisibility(View.GONE);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    private void saveBtnPressed(){
        if(allPassed()){

            Map<String, Object> monday = new HashMap<>();
            monday.put("from", from1.getText().toString().trim());
            monday.put("to", to1.getText().toString().trim());
            Map<String, Object> tuesday = new HashMap<>();
            tuesday.put("from", from2.getText().toString().trim());
            tuesday.put("to", to2.getText().toString().trim());
            Map<String, Object> wednesday = new HashMap<>();
            wednesday.put("from", from3.getText().toString().trim());
            wednesday.put("to", to3.getText().toString().trim());
            Map<String, Object> thursday = new HashMap<>();
            thursday.put("from", from4.getText().toString().trim());
            thursday.put("to", to4.getText().toString().trim());
            Map<String, Object> friday = new HashMap<>();
            friday.put("from", from5.getText().toString().trim());
            friday.put("to", to5.getText().toString().trim());
            Map<String, Object> saturday = new HashMap<>();
            saturday.put("from", from6.getText().toString().trim());
            saturday.put("to", to6.getText().toString().trim());
            Map<String, Object> sunday = new HashMap<>();
            sunday.put("from", from7.getText().toString().trim());
            sunday.put("to", to7.getText().toString().trim());

            ClinicHours workingHours = new ClinicHours(
                    monday, tuesday, wednesday, thursday, friday, saturday, sunday
            );

            if(workingHoursId != null && ! workingHoursId.equals("")){

                Log.d("Working Hours Id", "Working Hours need updating");
                //Toast.makeText(WorkingHours.this, "Working Hours updating", Toast.LENGTH_SHORT).show();

                workingHours.setDocID(workingHoursId);

                db.collection("Working Hours")
                        .document(workingHoursId)
                        .set(workingHours.getWeekList())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(WorkingHour.this, "Hours Updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(WorkingHour.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }else{
                Log.d("Working Hours", "Working Hours is not set");

                workingHoursId = db.collection("Working Hours").document().getId();

                workingHours.setDocID(workingHoursId);

                Log.d("ClinicID", clinicId);
                Log.d("WorkingHoursId", workingHoursId);

                db.collection("Working Hours")
                        .document(workingHoursId)
                        .set(workingHours.getWeekList())
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(WorkingHour.this, "Working Hours Updated", Toast.LENGTH_SHORT).show();

                                db.collection("Clinic")
                                        .document(clinicId)
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Map<String, Object> update = documentSnapshot.getData();
                                                update.put("hours", workingHoursId);

                                                db.collection("Clinic")
                                                        .document(clinicId)
                                                        .set(update)
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(WorkingHour.this, "Clinic Updated", Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Toast.makeText(WorkingHour.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        });
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(WorkingHour.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(WorkingHour.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        }
    }

    private boolean checkMap(Map<String, Object> day){
        return ! (day.get("from").equals("") && day.get("to").equals(""));
    }

    public void addBtnPressed(){
        if(!row1.isShown()){
            row1.setVisibility(View.VISIBLE);
        }
        else if(!row2.isShown()){
            row2.setVisibility((View.VISIBLE));
        }
        else if(!row3.isShown()){
            row3.setVisibility(View.VISIBLE);
        }
        else if(!row4.isShown()){
            row4.setVisibility((View.VISIBLE));
        }
        else if(!row5.isShown()){
            row5.setVisibility(View.VISIBLE);
        }
        else if(!row6.isShown() && ! s2.isChecked()){
            row6.setVisibility((View.VISIBLE));
        }
        else if(!row7.isShown() && ! s2.isChecked()) {
            row7.setVisibility(View.VISIBLE);
        }

    }


    public boolean allPassed(){

            if(row1.isShown()&& (!Auxiliary.validTime(from1.getText().toString())||!Auxiliary.validTime(to1.getText().toString()))){
               printError(from1,to1);

              System.out.println(from1.toString() );
               System.out.println(to1.toString());
                return false;

            }
            if(row2.isShown()&&(!Auxiliary.validTime(from2.getText().toString()) || !Auxiliary.validTime(to2.getText().toString()))){

                printError(from2,to2);

                return false;

            }
            if(row3.isShown()&&(!Auxiliary.validTime(from3.getText().toString())||!Auxiliary.validTime(to3.getText().toString()))){
                printError(from3,to3);

                return false;

            }
            if(row4.isShown()&&(!Auxiliary.validTime(from4.getText().toString())||!Auxiliary.validTime(to4.getText().toString()))){
                printError(from4,to4 );

                return false;

            }
            if(row5.isShown()&&(!Auxiliary.validTime(from5.getText().toString())||!Auxiliary.validTime(to5.getText().toString()))){
                printError(from5,to5);

                return false;

            }
            if(row6.isShown()&&(!Auxiliary.validTime(from6.getText().toString())||!Auxiliary.validTime(to6.getText().toString()))){
                printError(from6,to6);

                return false;

            }
            if(row7.isShown()&&(!Auxiliary.validTime(from7.getText().toString())||!Auxiliary.validTime(to7.getText().toString()))){
                printError(from7,to7);

                return false;
            }
        return true;

    }


    private void printError(TextView aa, TextView bb){

        if(!Auxiliary.validTime(aa.getText().toString())){
            aa.setError("Valid time format-> hh:mm:ss");
        }
        if(!Auxiliary.validTime(bb.getText().toString())){
            bb.setError("Valid time format-> hh:mm:ss");
        }
    }


}
