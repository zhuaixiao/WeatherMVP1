package com.example.test.inject;

import com.example.test.module.HttpModule;
import com.example.test.model.MainModelImpl;

import dagger.Component;

@Component(modules = HttpModule.class)
public interface MainModelImplComponent {
    void inject(MainModelImpl mainModel);
}
