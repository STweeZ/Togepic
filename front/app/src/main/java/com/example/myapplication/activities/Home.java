package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Converter;
import com.example.myapplication.Utils.Response;
import com.example.myapplication.Utils.RestClient;
import com.example.myapplication.Utils.UIMessage;
import com.example.myapplication.adapters.GroupAdapter;
import com.example.myapplication.chat.Message;
import com.example.myapplication.fragments.CreateGroupDialog;
import com.example.myapplication.objects.Groupe;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.button_addc).setOnClickListener(l -> {
            new CreateGroupDialog().show(getSupportFragmentManager(), CreateGroupDialog.TAG);
        });

    }

    private void update() {
        ImageView image = findViewById(R.id.userPicture);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        ((TextView) findViewById(R.id.toolbar_title)).setText(sp.getString("name", getString(R.string.app_name)));

        image.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            intent.putExtra("id", sp.getInt("id", -1));
            startActivity(intent);
        });

        String picture = sp.getString("picture", null);
        if (picture != null)
            image.setImageBitmap(Converter.stringToBitmap(picture));

        ((TextView) findViewById(R.id.toolbar_title)).setText(sp.getString("name", getString(R.string.app_name)));

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.groupList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        RestClient.makeRequest("GET", "/users/" + sp.getInt("id", -1) + "/groupes", null, Home.this, new RestClient.Callback() {
            @Override
            public void onResponse(Response response) {
                List<Groupe> groups = new ArrayList<>();
                JSONArray json = Converter.arrayFromString(response.getResponseString());
                try {
                    for(int i = 0; i < json.length(); i++){
                        JSONObject group = (JSONObject) json.get(i);

                        List<Message> posts = new ArrayList<>();
                        JSONArray jPosts = group.getJSONArray("posts");

                        for(int j = 0; j < jPosts.length(); j++) {
                            JSONObject currentPost = (JSONObject) jPosts.get(j);
                            JSONObject sender = (JSONObject) currentPost.get("user");
                            posts.add(new Message(
                                    sender.get("name").toString(),
                                    (Integer) sender.get("id"),
                                    currentPost.get("text").toString(),
                                    currentPost.get("creation").toString(),
                                    (JSONArray) currentPost.get("pictures"),
                                    null
                            ));
                        }

                        groups.add(new Groupe(group.getInt("id"), group.getString("name"), posts, group.getBoolean("isPrivate"), group.getString("nbUser"), group.getString("role"), group.has("picture") ? group.getString("picture") : null));
                    }
                    recyclerView.setAdapter(new GroupAdapter(groups, Home.this));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                if (groups.isEmpty()) {
                    findViewById(R.id.no_group).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.no_group).setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError(Response response) {
                int responseCode = response.getResponseCode();
                String msg;
                if (responseCode == HttpURLConnection.HTTP_CONFLICT)
                    msg = getString(R.string.err_no_user);
                else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                    msg = getString(R.string.err_user_info_missing);
                else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                    msg = getString(R.string.err_contact_server);
                else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    msg = getString(R.string.err_session_time_out);
                    startActivity(new Intent(Home.this, SignOutActivity.class));
                    finish();
                } else
                    msg = getString(R.string.err_unknown);
                UIMessage.showError(msg, Home.this);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        update();
    }
}
