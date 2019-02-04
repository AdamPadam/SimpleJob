package com.codedevstudio.orders.fragments;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.codedevstudio.orders.R;
import com.codedevstudio.orders.ServerDataWrappers.OrderResponseWrapper;
import com.codedevstudio.orders.activities.App;
import com.codedevstudio.orders.activities.CurrentOrderActivity;
import com.codedevstudio.orders.activities.NavDrawer;
import com.codedevstudio.orders.activities.OrderInfoActivity;
import com.codedevstudio.orders.models.OrderResponse;
import com.codedevstudio.orders.models.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * A placeholder fragment containing a simple view.
 */
public class OrderResponseFragment extends Fragment {
    private static final String ARG_ID = "order_response_id";
    private OrderResponse orderResponse;
    private UserInfo executorInfoFragment;
    public static OrderResponseFragment newInstance(OrderResponse orderResponse) {
        // Создаем фрагмент и кладем в его аргументы аргументы функции, и возвращаем этот фрагмент
        Bundle args = new Bundle();
        args.putSerializable(ARG_ID, orderResponse);
        OrderResponseFragment fragment = new OrderResponseFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        orderResponse = (OrderResponse) getArguments().getSerializable(ARG_ID);
    }

    private void acceptOrder(){

        App.getApi().acceptOrderResponse(orderResponse.getId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                Toast toast;

                if (response.code() == 201){
                    toast = Toast.makeText(getActivity(), "Заказ успешно принят", Toast.LENGTH_LONG);
                    Intent intent = new Intent(getActivity(), NavDrawer.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);                } else {
                    toast = Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_LONG);
                }
                toast.show();

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast toast = Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_LONG);
                toast.show();
            }
        });
    }

    public OrderResponseFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_response, container, false);
        executorInfoFragment = (UserInfo) getChildFragmentManager().findFragmentById(R.id.fragment_response_executor_info);
        User executor = orderResponse.getExecutor();
        executorInfoFragment.setUser(executor);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fabAcceptOrderResponse);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptOrder();
            }
        });

        TextView eMessage = (TextView) v.findViewById(R.id.messageText);
        eMessage.setText(orderResponse.getMessage());

        return v;
    }
}
