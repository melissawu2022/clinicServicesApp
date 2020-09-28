package com.example.clinicservicesapp;
import com.example.clinicservicesapp.Helpers.Auxiliary;
import com.example.clinicservicesapp.Models.Employee;
import com.example.clinicservicesapp.Models.Patient;
import com.example.clinicservicesapp.Models.Role;

import static org.junit.Assert.*;

import org.junit.Test;
public class ServiceTest {




    @Test
    public void testCheckEmpty(){
        boolean actual = Auxiliary.checkEmpty(" abcde ");
        boolean expected = false;
        assertEquals("Method checkEmpty failed ",expected,actual);

    }

    @Test
    public void testValidName(){
        boolean actual = Auxiliary.validName("O'Reilly");
        boolean expected = true;
        assertEquals("Method validName failed ", expected,actual);
    }


    @Test
    public void testPatientGetRole(){

        Enum actual = new Patient("John Doe", "john","123456", "djeu283jdiudm3dm3").getRole();
        Enum expected = Role.Patient;


        assertEquals("Method patientToString failed ",expected,actual);

    }

    
    @Test
    //check if name of account exists
    public void testGetName(){
        String actual = new Employee("Sandy Smith", "Sandy", "1234abcd", "kdiej93jkd8k29md").getName();
        String expected = "Sandy Smith";

        assertEquals("Method getName failed ",expected,actual);
    }

    //check for correct password
    @Test
    public void testGetPassword() {
        String actual = new Employee("Sandy Smith", "Sandy", "1234abcd", "kdi3kidjk3984jk").getPassword();
        String expected = "1234abcd";

        assertEquals("Method getPassword failed ",expected,actual);
    }
    @Test
    //test if account is deleted
    public void testGetDeleted() {
        Role actual = new Employee("Sandy Smith", "Sandy", "1234abcd", "kdikek03kiod").getRole();
        Role expected = Role.Employee;

        assertEquals("Role classification failed",expected,actual);
    }
    @Test

    public void testValidTime(){
        Boolean actual = Auxiliary.validTime("19:00");
        Boolean expected = true;

        assertEquals("Method validTime failed",expected,actual);
    }

    @Test

    public void testValueinBetween(){
        Boolean actual = Auxiliary.valueInBetween(12, 0, 24);
        Boolean expected = true;
        assertEquals("Method ValueinBetween failed", expected, actual);
    }


    @Test
    public void testGetID() {
        String actual = new Employee("Patrick Star", "HELLO", "password", "3lkiodlk3903jkd").getDocId();
        String expected = "3lkiodlk3903jkd";
        assertEquals("Method getDocId failed ", expected, actual);
    }



}
