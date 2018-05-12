package net.hailm.firebaseapp.view.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.listener.PopupSearchCallback;

public class PopupSearch extends DialogFragment {
    private View rootView;
    private TextView txtDistance;
    private TextView txtPrice;
    private TextView txtAcerage;
    private Button btnOk;
    private SeekBar sbDistance;
    private SeekBar sbPrice;
    private SeekBar sbAcreage;

    private PopupSearchCallback popupSearchCallback;

    private int progressDistance = 0;
    private int progressPrice = 0;
    private int progressAcreage = 0;

    public PopupSearch() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.layout_popup_search, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        popupSearchCallback = (PopupSearchCallback) getTargetFragment();
        txtDistance = rootView.findViewById(R.id.txt_popup_distance);
        txtPrice = rootView.findViewById(R.id.txt_popup_price);
        txtAcerage = rootView.findViewById(R.id.txt_popup_acrage);
        sbDistance = rootView.findViewById(R.id.sb_distance);
        sbPrice = rootView.findViewById(R.id.sb_prices);
        sbAcreage = rootView.findViewById(R.id.sb_acreage);
        btnOk = rootView.findViewById(R.id.btn_ok);

        setUpSeekBarDistance();
        setUpSeekBarPrice();
        setUpSeekBarAcreage();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupSearchCallback.onButtonClick(Double.valueOf(progressDistance), Long.valueOf(progressPrice), Long.valueOf(progressAcreage));
                dismiss();
            }
        });
    }

    private void setUpSeekBarDistance() {
        String distance = "Trong bán kính " + sbDistance.getProgress() + " km";
        txtDistance.setText(distance);

        sbDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // Khi giá trị progress thay đổi.
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValues, boolean fromUser) {
                progressDistance = progressValues;
                txtDistance.setText("Trong bán kính " + progressValues + " km");
            }

            // Khi người dùng bắt đầu cử chỉ kéo thanh gạt.
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            // Khi người dùng kết thúc cử chỉ kéo thanh gạt.
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txtDistance.setText("Trong bán kính " + progressDistance + " km");
            }
        });
    }

    private void setUpSeekBarPrice() {
        String price = "Giá phòng nhỏ hơn " + sbPrice.getProgress() + " triệu dồng";
        txtPrice.setText(price);

        sbPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValues, boolean fromUser) {
                progressPrice = progressValues;
                txtPrice.setText("Giá phòng nhỏ hơn " + progressValues + " triệu dồng");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txtPrice.setText("Giá phòng nhỏ hơn " + progressPrice + " triệu dồng");
            }
        });
    }

    private void setUpSeekBarAcreage() {
        String acreage = "Diện tích nhỏ hơn " + sbAcreage.getProgress() + " mét vuông";
        txtAcerage.setText(acreage);

        sbAcreage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValues, boolean fromUser) {
                progressAcreage = progressValues;
                txtAcerage.setText("Diện tích nhỏ hơn " + progressValues + " mét vuông");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txtAcerage.setText("Diện tích nhỏ hơn " + progressAcreage + " mét vuông");
            }
        });
    }
}
