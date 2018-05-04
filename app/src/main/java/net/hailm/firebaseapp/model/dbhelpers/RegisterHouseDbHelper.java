package net.hailm.firebaseapp.model.dbhelpers;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

import net.hailm.firebaseapp.base.BaseFireBase;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.RegisterHouseListener;
import net.hailm.firebaseapp.model.dbmodels.AddressModel;
import net.hailm.firebaseapp.model.dbmodels.CommentModel;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;

import java.util.List;

public class RegisterHouseDbHelper extends BaseFireBase {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Context context;

    public RegisterHouseDbHelper(Context context) {
        this.context = context;
        mAuth = getFirebaseAuth();
        mDatabase = getDatabaseReference();
    }

    /**
     * register House table
     *
     * @param houseModel
     * @param listener
     */
    public void registerHouses(HouseModel houseModel, final RegisterHouseListener listener) {
        mDatabase.child(Constants.HOUSES)
                .child(houseModel.getHouseId())
                .setValue(houseModel)
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

    /**
     * registerAddress
     *
     * @param addressModel
     * @param houseModel
     * @param listener
     */
    public void registerAddress(AddressModel addressModel, HouseModel houseModel, final RegisterHouseListener listener) {
        mDatabase.child(Constants.ADDRESS)
                .child(houseModel.getHouseId())
                .setValue(addressModel)
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

    /**
     * registerComment
     *
     * @param commentModel
     * @param houseId
     * @param listener
     */
    public void registerComment(CommentModel commentModel, String houseId, final RegisterHouseListener listener) {
        mDatabase.child(Constants.COMMENTS)
                .child(houseId)
                .child(commentModel.getCommentId())
                .setValue(commentModel)
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

    public void registerHouseImage(List<String> nameImageHouse, String houseId) {
        for (int i = 0; i < nameImageHouse.size(); i++) {
            mDatabase.child(Constants.HOUSE_IMAGES).child(houseId).child(nameImageHouse.get(i)).setValue(nameImageHouse.get(i));
        }
    }
}
