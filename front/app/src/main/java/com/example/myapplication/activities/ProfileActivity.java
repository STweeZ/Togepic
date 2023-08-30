package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityProfileBinding;
import com.example.myapplication.fragments.ProfileInvitation;
import com.example.myapplication.fragments.ProfileModification;
import com.example.myapplication.fragments.ProfileView;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private int id;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        name = intent.getStringExtra("name");

        Toolbar toolbar = findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (name != null) {
            binding.toolbarTitle.setText(name);
        }
        this.setListeners();

        Fragment informationFragment;
        if (id != (PreferenceManager.getDefaultSharedPreferences(this).getInt("id", -1))) {
            binding.radioGroup.setVisibility(View.INVISIBLE);
            informationFragment = new ProfileView();
            Bundle args = new Bundle();
            args.putInt("id", id);
            informationFragment.setArguments(args);
            binding.logoutPic.setVisibility(View.INVISIBLE);
        } else {
            binding.radioGroup.setVisibility(View.VISIBLE);
            informationFragment = new ProfileModification();
            binding.logoutPic.setVisibility(View.VISIBLE);
        }
        startAction(informationFragment);
    }

    private void setListeners() {
        binding.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == binding.radioButtonModification.getId())
                    startAction(new ProfileModification());
                else if (checkedId == binding.radioButtonInvitation.getId())
                    startAction(new ProfileInvitation());
            }
        });
        binding.radioGroup.check(R.id.radioButtonModification);

        binding.logoutPic.setOnClickListener(v -> {
            startActivity(new Intent(ProfileActivity.this, SignOutActivity.class));
            finish();
        });
    }

    private void startAction(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
