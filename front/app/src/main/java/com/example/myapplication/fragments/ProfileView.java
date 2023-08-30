package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.Utils.Converter;
import com.example.myapplication.Utils.Response;
import com.example.myapplication.Utils.RestClient;
import com.example.myapplication.Utils.UIMessage;
import com.example.myapplication.activities.SignOutActivity;
import com.example.myapplication.databinding.FragmentProfileViewBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class ProfileView extends Fragment {

    private FragmentProfileViewBinding binding;
    private int id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentProfileViewBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        if (getArguments() != null) {
            id = getArguments().getInt("id");
        }
        RestClient.makeRequest("GET",
                getString(R.string.api_rest_user) + id, null,
                getActivity(),
                new RestClient.Callback() {
                    @Override
                    public void onResponse(Response response) {
                        JSONObject jsonObject = Converter.jsonFromString(response.getResponseString());
                        try {
                            binding.inputName.setText(jsonObject.getString("name"));
                            if (jsonObject.has("description"))
                                binding.inputBio.setText(jsonObject.getString("description"));
                            if (jsonObject.has("picture")) {
                                binding.inputPicture
                                        .setImageBitmap(Converter.stringToBitmap(jsonObject.getString("picture")));
                            }
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
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
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
