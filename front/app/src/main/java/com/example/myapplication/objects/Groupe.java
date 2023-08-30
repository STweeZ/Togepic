package com.example.myapplication.objects;

import com.example.myapplication.chat.Message;

import java.util.ArrayList;
import java.util.List;

public class Groupe {
    private int id;
    private String name;
    private boolean isPrivate;
    private String picture;
    private String nbUsers;
    private List<Message> messages = new ArrayList<>();
    private String description;
    private String role;

    public Groupe(int id, String name, List<Message> posts, boolean isPrivate, String nbUser, String role, String picture) {
        this.id = id;
        this.name = name;
        this.messages = posts;
        this.isPrivate = isPrivate;
        this.nbUsers = nbUser;
        this.role = role;
        this.picture = picture;
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

    public String getNbUsers() {
        return nbUsers;
    }

    public void setNbUsers(String nbUsers) {
        this.nbUsers = nbUsers;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
