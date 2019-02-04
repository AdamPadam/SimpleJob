package com.codedevstudio.orders.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.codedevstudio.orders.R;
import com.codedevstudio.orders.fragments.OrderResponseFragment;
import com.codedevstudio.orders.models.OrderResponse;

public class ResponseActivity extends SingleFragmentActivity {
    private static final String TAG_ID = "order_response_id";

    public static Intent newIntent(Context packageContext, OrderResponse orderResponse){
        Intent intent = new Intent(packageContext, ResponseActivity.class);
        intent.putExtra(TAG_ID, orderResponse);
        return intent;
    }

    @Override
    protected Fragment createFragment() {
        OrderResponse orderResponse = (OrderResponse) getIntent().getSerializableExtra(TAG_ID);
        return OrderResponseFragment.newInstance(orderResponse);
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
