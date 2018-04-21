package net.hailm.firebaseapp.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.view.adapters.HomeVpgAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class HomeFragment extends Fragment implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener{
    @BindView(R.id.vpg_home)
    ViewPager mViewPager;
    @BindView(R.id.rd_house)
    RadioButton mRdHouse;
    @BindView(R.id.rd_place)
    RadioButton mRdPlace;
    @BindView(R.id.rd_house_place)
    RadioGroup mRdHousePlace;

    private HomeVpgAdapter mHomeAdapter;

    private View rootView;
    Unbinder unbinder;

    public HomeFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.activity_home, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        init();
        return rootView;
    }

    private void init() {
        mHomeAdapter = new HomeVpgAdapter(getFragmentManager());
        mViewPager.setAdapter(mHomeAdapter);

        mViewPager.addOnPageChangeListener(this);
        mRdHousePlace.setOnCheckedChangeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case 0:
                mRdHouse.setChecked(true);
                break;
            case 1:
                mRdPlace.setChecked(true);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rd_house:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.rd_place:
                mViewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }
}
