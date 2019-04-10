package com.example.test.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface HttpLocality {
    @GET
    Call<ResponseBody> getLocality(@Url String url);
}
