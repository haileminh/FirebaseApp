package net.hailm.firebaseapp.view.activities;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.base.BaseActivity;
import net.hailm.firebaseapp.view.fragments.AddHouseFragment;
import net.hailm.firebaseapp.view.fragments.HomeFragment;
import net.hailm.firebaseapp.view.fragments.PlaceFragment;
import net.hailm.firebaseapp.view.fragments.ProfileFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity {
    @BindView(R.id.bottom_navigation)
    BottomNavigationViewEx mBottomNav;
    FragmentTransaction transaction;

    boolean checkOnClick = false;
    boolean checkOnClickProfile = false;
    private boolean doubleBackToExitPressedOnce = false;

    private long lastPressedTime;
    private static final int PERIOD = 2000;

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
                        checkOnClickProfile = false;
                        break;
                    case R.id.item_search:
                        transaction.replace(R.id.frame_container, new PlaceFragment());
                        checkOnClick = false;
                        checkOnClickProfile = false;
                        break;
                    case R.id.item_add:
                        transaction.replace(R.id.frame_container, new AddHouseFragment());
                        checkOnClick = false;
                        checkOnClickProfile = false;
                        break;
                    case R.id.item_setting:
                        if (!checkOnClickProfile) {
                            transaction.replace(R.id.frame_container, new ProfileFragment());
                            checkOnClickProfile = true;
                        }
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

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();

        if (count == 0) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                //additional code
                return;
            }
            doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            getFragmentManager().popBackStack();
        }
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
//            switch (event.getAction()) {
//                case KeyEvent.ACTION_DOWN:
//                    if (event.getDownTime() - lastPressedTime < PERIOD) {
//                        // todo your
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Press once again to exit",
//                                Toast.LENGTH_SHORT).show();
//                        lastPressedTime = event.getEventTime();
//                    }
//                    return true;
//            }
//        }
//        return false;
//    }
}
