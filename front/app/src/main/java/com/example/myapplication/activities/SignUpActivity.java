package com.example.myapplication.activities;

import static com.example.myapplication.Utils.Tools.getCroppedBitmap;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.Utils.Converter;
import com.example.myapplication.Utils.MessagingService;
import com.example.myapplication.Utils.Response;
import com.example.myapplication.Utils.RestClient;
import com.example.myapplication.Utils.TextValidator;
import com.example.myapplication.Utils.UIMessage;
import com.example.myapplication.databinding.ActivitySignUpBinding;
import com.google.firebase.FirebaseApp;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        this.setListeners();
        FirebaseApp.initializeApp(this.getApplicationContext());
        MessagingService.setFCMToken(this.getPreferences(Context.MODE_PRIVATE));
        setContentView(binding.getRoot());
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageURI = result.getData().getData();
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(imageURI);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            bitmap = getCroppedBitmap(bitmap);
                            binding.userPicture.setImageBitmap(bitmap);
                            binding.addProfilePictureText.setVisibility(View.INVISIBLE);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            });

    private void setListeners() {

        binding.userPicture.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(i);
        });

        binding.userMail.addTextChangedListener(new TextValidator(binding.userMail, getString(R.string.err_bad_email)) {
            @Override
            public boolean validate(TextView textView, String text) {
                return (!TextUtils.isEmpty(text) && Patterns.EMAIL_ADDRESS.matcher(text).matches());
            }
        });

        binding.userPassword.addTextChangedListener(
                new TextValidator(binding.userPassword, getString(R.string.err_bad_password)) {
                    @Override
                    public boolean validate(TextView textView, String text) {
                        return text.length() >= 5;
                    }
                });

        binding.newAccount.setOnClickListener(v -> {
            onBackPressed();
            finish();
        });

        binding.createAccount.setOnClickListener(v -> {
            this.createAccount();
        });
    }

    private void createAccount() {
        if (TextUtils.isEmpty(binding.userName.getText().toString().trim())) {
            UIMessage.showError(getString(R.string.err_bad_name), SignUpActivity.this);
        } else if (!(!TextUtils.isEmpty(binding.userMail.getText())
                && Patterns.EMAIL_ADDRESS.matcher(binding.userMail.getText()).matches())) {
            UIMessage.showError(getString(R.string.err_bad_email), SignUpActivity.this);
        } else if (binding.userPassword.getText().length() < 5) {
            UIMessage.showError(getString(R.string.err_bad_password), SignUpActivity.this);
        } else if (!binding.userSecondPassword.getText().toString()
                .equals(binding.userPassword.getText().toString())) {
            UIMessage.showError(getString(R.string.err_bad_second_password), SignUpActivity.this);
        } else {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("email", binding.userMail.getText());
                jsonBody.put("password", binding.userPassword.getText());
                jsonBody.put("name", binding.userName.getText());
                jsonBody.put("FCMToken",MessagingService.getFCMToken(this.getPreferences(Context.MODE_PRIVATE)));
                RoundedImageView image = binding.userPicture;
                if (image.getDrawable() != null)
                    jsonBody.put("picture", Converter.imageViewToString(image));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RestClient.makeRequest("POST", getString(R.string.api_rest_user), jsonBody, SignUpActivity.this,
                    new RestClient.Callback() {
                        @Override
                        public void onResponse(Response response) {
                            UIMessage.showText(getString(R.string.create_account_valid), SignUpActivity.this);
                            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
                            finish();
                        }

                        @Override
                        public void onError(Response response) {
                            int responseCode = response.getResponseCode();
                            String msg;
                            if (responseCode == HttpURLConnection.HTTP_CONFLICT)
                                msg = getString(R.string.err_email_already_used);
                            else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                                msg = getString(R.string.err_user_info_missing);
                            else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                                msg = getString(R.string.err_contact_server);
                            else
                                msg = getString(R.string.err_unknown);
                            UIMessage.showError(msg, SignUpActivity.this);
                        }
                    });
        }
    }
}
