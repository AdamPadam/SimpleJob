package com.codedevstudio.orders.ServerDataWrappers;

import com.codedevstudio.orders.models.OrderResponse;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by fabius on 11/03/2018.
 */

public class OrderResponseWrapper{
        @SerializedName("order_response")
        @Expose
        private OrderResponse orderResponse;

        public OrderResponseWrapper(OrderResponse orderResponse) {
            this.orderResponse = orderResponse;
        }
}
