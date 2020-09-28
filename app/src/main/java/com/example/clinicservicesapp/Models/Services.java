package com.example.clinicservicesapp.Models;

import androidx.annotation.NonNull;

public class Services {

    private String staff;
    private String service;
    private String rate;
    private String documentID;

    public Services(String staff, String service, String rate){
        this.staff = staff;
        this.service = service;
        this.rate = rate;
    }

    public String getStaff() { return this.staff; }
    public String getService() { return this.service; }
    public String getRate() { return this.rate; }
    public String getDocumentID() { return this.documentID; }

    public void setStaff(String value) { this.staff = value; }
    public void setService(String value) { this.service = value; }
    public void setRate(String value) { this.rate = value; }
    public void setDocumentID(String value) { this.documentID = value; }

    @NonNull
    @Override
    public String toString() {
        return "Role: "+this.staff +"\nService: "+this.service + "\nRate: " + this.rate;
        //return "Service (" + this.staff + ", " + this.service + ")";
    }
}
