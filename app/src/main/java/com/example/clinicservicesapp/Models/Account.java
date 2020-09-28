package com.example.clinicservicesapp.Models;

public abstract class Account {

    private String name;
    private String username;
    private String password;

    Account(String name, String username, String password){
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public abstract Role getRole();
    public abstract String getDocId();

    @Override
    public abstract String toString();

    public String getName() { return this.name; }
    public String getUsername() { return this.username; }
    public String getPassword() { return this.password; }
}
