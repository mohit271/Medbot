package com.example.medbot;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface HealthAPI {

    @GET
    Call<MessageModal> getAnswer(@Body String ques);

    @POST
    Call<String> getAnswer2(@Body String ques);
}
