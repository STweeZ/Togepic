package fr.togepic.objects;

import java.io.Serializable;

public class UserCreateDTO implements Serializable {
    private String email;
    private String password;
    private String name;
    private String picture;

    private String FCMToken;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getFCMToken() {
        return FCMToken;
    }

    public void setFCMToken(String FCMToken) {
        this.FCMToken = FCMToken;
    }

    public boolean isValid() {
        return email != null && password != null && name != null;
    }
}
