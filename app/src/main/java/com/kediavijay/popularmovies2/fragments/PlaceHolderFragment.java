package com.kediavijay.popularmovies2.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kediavijay.popularmovies2.R;

/**
 * Created by vijaykedia on 18/04/16.
 */
public class PlaceHolderFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.place_holder_layout, container, false);
    }
}
