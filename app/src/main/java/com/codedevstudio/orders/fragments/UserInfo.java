package com.codedevstudio.orders.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codedevstudio.orders.R;
import com.codedevstudio.orders.models.User;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;

/**
* Экран, отображаюший информацию о пользователе
 */
public class UserInfo extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ArgumentsKey = "user_fragment_arguments";
    private CircleImageView userPhoto;
    private TextView userName;
    private TextView userBio;

    public UserInfo() {
        // Required empty public constructor
    }

    public void setUser(User user){
        userName.setText(user.getFullname());
        userBio.setText(user.getBio());
        if (user.getAvatar() != null){
            Picasso.get().load(user.getAvatar().getMedium()).into(userPhoto);
        }


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        userBio = (TextView) view.findViewById(R.id.aboutText);
        userPhoto = (CircleImageView) view.findViewById(R.id.userPhoto);
        userName = (TextView) view.findViewById(R.id.userName);
        return view;
    }





}
