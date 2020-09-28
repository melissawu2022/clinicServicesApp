package com.example.clinicservicesapp.PatientFeatures;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.autofill.Validator;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.basgeekball.awesomevalidation.AwesomeValidation;
import com.basgeekball.awesomevalidation.ValidationStyle;
import com.basgeekball.awesomevalidation.utility.RegexTemplate;
import com.example.clinicservicesapp.Models.Rating;
import com.example.clinicservicesapp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.clinicservicesapp.R.*;


public class RatingClinic extends AppCompatActivity {

    private Button btn;
    private EditText et;

    private FirebaseFirestore db;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_rating_clinic);

        db = FirebaseFirestore.getInstance();

        if(getIntent().getBundleExtra("information") != null){
            bundle = getIntent().getBundleExtra("information");
        }

        btn = findViewById(id.subRatingBtn);
        et = findViewById(id.textReview);

        final AwesomeValidation awesomeValidation = new AwesomeValidation(ValidationStyle.BASIC);

        awesomeValidation.addValidation(this, R.id.textReview, RegexTemplate.NOT_EMPTY, R.string.invalid_entry);


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final float rating = ((RatingBar)findViewById(id.ratingBar)).getRating();

                if(awesomeValidation.validate()) {

                    db.collection("Clinic")
                            .document(bundle.getString("clinicId"))
                            .get()
                            .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {

                                    final String ratingId = db.collection("Rating")
                                            .document().getId();

                                    final Map<String, Object> read = documentSnapshot.getData();

                                    List<String> ratingIds = (read.containsKey("ratings")) ? (List<String>)read.get("ratings") : new ArrayList<String>();
                                    ratingIds.add(ratingId);

                                    read.put("ratings", ratingIds);

                                    Map<String, Object> ratingMap = new HashMap<>();
                                    ratingMap.put("rate", rating); ratingMap.put("comment", et.getText().toString());

                                    db.collection("Rating")
                                            .document(ratingId)
                                            .set(ratingMap)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(RatingClinic.this, "Rating added", Toast.LENGTH_SHORT).show();

                                                    db.collection("Clinic")
                                                            .document(bundle.getString("clinicId"))
                                                            .set(read)
                                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void aVoid) {
                                                                    Toast.makeText(RatingClinic.this, "Rating submitted to clinic", Toast.LENGTH_SHORT).show();
                                                                    navigateBack();

                                                                }
                                                            })
                                                            .addOnFailureListener(new OnFailureListener() {
                                                                @Override
                                                                public void onFailure(@NonNull Exception e) {
                                                                    Toast.makeText(RatingClinic.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                }
                                                            });
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(RatingClinic.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(RatingClinic.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });


                }else {
                    Toast.makeText(getApplicationContext(), "Validation Failed", Toast.LENGTH_SHORT).show() ;
                }


            }
        });
    }

    @Override
    public void onBackPressed() {
        navigateBack();
    }

    public void navigateBack(){
        Intent intent = new Intent(RatingClinic.this, ViewClinicDialog.class);
        intent.putExtra("information", getIntent().getBundleExtra("information"));
        finish();
        startActivity(intent);
    }
}
