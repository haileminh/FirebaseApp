package net.hailm.firebaseapp.listener;

/**
 * Created by hai_l on 25/03/2018.
 */

public interface LoginListener {
    void loginSuccess();

    void loginFailure(String message);
}
