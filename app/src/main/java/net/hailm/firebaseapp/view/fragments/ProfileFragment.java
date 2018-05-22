package net.hailm.firebaseapp.view.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.AppConst;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.EditHouseRcvAdapterCallback;
import net.hailm.firebaseapp.listener.HouseProfileByIdListener;
import net.hailm.firebaseapp.listener.HouseProfileListener;
import net.hailm.firebaseapp.model.dbhelpers.HouseProfileDbHelper;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;
import net.hailm.firebaseapp.model.dbmodels.Users;
import net.hailm.firebaseapp.utils.DialogUtils;
import net.hailm.firebaseapp.view.adapters.HouseProfileRcvAdapter;

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
import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.DialogInterface.BUTTON_NEGATIVE;
import static android.content.DialogInterface.BUTTON_POSITIVE;

public class ProfileFragment extends Fragment implements EditHouseRcvAdapterCallback {
    @BindView(R.id.txt_total_house_by_id)
    TextView txtTotalHouseById;
    @BindView(R.id.rcv_house)
    RecyclerView mRcvHouse;
    @BindView(R.id.civ_avatar)
    CircleImageView civAvatar;
    @BindView(R.id.txt_name)
    TextView txtName;

    Unbinder unbinder;
    private View rootView;

    private HouseProfileRcvAdapter mHouseProfileRcvAdapter;
    private HouseProfileDbHelper mProfileDbHelper;
    private FirebaseAuth mAuth;
    private List<HouseModel> houseModelList;

    private String uid;
    private String email;
    private String userName;
    private String checkAcountLogin;
    private SharedPreferences mSharedPreferences;

    private FragmentManager manager;
    private FragmentTransaction transaction;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        unbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        mAuth = FirebaseAuth.getInstance();
        mProfileDbHelper = new HouseProfileDbHelper();
        // Get uid app when user login
        mSharedPreferences = getActivity().getSharedPreferences(Constants.LOCATION, Context.MODE_PRIVATE);
        uid = mSharedPreferences.getString(Constants.UID, "");
        email = mSharedPreferences.getString(Constants.EMAIL, "");
        userName = mSharedPreferences.getString(Constants.USER_NAME, "");
        checkAcountLogin = mSharedPreferences.getString(Constants.CHECK_ACOUNT_LOGIN, "");

        initDataProfile();
        initDataHouseById();
    }

    private void initDataProfile() {
        if (!uid.equals("")) {
            HouseProfileListener listener = new HouseProfileListener() {
                @Override
                public void getProfile(Users users) {
                    if (users != null) {
                        txtName.setText(users.getName());
                        setAvatarProfile(civAvatar, users.getAvatar());
                    } else {
                        String url = "https://pikmail.herokuapp.com/" + email;
                        txtName.setText(userName);
                        Glide.with(getContext()).load(url).into(civAvatar);
                    }
                }
            };

            mProfileDbHelper.getProfile(listener, uid);
        }
    }

    private void initDataHouseById() {
        houseModelList = new ArrayList<>();
        HouseProfileByIdListener listener = new HouseProfileByIdListener() {
            @Override
            public void getHouseById(HouseModel houseModel) {
                houseModelList.add(houseModel);
                txtTotalHouseById.setText(String.valueOf(houseModelList.size()));
                sortByUpdateDate();
                setAdapter(houseModelList);
            }
        };
        mProfileDbHelper.getHouseById(listener, uid);
    }

    private void setAdapter(List<HouseModel> houseModelList) {
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRcvHouse.setLayoutManager(llm);
        mHouseProfileRcvAdapter = new HouseProfileRcvAdapter(houseModelList, getActivity(), this);
        mRcvHouse.setAdapter(mHouseProfileRcvAdapter);
        mHouseProfileRcvAdapter.notifyDataSetChanged();
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

    @OnClick({R.id.btn_logout, R.id.btn_about, R.id.btn_edit_profile})
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                if (!uid.equals("")) {
                    logoutAcount();
                } else {
                    Toast.makeText(getContext(), "Bạn chưa đăng nhập mà", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_about:
                Toast.makeText(getContext(), "Hướng dẫn, bấm vào nhà trọ cần sửa, giữ lâu để xóa nhà trọ", Toast.LENGTH_LONG).show();
                break;
            case R.id.btn_edit_profile:
                if (checkAcountLogin.equals("1")) {
                    goProfileEditFragment();
                } else {
                    Toast.makeText(getContext(), "Tài khoản bạn đăng nhập không thể sửa", Toast.LENGTH_SHORT).show();
                }

                break;
            default:
                break;
        }
    }

    private void goProfileEditFragment() {
        ProfileEditFrament profileEditFrament = new ProfileEditFrament();
        manager = getActivity().getSupportFragmentManager();
        transaction = manager.beginTransaction();
        transaction.replace(R.id.frame_container, profileEditFrament);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private void logoutAcount() {
        DialogUtils.showAlertDialog(getContext(), getString(R.string.ban_co_muon_dang_xuat), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case BUTTON_POSITIVE:
                        dialog.dismiss();
                        break;
                    case BUTTON_NEGATIVE:
                        mAuth.signOut();
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
//                        editor.clear();  Là xoa het data trong SharedPrefernes
                        editor.remove(Constants.UID);
                        editor.remove(Constants.USER_NAME);
                        editor.remove(Constants.EMAIL);
                        editor.remove(Constants.CHECK_ACOUNT_LOGIN);
                        editor.commit();
                        break;
                    default:
                        break;
                }
            }
        });
    }

    /**
     * set avatar comment
     *
     * @param imageView
     * @param url
     */
    private void setAvatarProfile(final CircleImageView imageView, String url) {
        StorageReference mStorageAvatar = FirebaseStorage.getInstance().getReference()
                .child(Constants.MEMBERS)
                .child(url);
        LogUtils.d("Url img comment: " + mStorageAvatar);
        Glide.with(getActivity()).using(new FirebaseImageLoader()).load(mStorageAvatar).into(imageView);

    }

    @Override
    public void onItemCLick(HouseModel houseModel) {
        EditHouseFragment editHouseFragment = new EditHouseFragment();
        manager = getActivity().getSupportFragmentManager();
        transaction = manager.beginTransaction();

        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.HOUSE_MODEL_EDIT, houseModel);
        editHouseFragment.setArguments(bundle);

        transaction.replace(R.id.frame_container, editHouseFragment);
//        transaction.replace(android.R.id.content, editHouseFragment);
        transaction.addToBackStack(null);
        transaction.commit();
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
