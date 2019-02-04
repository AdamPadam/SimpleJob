package com.codedevstudio.orders.ServerDataWrappers;

import com.codedevstudio.orders.models.User;
import com.google.gson.annotations.SerializedName;

/**
 * Класс, хранящий пользователя для отправки на регистрацию и авторизацию
 */

public class AuthenticationWrapper {
    public AuthenticationWrapper(User user) {
        this.user = user;
    }
    private User user;
 }
