package net.hailm.firebaseapp.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.AppConst;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.PopupImageCallback;
import net.hailm.firebaseapp.listener.RegisterHouseListener;
import net.hailm.firebaseapp.model.dbhelpers.RegisterHouseDbHelper;
import net.hailm.firebaseapp.model.dbmodels.AddressModel;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;
import net.hailm.firebaseapp.utils.DialogUtils;
import net.hailm.firebaseapp.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class EditHouseFragment extends Fragment implements OnMapReadyCallback, PopupImageCallback {
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

    @BindView(R.id.btn_delete_image1)
    ImageView btnDeleteImage1;
    @BindView(R.id.btn_delete_image2)
    ImageView btnDeleteImage2;
    @BindView(R.id.btn_delete_image3)
    ImageView btnDeleteImage3;
    @BindView(R.id.btn_delete_image4)
    ImageView btnDeleteImage4;
    @BindView(R.id.btn_delete_image5)
    ImageView btnDeleteImage5;
    @BindView(R.id.btn_delete_image6)
    ImageView btnDeleteImage6;


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
    private RegisterHouseDbHelper mRegisterHouseDbHelper;
    private GoogleMap mGoogleMap;
    private SupportMapFragment mSupportMapFragment;

    private FragmentManager manager;
    private FragmentTransaction transaction;
    private int checkClickImage = 0;
    private List<Bitmap> bitmapList;
    private List<String> nameImageList;
    private StorageReference storageReference;

    // Lấy myLocation
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double myLatitude;
    private double myLongtitude;

    private double currentLongitude;
    private double currentLatidude;
    private Marker myMarker;

    private boolean onCLickBtnLocation = false;
    private List<ImageView> listImage;
    private List<ImageView> listBtnDeleteImage;

    private String houseId;

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
        mRegisterHouseDbHelper = new RegisterHouseDbHelper(getActivity());
        storageReference = FirebaseStorage.getInstance().getReference();
        bitmapList = new ArrayList<>();
        nameImageList = new ArrayList<>();
        houseId = houseModel.getHouseId();
        currentLatidude = houseModel.getAddressModel().getLatitude();
        currentLongitude = houseModel.getAddressModel().getLongitude();

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

        listImage = new ArrayList<>();
        listImage.add(img1);
        listImage.add(img2);
        listImage.add(img3);
        listImage.add(img4);
        listImage.add(img5);
        listImage.add(img6);

        listBtnDeleteImage = new ArrayList<>();
        listBtnDeleteImage.add(btnDeleteImage1);
        listBtnDeleteImage.add(btnDeleteImage2);
        listBtnDeleteImage.add(btnDeleteImage3);
        listBtnDeleteImage.add(btnDeleteImage4);
        listBtnDeleteImage.add(btnDeleteImage5);
        listBtnDeleteImage.add(btnDeleteImage6);

        if (houseModel.getHouseImages().size() > 0) {
            for (int i = 0; i < houseModel.getHouseImages().size(); i++) {
                listBtnDeleteImage.get(i).setVisibility(View.VISIBLE);
                StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                        .child(Constants.IMAGES)
                        .child(houseModel.getHouseImages().get(i));
                Glide.with(getContext()).using(new FirebaseImageLoader()).load(storageReference).into(listImage.get(i));
            }
        }
    }

    @OnClick({R.id.btn_register_house, R.id.btn_location,
            R.id.img_1, R.id.img_2, R.id.img_3, R.id.img_4, R.id.img_5, R.id.img_6,
            R.id.btn_delete_image1, R.id.btn_delete_image2, R.id.btn_delete_image3, R.id.btn_delete_image4,
            R.id.btn_delete_image5, R.id.btn_delete_image6})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_register_house:
                registerHouse();
                break;
            case R.id.btn_location:
                onCLickBtnLocation = true;
                getMyLocation();
                break;
            case R.id.btn_delete_image1:
                deleteImage(0);
                break;
            case R.id.btn_delete_image2:
                deleteImage(1);
                break;
            case R.id.btn_delete_image3:
                deleteImage(2);
                break;
            case R.id.btn_delete_image4:
                deleteImage(3);
                break;
            case R.id.btn_delete_image5:
                deleteImage(4);
                break;
            case R.id.btn_delete_image6:
                deleteImage(5);
                break;
            case R.id.img_1:
                checkClickImage = 1;
                showPopupImage();
                break;
            case R.id.img_2:
                checkClickImage = 2;
                showPopupImage();
                break;
            case R.id.img_3:
                checkClickImage = 3;
                showPopupImage();
                break;
            case R.id.img_4:
                checkClickImage = 4;
                showPopupImage();
                break;
            case R.id.img_5:
                checkClickImage = 5;
                showPopupImage();
                break;
            case R.id.img_6:
                checkClickImage = 6;
                showPopupImage();
                break;
            default:
                break;
        }
    }

    private void showPopupImage() {
        manager = getActivity().getSupportFragmentManager();
        PopupImageFragment popupImageFragment = new PopupImageFragment();
        popupImageFragment.setTargetFragment(this, 1000);
        popupImageFragment.show(manager, "dialog");
    }

    private void registerHouse() {
        if (checkInputData()) {
            HouseModel houseModel = createHouseModel();

            uploadImages();

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

            mRegisterHouseDbHelper.registerHouseImage(nameImageList, houseModel.getHouseId());

            DialogUtils.showProgressDialog("Update house loading ...", getActivity());
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    DialogUtils.hideProgressDialog();
                    goHomeFragment();
                }
            }, 1500);

        } else {
            Toast.makeText(getActivity(), "Update house fail.......", Toast.LENGTH_SHORT).show();
        }
    }

    private void goHomeFragment() {
        HomeFragment homeFragment = new HomeFragment();
        manager = getActivity().getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_container, homeFragment);
        transaction.commit();
    }

    private HouseModel createHouseModel() {
        HouseModel houseModel = new HouseModel();
        houseModel.setHouseId(houseId);

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat(AppConst.DATE_FORMAT);
        houseModel.setUpdateDate(dateFormat.format(date));

        houseModel.setUid(uid);

        String lamdlord = edtLamdlord.getText().toString().trim();
        houseModel.setLandlord(lamdlord);

        long price = Long.parseLong(edtPrice.getText().toString().trim());
        houseModel.setPrice(price);

        long acreage = Long.parseLong(edtAcreage.getText().toString().trim());
        houseModel.setAcreage(acreage);

        String tel = edtTel.getText().toString().trim();
        houseModel.setTel(tel);

        String contents = edtContents.getText().toString().trim();
        houseModel.setContents(contents);

        long quantity = Long.parseLong(edtQuantity.getText().toString().trim());
        houseModel.setQuantity(quantity);

        List<String> stringList = new ArrayList<>();

        if (cbWifi.isChecked()) {
            stringList.add(getString(R.string.MaTienIch1));
        }
        if (cbMayLanh.isChecked()) {
            stringList.add(getString(R.string.MaTienIch2));
        }
        if (cbBanGhe.isChecked()) {
            stringList.add(getString(R.string.MaTienIch3));
        }
        if (cbGuiXe.isChecked()) {
            stringList.add(getString(R.string.MaTienIch4));
        }
        if (cbNongLanh.isChecked()) {
            stringList.add(getString(R.string.MaTienIch5));
        }
        if (cbGiuong.isChecked()) {
            stringList.add(getString(R.string.MaTienIch6));
        }
        houseModel.setUtility(stringList);
        return houseModel;
    }

    private AddressModel createAddress() {
        AddressModel addressModel = new AddressModel();

        if (onCLickBtnLocation) {
            addressModel.setLatitude(myLatitude);
            addressModel.setLongitude(myLongtitude);
        } else {

            addressModel.setLatitude(currentLatidude);
            addressModel.setLongitude(currentLongitude);
        }


        String address = edtAddress.getText().toString().trim();
        addressModel.setAddress(address);
        return addressModel;
    }

    private void deleteImage(final int i) {
        DialogUtils.showAlertDialog(getContext(), getString(R.string.xoa_hinh_anh), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case BUTTON_POSITIVE:
                        dialog.dismiss();
                        break;
                    case BUTTON_NEGATIVE:
                        // Neu thic xoa anh trong storage thi mo no len

//                        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
//                                .child(Constants.IMAGES)
//                                .child(houseModel.getHouseImages().get(i));
//                        storageReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Toast.makeText(getActivity(), "Xoa thanh cong", Toast.LENGTH_SHORT).show();
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//
//                            }
//                        });

                        // 4 ở đây là do duoi .jpg, .png
                        String s = houseModel.getHouseImages().get(i);
                        for (int j = 0; j < s.length(); j++) {
                            char c = s.charAt(j);
                            String value = String.valueOf(c);
                            if (value.equals(".")) {
                                s = s.substring(0, s.length() - 4);
                            }
                        }

                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
                                .child(Constants.HOUSE_IMAGES)
                                .child(houseModel.getHouseId())
                                .child(s);
                        databaseReference.setValue(null);
                        listImage.get(i).setImageResource(R.drawable.ic_camara);
                        Toast.makeText(getContext(), "Xóa thành công ảnh nhà trọ", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private boolean checkInputData() {
        if (Utils.isEmpty(edtLamdlord) && Utils.isEmpty(edtAddress)
                && Utils.isEmpty(edtPrice) && Utils.isEmpty(edtAcreage)
                && Utils.isEmpty(edtQuantity)
                && Utils.isEmpty(edtTel) && Utils.isEmpty(edtContents)) {
            String tel = edtTel.getText().toString().trim();
            if (!Utils.isPhoneNumber(tel)) {
                edtTel.requestFocus();
                edtTel.setError(getString(R.string.khong_phai_so_dien_thoai));
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
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

    private void getMyLocation() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        try {
            Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
            locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    LogUtils.d("myLatitude register: " + myLatitude);
                    LogUtils.d("myLongtitude register: " + myLongtitude);

                    myLatitude = location.getLatitude();
                    myLongtitude = location.getLongitude();

                    showMyMarker();
                    showMyLocation();
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void showMyLocation(double latitude, double longitude, GoogleMap googleMap) {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(latitude, longitude))
                .zoom(15)
                .build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void showMyMarker() {
        if (myMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(myLatitude, myLongtitude));
            mGoogleMap.addMarker(markerOptions);
        }
    }

    private void showMyLocation() {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(myLatitude, myLongtitude))
                .zoom(15)
                .build();
        mGoogleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onButtonClick(Bitmap bitmap) {
        switch (checkClickImage) {
            case 1:
                img1.setImageBitmap(bitmap);
                bitmapList.add(bitmap);
                break;
            case 2:
                img2.setImageBitmap(bitmap);
                bitmapList.add(bitmap);
                break;
            case 3:
                img3.setImageBitmap(bitmap);
                bitmapList.add(bitmap);
                break;
            case 4:
                img4.setImageBitmap(bitmap);
                bitmapList.add(bitmap);
                break;
            case 5:
                img5.setImageBitmap(bitmap);
                bitmapList.add(bitmap);
                break;
            case 6:
                img6.setImageBitmap(bitmap);
                bitmapList.add(bitmap);
                break;
            default:
                break;
        }
    }

    private void uploadImages() {
        img1.setDrawingCacheEnabled(true);
        img1.buildDrawingCache();
        img2.setDrawingCacheEnabled(true);
        img2.buildDrawingCache();
        img3.setDrawingCacheEnabled(true);
        img3.buildDrawingCache();
        img4.setDrawingCacheEnabled(true);
        img4.buildDrawingCache();
        img5.setDrawingCacheEnabled(true);
        img5.buildDrawingCache();
        img6.setDrawingCacheEnabled(true);
        img6.buildDrawingCache();

        for (int i = 0; i < bitmapList.size(); i++) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapList.get(i).compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            String nameImage = UUID.randomUUID().toString();
            nameImageList.add(nameImage);
            StorageReference storageRef = this.storageReference.child(Constants.IMAGES).child(nameImage);
            UploadTask uploadTask = storageRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    LogUtils.d("downloadUrl: " + downloadUrl);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    LogUtils.d("Upload image faliure");
                }
            });
        }
    }
}
