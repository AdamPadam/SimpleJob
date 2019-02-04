package com.codedevstudio.orders.activities;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.codedevstudio.orders.Constants;
import com.codedevstudio.orders.R;
import com.codedevstudio.orders.fragments.SignupFragment;
import com.codedevstudio.orders.models.User;
import com.codedevstudio.orders.models.UserCredentials;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

//Активность, которая запускается при входе в приложение и является хостом для SignupFragment
public class SignupActivity extends SingleFragmentActivity {
    static String TAG = Constants.TAG + "SignupActivity";
    User user;
    @Override
    protected Fragment createFragment() {
        user= (User) getIntent().getSerializableExtra(TAG);
        return SignupFragment.newInstance(user);
    }

    public static Intent newIntent(Context packageContext){
        return new Intent(packageContext, SignupActivity.class);
    }
    public static Intent newIntent(Context packageContext, User user){
        Intent intent = new Intent(packageContext, SignupActivity.class);
        intent.putExtra(TAG, user);
        return intent;
    }
    @Override
    protected void onStart() {
        Log.w(TAG, "onStart");
        super.onStart();
        // Проверяем залогинен ли пользователь, и если да, то отправляем его в NavDrawer

        UserCredentials credentials = App.getCredentials();

        if (credentials != null && user == null){
            Intent intent = NavDrawer.newIntent(this);
            startActivity(intent);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                finishAfterTransition();
            }
        }
    }

    // Создаем интерфейс для отслеживания нажатия на кнопку "Назад" в фрагменте через колбэки
    protected OnBackPressedListener onBackPressedListener;

    public interface OnBackPressedListener {
        void doBack();
    }

    public void setOnBackPressedListener(OnBackPressedListener onBackPressedListener) {
        this.onBackPressedListener = onBackPressedListener;
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null)
            onBackPressedListener.doBack();
        else
            super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        onBackPressedListener = null;
        Log.w(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        Log.w(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onStop() {
        Log.w(TAG, "onStop");
        super.onStop();
    }

    @Override
    protected void onRestart() {
        Log.w(TAG, "onRestart");
        super.onRestart();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (user != null){
                    finish();
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }
}
