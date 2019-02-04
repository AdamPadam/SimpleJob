package com.codedevstudio.orders.models;

import com.codedevstudio.orders.Constants;

import java.io.Serializable;

/**
 * Created by fabius on 01/04/2018.
 */

public class Avatar implements Serializable {
    public String getThumb() {
        return Constants.SERVER_ADDRESS+thumb;
    }

    public void setThumb(String thumb) {
        this.thumb = thumb;
    }

    public String getMedium() {
        return Constants.SERVER_ADDRESS+medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    private String thumb;
    private String medium;
}
