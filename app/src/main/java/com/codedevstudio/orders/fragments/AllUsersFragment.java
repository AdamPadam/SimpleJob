package com.codedevstudio.orders.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.codedevstudio.orders.R;
import com.codedevstudio.orders.activities.App;
import com.codedevstudio.orders.activities.NavDrawer;
import com.codedevstudio.orders.activities.UserInfoActivity;
import com.codedevstudio.orders.models.Order;
import com.codedevstudio.orders.models.User;
import com.codedevstudio.orders.utils.StrippedRecyclerView;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.codedevstudio.orders.Constants.TAG;


public class AllUsersFragment extends Fragment {

    private StrippedRecyclerView usersRecyclerView;
    private TextView noUsers;
    private ProgressBar progressBar;
    public AllUsersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_all_users, container, false);
        usersRecyclerView = (StrippedRecyclerView) view.findViewById(R.id.all_users_recycler_view);
        usersRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        noUsers = (TextView) view.findViewById(R.id.no_users);
         progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);


        Call<List<User>> call = App.getApi().getUsers();
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                List<User> values = response.body();
                if (values != null) {
                    usersRecyclerView.setVisibility(View.VISIBLE);
                    noUsers.setVisibility(View.GONE);
                    updateUI(values);

                } else {
                    usersRecyclerView.setVisibility(View.GONE);
                    noUsers.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Log.w(TAG, t.toString());
                progressBar.setVisibility(View.GONE);
            }

        });

        return  view;
    }
    private void updateUI(List<User> users) {
        AllUsersFragment.UserAdapter adapter = new AllUsersFragment.UserAdapter(users);
        usersRecyclerView.setAdapter(adapter);
    }
    private class UserHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private User user;

        //private TextView mSubtitle;
        private TextView uStatusFree;
        private TextView uStatusBusy;
        private TextView uName;
        private CircleImageView uPhoto;
        UserHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            uStatusFree = (TextView) itemView.findViewById(R.id.userStatusFree);
            uStatusBusy= (TextView) itemView.findViewById(R.id.userStatusBusy);
            uName= (TextView) itemView.findViewById(R.id.userName);
            uPhoto = (CircleImageView) itemView.findViewById(R.id.userPhoto);
        }

        void bindUser(User bUser) {
            user = bUser;
            if (user.isBusy()){
                uStatusBusy.setVisibility(View.VISIBLE);
                uStatusFree.setVisibility(View.GONE);
            } else {
                uStatusBusy.setVisibility(View.GONE);
                uStatusFree.setVisibility(View.VISIBLE);
            }
            uName.setText(user.getFullname());
            Picasso.get().load(user.getAvatar().getThumb()).into(uPhoto);
        }

        @Override
        public void onClick(View view) {
            progressBar.setVisibility(View.VISIBLE);

            App.getApi().getUserInfo(user.getId()).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.code() == 200){
                        Intent intent = UserInfoActivity.newIntent(getActivity(), response.body());
                        startActivity(intent);
                    } else {
                        Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_LONG).show();
                    }
                    progressBar.setVisibility(View.GONE);
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast toast = Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_LONG);
                    toast.show();
                }
            });



        }
    }
    private class UserAdapter extends RecyclerView.Adapter<AllUsersFragment.UserHolder> {

        private List<User> users;

        UserAdapter(List<User> _users) {
            users = _users;
        }

        @Override
        public AllUsersFragment.UserHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.user_card, parent, false);
            return new AllUsersFragment.UserHolder(view);
        }

        @Override
        public void onBindViewHolder(final AllUsersFragment.UserHolder holder, int position) {
            User user = users.get(position);
            holder.bindUser(user);




        }

        @Override
        public int getItemCount() {
            return users.size();
        }
    }



}
