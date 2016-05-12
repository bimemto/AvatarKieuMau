package com.duyota.standardavatar;

import android.app.Application;

import com.karumi.dexter.Dexter;

/**
 * Created by admin on 5/12/16.
 */
public class StandardApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Dexter.initialize(this);
    }
}
