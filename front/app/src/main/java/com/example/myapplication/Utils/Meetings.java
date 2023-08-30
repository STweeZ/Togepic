package com.example.myapplication.Utils;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.myapplication.activities.Chat;

import org.json.JSONException;
import org.json.JSONObject;

import live.videosdk.rtc.android.VideoSDK;

public class Meetings {
    private static final String token="rre";

    public static void init(Context context){
        VideoSDK.initialize(context);
    }

    public static void joinMeetings(String meetingId){
    }

    public static void createMeetings(){
    }


}
