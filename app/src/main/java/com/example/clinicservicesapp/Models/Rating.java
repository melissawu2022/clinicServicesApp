package com.example.clinicservicesapp.Models;

import androidx.annotation.NonNull;

public class Rating {

    private String comment;
    private float rating;

    public Rating(float rating, String comment){
        this.comment = comment;
        this.rating = rating;
    }

    public String getComment() { return this.comment; }
    public float getRating() { return this.rating; }

    public void setComment(String value) { this.comment = value; }
    public void setRating(float value) { this.rating = value; }

    @NonNull
    @Override
    public String toString() {
        return "Rating: " + rating + "\nComment: " + comment;
    }
}
