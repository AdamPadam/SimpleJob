package com.codedevstudio.orders.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fabius on 11/03/2018.
 */

public class OrderResponse implements Serializable {

    public String getId() {
        return id;
    }

    private String id;

    @SerializedName("order_id")
    @Expose
    private String orderId;

    @Expose
    private User executor;

    @SerializedName("card_id")
    private long cardId;

    public User getExecutor() {
        return executor;
    }

    public OrderResponse(String orderId, String message, long cardId) {
        this.orderId = orderId;
        this.message = message;
        this.cardId = cardId;
    }

    @SerializedName("message")
    @Expose
    private String message;

    public String getOrderId() {
        return orderId;
    }

    public String getMessage() {
        return message;
    }


    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }
}
