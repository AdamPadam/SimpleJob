package com.codedevstudio.orders.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by fabius on 19/04/2018.
 */

public class Card implements Serializable {
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("card_binding")
    @Expose
    private String cardBinding;
    @SerializedName("card_holder")
    @Expose
    private String cardHolder;
    @SerializedName("card_number")
    @Expose
    private String cardNumber;
    @SerializedName("card_expiration_year")
    @Expose
    private Integer cardExpirationYear;
    @SerializedName("card_expiration_month")
    @Expose
    private Integer cardExpirationMonth;
    @SerializedName("user_id")
    @Expose
    private Integer userId;

    public long getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCardBinding() {
        return cardBinding;
    }

    public void setCardBinding(String cardBinding) {
        this.cardBinding = cardBinding;
    }

    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public Integer getCardExpirationYear() {
        return cardExpirationYear;
    }

    public void setCardExpirationYear(Integer cardExpirationYear) {
        this.cardExpirationYear = cardExpirationYear;
    }

    public Integer getCardExpirationMonth() {
        return cardExpirationMonth;
    }

    public void setCardExpirationMonth(Integer cardExpirationMonth) {
        this.cardExpirationMonth = cardExpirationMonth;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
