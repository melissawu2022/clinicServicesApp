package com.example.clinicservicesapp.AdminFeatures;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.clinicservicesapp.MainActivity;
import com.example.clinicservicesapp.R;

public class AdminControlPanel extends AppCompatActivity {

    private Button btnTest;
    private Button btnServ;
    private Button btnAcc;
    private TextView txtMsg1;
    private TextView txtMsg2;

    private Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_admin);
        getSupportActionBar().hide();

        btnServ = findViewById(R.id.ManagerServ);
        btnAcc = findViewById(R.id.ManageAcc);

        txtMsg1 = findViewById(R.id.inputUserName2);
        txtMsg2 = findViewById(R.id.role2);
        if(getIntent().getBundleExtra("information") != null){
            bundle = getIntent().getBundleExtra("information");
        }

        txtMsg1.setText(bundle.getString("name"));
        txtMsg2.setText(bundle.getString("role"));

        btnAcc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageAccount();
            }
        });

        btnServ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                manageService();
            }
        });

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AdminControlPanel.this)
                .setTitle("Confirmation")
                .setMessage("Do you want to log out?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent logOut = new Intent(AdminControlPanel.this, MainActivity.class);
                        finish();
                        startActivity(logOut);
                    }
                })
                .setNegativeButton("No", null);

        alertDialog.show();
    }

    public void signup(){}

    public void manageService(){
        Intent intent = new Intent(this, Admin.class);
        intent.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(intent);
    }

    public void manageAccount(){
        Intent intent = new Intent(this, AdminAccounts.class);
        intent.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(intent);
    }
}
