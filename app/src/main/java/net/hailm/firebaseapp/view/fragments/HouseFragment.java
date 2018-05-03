package net.hailm.firebaseapp.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.hailm.firebaseapp.Manifest;
import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.AppConst;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.HouseListener;
import net.hailm.firebaseapp.listener.HouseRcvAdapterCallback;
import net.hailm.firebaseapp.model.dbhelpers.HouseDbHelper;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;
import net.hailm.firebaseapp.view.adapters.HouseRcvAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by hai.lm on 13/04/2018.
 */

public class HouseFragment extends Fragment implements HouseRcvAdapterCallback {
    private static final int REQUEST_CODE_PERMISSION_CALL_PHONE = 1000;
    @BindView(R.id.rcv_house)
    RecyclerView mRcvHouse;
    @BindView(R.id.pgb_house)
    ProgressBar pgbHouse;
    @BindView(R.id.nestedScrollView)
    NestedScrollView mNestedScrollView;

    Unbinder unbinder;
    private View rootView;
    private HouseDbHelper mDbHelper;
    private HouseRcvAdapter mHouseRcvAdapter;
    private List<HouseModel> houseModelList;
    private List<HouseModel> listData = new ArrayList<>();
    private int itemLoaded = 10;

    private SharedPreferences mSharedPreferences;
    private Location currentLocation;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    private boolean onClickRecentDate = true;
    private boolean onClickLocation = false;
    private boolean onClickPrice = false;

    private String mTel;

    public HouseFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_house, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeComponents();
        initializeListHouse();
//        initialize();
    }

    private void initializeComponents() {
        mDbHelper = new HouseDbHelper();

        mSharedPreferences = getContext().getSharedPreferences(Constants.LOCATION, Context.MODE_PRIVATE);

        LogUtils.d("LOCATION: "
                + mSharedPreferences.getString(Constants.LATITUDE, "0")
                + ", " + mSharedPreferences.getString(Constants.LONGITUDE, "0"));
        currentLocation = new Location("");

        currentLocation.setLatitude(Double.parseDouble(mSharedPreferences.getString(Constants.LATITUDE, "0")));
        currentLocation.setLongitude(Double.parseDouble(mSharedPreferences.getString(Constants.LONGITUDE, "0")));
    }

    private void initializeListHouse() {
        pgbHouse.setVisibility(View.VISIBLE);
        houseModelList = new ArrayList<>();
        final HouseListener listener = new HouseListener() {
            @Override
            public void getListHouseModel(HouseModel houseModel) {
                houseModelList.add(houseModel);

                if (onClickRecentDate) {
                    sortByUpdateDate();
                } else if (onClickLocation) {
                    sortByUpdateLocation();
                } else if (onClickPrice) {
                    sortByUpdatePrice();
                }

                setAdapter(houseModelList, getActivity());
                LogUtils.d("houseModelList" + houseModelList);

                listData.addAll(houseModelList);
            }
        };

        LogUtils.d("houseModelList 2" + listData);

        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (v.getChildAt(v.getChildCount() - 1) != null) {
                    if (scrollY >= v.getChildAt(v.getChildCount() - 1).getMeasuredHeight() - v.getMeasuredHeight()) {
                        itemLoaded += 10;
                        mDbHelper.getListHouse(listener, currentLocation, itemLoaded, itemLoaded - 10);
                    }
                }
            }
        });

        mDbHelper.getListHouse(listener, currentLocation, itemLoaded, 0);
    }

    private void sortByUpdateDate() {
        Collections.sort(houseModelList, new Comparator<HouseModel>() {
            Date date1;
            Date date2;

            @Override
            public int compare(HouseModel o1, HouseModel o2) {
                String updateDate1 = o1.getUpdateDate();
                String updateDate2 = o2.getUpdateDate();
                try {
                    date1 = new SimpleDateFormat(AppConst.DATE_FORMAT).parse(updateDate1);
                    date2 = new SimpleDateFormat(AppConst.DATE_FORMAT).parse(updateDate2);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                LogUtils.d("SortBy Date: " + o1.getUpdateDate());
                return date2.compareTo(date1);
            }
        });
    }

    private void sortByUpdateLocation() {
        Collections.sort(houseModelList, new Comparator<HouseModel>() {
            @Override
            public int compare(HouseModel o1, HouseModel o2) {
                return o1.getAddressModel().getDistance() > o2.getAddressModel().getDistance() ? 1 : -1;
            }
        });
    }

    private void sortByUpdatePrice() {
        Collections.sort(houseModelList, new Comparator<HouseModel>() {
            @Override
            public int compare(HouseModel o1, HouseModel o2) {
                return o1.getPrice() > o2.getPrice() ? 1 : -1;
            }
        });
    }

    private void setAdapter(List<HouseModel> houseModelList, Context context) {
        if (context != null) {
            LinearLayoutManager llm = new LinearLayoutManager(getContext());
            mRcvHouse.setLayoutManager(llm);
            mHouseRcvAdapter = new HouseRcvAdapter(houseModelList, context, this);
            mRcvHouse.setAdapter(mHouseRcvAdapter);
            mHouseRcvAdapter.notifyDataSetChanged();
            pgbHouse.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.rd_recent_date, R.id.rd_location, R.id.rd_price})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.rd_recent_date:
                onClickRecentDate = true;
                onClickLocation = false;
                onClickPrice = false;
                LogUtils.d("onClickRecentDate:" + onClickRecentDate);
                initializeListHouse();
                break;
            case R.id.rd_location:
                onClickLocation = true;
                onClickRecentDate = false;
                onClickPrice = false;
                LogUtils.d("onClickLocation:" + onClickLocation);
                initializeListHouse();
                break;
            case R.id.rd_price:
                onClickPrice = true;
                onClickRecentDate = false;
                onClickLocation = false;
                LogUtils.d("onClickArea:" + onClickPrice);
                initializeListHouse();
                break;
            default:
                break;
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

    @Override
    public void onItemCLick(HouseModel houseModel) {
        LogUtils.d("onCLick...." + houseModel.getHouseId());
        LogUtils.d("onCLick 2 totalComment...." + houseModel.getCommentModelList().size());
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
