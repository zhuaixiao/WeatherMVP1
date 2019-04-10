package com.example.test.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.test.MyApplication;
import com.example.test.contract.MainContract;
import com.example.test.db.City;
import com.example.test.db.County;
import com.example.test.db.Province;
import com.example.test.http.HttpLocality;
import com.example.test.http.HttpWeather;
import com.example.test.inject.DaggerMainModelImplComponent;
import com.example.test.json.Weather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainModelImpl implements MainContract.Model {
    @Inject
    Retrofit retrofit;
    static final int PROVINCELEVEL = 0;
    static final int COUNTYLEVEL = 1;
    static final int CITYLEVEL = 2;
    int currentLevel;
    List<Province> provinces;
    List<County> counties;
    List<City> cities;
    Province selectedProvince;
    City selectedCity;
    String weatherId;
    List<String> list = new ArrayList<>();
    MainContract.Model.Listener listener;
    SharedPreferences pre;
    SharedPreferences.Editor edi;
    private static final String TAG = "123";

    @Override
    public void questLocality(int position, int flag) {
        if (flag == 0) {
            if (currentLevel == PROVINCELEVEL) {
                selectedProvince = provinces.get(position);
                questCity();
            } else if (currentLevel == CITYLEVEL) {
                selectedCity = cities.get(position);
                questCounty();
            } else if (currentLevel == COUNTYLEVEL) {
                weatherId = counties.get(position).getWeatherId();
                edi.putString("weather_id", weatherId);
                edi.apply();
                questWeather();
            }
        } else if (flag == 1) {
            if (currentLevel == COUNTYLEVEL) {
                questCity();
            } else if (currentLevel == CITYLEVEL) {
                questProvince();
            }
        } else if (flag == 2) {
            questProvince();
        }

    }


    public MainModelImpl( MainContract.Model.Listener listener) {
        DaggerMainModelImplComponent.builder().build().inject(this);
        this.listener = listener;
        pre = PreferenceManager.getDefaultSharedPreferences(MyApplication.getContext());
        edi = pre.edit();
    }

    //获取省级列表
    public void questProvince() {
        provinces = LitePal.findAll(Province.class);
        if (provinces.size() > 0) {
            list.clear();
            for (int i = 0; i < provinces.size(); i++) {
                list.add(provinces.get(i).getProvinceName());
            }
            currentLevel = PROVINCELEVEL;
            listener.onSuccessLocality(list);

        } else {
            retrofit.create(HttpLocality.class).getLocality("china").enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    try {
                        String provinceResponse = response.body().string();
                        JSONArray array = new JSONArray(provinceResponse);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            //保存到数据库
                            Province province = new Province();
                            province.setProvinceCode(object.getInt("id"));
                            province.setProvinceName(object.getString("name"));
                            province.save();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    questProvince();
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    listener.onError(t);
                }
            });
        }
    }

    //获取市级列表
    private void questCity() {
        cities = LitePal.where("provinceid=?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cities.size() > 0) {
            list.clear();
            for (int i = 0; i < cities.size(); i++) {
                list.add(cities.get(i).getCityName());
            }
            currentLevel = CITYLEVEL;
            listener.onSuccessLocality(list);
        } else {
            retrofit.create(HttpLocality.class).getLocality("china/" + selectedProvince.getProvinceCode())
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String cityResponse = response.body().string();
                                JSONArray array = new JSONArray(cityResponse);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);
                                    //保存到数据库
                                    City city = new City();
                                    city.setProvinceId(selectedProvince.getId());
                                    city.setCityCode(object.getInt("id"));
                                    city.setCityName(object.getString("name"));
                                    city.save();
                                }

                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            questCity();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            listener.onError(t);
                        }
                    });
        }
    }

    //    //获取县级列表
    private void questCounty() {
        counties = LitePal.where("cityid=?", String.valueOf(selectedCity.getId())).find(County.class);
        if (counties.size() > 0) {
            list.clear();
            for (int i = 0; i < counties.size(); i++) {
                list.add(counties.get(i).getCountyName());
            }
            currentLevel = COUNTYLEVEL;
            listener.onSuccessLocality(list);
        } else {
            retrofit.create(HttpLocality.class).getLocality("china/" + selectedProvince.getProvinceCode() + "/" + selectedCity.getCityCode())
                    .enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            try {
                                String cityResponse = response.body().string();
                                JSONArray array = new JSONArray(cityResponse);
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object = array.getJSONObject(i);
                                    //保存到数据库
                                    County county = new County();
                                    county.setCityId(selectedCity.getId());
                                    county.setCountyName(object.getString("name"));
                                    county.setWeatherId(object.getString("weather_id"));
                                    county.save();
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            questCounty();
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            listener.onError(t);
                        }
                    });
        }
    }

    @Override
    public void questWeather() {
        if (weatherId == null) {
            weatherId = pre.getString("weather_id", "CN101010100");
        }
        retrofit.create(HttpWeather.class).getWeather("weather?cityid=" + weatherId + "&key=1622d8849bb54f24a6022f1b04e26b8a")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Weather>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Weather weather) {
                        listener.onSuccessWeather(weather);
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }


}
