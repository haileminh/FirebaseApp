package net.hailm.firebaseapp.view.activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.blankj.utilcode.util.LogUtils;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.base.BaseActivity;
import net.hailm.firebaseapp.view.fragments.HomeFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @BindView(R.id.bottom_navigation)
    BottomNavigationViewEx mBottomNav;
    FragmentTransaction transaction;

    boolean checkOnClick = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initializeComponents();
    }

    private void initializeComponents() {
        mBottomNav.enableShiftingMode(true);
        mBottomNav.enableAnimation(false);
        transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, new HomeFragment());
        transaction.commit();

        mBottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                transaction = getSupportFragmentManager().beginTransaction();
                switch (item.getItemId()) {
                    case R.id.item_home:
                        if (!checkOnClick) {
                            transaction.replace(R.id.frame_container, new HomeFragment());
                            checkOnClick = true;
                        }
                        LogUtils.d("checkOnclick: " + checkOnClick);
                        break;
                    case R.id.item_setting:
                        checkOnClick = false;
                        break;
                    default:
                        break;
                }
                transaction.commit();
                return true;
            }
        });
    }
}
