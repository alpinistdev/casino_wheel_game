package com.example.halloweenswheelgame.model;

public class Stat {
    //members
    String index;
    String points;

    //constructor


    public Stat(String index, String points) {
        this.index = index;
        this.points = points;
    }

    //getters

    public String getIndex() {
        return index;
    }

    public String getPoints() {
        return points;
    }
}
