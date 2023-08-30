package com.example.myapplication.objects;

public class Invitation {

    private int id;
    private String name;
    private String picture;
    private String role;

    private boolean isInitialSelection = true;

    public Invitation(int id, String name, String picture) {
        this.id = id;
        this.name = name;
        this.picture = picture;
    }

    public Invitation(int id, String name, String picture, String role) {
        this(id, name, picture);
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isInitialSelection() {
        return isInitialSelection;
    }

    public void setInitialSelection(boolean initialSelection) {
        isInitialSelection = initialSelection;
    }
}
