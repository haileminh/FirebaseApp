package net.hailm.firebaseapp.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class EditHouseFragment extends Fragment implements OnMapReadyCallback {
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
    @BindView(R.id.cb_ban_ghe)
    CheckBox cbBanGhe;

    private HouseModel houseModel;
    private String uid;
    private SharedPreferences mSharedPreferences;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mSupportMapFragment;

    public EditHouseFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_edit_house, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            houseModel = (HouseModel) bundle.get(Constants.HOUSE_MODEL_EDIT);
            LogUtils.d("houseModel in EditHouseFragment: " + houseModel);
        } else {
            LogUtils.d("Bundle null in EditHouseFragment");
        }

        // Get uid app when user login
        mSharedPreferences = getActivity().getSharedPreferences(Constants.LOCATION, Context.MODE_PRIVATE);
        uid = mSharedPreferences.getString(Constants.UID, "");

        mSupportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mSupportMapFragment.getMapAsync(this);

        showData();
    }

    private void showData() {
        edtLamdlord.setText(houseModel.getLandlord());
        edtAddress.setText(houseModel.getAddressModel().getAddress());
        edtPrice.setText(String.valueOf(houseModel.getPrice()));
        edtAcreage.setText(String.valueOf(houseModel.getAcreage()));
        edtTel.setText(houseModel.getTel());
        edtQuantity.setText(String.valueOf(houseModel.getQuantity()));
        edtContents.setText(String.valueOf(houseModel.getContents()));

        List<String> utility = houseModel.getUtility();
        LogUtils.d("utility: " + utility);
        for (int i = 0; i < utility.size(); i++) {
            if (utility.get(i).equals(getString(R.string.MaTienIch1))) {
                cbWifi.setChecked(true);
            }

            if (utility.get(i).equals(getString(R.string.MaTienIch2))) {
                cbMayLanh.setChecked(true);
            }

            if (utility.get(i).equals(getString(R.string.MaTienIch3))) {
                cbBanGhe.setChecked(true);
            }

            if (utility.get(i).equals(getString(R.string.MaTienIch4))) {
                cbGuiXe.setChecked(true);
            }

            if (utility.get(i).equals(getString(R.string.MaTienIch5))) {
                cbNongLanh.setChecked(true);
            }

            if (utility.get(i).equals(getString(R.string.MaTienIch6))) {
                cbGiuong.setChecked(true);
            }
        }

        List<ImageView> listImage = new ArrayList<>();
        listImage.add(img1);
        listImage.add(img2);
        listImage.add(img3);
        listImage.add(img4);
        listImage.add(img5);
        listImage.add(img6);

        if (houseModel.getHouseImages().size() > 0) {
            for (int i = 0; i < houseModel.getHouseImages().size(); i++) {
                StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                        .child(Constants.IMAGES)
                        .child(houseModel.getHouseImages().get(i));
                Glide.with(getContext()).using(new FirebaseImageLoader()).load(storageReference).into(listImage.get(i));
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
        double latitude = houseModel.getAddressModel().getLatitude();
        double longitude = houseModel.getAddressModel().getLongitude();
        LatLng latLng = new LatLng(latitude, longitude);

        LogUtils.d("Map:" + latLng);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(new LatLng(latitude, longitude));
        markerOptions.title(houseModel.getLandlord());


        mGoogleMap.addMarker(markerOptions);

        showMyLocation(latitude, longitude, mGoogleMap);
    }

    private void showMyLocation(double latitude, double longitude, GoogleMap googleMap) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(15)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }
}
