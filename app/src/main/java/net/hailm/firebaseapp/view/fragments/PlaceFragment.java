package net.hailm.firebaseapp.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.hailm.firebaseapp.R;

/**
 * Created by hai.lm on 13/04/2018.
 */

public class PlaceFragment extends Fragment {
    private View rootView;

    public PlaceFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_place, container, false);
        return rootView;
    }
}
