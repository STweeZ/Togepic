package fr.togepic.objects;

import fr.togepic.entities.User;

import java.io.Serializable;
import java.util.Base64;

public class UserDTO implements Serializable {
    private long id;
    private String email;
    private String name;
    private String registration;
    private boolean emailVerified;
    private String picture;
    private String description;
    private String token;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegistration() {
        return registration;
    }

    public void setRegistration(String registration) {
        this.registration = registration;
    }

    public boolean isEmailVerified() {
        return emailVerified;
    }

    public void setEmailVerified(boolean emailVerified) {
        this.emailVerified = emailVerified;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public static UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.id = user.getId();
        dto.email = user.getEmail();
        dto.name = user.getName();
        dto.registration = user.getRegistration().toString();
        dto.emailVerified = user.getEmailVerified();
        if (user.getPicture() != null)
            dto.picture = Base64.getEncoder().encodeToString(user.getPicture());
        dto.description = user.getDescription();
        dto.token = user.getToken();
        return dto;
    }
}
