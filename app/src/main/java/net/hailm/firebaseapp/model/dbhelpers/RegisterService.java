package net.hailm.firebaseapp.model.dbhelpers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import net.hailm.firebaseapp.base.BaseFireBase;
import net.hailm.firebaseapp.define.Constants;
import net.hailm.firebaseapp.listener.RegisterListener;
import net.hailm.firebaseapp.model.dbmodels.Users;

/**
 * Created by hai.lm on 02/04/2018.
 */

public class RegisterService extends BaseFireBase {
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private Activity mActivity;

    public RegisterService(Activity mActivity) {
        this.mActivity = mActivity;
        mAuth = getFirebaseAuth();
        mDatabase = getDatabaseReference();
    }

    /**
     * Dang ki tai khoan bang gmail
     *
     * @param email
     * @param password
     * @param listener
     */
    public void registerAccount(String email, String password, final RegisterListener listener) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    final FirebaseUser userFB = task.getResult().getUser();
                    userFB.sendEmailVerification().addOnCompleteListener(mActivity, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                //Tiến hành thông tin user vào Database
                                Users users = new Users();
                                users.setUid(userFB.getUid());
                                users.setName("HaiLeMinh");
                                users.setAvatar("user2.png");
                                users.setEmail(userFB.getEmail());
                                createAccountInDatabase(users, new RegisterListener() {
                                    @Override
                                    public void registerSuccess() {
                                        mAuth.signOut();
                                        listener.registerSuccess();
                                    }

                                    @Override
                                    public void registerFailure(String message) {
                                        listener.registerFailure(message);
                                    }
                                });
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            listener.registerFailure(e.getMessage());
                        }
                    });
                } else {
                    listener.registerFailure("Email đã tồn tại !");
                }
            }
        });
    }

    /**
     * Luu thong tin user
     *
     * @param users
     * @param listener
     */
    private void createAccountInDatabase(Users users, final RegisterListener listener) {
        mDatabase.child(Constants.USERS)
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

    /**
     * Reset pass
     *
     * @param email
     */
    public void resetPass(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(mActivity, "Vui lòng xác thực mail", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
