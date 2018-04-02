package net.hailm.firebaseapp.listener;

/**
 * Created by hai_l on 25/03/2018.
 */

public interface RegisterListener {
    void registerSuccess();

    void registerFailure(String message);
}
