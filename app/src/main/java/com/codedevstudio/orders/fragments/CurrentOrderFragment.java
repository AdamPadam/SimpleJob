package com.codedevstudio.orders.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codedevstudio.orders.R;
import com.codedevstudio.orders.activities.App;
import com.codedevstudio.orders.models.Order;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.codedevstudio.orders.Constants.*;

public class CurrentOrderFragment extends Fragment {
    private static final String ARG_ID = "order_info_id";

    private Order mOrder;

    TextView mTitle, mSubtitle, mPhone, mAddress, mCost;
    FloatingActionButton mFabDone, mFabDiscard;

    DatabaseReference mReference;
    FirebaseDatabase mDatabase;

    public static CurrentOrderFragment newInstance(Order order) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_ID, order);

        CurrentOrderFragment fragment = new CurrentOrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrder = (Order) getArguments().getSerializable(ARG_ID);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.current_order_fragment, container, false);


        mTitle = (TextView) v.findViewById(R.id.title_tv);
        mSubtitle = (TextView) v.findViewById(R.id.subtitle_tv);
        mPhone = (TextView) v.findViewById(R.id.phone_number_tv);
        mAddress = (TextView) v.findViewById(R.id.address_tv);
        mCost = (TextView) v.findViewById(R.id.cost_tv);
        mFabDone = (FloatingActionButton) v.findViewById(R.id.fab_done);
        mFabDiscard = (FloatingActionButton) v.findViewById(R.id.fab_remove);

        mFabDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<Void> call = App.getApi().finishOrder(mOrder.getId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.code() == 200) {
                            Toast.makeText(getActivity(), "Вы закончили выполнять задание, ожидаем ответа от заказчика", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Проблемы на стороне сервера", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Response code is " + response.code());
                        }
                        getActivity().onBackPressed();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Log.w(TAG, t.toString());
                    }
                });
            }
        });
        mFabDiscard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Call<Void> call = App.getApi().discardOrder(mOrder.getId());
                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                        if (response.code() == 200) {
                            Toast.makeText(getActivity(), "Вы больше не выполняете это задание", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Проблемы на стороне сервера", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Response code is " + response.code());
                        }
                        getActivity().onBackPressed();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Log.w(TAG, t.toString());
                    }
                });
            }
        });


        mTitle.setText(mOrder.getTitle());
        mSubtitle.setText(mOrder.getSubtitle());
        mPhone.setText(mOrder.getPhone());
        mAddress.setText(mOrder.getAddress());
        mCost.setText(String.valueOf(mOrder.getCost()));

        return v;
    }
}
