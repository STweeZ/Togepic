package com.example.myapplication.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;

import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class Converter {

    public static String imageViewToString(RoundedImageView image) {
        Bitmap bitmap = Bitmap.createBitmap(image.getWidth(), image.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        image.draw(canvas);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        byte[] byteArray = outputStream.toByteArray();
        return java.util.Base64.getEncoder().encodeToString(byteArray);
    }

    public static Bitmap stringToBitmap(String encodedString) {
        byte[] byteArray = java.util.Base64.getDecoder().decode(encodedString);
        Bitmap bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
        return bitmap;
    }

    public static JSONObject jsonFromString(String message) {
        try {
            return new JSONObject(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray arrayFromString(String message) {
        try {
            return new JSONArray(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
