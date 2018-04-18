package net.hailm.firebaseapp.model.dbhelpers;

import android.location.Address;
import android.location.Location;

import com.blankj.utilcode.util.LogUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import net.hailm.firebaseapp.base.BaseFireBase;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.HouseListener;
import net.hailm.firebaseapp.model.dbmodels.AddressModel;
import net.hailm.firebaseapp.model.dbmodels.CommentModel;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;
import net.hailm.firebaseapp.model.dbmodels.Users;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hai.lm on 14/04/2018.
 */

public class HouseDbHelper extends BaseFireBase {
    private DatabaseReference mDataNodeRoot;

    public HouseDbHelper() {
        mDataNodeRoot = getDatabaseReference();
    }

    private DataSnapshot dataRoot;

    /**
     * Get list house fill recycleView
     *
     * @param houseListener
     * @param currentLocation
     */
    public void getListHouse(final HouseListener houseListener, final Location currentLocation,
                             final int nextItem, final int loadedItem) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                dataRoot = dataSnapshot;
                getListHouse(dataSnapshot, houseListener, currentLocation, nextItem, loadedItem);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        if (dataRoot != null) {
            getListHouse(dataRoot, houseListener, currentLocation, nextItem, loadedItem);
        } else {
            mDataNodeRoot.addListenerForSingleValueEvent(listener);
        }
    }

    private void getListHouse(DataSnapshot dataSnapshot, HouseListener houseListener, Location currentLocation, int nextItem, int loadedItem) {
        // Get list house
        int i = 0;
        DataSnapshot dataHouses = dataSnapshot.child(Constants.HOUSES);
        for (DataSnapshot values : dataHouses.getChildren()) {

            if (i == nextItem) {
                break;
            }

            if (i < loadedItem) {
                i++;
                continue;
            }
            i++;


            HouseModel houseModel = values.getValue(HouseModel.class);
            houseModel.setHouseId(values.getKey());

            // Get list image by id
            DataSnapshot dataImage = dataSnapshot.child(Constants.HOUSE_IMAGES).child(values.getKey());
            List<String> houseImages = new ArrayList<>();
            for (DataSnapshot valueImages : dataImage.getChildren()) {
                houseImages.add(valueImages.getValue(String.class));
            }
            houseModel.setHouseImages(houseImages);

            // Get list comment
            DataSnapshot dataComment = dataSnapshot.child(Constants.COMMENTS).child(houseModel.getHouseId());
            List<CommentModel> commentModelList = new ArrayList<>();

            for (DataSnapshot valueComment : dataComment.getChildren()) {
                CommentModel commentModel = valueComment.getValue(CommentModel.class);
                commentModel.setCommentId(valueComment.getKey());
                Users users = dataSnapshot.child(Constants.USERS).child(commentModel.getUid()).getValue(Users.class);
                commentModel.setUsers(users);

                List<String> listImageComment = new ArrayList<>();
                DataSnapshot dataImageComment = dataSnapshot.child(Constants.COMMENT_IMAGES)
                        .child(commentModel.getCommentId());
                for (DataSnapshot valueImageComment : dataImageComment.getChildren()) {
                    listImageComment.add(valueImageComment.getValue(String.class));
                }
                commentModel.setListCommentImages(listImageComment);
                commentModelList.add(commentModel);
            }
            houseModel.setCommentModelList(commentModelList);

            // Get HouseBranchs
            DataSnapshot dataAddress = dataSnapshot.child(Constants.ADDRESS).child(houseModel.getHouseId());
            AddressModel addressModel = dataAddress.getValue(AddressModel.class);
            Location locationHouse = new Location("");
            locationHouse.setLongitude(addressModel.getLongitude());
            locationHouse.setLatitude(addressModel.getLatitude());


            float distance = (currentLocation.distanceTo(locationHouse)) / 1000;
            LogUtils.d("Distance: " + distance + " - " + addressModel.getAddress());
            addressModel.setDistance(distance);

            houseModel.setAddressModel(addressModel);
            houseListener.getListHouseModel(houseModel);
        }
    }
}
