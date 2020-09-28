package com.example.clinicservicesapp.Models;

import androidx.annotation.NonNull;

public class Appointment {

    private String documentId;
    private String clinicId;
    private String clinicName;
    private String date;
    private String patientId;
    private String time;
    private boolean inClinic;

    public Appointment(String documentId, String clinicId, String clinicName, String date, String patientId, String time, boolean inClinic){
        this.documentId = documentId;
        this.clinicId = clinicId;
        this.clinicName = clinicName;
        this.date = date;
        this.patientId = patientId;
        this.time = time;
        this.inClinic = inClinic;
    }

    public String getDocumentId() { return this.documentId; }
    public String getClinicId() { return this.clinicId; }
    public String getClinicName() { return this.clinicName; }
    public String getDate() { return this.date; }
    public String getPatientId() { return this.patientId; }
    public String getTime() { return this.time; }

    public void setDocumentId(String value) { this.documentId = value; }
    public void setClinicId(String value) { this.clinicId = value; }
    public void setClinicName(String value) { this.clinicName = value; }
    public void setDate(String value) { this.date = value; }
    public void setPatientId(String value) { this.patientId = value; }
    public void setTime(String value) { this.time = value; }

    public String forClinic(){
        return "Date: " + date +
                "\nTime: " + time +
                "\nPatientID: " + patientId;
    }

    @NonNull
    @Override
    public String toString() {

        return (inClinic) ? forClinic() : "Clinic Name: " + clinicName +
                "\nDate: " + date +
                "\nTime: " + time;
    }
}
