package net.hailm.firebaseapp.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.blankj.utilcode.util.LogUtils;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.HouseListener;
import net.hailm.firebaseapp.model.dbhelpers.HouseDbHelper;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;
import net.hailm.firebaseapp.view.adapters.HouseRcvAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by hai.lm on 13/04/2018.
 */

public class HouseFragment extends Fragment {
    @BindView(R.id.rcv_house)
    RecyclerView mRcvHouse;
    @BindView(R.id.pgb_house)
    ProgressBar pgbHouse;

    Unbinder unbinder;
    private View rootView;
    private HouseDbHelper mDbHelper;
    private HouseRcvAdapter mHouseRcvAdapter;
    private List<HouseModel> houseModelList;

    private SharedPreferences mSharedPreferences;

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
        initialize();
    }

    private void initialize() {
        mDbHelper = new HouseDbHelper();

        mSharedPreferences = getContext().getSharedPreferences(Constants.LOCATION, Context.MODE_PRIVATE);

        LogUtils.d("LOCATION: "
                + mSharedPreferences.getString(Constants.LATITUDE, "0")
                + ", " + mSharedPreferences.getString(Constants.LONGITUDE, "0"));
        Location currentLocation = new Location("");

        currentLocation.setLatitude(Double.parseDouble(mSharedPreferences.getString(Constants.LATITUDE, "0")));
        currentLocation.setLongitude(Double.parseDouble(mSharedPreferences.getString(Constants.LONGITUDE, "0")));

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRcvHouse.setLayoutManager(llm);

        mRcvHouse.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LogUtils.d("Kiem tra: " + "ddddd");
            }
        });

        houseModelList = new ArrayList<>();
        HouseListener listener = new HouseListener() {
            @Override
            public void getListHouseModel(HouseModel houseModel) {
                LogUtils.d("Tel: " + houseModel.getTel());
                houseModelList.add(houseModel);

                Collections.sort(houseModelList, new Comparator<HouseModel>() {
                    @Override
                    public int compare(HouseModel o1, HouseModel o2) {
                        return o1.getAddressModel().getDistance() > o2.getAddressModel().getDistance() ? 1 : -1;
                    }
                });
                mHouseRcvAdapter = new HouseRcvAdapter(houseModelList, getContext());
                mRcvHouse.setAdapter(mHouseRcvAdapter);
                mHouseRcvAdapter.notifyDataSetChanged();
                pgbHouse.setVisibility(View.GONE);

                LogUtils.d("houseModelList" + houseModelList);


            }
        };

        mDbHelper.getListHouse(listener, currentLocation, 3, 0);
        test();
    }

    private void test() {
        List<HouseModel> list = new ArrayList<>();
        list.addAll(houseModelList);
        Collections.sort(list, new Comparator<HouseModel>() {
            @Override
            public int compare(HouseModel o1, HouseModel o2) {
                return 0;
            }
        });

        LogUtils.d("ListTest..." + list);
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

    @OnClick(R.id.btn_near_by)
    public void submit(View view) {
        switch (view.getId()) {
            case R.id.btn_near_by:
                test();
                break;
        }
    }
}
