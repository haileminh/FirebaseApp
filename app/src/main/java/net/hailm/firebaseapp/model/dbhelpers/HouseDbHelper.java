package net.hailm.firebaseapp.model.dbhelpers;

import com.blankj.utilcode.util.LogUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import net.hailm.firebaseapp.base.BaseFireBase;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.HouseListener;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;

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

    /**
     * Get list house fill recycleView
     *
     * @param houseListener
     */
    public void getListHostuse(final HouseListener houseListener) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot dataHouses = dataSnapshot.child(Constants.HOUSES);
                for (DataSnapshot values : dataHouses.getChildren()) {

                    HouseModel houseModel = values.getValue(HouseModel.class);
                    houseModel.setHouuseId(values.getKey());

                    DataSnapshot dataImage = dataSnapshot.child(Constants.HOUSE_IMAGES).child(values.getKey());
                    List<String> houseImages = new ArrayList<>();
                    for (DataSnapshot valueImages : dataImage.getChildren()) {
                        houseImages.add(valueImages.getValue(String.class));
                    }

                    houseModel.setHouseImages(houseImages);
                    houseListener.getListHouseModel(houseModel);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        mDataNodeRoot.addListenerForSingleValueEvent(listener);
    }
}
