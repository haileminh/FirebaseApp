package net.hailm.firebaseapp.listener;

import net.hailm.firebaseapp.model.dbmodels.HouseModel;

public interface EditHouseRcvAdapterCallback {
    void onItemCLick(HouseModel houseModel);

    void deleteHouse(HouseModel houseModel);
}
