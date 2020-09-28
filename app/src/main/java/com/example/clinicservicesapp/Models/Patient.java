package com.example.clinicservicesapp.Models;

import androidx.annotation.NonNull;

public class Patient extends Account {

    private Role role;
    private String docId;

    public Patient(String name, String username, String password, String docId){
        super(name, username, password);
        this.role = Role.Patient;
        this.docId = docId;
    }

    @Override
    public Role getRole() { return this.role; }
    public String getDocId() { return this.docId; }

    @NonNull
    @Override
    public String toString() {
//        return "Patient : {" +
//                super.getID() + "\n" +
//                super.getName() + "}";
        return super.getName() + "\n" + Role.Patient;
    }
}
