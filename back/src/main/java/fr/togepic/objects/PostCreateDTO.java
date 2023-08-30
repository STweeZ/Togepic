package fr.togepic.objects;

import fr.togepic.entities.Picture;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PostCreateDTO implements Serializable {

    PostLightDTO parent;
    String text;
    List<PictureCreateDTO> pictures = new ArrayList<>();

    public PostLightDTO getParent() {
        return parent;
    }

    public void setParent(PostLightDTO parent) {
        this.parent = parent;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isValid() {
        return text != null;
    }

    public List<PictureCreateDTO> getPictures() {
        return pictures;
    }

    public void setPictures(List<PictureCreateDTO> pictures) {
        this.pictures = pictures;
    }


    public List<Picture> transformPictures() {
        List<Picture> transformed = new ArrayList<>();
        for (PictureCreateDTO picture : pictures) {
            transformed.add(picture.toPicture());
        }
        return transformed;
    }
}
