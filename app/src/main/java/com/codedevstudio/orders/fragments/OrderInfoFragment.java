package com.codedevstudio.orders.fragments;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codedevstudio.orders.ServerDataWrappers.OrderResponseWrapper;
import com.codedevstudio.orders.activities.App;
import com.codedevstudio.orders.activities.AttachCardActivity;
import com.codedevstudio.orders.activities.OrderResponsesActivity;
import com.codedevstudio.orders.adapters.CardsAdapter;
import com.codedevstudio.orders.models.Card;
import com.codedevstudio.orders.models.Order;
import com.codedevstudio.orders.R;
import com.codedevstudio.orders.models.OrderResponse;
import com.codedevstudio.orders.models.User;
import com.codedevstudio.orders.models.UserCredentials;
import com.codedevstudio.orders.utils.StatusDisplayName;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.codedevstudio.orders.Constants.*;


public class OrderInfoFragment extends Fragment implements OnMapReadyCallback {
    private static final String ARG_ID = "order_info_id";

    private Order mOrder;

    MapView m;

    //UI элементы
    TextView mTitle, mSubtitle, mPhone, mAddress, mCost, creatorName, status;
    Button mShowPersonals, showResponses, takeOrder;

    LinearLayout mPersonalLayout;

    public static OrderInfoFragment newInstance(Order order) {
        // Создаем фрагмент и кладем в его аргументы аргументы функции, и возвращаем этот фрагмент
        Bundle args = new Bundle();
        args.putSerializable(ARG_ID, order);
        OrderInfoFragment fragment = new OrderInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    // Забираем mOrder из аргументов фрагмента
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOrder = (Order) getArguments().getSerializable(ARG_ID);
    }


