package com.rhix.ordering;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiOrder {
    @POST("order.php")
    Call<ApiResponse> checkout(@Body String orderDetails);

    @FormUrlEncoded
    @POST("listorder.php") // New endpoint for listing orders
    Call<OrderListResponse> listOrders(@Field("api_key") String apiKey);
}
