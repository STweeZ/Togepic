package com.example.myapplication.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import live.videosdk.rtc.android.VideoSDK;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.dsphotoeditor.sdk.activity.DsPhotoEditorActivity;
import com.dsphotoeditor.sdk.utils.DsPhotoEditorConstants;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Converter;
import com.example.myapplication.Utils.MessagingService;
import com.example.myapplication.Utils.Response;
import com.example.myapplication.Utils.RestClient;
import com.example.myapplication.Utils.Tools;
import com.example.myapplication.Utils.UIMessage;
import com.example.myapplication.Utils.VideoCall;
import com.example.myapplication.chat.ChatAdaptater;
import com.example.myapplication.chat.Message;
import com.example.myapplication.chat.Photo;
import com.example.myapplication.databinding.ActivityChatBinding;

import org.json.JSONException;
import org.json.JSONObject;

import tech.gusavila92.websocketclient.WebSocketClient;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Chat extends AppCompatActivity {

    public ChatAdaptater chatAdaptater;
    ListView mMessagesList;
    LinearLayout mPhotosList;
    public ActivityChatBinding binding;
    Message message;
    private int id;
    private String role;

    private boolean videoSDKEnable = true;

    Handler handler;
    Runnable runnable;


    private WebSocketClient webSocketClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        role = intent.getStringExtra("role");
        if (role.equals("READER")) {
            findViewById(R.id.footer).setVisibility(View.INVISIBLE);
            findViewById(R.id.reader_mode).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.footer).setVisibility(View.VISIBLE);
            findViewById(R.id.reader_mode).setVisibility(View.INVISIBLE);
        }

        VideoSDK.initialize(getApplicationContext());

        MessagingService.setChat(Chat.this);
        MessagingService.setFCMToken(getSharedPreferences("-", Context.MODE_PRIVATE));

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        this.message = new Message(sp.getString("name", getString(R.string.app_name)));

        // Init toolbar
        Toolbar toolbar = findViewById(R.id.home_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mPhotosList = (LinearLayout) findViewById(R.id.photos);

        mMessagesList = (ListView) findViewById(R.id.messages);
        this.chatAdaptater = new ChatAdaptater(this.getBaseContext(), sp.getInt("id", -1), id);
        this.chatAdaptater.update();
        mMessagesList.setAdapter(this.chatAdaptater);

        setListeners();
        createWebSocketClient();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);

    }

    private void setListeners() {

        binding.groupPicture.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), GroupeProfileActivity.class);
            intent.putExtra("id", id);
            intent.putExtra("role", role);
            startActivity(intent);
        });

        binding.add.setOnClickListener(l -> {
            PopupMenu menu = new PopupMenu(Chat.this, l);
            menu.getMenuInflater().inflate(R.menu.menu_popoup_chat, menu.getMenu());

            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.takePicture:
                            dispatchTakePictureIntent();
                            break;
                        case R.id.addPicture:
                            takePictureIntent();
                            break;
                        case R.id.call:
                            if(!videoSDKEnable){
                                Toast.makeText(Chat.this, "Votre téléphone ne supporte pas les appels vidéos", Toast.LENGTH_SHORT).show();
                                break;
                            }


                            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                                    || checkSelfPermission(
                                            Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_DENIED) {
                                String[] permission = { Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO };
                                requestPermissions(permission, 16);
                            } else {
                                makeCall();
                            }

                        default:
                            return false;
                    }
                    return false;
                }
            });
            menu.show();
        });

        binding.picturesPic.setOnClickListener(l -> {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
            Intent intent = new Intent(this, PicturesActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Bundle b = new Bundle();
            b.putInt("groupId", id);
            intent.putExtras(b);

            this.startActivity(intent);
        });

        binding.send.setOnClickListener(l -> {
            this.message.setContent(binding.editText.getText().toString());
            this.message.setCurrentDate();
            this.message.setGroupID(id);


            Message m = new Message(this.message.getOwner(),this.message.getContent(),this.message.getGroupID());
            m.setTemporaire(true);
            chatAdaptater.add(m);
            this.message.send(Chat.this);
            this.clear();
        });

    }

    private void createWebSocketClient(){

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                update();
                handler.postDelayed(this, 5000);
            }
        };

        handler.postDelayed(runnable,5000);


        /*
        URI uri;
        try {
            // Connect to local host
            uri = new URI("ws://10.0.2.2:8000/togepic/chat");
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                Log.i("WebSocket", "Session is starting");
                webSocketClient.send("Hello World!");
            }

            @Override
            public void onTextReceived(String s) {
                Log.i("WebSocket", "Message received");
                update();
            }

            @Override
            public void onBinaryReceived(byte[] data) {
            }

            @Override
            public void onPingReceived(byte[] data) {
            }

            @Override
            public void onPongReceived(byte[] data) {
            }

            @Override
            public void onException(Exception e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onCloseReceived() {
                Log.i("WebSocket", "Closed ");
                System.out.println("onCloseReceived");
            }
        };

        webSocketClient.setConnectTimeout(10000);
        webSocketClient.setReadTimeout(60000);
        webSocketClient.enableAutomaticReconnection(5000);
        webSocketClient.connect();
        */

    }

    // Click back btn
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }

    // PRENDRE UNE PHOTO
    Uri currentPhotoPath;

    private void dispatchTakePictureIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED
                    || checkSelfPermission(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {

                String[] permission = { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE };
                requestPermissions(permission, 1234);
            } else {
                openCamera();
            }
        } else {
            openCamera();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
            @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1234:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openCamera();
                else
                    Toast.makeText(this, "Veuillez accepter", Toast.LENGTH_SHORT).show();
                break;
            case 16:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    makeCall();
                else
                    Toast.makeText(this, "Veuillez accepter", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Prendre une photo");
        values.put(MediaStore.Images.Media.DESCRIPTION, "desc");

        currentPhotoPath = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent camintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        camintent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoPath);
        startActivityForResult(camintent, 15);
    }

    // JOINDRE DES FICHIERS
    private void takePictureIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");

        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 50);
    }

    // Make call
    private void makeCall() {
        VideoCall.createMeeting(Chat.this,id);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 200:
                    Uri outputUri = data.getData();
                    addPhoto(outputUri);
                    this.currentPhotoPath = null;
                    break;
                case 15:
                    // Prendre une photo
                    Intent dsPhotoEditorIntent = new Intent(this, DsPhotoEditorActivity.class);
                    int[] toolsToHide = { DsPhotoEditorActivity.TOOL_SHARPNESS };
                    dsPhotoEditorIntent.putExtra(DsPhotoEditorConstants.DS_PHOTO_EDITOR_TOOLS_TO_HIDE, toolsToHide);

                    dsPhotoEditorIntent.setData(this.currentPhotoPath);
                    startActivityForResult(dsPhotoEditorIntent, 200);
                    break;
                case 50:
                    // Joindre des fichiers
                    if (data.getClipData() != null) {
                        for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                            addPhoto(data.getClipData().getItemAt(i).getUri());
                        }
                    } else if (data.getData() != null) {
                        addPhoto(data.getData());
                    }

            }
        }
    }

    // Utilitaires
    private void addPhoto(Uri path) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), path);
            bitmap = Tools.getResizedBitmap(bitmap);

            message.addPhoto(new Photo(path, bitmap));
            binding.photosContainer.setVisibility(View.VISIBLE);

            View vi = getLayoutInflater().inflate(R.layout.item_photo_closable, null);
            ((ImageView) vi.findViewById(R.id.photo)).setImageURI(path);
            vi.setId(path.hashCode());
            vi.findViewById(R.id.close).setOnClickListener(l -> {
                removePhoto(path, vi);
            });

            mPhotosList.addView(vi);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void removePhoto(Uri path, View vi) {
        mPhotosList.removeView(vi);
        message.removePhotos(path);
        if(message.getPhotos().isEmpty())
            binding.photosContainer.setVisibility(View.INVISIBLE);

    }

    public void update() {
        this.chatAdaptater.update();
    }

    private void clear() {
        this.message = new Message("na");
        binding.editText.setText("");
        mPhotosList.removeAllViews();
    }
}
