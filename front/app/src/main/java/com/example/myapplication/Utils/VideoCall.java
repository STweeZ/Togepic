package com.example.myapplication.Utils;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.myapplication.activities.Chat;
import com.example.myapplication.chat.MeetingActivity;
import com.example.myapplication.chat.Message;

import org.json.JSONException;
import org.json.JSONObject;

public class VideoCall {
    public static final String token="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhcGlrZXkiOiIwMDYxNTc5MC04ZWZmLTRkMGYtYmZiMi00NjU3OTFkODhmMTIiLCJwZXJtaXNzaW9ucyI6WyJhbGxvd19qb2luIl0sImlhdCI6MTY3ODg3NTQxMCwiZXhwIjoxODM2NjYzNDEwfQ.HfvRV1XTCWGyYn1EfEpO3VIRclhdYKjJNgT_ZOQYSyg";

    public final static String JOIN_TEXT="Start meeting : ";



    public static void createMeeting(Context ctx, int groupID) {
        AndroidNetworking.post("https://api.videosdk.live/v2/rooms")
                .addHeaders("Authorization", VideoCall.token)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            final String meetingId = response.getString("roomId");
                            Intent intent = new Intent(ctx, MeetingActivity.class);
                            intent.putExtra("token", VideoCall.token);
                            intent.putExtra("meetingId", meetingId);
                            new Message("bhnj,k",VideoCall.JOIN_TEXT+ meetingId,groupID).send(ctx);
                            ctx.startActivity(intent);
                        } catch ( JSONException e) {
                            e.printStackTrace();
                        }
                    }


                    @Override
                    public void onError(ANError anError) {
                        anError.printStackTrace();
                    }
                });
    }

    public static void joinMeeting(Context ctx, String meettingId){
        Intent intent = new Intent(ctx, MeetingActivity.class);
        intent.putExtra("token", VideoCall.token);
        intent.putExtra("meetingId", meettingId);
        intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(intent);
    }

}
