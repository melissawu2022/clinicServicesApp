package com.example.clinicservicesapp.PatientFeatures;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.clinicservicesapp.R;

public class DisplayRating extends AppCompatActivity {

//    TextView tv;
//    TextView tv2;
//    String st;
//    String st2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_rating);

        ListView lv = findViewById(R.id.listView);
        String[] list = {"item1","item2","item3","item4"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);

        lv.setAdapter(adapter);

//        tv = findViewById(R.id.textReview);
//        st = getIntent().getExtras().getString("Value");
//        tv.setText("Comments: " + st);
//
//        tv2 = findViewById(R.id.rateNum);
//        st2 = getIntent().getExtras().getString("Value2");
//        tv2.setText("Rating: " + st2 + "/5");
    }
}
