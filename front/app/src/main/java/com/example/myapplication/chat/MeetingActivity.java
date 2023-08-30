package com.example.myapplication.chat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myapplication.R;

import live.videosdk.rtc.android.Meeting;
import live.videosdk.rtc.android.Participant;
import live.videosdk.rtc.android.VideoSDK;
import live.videosdk.rtc.android.listeners.MeetingEventListener;

public class MeetingActivity extends AppCompatActivity {
    // declare the variables we will be using to handle the meeting
    private Meeting meeting;
    private boolean micEnabled = true;
    private boolean webcamEnabled = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting);

        final String token = getIntent().getStringExtra("token");
        final String meetingId = getIntent().getStringExtra("meetingId");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);

        final String participantName = sp.getString("name","");

        // 1. Configuration VideoSDK with Token
        VideoSDK.config(token);
        // 2. Initialize VideoSDK Meeting
        meeting = VideoSDK.initMeeting(
                MeetingActivity.this, meetingId, participantName,
                micEnabled, webcamEnabled,null, null, null);

        // 3. Add event listener for listening upcoming events
        meeting.addEventListener(meetingEventListener);

        //4. Join VideoSDK Meeting
        meeting.join();

        ((TextView)findViewById(R.id.tvMeetingId)).setText(meetingId);

        final RecyclerView rvParticipants = findViewById(R.id.rvParticipants);
        rvParticipants.setLayoutManager(new GridLayoutManager(this, 2));
        rvParticipants.setAdapter(new ParticipantAdapter(meeting));

        this.setActionListeners();
    }


    private void setActionListeners() {
        // toggle mic
        findViewById(R.id.btnMic).setOnClickListener(view -> {
            if (micEnabled) {
                // this will mute the local participant's mic
                meeting.muteMic();
                Toast.makeText(MeetingActivity.this, "Mic Disabled", Toast.LENGTH_SHORT).show();
            } else {
                // this will unmute the local participant's mic
                meeting.unmuteMic();
                Toast.makeText(MeetingActivity.this, "Mic Enabled", Toast.LENGTH_SHORT).show();
            }
            micEnabled=!micEnabled;
        });

        // toggle webcam
        findViewById(R.id.btnWebcam).setOnClickListener(view -> {
            if (webcamEnabled) {
                // this will disable the local participant webcam
                meeting.disableWebcam();
                Toast.makeText(MeetingActivity.this, "Webcam Disabled", Toast.LENGTH_SHORT).show();
            } else {
                // this will enable the local participant webcam
                meeting.enableWebcam();
                Toast.makeText(MeetingActivity.this, "Webcam Enabled", Toast.LENGTH_SHORT).show();
            }
            webcamEnabled=!webcamEnabled;
        });

        // leave meeting
        findViewById(R.id.btnLeave).setOnClickListener(view -> {
            // this will make the local participant leave the meeting
            meeting.leave();
        });
    }

    // creating the MeetingEventListener
    private final MeetingEventListener meetingEventListener = new MeetingEventListener() {
        @Override
        public void onMeetingJoined() {
        }

        @Override
        public void onMeetingLeft() {
            meeting = null;
            if (!isDestroyed()) finish();
        }

        @Override
        public void onParticipantJoined(Participant participant) {
            Toast.makeText(MeetingActivity.this, participant.getDisplayName() + " joined", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onParticipantLeft(Participant participant) {
            Toast.makeText(MeetingActivity.this, participant.getDisplayName() + " left", Toast.LENGTH_SHORT).show();
        }
    };
}
