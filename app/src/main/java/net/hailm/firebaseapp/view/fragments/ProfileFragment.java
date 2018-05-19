package net.hailm.firebaseapp.view.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;
import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.HouseProfileByIdListener;
import net.hailm.firebaseapp.listener.HouseProfileListener;
import net.hailm.firebaseapp.model.dbhelpers.HouseProfileDbHelper;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;
import net.hailm.firebaseapp.model.dbmodels.Users;
import net.hailm.firebaseapp.view.adapters.HouseProfileRcvAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
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
    private SharedPreferences mSharedPreferences;

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
                setAdapter(houseModelList);
            }
        };
        mProfileDbHelper.getHouseById(listener, uid);
    }

    private void setAdapter(List<HouseModel> houseModelList) {
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        mRcvHouse.setLayoutManager(llm);
        mHouseProfileRcvAdapter = new HouseProfileRcvAdapter(houseModelList, getActivity());
        mRcvHouse.setAdapter(mHouseProfileRcvAdapter);
        mHouseProfileRcvAdapter.notifyDataSetChanged();
    }

    @OnClick(R.id.btn_logout)
    public void onViewClicked(View v) {
        switch (v.getId()) {
            case R.id.btn_logout:
                mAuth.signOut();
                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.clear();
                editor.commit();
                break;
            default:
                break;
        }
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
        Glide.with(getContext()).using(new FirebaseImageLoader()).load(mStorageAvatar).into(imageView);

    }
}
