package com.codedevstudio.orders.ServerDataWrappers;

import com.codedevstudio.orders.models.Order;

/**
 * Класс, хранящий заказ для отправки
 */

public class OrderWrapper
{
    private Order order;

    public OrderWrapper(Order order) {
        this.order = order;
    }
}
