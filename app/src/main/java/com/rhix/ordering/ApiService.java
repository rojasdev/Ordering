package com.rhix.ordering;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("listproducts.php")
    Call<ProductResponse> getProducts(@Field("api_key") String apiKey);
}
