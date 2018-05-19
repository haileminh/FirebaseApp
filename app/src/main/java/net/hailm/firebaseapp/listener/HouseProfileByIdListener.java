package net.hailm.firebaseapp.listener;

import net.hailm.firebaseapp.model.dbmodels.HouseModel;

public interface HouseProfileByIdListener {
    void getHouseById(HouseModel houseModel);
}
