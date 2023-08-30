package com.example.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.myapplication.R;
import com.example.myapplication.Utils.Converter;
import com.example.myapplication.Utils.Response;
import com.example.myapplication.Utils.RestClient;
import com.example.myapplication.Utils.UIMessage;
import com.example.myapplication.activities.GroupeProfileActivity;
import com.example.myapplication.activities.ProfileActivity;
import com.example.myapplication.activities.SignOutActivity;
import com.example.myapplication.objects.Invitation;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

public class MemberAdapter extends ArrayAdapter {

    private int id;
    private String role;

    public MemberAdapter(Context context, List invitations, int id, String role) {
        super(context, 0, invitations);
        this.id = id;
        this.role = role;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Invitation invitation = (Invitation) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.member_item, parent, false);
        }
        TextView groupName = convertView.findViewById(R.id.groupName);
        groupName.setText(invitation.getName());
        ImageView groupPicture = convertView.findViewById(R.id.groupPicture);
        if (invitation.getPicture() != null)
            groupPicture.setImageBitmap(Converter.stringToBitmap(invitation.getPicture()));
        groupPicture.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            intent.putExtra("id", invitation.getId());
            intent.putExtra("name", invitation.getName());
            getContext().startActivity(intent);
        });
        Spinner spinnerRoles = convertView.findViewById(R.id.groupRole);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(), R.array.roles,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerRoles.setAdapter(adapter);
        spinnerRoles.setSelection(
                Arrays.asList(convertView.getResources().getStringArray(R.array.roles)).indexOf(invitation.getRole()));
        spinnerRoles.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (invitation.isInitialSelection()) {
                    invitation.setInitialSelection(false);
                } else {
                    changeMemberRole(parentView.getItemAtPosition(position).toString(), invitation.getId());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        ImageButton refuseInvitation = convertView.findViewById(R.id.refuseInvitation);
        if (role.equals(getContext().getString(R.string.role_admin))
                && PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("id", -1) != invitation.getId()) {
            refuseInvitation.setVisibility(View.VISIBLE);
            spinnerRoles.setEnabled(true);
        } else {
            refuseInvitation.setVisibility(View.INVISIBLE);
            spinnerRoles.setEnabled(false);
        }
        refuseInvitation.setOnClickListener(l -> {
            RestClient.makeRequest("DELETE",
                    getContext().getString(R.string.api_rest_groupe) + id
                            + getContext().getString(R.string.api_rest_user)
                            + invitation.getId(),
                    null,
                    getContext(),
                    new RestClient.Callback() {
                        @Override
                        public void onResponse(Response response) {
                            remove(invitation);
                            notifyDataSetChanged();
                            UIMessage.showText(getContext().getString(R.string.user_ejected), getContext());
                        }

                        @Override
                        public void onError(Response response) {
                            int responseCode = response.getResponseCode();
                            String msg;
                            if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                                msg = getContext().getString(R.string.err_user_info_missing);
                            else if (responseCode == HttpURLConnection.HTTP_CONFLICT)
                                msg = getContext().getString(R.string.err_no_user_no_group);
                            else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN)
                                msg = getContext().getString(R.string.err_privilege);
                            else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                                msg = getContext().getString(R.string.err_contact_server);
                            else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                                msg = getContext().getString(R.string.err_session_time_out);
                                getContext().startActivity(new Intent(getContext(), SignOutActivity.class));
                            } else
                                msg = getContext().getString(R.string.err_unknown);
                            UIMessage.showError(msg, getContext());
                        }
                    });
        });
        return convertView;
    }

    private void changeMemberRole(String newRole, int memberID) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("role", newRole);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RestClient.makeRequest("PUT",
                getContext().getString(R.string.api_rest_groupe) + id
                        + getContext().getString(R.string.api_rest_user)
                        + memberID,
                jsonBody,
                getContext(),
                new RestClient.Callback() {
                    @Override
                    public void onResponse(Response response) {
                        UIMessage.showText(getContext().getString(R.string.role_changed), getContext());
                    }

                    @Override
                    public void onError(Response response) {
                        int responseCode = response.getResponseCode();
                        String msg;
                        if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                            msg = getContext().getString(R.string.err_user_info_missing);
                        else if (responseCode == HttpURLConnection.HTTP_CONFLICT)
                            msg = getContext().getString(R.string.err_no_user_no_group);
                        else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN)
                            msg = getContext().getString(R.string.err_privilege);
                        else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                            msg = getContext().getString(R.string.err_contact_server);
                        else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                            msg = getContext().getString(R.string.err_session_time_out);
                            getContext().startActivity(new Intent(getContext(), SignOutActivity.class));
                        } else
                            msg = getContext().getString(R.string.err_unknown);
                        UIMessage.showError(msg, getContext());
                    }
                });
    }

}
