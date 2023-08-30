package fr.togepic.objects;

import fr.togepic.entities.User;

import java.io.Serializable;
import java.util.Base64;

public class UserRetrieveDTO implements Serializable {
    private long id;
    private String name;
    private String picture;

    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

    public void setDescription(String description) {
        this.description = description;
    }

    public static UserRetrieveDTO toDTO(User user) {
        UserRetrieveDTO dto = new UserRetrieveDTO();
        dto.id = user.getId();
        dto.name = user.getName();
        if (user.getPicture() != null)
            dto.picture = Base64.getEncoder().encodeToString(user.getPicture());
        dto.description = user.getDescription();
        return dto;
    }
}
