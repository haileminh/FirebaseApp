package net.hailm.firebaseapp.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.AppConst;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.RegisterHouseListener;
import net.hailm.firebaseapp.model.dbhelpers.RegisterHouseDbHelper;
import net.hailm.firebaseapp.model.dbmodels.AddressModel;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AddHouseFragment extends Fragment {
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
    }

    @OnClick({R.id.btn_register_house})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_register_house:
                registerHouse();
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
        houseModel.setLandlord("Lê Minh Hải");
        houseModel.setPrice(1234556);
        houseModel.setAcreage(12);
        houseModel.setTel("012345666");
        houseModel.setContents("addddddddd");
        houseModel.setQuantity(1);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(AppConst.DATE_FORMAT);
        houseModel.setUpdateDate(dateFormat.format(date));

        String uid = mSharedPreferences.getString(Constants.UID, "");
        houseModel.setUid(uid);

        return houseModel;
    }

    private AddressModel createAddress() {
        AddressModel addressModel = new AddressModel();
        double latitude = Double.parseDouble(mSharedPreferences.getString(Constants.LATITUDE, "0"));
        double longitude = Double.parseDouble(mSharedPreferences.getString(Constants.LONGITUDE, "0"));
        addressModel.setLatitude(latitude);
        addressModel.setLatitude(longitude);
        addressModel.setAddress("abcbabjdcajdbka");
        return addressModel;
    }

    private boolean checkInputData() {

        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }
}
