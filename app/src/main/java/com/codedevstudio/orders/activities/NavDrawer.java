package com.codedevstudio.orders.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.codedevstudio.orders.Constants;
import com.codedevstudio.orders.R;
import com.codedevstudio.orders.fragments.AllUsersFragment;
import com.codedevstudio.orders.models.User;
import com.codedevstudio.orders.fragments.AddOrderFragment;
import com.codedevstudio.orders.fragments.AllOrderListFragment;
import com.codedevstudio.orders.fragments.CurrentOrdersFragment;
import com.codedevstudio.orders.fragments.GivenOrdersFragment;
import com.codedevstudio.orders.fragments.InfoFragment;
import com.codedevstudio.orders.fragments.UserFragment;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Главная активность, которая отображает основные фрагменты и реализует шаблон NavigationDrawer
@SuppressWarnings("deprecation")
public class NavDrawer extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    String TAG = Constants.TAG + "navDrawer";

    // Объявляем:
    // Фрагменты, которые будут отбражаться в этой активности
    AddOrderFragment mAddOrderFragment;
    AllOrderListFragment mAllOrderListFragment;
    CurrentOrdersFragment mCurrentOrdersFragment;
    GivenOrdersFragment mGivenOrdersFragment;
    UserFragment mUserFragment;
    AllUsersFragment mAllUsersFragment;
    InfoFragment mInfoFragment;
    Toolbar mToolbar;

    // Боковое меню
    NavigationView mNavigationView;

    // Базу данных Firebase
    FirebaseDatabase mFirebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {




        Log.w(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        String url = "http://polar-caverns-65452.herokuapp.com/safecrow_payments/payment_result?orderId=7804_90&status=success&type=pay";

        setContentView(R.layout.nav_bar);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        // Инициализируем основной layout
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Инициализируем боковое меню
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);


        // Получаем Имя, Фамилию и количество денег пользователя и отображаем их в "шапке" бокового меню
        updateUser();

//        mFirebaseDatabase.getReference(USERS_PATH).child(user.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                com.codedevstudio.orders.models.User userModel = dataSnapshot.getValue(com.codedevstudio.orders.models.User.class);
//                View hView = mNavigationView.getHeaderView(0);
//                ((TextView)hView.findViewById(R.id.name)).setText(userModel.getFirstName() + " " + userModel.getLastName());
//                ((TextView)hView.findViewById(R.id.textView)).setText(String.format("%s монет", String.valueOf(userModel.getMoney())));
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w(TAG, databaseError.getMessage());
//            }
//        });

        // Инициализируем основные фрагменты
        mAddOrderFragment = new AddOrderFragment();
        mAllOrderListFragment = new AllOrderListFragment();
        mAllUsersFragment = new AllUsersFragment();
        mCurrentOrdersFragment = new CurrentOrdersFragment();
        mGivenOrdersFragment = new GivenOrdersFragment();
        mInfoFragment = new InfoFragment();

        // Отображаем главный фрагмент AllOrderListFragment
        getSupportFragmentManager().beginTransaction().add(R.id.container, mAllOrderListFragment).commit();
        mToolbar.setTitle(R.string.all_orders);

        App.setNavDrawer(this);
    }

    public void updateUser() {
        Call<User> call = App.getApi().getCurrentUserInfo();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                // Если все успещно, то отправляем пользователя в главную активность
                if (response.code() == 200) {
                    User userModel = response.body();
                                    View hView = mNavigationView.getHeaderView(0);
                    ((TextView)hView.findViewById(R.id.name)).setText(userModel.getFirstName() + " " + userModel.getLastName());
                    ((TextView)hView.findViewById(R.id.textView)).setText(String.format("%s монет", String.valueOf(userModel.getMoney())));
                    if (mUserFragment == null){
                        mUserFragment = UserFragment.newInstance(userModel,true);
                    } else {
                        mUserFragment.updateCurrentUser(userModel);
                    }

                } else {
                    Log.w(TAG, response.errorBody().toString());
                }
            }
            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                Log.w(TAG, t.toString());
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        // Если меню открыто, то закрываем его, иначе выходим из приложения
        //DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
       // if (drawer.isDrawerOpen(GravityCompat.START)) {
        //    drawer.closeDrawer(GravityCompat.START);
        //} else {
            finishAndRemoveTask();
        //}
    }

    @Override
    protected void onResume() {
        Log.w(TAG, "onResume");
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.w(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        Log.w(TAG, "onStart");
        super.onStart();
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

    // Отображаем фрагмент, который выбрал пользователь в боковом меню
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        FragmentTransaction ftrans = getSupportFragmentManager().beginTransaction();

        if (id == R.id.all_orders) {
            ftrans.replace(R.id.container, mAllOrderListFragment);
            mToolbar.setTitle(R.string.all_orders);
        } else if (id == R.id.all_users) {
            ftrans.replace(R.id.container, mAllUsersFragment);
            mToolbar.setTitle(R.string.all_users);
        } else if (id == R.id.given) {
            mToolbar.setTitle(R.string.given);
            ftrans.replace(R.id.container, mGivenOrdersFragment);
        } else if (id == R.id.in_progress) {
            ftrans.replace(R.id.container, mCurrentOrdersFragment);
            mToolbar.setTitle(R.string.in_progress);
        } else if (id == R.id.user){
            ftrans.replace(R.id.container, mUserFragment);
            mToolbar.setTitle(R.string.user);
        } else if (id == R.id.add_order){
            ftrans.replace(R.id.container, mAddOrderFragment);
            mToolbar.setTitle(R.string.add_order);
        } else if (id == R.id.help) {
            ftrans.replace(R.id.container, mInfoFragment);
            mToolbar.setTitle(R.string.help);
        }else if(id == R.id.sign_out){
            //Выходим из аккаунта и пробрасываем в активити регистрации
            App.Logout();
            Intent intent = SignupActivity.newIntent(this);
            startActivity(intent);
        }
        ftrans.commit();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static Intent newIntent(Context packageContext){
        return new Intent(packageContext, NavDrawer.class);
    }
}
