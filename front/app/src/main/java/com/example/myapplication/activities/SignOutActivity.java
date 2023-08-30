package com.example.myapplication.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;

public class SignOutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SignOutActivity.this);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();

        startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        finish();
    }
}
