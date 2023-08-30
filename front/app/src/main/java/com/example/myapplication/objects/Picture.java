package com.example.myapplication.objects;

public class Picture {
   private int id;
    private String text;
    private User user;
    private int xGeolocate;
    private int yGeolocate;

    public Picture(int id, String text, User user, int xGeolocate, int yGeolocate) {
        this.id = id;
        this.text = text;
        this.user = user;
        this.xGeolocate = xGeolocate;
        this.yGeolocate = yGeolocate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getxGeolocate() {
        return xGeolocate;
    }

    public void setxGeolocate(int xGeolocate) {
        this.xGeolocate = xGeolocate;
    }

    public int getyGeolocate() {
        return yGeolocate;
    }

    public void setyGeolocate(int yGeolocate) {
        this.yGeolocate = yGeolocate;
    }



}
