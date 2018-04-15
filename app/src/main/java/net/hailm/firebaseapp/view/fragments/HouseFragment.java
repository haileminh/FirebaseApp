package net.hailm.firebaseapp.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.Utils;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.listener.HouseListener;
import net.hailm.firebaseapp.model.dbhelpers.HouseDbHelper;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;
import net.hailm.firebaseapp.view.adapters.HouseRcvAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by hai.lm on 13/04/2018.
 */

public class HouseFragment extends Fragment {
    @BindView(R.id.rcv_house)
    RecyclerView mRcvHouse;

    Unbinder unbinder;
    private View rootView;
    private HouseDbHelper mDbHelper;
    private HouseRcvAdapter mHouseRcvAdapter;
    private List<HouseModel> houseModelList;

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
        houseModelList = new ArrayList<>();

//        houseModelList = new ArrayList<>();
//        houseModelList.add(new HouseModel("HaiLeMinh", 12));
//        LogUtils.d("Test: " + houseModelList);

        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRcvHouse.setLayoutManager(llm);

        HouseListener listener = new HouseListener() {
            @Override
            public void getListHouseModel(HouseModel houseModel) {
                LogUtils.d("Kiem tra: " + houseModel.getTel());
                houseModelList.add(houseModel);
                mHouseRcvAdapter.notifyDataSetChanged();
            }
        };
        mDbHelper.getListHostuse(listener);

        mHouseRcvAdapter = new HouseRcvAdapter(houseModelList, getContext());
        mRcvHouse.setAdapter(mHouseRcvAdapter);

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
}
