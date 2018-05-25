package net.hailm.firebaseapp.view.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;
import net.hailm.firebaseapp.utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MyInfoHouse implements GoogleMap.InfoWindowAdapter {
    private LayoutInflater inflater;
    private Context context;

    public MyInfoHouse(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = inflater.inflate(R.layout.custom_view_house, null);

        LinearLayout llViewHouse = view.findViewById(R.id.ll_view_house);
        TextView txtLandlord = view.findViewById(R.id.txt_view_house_landlord);
        TextView txtAddress = view.findViewById(R.id.txt_view_house_address);
        TextView txtAcreage = view.findViewById(R.id.txt_view_house_acreage);
        TextView txtDistance = view.findViewById(R.id.txt_distance);
        TextView txtUpdateDate = view.findViewById(R.id.txt_view_house_update_date);

        final HouseModel houseModel = (HouseModel) marker.getTag();
        if (houseModel != null) {

            String address = context.getString(R.string.dia_chi) + " " + houseModel.getAddressModel().getAddress();
            txtAddress.setText(address);

            String landlord = context.getString(R.string.chu_nha) + " " + houseModel.getLandlord();
            txtLandlord.setText(landlord);

            String acreage = context.getString(R.string.dien_tich) + " " + houseModel.getAcreage() + " "
                    + context.getString(R.string.m2)
                    + " - " + context.getString(R.string.gia_phong) + " " + String.valueOf(houseModel.getPrice()
                    + " " + context.getString(R.string.dong));
            txtAcreage.setText(acreage);

            String distance = String.valueOf(String.format("%.2f", houseModel.getAddressModel().getDistance())) + context.getString(R.string.km);
            txtDistance.setText(distance);

            Date date = getDate(houseModel);
            String updateDate = context.getString(R.string.ngay) + " " + DateUtils.getDay(date) + " "
                    + context.getString(R.string.thang) + " " + DateUtils.getMonth(date) + " "
                    + context.getString(R.string.nam) + " " + DateUtils.getYear(date) + " "
                    + context.getString(R.string.luc) + " " + DateUtils.getTime(date);
            txtUpdateDate.setText(updateDate);
        } else {
            Toast.makeText(context, "Đây là vị trí của bạn!", Toast.LENGTH_SHORT).show();
            llViewHouse.removeAllViews();
        }

        return view;
    }

    /**
     * getDateTime
     *
     * @param houseModel
     * @return
     */
    private Date getDate(HouseModel houseModel) {
        String updateDate = houseModel.getUpdateDate();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm 'at' dd.MM.yyyy");
        try {
            Date date = format.parse(updateDate);
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
