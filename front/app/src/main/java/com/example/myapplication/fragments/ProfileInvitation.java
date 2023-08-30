package com.example.myapplication.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import com.example.myapplication.R;
import com.example.myapplication.Utils.Converter;
import com.example.myapplication.Utils.Response;
import com.example.myapplication.Utils.RestClient;
import com.example.myapplication.Utils.UIMessage;
import com.example.myapplication.activities.SignOutActivity;
import com.example.myapplication.adapters.GroupeListAdapter;
import com.example.myapplication.adapters.UserInvitationAdapter;
import com.example.myapplication.databinding.FragmentProfileInvitationBinding;
import com.example.myapplication.objects.Invitation;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class ProfileInvitation extends Fragment {

    private FragmentProfileInvitationBinding binding;
    List<Invitation> arrayOfInvitations = new ArrayList<>();
    private List<Integer> arrayOfMembers = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentProfileInvitationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        this.setGroupes();

        SearchView searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchGroupe(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ListView groupes = binding.listGroupe;
                if (newText.isEmpty()) {
                    GroupeListAdapter mHistory = new GroupeListAdapter(getActivity(), new ArrayList());
                    groupes.setAdapter(mHistory);
                    binding.listGroupe.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        ListView listView = binding.userInvitation;
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        RestClient.makeRequest("GET",
                getString(R.string.api_rest_user) + sp.getInt("id", -1) + getString(R.string.api_rest_invitation),
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
                                arrayOfInvitations.add(
                                        new Invitation(jsonObject.getInt("id"), jsonObject.getString("name"), picture));
                            }
                            binding.invitationInformation
                                    .setVisibility(arrayOfInvitations.size() == 0 ? View.VISIBLE : View.INVISIBLE);
                            binding.userInvitation
                                    .setVisibility(arrayOfInvitations.size() == 0 ? View.INVISIBLE : View.VISIBLE);
                            UserInvitationAdapter mHistory = new UserInvitationAdapter(getActivity(),
                                    arrayOfInvitations);
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
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void searchGroupe(String name) {
        List<Invitation> arrayOfGroupes = new ArrayList<>();
        ListView listOfGroupes = binding.listGroupe;
        RestClient.makeRequest("GET", getString(R.string.api_rest_groupes) + name,
                null,
                getActivity(),
                new RestClient.Callback() {
                    @Override
                    public void onResponse(Response response) {
                        JSONArray jsonArray = Converter.arrayFromString(response.getResponseString());
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (arrayOfMembers.contains(jsonObject.getInt("id"))) {
                                    continue;
                                }
                                String picture = null;
                                if (jsonObject.has("picture")) {
                                    picture = jsonObject.getString("picture");
                                }
                                arrayOfGroupes.add(
                                        new Invitation(jsonObject.getInt("id"), jsonObject.getString("name"), picture));
                            }
                        } catch (org.json.JSONException e) {
                            e.printStackTrace();
                        }
                        GroupeListAdapter mHistory = new GroupeListAdapter(getActivity(), arrayOfGroupes);
                        listOfGroupes.setAdapter(mHistory);
                        listOfGroupes.setVisibility(arrayOfGroupes.size() == 0 ? View.INVISIBLE : View.VISIBLE);
                    }

                    @Override
                    public void onError(Response response) {
                        int responseCode = response.getResponseCode();
                        String msg;
                        if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
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

    private void setGroupes() {
        RestClient.makeRequest("GET",
                getString(R.string.api_rest_user)
                        + PreferenceManager.getDefaultSharedPreferences(getActivity()).getInt("id", -1)
                        + getString(R.string.api_rest_groupe),
                null,
                getActivity(),
                new RestClient.Callback() {
                    @Override
                    public void onResponse(Response response) {
                        JSONArray jsonArray = Converter.arrayFromString(response.getResponseString());
                        try {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                arrayOfMembers.add(jsonObject.getInt("id"));
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
    }
}
