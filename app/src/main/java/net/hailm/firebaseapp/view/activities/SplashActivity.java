package net.hailm.firebaseapp.view.activities;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.base.BaseActivity;
import net.hailm.firebaseapp.define.Constants;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends BaseActivity implements Animation.AnimationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final int REQUEST_CODE_PERMISSION = 1000;
    @BindView(R.id.ll_splash)
    RelativeLayout llSplash;
    @BindView(R.id.img_logo)
    ImageView imgLogo;
    @BindView(R.id.txt_version)
    TextView txtVersion;
    private boolean checkChangeActivity = false;

    private GoogleApiClient mGoogleApiClient;
    // Lấy myLocation
    private FusedLocationProviderClient mFusedLocationProviderClient;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        init();
    }

    private void init() {
        ButterKnife.bind(this);
        mSharedPreferences = getSharedPreferences(Constants.LOCATION, MODE_PRIVATE);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        // Check permission

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
            boolean granted = checkPermissionGranted();
            if (granted) {
                mGoogleApiClient.connect();
                checkChangeActivity = true;
                setAnimSplash();
            } else {
                requestPermission();
            }
        } else {
            if (!isOpenGPS()) {
                showDialog1(getString(R.string.open_gps), SplashActivity.this);
                Toast.makeText(this, getString(R.string.open_gps), Toast.LENGTH_SHORT).show();
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            } else {
                setAnimSplash();
            }
        }


        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), 0);
            txtVersion.setText(getString(R.string.phien_ban) + " " + info.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void setAnimSplash() {
        Animation alphaAnim = AnimationUtils.loadAnimation(this, R.anim.alpla_background);
        llSplash.setAnimation(alphaAnim);
        Animation transitionAnim = AnimationUtils.loadAnimation(this, R.anim.transition_icon);
        imgLogo.setAnimation(transitionAnim);
        alphaAnim.setAnimationListener(this);
    }

    private boolean checkPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (isPermissionGranted(Manifest.permission.ACCESS_COARSE_LOCATION)
                    && isPermissionGranted(Manifest.permission.ACCESS_FINE_LOCATION)) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private boolean isPermissionGranted(String permission) {
        return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.ACCESS_FINE_LOCATION
                },
                REQUEST_CODE_PERMISSION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    mGoogleApiClient.connect();
                } else {
                    showDialog1(getResources().getString(R.string.open_gps), this);
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getMyLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    @Override
    protected void onDestroy() {
        mGoogleApiClient.disconnect();
        super.onDestroy();
    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {
        delay3s();
    }

    private void delay3s() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }
        }, 3000);
    }


    @Override
    public void onAnimationRepeat(Animation animation) {

    }

    public void getMyLocation() {
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            Task<Location> locationTask = mFusedLocationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LogUtils.d("Latitude: " + location.getLatitude());
                    LogUtils.d("Longitude: " + location.getLongitude());
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(Constants.LATITUDE, String.valueOf(location.getLatitude()));
                    editor.putString(Constants.LONGITUDE, String.valueOf(location.getLongitude()));
                    editor.commit();
                    if (!checkChangeActivity) {
                        delay3s();
                    }
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private boolean isOpenGPS() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        return locationManager != null
                && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
