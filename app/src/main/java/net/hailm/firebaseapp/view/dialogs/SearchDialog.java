package net.hailm.firebaseapp.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.blankj.utilcode.util.LogUtils;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.listener.PopupSearchCallback;

public class SearchDialog extends Dialog {
    private TextView txtDistance;
    private TextView txtPrice;
    private TextView txtAcerage;
    private Button btnOk;
    private SeekBar sbDistance;
    private SeekBar sbPrice;
    private SeekBar sbAcreage;
    private RadioButton rdSortByDate;
    private RadioButton rdSortByLocation;
    private RadioButton rdSortByPrice;

    private int progressDistance = 0;
    private int progressPrice = 0;
    private int progressAcreage = 0;

    private boolean mSortByDate;
    private boolean mSortByLocation;
    private boolean mSortByPrice;


    private PopupSearchCallback popupSearchCallback;

    public SearchDialog(@NonNull Context context, final PopupSearchCallback popupSearchCallback) {
        super(context);
        this.popupSearchCallback = popupSearchCallback;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_popup_search);

        getWindow().setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );
        initializeComponents();
        setUpSeekBarDistance();
        setUpSeekBarPrice();
        setUpSeekBarAcreage();

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupSearchCallback.onButtonClick(Double.valueOf(progressDistance)
                        , Long.valueOf(progressPrice)
                        , Long.valueOf(progressAcreage)
                        , rdSortByDate.isChecked()
                        , rdSortByLocation.isChecked()
                        , rdSortByPrice.isChecked());
                dismiss();
            }
        });
    }

    private void initializeComponents() {
        txtDistance = findViewById(R.id.txt_popup_distance);
        txtPrice = findViewById(R.id.txt_popup_price);
        txtAcerage = findViewById(R.id.txt_popup_acrage);
        sbDistance = findViewById(R.id.sb_distance);
        sbPrice = findViewById(R.id.sb_prices);
        sbAcreage = findViewById(R.id.sb_acreage);
        btnOk = findViewById(R.id.btn_ok);
        rdSortByDate = findViewById(R.id.rd_sort_by_date);
        rdSortByLocation = findViewById(R.id.rd_sort_by_location);
        rdSortByPrice = findViewById(R.id.rd_sort_by_price);
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
        String price = "Giá phòng lớn hơn " + sbPrice.getProgress() + " triệu đồng";
        txtPrice.setText(price);

        sbPrice.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValues, boolean fromUser) {
                progressPrice = progressValues;
                txtPrice.setText("Giá phòng lớn hơn " + progressValues + " triệu dồng");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txtPrice.setText("Giá phòng lớn hơn " + progressPrice + " triệu dồng");
            }
        });
    }

    private void setUpSeekBarAcreage() {
        String acreage = "Diện tích lớn hơn " + sbAcreage.getProgress() + " mét vuông";
        txtAcerage.setText(acreage);

        sbAcreage.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValues, boolean fromUser) {
                progressAcreage = progressValues;
                txtAcerage.setText("Diện tích lớn hơn " + progressValues + " mét vuông");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                txtAcerage.setText("Diện tích lớn hơn " + progressAcreage + " mét vuông");
            }
        });
    }
}