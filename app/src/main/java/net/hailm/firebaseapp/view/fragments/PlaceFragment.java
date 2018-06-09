package net.hailm.firebaseapp.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
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
import net.hailm.firebaseapp.listener.HouseListener;
import net.hailm.firebaseapp.listener.HouseRcvAdapterCallback;
import net.hailm.firebaseapp.listener.PopupSearchCallback;
import net.hailm.firebaseapp.model.dbhelpers.HouseDbHelper;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;
import net.hailm.firebaseapp.view.adapters.HouseRcvAdapter;
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
        PopupSearchCallback, HouseRcvAdapterCallback {
    private static final int REQUEST_CODE_PERMISSION_CALL_PHONE = 1000;
    private View rootView;
    @BindView(R.id.edt_address)
    EditText edtAddress;
    @BindView(R.id.txt_total_search)
    TextView txtTotalSearch;
    @BindView(R.id.txt_place_distance)
    TextView txtDistance;
    @BindView(R.id.txt_place_content_search)
    TextView txtContentSearch;
    @BindView(R.id.bottom_sheet)
    View bottomSheet;
    @BindView(R.id.rcv_house_search)
    RecyclerView mRcvHouseSearch;

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
    private boolean checkBtnSearch = false;
    private double mDistance;
    private long mPrice;
    private long mAcreage;

    private List<Marker> markerList;
    private List<HouseModel> houseModelList;

    private BottomSheetBehavior mBottomSheetBehavior;
    private HouseRcvAdapter mHouseRcvAdapter;
    private String mTel;

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

        mBottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    mBottomSheetBehavior.setPeekHeight(0);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });
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
        showMyLocation(latitude, longitude, 15);
        initShowIconHouseInMap();
        mGoogleMap.setOnInfoWindowClickListener(this);
    }

    private void showMyLocation(double latitude, double longitude, int zoom) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(zoom)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void initShowIconHouseInMap() {
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
                    String totalSearch = String.valueOf(houseModelList.size()) + " phòng";
                    txtTotalSearch.setText(totalSearch);
                    setAdapter(houseModelList, getActivity());
                } else if (checkBtnSearch) {
                    showHouseByAddress(houseModel, edtAddress.getText().toString().trim());
                    String totalSearch = String.valueOf(houseModelList.size()) + " phòng";
                    txtTotalSearch.setText(totalSearch);
                    setAdapter(houseModelList, getActivity());
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

        showMyLocation(latitude, longitude, 13);
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

    @OnClick({R.id.ll_search, R.id.img_search, R.id.txt_total_search})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.ll_search:
//                manager = getActivity().getSupportFragmentManager();
//                PopupSearch popupSearch = new PopupSearch();
//                popupSearch.setTargetFragment(this, 1001);
//                popupSearch.show(manager, "search");
                edtAddress.setVisibility(View.GONE);
                SearchDialog dialog = new SearchDialog(getContext(), this);
                dialog.show();
                break;
            case R.id.img_search:
                checkBtnSearch = true;
                checkAllHouse = false;
                checkSearchHouse = false;
                edtAddress.setVisibility(View.VISIBLE);
                initShowIconHouseInMap();
                break;
            case R.id.txt_total_search:
                bottomSheet.setVisibility(View.VISIBLE);
                mBottomSheetBehavior.setPeekHeight(300);
                mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                break;
            default:
                break;
        }
    }

    @Override
    public void onButtonClick(double distance, long price, long acreage) {
        if (distance == 0.0) {
            String ditacneText = getString(R.string.tim_theo_khoang_cach) + " Tất cả";
            txtDistance.setText(ditacneText);
        } else {
            String ditacneText = getString(R.string.tim_theo_khoang_cach) + " " + distance + " " + getString(R.string.km);
            txtDistance.setText(ditacneText);
        }

        String boLoc = getString(R.string.gia_phong) + " lớn hơn " + price + " triệu đồng /"
                + getString(R.string.dien_tich) + " lớn hơn " + acreage + " " + getString(R.string.m2);
        txtContentSearch.setText(boLoc);

        checkSearchHouse = true;
        checkAllHouse = false;
        mDistance = distance;
        mPrice = price;
        mAcreage = acreage;
        initShowIconHouseInMap();
    }

    private void showHouseBySearch(HouseModel houseModel) {
        if (mDistance != 0.0) {
            if (houseModel.getAddressModel().getDistance() <= mDistance) {
                if (mPrice == 0 && mAcreage == 0) {
                    houseModelList.add(houseModel);
                    mGoogleMap.clear();
                    markerList.clear();
                    showMarkerAddressBySearch();
                } else if (mPrice != 0 && mAcreage != 0) {
                    if (houseModel.getPrice() >= mPrice * 1000000 && houseModel.getAcreage() >= mAcreage) {
                        houseModelList.add(houseModel);
                        mGoogleMap.clear();
                        markerList.clear();
                        showMarkerAddressBySearch();
                    }
                } else if (mPrice != 0 && mAcreage == 0) {
                    if (houseModel.getPrice() >= mPrice * 1000000) {
                        houseModelList.add(houseModel);
                        mGoogleMap.clear();
                        markerList.clear();
                        showMarkerAddressBySearch();
                    }
                } else if (mAcreage != 0 && mPrice == 0) {
                    if (houseModel.getAcreage() >= mAcreage) {
                        houseModelList.add(houseModel);
                        mGoogleMap.clear();
                        markerList.clear();
                        showMarkerAddressBySearch();
                    }
                }
            }
        } else {
            if (mPrice == 0 && mAcreage == 0) {
                houseModelList.add(houseModel);
                mGoogleMap.clear();
                markerList.clear();
                showMarkerAddressBySearch();
            } else if (mPrice != 0 && mAcreage != 0) {
                if (houseModel.getPrice() >= mPrice * 1000000 && houseModel.getAcreage() >= mAcreage) {
                    houseModelList.add(houseModel);
                    mGoogleMap.clear();
                    markerList.clear();
                    showMarkerAddressBySearch();
                }
            } else if (mPrice != 0 && mAcreage == 0) {
                if (houseModel.getPrice() >= mPrice * 1000000) {
                    houseModelList.add(houseModel);
                    mGoogleMap.clear();
                    markerList.clear();
                    showMarkerAddressBySearch();
                }
            } else if (mAcreage != 0 && mPrice == 0) {
                if (houseModel.getAcreage() >= mAcreage) {
                    houseModelList.add(houseModel);
                    mGoogleMap.clear();
                    markerList.clear();
                    showMarkerAddressBySearch();
                }
            }
        }
    }

    private void showHouseByAddress(HouseModel houseModel, String address) {
        String a = houseModel.getAddressModel().getAddress().toLowerCase();
        String b = address.toLowerCase();

        if (a.contains(b)) {
            houseModelList.add(houseModel);
            mGoogleMap.clear();
            markerList.clear();
            showMarkerAddressBySearch();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void setAdapter(List<HouseModel> houseModelList, Context context) {
        if (context != null) {
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            mRcvHouseSearch.setLayoutManager(llm);
            mHouseRcvAdapter = new HouseRcvAdapter(houseModelList, context, this);
            mRcvHouseSearch.setAdapter(mHouseRcvAdapter);
            mHouseRcvAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemCLick(HouseModel houseModel) {
        HouseDetailFragment houseDetailFragment = new HouseDetailFragment();
        manager = getActivity().getSupportFragmentManager();
        transaction = manager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.HOUSE_MODEL, houseModel);
        houseDetailFragment.setArguments(bundle);

        transaction.replace(android.R.id.content, houseDetailFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onBtnClick(String tel) {
        mTel = tel;
        requesPermisions();
    }

    private void phoneCall() {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        String call = "tel:" + mTel;
        intent.setData(Uri.parse(call));
        startActivity(intent);
    }

    private void requesPermisions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isPermissionGranted(android.Manifest.permission.CALL_PHONE)) {
                phoneCall();
            } else {
                String[] permissions = new String[]{
                        android.Manifest.permission.CALL_PHONE
                };
                ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_CODE_PERMISSION_CALL_PHONE);
            }
        } else {
            phoneCall();
        }
    }

    private Boolean isPermissionGranted(String permission) {
        return ContextCompat.checkSelfPermission(getActivity(), permission) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_CALL_PHONE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    phoneCall();
                } else {
                    Toast.makeText(getActivity(), "Bạn cần cấp quyền để thực hiện chức năng này", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;
        }
    }
}