    private void getCards() {
        App.getApi().getCurrentUserInfo().enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                if (user != null) {
                    if (!user.getCards().isEmpty()) {
                        showPopup(getView(),user.getCards());
                    } else {
                        AlertDialog.Builder builder;
                        Context context = getContext();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(context);
                        }
                        builder.setTitle(R.string.card_required_title)
                                .setMessage(R.string.card_required_message)
                                .setPositiveButton(R.string.connect_card_accept, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getActivity(), AttachCardActivity.class);
                                        startActivity(intent);
                                    }
                                })
                                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    private void acceptOrder(final PopupWindow popup, String message, long cardId) {
        OrderResponse orderResponse = new OrderResponse(mOrder.getId(), message, cardId);
        App.getApi().takeOrder(new OrderResponseWrapper(orderResponse)).enqueue(new Callback<OrderResponse>() {
            @Override
            public void onResponse(Call<OrderResponse> call, Response<OrderResponse> response) {
                if (response.code() == 201) {
                    Toast.makeText(getActivity(), "Вы отправили запрос на выполнение задания", Toast.LENGTH_SHORT).show();
                } else if (response.code() == 422) {
                    Log.w(TAG, "Response is " + response.code());
                    try {
                        TreeMap<String, ArrayList<String>> errors = new Gson().fromJson(response.errorBody().string(), TreeMap.class);
                        String errorValues = TextUtils.join(",", errors.firstEntry().getValue());
                        Toast.makeText(getActivity(), errorValues, Toast.LENGTH_LONG).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.w(TAG, "Response is " + response.errorBody());
                    Toast.makeText(getActivity(), "Произошла ошибка", Toast.LENGTH_LONG).show();
                }
                popup.dismiss();
            }

            @Override
            public void onFailure(Call<OrderResponse> call, Throwable t) {
                Log.w(TAG, "Failed to read value.", t);
                popup.dismiss();
            }
        });
    }

    public void showPopup(View anchorView, List<Card> cards) {

        final View popupView = getLayoutInflater().inflate(R.layout.accept_order_popup_layout, null);
        popupView.setAlpha(0f);

        CardView card = (CardView) popupView.findViewById(R.id.Card);
        FrameLayout frame = (FrameLayout) popupView.findViewById(R.id.popup_background);
        final TextView messageText = (TextView) popupView.findViewById(R.id.order_accept_message);
        card.setTranslationY(500);
        final PopupWindow popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        Button cancelBtn = (Button) popupView.findViewById(R.id.accept_order_cancel);
        Button sendBtn = (Button) popupView.findViewById(R.id.accept_order_ok);
        final Spinner cardsSpinner = (Spinner) popupView.findViewById(R.id.cards_spinner);
        Card[] cardsArray = cards.toArray(new Card[cards.size()]);
        CardsAdapter adapter = new CardsAdapter(getContext(),R.layout.support_simple_spinner_dropdown_item,cardsArray);
        cardsSpinner.setAdapter(adapter);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                acceptOrder(popupWindow, messageText.getText().toString(),cardsSpinner.getSelectedItemId());
            }
        });

        View.OnClickListener hidePopup = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        };

        frame.setOnClickListener(hidePopup);
        cancelBtn.setOnClickListener(hidePopup);
        // Example: If you have a TextView inside `popup_layout.xml`


        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        // If you need the PopupWindow to dismiss when when touched outside
        popupWindow.setBackgroundDrawable(new ColorDrawable());
        int location[] = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        anchorView.getLocationOnScreen(location);

        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(anchorView, Gravity.CENTER,
                location[0], location[1] + anchorView.getHeight());

        int animLen = 250;
        popupView.animate()
                .alpha(1f)
                .setDuration(animLen)
                .setListener(null);
        card.animate().translationY(0)
                .setDuration(animLen)
                .setListener(null);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.activity_order_info, container, false);


        // Инициализируем UI элементы
        mTitle = (TextView) v.findViewById(R.id.title_tv);
        mSubtitle = (TextView) v.findViewById(R.id.subtitle_tv);
        mPhone = (TextView) v.findViewById(R.id.phone_number_tv);
        mAddress = (TextView) v.findViewById(R.id.address_tv);
        mCost = (TextView) v.findViewById(R.id.cost_tv);
        mShowPersonals = (Button) v.findViewById(R.id.show_personal);
        takeOrder = (Button) v.findViewById(R.id.takeOrder);
        showResponses = (Button) v.findViewById(R.id.showResponses);
        creatorName = (TextView) v.findViewById(R.id.creatorName);
        status = (TextView) v.findViewById(R.id.status);
        UserCredentials credentials = App.getCredentials();
        if (credentials.getId().equals(mOrder.getCreatorId())) {
            takeOrder.setVisibility(View.GONE);
            showResponses.setVisibility(View.VISIBLE);
        } else {
            takeOrder.setVisibility(View.VISIBLE);
            showResponses.setVisibility(View.GONE);
        }

        mPersonalLayout = (LinearLayout) v.findViewById(R.id.personal_layout);


        m = (MapView) v.findViewById(R.id.map);
        m.onCreate(savedInstanceState);
        m.getMapAsync(this);


        mTitle.setText(mOrder.getTitle());
        mSubtitle.setText(mOrder.getSubtitle());
        mPhone.setText(mOrder.getPhone());
        mAddress.setText(mOrder.getAddress());
        mCost.setText(String.valueOf(mOrder.getCost()));
        creatorName.setText(mOrder.getCreator().getFullname());
        getActivity().setTitle(String.format("%s %s", getString(R.string.order_number), mOrder.getId()));
        status.setText(StatusDisplayName.getStatusName(mOrder.getStatus()));
        mShowPersonals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mPersonalLayout.getVisibility() == View.GONE) {
                    mPersonalLayout.setVisibility(View.VISIBLE);
                    mShowPersonals.setText(R.string.hide_phone_number);

                } else {
                    mShowPersonals.setText(R.string.show_phone_number);
                    mPersonalLayout.setVisibility(View.GONE);
                }
            }
        });

        showResponses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = OrderResponsesActivity.newIntent(getActivity(), mOrder);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        // При нажатии на FloatingActionButton отправляем запрос на измение статуса заказа и id исполнителя
        takeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                showPopup(v);
                getCards();

            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        m.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        m.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        m.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        m.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng position = new LatLng(mOrder.getLat(), mOrder.getLng());
        googleMap.addMarker(new MarkerOptions().position(position)
                .title("Геопозиция"));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 10));
    }
}