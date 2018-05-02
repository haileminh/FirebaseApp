package net.hailm.firebaseapp.model.dbhelpers;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import net.hailm.firebaseapp.base.BaseFireBase;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.CommentListener;
import net.hailm.firebaseapp.model.dbmodels.CommentModel;
import net.hailm.firebaseapp.model.dbmodels.Users;

public class CommentDbHelper extends BaseFireBase {
    private DatabaseReference mDataNodeRoot;

    public CommentDbHelper() {
        mDataNodeRoot = getDatabaseReference();
    }

    public void getCommentList(final CommentListener commentListener, final String houseId) {


        mDataNodeRoot.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot dataComments = dataSnapshot.child(Constants.COMMENTS).child(houseId);
                for (DataSnapshot valueComment : dataComments.getChildren()) {
                    CommentModel commentModel = valueComment.getValue(CommentModel.class);
                    commentModel.setCommentId(valueComment.getKey());
                    Users users = dataSnapshot.child(Constants.USERS).child(commentModel.getUid()).getValue(Users.class);
                    commentModel.setUsers(users);


                    commentListener.getListHouseModel(commentModel);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
