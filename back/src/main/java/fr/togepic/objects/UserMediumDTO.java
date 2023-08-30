package fr.togepic.objects;

import fr.togepic.entities.User;

import java.io.Serializable;
import java.util.Base64;

public class UserMediumDTO implements Serializable {
    private long id;
    private String name;
    private String picture;

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public static UserMediumDTO toDTO(User user) {
        UserMediumDTO dto = new UserMediumDTO();
        dto.id = user.getId();
        dto.name = user.getName();
        if (user.getPicture() != null)
            dto.picture = Base64.getEncoder().encodeToString(user.getPicture());
        return dto;
    }
}
