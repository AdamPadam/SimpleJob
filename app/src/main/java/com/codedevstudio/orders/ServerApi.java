package com.codedevstudio.orders;


import com.codedevstudio.orders.ServerDataWrappers.AuthenticationWrapper;
import com.codedevstudio.orders.ServerDataWrappers.OrderResponseWrapper;
import com.codedevstudio.orders.ServerDataWrappers.OrderWrapper;
import com.codedevstudio.orders.models.Order;
import com.codedevstudio.orders.models.OrderResponse;
import com.codedevstudio.orders.models.User;
import com.codedevstudio.orders.models.UserCredentials;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ServerApi {
    @POST("users.json")
    Call<User> createUser(@Body AuthenticationWrapper request);

    @GET("users.json")
    Call<List<User>> getUsers();

    @GET("/users/show/{id}.json")
    Call<User> getUserInfo (@Path("id") String id);

    @GET("/users/show/me.json")
    Call<User> getCurrentUserInfo ();

    @POST("/users/sign_in.json")
    Call<UserCredentials> Login (@Body AuthenticationWrapper request);

    @POST("orders.json")
    Call<Order> createOrder(@Body OrderWrapper orderWrapper);

    @GET("orders.json")
    Call<List<Order>> getOrders();

    @GET("/users/me/given_orders.json")
    Call<List<Order>> getGivenOrders();

    @GET("/users/me/current_orders.json")
    Call<List<Order>> getCurrentOrders();

    @POST("order_responses.json")
    Call<OrderResponse> takeOrder(@Body  OrderResponseWrapper orderResponseWrapper);

    @POST("/order_responses/{id}/accept.json")
    Call<Void> acceptOrderResponse(@Path("id") String id);

    @GET("/orders/{id}/responses.json")
    Call<List<OrderResponse>> getCurrentOrderResponses(@Path("id") String id);

    @POST("/orders/{id}/confirm.json")
    Call<Void> confirmOrder(@Path("id") String id);

    @POST("/orders/{id}/finish.json")
    Call<Void> finishOrder(@Path("id") String id);

    @POST("/orders/{id}/discard.json")
    Call<Void> discardOrder(@Path("id") String id);

    @PUT("/users/me.json")
    Call<User> editUser(@Body AuthenticationWrapper request);

    @POST("/orders/{id}/pay.json")
    Call<HashMap<String,String>> payOrder(@Path("id") String id);

    @POST("/users/me/attach_card.json")
    Call<HashMap<String,String>> attachCard();
}
