package com.example.myapplication.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.myapplication.R;
import com.example.myapplication.databinding.ActivityGroupeProfileBinding;
import com.example.myapplication.fragments.GroupeProfileAdministration;
import com.example.myapplication.fragments.GroupeProfileInformation;

import java.util.Objects;

public class GroupeProfileActivity extends AppCompatActivity {

    private ActivityGroupeProfileBinding binding;
    private int id;
    private String role;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGroupeProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        role = intent.getStringExtra("role");
        name = intent.getStringExtra("name");

        binding.groupeRadioGroup
                .setVisibility(role.equals(getString(R.string.role_admin)) ? View.VISIBLE : View.INVISIBLE);

        Toolbar toolbar = findViewById(R.id.groupe_profile_toolbar);
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
            ((TextView) findViewById(R.id.groupe_toolbar_title)).setText(name);
        }
        this.setListeners();

        Fragment informationFragment = new GroupeProfileInformation();
        informationFragment.setArguments(getBundle());
        startAction(informationFragment);
    }

    private void setListeners() {
        binding.groupeRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == binding.radioButtonInformation.getId()) {
                    Fragment informationFragment = new GroupeProfileInformation();
                    informationFragment.setArguments(getBundle());
                    startAction(informationFragment);
                } else if (checkedId == binding.radioButtonAdministration.getId()) {
                    Fragment administrationFragment = new GroupeProfileAdministration();
                    administrationFragment.setArguments(getBundle());
                    startAction(administrationFragment);
                }
            }
        });
        binding.groupeRadioGroup.check(R.id.radioButtonInformation);
    }

    private Bundle getBundle() {
        Bundle args = new Bundle();
        args.putInt("id", id);
        args.putString("role", role);
        return args;
    }

    private void startAction(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.groupe_fragment_container, fragment);
        fragmentTransaction.commit();
    }
}
