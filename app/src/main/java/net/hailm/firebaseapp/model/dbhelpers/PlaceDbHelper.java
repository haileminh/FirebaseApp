package net.hailm.firebaseapp.model.dbhelpers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import net.hailm.firebaseapp.base.BaseFireBase;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.AddressListener;
import net.hailm.firebaseapp.model.dbmodels.AddressModel;

public class PlaceDbHelper extends BaseFireBase {
    private DatabaseReference mDataNodeRoot;

    public PlaceDbHelper() {
        mDataNodeRoot = getDatabaseReference();
    }

    public void getListAddress(final AddressListener addressListener) {

        mDataNodeRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot dataAddress = dataSnapshot.child(Constants.ADDRESS);
                for (DataSnapshot valueAddress : dataAddress.getChildren()) {
                    AddressModel addressModel = valueAddress.getValue(AddressModel.class);

                    addressListener.getListAddressModel(addressModel);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
