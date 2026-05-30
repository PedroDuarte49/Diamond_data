package com.example.diamondata.models;

import com.google.gson.annotations.SerializedName;

public class Player {
    private int id;
    private String name;
    private int number;

    // Le indicamos exactamente cómo se llama en Django
    @SerializedName("type")
    private String position_type;
    private Integer team;

    public Player(String name, int number, String position_type, Integer team) {
        this.name = name;
        this.number = number;
        this.position_type = position_type;
        this.team = team;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getNumber() { return number; }
    public String getPositionType() { return position_type; }
    public Integer getTeam() { return team; }
}