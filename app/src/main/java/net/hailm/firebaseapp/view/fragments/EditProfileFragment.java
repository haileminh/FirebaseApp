package net.hailm.firebaseapp.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.AppConst;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.HouseProfileListener;
import net.hailm.firebaseapp.listener.PopupImageCallback;
import net.hailm.firebaseapp.listener.RegisterHouseListener;
import net.hailm.firebaseapp.model.dbhelpers.HouseProfileDbHelper;
import net.hailm.firebaseapp.model.dbmodels.Users;
import net.hailm.firebaseapp.utils.DateUtils;
import net.hailm.firebaseapp.utils.DialogUtils;
import net.hailm.firebaseapp.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class EditProfileFragment extends Fragment implements PopupImageCallback {
    Unbinder unbinder;
    private View rootView;
    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.txt_birthDay)
    TextView txtBirthDay;
    @BindView(R.id.rd_male)
    RadioButton rdMale;
    @BindView(R.id.rd_famale)
    RadioButton rdFamale;
    @BindView(R.id.edt_address)
    EditText edtAddress;
    @BindView(R.id.civ_avatar)
    CircleImageView civAvatar;
    @BindView(R.id.txt_email)
    TextView txtEmail;

    private SwitchDateTimeDialogFragment dateTimeFragment;

    private String uid;
    private String nameImage;
    private SharedPreferences mSharedPreferences;
    private HouseProfileDbHelper mProfileDbHelper;
    private StorageReference storageReference;
    private HouseProfileDbHelper mDbHelper;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private List<Bitmap> bitmapList;

    private int checkClickImage = 0;
    private boolean checkIsImage = false;
    private boolean checkSetImage = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeComponents();
        initDateTime();
        initDataEditProfile();
    }

    private void initializeComponents() {
        mProfileDbHelper = new HouseProfileDbHelper();
        storageReference = FirebaseStorage.getInstance().getReference();
        mDbHelper = new HouseProfileDbHelper();
        bitmapList = new ArrayList<>();
        mSharedPreferences = getActivity().getSharedPreferences(Constants.LOCATION, Context.MODE_PRIVATE);
        uid = mSharedPreferences.getString(Constants.UID, "");
    }

    private void initDateTime() {
        dateTimeFragment = (SwitchDateTimeDialogFragment) getChildFragmentManager().findFragmentByTag(AppConst.DATE_DEFAULT);
        if (dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.birthday),
                    getString(R.string.ok),
                    getString(R.string.cancel),
                    getString(R.string.clean)
            );
        }
        dateTimeFragment.set24HoursMode(false);
        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(1970, Calendar.JANUARY, 1).getTime());
        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(2030, Calendar.DECEMBER, 31).getTime());

        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat(AppConst.DATE_DEFAULT, Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            LogUtils.e(AppConst.TAG, e.getMessage());
        }
    }

    private void initDataEditProfile() {
        if (!uid.equals("")) {
            HouseProfileListener listener = new HouseProfileListener() {
                @Override
                public void getProfile(Users users) {
                    if (users != null) {
                        edtName.setText(users.getName());
                        txtEmail.setText(users.getEmail());
                        nameImage = users.getAvatar();
                        edtAddress.setText(users.getAddress());

                        if (users.getBirthDay() == null) {
                            txtBirthDay.setText("Ngày sinh: ");
                        } else {
                            txtBirthDay.setText(users.getBirthDay());
                        }

                        if (users.getSex() == null) {

                        } else {
                            if (users.getSex().equals("Nam")) {
                                rdMale.setChecked(true);
                            } else if (users.getSex().equals("Nữ")) {
                                rdFamale.setChecked(true);
                            }
                        }


                        setAvatarProfile(civAvatar, users.getAvatar());
                    }
                }
            };
            mProfileDbHelper.getProfile(listener, uid);
        }
    }

    /**
     * set avatar profile
     *
     * @param imageView
     * @param url
     */
    private void setAvatarProfile(CircleImageView imageView, String url) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference()
                .child(Constants.MEMBERS)
                .child(url);
        Glide.with(getActivity()).using(new FirebaseImageLoader()).load(storageReference).into(imageView);
    }

    @OnClick({R.id.civ_avatar, R.id.btn_update, R.id.btn_delete_image, R.id.btn_back_profile, R.id.txt_birthDay})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.civ_avatar:
                checkClickImage = 1;
                if (checkIsImage) {
                    showPopupImage();
                }
                break;
            case R.id.btn_delete_image:
                deleteProfile();
                break;
            case R.id.btn_update:
                updateProfile();
                break;
            case R.id.btn_back_profile:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.txt_birthDay:
                if (!dateTimeFragment.isAdded()) {
                    dateTimeFragment.show(getChildFragmentManager(), AppConst.DATE_DEFAULT);
                    dateTimeFragment.setOnButtonClickListener(new DateTimePickerFragment(txtBirthDay,
                            new DateTimePickerFragment.OnClickPositiveButtonListener() {
                                @Override
                                public void onClickPositiveButton(Date date) {
                                    String dateTime = getString(R.string.ngay) + " " + DateUtils.getDay(date) + " "
                                            + getString(R.string.thang) + " " + DateUtils.getMonth(date) + " "
                                            + getString(R.string.nam) + " " + DateUtils.getYear(date);
                                    txtBirthDay.setText(dateTime);
                                }
                            }));
                }
                break;
            default:
                break;
        }
    }

    private void updateProfile() {
        if (checkInputData()) {
            uploadImages();
            Users users = createUsers();
            mDbHelper.registerProfile(users, new RegisterHouseListener() {
                @Override
                public void registerSuccess() {
                    LogUtils.d("Update profile success");
                    Toast.makeText(getContext(), "Update profile success", Toast.LENGTH_SHORT).show();
                    goProfileFragment();
                }

                @Override
                public void registerFailure(String message) {
                    LogUtils.d("Register  house Failure");
                    Toast.makeText(getContext(), "Update profile Failure", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void goProfileFragment() {
        ProfileFragment profileFragment = new ProfileFragment();
        manager = getActivity().getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_container, profileFragment);
        transaction.commit();
    }

    private void uploadImages() {
        civAvatar.setDrawingCacheEnabled(true);
        civAvatar.buildDrawingCache();

        for (int i = 0; i < bitmapList.size(); i++) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapList.get(i).compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();

            nameImage = UUID.randomUUID().toString();
            StorageReference storageRef = storageReference.child(Constants.MEMBERS).child(nameImage);

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

    private Users createUsers() {
        Users users = new Users();
        users.setEmail(txtEmail.getText().toString());
        users.setUid(uid);
        users.setName(edtName.getText().toString().trim());
        users.setAvatar(nameImage);
        users.setBirthDay(txtBirthDay.getText().toString());
        if (rdMale.isChecked()) {
            users.setSex("Nam");
        } else {
            users.setSex("Nữ");
        }
        users.setAddress(edtAddress.getText().toString());
        return users;
    }

    private boolean checkInputData() {
        if (Utils.isEmpty(edtName)) {
            if (!checkSetImage) {
                Toast.makeText(getContext(), "Bạn chưa nhập ảnh", Toast.LENGTH_SHORT).show();
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    private void showPopupImage() {
        manager = getActivity().getSupportFragmentManager();
        PopupImageFragment popupImageFragment = new PopupImageFragment();
        popupImageFragment.setTargetFragment(this, 1000);
        popupImageFragment.show(manager, "dialog");
    }

    private void deleteProfile() {
        DialogUtils.showAlertDialog(getContext(), getString(R.string.xoa_hinh_anh), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case BUTTON_POSITIVE:
                        dialog.dismiss();
                        break;
                    case BUTTON_NEGATIVE:
                        checkIsImage = true;
                        checkSetImage = false;
//                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference()
//                                .child(Constants.USERS)
//                                .child(uid)
//                                .child("avatar");
//                        databaseReference.setValue(null);
                        civAvatar.setImageResource(R.drawable.ic_camara);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    @Override
    public void onButtonClick(Bitmap bitmap) {
        switch (checkClickImage) {
            case 1:
                checkSetImage = true;
                civAvatar.setImageBitmap(bitmap);
                bitmapList.add(bitmap);
                break;
            default:
                break;
        }
    }
}
