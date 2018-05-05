package net.hailm.firebaseapp.view.fragments;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.model.dbmodels.AddressModel;
import net.hailm.firebaseapp.model.dbmodels.HouseModel;

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

//        ImageView imgHouse = view.findViewById(R.id.txt_view_house_landlord);
        TextView txtLandlord = view.findViewById(R.id.txt_view_house_landlord);
        TextView txtAddress = view.findViewById(R.id.txt_view_house_address);
        TextView txtUpdateDate = view.findViewById(R.id.txt_view_house_update_date);

        AddressModel houseModel = (AddressModel) marker.getTag();
        if (houseModel != null) {
            txtAddress.setText(houseModel.getAddress());
            txtLandlord.setText(String.valueOf(houseModel.getLatitude()));
            txtUpdateDate.setText(String.valueOf(houseModel.getLongitude()));
        } else {
            Toast.makeText(context, "Đây là vị trí của bạn!", Toast.LENGTH_SHORT).show();
        }

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
