package com.example.myapplication.fragments;

import static android.app.Activity.RESULT_OK;
import static com.example.myapplication.Utils.Tools.getCroppedBitmap;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.myapplication.R;
import com.example.myapplication.Utils.Converter;
import com.example.myapplication.Utils.Response;
import com.example.myapplication.Utils.RestClient;
import com.example.myapplication.Utils.TextValidator;
import com.example.myapplication.Utils.UIMessage;
import com.example.myapplication.activities.SignOutActivity;
import com.example.myapplication.databinding.FragmentProfileModificationBinding;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.Iterator;

public class ProfileModification extends Fragment {

    private FragmentProfileModificationBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentProfileModificationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        this.setListeners();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String name = sp.getString("name", null);
        String email = sp.getString("email", null);
        String description = sp.getString("description", null);
        String picture = sp.getString("picture", null);
        if (name != null)
            binding.inputNameModify.setText(name);
        if (email != null)
            binding.inputMailModify.setText(email);
        if (description != null)
            binding.inputBioModify.setText(description);
        if (picture != null) {
            binding.inputPicture.setImageBitmap(Converter.stringToBitmap(picture));
            binding.modifyProfilePictureText.setVisibility(View.INVISIBLE);
        }
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setListeners() {
        binding.inputPicture.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(i);
        });

        binding.inputMailModify.addTextChangedListener(
                new TextValidator((binding.inputMailModify), getString(R.string.invalid_email)) {
                    @Override
                    public boolean validate(TextView textView, String text) {
                        return (!TextUtils.isEmpty(text) && Patterns.EMAIL_ADDRESS.matcher(text).matches());
                    }
                });

        binding.inputPasswordModify.addTextChangedListener(
                new TextValidator(binding.inputPasswordModify, getString(R.string.err_bad_password)) {
                    @Override
                    public boolean validate(TextView textView, String text) {
                        return text.length() >= 5;
                    }
                });

        binding.saveProfileModification.setOnClickListener(v -> {
            this.modifyAccount();
        });

        binding.deleteAccountButton.setOnClickListener(v -> {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("token",
                        sp.getString("token", null));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            RestClient.makeRequest("DELETE", getString(R.string.api_rest_user) + sp.getInt("id", -1), jsonBody,
                    getActivity(),
                    new RestClient.Callback() {
                        @Override
                        public void onResponse(Response response) {
                            UIMessage.showText(getString(R.string.account_deleted), getActivity());
                            startActivity(new Intent(getActivity(), SignOutActivity.class));
                            getActivity().finish();
                        }

                        @Override
                        public void onError(Response response) {
                            int responseCode = response.getResponseCode();
                            String msg;
                            if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                                msg = getString(R.string.err_user_info_missing);
                            else if (responseCode == HttpURLConnection.HTTP_CONFLICT)
                                msg = getString(R.string.err_no_user);
                            else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                                msg = getString(R.string.err_contact_server);
                            else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                                msg = getString(R.string.err_session_time_out);
                                startActivity(new Intent(getActivity(), SignOutActivity.class));
                                getActivity().finish();
                            } else
                                msg = getString(R.string.err_unknown);
                            UIMessage.showError(msg, getActivity());
                        }
                    });
        });

        binding.verificationEmail.setOnClickListener(v -> {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            RestClient.makeRequest("POST",
                    getString(R.string.api_rest_send_verification_email), null,
                    getActivity(),
                    new RestClient.Callback() {
                        @Override
                        public void onResponse(Response response) {
                            UIMessage.showText(getString(R.string.check_your_mails), getActivity());
                        }

                        @Override
                        public void onError(Response response) {
                            int responseCode = response.getResponseCode();
                            String msg;
                            if (responseCode == HttpURLConnection.HTTP_CONFLICT)
                                msg = getString(R.string.err_email_already_verified_or_no_user_existing);
                            else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                                msg = getString(R.string.err_user_info_missing);
                            else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                                msg = getString(R.string.err_contact_server);
                            else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                                msg = getString(R.string.err_session_time_out);
                                startActivity(new Intent(getActivity(), SignOutActivity.class));
                                getActivity().finish();
                            } else
                                msg = getString(R.string.err_unknown);
                            UIMessage.showError(msg, getActivity());
                        }
                    });
        });
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageURI = result.getData().getData();

                        try {
                            InputStream inputStream = getActivity().getContentResolver().openInputStream(imageURI);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            bitmap = getCroppedBitmap(bitmap);
                            binding.inputPicture.setImageBitmap(bitmap);
                            binding.modifyProfilePictureText.setVisibility(View.INVISIBLE);

                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            });

    private void modifyAccount() {
        if (TextUtils.isEmpty(binding.inputNameModify.getText().toString().trim()))
            UIMessage.showError(getString(R.string.err_bad_name), getActivity());
        else if (TextUtils.isEmpty(binding.inputMailModify.getText())
                || !Patterns.EMAIL_ADDRESS.matcher(binding.inputMailModify.getText()).matches()) {
            UIMessage.showError(getString(R.string.err_bad_email), getActivity());
        } else if (binding.inputPasswordModify.getText().length() < 5) {
            UIMessage.showError(getString(R.string.err_bad_password), getActivity());
        } else if (!binding.inputSecondPasswordModify.getText().toString()
                .equals(binding.inputPasswordModify.getText().toString())) {
            UIMessage.showError(getString(R.string.err_bad_second_password), getActivity());
        } else {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("name", binding.inputNameModify.getText());
                jsonBody.put("email", binding.inputMailModify.getText());
                if (!binding.inputPasswordModify.getText().equals(""))
                    jsonBody.put("password", binding.inputPasswordModify.getText());
                RoundedImageView image = binding.inputPicture;
                if (image.getDrawable() != null)
                    jsonBody.put("picture", Converter.imageViewToString(image));
                jsonBody.put("description", binding.inputBioModify.getText());

                // Used to identify the user
                jsonBody.put("token",
                        PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("token", null));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            RestClient.makeRequest("PUT", getString(R.string.api_rest_user) + sp.getInt("id", -1), jsonBody,
                    getActivity(),
                    new RestClient.Callback() {
                        @Override
                        public void onResponse(Response response) {
                            JSONObject json = Converter.jsonFromString(response.getResponseString());

                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            SharedPreferences.Editor editor = sp.edit();

                            // Store updated user's information
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
                            UIMessage.showText(getString(R.string.modify_account_valid), getActivity());
                        }

                        @Override
                        public void onError(Response response) {
                            int responseCode = response.getResponseCode();
                            String msg;
                            if (responseCode == HttpURLConnection.HTTP_CONFLICT)
                                msg = getString(R.string.err_no_user_existing);
                            else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                                msg = getString(R.string.err_user_info_missing);
                            else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                                msg = getString(R.string.err_contact_server);
                            else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                                msg = getString(R.string.err_session_time_out);
                                startActivity(new Intent(getActivity(), SignOutActivity.class));
                                getActivity().finish();
                            } else
                                msg = getString(R.string.err_unknown);
                            UIMessage.showError(msg, getActivity());
                        }
                    });
        }
    }
}
