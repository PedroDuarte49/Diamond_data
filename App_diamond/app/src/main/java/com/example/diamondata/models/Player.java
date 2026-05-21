package com.example.diamondata.models;

public class Player {
    private int id;
    private String name;
    private int number;
    private String position_type; // "F" o "P"
    private Integer team; // ID del equipo (puede ser null)

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