package com.example.myapplication.adapters;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.net.HttpURLConnection;
import java.util.List;

public class UserListAdapter extends ArrayAdapter {

    private int id;

    public UserListAdapter(Context context, List invitations, int id) {
        super(context, 0, invitations);
        this.id = id;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Invitation invitation = (Invitation) getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.groupe_user_item, parent, false);
        }
        TextView userName = convertView.findViewById(R.id.name);
        userName.setText(invitation.getName());
        ImageView userPicture = convertView.findViewById(R.id.picture);
        if (invitation.getPicture() != null)
            userPicture.setImageBitmap(Converter.stringToBitmap(invitation.getPicture()));
        userPicture.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ProfileActivity.class);
            intent.putExtra("id", invitation.getId());
            intent.putExtra("name", invitation.getName());
            getContext().startActivity(intent);
        });
        ImageButton acceptInvitation = convertView.findViewById(R.id.join);
        acceptInvitation.setOnClickListener(l -> {
            RestClient.makeRequest("POST",
                    getContext().getString(R.string.api_rest_groupe) + id
                            + getContext().getString(R.string.api_rest_user)
                            + invitation.getId(),
                    null,
                    getContext(),
                    new RestClient.Callback() {
                        @Override
                        public void onResponse(Response response) {
                            remove(invitation);
                            UIMessage.showText(getContext().getString(R.string.invitation_sent), getContext());
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

}
