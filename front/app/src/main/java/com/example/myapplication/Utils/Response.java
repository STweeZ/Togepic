package com.example.myapplication.Utils;

public class Response {
    private int responseCode;
    private String responseString;

    public Response(int responseCode, String responseString) {
        this.responseCode = responseCode;
        this.responseString = responseString;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public String getResponseString() {
        return responseString;
    }

    public void setResponseString(String responseString) {
        this.responseString = responseString;
    }
}
