package com.example.clinicservicesapp.AdminFeatures;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.clinicservicesapp.Models.Account;
import com.example.clinicservicesapp.Models.Employee;
import com.example.clinicservicesapp.Models.Patient;
import com.example.clinicservicesapp.Models.Role;
import com.example.clinicservicesapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unchecked")
public class AdminAccounts extends AppCompatActivity {

    private ArrayList<Account> arrayList;
    private ArrayAdapter<Account> adapter;
    private ListView listv;
    private FirebaseFirestore db;
    private Button returnBtn, addAccountBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_accounts);
        getSupportActionBar().hide();

        db = FirebaseFirestore.getInstance();

        listv = (ListView) findViewById(R.id.accountList);
        returnBtn = findViewById(R.id.returnToMain);
        arrayList = new ArrayList<Account>();
        adapter = new ArrayAdapter<>(AdminAccounts.this, android.R.layout.simple_list_item_1,arrayList);
        listv.setAdapter(adapter);

        returnBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        listv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openDialog(arrayList.get(position),position);
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
        Intent intent = new Intent(AdminAccounts.this, AdminControlPanel.class);
        intent.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(intent);
    }

    public void openDialog(final Account oldItem, final int index) {

        View view = getLayoutInflater().inflate(R.layout.delete_accounts_dialog, null);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        //final View dialogView = inflater.inflate(R.layout.delete_accounts_dialog, null);
        dialogBuilder.setView(view);
        dialogBuilder.setTitle("Delete Account");

        final AlertDialog dialog = dialogBuilder.create();
        dialog.show();

        final Button delete_button = view.findViewById(R.id.btnDelete);




        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(oldItem.getRole() == Role.Employee){

                    arrayList.remove(oldItem);

                    db.collection("Employee")
                            .document(oldItem.getDocId())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AdminAccounts.this, "Account Deleted", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    refresh();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AdminAccounts.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    refresh();
                                }
                            });

                }else if(oldItem.getRole() == Role.Patient){

                    arrayList.remove(oldItem);

                    db.collection("Patient")
                            .document(oldItem.getDocId())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(AdminAccounts.this, "Account Deleted", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    refresh();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AdminAccounts.this, e.getMessage(), Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                    refresh();
                                }
                            });

                }
            }
        });
    }

    public void refresh(){
        final Task<QuerySnapshot> employee = db.collection("Employee").get();
        final Task<QuerySnapshot> patient = db.collection("Patient").get();

        Task combined = Tasks.whenAll(employee, patient)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        arrayList.clear();
                        List<DocumentSnapshot> docEmployee = employee.getResult().getDocuments();
                        List<DocumentSnapshot> docPatient = patient.getResult().getDocuments();

                        Log.d("Length Employee", String.valueOf(docEmployee.size()));
                        Log.d("Length Patient", String.valueOf(docPatient.size()));

                        Map<String, Object> read = new HashMap<>();
                        for(DocumentSnapshot docSnap : docEmployee){
                            if(! docSnap.getId().equals("AA")){
                                read = docSnap.getData();

                                Account account = new Employee(
                                        (String)    read.get("name"),
                                        (String)    read.get("username"),
                                        (String)    read.get("password"),
                                        docSnap.getId()
                                );

                                Log.d("Employee", account.toString());

                                arrayList.add(account);
                            }
                        }

                        for(DocumentSnapshot docSnap : docPatient){
                            if(! docSnap.getId().equals("AA")){
                                read = docSnap.getData();
                                Log.d("Read Patient", read.toString());

                                Account account = new Patient(
                                        (String)    read.get("name"),
                                        (String)    read.get("username"),
                                        (String)    read.get("password"),
                                        docSnap.getId()
                                );

                                arrayList.add(account);
                            }
                        }

                        adapter.notifyDataSetChanged();
                    }
                });
    }
}
