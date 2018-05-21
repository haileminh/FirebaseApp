package net.hailm.firebaseapp.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.AddressListener;
import net.hailm.firebaseapp.listener.HouseListener;
import net.hailm.firebaseapp.listener.PopupSearchCallback;
import net.hailm.firebaseapp.model.dbhelpers.HouseDbHelper;
import net.hailm.firebaseapp.model.dbhelpers.PlaceDbHelper;
import net.hailm.firebaseapp.model.dbmodels.AddressModel;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;
import net.hailm.firebaseapp.view.dialogs.SearchDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by hai.lm on 13/04/2018.
 */

public class PlaceFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        PopupSearchCallback {
    private View rootView;
    @BindView(R.id.txt_place_distance)
    TextView txtDistance;
    @BindView(R.id.txt_place_content_search)
    TextView txtContentSearch;
    Unbinder unbinder;

    private GoogleMap mGoogleMap;
    private SupportMapFragment mSupportMapFragment;
    private SharedPreferences mSharedPreferences;

    private HouseDbHelper mDbHelper;
    private MarkerOptions markerOptions;
    private Marker myMarker;
    private MyInfoHouse mMyInfoHouse;
    private Location currentLocation;
    private double latitude;
    private double longitude;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    private boolean checkAllHouse = true;
    private boolean checkSearchHouse = false;
    private double mDistance;
    private long mPrice;
    private long mAcreage;

    private List<Marker> markerList;
    private List<HouseModel> houseModelList;

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
        markerOptions = new MarkerOptions();

