package com.example.myapplication.chat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Base64;
import android.util.Log;

import com.example.myapplication.R;
import com.example.myapplication.Utils.Converter;
import com.example.myapplication.Utils.Response;
import com.example.myapplication.Utils.RestClient;
import com.example.myapplication.Utils.Tools;
import com.example.myapplication.Utils.UIMessage;
import com.example.myapplication.activities.Chat;
import com.example.myapplication.activities.SignOutActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import live.videosdk.rtc.android.lib.model.Me;

public class Message {
    String owner;
    String content;
    String date;
    int groupID;

    Bitmap senderBitmap;
    boolean temporaire = false;

    public List<Photo> photos = new ArrayList<>();

    int senderID;

    public Message(String owner){
        this.owner = owner;
        this.senderBitmap = null;
    }

    public Message(String owner, String content,int groupID){
        this.owner = owner;
        this.content = content;
        this.date = "";
        this.groupID = groupID;
        this.senderBitmap = null;
    }
    public List<Photo> getPhotos() {
        return this.photos;
    }

    public Message(String owner, int senderID, String content, String date, JSONArray photosJSONArray,Bitmap userBitmap){
        this.owner = owner;
        this.senderID = senderID;
        this.content=content;
        this.date=date;

        if(photosJSONArray==null)return;
        for(int i=0;i<photosJSONArray.length();i++){
            try {
                JSONObject obj = (JSONObject) photosJSONArray.get(i);


                String base64str = obj.get("text").toString();
                Bitmap bitmap = Converter.stringToBitmap(base64str);

                Photo p = new Photo(null,bitmap);
                this.photos.add(p);

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        this.senderBitmap = userBitmap;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setCurrentDate() {
        Date date = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String strDate = dateFormat.format(date);
        this.setDate(strDate);
    }


    public void addPhoto(Photo p ){
        this.photos.add(p);
    }

    public void removePhotos(Uri path){
        for(int i=0;i<photos.size();i++){
            if(photos.get(i).getUri().equals(path)){
                photos.remove(i);
            }
        }
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public JSONObject asJSONObject(){
        JSONObject res = new JSONObject();
        try {
            res.put("text", this.getContent());
            res.put("pictures", this.getPhotosAsJSONArray());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return res;
    }
    public JSONArray getPhotosAsJSONArray() {
        JSONArray res = new JSONArray();
        for(Photo p : this.getPhotos()){
            res.put(p.getPhotoAsJSONObject());
        }
        return res;
    }

    public void send(Context ctx) {
        if(this.getContent().isEmpty() && this.getPhotos().isEmpty()){
            UIMessage.showText(ctx.getString(R.string.err_empty_message), ctx);
            return;
        }
        RestClient.makeRequest("POST", "/groupes/" + groupID + "/posts", this.asJSONObject(), ctx, new RestClient.Callback() {
            @Override
            public void onResponse(Response response) {
                UIMessage.showText(ctx.getString(R.string.message_sent), ctx);
            }

            @Override
            public void onError(Response response) {
                int responseCode = response.getResponseCode();
                String msg;
                if (responseCode == HttpURLConnection.HTTP_CONFLICT)
                    msg = ctx.getString(R.string.err_no_group);
                else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN)
                    msg = ctx.getString(R.string.err_privilege);
                else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                    msg = ctx.getString(R.string.err_user_info_missing);
                else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                    msg = ctx.getString(R.string.err_contact_server);
                else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    msg = ctx.getString(R.string.err_session_time_out);
                    ctx.startActivity(new Intent(ctx, SignOutActivity.class));
                }
                else
                    msg = ctx.getString(R.string.err_unknown);
                UIMessage.showError(msg, ctx);
            }
        });
    }

    public boolean isTemporaire() {
        return temporaire;
    }

    public void setTemporaire(boolean temporaire) {
        this.temporaire = temporaire;
    }


}
