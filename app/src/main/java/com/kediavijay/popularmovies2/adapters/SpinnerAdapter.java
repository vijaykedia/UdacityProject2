package com.kediavijay.popularmovies2.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by vijaykedia on 20/04/16.
 * This will serve as adapter for spinner
 */
public class SpinnerAdapter extends ArrayAdapter<String> {

    public SpinnerAdapter(@NonNull final Context context, final int resource, @NonNull final List<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getDropDownView(final int position, View convertView, @NonNull final ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

    @Override
    public View getView(final int position, View convertView, @NonNull final ViewGroup parent) {
        return super.getView(position, convertView, parent);
    }
}