        mSharedPreferences = getActivity().getSharedPreferences(Constants.LOCATION, Context.MODE_PRIVATE);
        latitude = Double.parseDouble(mSharedPreferences.getString(Constants.LATITUDE, "0"));
        longitude = Double.parseDouble(mSharedPreferences.getString(Constants.LONGITUDE, "0"));
        currentLocation = new Location("");
        currentLocation.setLatitude(latitude);
        currentLocation.setLongitude(longitude);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        showMyMarker();
    }

    private void showMyMarker() {
        LatLng latLng = new LatLng(latitude, longitude);
        LogUtils.d("latLng PlaceFrament: " + latLng);

        markerOptions.position(latLng);
        markerOptions.title("me");
        mGoogleMap.addMarker(markerOptions);
        showMyLocation(latitude, longitude);
        initializeComponents();
        mGoogleMap.setOnInfoWindowClickListener(this);
    }

    private void showMyLocation(double latitude, double longitude) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(15)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void initializeComponents() {
        mMyInfoHouse = new MyInfoHouse(getActivity());
        mDbHelper = new HouseDbHelper();
        markerList = new ArrayList<>();
        houseModelList = new ArrayList<>();

        HouseListener listener = new HouseListener() {
            @Override
            public void getListHouseModel(HouseModel houseModel) {
                if (checkAllHouse) {
                    houseModelList.add(houseModel);
                    showMarkerAddress(houseModel);
                } else if (checkSearchHouse) {
                    showHouseBySearch(houseModel);
                }
            }
        };

        mDbHelper.getListHouse(listener, currentLocation, 1000, 0);
    }

    private void showMarkerAddress(HouseModel houseModel) {
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
        mGoogleMap.setInfoWindowAdapter(mMyInfoHouse);

        try {
            mGoogleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        double latitude = houseModel.getAddressModel().getLatitude();
        double longitude = houseModel.getAddressModel().getLongitude();

        LatLng latLng = new LatLng(latitude, longitude);
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_view_house));

        myMarker = mGoogleMap.addMarker(markerOptions);
        markerList.add(myMarker);
        myMarker.setTag(houseModel);
    }

    private void showMarkerAddressBySearch() {
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.getUiSettings().setMapToolbarEnabled(true);
        mGoogleMap.setInfoWindowAdapter(mMyInfoHouse);

        try {
            mGoogleMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < houseModelList.size(); i++) {
            double latitude = houseModelList.get(i).getAddressModel().getLatitude();
            double longitude = houseModelList.get(i).getAddressModel().getLongitude();

            LatLng latLng = new LatLng(latitude, longitude);
            markerOptions.position(latLng);
            markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_view_house));

            myMarker = mGoogleMap.addMarker(markerOptions);
            markerList.add(myMarker);
            myMarker.setTag(houseModelList.get(i));
        }

        CircleOptions circleOptions = new CircleOptions();
        circleOptions.center(new LatLng(latitude, longitude));
        circleOptions.radius(mDistance * 1000);
        circleOptions.fillColor(Color.argb(40, 100, 30, 80));
        circleOptions.strokeWidth(0);
        mGoogleMap.addCircle(circleOptions);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        LogUtils.d("Info window clicked");
        HouseDetailFragment houseDetailFragment = new HouseDetailFragment();
        manager = getActivity().getSupportFragmentManager();
        transaction = manager.beginTransaction();

        Bundle bundle = new Bundle();
        HouseModel houseModel = (HouseModel) marker.getTag();
        bundle.putParcelable(Constants.HOUSE_MODEL, houseModel);
        houseDetailFragment.setArguments(bundle);

        transaction.replace(android.R.id.content, houseDetailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @OnClick({R.id.ll_search})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.ll_search:
//                manager = getActivity().getSupportFragmentManager();
//                PopupSearch popupSearch = new PopupSearch();
//                popupSearch.setTargetFragment(this, 1001);
//                popupSearch.show(manager, "search");

                SearchDialog dialog = new SearchDialog(getContext(), this);
                dialog.show();
                break;
            default:
                break;
        }
    }

    @Override
    public void onButtonClick(double distance, long price, long acreage) {
        txtDistance.setText(String.valueOf(distance));
        txtContentSearch.setText(String.valueOf(price) + " / " + String.valueOf(acreage));

        checkSearchHouse = true;
        checkAllHouse = false;
        mDistance = distance;
        mPrice = price;
        mAcreage = acreage;
        initializeComponents();
//        showHouse(distance, price, acreage);

    }

    private void showHouseBySearch(HouseModel houseModel) {
//        if (houseModel.getAddressModel().getDistance() < mDistance) {
//            houseModelList.add(houseModel);
//            mGoogleMap.clear();
//            markerList.clear();
//            showMarkerAddressBySearch();
//        }

        //
        if (mDistance != 0.0) {
            if (houseModel.getAddressModel().getDistance() <= mDistance) {
                if (mPrice == 0 && mAcreage == 0) {
                    houseModelList.add(houseModel);
                    mGoogleMap.clear();
                    markerList.clear();
                    showMarkerAddressBySearch();
                } else if (mPrice != 0 && mAcreage != 0) {
                    if (houseModel.getPrice() >= mPrice*1000000 && houseModel.getAcreage() >= mAcreage) {
                        houseModelList.add(houseModel);
                        mGoogleMap.clear();
                        markerList.clear();
                        showMarkerAddressBySearch();
                    }
                } else if (mPrice != 0 && mAcreage == 0) {
                    if (houseModel.getPrice() >= mPrice*1000000) {
                        houseModelList.add(houseModel);
                        mGoogleMap.clear();
                        markerList.clear();
                        showMarkerAddressBySearch();
                    }
                } else if (mAcreage != 0 && mPrice == 0) {
                    if (houseModel.getPrice() >= mAcreage) {
                        houseModelList.add(houseModel);
                        mGoogleMap.clear();
                        markerList.clear();
                        showMarkerAddressBySearch();
                    }
                }
            }
        } else {

        }

    }

    private void showHouse(final double distance, long price, long acreage) {
        mMyInfoHouse = new MyInfoHouse(getActivity());
        mDbHelper = new HouseDbHelper();


        HouseListener listener = new HouseListener() {
            @Override
            public void getListHouseModel(HouseModel houseModel) {
                if (distance > 0) {
                    if (houseModel.getAddressModel().getDistance() > 50) {
                        showMarkerAddress(houseModel);
                    }

                }
            }
        };

        mDbHelper.getListHouse(listener, currentLocation, 1000, 0);
    }
}
