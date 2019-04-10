package com.example.test.http;

import com.example.test.json.Weather;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Url;

public interface HttpWeather {
    @GET
    Observable<Weather> getWeather(@Url String url);}

