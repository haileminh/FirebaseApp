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

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PolylineHouseFragment extends Fragment implements OnMapReadyCallback {
    private View rootView;
    Unbinder unbinder;

    private GoogleMap mGoogleMap;
    private SupportMapFragment mSupportMapFragment;
    private double latitudeHouse;
    private double longitudeHouse;

    private SharedPreferences mSharedPreferences;
    private Location currentLocation;

    public PolylineHouseFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_polyline_house, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mSupportMapFragment.getMapAsync(this);

        latitudeHouse = getArguments().getDouble(Constants.Latitude);
        longitudeHouse = getArguments().getDouble(Constants.Longitude);

        LogUtils.d("LatLng:" + latitudeHouse + "," + longitudeHouse);


        mSharedPreferences = getContext().getSharedPreferences(Constants.LOCATION, Context.MODE_PRIVATE);

        LogUtils.d("LOCATION: "
                + mSharedPreferences.getString(Constants.LATITUDE, "0")
                + ", " + mSharedPreferences.getString(Constants.LONGITUDE, "0"));
        currentLocation = new Location("");

        currentLocation.setLatitude(Double.parseDouble(mSharedPreferences.getString(Constants.LATITUDE, "0")));
        currentLocation.setLongitude(Double.parseDouble(mSharedPreferences.getString(Constants.LONGITUDE, "0")));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));

        mGoogleMap.addMarker(markerOptions);

        showMyLocation();
    }

    private void showMyLocation() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()))
                .zoom(15)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
