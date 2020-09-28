package com.example.clinicservicesapp.Models;

import androidx.annotation.NonNull;

public class Employee extends Account {

    private Role role;
    private String docId;

    public Employee(String name, String username, String password, String docId){
        super(name, username, password);
        this.role = Role.Employee;
        this.docId = docId;
    }

    @Override
    public Role getRole() { return this.role; }
    public String getDocId() { return docId; }

    @NonNull
    @Override
    public String toString() {
//        return "Employee : {" +
//                super.getID() + "\n" +
//                super.getName() + "}";
        return super.getName() + "\n" + Role.Employee;
    }
}
