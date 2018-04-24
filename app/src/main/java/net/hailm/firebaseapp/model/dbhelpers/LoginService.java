package net.hailm.firebaseapp.model.dbhelpers;

import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import net.hailm.firebaseapp.base.BaseFireBase;
import net.hailm.firebaseapp.listener.LoginListener;

/**
 * Created by hai.lm on 03/04/2018.
 */

public class LoginService extends BaseFireBase {
    private FirebaseAuth mAuth;

    public LoginService() {
        mAuth = getFirebaseAuth();
    }

    /**
     * Xac thuc tai khoan
     *
     * @param email
     * @param pass
     * @param listener
     */
    public void loginAccountEmail(String email, String pass, final LoginListener listener) {
        mAuth.signInWithEmailAndPassword(email, pass).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.loginFailure(e.getMessage());
            }
        }).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                listener.loginSuccess();
            }
        });
    }

    /**
     * loginGoogle
     */
    public void loginGoogle() {

    }
}
