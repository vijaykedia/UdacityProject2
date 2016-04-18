package com.kediavijay.popularmovies2.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by vijaykedia on 11/04/16.
 * <p/>
 * Service to handle sync requests
 * <p/>
 * This service is invoked in response to Intents with action android.content.SyncAdapter, and
 * returns a Binder connection to {@link SyncAdapter}.
 * <p/>
 * Note: The {@link SyncService} itself is not notified when a new sync occurs. It's role is to
 * manage the lifecycle of our {@link SyncAdapter} and provide a handle to said {@link SyncAdapter} to the
 * OS on request.
 */
public class SyncService extends Service {

    private static final String LOG_TAG = SyncService.class.getSimpleName();

    private static final Object sSyncAdapterLock = new Object();
    private static SyncAdapter sSyncAdapter = null;

    /**
     * Thread-Safe constructor. Initialize static {@link SyncAdapter} instance
     */
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(LOG_TAG, "SyncService created");
        synchronized (sSyncAdapterLock) {
            if (sSyncAdapter == null) {
                sSyncAdapter = new SyncAdapter(getApplicationContext(), true);
            }
        }
//        android.os.Debug.waitForDebugger();
        System.out.print("hello");
    }

    /**
     * Return Binder handle for IPC communication with {@link SyncAdapter}.
     * <p/>
     * New sync requests will be sent directly to the SyncAdapter using this channel.
     *
     * @param intent Calling intent
     * @return Binder handle for {@link SyncAdapter}
     */
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return sSyncAdapter.getSyncAdapterBinder();
    }

    /**
     * Logging only destructor
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(LOG_TAG, "SyncService destroyed");
    }
}
