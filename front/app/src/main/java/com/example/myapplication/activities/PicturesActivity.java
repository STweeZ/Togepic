package com.example.myapplication.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myapplication.BuildConfig;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Converter;
import com.example.myapplication.Utils.Response;
import com.example.myapplication.Utils.RestClient;
import com.example.myapplication.Utils.UIMessage;
import com.example.myapplication.adapters.GroupAdapter;
import com.example.myapplication.adapters.PictureAdapter;
import com.example.myapplication.chat.Message;
import com.example.myapplication.databinding.ActivityChatBinding;
import com.example.myapplication.fragments.CreateGroupDialog;
import com.example.myapplication.objects.Groupe;
import com.example.myapplication.objects.Picture;
import com.example.myapplication.objects.User;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class PicturesActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pictures);

        Toolbar toolbar = findViewById(R.id.picture_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.pictureList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RestClient.makeRequest("GET", "/groupes/"+ getIntent().getIntExtra("groupId", -1) +"/pictures", null, PicturesActivity.this, new RestClient.Callback() {
            @Override
            public void onResponse(Response response) {
                List<Picture> pictures = new ArrayList<>();
                JSONArray json = Converter.arrayFromString(response.getResponseString());
                try {
                    for (int i = 0; i < json.length(); i++) {
                        JSONObject picture = (JSONObject) json.get(i);
                        JSONObject user = (JSONObject) picture.getJSONObject("user");
                        Picture pictureAdd = new Picture(picture.getInt("id"),
                                picture.getString("text"),
                                new User(user.getInt("id"), user.getString("name"), user.has("picture") ? user.getString("picture") : null),
                                picture.getInt("xGeolocate"),
                                picture.getInt("yGeolocate")
                        );
                        pictures.add(pictureAdd);
                    }
                    recyclerView.setAdapter(new PictureAdapter(pictures, PicturesActivity.this));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                if (pictures.isEmpty()) {
                    findViewById(R.id.no_picture).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.no_picture).setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onError(Response response) {
                int responseCode = response.getResponseCode();
                String msg;
                if (responseCode == HttpURLConnection.HTTP_CONFLICT)
                    msg = getString(R.string.err_no_group);
                else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN)
                    msg = getString(R.string.err_privilege);
                else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                    msg = getString(R.string.err_user_info_missing);
                else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                    msg = getString(R.string.err_contact_server);
                else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    msg = getString(R.string.err_session_time_out);
                    startActivity(new Intent(PicturesActivity.this, SignOutActivity.class));
                    finish();
                } else
                    msg = getString(R.string.err_unknown);
                UIMessage.showError(msg, PicturesActivity.this);
            }
        });
    }
}
