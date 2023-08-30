package com.example.myapplication.chat;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class Photo {
    Bitmap bitmap;
    Uri uri;

    public Photo(Uri uri,Bitmap bitmap){
        this.uri = uri;
        this.bitmap = bitmap;
    }



    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getBitmapAsBase64(){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 50, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return java.util.Base64.getEncoder().encodeToString(byteArray);
    }
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Uri getUri() {
        return uri;
    }

    public void setUri(Uri uri) {
        this.uri = uri;
    }

    public JSONObject getPhotoAsJSONObject() {
        JSONObject res = new JSONObject();
        try {
            res.put("yGeolocate",56);
            res.put("xGeolocate",78);
            res.put("text" , getBitmapAsBase64());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return res;
    }
}

