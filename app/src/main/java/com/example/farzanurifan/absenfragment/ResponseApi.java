package com.example.farzanurifan.absenfragment;

import com.google.gson.annotations.SerializedName;

public class ResponseApi {
    @SerializedName("msg")
    String msg;

    public String getMessage() {
        return msg;
    }
}
