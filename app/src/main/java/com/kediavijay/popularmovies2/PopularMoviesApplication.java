package com.kediavijay.popularmovies2;

import android.app.Application;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.facebook.stetho.Stetho;

/**
 * Created by vijaykedia on 10/04/16.
 * This will serve as global context
 */
public class PopularMoviesApplication extends Application {

    private static final String LOG_TAG = PopularMoviesApplication.class.getSimpleName();

    private static PopularMoviesApplication sInstance;

    private RequestQueue mRequestQueue;
    private ImageLoader imageLoader;

    public void onCreate() {

        Log.d(LOG_TAG, "onCreate() -- Starting application.");

        super.onCreate();
        sInstance = this;
        Stetho.initializeWithDefaults(this);
    }

    public static synchronized PopularMoviesApplication getInstance() {
        return sInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(@NonNull final Request<T> request, @NonNull final String tag) {
        request.setTag(tag);
        getRequestQueue().add(request);
    }

    public ImageLoader getImageLoader() {

        if(imageLoader == null) {

            imageLoader = new ImageLoader(getRequestQueue(), new ImageLoader.ImageCache() {

                private final LruCache<String, Bitmap>
                        cache = new LruCache<>(50);

                @Override
                public Bitmap getBitmap(String url) {
                    return cache.get(url);
                }

                @Override
                public void putBitmap(String url, Bitmap bitmap) {
                    cache.put(url, bitmap);
                }
            });
        }
        return imageLoader;
    }
}
