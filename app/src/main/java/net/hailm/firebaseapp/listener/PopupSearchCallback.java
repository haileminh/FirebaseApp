package net.hailm.firebaseapp.listener;

public interface PopupSearchCallback {
    void onButtonClick(double distance, long price, long acreage, boolean sortByDate, boolean sortByLocation, boolean sortByPrice);
}
