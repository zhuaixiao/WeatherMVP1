package com.example.test.contract;

import com.example.test.json.Weather;

import java.util.List;

public interface MainContract {
    interface Model {
        /**
         * 请求区域地址
         *
         * @param position 当前点击的位置，没有则可传2
         * @param flag     继续向下获取地址，传入0；返回上级地址，传入1；没有则可传入2
         */
        void questLocality(int position, int flag);

        /**
         * 请求天气信息
         */
        void questWeather();

        /**
         * 监听接口
         */
        interface Listener {
            //错误
            void onError(Throwable t);

            //地址获取成功
            void onSuccessLocality(List<String> l);

            //天气获取成功
            void onSuccessWeather(Weather w);
        }
    }

    interface View {
        void questLocality(int position, int flag);

        void questWeather();

        void showLocality(List<String> l);

        void showWeather(Weather w);

        void onError(Throwable t);

    }

    interface Presenter {
        void questLocality(int position, int flag);

        void questWeather();
    }
}
