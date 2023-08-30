package fr.togepic.objects;

import fr.togepic.entities.User;

import java.io.Serializable;

public class UserUpdateFCMTokenDTO implements Serializable {
    String FCM;

    public String getFCM() {
        return FCM;
    }

    public void setFCM(String FCM) {
        this.FCM = FCM;
    }
}
