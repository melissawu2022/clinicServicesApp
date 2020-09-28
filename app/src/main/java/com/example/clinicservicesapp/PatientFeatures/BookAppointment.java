package com.example.clinicservicesapp.PatientFeatures;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.Gravity;
import android.widget.PopupWindow;

import com.example.clinicservicesapp.Helpers.Auxiliary;
import com.example.clinicservicesapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class BookAppointment extends AppCompatActivity {

    private Button date,time,save;
    private TextView textView_time,textView_date,textView_clinic;

    private FirebaseFirestore db;
    private Bundle bundle;

    @Override
    public  void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.make_appointment);

        db = FirebaseFirestore.getInstance();

        if(getIntent().getBundleExtra("information") != null){
            bundle = getIntent().getBundleExtra("information");
            Log.d("ClinicName Bundle", bundle.getString("clinicName"));

        }

        textView_time = (TextView) findViewById(R.id.textView_time);
        textView_date =(TextView) findViewById(R.id.textView_date);

        date =(Button) findViewById(R.id.btn_date);

        Calendar calendar =  Calendar.getInstance();
        SimpleDateFormat mdformat = new SimpleDateFormat("HH:mm:ss z");
        String t = mdformat.format(calendar.getTime());

        SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd");

        textView_time.setText(t);//set time
        textView_date.setText(format.format(calendar.getTime()));

        textView_clinic = findViewById(R.id.textView_Clinic);
        textView_clinic.setText(bundle.getString("clinicName"));
        textView_clinic.setEnabled(false);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupDate();
            }
        });
        time =(Button) findViewById(R.id.btn_time);
        save =(Button) findViewById(R.id.btn_appointment_save);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Map<String, Object> appointment = new HashMap<>();
                appointment.put("date", textView_date.getText().toString());
                appointment.put("time", textView_time.getText().toString());
                appointment.put("clinicName", textView_clinic.getText().toString());
                appointment.put("patientID", bundle.getString("docId"));
                appointment.put("clinicId", bundle.getString("clinicId"));

                Log.d("Appointment Map", appointment.toString());
                final String appointmentID = db.collection("Appointments")
                        .document().getId();
                db.collection("Appointments")
                        .document(appointmentID)
                        .set(appointment)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(BookAppointment.this, "Appointment Created", Toast.LENGTH_SHORT).show();
                                db.collection("Clinic")
                                        .document(bundle.getString("clinicId"))
                                        .get()
                                        .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                            @Override
                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                Toast.makeText(BookAppointment.this, "Calculating Waiting Time", Toast.LENGTH_SHORT).show();

                                                //Checks if the appointments attribute exists
                                                if(! documentSnapshot.contains("appointments")){
                                                    List<String> temp = new ArrayList<>(); temp.add(appointmentID);
                                                    Map<String, Object> update = documentSnapshot.getData();
                                                    update.put("appointments", temp);

                                                    //Sets the first appointment if the attribute doesn't exist
                                                    db.collection("Clinic")
                                                            .document(bundle.getString("clinicId"))
                                                            .set(update)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(BookAppointment.this, "Waiting Time is 0 minutes", Toast.LENGTH_SHORT).show();
                                                                }
                                                            });

                                                    //Gets the patient data to update the waiting time
                                                    db.collection("Patient")
                                                            .document(bundle.getString("docId"))
                                                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                            //If the waiting time attribute doesn't exist
                                                            if(! documentSnapshot.contains("waitingTime")){
                                                                Map<String, Object> update = documentSnapshot.getData();
                                                                update.put("waitingTime", 0);

                                                                //Makes new waitingTime attribute in database
                                                                db.collection("Patient")
                                                                        .document(bundle.getString("docId"))
                                                                        .set(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(BookAppointment.this, "Completed", Toast.LENGTH_SHORT).show();

                                                                        Bundle extraBundle = getIntent().getBundleExtra("information");
                                                                        extraBundle.remove("clinicId");
                                                                        extraBundle.remove("clinicName");

                                                                        Intent intent = new Intent(BookAppointment.this, SearchServices.class);
                                                                        intent.putExtra("information", extraBundle);
                                                                        finish();
                                                                        startActivity(intent);
                                                                    }
                                                                })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {

                                                                            }
                                                                        });
                                                            }else{
                                                                Map<String, Object> update = documentSnapshot.getData();
                                                                update.put("waitingTime", 0);

                                                                //Makes new waitingTime attribute in database
                                                                db.collection("Patient")
                                                                        .document(bundle.getString("docId"))
                                                                        .set(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(BookAppointment.this, "Completed", Toast.LENGTH_SHORT).show();

                                                                        Bundle extraBundle = getIntent().getBundleExtra("information");
                                                                        extraBundle.remove("clinicId");
                                                                        extraBundle.remove("clinicName");

                                                                        Intent intent = new Intent(BookAppointment.this, SearchServices.class);
                                                                        intent.putExtra("information", extraBundle);
                                                                        finish();
                                                                        startActivity(intent);
                                                                    }
                                                                })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {

                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {

                                                                }
                                                            });
                                                }else{

                                                    //Attributes exists -> get all current appointments
                                                    List<String> getAppointments = (List<String>)documentSnapshot.get("appointments");
                                                    if(getAppointments.size() == 0){
                                                        getAppointments.add(appointmentID);
                                                        Map<String, Object> update= documentSnapshot.getData();
                                                        update.put("appointments", getAppointments); //update with new appointments
                                                        db.collection("Clinic")
                                                                .document(bundle.getString("clinicId"))
                                                                .set(update)
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void aVoid) {
                                                                        Toast.makeText(BookAppointment.this, "Waiting Time is 0 minutes", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {

                                                                    }
                                                                });

                                                        db.collection("Patient")
                                                                .document(bundle.getString("docId"))
                                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                if(! documentSnapshot.contains("waitingTime")){
                                                                    Map<String, Object> update = documentSnapshot.getData();
                                                                    update.put("waitingTime", 0);

                                                                    db.collection("Patient")
                                                                            .document(bundle.getString("docId"))
                                                                            .set(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(BookAppointment.this, "Completed", Toast.LENGTH_SHORT).show();

                                                                            Bundle extraBundle = getIntent().getBundleExtra("information");
                                                                            extraBundle.remove("clinicId");
                                                                            extraBundle.remove("clinicName");

                                                                            Intent intent = new Intent(BookAppointment.this, SearchServices.class);
                                                                            intent.putExtra("information", extraBundle);
                                                                            finish();
                                                                            startActivity(intent);
                                                                        }
                                                                    })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {

                                                                                }
                                                                            });
                                                                }else{
                                                                    Map<String, Object> update = documentSnapshot.getData();
                                                                    update.put("waitingTime", 0);

                                                                    db.collection("Patient")
                                                                            .document(bundle.getString("docId"))
                                                                            .set(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(BookAppointment.this, "Completed", Toast.LENGTH_SHORT).show();

                                                                            Bundle extraBundle = getIntent().getBundleExtra("information");
                                                                            extraBundle.remove("clinicId");
                                                                            extraBundle.remove("clinicName");

                                                                            Intent intent = new Intent(BookAppointment.this, SearchServices.class);
                                                                            intent.putExtra("information", extraBundle);
                                                                            finish();
                                                                            startActivity(intent);
                                                                        }
                                                                    })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {

                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {

                                                                    }
                                                                });
                                                    }else{
                                                        int length = getAppointments.size();
                                                        final int waitingTime = length * 15;
                                                        getAppointments.add(appointmentID);
                                                        Map<String, Object> update = documentSnapshot.getData();
                                                        update.put("appointments", getAppointments);

                                                        db.collection("Clinic")
                                                                .document(bundle.getString("clinicId"))
                                                                .set(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void aVoid) {
                                                                Toast.makeText(BookAppointment.this, "Waiting Time is " + waitingTime + " minutes", Toast.LENGTH_SHORT).show();
                                                            }
                                                        })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {

                                                                    }
                                                                });

                                                        db.collection("Patient")
                                                                .document(bundle.getString("docId"))
                                                                .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                            @Override
                                                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                                                if(! documentSnapshot.contains("waitingTime")){
                                                                    Map<String, Object> update = documentSnapshot.getData();
                                                                    update.put("waitingTime", waitingTime);

                                                                    db.collection("Patient")
                                                                            .document(bundle.getString("docId"))
                                                                            .set(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(BookAppointment.this, "Completed", Toast.LENGTH_SHORT).show();

                                                                            Bundle extraBundle = getIntent().getBundleExtra("information");
                                                                            extraBundle.remove("clinicId");
                                                                            extraBundle.remove("clinicName");

                                                                            Intent intent = new Intent(BookAppointment.this, SearchServices.class);
                                                                            intent.putExtra("information", extraBundle);
                                                                            finish();
                                                                            startActivity(intent);
                                                                        }
                                                                    })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {

                                                                                }
                                                                            });
                                                                }else{
                                                                    Map<String, Object> update = documentSnapshot.getData();
                                                                    update.put("waitingTime", waitingTime);

                                                                    db.collection("Patient")
                                                                            .document(bundle.getString("docId"))
                                                                            .set(update).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                        @Override
                                                                        public void onSuccess(Void aVoid) {
                                                                            Toast.makeText(BookAppointment.this, "Completed", Toast.LENGTH_SHORT).show();

                                                                            Bundle extraBundle = getIntent().getBundleExtra("information");
                                                                            extraBundle.remove("clinicId");
                                                                            extraBundle.remove("clinicName");

                                                                            Intent intent = new Intent(BookAppointment.this, SearchServices.class);
                                                                            intent.putExtra("information", extraBundle);
                                                                            finish();
                                                                            startActivity(intent);
                                                                        }
                                                                    })
                                                                            .addOnFailureListener(new OnFailureListener() {
                                                                                @Override
                                                                                public void onFailure(@NonNull Exception e) {

                                                                                }
                                                                            });
                                                                }
                                                            }
                                                        })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {

                                                                    }
                                                                });
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
                        });

            }
        });


        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupTime();
            }
        });

    }

    @Override
    public void onBackPressed() {
        navigateBack();
    }

    public void navigateBack(){
        Bundle extraBundle = getIntent().getBundleExtra("information");
        Intent intent = new Intent(BookAppointment.this, SearchServices.class);
        intent.putExtra("information", extraBundle);
        finish();
        startActivity(intent);
    }



    private void showPopupDate(){


        LayoutInflater inflater = (LayoutInflater) BookAppointment.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final DatePicker myDatePicker;


        final View layout = inflater.inflate(R.layout.popup_date_picker,(ViewGroup) findViewById(R.id.popup2));

        final float density = BookAppointment.this.getResources().getDisplayMetrics().density;

        final PopupWindow window = new PopupWindow(layout,(int)density*480,(int)density*850,true);
        myDatePicker  = (DatePicker) layout.findViewById(R.id.popup2_date_picker);
        window.showAtLocation(findViewById(R.id.makeAppointment),Gravity.CENTER,0,0);

        Button cancel2 = (Button) layout.findViewById(R.id.btn_popup2_cancel);
        Button add2 = (Button) layout.findViewById(R.id.btn_popup2_add);

        cancel2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });


        add2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat f = new SimpleDateFormat("yyyy.MM.dd");

                int y = myDatePicker.getYear();
                int m = myDatePicker.getMonth() + 1;
                int d = myDatePicker.getDayOfMonth();
                String a = f.format( System.currentTimeMillis());
                String b = f.format(new Date(y, m, d));
                Log.d("Time", a);
                Log.d("Time B", b);

                Date date1 = new Date(y, m - 1, d);
                Log.d("Date date1", date1.toString());

                int y2 = Integer.valueOf(new String(new char[]{a.charAt(0),a.charAt(1),a.charAt(2),a.charAt(3)}));
                int m2 = Integer.valueOf(new String(new char[]{a.charAt(5),a.charAt(6)}));
                int d2 = Integer.valueOf(new String(new char[]{a.charAt(8),a.charAt(9)}));

                Date date2 = new Date(y2, m2 - 1, d2);
                Log.d("Date date2", date2.toString());

                Log.d("Using Date Class Before", String.valueOf(date1.before(date2)));


                System.out.println((y>=y2 && m >= m2 && d >= d2));

               if(date1.before(date2)){
                    window.dismiss();
                    textView_date.setText(myDatePicker.getYear() + "." + (myDatePicker.getMonth() + 1) + "." + myDatePicker.getDayOfMonth());
                    Toast.makeText(BookAppointment.this,"cannot select date from the past ",Toast.LENGTH_LONG).show();

                }
                else {

                    textView_date.setText(String.valueOf(myDatePicker.getYear())+"."+(myDatePicker.getMonth() + 1)+"."+myDatePicker.getDayOfMonth());
                    window.dismiss();
                }


            }
        });




    }

    private void showPopupTime(){

        LayoutInflater inflater = (LayoutInflater) BookAppointment.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Button cancel1,add1;
        final float density = BookAppointment.this.getResources().getDisplayMetrics().density;
        final View layout = inflater.inflate(R.layout.popup_time_picker,(ViewGroup) findViewById(R.id.popup_1));

        final PopupWindow window = new PopupWindow(layout,(int )density*380,(int)density*650,true);
        window.showAtLocation(findViewById(R.id.makeAppointment),Gravity.CENTER,0,0);
        add1 = (Button) layout.findViewById(R.id.btn_popup1_add);
        cancel1 = (Button) layout.findViewById(R.id.btn_popup1_cancel);
        final TimePicker myTimePicker;
        myTimePicker = (TimePicker) layout.findViewById(R.id.popup1_time_picker);
        myTimePicker.setIs24HourView(true);

        cancel1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                window.dismiss();
            }
        });
        add1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                textView_time.setText(myTimePicker.getCurrentHour().toString()+":"+ myTimePicker.getCurrentMinute().toString());
                window.dismiss();
            }
        });


    }

}
