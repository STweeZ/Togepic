package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.Utils.Converter;
import com.example.myapplication.Utils.Response;
import com.example.myapplication.Utils.RestClient;
import com.example.myapplication.Utils.UIMessage;
import com.example.myapplication.activities.Home;
import com.example.myapplication.activities.SignOutActivity;
import com.example.myapplication.adapters.MemberAdapter;
import com.example.myapplication.databinding.FragmentGroupeProfileInformationBinding;
import com.example.myapplication.objects.Invitation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class GroupeProfileInformation extends Fragment {

    private FragmentGroupeProfileInformationBinding binding;
    private int id;
    private String role;
    private List<Invitation> arrayOfMembers = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt("id");
            role = String.valueOf(getArguments().getString("role"));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentGroupeProfileInformationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        this.setListeners();
        this.actualize();

        binding.groupeMember.setVisibility(role.isEmpty() ? View.INVISIBLE : View.VISIBLE);
        binding.quitGroupButton.setVisibility(role.isEmpty() ? View.INVISIBLE : View.VISIBLE);

        ListView listView = binding.groupeMember;
        if (!role.isEmpty()) {
            RestClient.makeRequest("GET", getString(R.string.api_rest_groupe) + id + getString(R.string.api_rest_user),
                    null,
                    getActivity(),
                    new RestClient.Callback() {
                        @Override
                        public void onResponse(Response response) {
                            JSONArray jsonArray = Converter.arrayFromString(response.getResponseString());
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    String picture = null;
                                    if (jsonObject.has("picture")) {
                                        picture = jsonObject.getString("picture");
                                    }
                                    arrayOfMembers.add(
                                            new Invitation(jsonObject.getInt("id"), jsonObject.getString("name"),
                                                    picture,
                                                    jsonObject.getString("role")));
                                }
                                binding.invitationInformation
                                        .setVisibility(arrayOfMembers.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                                binding.groupeMember
                                        .setVisibility(arrayOfMembers.size() == 0 ? View.INVISIBLE : View.VISIBLE);
                                MemberAdapter mHistory = new MemberAdapter(getActivity(), arrayOfMembers, id, role);
                                listView.setAdapter(mHistory);
                            } catch (org.json.JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Response response) {
                            int responseCode = response.getResponseCode();
                            String msg;
                            if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                                msg = getString(R.string.err_user_info_missing);
                            else if (responseCode == HttpURLConnection.HTTP_CONFLICT)
                                msg = getString(R.string.err_no_group);
                            else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN)
                                msg = getString(R.string.err_privilege);
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
        } else {
            binding.invitationInformation.setVisibility(View.INVISIBLE);
        }
        return root;
    }

    private void setListeners() {
        binding.quitGroupButton.setOnClickListener(v -> {
            RestClient.makeRequest("DELETE",
                    getString(R.string.api_rest_user)
                            + PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("id", -1)
                            + getString(R.string.api_rest_groupe) + id,
                    null,
                    getActivity(),
                    new RestClient.Callback() {
                        @Override
                        public void onResponse(Response response) {
                            UIMessage.showText(getString(R.string.groupe_left), getActivity());
                            startActivity(new Intent(getActivity(), Home.class));
                            getActivity().finish();
                        }

                        @Override
                        public void onError(Response response) {
                            int responseCode = response.getResponseCode();
                            String msg;
                            if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                                msg = getString(R.string.err_user_info_missing);
                            else if (responseCode == HttpURLConnection.HTTP_CONFLICT)
                                msg = getString(R.string.err_no_user_no_group);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void actualize() {
        RestClient.makeRequest("GET", getString(R.string.api_rest_groupe) + id,
                null,
                getActivity(),
                new RestClient.Callback() {
                    @Override
                    public void onResponse(Response response) {
                        JSONObject jsonObject = Converter.jsonFromString(response.getResponseString());
                        try {
                            if (jsonObject.has("picture")) {
                                binding.groupePicture
                                        .setImageBitmap(Converter.stringToBitmap(jsonObject.getString("picture")));
                            }
                            binding.groupeName.setText(jsonObject.getString("name"));
                            if (jsonObject.has("description")) {
                                binding.groupeBio.setText(jsonObject.getString("description"));
                            }
                        } catch (org.json.JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Response response) {
                        int responseCode = response.getResponseCode();
                        String msg;
                        if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                            msg = getString(R.string.err_user_info_missing);
                        else if (responseCode == HttpURLConnection.HTTP_CONFLICT)
                            msg = getString(R.string.err_no_group);
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
