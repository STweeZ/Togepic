package fr.togepic.objects;

import fr.togepic.entities.Groupe;

import java.io.Serializable;
import java.util.Base64;

public class GroupeRetrieveDTO implements Serializable {
    private long id;
    private String name;
    private String description;
    private String picture;
    private boolean isPrivate;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public boolean getIsPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public static GroupeRetrieveDTO toDTO(Groupe groupe) {
        GroupeRetrieveDTO dto = new GroupeRetrieveDTO();
        dto.id = groupe.getId();
        dto.name = groupe.getName();
        if (groupe.getPicture() != null)
            dto.picture = Base64.getEncoder().encodeToString(groupe.getPicture());
        dto.description = groupe.getDescription();
        dto.isPrivate = groupe.getIsPrivate();
        return dto;
    }
}
