package com.example.myapplication.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.Utils.Converter;
import com.example.myapplication.Utils.Response;
import com.example.myapplication.Utils.RestClient;
import com.example.myapplication.Utils.TextValidator;
import com.example.myapplication.Utils.UIMessage;
import com.example.myapplication.databinding.ActivitySignInBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;

import tech.gusavila92.websocketclient.WebSocketClient;

public class SignInActivity extends AppCompatActivity {

    private ActivitySignInBinding binding;

    WebSocketClient webSocketClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SignInActivity.this);

        String token = sp.getString("token", null);
        if (token != null) {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("token", sp.getString("token", null));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendRequest(jsonBody, getString(R.string.hello_again));
        }
        this.setListeners();
        setContentView(binding.getRoot());

    }

    private void setListeners() {
        binding.newAccount.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), SignUpActivity.class));
        });

        binding.inputMail.addTextChangedListener(new TextValidator(binding.inputMail) {
            @Override
            public boolean validate(TextView textView, String text) {
                return (!TextUtils.isEmpty(text) && Patterns.EMAIL_ADDRESS.matcher(text).matches());
            }
        });

        binding.login.setOnClickListener(v -> {
            this.login();
        });
    }

    private void login() {
        if (!(!TextUtils.isEmpty(binding.inputMail.getText())
                && Patterns.EMAIL_ADDRESS.matcher(binding.inputMail.getText()).matches())) {
            UIMessage.showError(getString(R.string.err_bad_email), this);
        } else if (!binding.inputPassword.getText().toString().equals(binding.inputPassword.getText().toString())) {
            UIMessage.showError(getString(R.string.err_bad_password), this);
        } else {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("email", binding.inputMail.getText());
                jsonBody.put("password", binding.inputPassword.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            sendRequest(jsonBody, getString(R.string.welcome));
        }
    }

    private void sendRequest(JSONObject jsonBody, String message) {
        RestClient.makeRequest("POST", getString(R.string.api_rest_login), jsonBody, SignInActivity.this,
                new RestClient.Callback() {
                    @Override
                    public void onResponse(Response response) {
                        JSONObject json = Converter.jsonFromString(response.getResponseString());

                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(SignInActivity.this);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();

                        try {
                            for (Iterator iterator = json.keys(); iterator.hasNext();) {
                                String key = (String) iterator.next();
                                Object val = json.get(String.valueOf(key));
                                if (val.getClass() == Boolean.class)
                                    editor.putBoolean(key, (Boolean) val);
                                else if (val.getClass() == Integer.class)
                                    editor.putInt(key, (Integer) val);
                                else
                                    editor.putString(key, (String) val);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        editor.apply();

                        UIMessage.showText(message, SignInActivity.this);
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        finish();
                    }

                    @Override
                    public void onError(Response response) {
                        if (!message.equals(getString(R.string.hello_again))) {
                            int responseCode = response.getResponseCode();
                            String msg;
                            if (responseCode == HttpURLConnection.HTTP_CONFLICT)
                                msg = getString(R.string.err_no_user_existing);
                            else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                                msg = getString(R.string.err_user_info_missing);
                            else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                                msg = getString(R.string.err_contact_server);
                            else
                                msg = getString(R.string.err_unknown);
                            UIMessage.showError(msg, SignInActivity.this);
                        }
                    }
                });
    }
}
