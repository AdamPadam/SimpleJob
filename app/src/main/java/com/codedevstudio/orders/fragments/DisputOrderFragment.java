package com.codedevstudio.orders.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.codedevstudio.orders.R;
import com.codedevstudio.orders.utils.CustomSwipeAdaptor;

/**
 * Created by FOX on 18.04.18.
 */

public class DisputOrderFragment extends Fragment {
    private Button BtnSend;
    private Button BtnAdd;
    private EditText Description;
    private EditText Tel;
    PagerAdapter pagerAdapter;
    CustomSwipeAdaptor customSwipeAdaptor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conflict, container, false);
        Description = (EditText)view.findViewById(R.id.aboutProblem);
        Tel = (EditText)view.findViewById(R.id.phone_number_dis);
        BtnSend = (Button)view.findViewById(R.id.send_btn);
        BtnAdd = (Button)view.findViewById(R.id.add_btn);

        customSwipeAdaptor = new CustomSwipeAdaptor(this);
//        pagerAdapter.setAdapter(customSwipeAdaptor);



        BtnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //прикрепление фотографий


            }
        });




        BtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //отправка (ид заказа и данных из полей на сервер + отправка фотографий)

            }
        });

        return view;
    }


}
