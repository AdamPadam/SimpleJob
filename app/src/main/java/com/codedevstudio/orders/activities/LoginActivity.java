package com.codedevstudio.orders.activities;


import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;

import com.codedevstudio.orders.fragments.LoginFragment;

// Активность, которая является хостом для LoginFragment
public class LoginActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new LoginFragment();
    }

    public static Intent newIntent(Context packageContext){
        return new Intent(packageContext, LoginActivity.class);
    }
}
