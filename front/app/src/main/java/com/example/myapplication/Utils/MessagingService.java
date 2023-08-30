package com.example.myapplication.Utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.example.myapplication.R;
import com.example.myapplication.activities.Chat;
import com.example.myapplication.chat.ChatAdaptater;
import com.example.myapplication.chat.Message;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.sql.Timestamp;

public class MessagingService extends FirebaseMessagingService {

    private static Chat chat;

    public void onNewToken(@NonNull String token){

        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("FCM", token);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    public static void setFCMToken(SharedPreferences sharedPref){
        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(new OnCompleteListener<String>() {
            @Override
            public void onComplete(@NonNull Task<String> task) {
                if(task.isSuccessful()){
                    String token = task.getResult();
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("FCM_TOKEN", token);
                    editor.apply();
                }
            }
        });
    }

    public static String getFCMToken(SharedPreferences sharedPref){
        return sharedPref.getString("FCM_TOKEN",null);
    }


    public static void setChat(Chat chat){
        MessagingService.chat = chat;
    }

    public static Chat getChat(){
        return MessagingService.chat;
    }
}
