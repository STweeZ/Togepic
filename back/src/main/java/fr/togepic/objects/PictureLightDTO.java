package fr.togepic.objects;

import fr.togepic.entities.Picture;

import java.io.Serializable;
import java.util.Base64;

public class PictureLightDTO implements Serializable {
    long id;
    String text;
    String xGeolocate;
    String yGeolocate;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
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

    public static PictureLightDTO toDTO(Picture picture) {
        PictureLightDTO dto = new PictureLightDTO();
        dto.id = picture.getId();
        if (picture.getText() != null)
            dto.text = Base64.getEncoder().encodeToString(picture.getText());
        dto.xGeolocate = picture.getXGeolocate();
        dto.yGeolocate = picture.getYGeolocate();
        return dto;
    }
}
