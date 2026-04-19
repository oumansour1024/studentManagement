package com.example.studentmanagement.data.remote;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    @FormUrlEncoded
    @POST("rest/login.php")
    Call<AuthResponse> login(
            @Field("login") String login,
            @Field("passwd") String passwd
    );
}