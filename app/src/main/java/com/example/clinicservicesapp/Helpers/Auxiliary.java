package com.example.clinicservicesapp.Helpers;

import android.content.Intent;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Auxiliary {

    public static boolean checkEmpty(String name){
        return name.trim().length() < 1;

    }

    public static boolean validName(String name){

        return name.trim().length() >=2 && name.matches("[A-Za-z\']*");

    }
    public static boolean validAddress(String addy){

        return addy.trim().length() >=3;

    }

    public static boolean validHours(String hours){

        if(hours.contains("-")){

            String[] split = hours.split("-");
            split[0] = split[0].replace('h', ':');
            split[1] = split[1].replace('h', ':');
            split[0] += ":00"; split[1] += ":00";
            //Check from

            if(validTime(split[0]) && validTime(split[1])){
                return true;
            }
            return false;

        }else{
            return false;
        }
    }


    public static boolean validTime(String time){
        boolean flag = false;

        if(time.length()<4 || time.length()>8){
            return false;
        }
       String[] t = time.trim().split(":");

       for(int i=0;i<t.length;i++){
           if(i== 0){
               flag=valueInBetween(Integer.parseInt(t[i]),0,23);

           }
           else if(flag){
               flag = valueInBetween(Integer.parseInt(t[i]),0,59);
           }

       }
       return flag;

    }

    



    public static boolean valueInBetween(int value,int lower_bound,int upper_bound){
        return value<=upper_bound && value>=lower_bound;
    }



}
