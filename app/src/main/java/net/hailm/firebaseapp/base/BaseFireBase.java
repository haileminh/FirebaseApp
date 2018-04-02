package net.hailm.firebaseapp.base;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Created by hai_l on 25/03/2018.
 */

public abstract class BaseFireBase {
    private DatabaseReference mDatabase;
    private FirebaseAuth mFirebaseAuth;
    private StorageReference mStorageReference;

    /**
     * Danh cho database
     *
     * @return
     */
    protected DatabaseReference getDatabaseReference() {
        if (mDatabase == null) {
            return FirebaseDatabase.getInstance().getReference();
        } else {
            return mDatabase;
        }
    }

    /**
     * Danh cho auth
     *
     * @return
     */
    protected FirebaseAuth getFirebaseAuth() {
        if (mFirebaseAuth == null) {
            return FirebaseAuth.getInstance();
        } else {
            return mFirebaseAuth;
        }
    }

    /**
     * Danh cho upload file
     *
     * @return
     */
    protected StorageReference getStorageReference() {
        if (mStorageReference == null) {
            return FirebaseStorage.getInstance().getReference();
        } else {
            return mStorageReference;
        }
    }
}
