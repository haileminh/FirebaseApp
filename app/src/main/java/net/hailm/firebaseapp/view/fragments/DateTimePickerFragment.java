package net.hailm.firebaseapp.view.fragments;

import android.content.Context;
import android.widget.TextView;

import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import net.hailm.firebaseapp.define.AppConst;

import java.util.Date;

/**
 * Created by vohung on 21/10/2560.
 */

public class DateTimePickerFragment implements SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener {

    private OnClickPositiveButtonListener mOnClickPositiveButtonListener;
    private TextView mTextView;
    private Context context;

    public DateTimePickerFragment(TextView textView, OnClickPositiveButtonListener onClickPositiveButtonListener) {
        this.context = context;
        this.mTextView = textView;
        this.mOnClickPositiveButtonListener = onClickPositiveButtonListener;

    }

    @Override
    public void onNeutralButtonClick(Date date) {
        mTextView.setText(AppConst.DEFAULT_VALUE);
    }

    @Override
    public void onPositiveButtonClick(Date date) {
        mOnClickPositiveButtonListener.onClickPositiveButton(date);
    }

    @Override
    public void onNegativeButtonClick(Date date) {

    }


    public interface OnClickPositiveButtonListener {
        void onClickPositiveButton(Date date);
    }

}
