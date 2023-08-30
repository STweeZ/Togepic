package com.example.myapplication.fragments;

import static android.app.Activity.RESULT_OK;
import static com.example.myapplication.Utils.Tools.getCroppedBitmap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.example.myapplication.R;
import com.example.myapplication.Utils.Converter;
import com.example.myapplication.Utils.Response;
import com.example.myapplication.Utils.RestClient;
import com.example.myapplication.Utils.UIMessage;
import com.example.myapplication.activities.SignOutActivity;
import com.example.myapplication.activities.SignUpActivity;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class CreateGroupDialog extends DialogFragment {

    public static String TAG = "CreateGroupDialog";

    public static CreateGroupDialog newInstance() {
        CreateGroupDialog f = new CreateGroupDialog();
        Bundle args = new Bundle();
        f.setArguments(args);
        return f;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View v = inflater.inflate(R.layout.create_group_dialog, null);
        builder.setView(v);
        setListeners(v);
        return builder.create();
    }

    private void setListeners(View v) {
        v.findViewById(R.id.newGroupPicture).setOnClickListener(b -> {
            Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            pickImage.launch(i);
        });

        v.findViewById(R.id.cancel).setOnClickListener(b -> {
            CreateGroupDialog.this.getDialog().cancel();
        });
        v.findViewById(R.id.create).setOnClickListener(b -> {
            if (TextUtils.isEmpty(((TextView) v.findViewById(R.id.groupName)).getText().toString().trim())) {
                UIMessage.showError(getString(R.string.err_bad_name), getActivity());
            } else {
                JSONObject jsonBody = new JSONObject();
                try {
                    jsonBody.put("name", ((EditText) v.findViewById(R.id.groupName)).getText());
                    jsonBody.put("description", ((EditText) v.findViewById(R.id.groupDesc)).getText());
                    jsonBody.put("isPrivate", ((Switch) v.findViewById(R.id.isPrivate)).isChecked());
                    RoundedImageView image = v.findViewById(R.id.newGroupPicture);
                    if (image.getDrawable() != null)
                        jsonBody.put("picture", Converter.imageViewToString(image));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                RestClient.makeRequest("POST", getString(R.string.api_rest_groupe), jsonBody, getContext(),
                    new RestClient.Callback() {
                        @Override
                        public void onResponse(Response response) {
                            UIMessage.showText("Group created!", getActivity());
                            CreateGroupDialog.this.getDialog().cancel();
                            getActivity().recreate();
                        }

                        @Override
                        public void onError(Response response) {
                            int responseCode = response.getResponseCode();
                            String msg;
                            if (responseCode == HttpURLConnection.HTTP_FORBIDDEN)
                                msg = getString(R.string.err_user_not_verified);
                            else if (responseCode == HttpURLConnection.HTTP_BAD_REQUEST)
                                msg = getString(R.string.err_user_info_missing);
                            else if (responseCode == HttpURLConnection.HTTP_INTERNAL_ERROR)
                                msg = getString(R.string.err_contact_server);
                            else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                                msg = getString(R.string.err_session_time_out);
                                startActivity(new Intent(getActivity(), SignOutActivity.class));
                                getActivity().finish();
                            }
                            else
                                msg = getString(R.string.err_unknown);
                            UIMessage.showError(msg, getActivity());
                        }
                    });
        }
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
                            ((RoundedImageView) getDialog().findViewById(R.id.newGroupPicture)).setImageBitmap(bitmap);
                            ((ImageView) getDialog().findViewById(R.id.newGroupPictureText)).setVisibility(View.INVISIBLE);

                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            });

}
