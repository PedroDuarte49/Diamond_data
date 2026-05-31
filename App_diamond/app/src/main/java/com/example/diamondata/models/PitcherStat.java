package com.example.diamondata.models;

import com.google.gson.annotations.SerializedName;

public class PitcherStat {
    @SerializedName("player_id")
    private int player; // ID del jugador
    @SerializedName("game_id")
    private int game;   // ID del partido
    private int g;
    private int p;
    private double pcl; // Efectividad (ERA)
    private int j;
    private int a;
    private int jc;
    private int bl;
    private int sv;
    private double il;  // Entradas Lanzadas (acepta decimales como 6.1)
    private int h;
    private int cl;
    private int so;

    public PitcherStat() {}

    // Getters y Setters
    public int getPlayer() { return player; }
    public void setPlayer(int player) { this.player = player; }
    public int getGame() { return game; }
    public void setGame(int game) { this.game = game; }
    public int getG() { return g; }
    public void setG(int g) { this.g = g; }
    public int getP() { return p; }
    public void setP(int p) { this.p = p; }
    public double getPcl() { return pcl; }
    public void setPcl(double pcl) { this.pcl = pcl; }
    public int getJ() { return j; }
    public void setJ(int j) { this.j = j; }
    public int getA() { return a; }
    public void setA(int a) { this.a = a; }
    public int getJc() { return jc; }
    public void setJc(int jc) { this.jc = jc; }
    public int getBl() { return bl; }
    public void setBl(int bl) { this.bl = bl; }
    public int getSv() { return sv; }
    public void setSv(int sv) { this.sv = sv; }
    public double getIl() { return il; }
    public void setIl(double il) { this.il = il; }
    public int getH() { return h; }
    public void setH(int h) { this.h = h; }
    public int getCl() { return cl; }
    public void setCl(int cl) { this.cl = cl; }
    public int getSo() { return so; }
    public void setSo(int so) { this.so = so; }
}
