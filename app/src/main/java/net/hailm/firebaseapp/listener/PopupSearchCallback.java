package net.hailm.firebaseapp.listener;

public interface PopupSearchCallback {
    void onButtonClick(double distance, long priceMin, long priceMax, long acreageMin, long acreageMax, boolean sortByDate, boolean sortByLocation, boolean sortByPrice);
}
