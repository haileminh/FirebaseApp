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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.AppConst;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.RegisterHouseListener;
import net.hailm.firebaseapp.model.dbhelpers.RegisterHouseDbHelper;
import net.hailm.firebaseapp.model.dbmodels.AddressModel;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;
import net.hailm.firebaseapp.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddHouseFragment extends Fragment implements OnMapReadyCallback {
    Unbinder unbinder;
    private View rootView;

    @BindView(R.id.img_1)
    ImageView img1;
    @BindView(R.id.img_2)
    ImageView img2;
    @BindView(R.id.img_3)
    ImageView img3;
    @BindView(R.id.img_4)
    ImageView img4;
    @BindView(R.id.img_5)
    ImageView img5;
    @BindView(R.id.img_6)
    ImageView img6;

    @BindView(R.id.edt_landlord)
    EditText edtLamdlord;
    @BindView(R.id.edt_address)
    EditText edtAddress;
    @BindView(R.id.edt_price)
    EditText edtPrice;
    @BindView(R.id.edt_acreage)
    EditText edtAcreage;
    @BindView(R.id.edt_tel)
    EditText edtTel;
    @BindView(R.id.edt_quantity)
    EditText edtQuantity;
    @BindView(R.id.edt_contents)
    EditText edtContents;

    @BindView(R.id.cb_gui_xe)
    CheckBox cbGuiXe;
    @BindView(R.id.cb_wifi)
    CheckBox cbWifi;
    @BindView(R.id.cb_may_lanh)
    CheckBox cbMayLanh;
    @BindView(R.id.cb_giuong)
    CheckBox cbGiuong;
    @BindView(R.id.cb_nong_lanh)
    CheckBox cbNongLanh;

    private SharedPreferences mSharedPreferences;
    private RegisterHouseDbHelper mRegisterHouseDbHelper;
    private SupportMapFragment mSupportMapFragment;
    private GoogleMap mGoogleMap;

    // Lấy myLocation
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double myLatitude;
    private double myLongtitude;
    private Marker myMarker;

    private boolean onCLickBtnLocation = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_add_house, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeComponents();
    }

    private void initializeComponents() {
        mRegisterHouseDbHelper = new RegisterHouseDbHelper(getActivity());
        mSharedPreferences = getActivity().getSharedPreferences(Constants.LOCATION, Context.MODE_PRIVATE);

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mSupportMapFragment.getMapAsync(this);
    }

    @OnClick({R.id.btn_register_house, R.id.btn_location})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_register_house:
                registerHouse();
                break;
            case R.id.btn_location:
                onCLickBtnLocation = true;
                getMyLocation();
                break;
            default:
                break;
        }
    }

    private void registerHouse() {
        if (checkInputData()) {
            HouseModel houseModel = createHouseModel();

            mRegisterHouseDbHelper.registerHouses(houseModel, new RegisterHouseListener() {
                @Override
                public void registerSuccess() {
                    LogUtils.d("Register house success");
                }

                @Override
                public void registerFailure(String message) {
                    LogUtils.d("Register  house Failure");
                }
            });

            AddressModel addressModel = createAddress();
            mRegisterHouseDbHelper.registerAddress(addressModel, houseModel, new RegisterHouseListener() {
                @Override
                public void registerSuccess() {
                    LogUtils.d("Register address success");
                }

                @Override
                public void registerFailure(String message) {
                    LogUtils.d("Register  address Failure");
                }
            });
        }
    }

    private HouseModel createHouseModel() {
        HouseModel houseModel = new HouseModel();
        String houseId = UUID.randomUUID().toString();
        houseModel.setHouseId(houseId);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(AppConst.DATE_FORMAT);
        houseModel.setUpdateDate(dateFormat.format(date));

        String uid = mSharedPreferences.getString(Constants.UID, "");
        houseModel.setUid(uid);

        String lamdlord = edtLamdlord.getText().toString().trim();
        houseModel.setLandlord(lamdlord);

        long price = Long.parseLong(edtPrice.getText().toString().trim());
        houseModel.setPrice(price);

        long acreage = Long.parseLong(edtAcreage.getText().toString().trim());
        houseModel.setAcreage(acreage);

        String tel = edtTel.getText().toString().trim();
        houseModel.setTel(tel);

        String contents = edtContents.getText().toString().trim();
        houseModel.setContents(contents);

        long quantity = Long.parseLong(edtQuantity.getText().toString().trim());
        houseModel.setQuantity(quantity);

        return houseModel;
    }

    private AddressModel createAddress() {
        AddressModel addressModel = new AddressModel();

        if (onCLickBtnLocation) {
            addressModel.setLatitude(myLatitude);
            addressModel.setLongitude(myLongtitude);
        } else {
            double latitude = Double.parseDouble(mSharedPreferences.getString(Constants.LATITUDE, "0"));
            double longitude = Double.parseDouble(mSharedPreferences.getString(Constants.LONGITUDE, "0"));
            addressModel.setLatitude(latitude);
            addressModel.setLongitude(longitude);
        }


        String address = edtAddress.getText().toString().trim();
        addressModel.setAddress(address);
        return addressModel;
    }

    private boolean checkInputData() {
        if (Utils.isEmpty(edtLamdlord) && Utils.isEmpty(edtAddress)
                && Utils.isEmpty(edtPrice) && Utils.isEmpty(edtAcreage)
                && Utils.isEmpty(edtQuantity)
                && Utils.isEmpty(edtTel) && Utils.isEmpty(edtContents)) {
            String tel = edtTel.getText().toString().trim();
            if (!Utils.isPhoneNumber(tel)) {
                edtTel.requestFocus();
                edtTel.setError(getString(R.string.khong_phai_so_dien_thoai));
                return false;
            } else if (!onCLickBtnLocation) {
                Toast.makeText(getActivity(), "Bạn nhập vị trị nhà trọ", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
    }

    private void getMyLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LogUtils.d("myLatitude register: " + myLatitude);
                    LogUtils.d("myLongtitude register: " + myLongtitude);

                    myLatitude = location.getLatitude();
                    myLongtitude = location.getLongitude();

                    showMyMarker();
                    showMyLocation();
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void showMyMarker() {
        if (myMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(myLatitude, myLongtitude));
            mGoogleMap.addMarker(markerOptions);
        }
    }

    private void showMyLocation() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(myLatitude, myLongtitude))
                .zoom(15)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
