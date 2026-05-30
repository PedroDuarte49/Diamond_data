package com.example.diamondata.models;

import com.google.gson.annotations.SerializedName;

public class Game {
    private int id;

    @SerializedName("opponent")
    private String opponent;

    @SerializedName("date")
    private String date;

    @SerializedName("season")
    private String season;

    @SerializedName("location")
    private String location;

    public Game(String opponent, String date, String season, String location) {
        this.opponent = opponent;
        this.date = date;
        this.season = season;
        this.location = location;
    }

    public int getId() { return id; }
    public String getOpponent() { return opponent; }
    public String getDate() { return date; }
    public String getSeason() { return season; }
    public String getLocation() { return location; }
}