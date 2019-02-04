package com.codedevstudio.orders.activities;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.codedevstudio.orders.R;
import com.codedevstudio.orders.fragments.OrderInfoFragment;
import com.codedevstudio.orders.fragments.UserFragment;
import com.codedevstudio.orders.fragments.UserInfo;
import com.codedevstudio.orders.models.Order;
import com.codedevstudio.orders.models.User;

public class UserInfoActivity extends SingleFragmentActivity {

    private static final String TAG_ID = "user_info_activity";

    public static Intent newIntent(Context packageContext, User user){
        Intent intent = new Intent(packageContext, UserInfoActivity.class);
        intent.putExtra(TAG_ID, user);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        User user= (User) getIntent().getSerializableExtra(TAG_ID);
        return UserFragment.newInstance(user,false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        ActionBar ab = getSupportActionBar();

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);
    }
}
