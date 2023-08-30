package com.example.myapplication.fragments;

import static android.app.Activity.RESULT_OK;
import static com.example.myapplication.Utils.Tools.getCroppedBitmap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.Utils.Converter;
import com.example.myapplication.Utils.Response;
import com.example.myapplication.Utils.RestClient;
import com.example.myapplication.Utils.UIMessage;
import com.example.myapplication.activities.Home;
import com.example.myapplication.activities.ProfileActivity;
import com.example.myapplication.activities.SignOutActivity;
import com.example.myapplication.adapters.GroupeInvitationAdapter;
import com.example.myapplication.adapters.GroupeListAdapter;
import com.example.myapplication.adapters.MemberAdapter;
import com.example.myapplication.adapters.UserInvitationAdapter;
import com.example.myapplication.adapters.UserListAdapter;
import com.example.myapplication.databinding.FragmentGroupeProfileAdministrationBinding;
import com.example.myapplication.objects.Invitation;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

public class GroupeProfileAdministration extends Fragment {

    private FragmentGroupeProfileAdministrationBinding binding;
    private int id;
    private List<Invitation> arrayOfInvitations = new ArrayList<>();
    private List<Integer> arrayOfMembers = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            id = getArguments().getInt("id");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentGroupeProfileAdministrationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        this.setListeners();
        this.actualize();
        this.setMembers();

        SearchView searchView = binding.searchView;
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                binding.listUser.setVisibility(View.VISIBLE);
                searchUser(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ListView groupes = binding.listUser;
                if (newText.isEmpty()) {
                    UserListAdapter mHistory = new UserListAdapter(getActivity(), new ArrayList(), id);
                    groupes.setAdapter(mHistory);
                    binding.listUser.setVisibility(View.INVISIBLE);
                }
                return false;
            }
        });

        ListView listView = binding.groupeInvitation;
        RestClient.makeRequest("GET",
                getString(R.string.api_rest_groupe) + id + getString(R.string.api_rest_invitation),
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
                            binding.groupeInvitation
                                    .setVisibility(arrayOfInvitations.size() == 0 ? View.INVISIBLE : View.VISIBLE);
                            GroupeInvitationAdapter mHistory = new GroupeInvitationAdapter(getActivity(),
                                    arrayOfInvitations, id);
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
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void setListeners() {
        binding.inputGroupePicture.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(i);
        });

        binding.saveProfileModification.setOnClickListener(v -> {
            this.modifyGroupe();
        });

        binding.deleteGroupeButton.setOnClickListener(v -> {
            RestClient.makeRequest("DELETE", getString(R.string.api_rest_groupe) + id, null,
                    getActivity(),
                    new RestClient.Callback() {
                        @Override
                        public void onResponse(Response response) {
                            UIMessage.showText(getString(R.string.groupe_deleted), getActivity());
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
                            binding.inputGroupePicture.setImageBitmap(bitmap);
                            binding.modifyGroupeProfilePictureText.setVisibility(View.INVISIBLE);

                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            });

    private void modifyGroupe() {
        if (TextUtils.isEmpty(binding.inputGroupeNameModify.getText().toString().trim())) {
            UIMessage.showError(getString(R.string.err_bad_name), getActivity());
        } else {
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("name", binding.inputGroupeNameModify.getText());
                RoundedImageView image = binding.inputGroupePicture;
                if (image.getDrawable() != null)
                    jsonBody.put("picture", Converter.imageViewToString(image));
                jsonBody.put("description", binding.inputGroupeBioModify.getText());
            } catch (JSONException e) {
                e.printStackTrace();
            }

            RestClient.makeRequest("PUT", getString(R.string.api_rest_groupe) + id, jsonBody,
                    getActivity(),
                    new RestClient.Callback() {
                        @Override
                        public void onResponse(Response response) {
                            UIMessage.showText(getString(R.string.modify_account_valid), getActivity());
                        }

                        @Override
                        public void onError(Response response) {
                            int responseCode = response.getResponseCode();
                            String msg;
                            if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                                msg = getString(R.string.err_user_info_missing);
                            else if (responseCode == HttpURLConnection.HTTP_CONFLICT)
                                msg = getString(R.string.err_no_user_no_group);
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
        }
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
                                binding.inputGroupePicture
                                        .setImageBitmap(Converter.stringToBitmap(jsonObject.getString("picture")));
                                binding.modifyGroupeProfilePictureText.setVisibility(View.INVISIBLE);
                            }
                            binding.inputGroupeNameModify.setText(jsonObject.getString("name"));
                            if (jsonObject.has("description")) {
                                binding.inputGroupeBioModify.setText(jsonObject.getString("description"));
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

    private void searchUser(String name) {
        List<Invitation> arrayOfUsers = new ArrayList<>();
        ListView listOfUsers = binding.listUser;
        RestClient.makeRequest("GET", getString(R.string.api_rest_users) + name,
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
                                arrayOfUsers.add(
                                        new Invitation(jsonObject.getInt("id"), jsonObject.getString("name"), picture));
                            }
                        } catch (org.json.JSONException e) {
                            e.printStackTrace();
                        }
                        UserListAdapter mHistory = new UserListAdapter(getActivity(), arrayOfUsers, id);
                        listOfUsers.setAdapter(mHistory);
                        listOfUsers.setVisibility(arrayOfUsers.size() == 0 ? View.INVISIBLE : View.VISIBLE);
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

    private void setMembers() {
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
    }
}
