package com.example.myapplication.chat;

import android.content.Context;
import android.graphics.Bitmap;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.Utils.Converter;
import com.example.myapplication.Utils.MessagingService;
import com.example.myapplication.Utils.Response;
import com.example.myapplication.Utils.RestClient;
import com.example.myapplication.Utils.UIMessage;
import com.example.myapplication.Utils.VideoCall;
import com.makeramen.roundedimageview.RoundedImageView;
import com.example.myapplication.activities.SignOutActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ChatAdaptater extends BaseAdapter {

    Context context;
    List<Message> data;
    private static LayoutInflater inflater = null;

    private static final Random rand = new Random();

    private int userID;
    private int groupID;

    public ChatAdaptater(Context context, int userID, int groupID) {
        this.context = context;
        this.userID = userID;
        this.groupID = groupID;
        this.data = new ArrayList<>();

        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void add(Message m) {
        this.data.add(m);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        Message m = data.get(position);
        return m;
    }

    public int getRealMessage(){
        int res=0;
        for(int i=0;i<data.size();i++){
            if(!data.get(i).isTemporaire())res++;
        }
        return res;
    }
    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        Message m = (Message) getItem(position);

        if(m.getContent().startsWith(VideoCall.JOIN_TEXT)) {
            vi = inflater.inflate(R.layout.item_button_join_metting, null);

            String meetingID = m.getContent().substring(VideoCall.JOIN_TEXT.length());
            vi.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    VideoCall.joinMeeting(context,meetingID);
                }
            });
            return vi;
        }

        if(m.isTemporaire())
            vi = inflater.inflate(R.layout.item_temporaire,null);
        else if(m.getSenderID() == this.userID)
            vi = inflater.inflate(R.layout.item_sent, null);
        else vi = inflater.inflate(R.layout.item_received, null);


        if(m.getContent()==null) return vi;

        //Ajout du texte
        ((TextView)vi.findViewById(R.id.textMessage)).setText(m.content);
        ((TextView)vi.findViewById(R.id.dateMessage)).setText(m.date.replace(" ","\n"));

        // Ajout des photos
        LinearLayout ll = vi.findViewById(R.id.msg_photos);
        ll.removeAllViews();

        Photo p;
        for (int i = 0; i < m.getPhotos().size(); i++) {
            p = m.getPhotos().get(i);

            ImageView image = (ImageView) inflater.inflate(R.layout.item_photo_simple, null);
            image.setImageBitmap(p.getBitmap());

            if (i != m.getPhotos().size() - 1)
                image.setPadding(0, 0, 80, 0);

            ll.addView(image);
        }

        //Ajout du bitmap
        if(m.senderBitmap!=null){
            RoundedImageView userAvatar = vi.findViewById(R.id.avatar);
            userAvatar.setImageBitmap(m.senderBitmap);

        }

        return vi;
    }

    public void update() {
        RestClient.makeRequest("GET", context.getString(R.string.api_rest_groupe) + groupID, null, context,
                new RestClient.Callback() {

                    @Override
                    public void onResponse(Response response) {
                        JSONObject json = Converter.jsonFromString(response.getResponseString());
                        List<Message> messages = new ArrayList<>();

                        try {
                            ((TextView) MessagingService.getChat().findViewById(R.id.toolbar_title))
                                    .setText(json.getString("name"));
                            if (json.has("picture"))
                                MessagingService.getChat().binding.groupPicture.setImageBitmap(Converter.stringToBitmap(json.getString("picture")));
                            else
                                MessagingService.getChat().binding.groupPicture.setImageResource(R.drawable.togepic);

                            if (json.has("posts")) {
                                JSONArray post = json.getJSONArray("posts");

                                if(post.length()==getRealMessage()) return;

                                for (int i = 0; i < post.length(); i++) {
                                    JSONObject currentPost = (JSONObject) post.get(i);
                                    JSONObject sender = (JSONObject) currentPost.get("user");
                                    Bitmap bitmap = null;
                                    if(sender.has("picture")){
                                        String base64str = sender.getString("picture");
                                        bitmap = Converter.stringToBitmap(base64str);

                                    }
                                    messages.add(new Message(
                                            sender.get("name").toString(),
                                            (Integer) sender.get("id"),
                                            currentPost.get("text").toString(),
                                            currentPost.get("creation").toString(),
                                            (JSONArray) currentPost.get("pictures"),
                                            bitmap
                                    ));
                                }
                            }

                            if (messages.isEmpty()) {
                                MessagingService.getChat().binding.noMessage.setVisibility(View.VISIBLE);
                            } else {
                                MessagingService.getChat().binding.noMessage.setVisibility(View.INVISIBLE);
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                        data = messages;
                        notifyDataSetChanged();

                    }

                    @Override
                    public void onError(Response response) {
                        int responseCode = response.getResponseCode();
                        String msg;
                        if (responseCode == HttpURLConnection.HTTP_CONFLICT)
                            msg = context.getString(R.string.err_no_group);
                        else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                            msg = context.getString(R.string.err_user_info_missing);
                        else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                            msg = context.getString(R.string.err_contact_server);
                        else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            msg = context.getString(R.string.err_session_time_out);
                            context.startActivity(new Intent(context, SignOutActivity.class));
                        }
                        else
                            msg = context.getString(R.string.err_unknown);
                        UIMessage.showError(msg, context);
                    }
                },false);
    }
}
