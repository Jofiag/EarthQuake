package com.jofiagtech.earthquake.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.jofiagtech.earthquake.R;

public class CustomInfoWindow implements GoogleMap.InfoWindowAdapter {

    private View mView;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public CustomInfoWindow(Context context) {
        mContext = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = mLayoutInflater.inflate(R.layout.info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
