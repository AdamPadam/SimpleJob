package com.codedevstudio.orders.fragments;


import android.content.Intent;
import android.graphics.Color;
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

import com.codedevstudio.orders.activities.App;
import com.codedevstudio.orders.models.Order;
import com.codedevstudio.orders.R;
import com.codedevstudio.orders.activities.OrderInfoActivity;
import com.codedevstudio.orders.utils.SeparatorDecoration;
import com.codedevstudio.orders.utils.StrippedRecyclerView;

import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.codedevstudio.orders.Constants.*;

public class AllOrderListFragment extends Fragment {

    // Объявляем UI элементы
    private StrippedRecyclerView mOrderRecyclerView;
    private TextView mNoOrders;

    private List<Order> ordersList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        // Инициализируем UI элементы
        mOrderRecyclerView = (StrippedRecyclerView) view.findViewById(R.id.all_orders_recycler_view);
        mOrderRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNoOrders = (TextView) view.findViewById(R.id.no_orders);



        // Инициализируем ProgressBar и делаем его видимым до получения результатов
        final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);


        //Получаем с сервера список заказов
        Call<List<Order>> call = App.getApi().getOrders();
        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                List<Order> values = response.body();
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
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.w(TAG, t.toString());
                progressBar.setVisibility(View.GONE);
            }

        });


        return view;
    }

    //  Обновляем adapter новыми результатами
    private void updateUI(List<Order> ordersUpdate) {
        if (ordersList != null){
            ordersList.clear();
        }

        ordersList = ordersUpdate;

        OrderAdapter adapter;
        if (mOrderRecyclerView.getAdapter() != null){
            adapter = (OrderAdapter) mOrderRecyclerView.getAdapter();
            adapter.notifyDataSetChanged();

        } else {
            adapter = new OrderAdapter(ordersList);
        }

        mOrderRecyclerView.setAdapter(adapter);
    }

    // Реализация паттерна ViewHolder
    private class OrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Order mOrder;

        private TextView mTitle;
        private TextView mSubtitle;
        private TextView mCost;
        private TextView mTime;

        OrderHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitle = (TextView) itemView.findViewById(R.id.title);
            mSubtitle = (TextView) itemView.findViewById(R.id.subtitle);
            mCost = (TextView) itemView.findViewById(R.id.cost);
            mTime = (TextView) itemView.findViewById(R.id.time);
        }

        void bindOrder(Order order) {
            mOrder = order;
            String title = mOrder.getTitle();
//            if (title.length() > 27){
//                title = title.substring(0, 23) + "...";
//            }
            String subTitle = mOrder.getSubtitle();
//            if (subTitle.length() > 27){
//                subTitle = subTitle.substring(0, 23) + "...";
//            }

            mTitle.setText(title);
            mSubtitle.setText(subTitle);
            mCost.setText(String.valueOf(order.getCost()));
            mTime.setText(android.text.format.DateFormat.format("HH:mm", new Date(order.getTime())));
        }

        @Override
        public void onClick(View view) {
            Intent intent = OrderInfoActivity.newIntent(getActivity(), mOrder);
            startActivity(intent);
        }
    }

    // Реализация адаптера
    private class OrderAdapter extends RecyclerView.Adapter<OrderHolder> {

        private List<Order> mOrders;

        OrderAdapter(List<Order> orders) {
            mOrders = orders;
        }

        @Override
        public OrderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View view = layoutInflater.inflate(R.layout.card, parent, false);
            return new OrderHolder(view);
        }

        @Override
        public void onBindViewHolder(OrderHolder holder, int position) {
            Order order = mOrders.get(position);
            holder.bindOrder(order);
        }

        @Override
        public int getItemCount() {
            return mOrders.size();
        }
    }


}
