package com.example.myapplication.Utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public abstract class TextValidator implements TextWatcher {
    private final TextView textView;
    private final String error_msg;

    public TextValidator(TextView textView) {
        this.textView = textView;
        this.error_msg = "Invalid entry";
    }

    public TextValidator(TextView textView, String error_msg) {
        this.textView = textView;
        this.error_msg = error_msg;
    }

    public abstract boolean validate(TextView textView, String text);

    @Override
    final public void afterTextChanged(Editable s) {
        String text = textView.getText().toString();
        boolean isCorrect = validate(textView, text);
        if (!isCorrect) {
            textView.setError(this.error_msg);
        }

    }

    @Override
    final public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    final public void onTextChanged(CharSequence s, int start, int before, int count) {
    }
}
