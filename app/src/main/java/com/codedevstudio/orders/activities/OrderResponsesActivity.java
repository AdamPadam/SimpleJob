package com.codedevstudio.orders.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.codedevstudio.orders.R;
import com.codedevstudio.orders.fragments.OrderInfoFragment;
import com.codedevstudio.orders.fragments.OrderResponsesFragment;
import com.codedevstudio.orders.models.Order;

/**
 * Created by fabius on 11/03/2018.
 */

public class OrderResponsesActivity extends SingleFragmentActivity {
    private static final String TAG_ID = "com.codedevstudio.id";

    public static Intent newIntent(Context packageContext, Order order){
        Intent intent = new Intent(packageContext, OrderResponsesActivity.class);
        intent.putExtra(TAG_ID, order);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        Order order = (Order) getIntent().getSerializableExtra(TAG_ID);
        return OrderResponsesFragment.newInstance(order);
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
