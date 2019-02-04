package com.codedevstudio.orders.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.codedevstudio.orders.activities.App;
import com.codedevstudio.orders.activities.CurrentOrderActivity;
import com.codedevstudio.orders.models.Order;
import com.codedevstudio.orders.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.codedevstudio.orders.Constants.*;

@SuppressWarnings("ConstantConditions")
public class CurrentOrdersFragment extends Fragment {

    private RecyclerView mOrderRecyclerView;
    private TextView mNoOrders;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        mNoOrders = (TextView)view.findViewById(R.id.no_orders);
        mOrderRecyclerView = (RecyclerView) view.findViewById(R.id.all_orders_recycler_view);
        mOrderRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        App.getApi().getCurrentOrders().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                List<Order> list = response.body();
                if (list == null || list.size() == 0) {
                    mNoOrders.setVisibility(View.VISIBLE);
                    mNoOrders.setText("На данный момент у вас нет заказов");
                    list = new ArrayList<>();
                }else {
                    mNoOrders.setVisibility(View.GONE);
                    Collections.sort(list);
                }
                updateUI(list);
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.d(TAG,"Cant get current orders",t);
            }
        });


        return view;
    }

    private void updateUI(List<Order> ordersUpdate) {
        OrderAdapter adapter = new OrderAdapter(ordersUpdate);
        mOrderRecyclerView.setAdapter(adapter);
    }




    private class OrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private Order mOrder;

        private TextView mTitle;
        private TextView mSubtitle;
        private TextView mCost;
        private TextView mTime;

        private View mView;

        OrderHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);

            mTitle = (TextView) itemView.findViewById(R.id.title);
            mSubtitle = (TextView) itemView.findViewById(R.id.subtitle);
            mCost = (TextView) itemView.findViewById(R.id.cost);
            mTime = (TextView)itemView.findViewById(R.id.time);
            mView = itemView;
        }

        @SuppressWarnings("deprecation")
        void bindOrder(Order order) {
            mOrder = order;

            mTitle.setText(order.getTitle());
            mSubtitle.setText(order.getSubtitle());
            mCost.setText(String.valueOf(order.getCost()));
            mTime.setText(android.text.format.DateFormat.format("HH:MM" ,new Date(order.getTime())));



            if (order.getStatus() == Order.Status.SUCCESFULLYDONE) mView.setBackgroundColor(getResources().getColor(R.color.gray));
            else if (order.getStatus() == Order.Status.WAITFORTHEACCEPT) mView.setBackgroundColor(getResources().getColor(R.color.yellow));
            else if (order.getStatus() == Order.Status.EXPIRED) mView.setBackgroundColor(getResources().getColor(R.color.soft_red));

        }

        @Override
        public void onClick(View view) {
            if (mOrder.getStatus() == Order.Status.INDONE) {
                Intent intent = CurrentOrderActivity.newIntent(getActivity(), mOrder);
                startActivity(intent);
            }
        }
    }

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
