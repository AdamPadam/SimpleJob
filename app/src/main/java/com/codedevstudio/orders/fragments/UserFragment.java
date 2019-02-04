package com.codedevstudio.orders.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.codedevstudio.orders.R;
import com.codedevstudio.orders.activities.App;
import com.codedevstudio.orders.activities.SignupActivity;
import com.codedevstudio.orders.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.codedevstudio.orders.Constants.TAG;
import static com.codedevstudio.orders.Constants.USERS_PATH;

// Отображаем основную информацию о пользователе
public class UserFragment extends Fragment {
    private static final String ARG_ID = "user_fragment_id";
    private static final String USER_ARG_ID = ARG_ID+"_user";
    private static final String BOOL_ARG_ID = ARG_ID+"_bool";
    private Button editProfile;
    private User user;
    boolean isCurrentUser;

    public static UserFragment newInstance(User user, Boolean isCurrentUser) {
        Bundle args = new Bundle();
        args.putSerializable(USER_ARG_ID, user);
        args.putBoolean(BOOL_ARG_ID, isCurrentUser);

        UserFragment fragment = new UserFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void updateCurrentUser(User user){
        UserInfo userInfoFragment = (UserInfo) getChildFragmentManager().findFragmentById(R.id.user_page_user_info);
        userInfoFragment.setUser(user);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = (User) getArguments().getSerializable(USER_ARG_ID);
        isCurrentUser = getArguments().getBoolean(BOOL_ARG_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.user_page, container, false);
        UserInfo userInfoFragment = (UserInfo) getChildFragmentManager().findFragmentById(R.id.user_page_user_info);
        userInfoFragment.setUser(user);
        editProfile = (Button) view.findViewById(R.id.edit_profile);

        if (isCurrentUser){
            editProfile.setVisibility(View.VISIBLE);
            editProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = SignupActivity.newIntent(getActivity(), user);
                    startActivity(intent);
                }
            });
        } else {
            editProfile.setVisibility(View.GONE);
        }
        return view;
    }
}
