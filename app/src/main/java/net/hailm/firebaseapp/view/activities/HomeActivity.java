package net.hailm.firebaseapp.view.activities;

import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.base.BaseActivity;
import net.hailm.firebaseapp.view.adapters.HomeVpgAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends BaseActivity implements ViewPager.OnPageChangeListener, RadioGroup.OnCheckedChangeListener {
    @BindView(R.id.vpg_home)
    ViewPager mViewPager;
    @BindView(R.id.rd_house)
    RadioButton mRdHouse;
    @BindView(R.id.rd_place)
    RadioButton mRdPlace;
    @BindView(R.id.rd_house_place)
    RadioGroup mRdHousePlace;

    private HomeVpgAdapter mHomeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initializeComponents();
    }

    private void initializeComponents() {
        ButterKnife.bind(this);
        mHomeAdapter = new HomeVpgAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(mHomeAdapter);

        mViewPager.addOnPageChangeListener(this);
        mRdHousePlace.setOnCheckedChangeListener(this);
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
