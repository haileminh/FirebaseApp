package net.hailm.firebaseapp.listener;

import net.hailm.firebaseapp.model.dbmodels.HouseModel;

public interface HouseRcvAdapterCallback {

    void onItemCLick(HouseModel houseModel);

    void onBtnClick(String tel);

    void deleteHouseByAdmin(HouseModel houseModel);

}
