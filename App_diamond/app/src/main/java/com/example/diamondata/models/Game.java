package com.example.diamondata.models;

public class Game {
    private int id;
    private String opponent;
    private String date; // Formato "YYYY-MM-DD"
    private String season;
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
