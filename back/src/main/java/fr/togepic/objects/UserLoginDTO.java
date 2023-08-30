package fr.togepic.objects;

import java.io.Serializable;

public class UserLoginDTO implements Serializable {
    private String email;
    private String password;

    private String FCMToken;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFCMToken(){
        return this.FCMToken;
    }

    public void setFCMToken(String FCMToken){
        this.FCMToken = FCMToken;
    }



    public boolean isValid() {
        return email != null || password != null;
    }
}
