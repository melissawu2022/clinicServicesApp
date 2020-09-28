package com.example.clinicservicesapp.PatientFeatures;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.clinicservicesapp.MainActivity;
import com.example.clinicservicesapp.EmployeeFeatures.DisplayEmployee;
import com.example.clinicservicesapp.R;

public class DisplayPatient extends AppCompatActivity {

    private Bundle bundle;
    private Button search;

    private Button viewappointment;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display);
        getSupportActionBar().hide();

        if(getIntent().getBundleExtra("information") != null){
            bundle = getIntent().getBundleExtra("information");
        }

        String username = bundle.getString("name");
        String role = bundle.getString("role");

        Log.d("ReadingIntent", username);
        Log.d("ReadingIntent", role);

        TextView tv = (TextView)findViewById(R.id.inputUserName);
        tv.setText(username);

        TextView roleTv = (TextView) findViewById(R.id.role);
        roleTv.setText(role);

        search = findViewById(R.id.searchService);
        viewappointment = findViewById(R.id.viewAppointment);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchServices();
            }
        });

        viewappointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                patientAppointments();
            }
        });



    }

    public void patientAppointments() {
        Intent intent = new Intent(this, PatientAppointment.class);
        intent.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(intent);
    }

    public void openRatingPage() {
        Intent intent = new Intent(this, RatingClinic.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(DisplayPatient.this)
                .setTitle("Confirmation")
                .setMessage("Do you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent logOut = new Intent(DisplayPatient.this, MainActivity.class);
                        finish();
                        startActivity(logOut);
                    }
                })
                .setNegativeButton("No", null);

        alertDialog.show();
    }

    public void searchServices(){
        Intent intent = new Intent(DisplayPatient.this, SearchServices.class);
        intent.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(intent);
    }

}
