package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.myapplication.R;
import com.example.myapplication.fragments.Editor;

public class EditPicture extends AppCompatActivity {
    Bitmap bitmap ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(new MyCanvas(this));

    }

    private void setListeners() {
        getSupportFragmentManager().beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.frag, Editor.class, null)
                .commit();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        startActivity(new Intent(getApplicationContext(), Chat.class));
        return true;
    }
}