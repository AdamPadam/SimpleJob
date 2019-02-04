package com.codedevstudio.orders.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.codedevstudio.orders.R;
import com.codedevstudio.orders.activities.App;
import com.codedevstudio.orders.activities.OrderInfoActivity;
import com.codedevstudio.orders.activities.ResponseActivity;
import com.codedevstudio.orders.models.Order;
import com.codedevstudio.orders.models.OrderResponse;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.codedevstudio.orders.Constants.TAG;

public class OrderResponsesFragment extends Fragment {
    // Объявляем UI элементы
    private RecyclerView mOrderRecyclerView;
    private TextView mNoOrders;
    private Order mOrder;
    private static final String ARG_ID = "order_id";

    public static OrderResponsesFragment newInstance(Order order) {
        // Создаем фрагмент и кладем в его аргументы аргументы функции, и возвращаем этот фрагмент
        Bundle args = new Bundle();
        args.putSerializable(ARG_ID, order);
        OrderResponsesFragment fragment = new OrderResponsesFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        // Инициализируем UI элементы
        mOrderRecyclerView = (RecyclerView) view.findViewById(R.id.all_orders_recycler_view);
        mOrderRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNoOrders = (TextView) view.findViewById(R.id.no_orders);


        // Инициализируем ProgressBar и делаем его видимым до получения результатов
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);


        //Получаем с сервера список заказов


        Order order = (Order) getArguments().getSerializable(ARG_ID);
        Call<List<OrderResponse>> call = App.getApi().getCurrentOrderResponses(order.getId());
        call.enqueue(new Callback<List<OrderResponse>>() {
            @Override
            public void onResponse(Call<List<OrderResponse>> call, Response<List<OrderResponse>> response) {
                List<OrderResponse> values = response.body();
                if (values != null) {
                    mOrderRecyclerView.setVisibility(View.VISIBLE);
                    mNoOrders.setVisibility(View.GONE);
                    updateUI(values);

                } else {
                    mOrderRecyclerView.setVisibility(View.GONE);
                    mNoOrders.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<OrderResponse>> call, Throwable t) {
                Log.w(TAG, t.toString());
                progressBar.setVisibility(View.GONE);
            }

        });


        return view;
    }

    //  Обновляем adapter новыми результатами
    private void updateUI(List<OrderResponse> ordersUpdate) {
        OrderResponsesFragment.OrderResponseAdapter adapter = new OrderResponsesFragment.OrderResponseAdapter(ordersUpdate);
        mOrderRecyclerView.setAdapter(adapter);
    }

    // Реализация паттерна ViewHolder
    private class OrderResponseHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private OrderResponse mOrder;

        private TextView mTitle;
        private TextView mSubtitle;
        private TextView mCost;
        private TextView mTime;

        OrderResponseHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitle = (TextView) itemView.findViewById(R.id.title);
            mSubtitle = (TextView) itemView.findViewById(R.id.subtitle);
            mCost = (TextView) itemView.findViewById(R.id.cost);
            mTime = (TextView) itemView.findViewById(R.id.time);
        }

        void bindOrder(OrderResponse order) {
            mOrder = order;
            String title = mOrder.getExecutor().getFirstName();
//            if (title.length() > 27){
//                title = title.substring(0, 23) + "...";
//            }
            String subTitle = mOrder.getExecutor().getLastName();
//            if (subTitle.length() > 27){
//                subTitle = subTitle.substring(0, 23) + "...";
//            }

            mTitle.setText(title);
            mSubtitle.setText(subTitle);
//            mCost.setText(String.valueOf(order.getCost()));
//            mTime.setText(android.text.format.DateFormat.format("HH:mm", new Date(order.getTime())));
        }

        @Override
        public void onClick(View view) {
            Log.d(TAG,"Response clicked");
            Intent intent = ResponseActivity.newIntent(getActivity(), mOrder);
            startActivity(intent);
        }
    }

    // Реализация адаптера
    private class OrderResponseAdapter extends RecyclerView.Adapter<OrderResponseHolder> {

        private List<OrderResponse> mOrders;

        OrderResponseAdapter(List<OrderResponse> orders) {
            mOrders = orders;
        }

        @Override
        public OrderResponsesFragment.OrderResponseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.card, parent, false);
            return new OrderResponsesFragment.OrderResponseHolder(view);
        }

        @Override
        public void onBindViewHolder(OrderResponsesFragment.OrderResponseHolder holder, int position) {
            OrderResponse orderResponse = mOrders.get(position);
            holder.bindOrder(orderResponse);
        }

        @Override
        public int getItemCount() {
            return mOrders.size();
        }
    }
}
