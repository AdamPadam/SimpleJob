package com.codedevstudio.orders.fragments;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import com.codedevstudio.orders.activities.PayActivity;
import com.codedevstudio.orders.models.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.codedevstudio.orders.Constants.JOBS_PATH;
import static com.codedevstudio.orders.Constants.TAG;
import static com.codedevstudio.orders.activities.App.getApi;

@SuppressWarnings("ConstantConditions")
public class GivenOrdersFragment extends Fragment {
    private RecyclerView mOrderRecyclerView;
    private TextView mNoOrders;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        mOrderRecyclerView = (RecyclerView) view.findViewById(R.id.all_orders_recycler_view);
        mOrderRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mNoOrders = (TextView)view.findViewById(R.id.no_orders);


        final ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        getApi().getGivenOrders().enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                List<Order> values = response.body();
                if (values != null) {
                    Collections.sort(values);
                    mOrderRecyclerView.setVisibility(View.VISIBLE);
                    mNoOrders.setVisibility(View.GONE);
                    updateUI(values);
                }else {
                    mOrderRecyclerView.setVisibility(View.GONE);
                    mNoOrders.setVisibility(View.VISIBLE);
                }
                Log.d(TAG, "Value is: " + values);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.w(TAG, "Failed to read value.", t);

            }
        });

        return view;
    }

    private void updateUI(List<Order> ordersUpdate) {
        OrderAdapter adapter = new OrderAdapter(ordersUpdate);
        mOrderRecyclerView.setAdapter(adapter);
    }

    @SuppressWarnings("ConstantConditions")
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

            if (order.getStatus() == Order.Status.SUCCESFULLYDONE) mView.setBackgroundColor(getResources().getColor(R.color.gray));
            else if (order.getStatus() == Order.Status.WAITFORTHEACCEPT) mView.setBackgroundColor(getResources().getColor(R.color.yellow));
            else if (order.getStatus() == Order.Status.EXPIRED) mView.setBackgroundColor(getResources().getColor(R.color.soft_red));


            mTitle.setText(order.getTitle());
            mSubtitle.setText(order.getSubtitle());
            mCost.setText(String.valueOf(order.getCost()));
            mTime.setText(android.text.format.DateFormat.format("HH:MM" ,new Date(order.getTime())));
        }

        @Override
        public void onClick(View view) {
            if (mOrder.getStatus() == Order.Status.WAITFORTHEACCEPT) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Вы подтверждаете выполнение заказа?")
                        .setCancelable(true)
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Call<Void> call = getApi().confirmOrder(mOrder.getId());
                                call.enqueue(new Callback<Void>() {
                                    @Override
                                    public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                                        if (response.code() == 200){
                                            Toast.makeText(getActivity(), "Все прошло успешно", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getActivity(), "Проблемы на стороне сервера", Toast.LENGTH_SHORT).show();
                                            Log.w(TAG, "Response code is " + response.code());
                                        }
                                    }

                                    @Override
                                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                                        Log.w(TAG, t.toString());
                                    }
                                });
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else if (mOrder.getStatus() == Order.Status.PENDING){
                Call<HashMap<String,String>> call = App.getApi().payOrder(mOrder.getId());
                call.enqueue(new Callback<HashMap<String, String>>() {
                    @Override
                    public void onResponse(Call<HashMap<String, String>> call, Response<HashMap<String, String>> response) {
                        if (response.code() == 200){
                            String url = response.body().get("redirect_url");
                            Intent intent = PayActivity.newIntent(getActivity(),url);
                            startActivity(intent);
                        } else {
                            Toast.makeText(getActivity(), "Проблемы на стороне сервера", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Response code is " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<HashMap<String, String>> call, Throwable t) {
                        Log.w(TAG, t.toString());
                    }
                });
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

