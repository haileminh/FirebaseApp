package net.hailm.firebaseapp.model.dbhelpers;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import net.hailm.firebaseapp.base.BaseFireBase;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.HouseProfileByIdListener;
import net.hailm.firebaseapp.listener.HouseProfileListener;
import net.hailm.firebaseapp.listener.RegisterHouseListener;
import net.hailm.firebaseapp.model.dbmodels.AddressModel;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;
import net.hailm.firebaseapp.model.dbmodels.Users;

import java.util.ArrayList;
import java.util.List;

public class HouseProfileDbHelper extends BaseFireBase {
    private DatabaseReference mDataNodeRoot;

    public HouseProfileDbHelper() {
        mDataNodeRoot = getDatabaseReference();
    }

    /**
     * getProfile
     *
     * @param listener
     * @param uid
     */
    public void getProfile(final HouseProfileListener listener, final String uid) {
        mDataNodeRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.child(Constants.USERS).child(uid).getValue(Users.class);
                listener.getProfile(users);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * get profile by id
     *
     * @param listener
     * @param uid
     */
    public void getHouseById(final HouseProfileByIdListener listener, final String uid) {
        mDataNodeRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot dataHouse = dataSnapshot.child(Constants.HOUSES);
                for (DataSnapshot values : dataHouse.getChildren()) {
                    HouseModel houseModel = values.getValue(HouseModel.class);
                    if (houseModel.getUid().equals(uid)) {
                        houseModel.setHouseId(values.getKey());

                        // Get list images by ID
                        DataSnapshot dataImage = dataSnapshot.child(Constants.HOUSE_IMAGES).child(values.getKey());
                        List<String> houseImages = new ArrayList<>();
                        for (DataSnapshot valueImages : dataImage.getChildren()) {
                            houseImages.add(valueImages.getValue(String.class));
                        }
                        houseModel.setHouseImages(houseImages);

                        // GetAddress
                        DataSnapshot dataAddress = dataSnapshot.child(Constants.ADDRESS).child(houseModel.getHouseId());
                        AddressModel addressModel = dataAddress.getValue(AddressModel.class);
                        houseModel.setAddressModel(addressModel);

                        listener.getHouseById(houseModel);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    /**
     * update profile
     *
     * @param users
     * @param listener
     */
    public void registerProfile(Users users, final RegisterHouseListener listener) {
        mDataNodeRoot.child(Constants.USERS)
                .child(users.getUid())
                .setValue(users)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.registerSuccess();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.registerFailure(e.getMessage());
            }
        });
    }
}
