package com.codedevstudio.orders.fragments;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.codedevstudio.orders.ServerDataWrappers.OrderWrapper;
import com.codedevstudio.orders.activities.App;
import com.codedevstudio.orders.R;
import com.codedevstudio.orders.models.Order;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.codedevstudio.orders.activities.App;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.List;

import static com.codedevstudio.orders.Constants.*;


@SuppressWarnings("ConstantConditions")
public class AddOrderFragment extends Fragment {

    private static final String DIALOG_DATE = "DialogDate";
    //UI элементы
    EditText mTitleEditText, mSubtitleEditText, mPhoneNumberEditText, mAddressEditText, mCostEditText, mTimeEditText;
    FloatingActionButton mAddButton;

    private FirebaseUser mUser;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_order, container, false);

        mUser = FirebaseAuth.getInstance().getCurrentUser();

        //Инициализация UI элементов
        mTitleEditText = (EditText) view.findViewById(R.id.title_et);
        mSubtitleEditText = (EditText) view.findViewById(R.id.subtitle_et);
        mPhoneNumberEditText = (EditText) view.findViewById(R.id.phone_number_et);
        mAddressEditText = (EditText) view.findViewById(R.id.address_et);
        mCostEditText = (EditText) view.findViewById(R.id.cost_et);
        mTimeEditText = (EditText) view.findViewById(R.id.time_et);

        mAddButton = (FloatingActionButton) view.findViewById(R.id.add_button);

        // Назначаем слушателя для обработки нажатий на кнопку
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Проверяем, пусты ли поля
                if (isFieldsEmpty())
                    Toast.makeText(getActivity(), getText(R.string.warning_fields_empty), Toast.LENGTH_SHORT).show();
                else {


                    try {
                        LatLng latLng = getLocationFromAddress(mAddressEditText.getText().toString());
                        final double lat = latLng.latitude;
                        final double lng = latLng.longitude;


                        Order order = new Order(
                                mTitleEditText.getText().toString(),
                                mSubtitleEditText.getText().toString(),
                                mAddressEditText.getText().toString(),
                                mPhoneNumberEditText.getText().toString(),
                                Integer.parseInt(mCostEditText.getText().toString()),
                                36 * 100000 * Long.parseLong(mTimeEditText.getText().toString()),
                                lat, lng);
                        App.getApi().createOrder(new OrderWrapper(order)).enqueue(new Callback<Order>() {
                            //                                    App.getApi().createOrder(task.getResult().getToken(),
//                                            mTitleEditText.getText().toString(), mSubtitleEditText.getText().toString(),
//                                            mAddressEditText.getText().toString(), mPhoneNumberEditText.getText().toString(),
//                                            Integer.parseInt(mCostEditText.getText().toString()),
//                                            36 * 100000 * Long.parseLong(mTimeEditText.getText().toString()), lat, lng).enqueue(new Callback<String>() {
                            @Override
                            public void onResponse(@NonNull Call<Order> call, @NonNull Response<Order> response) {
                                // Проверяем код, возвращаемый сервером
                                if (response.code() == 201) {
                                    // Если заказ создан успешно, то обнуляем поля и отображаем тост, о успешном создании
                                    Toast.makeText(getActivity(), "Заказ создан", Toast.LENGTH_SHORT).show();
                                    mTitleEditText.setText("");
                                    mSubtitleEditText.setText("");
                                    mPhoneNumberEditText.setText("");
                                    mAddressEditText.setText("");
                                    mCostEditText.setText("");
                                    mTimeEditText.setText("");

                                    // Закрываем клавиатуру
                                    View view = getActivity().getCurrentFocus();
                                    if (view != null) {
                                        InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                                        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    }
                                } else if (response.code() == 406)
                                    Toast.makeText(getActivity(), "Недостаточно денег", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(@NonNull Call<Order> call, @NonNull Throwable t) {
                                Log.w(TAG, t.toString());
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "Не удается распознать адрес", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }

    // Проверяем, пусты ли поля
    private boolean isFieldsEmpty() {
        return isEmpty(mTitleEditText) || isEmpty(mSubtitleEditText) || isEmpty(mPhoneNumberEditText)
                || isEmpty(mAddressEditText) || isEmpty(mCostEditText) || isEmpty(mTimeEditText);
    }

    // Проверяем, пусто ли поле
    private boolean isEmpty(EditText editText) {
        Editable text = editText.getText();
        return text == null || text.toString().isEmpty() || text.length() == 0;
    }

    public LatLng getLocationFromAddress(String strAddress) throws IOException {

        Geocoder coder = new Geocoder(getActivity());
        List<Address> address;
        LatLng p1 = null;

        address = coder.getFromLocationName(strAddress, 5);
        if (address == null) {
            return null;
        }
        Address location = address.get(0);

        p1 = new LatLng(location.getLatitude(),
                location.getLongitude());

        return p1;
    }
}
