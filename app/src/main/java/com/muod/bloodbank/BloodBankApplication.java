package com.muod.bloodbank;

import android.app.Application;

import com.muod.bloodbank.utils.FontOverride;

public class BloodBankApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        FontOverride.setDefaultFont(this, "DEFAULT", "fonts/VarelaRound-Regular.ttf");
        FontOverride.setDefaultFont(this, "MONOSPACE", "fonts/VarelaRound-Regular.ttf");
        FontOverride.setDefaultFont(this, "SERIF", "fonts/VarelaRound-Regular.ttf");
        FontOverride.setDefaultFont(this, "SANS_SERIF", "fonts/VarelaRound-Regular.ttf");
    }
}
