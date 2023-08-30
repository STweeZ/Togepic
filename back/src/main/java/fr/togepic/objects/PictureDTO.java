package fr.togepic.objects;

import fr.togepic.entities.Picture;

import java.io.Serializable;
import java.util.Base64;

public class PictureDTO implements Serializable {
    long id;
    UserMediumDTO user;
    String text;
    String xGeolocate;
    String yGeolocate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public UserMediumDTO getUser() {
        return user;
    }

    public void setUesr(UserMediumDTO user) {
        this.user = user;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getxGeolocate() {
        return xGeolocate;
    }

    public void setxGeolocate(String xGeolocate) {
        this.xGeolocate = xGeolocate;
    }

    public String getyGeolocate() {
        return yGeolocate;
    }

    public void setyGeolocate(String yGeolocate) {
        this.yGeolocate = yGeolocate;
    }

    public static PictureDTO toDTO(Picture picture) {
        PictureDTO dto = new PictureDTO();
        dto.id = picture.getId();
        dto.user = UserMediumDTO.toDTO(picture.getPost().getUser());
        if (picture.getText() != null)
            dto.text = Base64.getEncoder().encodeToString(picture.getText());
        dto.xGeolocate = picture.getXGeolocate();
        dto.yGeolocate = picture.getYGeolocate();
        return dto;
    }
}
