package fr.togepic.objects;

import java.io.Serializable;

public class GroupeCreateDTO implements Serializable {
    private String name;
    private String description;
    private String picture;
    private boolean isPrivate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public boolean getIsPrivate() {
        return isPrivate;
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

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean isValid() {
        return name != null;
    }
}
