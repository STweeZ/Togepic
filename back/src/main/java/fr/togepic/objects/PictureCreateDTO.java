package fr.togepic.objects;

import fr.togepic.entities.Picture;

import java.io.Serializable;
import java.util.Base64;

public class PictureCreateDTO implements Serializable {

    String text;
    String xGeolocate;
    String yGeolocate;

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

    public Picture toPicture() {
        return new Picture(Base64.getDecoder().decode(text), xGeolocate, yGeolocate);
    }
}
