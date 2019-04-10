package com.example.test.presenter;

import com.example.test.contract.MainContract;
import com.example.test.json.Weather;
import com.example.test.model.MainModelImpl;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.List;

public class MainPresenterImpl implements MainContract.Presenter {
    private MainContract.Model model;
    private MainContract.View view;
    private Reference<MainContract.View> r2;

    @Override
    public void questLocality(int position, int flag) {
        model.questLocality(position, flag);
    }

    @Override
    public void questWeather() {
        model.questWeather();
    }

    MainContract.Model.Listener listener = new MainContract.Model.Listener() {
        @Override
        public void onError(Throwable t) {
            view.onError(t);
        }

        @Override
        public void onSuccessLocality(List<String> l) {
            view.showLocality(l);
        }

        @Override
        public void onSuccessWeather(Weather w) {
            view.showWeather(w);
        }
    };

    public MainPresenterImpl(MainContract.View v) {
        r2 = new WeakReference<>(v);

        if (r2.get() != null) {
            view = r2.get();
        }
        model = new MainModelImpl(listener);
    }
}
