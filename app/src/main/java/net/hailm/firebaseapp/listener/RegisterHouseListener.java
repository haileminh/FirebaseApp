package net.hailm.firebaseapp.listener;

public interface RegisterHouseListener {
    void registerSuccess();

    void registerFailure(String message);
}
