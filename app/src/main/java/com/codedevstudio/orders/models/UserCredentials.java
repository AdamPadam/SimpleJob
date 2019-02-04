package com.codedevstudio.orders.models;
import com.google.firebase.database.IgnoreExtraProperties;
import com.google.gson.annotations.SerializedName;

/**
 * Данные для авторизации пользователя
 */

@IgnoreExtraProperties
public class UserCredentials {
    private String email;

    @SerializedName("auth_token")
    private String token;

    private  String id;

    public  String getId(){ return  id;}

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public UserCredentials(String id, String email, String token) {
        this.id = id;
        this.email = email;
        this.token = token;
    }
}
