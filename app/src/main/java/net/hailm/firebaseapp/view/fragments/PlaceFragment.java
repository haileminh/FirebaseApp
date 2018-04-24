package net.hailm.firebaseapp.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.LogUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.model.dbhelpers.HouseDbHelper;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hai.lm on 13/04/2018.
 */

public class PlaceFragment extends Fragment implements OnMapReadyCallback {
    private View rootView;
    Unbinder unbinder;

    private GoogleMap mGoogleMap;
    private SupportMapFragment mSupportMapFragment;
    private SharedPreferences mSharedPreferences;

    public PlaceFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_place, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mSupportMapFragment.getMapAsync(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        showMyMarker(mGoogleMap);
    }

    private void showMyMarker(GoogleMap mGoogleMap) {
        mSharedPreferences = getActivity().getSharedPreferences(Constants.LOCATION, Context.MODE_PRIVATE);

        double latitude = Double.parseDouble(mSharedPreferences.getString(Constants.LATITUDE, "0"));
        double longitude = Double.parseDouble(mSharedPreferences.getString(Constants.LONGITUDE, "0"));
        LatLng latLng = new LatLng(latitude, longitude);
        LogUtils.d("latLng PlaceFrament: " + latLng);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        mGoogleMap.addMarker(markerOptions);
        showMyLocation(latitude, longitude, mGoogleMap);
    }

    private void showMyLocation(double latitude, double longitude, GoogleMap mGoogleMap) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(15)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

}
