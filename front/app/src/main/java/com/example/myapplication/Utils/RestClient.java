package com.example.myapplication.Utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.preference.PreferenceManager;
import android.util.Log;

import com.example.myapplication.R;

import java.io.IOException;
import java.io.InputStream;

public class RestClient {

    public interface Callback {
        void onResponse(Response response);

        void onError(Response errorMessage);
    }

    public static void makeRequest(final String method, final String apiUrl, final JSONObject jsonBody, Context context, final Callback callback) {
        RestClient.makeRequest(method,apiUrl,jsonBody,context,callback,true);
    }


    public static void makeRequest(final String method, final String apiUrl, final JSONObject jsonBody,Context context, final Callback callback,boolean showProgressbar) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        new AsyncTask<Void, Void, Response>() {
            @Override
            protected void onPreExecute() {
                if(!showProgressbar) return;
                progressDialog.setTitle(context.getString(R.string.loading));
                progressDialog.setMessage(context.getString(R.string.please_wait));
                progressDialog.show();
            }

            @Override
            protected Response doInBackground(Void... params) {
                HttpURLConnection connection = null;
                try {
                    URL url = new URL(context.getString(R.string.api_rest_url) + apiUrl);
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod(method);
                    connection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                    String token = PreferenceManager.getDefaultSharedPreferences(context).getString("token", null);
                    if (token != null) {
                        connection.setRequestProperty("Authorization",  "Bearer " + token);
                    }
                    if (!method.equals("GET")) {
                        connection.setDoOutput(true);
                        connection.setDoInput(true);
                        if (jsonBody != null) {
                            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                            writer.write(jsonBody.toString());
                            writer.flush();
                            writer.close();
                        }
                    }

                    int statusCode = connection.getResponseCode();
                    String responseString = "";

                    if (statusCode != HttpURLConnection.HTTP_OK && statusCode != HttpURLConnection.HTTP_NO_CONTENT)
                        Log.e(RestClient.class.getSimpleName(),
                                context.getString(R.string.communication_fail_status) + statusCode);
                    else {
                        InputStream inputStream = connection.getInputStream();
                        responseString = convertStreamToString(inputStream);
                    }

                    return new Response(statusCode, responseString);
                } catch (IOException e) {
                    e.printStackTrace();
                    return new Response(HttpURLConnection.HTTP_INTERNAL_ERROR, null);
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }

            @Override
            protected void onPostExecute(Response response) {
                progressDialog.dismiss();
                if (response.getResponseCode() != HttpURLConnection.HTTP_OK
                        && response.getResponseCode() != HttpURLConnection.HTTP_NO_CONTENT) {
                    callback.onError(response);
                } else {
                    callback.onResponse(response);
                }
            }
        }.execute();
    }

    private static String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}
