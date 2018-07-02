package net.hailm.firebaseapp.view.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;

import net.hailm.firebaseapp.R;
import net.hailm.firebaseapp.listener.PopupSearchCallback;

import java.text.NumberFormat;
import java.util.Locale;

public class SearchDialog extends Dialog {
    private TextView txtDistance;
    private Button btnOk;
    private SeekBar sbDistance;
    private LinearLayout llDistance;

    private RadioGroup radioGroup;

    private EditText edtAddress;
    private EditText edtPriceMin;
    private EditText edtPriceMax;
    private EditText edtAcreageMin;
    private EditText edtAcreageMax;

    private RadioButton rdSortByDate;
    private RadioButton rdSortByLocation;
    private RadioButton rdSortByPrice;

    private double progressDistance = 0.0;

    private String mAddress = "";
    private long mPriceMin = -1;
    private long mPriceMax = -1;
    private long mAcreageMin = -1;
    private long mAcreageMax = -1;


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

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getData();
                if (checkInputPrices() == true && checkInputAcreage() == true && checkCompareDataPrice() == true && checkCompareDataAcreage() == true) {
                    popupSearchCallback.onButtonClick(mAddress
                            , Double.valueOf(progressDistance)
                            , mPriceMin
                            , mPriceMax
                            , mAcreageMin
                            , mAcreageMax
                            , rdSortByDate.isChecked()
                            , rdSortByLocation.isChecked()
                            , rdSortByPrice.isChecked());
                    dismiss();
                }

            }
        });

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rd_address) {
                    llDistance.setVisibility(View.GONE);
                    edtAddress.setVisibility(View.VISIBLE);
                }
                if (checkedId == R.id.rd_vitri) {
                    llDistance.setVisibility(View.VISIBLE);
                    edtAddress.setVisibility(View.GONE);
                }
            }
        });
    }

    private void initializeComponents() {
        txtDistance = findViewById(R.id.txt_popup_distance);
        sbDistance = findViewById(R.id.sb_distance);
        llDistance = findViewById(R.id.ll_distance);
        btnOk = findViewById(R.id.btn_ok);

        radioGroup = findViewById(R.id.rd_group);

        edtAddress = findViewById(R.id.edt_address_search);
        edtPriceMin = findViewById(R.id.edt_price_min);
        edtPriceMax = findViewById(R.id.edt_price_max);
        edtAcreageMin = findViewById(R.id.edt_acreage_min);
        edtAcreageMax = findViewById(R.id.edt_acreage_max);

        rdSortByDate = findViewById(R.id.rd_sort_by_date);
        rdSortByLocation = findViewById(R.id.rd_sort_by_location);
        rdSortByPrice = findViewById(R.id.rd_sort_by_price);

        inputText(edtPriceMin);
        inputText(edtPriceMax);
    }

    private void getData() {
        String address = edtAddress.getText().toString().trim();
        if (!address.equals("")) {
            mAddress = address;
        }

        String priceMin = String.valueOf(edtPriceMin.getText());
        if (!priceMin.equals("")) {
            mPriceMin = getPrice(edtPriceMin);
        }

        String priceMax = String.valueOf(edtPriceMax.getText());
        if (!priceMax.equals("")) {
            mPriceMax = getPrice(edtPriceMax);
        }

        String acreageMin = String.valueOf(edtAcreageMin.getText());
        if (!acreageMin.equals("")) {
            mAcreageMin = Long.parseLong(acreageMin);
        }

        String acreageMax = String.valueOf(edtAcreageMax.getText());
        if (!acreageMax.equals("")) {
            mAcreageMax = Long.parseLong(acreageMax);
        }
        LogUtils.d("PriceMin: " + priceMin + priceMax + acreageMin + acreageMax);
    }

    private long getPrice(EditText editText) {
        String price = "";
        String text = editText.getText().toString().trim();
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != '.') {
                price += text.charAt(i);
            }
        }

        long priceLong = Long.parseLong(price);
        return priceLong;
    }

    private boolean checkCompareDataPrice() {
        String priceMin = edtPriceMin.getText().toString().trim();
        String priceMax = edtPriceMax.getText().toString().trim();

        if (!priceMin.equals("") && !priceMax.equals("")) {
            if (getPrice(edtPriceMin) > getPrice(edtPriceMax)) {
                Toast.makeText(getContext(), "Giá lớn nhất phải lớn hơn giá nhỏ nhất", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    private boolean checkCompareDataAcreage() {
        String acreageMin = edtAcreageMin.getText().toString().trim();
        String acreageMax = edtAcreageMax.getText().toString().trim();
        if (!acreageMin.equals("") && !acreageMax.equals("")) {
            if (Long.parseLong(acreageMin) > Long.parseLong(acreageMax)) {
                Toast.makeText(getContext(), "Giá lớn nhất phải lớn hơn giá nhỏ nhất", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }

    }

    private boolean checkInputPrices() {
        String priceMin = edtPriceMin.getText().toString().trim();
        String priceMax = edtPriceMax.getText().toString().trim();

        if (!priceMin.equals("") || !priceMax.equals("")) {
            if (!priceMin.equals("") && !priceMax.equals("")) {
                return true;
            } else {
                Toast.makeText(getContext(), "Bạn phải nhập cả giá nhỏ nhất và giá lớn nhất", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            return true;
        }
    }

    private boolean checkInputAcreage() {
        String acreageMin = edtAcreageMin.getText().toString().trim();
        String acreageMax = edtAcreageMax.getText().toString().trim();

        if (!acreageMin.equals("") || !acreageMax.equals("")) {
            if (!acreageMin.equals("") && !acreageMax.equals("")) {
                return true;
            } else {
                Toast.makeText(getContext(), "Bạn cần phải nhập diện tích nhỏ nhất và diện tích lớn nhất", Toast.LENGTH_SHORT).show();
                return false;
            }
        } else {
            return true;
        }
    }


    private void setUpSeekBarDistance() {
        String distance = "Trong bán kính " + sbDistance.getProgress() + " km";
        txtDistance.setText(distance);

        sbDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            // Khi giá trị progress thay đổi.
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValues, boolean fromUser) {
                progressDistance = progressValues * 0.5;
                txtDistance.setText("Trong bán kính " + progressValues * 0.5 + " km");
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

    private void inputText(final EditText edtInput) {
        edtInput.addTextChangedListener(new TextWatcher() {
            private String current = "";

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    edtInput.removeTextChangedListener(this);

                    Locale local = new Locale("id", "id");
                    String replaceable = String.format("[Rp,.\\s]",
                            NumberFormat.getCurrencyInstance().getCurrency()
                                    .getSymbol(local));
                    String cleanString = s.toString().replaceAll(replaceable,
                            "");

                    double parsed;
                    try {
                        parsed = Double.parseDouble(cleanString);
                    } catch (NumberFormatException e) {
                        parsed = 0.00;
                    }

                    NumberFormat formatter = NumberFormat
                            .getCurrencyInstance(local);
                    formatter.setMaximumFractionDigits(0);
                    formatter.setParseIntegerOnly(true);
                    String formatted = formatter.format((parsed));

                    String replace = String.format("[Rp\\s]",
                            NumberFormat.getCurrencyInstance().getCurrency()
                                    .getSymbol(local));
                    String clean = formatted.replaceAll(replace, "");

                    current = formatted;
                    edtInput.setText(clean);
                    edtInput.setSelection(clean.length());
                    edtInput.addTextChangedListener(this);
                }
            }
        });
    }
}