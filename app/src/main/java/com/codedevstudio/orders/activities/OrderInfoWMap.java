package com.codedevstudio.orders.activities;

import com.codedevstudio.orders.R;
import com.codedevstudio.orders.models.Order;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static com.codedevstudio.orders.Constants.JOBS_PATH;
import static com.codedevstudio.orders.Constants.TAG;


public class OrderInfoWMap extends AppCompatActivity implements
        OnMapReadyCallback {

    private static final String TAG_ID = "com.codedevstudio.id";

    private Order mOrder;

    //UI элементы
    TextView mTitle, mSubtitle, mPhone, mAddress, mCost;
    Button mShowPersonals;
    FloatingActionButton fab;
    LinearLayout mPersonalLayout;

    MapView mMapView;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private boolean mPermissionDenied = false;

    public GoogleMap mMap;

    public static Intent newIntent(Context packageContext, Order order){
        Intent intent = new Intent(packageContext, OrderInfoActivity.class);
        intent.putExtra(TAG_ID, order);
        return intent;
    }


    @Override
    protected void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_info);

        mOrder = (Order) getIntent().getSerializableExtra(TAG_ID);

        // Инициализируем UI элементы
        mTitle = (TextView) findViewById(R.id.title_tv);
        mSubtitle = (TextView) findViewById(R.id.subtitle_tv);
        mPhone = (TextView) findViewById(R.id.phone_number_tv);
        mAddress = (TextView) findViewById(R.id.address_tv);
        mCost = (TextView) findViewById(R.id.cost_tv);
        mShowPersonals = (Button) findViewById(R.id.show_personal);
        //fab = (FloatingActionButton) findViewById(R.id.fab);
        mPersonalLayout = (LinearLayout) findViewById(R.id.personal_layout);

        fab.setVisibility(View.GONE);

        mTitle.setText(mOrder.getTitle());
        mSubtitle.setText(mOrder.getSubtitle());
        mPhone.setText(mOrder.getPhone());
        mAddress.setText(mOrder.getAddress());
        mCost.setText(String.valueOf(mOrder.getCost()));

        mShowPersonals.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mPersonalLayout.setVisibility(View.VISIBLE);
                mShowPersonals.setVisibility(View.GONE);
            }
        });

        // При нажатии на FloatingActionButton отправляем запрос на измение статуса заказа и id исполнителя
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final DatabaseReference jobsReference = database.getReference(JOBS_PATH + mOrder.getId());

                jobsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.w(TAG, dataSnapshot.getValue(Order.class).toString());
                        final Order order = dataSnapshot.getValue(Order.class);
                        if (order.getStatus() == Order.Status.OPEN) {
                            //noinspection ConstantConditions
                            jobsReference.child("executorId").setValue(FirebaseAuth.getInstance().getCurrentUser().getUid());
                            jobsReference.child("status").setValue(Order.Status.INDONE);
                            Toast.makeText(getApplicationContext(), "Вы теперь выполняете это задание", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Кто-то уже выполняет это задание", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "Failed to read value.", databaseError.toException());
                    }
                });
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;

        LatLng place = new LatLng(-33.852, 151.211);
        mMap.addMarker(new MarkerOptions().position(place)
                .title("Местонахождение")
                .icon(BitmapDescriptorFactory
                        .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(place));
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

}