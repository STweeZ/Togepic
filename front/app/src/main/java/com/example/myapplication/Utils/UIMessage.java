package com.example.myapplication.Utils;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

public class UIMessage {

    public static void showError(String err, Context context) {
        Toast errorToast = Toast.makeText(context, err, Toast.LENGTH_SHORT);
        errorToast.show();
    }

    public static void showText(String msg, Context context) {
        Toast errorToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        errorToast.show();
    }
}
