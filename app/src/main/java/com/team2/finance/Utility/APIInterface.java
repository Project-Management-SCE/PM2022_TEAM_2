package com.team2.finance.Utility;

import com.team2.finance.Model.ResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIInterface {
    @GET("top-headlines")
    Call<ResponseModel> getLatestNews(@Query("q") String source, @Query("apiKey") String apiKey);
}