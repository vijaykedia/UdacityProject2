package com.kediavijay.popularmovies2.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

/**
 * Created by vijaykedia on 11/04/16.
 * In order for the sync adapter framework to access authenticator, we need create a bound Service for it.
 * Reference : http://developer.android.com/training/sync-adapters/creating-authenticator.html
 */
public class AuthenticatorService extends Service {

    private Authenticator authenticator;

    @Override
    public void onCreate() {
        super.onCreate();
        authenticator = new Authenticator(getApplicationContext());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return authenticator.getIBinder();
    }
}
