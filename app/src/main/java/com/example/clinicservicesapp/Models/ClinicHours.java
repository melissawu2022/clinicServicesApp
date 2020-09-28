package com.example.clinicservicesapp.Models;

import android.util.Log;

import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ClinicHours {
    private Map<String, Object> weekList;
    private String docID;

    public ClinicHours(Map<String, Object> monday,
                       Map<String, Object> tuesday,
                       Map<String, Object> wednesday,
                       Map<String, Object> thursday,
                       Map<String, Object> friday,
                       Map<String, Object> saturday,
                       Map<String, Object> sunday){

        weekList = new HashMap<>();

        weekList.put("monday", monday);
        weekList.put("tuesday", tuesday);
        weekList.put("wednesday", wednesday);
        weekList.put("thursday", thursday);
        weekList.put("friday", friday);
        weekList.put("saturday", saturday);
        weekList.put("sunday", sunday);
    }

    public ClinicHours(Map<String, Object> weekList){
        this.weekList = weekList;
        Log.d("ClinicHours constructor", this.weekList.toString());
    }

    public void setDocID(String value) { this.docID = value; }

    public Map<String, Object> getWeekList(){ return weekList; }
    public String getDocID() { return docID; }

    public Map<String, Object> getDay(String day){
        return (Map<String, Object>) weekList.get(day);
    }


    @NonNull
    @Override
    public String toString() {
        return weekList.toString();
    }
}
