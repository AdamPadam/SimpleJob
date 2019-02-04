package com.codedevstudio.orders.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import com.codedevstudio.orders.R;
import com.codedevstudio.orders.fragments.CurrentOrderFragment;
import com.codedevstudio.orders.models.Order;

//Активность, которая является хостом для CurrentOrderFragment
public class CurrentOrderActivity extends SingleFragmentActivity {

    private static final String TAG_ORDER = "com.codedevstudio.id";

    // Метод, который принимает Order для передачи его фрагменту, и возвращающий Intent
    public static Intent newIntent(Context packageContext, Order order){
        Intent intent = new Intent(packageContext, CurrentOrderActivity.class);
        intent.putExtra(TAG_ORDER, order);
        return intent;
    }

    // Метод, который создает фрагмент, хостом которого выступает эта активность, и передает в него Order из интента
    @Override
    protected Fragment createFragment() {
        Order order = (Order) getIntent().getSerializableExtra(TAG_ORDER);
        return CurrentOrderFragment.newInstance(order);
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
