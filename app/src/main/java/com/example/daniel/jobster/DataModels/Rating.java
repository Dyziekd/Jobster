package com.example.daniel.jobster.DataModels;

public class Rating
{
    private long id, idProfileFrom, idProfileTo;
    private double rating;
    private String comment, userFromName;

    // constructor for listview (Opinions)
    public Rating(long id, String userFromName, String comment, double rating, long idProfileFrom, long idProfileTo)
    {
        this.id = id;
        this.userFromName = userFromName;
        this.comment = comment;
        this.rating = rating;
        this.idProfileFrom = idProfileFrom;
        this.idProfileTo = idProfileTo;
    }

    public long getId() {
        return id;
    }

    public double getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public String getUserFromName() {
        return userFromName;
    }

    public void setId(long id) {
        this.id = id;
    }

}
